package lateralpraxis.lpsmartfarm.visits;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.recommendation.RecommendationList;
import lateralpraxis.lpsmartfarm.recommendation.RecommendationNurseryList;
import lateralpraxis.type.CustomType;

public class RoutineVisitNursery1 extends Activity {

    /*------------------------Start of code for controls Declaration------------------------------*/
    private RadioGroup radioType;
    private RadioButton radioMain, radioMini;
    private Spinner spNursery, spNurseryZone;
    private Button btnBack, btnNext;
    /*------------------------End of code for controls Declaration------------------------------*/

    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/

    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, type, nurseryId, nursery, zoneId, zone, EntryFor, FromPage;
    /*------------------------End of code for variable Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit_nursery1);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls Declaration--------------------------*/
        radioType = (RadioGroup) findViewById(R.id.radioType);
        radioMain = (RadioButton) findViewById(R.id.radioMain);
        radioMini = (RadioButton) findViewById(R.id.radioMini);
        spNursery = (Spinner) findViewById(R.id.spNursery);
        spNurseryZone = (Spinner) findViewById(R.id.spNurseryZone);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

           /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
            FromPage = extras.getString("FromPage");
            EntryFor = extras.getString("EntryFor");
        }

        if (radioMain.isChecked())
            type = "Main";
        else if (radioMini.isChecked())
            type = "Mini";

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioType.findViewById(checkedId);
                int index = radioType.indexOfChild(radioButton);
                if (index == 0) {
                    type = "Main";
                } else {
                    type = "Mini";
                }
                //To bind nursery spinner
                spNursery.setAdapter(DataAdapter("nurserybytype", type));
            }
        });

        //To bind nursery spinner
        spNursery.setAdapter(DataAdapter("nurserybytype", type));
        if (nurseryId != null) {
            int spdCnt = spNursery.getAdapter().getCount();
            for (int i = 0; i < spdCnt; i++) {
                if (((CustomType) spNursery.getItemAtPosition(i)).getId().toString().equals(nurseryId))
                    spNursery.setSelection(i);
            }
        }

        /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
        spNursery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spNurseryZone.setAdapter(DataAdapter("nurseryzone", String.valueOf(((CustomType) spNursery.getSelectedItem()).getId())));
                if (zoneId != null) {
                    int spdCnt = spNurseryZone.getAdapter().getCount();
                    for (int i = 0; i < spdCnt; i++) {
                        if (((CustomType) spNurseryZone.getItemAtPosition(i)).getId().toString().equals(zoneId))
                            spNurseryZone.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        /*---------------Start of code to set Click Event for Button back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ((spNursery.getSelectedItemPosition() == 0))
                    common.showToast("Nursery is mandatory!", 5, 1);
                else if ((spNurseryZone.getSelectedItemPosition() == 0))
                    common.showToast("Zone is mandatory!", 5, 1);
                else {
                    //To move from RoutineVisitNursery1 to RoutineVisitNursery2 page
                    Intent intent = new Intent(RoutineVisitNursery1.this, RoutineVisitNursery2.class);
                    intent.putExtra("type", type);
                    intent.putExtra("nurseryId", String.valueOf(((CustomType) spNursery.getSelectedItem()).getId()));
                    intent.putExtra("nursery", String.valueOf(((CustomType) spNursery.getSelectedItem()).getName()));
                    intent.putExtra("zoneId", String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getId()));
                    intent.putExtra("zone", String.valueOf(((CustomType) spNurseryZone.getSelectedItem()).getName()));
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("FromPage", FromPage);
                    startActivity(intent);
                    finish();
                }
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/
    }

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
                onBackPressed();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RoutineVisitNursery1.this, ActivityHome.class);
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
        Intent intent;
        if (EntryFor.equalsIgnoreCase("Recommendation")) {
            if (FromPage.equalsIgnoreCase("FarmBlock"))
                //To move from RoutineVisitNursery1 to RecommendationList page
                intent = new Intent(RoutineVisitNursery1.this, RecommendationList.class);
            else
                //To move from RoutineVisitNursery1 to RecommendationNurseryList page
                intent = new Intent(RoutineVisitNursery1.this, RecommendationNurseryList.class);
        } else
            intent = new Intent(RoutineVisitNursery1.this, RoutineVisitNurseryList.class);
        intent.putExtra("type", type);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nursery", nursery);
        intent.putExtra("zoneId", zoneId);
        intent.putExtra("zone", zone);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("FromPage", FromPage);
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
