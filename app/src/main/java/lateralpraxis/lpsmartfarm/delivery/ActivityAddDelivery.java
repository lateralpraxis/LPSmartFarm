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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityAddDelivery extends Activity {

    private final Context mContext = this;
    DatabaseAdapter dba;
    UserSessionManager session;
    Common common;
    String userId, lang;
    private TextView tvDispatchId, tvDriverName, tvDriverMobileNo, tvDispatchFor;
    private Spinner spShortClose;
    private ListView lvDispatchItems;
    private ArrayList<HashMap<String, String>> dispatchData = null;
    private String dispatchId, driverName, driverMobileNo, dispatchFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery);

        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        HashMap<String, String> user = session.getLoginUserDetails();
        dispatchData = new ArrayList<>();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Find Controls on the layout">
        tvDispatchId = findViewById(R.id.tvDispatchId);
        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverMobileNo = findViewById(R.id.tvDriverMobileNo);
        tvDispatchFor = findViewById(R.id.tvDispatchFor);
        lvDispatchItems = findViewById(R.id.lvDispatchItems);
        spShortClose = findViewById(R.id.spShortClose);
        //</editor-fold>


        //<editor-fold desc="Bind ShortClose data to Spinner">
        spShortClose.setAdapter(DataAdapter("shortclosereason", ""));
        //</editor-fold>


        //<editor-fold desc="Get Extra values from Intent call">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            dispatchId = extras.getString("dispatchId");
            driverName = extras.getString("driverName");
            driverMobileNo = extras.getString("driverMobileNo");
            dispatchFor = extras.getString("dispatchFor");
        }
        //</editor-fold>

        //<editor-fold desc="Assign values to layout controls">
        tvDispatchId.setText(dispatchId);
        tvDriverName.setText(driverName);
        tvDriverMobileNo.setText(driverMobileNo);
        tvDispatchFor.setText(dispatchFor);
        //</editor-fold>

        if (common.isConnected()) {
            BindData();
        } else {
            Intent intent = new Intent(mContext, ActivityHome.class);
            startActivity(intent);
            finish();
        }
    }

    /*Method to fetch data and bind to spinner*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> labels = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, labels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    private void BindData() {
        dispatchData.clear();
        dba.open();
        dispatchData = dba.getPendingDispatchItemsForDelivery(dispatchId);
        dba.close();
        if (dispatchData.size() != 0) {
            lvDispatchItems.setAdapter(new CustomAdapter(mContext, dispatchData));
            ViewGroup.LayoutParams params = lvDispatchItems.getLayoutParams();
            lvDispatchItems.setLayoutParams(params);
            lvDispatchItems.requestLayout();
        }

    }

    /**
     * Method to load previous Activity if back is pressed
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityAddDelivery.this, ActivityViewPendingDispatch.class);
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

    //<editor-fold desc="Functions related to show home menu and trap back button press">

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

    public static class ViewHolder {
        TextView tvDispatchItem, tvDispatchItemQty;
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
                convertView = inflater.inflate(R.layout.list_pending_dispatch_items, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvDispatchItem = convertView.findViewById(R.id.tvDispatchItem);
            holder.tvDispatchItemQty = convertView.findViewById(R.id.tvDispatchItemQty);

            final HashMap<String, String> itemData = _listData.get(position);
            holder.tvDispatchItem.setText(itemData.get("PolybagTitle"));
            /*holder.tvDispatchItemQty.setText(itemData.get("DriverName"));*/

            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
    //</editor-fold>
}
