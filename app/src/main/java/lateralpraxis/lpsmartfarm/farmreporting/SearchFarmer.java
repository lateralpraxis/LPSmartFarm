package lateralpraxis.lpsmartfarm.farmreporting;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.EditText;
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
import lateralpraxis.lpsmartfarm.recommendation.RecommendationList;
import lateralpraxis.lpsmartfarm.visits.RoutineVisit1;
import lateralpraxis.lpsmartfarm.visits.RoutineVisitList;
import lateralpraxis.type.FarmerData;

public class SearchFarmer extends Activity {
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*--------------Start of Code for variable declaration-----------*/

    private int lsize = 0;
    private ArrayList<HashMap<String, String>> FarmerDetails;
    private String EntryFor;
    private static String lang;
    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView listSelectFarmer;
    private TextView tvEmpty;
    private EditText etSearchText;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_farmer);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

          /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        FarmerDetails = new ArrayList<HashMap<String, String>>();
        lang = session.getDefaultLang();
        /*------------------------End of code for creating instance of class--------------------*/

          /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            EntryFor = extras.getString("EntryFor");
        }
        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        listSelectFarmer = (ListView) findViewById(R.id.listSelectFarmer);
        etSearchText = (EditText) findViewById(R.id.etSearchText);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        /*Code to be executed on Search Button Click*/
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*------------Code to Fetch Farmers By Seach Text------------------*/
                if (!TextUtils.isEmpty(etSearchText.getText().toString().trim())) {
                    FarmerDetails.clear();
                    dba.open();
                    List<FarmerData> lables = dba.getFarmerListBySerachText(etSearchText.getText().toString().trim());
                    lsize = lables.size();
                    if (lsize > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        for (int i = 0; i < lables.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("FarmerUniqueId", lables.get(i).getFarmerUniqueId());
                            hm.put("FarmerCode", String.valueOf(lables.get(i).getFarmerCode()));
                            hm.put("FarmerName", String.valueOf(lables.get(i).getFarmerName()));
                            hm.put("FatherName", String.valueOf(lables.get(i).getFatherName()));
                            hm.put("Mobile", String.valueOf(lables.get(i).getMobile()));
                            hm.put("Address", String.valueOf(lables.get(i).getAddress()));
                            hm.put("IsDuplicate", String.valueOf(lables.get(i).getIsDuplicate()));
                            FarmerDetails.add(hm);
                        }
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    dba.close();

                    Cadapter = new CustomAdapter(SearchFarmer.this, FarmerDetails);
                    if (lsize > 0) {
                        listSelectFarmer.setAdapter(Cadapter);
                        tvEmpty.setVisibility(View.GONE);
                        listSelectFarmer.setVisibility(View.VISIBLE);
                    } else {

                        tvEmpty.setVisibility(View.VISIBLE);
                        listSelectFarmer.setVisibility(View.GONE);
                    }
                } else
                    common.showToast(lang.equalsIgnoreCase("en")?"Please enter search text.":"कृपया खोज पाठ दर्ज करें।");
            }
        });

        listSelectFarmer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (EntryFor.equalsIgnoreCase("FarmBlock")) {
                    Intent intent = new Intent(SearchFarmer.this, ReportingStep1.class);
                    intent.putExtra("farmerUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                    intent.putExtra("farmerName", String.valueOf(((TextView) item.findViewById(R.id.tvFarmer)).getText().toString()));
                    intent.putExtra("farmerMobile", String.valueOf(((TextView) item.findViewById(R.id.tvMobile)).getText().toString()));
                    intent.putExtra("EntryFor", EntryFor);
                    startActivity(intent);
                    finish();
                }
                else if (EntryFor.equalsIgnoreCase("visit") || EntryFor.equalsIgnoreCase("Recommendation")) {
                    Intent intent = new Intent(SearchFarmer.this, RoutineVisit1.class);
                    intent.putExtra("farmerUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                    intent.putExtra("farmerName", String.valueOf(((TextView) item.findViewById(R.id.tvFarmer)).getText().toString()));
                    intent.putExtra("farmerMobile", String.valueOf(((TextView) item.findViewById(R.id.tvMobile)).getText().toString()));
                    intent.putExtra("entryType", "add");
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("FromPage", "FarmBlock");
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
                onBackPressed();
                return true;
            case R.id.action_go_to_home:
                Intent intent1 = new Intent(SearchFarmer.this, ActivityHome.class);
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
        if (EntryFor.equalsIgnoreCase("FarmBlock"))
            intent = new Intent(SearchFarmer.this, FarmReportList.class);
        else if (EntryFor.equalsIgnoreCase("Recommendation"))
            intent = new Intent(SearchFarmer.this, RecommendationList.class);
        else if (EntryFor.equalsIgnoreCase("visit"))
            intent = new Intent(SearchFarmer.this, RoutineVisitList.class);
        else
            intent = new Intent(SearchFarmer.this, ActivityHome.class);
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
        TextView tvUniqueId, tvFarmerCode, tvFarmer, tvFather, tvMobile, tvAddress;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listSelectFarmer) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            FarmerDetails = listSelectFarmer;
        }

        @Override
        public int getCount() {
            return FarmerDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return FarmerDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_farmers, null);
                holder = new ViewHolder();

                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvFarmerCode = (TextView) arg1.findViewById(R.id.tvFarmerCode);
                holder.tvFarmer = (TextView) arg1.findViewById(R.id.tvFarmer);
                holder.tvFather = (TextView) arg1.findViewById(R.id.tvFather);
                holder.tvMobile = (TextView) arg1.findViewById(R.id.tvMobile);
                holder.tvAddress = (TextView) arg1.findViewById(R.id.tvAddress);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvUniqueId.setText(FarmerDetails.get(arg0).get("FarmerUniqueId"));
            holder.tvFarmerCode.setText(FarmerDetails.get(arg0).get("FarmerCode"));
            if (FarmerDetails.get(arg0).get("IsDuplicate").equalsIgnoreCase("1"))
                holder.tvFarmer.setText(Html.fromHtml("<font color=#FF0000> " + FarmerDetails.get(arg0).get("FarmerName") + "</font>"));
            else
                holder.tvFarmer.setText(FarmerDetails.get(arg0).get("FarmerName"));
            holder.tvFather.setText(FarmerDetails.get(arg0).get("FatherName"));
            holder.tvMobile.setText(FarmerDetails.get(arg0).get("Mobile"));
            holder.tvAddress.setText(FarmerDetails.get(arg0).get("Address"));
            if (TextUtils.isEmpty(FarmerDetails.get(arg0).get("FarmerCode")))
                holder.tvFarmerCode.setVisibility(View.GONE);
            else
                holder.tvFarmerCode.setVisibility(View.VISIBLE);
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
