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
import lateralpraxis.type.PendingNurseryJobCardData;

public class PendingNurseryJobCard extends Activity {
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*--------------Start of Code for variable declaration-----------*/

    private int lsize = 0;
    private ArrayList<HashMap<String, String>> PendingNurseryJobCardDetails;

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
        PendingNurseryJobCardDetails = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        listSelectPlantation = (ListView) findViewById(R.id.listSelectPlantation);
        /*------------Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
        dba.open();
        List<PendingNurseryJobCardData> lables = dba.getPendingNurseryJobCardList();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("NurseryType", lables.get(i).getNurseryType());
                hm.put("NurseryName", lables.get(i).getNurseryName());
                hm.put("UniqueId", lables.get(i).getUniqueId());
                hm.put("NurseryZone", String.valueOf(lables.get(i).getNurseryZone()));
                hm.put("ContactPerson", String.valueOf(lables.get(i).getContactPerson()));
                hm.put("Mobile", String.valueOf(lables.get(i).getMobile()));
                hm.put("Address", String.valueOf(lables.get(i).getAddress()));
                hm.put("Plantation", String.valueOf(lables.get(i).getPlantation()));
                hm.put("PlUniqueId", String.valueOf(lables.get(i).getPlUniqueId()));
                PendingNurseryJobCardDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(PendingNurseryJobCard.this, PendingNurseryJobCardDetails);
        if (lsize > 0)
            listSelectPlantation.setAdapter(Cadapter);

        listSelectPlantation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id.tvNurName)).getText().toString()))) {
                    Intent intent;
                    dba.openR();
                    boolean isExist = dba.isJobCardAlreayConfirmed(String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()), String.valueOf(((TextView) item.findViewById(R.id.tvPLUniqueId)).getText().toString()));
                    dba.close();
                    if (isExist)
                        intent = new Intent(PendingNurseryJobCard.this, JobCardNurseryConfirmationDetail.class);
                    else
                        intent = new Intent(PendingNurseryJobCard.this, JobCardNurseryConfirmation.class);
                    intent.putExtra("nurType", String.valueOf(((TextView) item.findViewById(R.id.tvNurType)).getText().toString()));
                    intent.putExtra("nurName", String.valueOf(((TextView) item.findViewById(R.id.tvNurName)).getText().toString()));
                    intent.putExtra("nurZone", String.valueOf(((TextView) item.findViewById(R.id.tvNurZone)).getText().toString()));
                    intent.putExtra("plantation", String.valueOf(((TextView) item.findViewById(R.id.tvPlantation)).getText().toString()));
                    intent.putExtra("address", String.valueOf(((TextView) item.findViewById(R.id.tvAddress)).getText().toString()));
                    intent.putExtra("uniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvPLUniqueId)).getText().toString()));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(PendingNurseryJobCard.this, ActivityHome.class);
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
        TextView tvUniqueId, tvPLUniqueId, tvNurType, tvNurName, tvNurZone, tvPlantation, tvAddress;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listSelectPlantation) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            PendingNurseryJobCardDetails = listSelectPlantation;
        }

        @Override
        public int getCount() {
            return PendingNurseryJobCardDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return PendingNurseryJobCardDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_pendingnurseryjobcard, null);
                holder = new ViewHolder();
                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvPLUniqueId = (TextView) arg1.findViewById(R.id.tvPLUniqueId);
                holder.tvNurType = (TextView) arg1.findViewById(R.id.tvNurType);
                holder.tvNurName = (TextView) arg1.findViewById(R.id.tvNurName);
                holder.tvNurZone = (TextView) arg1.findViewById(R.id.tvNurZone);
                holder.tvPlantation = (TextView) arg1.findViewById(R.id.tvPlantation);
                holder.tvAddress = (TextView) arg1.findViewById(R.id.tvAddress);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }
            holder.tvUniqueId.setText(PendingNurseryJobCardDetails.get(arg0).get("UniqueId"));
            holder.tvPLUniqueId.setText(PendingNurseryJobCardDetails.get(arg0).get("PlUniqueId"));
            holder.tvNurType.setText(PendingNurseryJobCardDetails.get(arg0).get("NurseryType"));
            holder.tvNurName.setText(PendingNurseryJobCardDetails.get(arg0).get("NurseryName"));
            holder.tvNurZone.setText(PendingNurseryJobCardDetails.get(arg0).get("NurseryZone"));
            holder.tvPlantation.setText(PendingNurseryJobCardDetails.get(arg0).get("Plantation"));
            holder.tvAddress.setText(PendingNurseryJobCardDetails.get(arg0).get("Address"));
            if (TextUtils.isEmpty(PendingNurseryJobCardDetails.get(arg0).get("NurseryName")))
                holder.tvNurName.setVisibility(View.GONE);
            else
                holder.tvNurName.setVisibility(View.VISIBLE);
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
