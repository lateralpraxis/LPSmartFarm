package lateralpraxis.lpsmartfarm.recommendation;

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
import android.widget.AdapterView;
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
import lateralpraxis.lpsmartfarm.farmreporting.SearchFarmer;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.VisitNurseryData;


public class RecommendationList extends Activity {
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
    private ArrayList<HashMap<String, String>> list;

    /*-----------Start of Code for control declaration-----------*/
    private ListView lvList;
    private View tvDivider;
    private Button btnAdd;
    private TextView tvEmpty, tvRecommendationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_list);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        list = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        lvList = (ListView) findViewById(R.id.list);
        tvDivider = (View) findViewById(R.id.tvDivider);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            //On click of view delivery button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, SearchFarmer.class);
                intent.putExtra("EntryFor", "Recommendation");
                intent.putExtra("entryType", "add");
                intent.putExtra("FromPage", "FarmBlock");
                startActivity(intent);
                finish();
            }
        });

        /*------------Code to fetch and bind data from recommendation table*/
        dba.open();
        dba.DeleteTempRecommendation();
        List<VisitNurseryData> lables = dba.GetRecommendationFarmBlockList();
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("UniqueId", lables.get(i).getVisitUniqueId());
                hm.put("FarmerType", String.valueOf(lables.get(i).getNurseryType()));
                hm.put("MobileNursery", String.valueOf(lables.get(i).getNursery()));
                hm.put("FBCodeZone", String.valueOf(lables.get(i).getZone()));
                hm.put("Plantation", String.valueOf(lables.get(i).getPlantation()));
                hm.put("VisitDate", String.valueOf(lables.get(i).getVisitDate()));
                list.add(hm);
            }
            Cadapter = new CustomAdapter(RecommendationList.this, list);
            lvList.setAdapter(Cadapter);
            lvList.setVisibility(View.VISIBLE);
tvDivider.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);	
            lvList.setVisibility(View.GONE);
        }

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                Intent intent = new Intent(RecommendationList.this, RecommendationView.class);
                intent.putExtra("FromPage","FarmBlock");
                intent.putExtra("uniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvId)).getText().toString()));
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
                Intent intent = new Intent(RecommendationList.this, ActivityHome.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RecommendationList.this, ActivityHome.class);
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
        Intent intent = new Intent(RecommendationList.this, ActivityHome.class);
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
        TextView tvId, tvFarmer, tvMobile, tvFarmBlockCode, tvFarmerHeader, tvMobileHeader, tvFarmBlockCodeHeader, tvPlantation, tvVisitDate;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            list = lvList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_visit, null);
                holder = new ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvFarmer = (TextView) arg1.findViewById(R.id.tvFarmer);
                holder.tvMobile = (TextView) arg1.findViewById(R.id.tvMobile);
                holder.tvFarmBlockCode = (TextView) arg1.findViewById(R.id.tvFarmBlockCode);
                holder.tvFarmerHeader = (TextView) arg1.findViewById(R.id.tvFarmerHeader);
                holder.tvMobileHeader = (TextView) arg1.findViewById(R.id.tvMobileHeader);
                holder.tvFarmBlockCodeHeader = (TextView) arg1.findViewById(R.id.tvFarmBlockCodeHeader);
                holder.tvPlantation = (TextView) arg1.findViewById(R.id.tvPlantation);
                holder.tvVisitDate = (TextView) arg1.findViewById(R.id.tvVisitDate);

                arg1.setTag(holder);

            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(list.get(arg0).get("UniqueId"));
            if (list.get(arg0).get("FarmerType").equalsIgnoreCase("Main") || list.get(arg0).get("FarmerType").equalsIgnoreCase("Mini")) {
                holder.tvFarmerHeader.setText("Type");
                holder.tvMobileHeader.setText("Nursery");
                holder.tvFarmBlockCodeHeader.setText("Zone");
            } else {
                holder.tvFarmerHeader.setText("Farmer");
                holder.tvMobileHeader.setText("Mobile");
                holder.tvFarmBlockCodeHeader.setText("Farm Block");
            }
            holder.tvFarmer.setText(":\t" + list.get(arg0).get("FarmerType"));
            holder.tvMobile.setText(":\t" + list.get(arg0).get("MobileNursery"));
            holder.tvFarmBlockCode.setText(":\t" + list.get(arg0).get("FBCodeZone"));
            holder.tvPlantation.setText(":\t" + list.get(arg0).get("Plantation"));
            holder.tvVisitDate.setText(":\t" + common.convertToDisplayDateFormat(list.get(arg0).get("VisitDate")));
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
