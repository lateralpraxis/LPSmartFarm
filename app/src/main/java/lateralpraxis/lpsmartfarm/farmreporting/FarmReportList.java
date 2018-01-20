package lateralpraxis.lpsmartfarm.farmreporting;

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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;


public class FarmReportList extends Activity {


    final Context context = this;
    //<editor-fold desc="Code For Class Declaration">
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    //</editor-fold>
    private Intent intent;
    //<editor-fold desc="Code For Control Declaration">
    private TextView tvEmpty, linkReport;
    private View tvDivider;
    private ListView listReport;
    //</editor-fold>

    //<editor-fold desc="Code For Variable Declaration">
    private int listSize = 0;
    private String userRole;
    private ArrayList<String> farmerData;
    //</editor-fold>

    //<editor-fold desc="Code To Be executed on On Create">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_report_list);

        //<editor-fold desc="Code For Setting Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Code For Creating Instance of Class">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //<editor-fold desc="Code For Finding Text View">
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        linkReport = (TextView) findViewById(R.id.linkReport);
        listReport = (ListView) findViewById(R.id.listReport);
        //</editor-fold>

        dba.openR();
        userRole = dba.getAllRoles();

        linkReport.setOnClickListener(new View.OnClickListener() {
            //On click of view delivery button
            @Override
            public void onClick(View arg0) {
                if (userRole.contains("Farmer")) {
                    dba.openR();
                    farmerData = dba.getFarmerDetailsForFarmerLogin();
                    Intent intent = new Intent(FarmReportList.this, ReportingStep1.class);
                    intent.putExtra("farmerUniqueId", farmerData.get(0));
                    intent.putExtra("farmerName", farmerData.get(1));
                    intent.putExtra("farmerMobile", farmerData.get(2));
                    intent.putExtra("EntryFor", "FarmBlock");
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(context, SearchFarmer.class);
                    intent.putExtra("EntryFor", "FarmBlock");
                    startActivity(intent);
                    finish();
                }
            }
        });

        dba.openR();
        ArrayList<HashMap<String, String>> listJobCards = dba.getJobCardList();
        listSize = listJobCards.size();
        if (listSize != 0) {
            listReport.setAdapter(new ReportListAdapter(context, listJobCards));

            ViewGroup.LayoutParams params = listReport.getLayoutParams();
            //params.height = 500;
            listReport.setLayoutParams(params);
            listReport.requestLayout();
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            listReport.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        listReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                Intent intent = new Intent(FarmReportList.this, FarmReportDetail.class);
                intent.putExtra("jobCardUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvJobCardUniqueId)).getText().toString()));
                intent.putExtra("reportDate", String.valueOf(((TextView) item.findViewById(R.id.tvVisitDate)).getText().toString()));
                intent.putExtra("fbCode", String.valueOf(((TextView) item.findViewById(R.id.tvFBCode)).getText().toString()));
                intent.putExtra("plantation", String.valueOf(((TextView) item.findViewById(R.id.tvPlantation)).getText().toString()));
                startActivity(intent);
                finish();
                finish();
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Code for Providing Functionality on Back key Press And Action Bar Back and Home Button">
    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FarmReportList.this, ActivityHome.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent intent1 = new Intent(FarmReportList.this, ActivityHome.class);
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
//</editor-fold>

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FarmReportList.this, ActivityHome.class);
        intent.putExtra("EntryFor", "FarmBlock");
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

    //<editor-fold desc="Code Binding Data In List">
    public static class viewHolder {
        TextView tvVisitDate, tvFBCode, tvPlantation, tvJobCardUniqueId;
        int ref;
    }

    private class ReportListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listJobCards;
        String _type;
        private Context context2;

        public ReportListAdapter(Context context,
                                 ArrayList<HashMap<String, String>> listPlannedActivity) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listJobCards = listPlannedActivity;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listJobCards.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final viewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_farm_report, null);
                holder = new viewHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;
            holder.tvJobCardUniqueId = (TextView) convertView
                    .findViewById(R.id.tvJobCardUniqueId);
            holder.tvVisitDate = (TextView) convertView
                    .findViewById(R.id.tvVisitDate);
            holder.tvFBCode = (TextView) convertView
                    .findViewById(R.id.tvFBCode);
            holder.tvPlantation = (TextView) convertView
                    .findViewById(R.id.tvPlantation);

            final HashMap<String, String> itemPlannedActivity = _listJobCards.get(position);
            holder.tvJobCardUniqueId.setText(itemPlannedActivity.get("JobCardUniqueId"));
            holder.tvVisitDate.setText(common.convertToDisplayDateFormat(itemPlannedActivity.get("VisitDate")));
            holder.tvFBCode.setText(itemPlannedActivity.get("FarmBlockCode"));
            holder.tvPlantation.setText(itemPlannedActivity.get("Plantation").replace("/", "-"));
            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
    //</editor-fold>
}
