package lateralpraxis.lpsmartfarm.nursery;

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

public class ActivityViewNurseryPlantation extends Activity {

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;

    /*Variables declaration*/
    private String userId, plantationUniqueId, plantationCode, nurseryUniqueId, nurseryId, nurseryType, nurseryName, plantationId,nurseryZoneName, zoneId;

    /*Code for control declaration*/
    private TextView linkInterCropping, linkUpdate, tvEmpty, tvNurseryUniqueId, tvNurseryId, tvNursertType, tvNurseryName,
            tvPlantationCode, tvZoneId, tvZone, tvPlantationId, tvPlantationUniqueId, tvCrop, tvVariety, tvType, tvMonthAge, tvArea,
            tvDate, tvSystem, tvRow, tvCol, tvBal, tvTot;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nursery_plantation);

        /*Code for setting action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*Code for creating instance of class*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        linkInterCropping = (TextView) findViewById(R.id.linkInterCropping);
        linkUpdate = (TextView) findViewById(R.id.linkUpdate);

        /*Code for finding controls*/
        tvNurseryUniqueId = (TextView) findViewById(R.id.tvNurseryUniqueId);
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvPlantationId = (TextView) findViewById(R.id.tvPlantationId);
        tvPlantationUniqueId = (TextView) findViewById(R.id.tvPlantationUniqueId);
        tvPlantationCode = (TextView) findViewById(R.id.tvPlantationCode);
        tvZoneId = (TextView) findViewById(R.id.tvZoneId);
        tvZone = (TextView) findViewById(R.id.tvZone);
        tvNursertType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvCrop = (TextView) findViewById(R.id.tvCrop);
        tvVariety = (TextView) findViewById(R.id.tvVariety);
        tvType = (TextView) findViewById(R.id.tvType);
        tvMonthAge = (TextView) findViewById(R.id.tvMonthAge);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvSystem = (TextView) findViewById(R.id.tvSystem);
        tvRow = (TextView) findViewById(R.id.tvRow);
        tvCol = (TextView) findViewById(R.id.tvCol);
        tvBal = (TextView) findViewById(R.id.tvBal);
        tvTot = (TextView) findViewById(R.id.tvTot);

        /*Code to get data from posted page*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationCode = extras.getString("plantationCode");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            nurseryZoneName = extras.getString("nurseryZoneName");
            zoneId = extras.getString("zoneId");
        }

        /*Start of code to bind Plantation details*/
        dba.openR();
        list = dba.getNurseryPlantationDetailByUniqueId(plantationUniqueId);
        tvPlantationUniqueId.setText(plantationUniqueId);
        tvNurseryId.setText(nurseryId);
        tvNursertType.setText(nurseryType);
        tvNurseryName.setText(nurseryName);
        tvPlantationCode.setText(list.get(1));
        tvZoneId.setText(list.get(4));
        tvZone.setText(list.get(6));
        tvCrop.setText(list.get(8));
        tvVariety.setText(list.get(10));
        tvType.setText(list.get(12));
        tvMonthAge.setText(list.get(14));
        tvArea.setText(list.get(15));
        tvDate.setText(list.get(16).replace("/", "-"));
        tvSystem.setText(list.get(18));
        tvRow.setText(list.get(19));
        tvCol.setText(list.get(20));
        tvBal.setText(list.get(21));
        tvTot.setText(list.get(22));

        linkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityViewNurseryPlantation.this, ActivityUpdateNurseryPlantation.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("nurseryZoneUniqueId", (list.get(4)));
                intent.putExtra("nurseryZoneId", zoneId);
                intent.putExtra("nurseryZone", (list.get(6)));
                startActivity(intent);
                finish();
            }
        });

        linkInterCropping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityViewNurseryPlantation.this, ActivityViewNurseryInterCropping.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("nurseryZoneUniqueId", (list.get(4)));
                intent.putExtra("nurseryZoneId", zoneId);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("plarea", (list.get(15)));
                startActivity(intent);
                finish();
            }
        });
    }

    /*Method to view intent on Action Bar Click*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityViewNurseryPlantation.this, ActivityViewNurseryPlantationList.class);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
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

    /*To create menu on inflater*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /*Method to view intent on Back Press Click*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityViewNurseryPlantation.this, ActivityViewNurseryPlantationList.class);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryType", nurseryType);
        intent.putExtra("nurseryName", nurseryName);
        intent.putExtra("plantationCode", plantationCode);
        intent.putExtra("nurseryZone", nurseryZoneName);
        intent.putExtra("nurseryZoneId", zoneId);
        startActivity(intent);
        finish();
    }

    /*Method to check android version ad load action bar appropriately*/
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
