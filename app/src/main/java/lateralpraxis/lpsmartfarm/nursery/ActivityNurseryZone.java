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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityNurseryZone extends Activity {
    private final Context mContext = this;
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private Spinner spNursery, spNurseryZone;
    private Button btnBack, btnNext;
    /*------------------------End of code for controls Declaration------------------------------*/
/*------------------------Start of code for variable Declaration------------------------------*/
    private int nurseryId = 0, nurseryZoneId = 0;
    private String nursery = "", nurseryZone = "", nzId = "0";
    /*------------------------Start of code for variable Declaration------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
         /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryId = Integer.valueOf(extras.getString("nurseryId"));
            nurseryZoneId = Integer.valueOf(extras.getString("nurseryZoneId"));
            nursery = String.valueOf(extras.getString("nurseryName"));
            nurseryZone = String.valueOf(extras.getString("nurseryZone"));
        }
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //Setting Layout
        setContentView(R.layout.activity_nursery_zone);
        //Creating instance for classes
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        spNursery = (Spinner) findViewById(R.id.spNursery);
        spNurseryZone = (Spinner) findViewById(R.id.spNurseryZone);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        spNursery.setAdapter(DataAdapter("nursery", ""));
        if (nurseryId > 0) {
            int spdCnt = spNursery.getAdapter().getCount();
            for (int i = 0; i < spdCnt; i++) {
                if (((CustomType) spNursery.getItemAtPosition(i)).getId().equals(nurseryId))
                    spNursery.setSelection(i);
            }
        }

        /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
        spNursery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spNurseryZone.setAdapter(DataAdapter("nurseryzone", String.valueOf(((CustomType) spNursery.getSelectedItem()).getId())));
                if (nurseryZoneId > 0) {
                    int spdCnt = spNurseryZone.getAdapter().getCount();
                    for (int i = 0; i < spdCnt; i++) {
                        if (((CustomType) spNurseryZone.getItemAtPosition(i)).getId().equals(nurseryZoneId))
                            spNurseryZone.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        spNurseryZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

         /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move back Farm Block Characteristic page
                Intent intent = new Intent(ActivityNurseryZone.this, ActivityHome.class);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ((spNursery.getSelectedItemPosition() == 0))
                    common.showToast("Nursery is mandatory");
                else {
                    Intent intent;
                    dba.openR();
                    nzId = String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getName()).equalsIgnoreCase((""))?"0":
                            String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getId());
                    boolean isExist = dba.isExistNurseryCoordinates(String.valueOf(((CustomType) spNursery.getSelectedItem()).getId()), String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getId()));
                    if (isExist)
                        intent = new Intent(ActivityNurseryZone.this, ActivityNurseryZoneCoordinateUpdate.class);
                    else
                        intent = new Intent(ActivityNurseryZone.this, ActivityNurseryZoneCoordinate.class);
                    intent.putExtra("nurseryId",  String.valueOf(((CustomType) spNursery.getSelectedItem()).getId()));
                    intent.putExtra("nurseryZoneId", nzId);
                    intent.putExtra("nurseryName", String.valueOf(((CustomType) spNursery.getSelectedItem()).getName()));
                    intent.putExtra("nurseryZone", String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getName()));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

/*---------------Method to view intent on Back Press Click-------------------------*/

    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityNurseryZone.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityNurseryZone.this, ActivityHome.class);
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
        Intent i = new Intent(ActivityNurseryZone.this, ActivityHome.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
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
