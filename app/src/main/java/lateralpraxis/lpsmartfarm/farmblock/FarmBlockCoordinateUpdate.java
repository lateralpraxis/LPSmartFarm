package lateralpraxis.lpsmartfarm.farmblock;

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
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomCoord;

public class FarmBlockCoordinateUpdate extends Activity {

    final Context context = this;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvEmpty;
    private Button btnBack, btnUpdateGps, btnNext;
    private LinearLayout llFarmBlock;
    private ListView lvCoordinates;
    private View tvDivider;
    /*------------------------End of code for controls Declaration------------------------------*/

    /*------------------------Start of code for variable Declaration------------------------------*/
    private String area, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private static String lang;
    /*------------------------End of code for Variable Declaration------------------------------*/

    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_block_coordinate_update);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        lang = session.getDefaultLang();
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            entryType = extras.getString("entryType");
            farmerCode = extras.getString("farmerCode");
        }

         /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        tvDivider = (View) findViewById(R.id.tvDivider);
        btnUpdateGps = (Button) findViewById(R.id.btnUpdateGps);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

         /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }


        dba.open();
        area = dba.GetAreaByFarmBlockUniqueId(farmBlockUniqueId);
        dba.close();

        //To display GPS Coordinates
        BindCoordinates();

        //Code to delete and move to add GPS Coordinate page
        btnUpdateGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockCoordinateUpdate.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to delete all coordinates?":"क्या आप निश्चित हैं, आप सभी निर्देशांक हटाना चाहते हैं?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dba.open();
                                dba.deleteFarmBlockCoordinates(farmBlockUniqueId);
                                dba.close();
                                Intent intent = new Intent(FarmBlockCoordinateUpdate.this, FarmBlockCoordinate.class);
                                intent.putExtra("farmerUniqueId", farmerUniqueId);
                                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                                intent.putExtra("farmerName", farmerName);
                                intent.putExtra("farmerMobile", farmerMobile);
                                intent.putExtra("farmBlockCode", farmBlockCode);
                                intent.putExtra("entryType", entryType);
                                intent.putExtra("farmerCode", farmerCode);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
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
                Intent intent = new Intent(FarmBlockCoordinateUpdate.this, FarmBlockIssues.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("entryType", entryType);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Double.valueOf(area) >= 10) {
                    int coordinateSize =0;
                    coordinateSize = BindCoordinates();
                    if (coordinateSize < 4) {
                        common.showToast(lang.equalsIgnoreCase("en")?"Minimum 4 coordinates are required!":"न्यूनतम 4 निर्देशांक आवश्यक हैं!", 5, 1);
                    }
                    else
                    {
                        //Move to FarmBlockList page
                        Intent intent = new Intent(FarmBlockCoordinateUpdate.this, FarmBlockList.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("entryType", entryType);
                        intent.putExtra("farmerCode", farmerCode);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    //Move to FarmBlockList page
                    Intent intent = new Intent(FarmBlockCoordinateUpdate.this, FarmBlockList.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmBlockCode", farmBlockCode);
                    intent.putExtra("entryType", entryType);
                    intent.putExtra("farmerCode", farmerCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        /*---------------End of code to set Click Event for Button-------------------------*/
    }


    //Method to bind GPS Coordinates in list view
    public int BindCoordinates() {
        //<editor-fold desc="Code to bind FarmBlock Coordinate Details">
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();
        List<CustomCoord> lables = dba.GetFarmBlockCoordinates(farmBlockUniqueId);
        dba.close();
        tvEmpty.setVisibility(View.VISIBLE);
        tvDivider.setVisibility(View.GONE);
        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("Latitude", lables.get(i).getLatitude());
            hm.put("Longitude", lables.get(i).getLongitude());
            hm.put("Accuracy", String.valueOf(lables.get(i).getAccuracy()));
            coordList.add(hm);
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        }

        // Keys used in Hashmap
        String[] from = {"Latitude", "Longitude"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvLatitude, R.id.tvLongitude};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), coordList, R.layout.list_farm_coordinates, from, to);
        lvCoordinates.setAdapter(adapter);
        //</editor-fold>
        return lables.size();
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockCoordinateUpdate.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockCoordinateUpdate.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(FarmBlockCoordinateUpdate.this, FarmBlockIssues.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("entryType", entryType);
        intent.putExtra("farmerCode", farmerCode);
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
