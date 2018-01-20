package lateralpraxis.lpsmartfarm.nursery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.type.CustomCoord;

public class ActivityNurseryZoneCoordinateUpdate extends Activity {

    final Context context = this;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvNursuery, tvNurseryZone, tvEmpty, tvNurseryType, tvHeader;
    private Button btnBack, btnUpdateGps, btnNext;
    private ListView lvCoordinates;
    private LinearLayout llNurseryZone;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String nurseryId, nurseryZoneId = "0", nursery, nurseryZone, nurType, nurseryUniqueId;
    /*------------------------End of code for Variable Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Intent intent;
    /*------------------------End of code for class Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_zone_coordinate_update);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*-----------------Code to get data from posted page--------------------------*/
        llNurseryZone = (LinearLayout) findViewById(R.id.llNurseryZone);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryId = extras.getString("nurseryId");
            nurseryZoneId = extras.getString("nurseryZoneId");
            nursery = extras.getString("nurseryName");
            nurseryZone = extras.getString("nurseryZone");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurType = extras.getString("nurType");
        }
        if (nurseryZoneId.equalsIgnoreCase("0"))
            llNurseryZone.setVisibility(View.GONE);
        else {
            llNurseryZone.setVisibility(View.VISIBLE);
        }
         /*------------------------Start of code for controls
         Declaration--------------------------*/
        tvNursuery = (TextView) findViewById(R.id.tvNursuery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvHeader= (TextView) findViewById(R.id.tvHeader);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        btnUpdateGps = (Button) findViewById(R.id.btnUpdateGps);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

         /*--------Code to set Farmer Details---------------------*/
        tvNursuery.setText(nursery);
        tvNurseryZone.setText(nurseryZone);
        tvNurseryType.setText(nurType);
        if (nurseryZoneId.equalsIgnoreCase("0"))
            tvHeader.setText(R.string.label_NurseryGPS);
        else {
            tvHeader.setText(R.string.label_NurseryZoneGPS);
        }
        //To display GPS Coordinates
        BindCoordinates();
        //Code to delete and move to add GPS Coordinate page
        btnUpdateGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (ActivityNurseryZoneCoordinateUpdate.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to delete all coordinates?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dba.open();
                                dba.deleteNurseryCoordinates(nurseryId, nurseryZoneId);
                                dba.close();
                                if (nurseryZoneId.equalsIgnoreCase("0"))
                                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryView.class);
                                else
                                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryZoneView.class);
                                intent.putExtra("nurseryId", nurseryId);
                                intent.putExtra("nurseryZoneId", nurseryZoneId);
                                intent.putExtra("nurType", nurType);
                                intent.putExtra("nurName", nursery);
                                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
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

        /*---------------Start of code to set Click Event for Button Back-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move back Farm Block Characteristic page
                if (nurseryZoneId.equalsIgnoreCase("0"))
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryView.class);
                else
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryZoneView.class);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (nurseryZoneId.equalsIgnoreCase("0"))
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryView.class);
                else
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryZoneView.class);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                startActivity(intent);
                finish();
            }
        });
        /*---------------End of code to set Click Event for Button-------------------------*/
    }


    //Method to bind GPS Coordinates in list view
    public int BindCoordinates() {
        //<editor-fold desc="Code to bind POA/POI Details">
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();
        List<CustomCoord> lables = dba.GetNurseryCoordinates(nurseryId, nurseryZoneId);
        dba.close();
        tvEmpty.setVisibility(View.VISIBLE);
        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("Latitude", lables.get(i).getLatitude());
            hm.put("Longitude", lables.get(i).getLongitude());
            coordList.add(hm);
            tvEmpty.setVisibility(View.GONE);
        }

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
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryView.class);
                else
                    intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryZoneView.class);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (ActivityNurseryZoneCoordinateUpdate.this);
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
                                        (ActivityNurseryZoneCoordinateUpdate.this, ActivityHome
                                                .class);
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
            intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryView.class);
        else
            intent = new Intent(ActivityNurseryZoneCoordinateUpdate.this, NurseryZoneView.class);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryZoneId", nurseryZoneId);
        intent.putExtra("nurType", nurType);
        intent.putExtra("nurName", nursery);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
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
