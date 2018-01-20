package lateralpraxis.lpsmartfarm.nursery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomCoord;

public class ActivityNurseryZoneCoordinate extends FragmentActivity {
    //implements OnMapReadyCallback
    //CustomAdapter Cadapter;
    final Context context = this;
    //-------Variables used in Capture GPS---------//
//    private GoogleMap googleMap;
//    private LatLng levelLatLng;
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    float zoom = 0;
    // GPSTracker class
    GPSTracker gps;
    String userId = "";
    String provider;
    double flatitude = 0.0;
    /*------------------------End of code for class Declaration------------------------------*/
    double flongitude = 0.0;
    private int cnt = 0;
    //    private GoogleMap googleMapDisplay;
//    private ArrayList<LatLng> arrayPoints = null;
//    PolylineOptions polylineOptions;
    private int zoomLevel = 15;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvNursery, tvNurseryZone, tvEmpty, tvCoordinates, tvNurseryType, tvHeader;
    private Button btnBack, btnNext, btnFetchGps, btnAddGps;
    private ListView lvCoordinates;
    private LinearLayout llNurseryZone;
    /*------------------------End of code for controls Declaration------------------------------*/
        /*------------------------Start of code for variable
        Declaration------------------------------*/
    private String nurseryId, nurseryZoneId, nurseryName, nurseryZone, nurType, nurseryUniqueId;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> GPSDetails;
    /*------------------------End of code for Variable Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_zone_coordinate);
        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        GPSDetails = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*-----------------Code to get data from posted page--------------------------*/
        llNurseryZone = (LinearLayout) findViewById(R.id.llNurseryZone);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId= extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryZoneId = extras.getString("nurseryZoneId");
            nurseryName = extras.getString("nurseryName");
            nurseryZone = extras.getString("nurseryZone");
            nurType = extras.getString("nurType");
        }
        if (nurseryZoneId.equalsIgnoreCase("0"))
            llNurseryZone.setVisibility(View.GONE);
        else {
            llNurseryZone.setVisibility(View.VISIBLE);
        }
        dba.open();
        dba.deleteTempNurseryGPSData();
        dba.close();
         /*------------------------Start of code for controls
         Declaration--------------------------*/
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvHeader= (TextView) findViewById(R.id.tvHeader);
        tvNurseryType= (TextView) findViewById(R.id.tvNurseryType);
        tvCoordinates = (TextView) findViewById(R.id.tvCoordinates);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        btnFetchGps = (Button) findViewById(R.id.btnFetchGps);
        btnAddGps = (Button) findViewById(R.id.btnAddGps);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnAddGps.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
         /*--------Code to set Nursery Details---------------------*/
        if (nurseryZoneId.equalsIgnoreCase("0"))
            tvHeader.setText(R.string.label_NurseryGPS);
        else {
            tvHeader.setText(R.string.label_NurseryZoneGPS);
        }
        tvNursery.setText(nurseryName);
        tvNurseryZone.setText(nurseryZone);
        tvNurseryType.setText(nurType);
        //Code to fetch GPS Coordinates
        btnFetchGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tvCoordinates.setText("");
                latitude = "NA";
                longitude = "NA";
                accuracy = "NA";
                latitudeN = "NA";
                longitudeN = "NA";
                // create class object
                gps = new GPSTracker(ActivityNurseryZoneCoordinate.this);
                if (gps.canGetLocation()) {
                    flatitude = gps.getLatitude();
                    flongitude = gps.getLongitude();
                    if (flatitude == 0.0 || flongitude == 0.0) {
                        common.showAlert(ActivityNurseryZoneCoordinate.this, "Unable to fetch " +
                                "coordinates. Please try again.", false);
                    } else {
                        latitude = String.valueOf(flatitude);
                        longitude = String.valueOf(flongitude);

                        btnAddGps.setVisibility(View.VISIBLE);
                        tvCoordinates.setVisibility(View.VISIBLE);
                        if (!latitude.equals("NA") && !longitude.equals("NA")) {
                            latitudeN = latitude.toString();
                            longitudeN = longitude.toString();
                            accuracy = common.stringToOneDecimal(String.valueOf(gps.accuracy)) +
                                    " mts";
                            tvCoordinates.setText(Html.fromHtml("<b>" + latitude.toString() + " ," +
                                    " " + longitude.toString() + " " + accuracy + "</b>"));
                        } else if (latitude.equals("NA") || longitude.equals("NA")) {
                            flatitude = gps.getLatitude();
                            flongitude = gps.getLongitude();
                        }
                    }
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        btnAddGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dba.open();
                Boolean isGPSExists = dba.TempNurseryGPSExists(latitudeN, longitudeN);
                if (isGPSExists.equals(false)) {
                    dba.Insert_TempNurseryGPS(latitudeN, longitudeN, accuracy);
                    common.showAlert(ActivityNurseryZoneCoordinate.this, "Coordinate saved " +
                            "successfully.", false);
                } else {
                    common.showAlert(ActivityNurseryZoneCoordinate.this, "This coordinate is " +
                            "already captured.", false);
                }
                btnAddGps.setVisibility(View.GONE);
                int coordSize = BindCoordinates();
                if (coordSize >= 4) {
                    btnNext.setVisibility(View.VISIBLE);
                }
                tvCoordinates.setText("");
            }
        });
        /*---------------Start of code to set Click Event for Button Back &
        Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To back page
                Intent intent;
                if (nurseryZoneId.equalsIgnoreCase("0"))
                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryView.class);
                else
                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryZoneView.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurseryName);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (ActivityNurseryZoneCoordinate.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, want to submit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dba.open();
                                dba.insertNurseryZoneCoordinates(nurseryId, nurseryZoneId, userId);
                                dba.close();
                                if (nurseryZoneId.equalsIgnoreCase("0"))
                                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryView.class);
                                else
                                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryZoneView.class);
                                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                                intent.putExtra("nurseryId", nurseryId);
                                intent.putExtra("nurseryZoneId", nurseryZoneId);
                                intent.putExtra("nurType", nurType);
                                intent.putExtra("nurName", nurseryName);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                System.out.println("No Pressed");
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });
        /*---------------End of code to set Click Event for Button Save &
        Next-------------------------*/
    }


    //Method to bind list view
    public int BindCoordinates() {
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<CustomCoord> lables = dba.GetTempNurseryGPS();
        tvEmpty.setVisibility(View.VISIBLE);
        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("Latitude", lables.get(i).getLatitude());
            hm.put("Longitude", lables.get(i).getLongitude());
            hm.put("Accuracy", String.valueOf(lables.get(i).getAccuracy()));
            coordList.add(hm);
            tvEmpty.setVisibility(View.GONE);
        }
        dba.close();

        // Keys used in Hashmap
        String[] from = {"Latitude", "Longitude"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvLatitude, R.id.tvLongitude};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), coordList, R.layout
                .list_farm_coordinates, from, to);
        lvCoordinates.setAdapter(adapter);
        //</editor-fold>
        return lables.size();
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (nurseryZoneId.equalsIgnoreCase("0"))
                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryView.class);
                else
                    intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryZoneView.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurseryName);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (ActivityNurseryZoneCoordinate.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard " +
                                "any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent
                                        (ActivityNurseryZoneCoordinate.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // To create menu on inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        if (nurseryZoneId.equalsIgnoreCase("0"))
            intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryView.class);
        else
            intent = new Intent(ActivityNurseryZoneCoordinate.this, NurseryZoneView.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryZoneId", nurseryZoneId);
        intent.putExtra("nurType", nurType);
        intent.putExtra("nurName", nurseryName);
        startActivity(intent);
        finish();
    }

    //Method to check android version ad load action bar appropriately
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void actionBarSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setIcon(R.mipmap.ic_launcher);
            ab.setHomeButtonEnabled(true);
        }
    }
}
