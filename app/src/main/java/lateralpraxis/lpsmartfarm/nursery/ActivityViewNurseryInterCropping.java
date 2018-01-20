package lateralpraxis.lpsmartfarm.nursery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.NurseryInterCroppingData;

public class ActivityViewNurseryInterCropping extends Activity {
    final Context context = this;
    private Intent intent;

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter customAdapter;

    /*Code for variable declaration*/
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> interCroppingList;
    private String userId, nurseryUniqueId, nurseryId, nurseryType, nurseryName, plantationUniqueId, plantationCode,
            nurseryZoneUniqueId, nurseryZoneId, nurseryZone,plarea;

    /*Code for control declaration*/
    private ListView list;
    private Button btnBack;
    private TextView tvEmpty, linkAdd,
            tvNurseryUniqueId, tvNurseryId, tvNurseryType, tvNurseryName, tvZoneUniqueId, tvZoneId, tvZone, tvPlantationUniqueCode,
            tvPlantationCode;
    private View tvDivider;
    private LinearLayout llPlantationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nursery_inter_cropping);

        /*Code for setting Action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*Code for creating instance of classes*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        interCroppingList = new ArrayList<>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);


        /*Code for finding controls*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        list = (ListView) findViewById(R.id.list);
        llPlantationCode = (LinearLayout) findViewById(R.id.llPlantationCode);

        tvPlantationUniqueCode = (TextView) findViewById(R.id.tvPlantationUniqueId);
        tvPlantationCode = (TextView) findViewById(R.id.tvPlantationCode);
        tvNurseryUniqueId = (TextView) findViewById(R.id.tvNurseryUniqueId);
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvPlantationUniqueCode = (TextView) findViewById(R.id.tvPlantationUniqueId);
        tvZoneUniqueId = (TextView) findViewById(R.id.tvZoneUniqueId);
        tvZoneId = (TextView) findViewById(R.id.tvZoneId);
        tvZone = (TextView) findViewById(R.id.tvZone);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);


        /*Code to get the data from posted page*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationCode = extras.getString("plantationCode");
            nurseryZoneUniqueId = extras.getString("nurseryZoneUniqueId");
            nurseryZoneId = extras.getString("nurseryZoneId");
            nurseryZone = extras.getString("nurseryZone");
            plarea=extras.getString("plarea");


            tvNurseryUniqueId.setText(nurseryUniqueId);
            tvNurseryId.setText(nurseryId);
            tvNurseryType.setText(nurseryType);
            tvNurseryName.setText(nurseryName);
            tvZoneUniqueId.setText(nurseryZoneUniqueId);
            tvZoneId.setText(nurseryZoneId);
            tvZone.setText(nurseryZone);
            tvPlantationUniqueCode.setText(plantationCode);
            tvPlantationCode.setText(plantationCode);
        }

        /*Add OnClickListener on link*/
        linkAdd.setOnClickListener(new View.OnClickListener() {
            //On click of add inter cropping button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, ActivityAddNurseryInterCropping.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurseryZone", nurseryZone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("entryType", "add");
                intent.putExtra("plarea", plarea);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, ActivityViewNurseryPlantation.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("zoneId", nurseryZoneId);
                intent.putExtra("nurseryZoneName", nurseryZone);
                startActivity(intent);
                finish();
            }
        });

        /*Code to fetch and bind data from Nursery Inter Cropping table*/
        dba.open();
        List<NurseryInterCroppingData> data = dba.getNurseryInterCroppingByPlantationUniqueId(plantationUniqueId);
        lsize = data.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < data.size(); i++) {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("Id", data.get(i).getId());
                hm.put("NurseryId", String.valueOf(data.get(i).getNurseryId()));
                hm.put("NurseryType", String.valueOf(data.get(i).getNurseryType()));
                hm.put("NurseryName", String.valueOf(data.get(i).getNurseryName()));
                hm.put("CropId", String.valueOf(data.get(i).getCropId()));
                hm.put("Crop", String.valueOf(data.get(i).getCrop()));
                hm.put("CropVarietyId", String.valueOf(data.get(i).getCropVarietyId()));
                hm.put("CropVariety", String.valueOf(data.get(i).getCropVariety()));
                hm.put("SeasonId", String.valueOf(data.get(i).getSeasonId()));
                hm.put("Season", String.valueOf(data.get(i).getSeason()));
                hm.put("Acreage", String.valueOf(data.get(i).getAcreage()));
                interCroppingList.add(hm);
            }
        } else {
            tvDivider.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        dba.close();

        customAdapter = new CustomAdapter(ActivityViewNurseryInterCropping.this, interCroppingList);
        if (lsize > 0)
            list.setAdapter(customAdapter);
    }

    //<editor-fold desc="Method to view intent on Action Bar Click">
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                intent = new Intent(context, ActivityViewNurseryPlantation.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("zoneId", nurseryZoneId);
                intent.putExtra("nurseryZoneName", nurseryZone);

                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(ActivityViewNurseryInterCropping.this, ActivityHome.class);
                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeScreenIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //</editor-fold>

    //<editor-fold desc="To create menu on inflater">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Method to view intent on Back Press Click">
    @Override
    public void onBackPressed() {
        //Go to ActivityViewPlantation page
        Intent intent = new Intent(ActivityViewNurseryInterCropping.this, ActivityViewNurseryPlantation.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryType", nurseryType);
        intent.putExtra("nurseryName", nurseryName);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantationCode", plantationCode);
        intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
        intent.putExtra("zoneId", nurseryZoneId);
        intent.putExtra("nurseryZoneName", nurseryZone);
        startActivity(intent);
        finish();
    }
    //</editor-fold>

    //<editor-fold desc="Method to check android version ad load action bar appropriately">
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void actionBarSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setIcon(R.mipmap.ic_launcher);
            ab.setHomeButtonEnabled(true);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewHolder Class">
    public static class ViewHolder {
        TextView tvId, tvFirst, tvSecond;
        Button btnDelete;
    }
    //</editor-fold>


    //<editor-fold desc="CustomAdapter class implementation">
    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            interCroppingList = list;
        }

        @Override
        public int getCount() {
            return interCroppingList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return interCroppingList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {

            final ViewHolder holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_cropping_pattern_details, null);
                holder = new ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvFirst = (TextView) arg1.findViewById(R.id.tvFirst);
                holder.tvSecond = (TextView) arg1.findViewById(R.id.tvSecond);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(interCroppingList.get(arg0).get("Id"));
            holder.tvFirst.setText(interCroppingList.get(arg0).get("Season"));
            holder.tvSecond.setText(interCroppingList.get(arg0).get("Crop") + " - " + interCroppingList.get(arg0).get("CropVariety") + " , " + interCroppingList.get(arg0).get("Acreage") + " Sq.Ft.");
            holder.btnDelete.setVisibility(View.GONE);
            /*holder.btnDelete.setVisibility((interCroppingList.get(arg0).get("IsSync").equalsIgnoreCase("1"))
                    ? View.GONE
                    : View.VISIBLE);*/
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
    //</editor-fold>
}
