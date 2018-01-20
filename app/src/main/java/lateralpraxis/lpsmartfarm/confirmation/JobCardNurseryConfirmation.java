package lateralpraxis.lpsmartfarm.confirmation;

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
import android.support.v7.widget.SwitchCompat;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.ImageLoadingUtils;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.JobCardConfirmationData;

public class JobCardNurseryConfirmation extends Activity {
    final Context context = this;
    private final Context mContext = this;
    CustomAdapter adapter;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    File destination, filePlanned;
    private ImageLoadingUtils utils;
    private int lsize = 0;
    private int cnt = 0;
    private String cntActType = "";
    /*------------------Code for Variable Declaration---------------*/
    private String nurType, nurName, nurZone, uniqueId, plantationUniqueId, plantation, actualVsPlanned, address;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvNurType, tvNurName, tvNurZone, tvPlantation, tvEmpty;
    private ListView lvReportedActivity;
    private ArrayList<HashMap<String, String>> list;
    private Button btnBack, btnSave;
    private View tvDivider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobcard_nursery_confirmation);
         /*------------------------Start of code for setting action
         bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/
         /*------------------------Start of code for creating instance of
         class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());

         /*------------------------Code for finding controls-----------------------*/
        tvNurType = (TextView) findViewById(R.id.tvNurType);
        tvNurName = (TextView) findViewById(R.id.tvNurName);
        tvNurZone = (TextView) findViewById(R.id.tvNurZone);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        // tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        lvReportedActivity = (ListView) findViewById(R.id.lvReportedActivity);
        list = new ArrayList<HashMap<String, String>>();

          /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurType = extras.getString("nurType");
            nurName = extras.getString("nurName");
            nurZone = extras.getString("nurZone");
            plantation = extras.getString("plantation");
            address = extras.getString("address");
            uniqueId = extras.getString("uniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            tvNurType.setText(nurType);
            tvNurName.setText(nurName);
            tvNurZone.setText(nurZone);
            tvPlantation.setText(plantation);
            // tvAddress.setText(address);
            tvPlantation.setText(plantation);
        }
        BindData(plantationUniqueId);
         /*---------------Start of code to set Click Event for Button Back &
         Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(JobCardNurseryConfirmation.this,
                        PendingNurseryJobCard.class);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurName);
                intent.putExtra("nurZone", nurZone);
                intent.putExtra("plantation", plantation);
                intent.putExtra("address", address);
                intent.putExtra("uniqueId", uniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                startActivity(intent);
                finish();
            }
        });

        //To validate and Save into temp table
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to Accept / Reject reported activities?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                lvReportedActivity.clearFocus();
                                if (list.size() > 0) {
                                    for (int i = 0; i < lvReportedActivity.getCount(); i++) {
                                        LinearLayout layout1 = (LinearLayout) lvReportedActivity
                                                .getChildAt(i);
                                        TextView tvJobCardDetailId = (TextView) layout1.getChildAt(0);
                                        TextView tvFBNurType = (TextView) layout1.getChildAt(2);
                                        TextView tvFBNurId = (TextView) layout1.getChildAt(1);
                                        TextView tvVDate = (TextView) layout1.getChildAt(3);
                                        LinearLayout layout = (LinearLayout) lvReportedActivity
                                                .getChildAt(i);
                                        TableLayout tableLayoutTotal = (TableLayout) layout.getChildAt(4);
                                        TableRow tableRow = (TableRow) tableLayoutTotal.getChildAt(0);
                                        TableRow tableRow1 = (TableRow) tableLayoutTotal.getChildAt(1);
                                        SwitchCompat actValue = (SwitchCompat) tableRow1.getChildAt(4);
                                        dba.open();
                                        dba.updateJobCardPending(uniqueId, plantationUniqueId, tvJobCardDetailId.getText().toString(), String.valueOf(actValue.isChecked()));
                                        dba.close();
                                    }
                                    common.showToast("Details saved successfully.", 5, 3);
                                    Intent intent = new Intent(JobCardNurseryConfirmation.this, PendingNurseryJobCard.class);
                                    intent.putExtra("nurType", nurType);
                                    intent.putExtra("nurName", nurName);
                                    intent.putExtra("nurZone", nurZone);
                                    intent.putExtra("plantation", plantation);
                                    intent.putExtra("address", address);
                                    intent.putExtra("uniqueId", uniqueId);
                                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });
    }

    /*---------Code to bind list of Observation Details---------------------------------*/
    private void BindData(String plantationUniqueId) {
        list.clear();
        dba.open();
        List<JobCardConfirmationData> lables = dba.getPendingJobCard(plantationUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            lvReportedActivity.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("JobCardDetailId", lables.get(i).getJobCardDetailId());
                hm.put("VisitDate", lables.get(i).getVisitDate());
                hm.put("FarmActivity", String.valueOf(lables.get(i).getFarmActivity()));
                hm.put("FbNurType", String.valueOf(lables.get(i).getFbNurType()));
                hm.put("FbNurId", String.valueOf(lables.get(i).getFbNurId()));
                hm.put("ActivityValue", String.valueOf(lables.get(i).getActivityValue()));
                if(cntActType.equalsIgnoreCase(lables.get(i).getVisitDate() + "~" +String.valueOf(lables.get(i).getActivityType())))
                    hm.put("ActivityTypeHeader", "");
                else
                    hm.put("ActivityTypeHeader", String.valueOf(lables.get(i).getActivityType()));
                hm.put("ActivityType", String.valueOf(lables.get(i).getActivityType()));
                hm.put("PlannedValue", String.valueOf(lables.get(i).getPlannedValue()));
                hm.put("Uom", String.valueOf(lables.get(i).getUom()));
                hm.put("Remarks", String.valueOf(lables.get(i).getRemarks()));
                cntActType = lables.get(i).getVisitDate() + "~" +String.valueOf(lables.get(i).getActivityType());
                list.add(hm);
            }
            adapter = new CustomAdapter(JobCardNurseryConfirmation.this, list);
            lvReportedActivity.setAdapter(adapter);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
            lvReportedActivity.setVisibility(View.GONE);
        }
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(JobCardNurseryConfirmation.this,
                        PendingNurseryJobCard.class);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurName);
                intent.putExtra("nurZone", nurZone);
                intent.putExtra("plantation", plantation);
                intent.putExtra("address", address);
                intent.putExtra("uniqueId", uniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:

                Intent intent1 = new Intent(JobCardNurseryConfirmation.this,
                        ActivityHome.class);
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
        Intent intent = new Intent(JobCardNurseryConfirmation.this,
                PendingNurseryJobCard.class);
        intent.putExtra("nurType", nurType);
        intent.putExtra("nurName", nurName);
        intent.putExtra("nurZone", nurZone);
        intent.putExtra("plantation", plantation);
        intent.putExtra("address", address);
        intent.putExtra("uniqueId", uniqueId);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
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
        TextView tvJobCardDetailId, tvVisitDate, tvFarmActivity, tvFBNurType, tvFBNurId, tvVDate, tvPlanned, tvActual, tvRemarks, tvUom, tvActivityHeader;
        SwitchCompat toggleBtn;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvReportedActivity) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            list = lvReportedActivity;
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
            cnt = cnt + 1;
            final ViewHolder holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_reported_activity, null);
                holder = new ViewHolder();
                holder.tvJobCardDetailId = (TextView) arg1.findViewById(R.id.tvJobCardDetailId);
                holder.tvVisitDate = (TextView) arg1.findViewById(R.id.tvVisitDate);
                holder.tvFarmActivity = (TextView) arg1.findViewById(R.id.tvFarmActivity);
                holder.tvFBNurId = (TextView) arg1.findViewById(R.id.tvFBNurId);
                holder.tvFBNurType = (TextView) arg1.findViewById(R.id.tvFBNurType);
                holder.tvVDate = (TextView) arg1.findViewById(R.id.tvVDate);
                holder.toggleBtn = (SwitchCompat) arg1.findViewById(R.id.toggleBtn);
                holder.tvPlanned = (TextView) arg1.findViewById(R.id.tvPlanned);
                holder.tvActual = (TextView) arg1.findViewById(R.id.tvActual);
                holder.tvRemarks = (TextView) arg1.findViewById(R.id.tvRemarks);
                holder.tvUom = (TextView) arg1.findViewById(R.id.tvUom);
                holder.tvActivityHeader = (TextView) arg1.findViewById(R.id.tvActivityHeader);

                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            if (arg0 == 0) {
                cnt = 1;
                holder.tvVisitDate.setVisibility(View.VISIBLE);
                holder.tvVisitDate.setText(list.get(arg0).get("VisitDate"));
            } else {
                if (list.get(arg0 - 1).get("VisitDate").equals(list.get(arg0).get("VisitDate"))) {
                    holder.tvVisitDate.setVisibility(View.GONE);
                } else {
                    cnt = 1;
                    holder.tvVisitDate.setVisibility(View.VISIBLE);
                    holder.tvVisitDate.setText(list.get(arg0).get("VisitDate"));
                }
            }
            holder.tvActivityHeader.setText(list.get(arg0).get("ActivityTypeHeader"));
            holder.tvJobCardDetailId.setText(list.get(arg0).get("JobCardDetailId"));
            holder.tvVisitDate.setText(common.convertDateFormat(list.get(arg0).get("VisitDate")).replace(" 00:00:00", ""));
            holder.tvFarmActivity.setText(list.get(arg0).get("FarmActivity"));
            holder.tvFBNurId.setText(list.get(arg0).get("FbNurId"));
            holder.tvFBNurType.setText(list.get(arg0).get("FbNurType"));
            holder.tvVDate.setText(list.get(arg0).get("VisitDate"));
            holder.tvUom.setText(" " + list.get(arg0).get("Uom"));
            holder.tvRemarks.setText(list.get(arg0).get("Remarks"));
            if (list.get(arg0).get("ActivityType").equalsIgnoreCase("Planned")) {
                holder.tvPlanned.setText(String.format("%.2f", Double.valueOf(list.get(arg0).get("PlannedValue"))));
                holder.tvActual.setText(String.format("%.2f", Double.valueOf(list.get(arg0).get("ActivityValue"))));
            } else {
                holder.tvPlanned.setText("");
                holder.tvActual.setText(String.format("%.2f", Double.valueOf(list.get(arg0).get("ActivityValue"))));
            }
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
