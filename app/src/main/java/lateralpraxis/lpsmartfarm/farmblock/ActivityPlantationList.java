package lateralpraxis.lpsmartfarm.farmblock;

//<editor-fold desc="Imports">

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import lateralpraxis.type.PlantationData;
//</editor-fold>

/**
 * Created by amol.marathe on 30-10-2017.
 */

public class ActivityPlantationList extends Activity {
    private final Context context = this;

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;

    /*Variables declaration*/
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> PlantationDetails;
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile,farmerCode,farmBlockCode,userRole;
    private static String lang;
    /*Code for control declaration*/
    private ListView listSelectPlantation;
    private TextView linkAdd, tvEmpty,tvFarmer,tvMobile,tvFarmBlock;
    private View tvDivider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plantation_list);

        /*Code for setting action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*Code for creating instance of class*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        PlantationDetails = new ArrayList<>();
        lang = session.getDefaultLang();
        /*Code for finding controls*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvDivider= (View) findViewById(R.id.tvDivider);
        listSelectPlantation = (ListView) findViewById(R.id.listSelectPlantation);

          /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmerCode = extras.getString("farmerCode");
            farmBlockCode = extras.getString("farmBlockCode");
            farmBlockUniqueId= extras.getString("farmBlockUniqueId");
            tvFarmer.setText(farmerName);
            tvMobile.setText(farmerMobile);
            tvFarmBlock.setText(farmBlockCode);
        }

        dba.openR();
        Boolean isFarmerEditable=false;
        userRole = dba.getAllRoles();
        isFarmerEditable =dba.isFarmerEditable(farmerUniqueId);
        if(isFarmerEditable || userRole.contains("Farmer"))
            linkAdd.setVisibility(View.VISIBLE);
        else
            linkAdd.setVisibility(View.GONE);
        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityPlantationList.this, ActivityAddPlantation.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", tvFarmBlock.getText().toString().trim());
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
        /*Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
        dba.open();
        List<PlantationData> lables = dba.getPlantationList(farmBlockUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("PlantationUniqueId", lables.get(i).getPlantationUniqueId());
                hm.put("Crop", lables.get(i).getCrop());
                hm.put("CropVariety", lables.get(i).getCropVariety());
                hm.put("Plants", lables.get(i).getTotalPlant());
                hm.put("PlantationCode", lables.get(i).getFarmerUniqueId());
                PlantationDetails.add(hm);
            }
        } else {
            tvDivider.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ActivityPlantationList.this, PlantationDetails);
        if (lsize > 0)
            listSelectPlantation.setAdapter(Cadapter);

        listSelectPlantation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id.tvPlantationUniqueId)).getText().toString()))) {
                    Intent intent = new Intent(ActivityPlantationList.this, ActivityViewPlantation.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvPlantationUniqueId)).getText().toString()));
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmBlockCode", tvFarmBlock.getText().toString().trim());
                    intent.putExtra("farmerCode", farmerCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public static class ViewHolder {
        TextView tvPlantationUniqueId, tvPlantationCode, tvPlantationCrop, tvPlantationCropVariety, tvPlantationNos;
        //LinearLayout llPlantationCode;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listSelectPlantation) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            PlantationDetails = listSelectPlantation;
        }

        @Override
        public int getCount() {
            return PlantationDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return PlantationDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_plantation, null);
                holder = new ViewHolder();

                holder.tvPlantationUniqueId = (TextView) arg1.findViewById(R.id.tvPlantationUniqueId);
                holder.tvPlantationCode = (TextView) arg1.findViewById(R.id.tvPlantationCode);
                holder.tvPlantationCrop = (TextView) arg1.findViewById(R.id.tvPlantationCrop);
                holder.tvPlantationCropVariety = (TextView) arg1.findViewById(R.id.tvPlantationCropVariety);
                holder.tvPlantationNos = (TextView) arg1.findViewById(R.id.tvPlantationNos);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvPlantationUniqueId.setText(PlantationDetails.get(arg0).get("PlantationUniqueId"));
            holder.tvPlantationCode.setText(PlantationDetails.get(arg0).get("PlantationCode"));
            holder.tvPlantationCrop.setText(PlantationDetails.get(arg0).get("Crop"));
            holder.tvPlantationCropVariety.setText(PlantationDetails.get(arg0).get("CropVariety"));
            holder.tvPlantationNos.setText(PlantationDetails.get(arg0).get("Plants"));
            holder.tvPlantationCode.setText((TextUtils.isEmpty(PlantationDetails.get(arg0).get("PlantationCode")))
                    ? ""
                    : PlantationDetails.get(arg0).get("PlantationCode"));
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityPlantationList.this, FarmBlockView.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmerCode", farmerCode);
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

        Intent intent = new Intent(ActivityPlantationList.this, FarmBlockView.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmerCode", farmerCode);
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
