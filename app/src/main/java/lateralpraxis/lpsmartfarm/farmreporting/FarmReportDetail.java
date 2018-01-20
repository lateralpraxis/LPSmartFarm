package lateralpraxis.lpsmartfarm.farmreporting;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;

public class FarmReportDetail extends Activity {

    final Context context = this;
    //<editor-fold desc="Code For Class Declaration">
    Common common;
    DatabaseAdapter dba;
    //</editor-fold>
    UserSessionManager session;
    File filePlanned;
    //<editor-fold desc="Code For Variable Declaration">
    private String jobcardUniqueId, fbCode, reportDate, plantation;
    private int plListSize = 0;
    private int plRecomSize = 0;
    private int plUnplanSize = 0;
    private File[] listFilePlanned;
    private String[] FilePathStringsPlanned, FileNameStringsPlanned;
    //</editor-fold>
    //<editor-fold desc="Code For Control Declaration">
    private TextView tvDate, tvFBCode, tvPlantation, tvPlannedEmpty, tvRecommendedEmpty, tvUnplannedEmpty;
    private ListView lvUnPlannedList, lvRecommendedList, lvPlannedList;
    private View tvDividerUnplanned, tvDividerRecommended, tvDividerPlanned;
    //</editor-fold>

    //<editor-fold desc="Code To Be executed on On Create">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_report_detail);

        //<editor-fold desc="Code For Setting Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Code For Creating Instance of Class">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //<editor-fold desc="Code For Finding Controls">
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvFBCode = (TextView) findViewById(R.id.tvFBCode);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvPlannedEmpty = (TextView) findViewById(R.id.tvPlannedEmpty);
        tvRecommendedEmpty = (TextView) findViewById(R.id.tvRecommendedEmpty);
        tvUnplannedEmpty = (TextView) findViewById(R.id.tvUnplannedEmpty);
        tvDividerUnplanned = (View) findViewById(R.id.tvDividerUnplanned);
        tvDividerRecommended = (View) findViewById(R.id.tvDividerRecommended);
        tvDividerPlanned = (View) findViewById(R.id.tvDividerPlanned);
        lvUnPlannedList = (ListView) findViewById(R.id.lvUnPlannedList);
        lvRecommendedList = (ListView) findViewById(R.id.lvRecommendedList);
        lvPlannedList = (ListView) findViewById(R.id.lvPlannedList);
        //</editor-fold>

        //<editor-fold desc="Code to Bind data from Previous Intent">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {

