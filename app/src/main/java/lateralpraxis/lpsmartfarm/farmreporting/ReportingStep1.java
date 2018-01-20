package lateralpraxis.lpsmartfarm.farmreporting;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomData;

public class ReportingStep1 extends Activity {
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;

    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId, farmerName, farmerMobile, EntryFor,userRole;
    private static String lang;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvFarmer, tvMobile;
    private Spinner spFarmBlock;
    private Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_step1);
         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

          /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());

         /*------------------------Code for finding controls-----------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        spFarmBlock = (Spinner) findViewById(R.id.spFarmBlock);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
         /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            EntryFor = extras.getString("EntryFor");
        }

        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);

        dba.openR();
        userRole = dba.getAllRoles();
        lang = session.getDefaultLang();
        //Code to bind Farm Blocks By Farmer Unique Id
        spFarmBlock.setAdapter(DataAdapter("farmblockforjobcardbyfarmer", farmerUniqueId));

          /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move on Search Farmer page
                Intent intent;
                if (userRole.contains("Farmer"))
                    intent = new Intent(ReportingStep1.this, FarmReportList.class);
                else
                    intent = new Intent(ReportingStep1.this, SearchFarmer.class);
                intent.putExtra("EntryFor", EntryFor);
                startActivity(intent);
                finish();
            }
        });

        //To validate and move to plantation population screen
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ((spFarmBlock.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Farm Block is mandatory!":"फार्म ब्लॉक अनिवार्य है!");
                else
                {
                    Intent intent = new Intent(ReportingStep1.this, ReportingStep2.class);
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmblockUniqueId", ((CustomData) spFarmBlock.getSelectedItem()).getId());
                    intent.putExtra("farmblockCode",((CustomData) spFarmBlock.getSelectedItem()).getName());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<CustomData> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomData> lables = dba.GetOtherMasterDetails(masterType, filter);
        ArrayAdapter<CustomData> dataAdapter = new ArrayAdapter<CustomData>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                if (userRole.contains("Farmer"))
                    intent = new Intent(ReportingStep1.this, FarmReportList.class);
                else
                    intent = new Intent(ReportingStep1.this, SearchFarmer.class);
                intent.putExtra("EntryFor", EntryFor);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent intent1 = new Intent(ReportingStep1.this, ActivityHome.class);
                startActivity(intent1);
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
        Intent intent;
        if (userRole.contains("Farmer"))
            intent = new Intent(ReportingStep1.this, FarmReportList.class);
        else
            intent = new Intent(ReportingStep1.this, SearchFarmer.class);
        intent.putExtra("EntryFor", "FarmBlock");
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
