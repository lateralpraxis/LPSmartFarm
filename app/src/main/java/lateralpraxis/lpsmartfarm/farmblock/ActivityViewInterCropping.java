package lateralpraxis.lpsmartfarm.farmblock;

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
import lateralpraxis.type.InterCroppingData;

public class ActivityViewInterCropping extends Activity {
    final Context context = this;
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    private Intent intent;
    /*--------------Start of Code for variable declaration-----------*/
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> interCroppingList;
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmerCode, plantationUniqueId, farmBlockCode, plantationCode,userRole;
    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView list;
    private Button btnUpdate;
    private TextView tvEmpty, linkAdd, tvFarmer, tvMobile, tvFarmBlock, tvPlantationCode;
    private View tvDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inter_cropping);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        interCroppingList = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvPlantationCode = (TextView) findViewById(R.id.tvPlantationCode);
        tvDivider= (View) findViewById(R.id.tvDivider);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        list = (ListView) findViewById(R.id.list);
        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            plantationCode = extras.getString("plantationCode");
            farmerCode = extras.getString("farmerCode");
            tvFarmer.setText(farmerName);
            tvFarmBlock.setText(farmBlockCode);
            tvMobile.setText(farmerMobile);
            tvPlantationCode.setText(plantationCode);
        }
        dba.openR();
        Boolean isFarmerEditable=false;
        userRole = dba.getAllRoles();
        isFarmerEditable =dba.isFarmerEditable(farmerUniqueId);
        if(isFarmerEditable  || userRole.contains("Farmer"))
            linkAdd.setVisibility(View.VISIBLE);
        else
            linkAdd.setVisibility(View.GONE);
        linkAdd.setOnClickListener(new View.OnClickListener() {
            //On click of add inter cropping button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, ActivityAddInterCropping.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("farmerCode", farmerCode);
                intent.putExtra("entryType", "add");
                startActivity(intent);
                finish();
            }
        });

        /*------------Code to Fetch and Bind Data from Farm Block Table*/
        dba.open();
        List<InterCroppingData> lables = dba.getInterCroppingByUniqueId(plantationUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("FarmBlockUniqueId", String.valueOf(lables.get(i).getFarmBlockUniqueId()));
                hm.put("CropId", String.valueOf(lables.get(i).getCropId()));
                hm.put("Crop", String.valueOf(lables.get(i).getCrop()));
                hm.put("CropVarietyId", String.valueOf(lables.get(i).getCropVarietyId()));
                hm.put("CropVariety", String.valueOf(lables.get(i).getCropVariety()));
                hm.put("SeasonId", String.valueOf(lables.get(i).getSeasonId()));
                hm.put("Season", String.valueOf(lables.get(i).getSeason()));
                hm.put("Acreage", String.valueOf(lables.get(i).getAcreage()));
                interCroppingList.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ActivityViewInterCropping.this, interCroppingList);
        if (lsize > 0)
            list.setAdapter(Cadapter);
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityViewInterCropping.this, ActivityViewPlantation.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(ActivityViewInterCropping.this, ActivityHome.class);
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
        //Go to ActivityViewPlantation page
        Intent intent = new Intent(ActivityViewInterCropping.this, ActivityViewPlantation.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("plantationCode", plantationCode);
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

    public static class ViewHolder {
        TextView tvId, tvFirst, tvSecond;
    }

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
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(interCroppingList.get(arg0).get("Id"));
            holder.tvFirst.setText(interCroppingList.get(arg0).get("Season"));
            holder.tvSecond.setText(interCroppingList.get(arg0).get("Crop") + " - " + interCroppingList.get(arg0).get("CropVariety") + " , " + interCroppingList.get(arg0).get("Acreage") + " Acre");
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
