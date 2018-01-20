package lateralpraxis.lpsmartfarm.visits;

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
import lateralpraxis.lpsmartfarm.recommendation.Recommendation2;
import lateralpraxis.type.CustomData;

public class RoutineVisit2 extends Activity {

    CustomAdapter adapter;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvFarmBlock, tvEmpty;
    private ListView lpList;
    private View tvDivider;
    private Button btnBack, btnNext;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    /*------------------------End of code for variable Declaration------------------------------*/
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, EntryFor, FromPage;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit2);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        list = new ArrayList<HashMap<String, String>>();

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        lpList = (ListView) findViewById(R.id.lpList);
        tvDivider = (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

          /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            EntryFor = extras.getString("EntryFor");
            FromPage = extras.getString("FromPage");
            tvFarmer.setText(farmerName);
            tvFarmBlock.setText(farmBlockCode);
        }

        /*---------------Start of code to set Click Event for Back Button -------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move routine visit 2 to routine visit 1 page
                Intent intent = new Intent(RoutineVisit2.this, RoutineVisit1.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
            }
        });
        /*---------------End of code to set Click Event for Back Button -------------------------*/

        /*---------Code to bind list of plantation details---------------------------------*/
        dba.open();
        List<CustomData> lables = dba.getRoutinePlantationListByFarmBlockId(farmBlockUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("Name", lables.get(i).getName());
                list.add(hm);
            }
            adapter = new CustomAdapter(RoutineVisit2.this, list);
            lpList.setAdapter(adapter);
            lpList.setVisibility(View.VISIBLE);
tvDivider.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            lpList.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);	
        }

        lpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                Intent intent;
                if (EntryFor.equalsIgnoreCase("Recommendation")) {
                    dba.openR();
                    String weekNo = dba.getWeekNoForPlantation(String.valueOf(((TextView) item.findViewById(R.id.tvId)).getText().toString()));
                    if (TextUtils.isEmpty(weekNo)) {
                        common.showToast("Plantation week is not available. Please synchronize masters to fetch plantation week.", 5, 2);
                    } else {
                        intent = new Intent(RoutineVisit2.this, Recommendation2.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvId)).getText().toString()));
                        intent.putExtra("plantationName", String.valueOf(((TextView) item.findViewById(R.id.tvName)).getText().toString()));
                        intent.putExtra("EntryFor", EntryFor);
                        intent.putExtra("FromPage", FromPage);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    intent = new Intent(RoutineVisit2.this, RoutineVisit3.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmBlockCode", farmBlockCode);
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvId)).getText().toString()));
                    intent.putExtra("plantationName", String.valueOf(((TextView) item.findViewById(R.id.tvName)).getText().toString()));
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("FromPage", FromPage);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RoutineVisit2.this, RoutineVisit1.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RoutineVisit2.this, ActivityHome.class);
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
        Intent intent = new Intent(RoutineVisit2.this, RoutineVisit1.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
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

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvId, tvName;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lpList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            list = lpList;
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
                arg1 = mInflater.inflate(R.layout.list_routine_visit2, null);
                holder = new ViewHolder();
                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvName = (TextView) arg1.findViewById(R.id.tvName);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(list.get(arg0).get("Id"));
            holder.tvName.setText(list.get(arg0).get("Name"));
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
