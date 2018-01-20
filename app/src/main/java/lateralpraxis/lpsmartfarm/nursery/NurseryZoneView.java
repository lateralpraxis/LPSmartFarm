package lateralpraxis.lpsmartfarm.nursery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class NurseryZoneView extends Activity {
    private final Context mContext = this;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvType, tvNursery, tvNurseryZone, tvArea, tvEmptyCoordinates;
    private View tvDivider;
    private Button btnAdd, btnAddGps;
    private ListView lvCoordinates;
    /*------------------------End of code for class Declaration------------------------------*/

    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    private Intent intent;
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, nurseryId, zoneId, nurType, nurName, nurseryUniqueId, nurseryZoneName;
    /*------------------------End of code for variable Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nursery_zone_view);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls
         Declaration--------------------------*/
        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvEmptyCoordinates = (TextView) findViewById(R.id.tvEmptyCoordinates);
        tvDivider= (View) findViewById(R.id.tvDivider);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAddGps = (Button) findViewById(R.id.btnAddGps);
           /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryId = extras.getString("nurseryId");
            zoneId = extras.getString("nurseryZoneId");
            nurType = extras.getString("nurType");
            nurName = extras.getString("nurName");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
        }

        if (nurseryId != null) {
            dba.open();
            ArrayList<HashMap<String, String>> obj;
            obj = dba.GetNurseryZoneViewById(zoneId);
            dba.close();
            tvType.setText(obj.get(0).get("NurseryType"));
            tvNursery.setText(obj.get(0).get("Nursery"));
            nurseryZoneName = String.valueOf(obj.get(0).get("Zone"));
            tvNurseryZone.setText(obj.get(0).get("Zone"));
            tvArea.setText(obj.get(0).get("Area"));
        }

          /*--------Code to be executed on click event of Zone View link---------------------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(NurseryZoneView.this,
                        ActivityViewNurseryPlantationList.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurType);
                intent.putExtra("nurseryName", nurName);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
                startActivity(intent);
                finish();
            }
        });

    /*--------Code to be executed on click event of Zone Coordinatess Click---------------------*/
        btnAddGps.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                dba.openR();
                boolean isExist = dba.isExistNurseryCoordinates(nurseryId, zoneId);
                if (isExist)
                    intent = new Intent(NurseryZoneView.this, ActivityNurseryZoneCoordinateUpdate
                            .class);
                else
                    intent = new Intent(NurseryZoneView.this, ActivityNurseryZoneCoordinate.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId",zoneId);
                intent.putExtra("nurseryName", nurName);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurseryZone", nurseryZoneName);
                startActivity(intent);
                finish();
            }
        });

        //Method to bind GPS Coordinates in list view
        BindCoordinates();
    }

    //Method to bind GPS Coordinates in list view
    public int BindCoordinates() {
        //<editor-fold desc="Code to bind coordinate Details">
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();
        List<CustomCoord> lables = dba.GetNurseryCoordinates(nurseryId, zoneId);
        dba.close();
        tvEmptyCoordinates.setVisibility(View.VISIBLE);
        tvDivider.setVisibility(View.GONE);
        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("Latitude", lables.get(i).getLatitude());
            hm.put("Longitude", lables.get(i).getLongitude());
            //hm.put("Accuracy", String.valueOf(lables.get(i).getAccuracy()));
            coordList.add(hm);
            tvEmptyCoordinates.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        }

        // Keys used in Hashmap
        String[] from = {"Latitude", "Longitude"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvLatitude, R.id.tvLongitude};

        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), coordList, R.layout
                .list_farm_coordinates, from, to);
        lvCoordinates.setAdapter(adapter);
        //</editor-fold>
        return lables.size();
        //</editor-fold>
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //To move from visit details to plantation details page
                Intent intent = new Intent(NurseryZoneView.this, NurseryZone.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId",zoneId);
                intent.putExtra("nurName", nurName);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurseryZone", nurseryZoneName);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(NurseryZoneView.this, ActivityHome.class);
                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeScreenIntent);
                finish();
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
        //To move from visit details to plantation details page
        Intent intent = new Intent(NurseryZoneView.this, NurseryZone.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryZoneId",zoneId);
        intent.putExtra("nurName", nurName);
        intent.putExtra("nurType", nurType);
        intent.putExtra("nurseryZone", nurseryZoneName);
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
