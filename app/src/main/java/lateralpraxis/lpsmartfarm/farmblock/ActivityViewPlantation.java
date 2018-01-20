package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityViewPlantation extends Activity {

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    /*Variables declaration*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmerCode, farmBlockCode, plantationUniqueId,plCode;

    /*Code for control declaration*/
    private TextView linkAdd, tvEmpty, tvFarmer, tvMobile, tvFarmBlock, tvCropHead, tvVarietyHead, tvTypeHead, tvMonthageHead, tvAreaHead, tvDateHead, tvSystemHead, tvRowHead, tvColHead, tvBalHead, tvTotHead;
    private ArrayList<String> plantationData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plantation);
         /*Code for setting action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*Code for creating instance of class*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());

         /*Code for finding controls*/
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvCropHead = (TextView) findViewById(R.id.tvCropHead);
        tvVarietyHead = (TextView) findViewById(R.id.tvVarietyHead);
        tvTypeHead = (TextView) findViewById(R.id.tvTypeHead);
        tvMonthageHead = (TextView) findViewById(R.id.tvMonthageHead);
        tvAreaHead = (TextView) findViewById(R.id.tvAreaHead);
        tvDateHead = (TextView) findViewById(R.id.tvDateHead);
        tvSystemHead = (TextView) findViewById(R.id.tvSystemHead);
        tvRowHead = (TextView) findViewById(R.id.tvRowHead);
        tvColHead = (TextView) findViewById(R.id.tvColHead);
        tvBalHead = (TextView) findViewById(R.id.tvBalHead);
        tvTotHead = (TextView) findViewById(R.id.tvTotHead);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmerCode = extras.getString("farmerCode");
            farmBlockCode = extras.getString("farmBlockCode");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            tvFarmer.setText(farmerName);
            tvMobile.setText(farmerMobile);
            tvFarmBlock.setText(farmBlockCode);
        }

        /*---------------Start of code to bind Plantation details-------------------------*/
        dba.openR();
        plantationData = dba.getPlantationDetailByUniqueId(plantationUniqueId);
        tvCropHead.setText(plantationData.get(2));
        tvVarietyHead.setText(plantationData.get(3));
        tvTypeHead.setText(plantationData.get(4));
        tvMonthageHead.setText(plantationData.get(5));
        tvAreaHead.setText(plantationData.get(6));
        tvDateHead.setText(plantationData.get(7).replace("/","-"));
        tvSystemHead.setText(plantationData.get(9));
        tvRowHead.setText(plantationData.get(10));
        tvColHead.setText(plantationData.get(11));
        tvBalHead.setText(plantationData.get(12));
        tvTotHead.setText(plantationData.get(13));
        plCode=plantationData.get(14);

        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityViewPlantation.this, ActivityViewInterCropping.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plCode);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode",farmBlockCode);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityViewPlantation.this, ActivityPlantationList.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmerCode", farmerCode);
                intent.putExtra("farmBlockCode",farmBlockCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(this, ActivityHome.class);
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

        Intent intent = new Intent(ActivityViewPlantation.this, ActivityPlantationList.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmerCode", farmerCode);
        intent.putExtra("farmBlockCode",farmBlockCode);
        startActivity(intent);
        finish();
    }

    // To create menu on inflater for displaying home button
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }*/

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
