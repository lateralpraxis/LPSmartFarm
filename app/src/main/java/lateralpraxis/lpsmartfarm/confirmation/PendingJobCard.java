package lateralpraxis.lpsmartfarm.confirmation;

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
import lateralpraxis.type.PendingJobCardData;

public class PendingJobCard extends Activity {
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*--------------Start of Code for variable declaration-----------*/

    private int lsize = 0;
    private ArrayList<HashMap<String, String>> PendingJobCardDetails;

    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView listSelectPlantation;
    private TextView tvEmpty;
    private View tvDivider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_jobcard_list);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        PendingJobCardDetails = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        listSelectPlantation = (ListView) findViewById(R.id.listSelectPlantation);
        /*------------Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
        dba.open();
        List<PendingJobCardData> lables = dba.getPendingJobCardList();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("FarmBlockCode", lables.get(i).getFarmBlockCode());
                hm.put("UniqueId", lables.get(i).getUniqueId());
                hm.put("FarmerName", lables.get(i).getFarmerName());
                hm.put("FatherName", String.valueOf(lables.get(i).getFatherName()));
                hm.put("Mobile", String.valueOf(lables.get(i).getMobile()));
                hm.put("Address", String.valueOf(lables.get(i).getAddress()));
                hm.put("Plantation", String.valueOf(lables.get(i).getPlantation()));
                hm.put("PlUniqueId", String.valueOf(lables.get(i).getPlUniqueId()));
                PendingJobCardDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(PendingJobCard.this, PendingJobCardDetails);
        if (lsize > 0)
            listSelectPlantation.setAdapter(Cadapter);

        listSelectPlantation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id.tvFarmerName)).getText().toString()))) {
                    Intent intent;
                    dba.openR();
                    boolean isExist = dba.isJobCardAlreayConfirmed(String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()), String.valueOf(((TextView) item.findViewById(R.id.tvPLUniqueId)).getText().toString()));
                    dba.close();
                    if (isExist)
                        intent = new Intent(PendingJobCard.this, JobCardConfirmationDetail.class);
                    else
                        intent = new Intent(PendingJobCard.this, JobCardConfirmationStep3.class);
                    intent.putExtra("farmerName", String.valueOf(((TextView) item.findViewById(R.id.tvFarmerName)).getText().toString()));
                    intent.putExtra("farmerMobile", String.valueOf(((TextView) item.findViewById(R.id.tvFarmerMobile)).getText().toString()));
                    intent.putExtra("farmblockCode", String.valueOf(((TextView) item.findViewById(R.id.tvFarmBlock)).getText().toString()));
                    intent.putExtra("farmblockUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvPLUniqueId)).getText().toString()));
                    intent.putExtra("plantation", String.valueOf(((TextView) item.findViewById(R.id.tvPlantation)).getText().toString()));
                    intent.putExtra("address", String.valueOf(((TextView) item.findViewById(R.id.tvAddress)).getText().toString()));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(PendingJobCard.this, ActivityHome.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
    }

    // When press back button go to home screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        TextView tvUniqueId, tvPLUniqueId, tvFarmerName, tvFarmerMobile, tvFarmBlock, tvPlantation, tvAddress;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listSelectPlantation) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            PendingJobCardDetails = listSelectPlantation;
        }

        @Override
        public int getCount() {
            return PendingJobCardDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return PendingJobCardDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_pendingjobcard, null);
                holder = new ViewHolder();
                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvPLUniqueId = (TextView) arg1.findViewById(R.id.tvPLUniqueId);
                holder.tvFarmerName = (TextView) arg1.findViewById(R.id.tvFarmerName);
                holder.tvFarmerMobile = (TextView) arg1.findViewById(R.id.tvFarmerMobile);
                holder.tvFarmBlock = (TextView) arg1.findViewById(R.id.tvFarmBlock);
                holder.tvPlantation = (TextView) arg1.findViewById(R.id.tvPlantation);
                holder.tvAddress = (TextView) arg1.findViewById(R.id.tvAddress);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }
            holder.tvUniqueId.setText(PendingJobCardDetails.get(arg0).get("UniqueId"));
            holder.tvPLUniqueId.setText(PendingJobCardDetails.get(arg0).get("PlUniqueId"));
            holder.tvFarmerName.setText(PendingJobCardDetails.get(arg0).get("FarmerName"));
            holder.tvFarmerMobile.setText(PendingJobCardDetails.get(arg0).get("Mobile"));
            holder.tvFarmBlock.setText(PendingJobCardDetails.get(arg0).get("FarmBlockCode"));
            holder.tvPlantation.setText(PendingJobCardDetails.get(arg0).get("Plantation"));
            holder.tvAddress.setText(PendingJobCardDetails.get(arg0).get("Address"));
            if (TextUtils.isEmpty(PendingJobCardDetails.get(arg0).get("FarmerName")))
                holder.tvFarmerName.setVisibility(View.GONE);
            else
                holder.tvFarmerName.setVisibility(View.VISIBLE);
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
