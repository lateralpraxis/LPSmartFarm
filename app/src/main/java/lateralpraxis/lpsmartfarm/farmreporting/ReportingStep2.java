package lateralpraxis.lpsmartfarm.farmreporting;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomData;

public class ReportingStep2 extends Activity {
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId, farmerName, farmerMobile, farmblockCode, farmblockUniqueId, jobcardUniqueId, EntryFor, jobcardU;
    private ArrayList<HashMap<String, String>> PlantationDetails;
    private int lsize = 0;
    private static String lang;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvEmpty;
    private View tvDivider;
    private ListView lvPlantation;

    private Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_step2);
         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

          /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        PlantationDetails = new ArrayList<HashMap<String, String>>();
        lang = session.getDefaultLang();
         /*------------------------Code for finding controls-----------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        lvPlantation = (ListView) findViewById(R.id.lvPlantation);
         /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmblockCode = extras.getString("farmblockCode");
            farmblockUniqueId = extras.getString("farmblockUniqueId");
            EntryFor = extras.getString("EntryFor");
        }
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        tvFarmBlock.setText(farmblockCode);

        PlantationDetails.clear();
        dba.open();
        /*************Method to fetch Plantation By Farm Block Id**********************/
        List<CustomData> lables = dba.getPlantationListByFarmBlockId(farmblockUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("Name", String.valueOf(lables.get(i).getName()).replace("/","-"));
                PlantationDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ReportingStep2.this, PlantationDetails);
        if (lsize > 0) {
            lvPlantation.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvPlantation.setVisibility(View.VISIBLE);
        } else {

            tvEmpty.setVisibility(View.VISIBLE);
            lvPlantation.setVisibility(View.GONE);
        }

        /*************On click event for List View**********************/
        lvPlantation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                dba.openR();
                String weekNo = dba.getWeekNoForPlantation(String.valueOf(((TextView) item.findViewById(R.id.tvPlantationUniqueId)).getText().toString()));
                if (TextUtils.isEmpty(weekNo)) {
                    common.showToast(lang.equalsIgnoreCase("en")?"Plantation week is not available. Please synchronize masters to fetch plantation week.":"रोपण सप्ताह उपलब्ध नहीं है। कृपया रोपण सप्ताह लाने के लिए मास्टर को सिंक्रनाइज़ करें।", 5, 2);
                } else {
                    dba.open();
                    /*************Code to delete data from temporary table**********************/
                    dba.deleteTempJobCardDetail();

                    /*************Code to fetch Job Card Unique Id by Plantation Unique Id**********************/
                    jobcardUniqueId = dba.getJobCardUniqueId(String.valueOf(((TextView) item.findViewById(R.id.tvPlantationUniqueId)).getText().toString()), farmblockUniqueId);

                    /*************Method to Move Data from Main Table to Temporary Table**********************/
                    dba.InsertMainToTempJobCard(jobcardUniqueId);
                    dba.close();
                    if (TextUtils.isEmpty(jobcardUniqueId))
                        jobcardUniqueId = UUID.randomUUID().toString();
                    Intent intent = new Intent(ReportingStep2.this, ReportingStep3.class);
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                    intent.putExtra("farmblockCode", farmblockCode);
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvPlantationUniqueId)).getText().toString()));
                    intent.putExtra("plantation", String.valueOf(((TextView) item.findViewById(R.id.tvPlantationName)).getText().toString()));
                    intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                    startActivity(intent);
                    finish();

                }
            }
        });
        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move on Reporting Step 1
                Intent intent = new Intent(ReportingStep2.this, ReportingStep1.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
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
                Intent intent = new Intent(ReportingStep2.this, ReportingStep1.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(ReportingStep2.this);
                // set title
                alertDialogBuilderh.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilderh
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(ReportingStep2.this, ActivityHome.class);
                                startActivity(intent1);
                                finish();

                            }
                        })
                        .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialogh = alertDialogBuilderh.create();
                // show it
                alertDialogh.show();
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
        Intent intent = new Intent(ReportingStep2.this, ReportingStep1.class);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmblockUniqueId", farmblockUniqueId);
        intent.putExtra("farmblockCode", farmblockCode);
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

    /*-----------Start of Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvPlantationUniqueId, tvPlantationName;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvPlantation) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            PlantationDetails = lvPlantation;
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
                arg1 = mInflater.inflate(R.layout.list_plantation_variety, null);
                holder = new ViewHolder();

                holder.tvPlantationUniqueId = (TextView) arg1.findViewById(R.id.tvPlantationUniqueId);
                holder.tvPlantationName = (TextView) arg1.findViewById(R.id.tvPlantationName);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvPlantationUniqueId.setText(PlantationDetails.get(arg0).get("Id"));
            holder.tvPlantationName.setText(PlantationDetails.get(arg0).get("Name"));

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
    /*-----------End of Code for Handling Data Binding---------------------------*/
}
