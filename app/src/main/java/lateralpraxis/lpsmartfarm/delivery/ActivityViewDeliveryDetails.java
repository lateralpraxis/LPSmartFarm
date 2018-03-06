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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityViewDeliveryDetails extends Activity {

    private final Context mContext = this;

    private ListView lvDispatchItems;
    private ArrayList<HashMap<String, String>> dataList = null;
    private String userId, lang;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String dispatchId, driverName, driverMobileNo, dispatchForId, dispatchForName, dispatchForMobile;
    private TextView tvDriverName, tvDriverMobileNo, tvDispatchForName, tvTotalDelivery, tvShortClose,
            tvBookedQty, tvAmount, tvBalance, tvPaymentMode, tvPaymentAmount, tvPaymentRemarks;

    private Integer totalItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_delivery_details);

        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        HashMap<String, String> user = session.getLoginUserDetails();
        dataList = new ArrayList<>();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        //</editor-fold>

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Find controls by Id">
        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverMobileNo = findViewById(R.id.tvDriverMobileNo);
        tvDispatchForName = findViewById(R.id.tvDispatchForName);
        tvTotalDelivery = findViewById(R.id.tvTotalDelivery);
        tvShortClose = findViewById(R.id.tvShortClose);
        tvBookedQty = findViewById(R.id.tvBookedQty);
        tvAmount = findViewById(R.id.tvAmount);
        tvBalance = findViewById(R.id.tvBalance);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        tvPaymentRemarks = findViewById(R.id.tvPaymentRemarks);
        lvDispatchItems = findViewById(R.id.lvDispatchItems);
        //</editor-fold>

        //<editor-fold desc="Get Extra values from Intent call">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            dispatchId = extras.getString("dispatchId");
            driverName = extras.getString("driverName");
            driverMobileNo = extras.getString("driverMobileNo");
            dispatchForId = extras.getString("dispatchForId");
            dispatchForName = extras.getString("dispatchForName");
            dispatchForMobile = extras.getString("dispatchForMobile");
        }
        //</editor-fold>

        if (common.isConnected()) {
            BindData();
        }

    }

    private void BindData() {
        //<editor-fold desc="Assign values to layout controls">
        tvDriverName.setText(driverName);
        tvDriverMobileNo.setText(driverMobileNo);
        tvDispatchForName.setText(dispatchForName);
        //</editor-fold>

        dataList.clear();
        dba.open();
        dataList = dba.getDispatchItemsDeliveryDetails(dispatchId);
        dba.close();
        if (dataList.size() != 0) {
            lvDispatchItems.setAdapter(new CustomAdapter(mContext, dataList));
            ViewGroup.LayoutParams params = lvDispatchItems.getLayoutParams();
            lvDispatchItems.setLayoutParams(params);
            lvDispatchItems.requestLayout();

            for (int i = 0; i < dataList.size(); i++) {
                totalItems += Integer.valueOf(dataList.get(i).get("Quantity").toString());
            }
            tvTotalDelivery.setText(totalItems.toString());

            dba.open();
            String reason = dba.getShortCloseReason(dispatchId);
            dba.close();

            tvShortClose.setText(reason);
        }

        /* Booking details */
        dataList.clear();
        dba.open();
        dataList = dba.getPendingDispatchItemsForDelivery(dispatchId);
        dba.close();
        if (dataList.size() != 0) {
            int totalQuantity = 0, totalAmount = 0;
            for (int i = 0; i < dataList.size(); i++) {
                totalQuantity += Integer.valueOf(dataList.get(i).get("Quantity"));
                totalAmount += (Double.valueOf(dataList.get(i).get("Rate")) * Integer.valueOf(dataList.get(i).get("DeliveryQuantity")));
            }
            dba.open();
            int balance = dba.getBalanceForFarmerNursery(dispatchForId);
            dba.close();
            tvBookedQty.setText(String.valueOf(totalQuantity));
            tvAmount.setText(Double.valueOf(totalAmount).toString());
            tvBalance.setText(Double.valueOf(balance).toString());
        }

        /* Payment details */
        dataList.clear();
        dba.open();
        dataList = dba.getPaymentDetailsAgainstDispatchDelivery(dispatchId);
        dba.close();
        if (dataList.size() != 0) {
            tvPaymentMode.setText(dataList.get(0).get("PaymentModeTitle"));
            tvPaymentAmount.setText(dataList.get(0).get("PaymentAmount"));
            tvPaymentRemarks.setText(dataList.get(0).get("PaymentRemarks"));
        }
    }

    public static class ViewHolder {
        TextView tvDispatchItem, tvDeliveryQty;
        EditText etDeliveryQty;
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
            holder.tvDeliveryQty = convertView.findViewById(R.id.tvDeliveryQty);
            holder.tvDeliveryQty.setVisibility(View.VISIBLE);
            holder.etDeliveryQty = convertView.findViewById(R.id.etDeliveryQty);
            holder.etDeliveryQty.setVisibility(View.GONE);

            final HashMap<String, String> itemData = _listData.get(position);
            holder.tvDispatchItem.setText(itemData.get("PolybagTitle"));
            holder.tvDeliveryQty.setText(itemData.get("Quantity"));

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
        Intent i = new Intent(ActivityViewDeliveryDetails.this, ActivityHome.class);
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
