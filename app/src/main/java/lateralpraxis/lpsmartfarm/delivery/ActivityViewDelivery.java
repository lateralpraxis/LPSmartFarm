package lateralpraxis.lpsmartfarm.delivery;

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

public class ActivityViewDelivery extends Activity {

    private final Context mContext = this;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String userId;
    private String lang;

    private TextView linkAdd, tvEmpty;
    private ListView lvPendingDispatchForDelivery;
    private View tvDivider;

    private ArrayList<HashMap<String, String>> dispatchData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_delivery);

        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-

        //<editor-fold desc="Find controls by Id">
        tvEmpty = findViewById(R.id.tvEmpty);
        tvDivider = findViewById(R.id.tvDivider);
        lvPendingDispatchForDelivery = findViewById(R.id.lvPendingDispatchForDelivery);
        //</editor-fold>

        linkAdd = findViewById(R.id.linkAdd);

        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityViewDelivery.this, ActivityViewPendingDispatch.class);
                startActivity(intent);
                finish();
            }
        });

        if (common.isConnected()) {
            BindData();
        } else {
            Intent intent = new Intent(mContext, ActivityHome.class);
            startActivity(intent);
            finish();
        }
        lvPendingDispatchForDelivery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                Intent intent = new Intent(ActivityViewDelivery.this, ActivityAddDelivery.class);
                intent.putExtra("dispatchId", String.valueOf(((TextView) item.findViewById(R.id.tvDispatchId)).getText().toString()));
                intent.putExtra("driverName", String.valueOf(((TextView) item.findViewById(R.id.tvDriverName)).getText().toString()));
                intent.putExtra("driverMobileNo", String.valueOf(((TextView) item.findViewById(R.id.tvDriverMobileNo)).getText().toString()));
                intent.putExtra("dispatchForId", String.valueOf(((TextView) item.findViewById(R.id.tvDispatchForId)).getText().toString()));
                intent.putExtra("dispatchForName", String.valueOf(((TextView) item.findViewById(R.id.tvDispatchForName)).getText().toString()));
                intent.putExtra("dispatchForMobile", String.valueOf(((TextView) item.findViewById(R.id.tvDispatchForMobile)).getText().toString()));
                startActivity(intent);
                finish();
            }
        });
    }

    private void BindData() {
        // dispatchData.clear();
        dba.open();
        dispatchData = dba.getUnSyncDeliveryDetailsForDispatches();
        dba.close();
        if (dispatchData.size() != 0) {
            lvPendingDispatchForDelivery.setAdapter(new CustomAdapter(mContext, dispatchData));

            ViewGroup.LayoutParams params = lvPendingDispatchForDelivery.getLayoutParams();
            lvPendingDispatchForDelivery.setLayoutParams(params);
            lvPendingDispatchForDelivery.requestLayout();
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvPendingDispatchForDelivery.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {
        TextView tvDispatchId, tvDriverName, tvDriverMobileNo, tvDispatchForName,
                tvDispatchForId, tvDispatchForMobile, tvDispatchDetails, tvDispatchToDetails;
        int ref;
    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> _listData;
        private Context docContext;
        private LayoutInflater inflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listData) {
            this.docContext = context;
            inflater = LayoutInflater.from(docContext);
            _listData = listData;
        }

        @Override
        public int getCount() {
            return _listData.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_pending_dispatch, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvDispatchId = convertView.findViewById(R.id.tvDispatchId);
            holder.tvDriverName = convertView.findViewById(R.id.tvDriverName);
            holder.tvDriverMobileNo = convertView.findViewById(R.id.tvDriverMobileNo);
            holder.tvDispatchForName = convertView.findViewById(R.id.tvDispatchForName);
            holder.tvDispatchForId = convertView.findViewById(R.id.tvDispatchForId);
            holder.tvDispatchForMobile = convertView.findViewById(R.id.tvDispatchForMobile);
            holder.tvDispatchDetails = convertView.findViewById(R.id.tvDispatchDetails);
            holder.tvDispatchToDetails = convertView.findViewById(R.id.tvDispatchToDetails);

            final HashMap<String, String> itemData = _listData.get(position);
            holder.tvDispatchId.setText(itemData.get("Id"));
            holder.tvDriverName.setText(itemData.get("DriverName"));
            holder.tvDriverMobileNo.setText(itemData.get("DriverMobileNo"));
            holder.tvDispatchForId.setText(itemData.get("DispatchForId"));
            holder.tvDispatchForName.setText(itemData.get("DispatchForName"));
            holder.tvDispatchForMobile.setText(itemData.get("DispatchForMobile"));
            holder.tvDispatchDetails.setText(itemData.get("VehicleNo") + " - " + itemData.get("Code"));
            holder.tvDispatchToDetails.setText(itemData.get("DispatchForName") + " - " + itemData.get("TotalDispatch"));

            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }
    }

    //<editor-fold desc="Functions related to show home menu and trap back button press">

    /**
     * Method to load previous Activity if back is pressed
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityViewDelivery.this, ActivityHome.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
    }

    /**
     * When press back button go to home screen
     *
     * @param item
     * @return
     */
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

    /**
     * To create menu on inflater
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * Method to check android version ad load action bar appropriately
     */
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
}