            jobcardUniqueId = extras.getString("jobCardUniqueId");
            fbCode = extras.getString("fbCode");
            reportDate = extras.getString("reportDate");
            plantation = extras.getString("plantation");
        }
        //</editor-fold>

        //<editor-fold desc="Code to set data in Text View">
        tvDate.setText(reportDate);
        tvFBCode.setText(fbCode);
        tvPlantation.setText(plantation.replace("/", "-"));
        //</editor-fold>

        //<editor-fold desc="Code to Bind Planned List Activities">
        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewPlanned = dba.getPlannedActivities(jobcardUniqueId);
        plListSize = listDetailForViewPlanned.size();
        if (listDetailForViewPlanned.size() != 0) {
            lvPlannedList.setAdapter(new PlannedListAdapter(context, listDetailForViewPlanned));

            ViewGroup.LayoutParams params = lvPlannedList.getLayoutParams();
            params.height = 500;
            lvPlannedList.setLayoutParams(params);
            lvPlannedList.requestLayout();
            tvPlannedEmpty.setVisibility(View.GONE);
            tvDividerPlanned.setVisibility(View.VISIBLE);
        } else {
            lvPlannedList.setAdapter(null);
            tvPlannedEmpty.setVisibility(View.VISIBLE);
            tvDividerPlanned.setVisibility(View.GONE);
        }
        //</editor-fold>

        //<editor-fold desc="Code to Bind Recommended List Activities">
        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewRecommended = dba.getRecommendedActivities(jobcardUniqueId);
        plRecomSize = listDetailForViewRecommended.size();
        if (listDetailForViewRecommended.size() != 0) {
            lvRecommendedList.setAdapter(new PlannedListAdapter(context, listDetailForViewRecommended));

            ViewGroup.LayoutParams params = lvRecommendedList.getLayoutParams();
            params.height = 500;
            lvRecommendedList.setLayoutParams(params);
            lvRecommendedList.requestLayout();
            tvRecommendedEmpty.setVisibility(View.GONE);
            tvDividerRecommended.setVisibility(View.VISIBLE);
        } else {
            lvRecommendedList.setAdapter(null);
            tvRecommendedEmpty.setVisibility(View.VISIBLE);
            tvDividerRecommended.setVisibility(View.GONE);
        }
        //</editor-fold>

        //<editor-fold desc="Code to Bind UnPlanned Activities">
        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewAdditional = dba.getUnplannedActivities(jobcardUniqueId);
        plUnplanSize = listDetailForViewAdditional.size();
        if (listDetailForViewAdditional.size() != 0) {
            lvUnPlannedList.setAdapter(new AdditionalListAdapter(context, listDetailForViewAdditional));

            ViewGroup.LayoutParams params = lvUnPlannedList.getLayoutParams();
            params.height = 500;
            lvUnPlannedList.setLayoutParams(params);
            lvUnPlannedList.requestLayout();
            tvUnplannedEmpty.setVisibility(View.GONE);
            tvDividerUnplanned.setVisibility(View.VISIBLE);
        } else {
            lvUnPlannedList.setAdapter(null);
            tvUnplannedEmpty.setVisibility(View.VISIBLE);
            tvDividerUnplanned.setVisibility(View.GONE);
        }
        //</editor-fold>

    }
    //</editor-fold>

    //<editor-fold desc="Code for Providing Functionality on Back key Press And Action Bar Back and Home Button">
    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FarmReportDetail.this, FarmReportList.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent intent1 = new Intent(FarmReportDetail.this, ActivityHome.class);
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
        Intent intent = new Intent(FarmReportDetail.this, FarmReportList.class);
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
    //</editor-fold>

    //<editor-fold desc="Code for Displaying Planned and Recommended Activity">
    public static class viewHolder {
        TextView tvName, tvFileName, tvPlanned, tvActual, tvUom, tvTotal, tvRemarks;
        Button btnViewAttach;
        int ref;
    }

    //<editor-fold desc="Code for Displaying Un Planned Activity">
    public static class viewUnplannedHolder {
        TextView tvName, tvFileName, tvPlanned, tvActual, tvUom;
        Button btnViewAttach;
        int ref;
    }
    //</editor-fold>

    private class PlannedListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listPlannedActivity;
        String _type;
        private Context context2;

        public PlannedListAdapter(Context context,
                                  ArrayList<HashMap<String, String>> listPlannedActivity) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listPlannedActivity = listPlannedActivity;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listPlannedActivity.size();
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
                convertView = inflater.inflate(R.layout.list_planned_recommended, null);
                holder = new viewHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvName = (TextView) convertView
                    .findViewById(R.id.tvName);
            holder.tvPlanned = (TextView) convertView
                    .findViewById(R.id.tvPlanned);
            holder.tvActual = (TextView) convertView
                    .findViewById(R.id.tvActual);
            holder.tvUom = (TextView) convertView
                    .findViewById(R.id.tvUom);
            holder.tvTotal = (TextView) convertView
                    .findViewById(R.id.tvTotal);
            holder.tvRemarks = (TextView) convertView
                    .findViewById(R.id.tvRemarks);
            holder.btnViewAttach = (Button) convertView
                    .findViewById(R.id.btnViewAttach);
            holder.tvFileName = (TextView) convertView
                    .findViewById(R.id.tvFileName);
            final HashMap<String, String> itemPlannedActivity = _listPlannedActivity.get(position);


            if (!TextUtils.isEmpty(itemPlannedActivity.get("SubActivityName")))
                holder.tvName.setText(itemPlannedActivity.get("ActivityName") + " - " + itemPlannedActivity.get("SubActivityName"));// + " - " + itemPlannedActivity.get("Quantity") + " " + itemPlannedActivity.get("UOM") + " - " + itemPlannedActivity.get("ActualQty") + " " + itemPlannedActivity.get("UOM"));
            else
                holder.tvName.setText(itemPlannedActivity.get("ActivityName"));// + " - " + itemPlannedActivity.get("Quantity") + " " + itemPlannedActivity.get("UOM") + " - " + itemPlannedActivity.get("ActualQty") + " " + itemPlannedActivity.get("UOM"));
            holder.tvPlanned.setText(common.stringToTwoDecimal(String.format("%.2f", Double.valueOf(itemPlannedActivity.get("Quantity")))));
            if (!TextUtils.isEmpty(itemPlannedActivity.get("ActualQty")))
                holder.tvActual.setText(common.stringToTwoDecimal(String.format("%.2f", Double.valueOf(itemPlannedActivity.get("ActualQty")))));
            else
                holder.tvActual.setText(itemPlannedActivity.get("ActualQty"));

            if (!TextUtils.isEmpty(itemPlannedActivity.get("TotalValue")))
                holder.tvTotal.setText(common.stringToTwoDecimal(String.format("%.2f", Double.valueOf(itemPlannedActivity.get("TotalValue")))));
            else
                holder.tvTotal.setText(itemPlannedActivity.get("TotalValue"));

            holder.tvUom.setText(" " + itemPlannedActivity.get("UOM"));
            if (TextUtils.isEmpty(itemPlannedActivity.get("FileName"))) {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Black
                holder.btnViewAttach.setEnabled(false);
            } else {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Green
                holder.btnViewAttach.setEnabled(true);
            }
            holder.tvFileName.setText(itemPlannedActivity.get("FileName"));
            holder.tvRemarks.setText(itemPlannedActivity.get("Remarks"));
            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    try {

                        String actPath = itemPlannedActivity.get("FileName");
                        int pathLen = actPath.split("/").length;
                        String newPath = actPath.split("/")[pathLen - 4];

                        // common.showToast("New Actual Path="+newPath);
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!");
                        } else {
                            // Locate the image folder in your SD Card
                            File file1 = new File(actPath);
                            filePlanned = new File(file1.getParent());
                        }

                        if (filePlanned.isDirectory()) {

                            listFilePlanned = filePlanned.listFiles(new FilenameFilter() {
                                public boolean accept(File directory, String fileName) {
                                    return fileName.endsWith(".jpeg") || fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for FilePathStrings
                            FilePathStringsPlanned = new String[listFilePlanned.length];
                            // Create a String array for FileNameStrings
                            FileNameStringsPlanned = new String[listFilePlanned.length];

                            for (int i = 0; i < listFilePlanned.length; i++) {

                                // Get the path of the image file
                                if (!listFilePlanned[i].getName().toString().toLowerCase().equals(".nomedia")) {
                                    FilePathStringsPlanned[i] = listFilePlanned[i].getAbsolutePath();
                                    // Get the name image file
                                    FileNameStringsPlanned[i] = listFilePlanned[i].getName();

                                    Intent i1 = new Intent(FarmReportDetail.this, ViewImage.class);
                                    // Pass String arrays FilePathStrings
                                    i1.putExtra("filepath", FilePathStringsPlanned);
                                    // Pass String arrays FileNameStrings
                                    i1.putExtra("filename", FileNameStringsPlanned);
                                    // Pass click position
                                    i1.putExtra("position", 0);
                                    startActivity(i1);
                                }
                            }
                        }


                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(FarmReportDetail.this, "Image available on FarmArt portal. Report already synchronized.", false);

                    }
                }
            });


            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }
    }

    private class AdditionalListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listAdditionalActivity;
        String _type;
        private Context context2;

        public AdditionalListAdapter(Context context,
                                     ArrayList<HashMap<String, String>> listAdditionalActivity) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listAdditionalActivity = listAdditionalActivity;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listAdditionalActivity.size();
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
            final viewUnplannedHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_planned_recommended, null);
                holder = new viewUnplannedHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewUnplannedHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvName = (TextView) convertView
                    .findViewById(R.id.tvName);
            holder.btnViewAttach = (Button) convertView
                    .findViewById(R.id.btnViewAttach);
            holder.tvFileName = (TextView) convertView
                    .findViewById(R.id.tvFileName);
            holder.tvPlanned = (TextView) convertView
                    .findViewById(R.id.tvPlanned);
            holder.tvActual = (TextView) convertView
                    .findViewById(R.id.tvActual);
            holder.tvUom = (TextView) convertView
                    .findViewById(R.id.tvUom);
            final HashMap<String, String> itemPlannedActivity = _listAdditionalActivity.get(position);


            if (!TextUtils.isEmpty(itemPlannedActivity.get("SubActivityName")))
                holder.tvName.setText(itemPlannedActivity.get("ActivityName") + " - " + itemPlannedActivity.get("SubActivityName"));// + " - " + itemPlannedActivity.get("ActualQty") + " " + itemPlannedActivity.get("UOM"));
            else
                holder.tvName.setText(itemPlannedActivity.get("ActivityName"));//+ " - "  + itemPlannedActivity.get("ActualQty") + " " + itemPlannedActivity.get("UOM"));

            if (!TextUtils.isEmpty(itemPlannedActivity.get("ActualQty")))
                holder.tvActual.setText(common.stringToTwoDecimal(String.format("%.2f", Double.valueOf(itemPlannedActivity.get("ActualQty")))));
            else
                holder.tvActual.setText(itemPlannedActivity.get("ActualQty"));
            holder.tvUom.setText(" "+itemPlannedActivity.get("UOM"));
            if (TextUtils.isEmpty(itemPlannedActivity.get("FileName"))) {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Black
                holder.btnViewAttach.setEnabled(false);
            } else {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Green
                holder.btnViewAttach.setEnabled(true);
            }

            holder.tvFileName.setText(itemPlannedActivity.get("FileName"));

            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    try {

                        String actPath = itemPlannedActivity.get("FileName");
                        int pathLen = actPath.split("/").length;
                        String newPath = actPath.split("/")[pathLen - 4];

                        // common.showToast("New Actual Path="+newPath);
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!");
                        } else {
                            // Locate the image folder in your SD Card
                            File file1 = new File(actPath);
                            filePlanned = new File(file1.getParent());
                        }

                        if (filePlanned.isDirectory()) {

                            listFilePlanned = filePlanned.listFiles(new FilenameFilter() {
                                public boolean accept(File directory, String fileName) {
                                    return fileName.endsWith(".jpeg") || fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for FilePathStrings
                            FilePathStringsPlanned = new String[listFilePlanned.length];
                            // Create a String array for FileNameStrings
                            FileNameStringsPlanned = new String[listFilePlanned.length];

                            for (int i = 0; i < listFilePlanned.length; i++) {

                                // Get the path of the image file
                                if (!listFilePlanned[i].getName().toString().toLowerCase().equals(".nomedia")) {
                                    FilePathStringsPlanned[i] = listFilePlanned[i].getAbsolutePath();
                                    // Get the name image file
                                    FileNameStringsPlanned[i] = listFilePlanned[i].getName();

                                    Intent i1 = new Intent(FarmReportDetail.this, ViewImage.class);
                                    // Pass String arrays FilePathStrings
                                    i1.putExtra("filepath", FilePathStringsPlanned);
                                    // Pass String arrays FileNameStrings
                                    i1.putExtra("filename", FileNameStringsPlanned);
                                    // Pass click position
                                    i1.putExtra("position", 0);
                                    startActivity(i1);
                                }
                            }
                        }


                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(FarmReportDetail.this, "Image not available for view.", false);

                    }
                }
            });
            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
    //</editor-fold>
}
