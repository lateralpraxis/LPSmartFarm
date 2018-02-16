package lateralpraxis.lpsmartfarm.delivery;

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
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvDispatchId, tvDriverName, tvDriverMobileNo, tvDispatchForName, tvDispatchForMobile, tvTotalDelivery, tvTotalDispatch;
    private Spinner spShortClose;
    private ListView lvDispatchItems;
    private ArrayList<HashMap<String, String>> dispatchData = null;
    private String dispatchId, driverName, driverMobileNo, dispatchForName, dispatchForMobile;
    private Button btnBack, btnSave;

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
        tvDispatchForName = findViewById(R.id.tvDispatchForName);
        tvDispatchForMobile = findViewById(R.id.tvDispatchForMobile);
        lvDispatchItems = findViewById(R.id.lvDispatchItems);
        tvTotalDelivery = findViewById(R.id.tvTotalDelivery);
        tvTotalDispatch = findViewById(R.id.tvTotalDispatch);
        spShortClose = findViewById(R.id.spShortClose);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
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
            dispatchForName = extras.getString("dispatchForName");
            dispatchForMobile = extras.getString("dispatchForMobile");
        }
        //</editor-fold>

        //<editor-fold desc="Assign values to layout controls">
        tvDispatchId.setText(dispatchId);
        tvDriverName.setText(driverName);
        tvDriverMobileNo.setText(driverMobileNo);
        tvDispatchForName.setText(dispatchForName);
        tvDispatchForMobile.setText(dispatchForMobile);
        //</editor-fold>

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAddDelivery.this, ActivityViewPendingDispatch.class);
                startActivity(intent);
                finish();
            }
        });

        /*Save button click Listener*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Check delivery quantity against dispatch quantity */
                if (((getValue(tvTotalDelivery.getText().toString())) < getValue(tvTotalDispatch.getText().toString()))
                        && (spShortClose.getSelectedItemPosition() == 0)) {
                    common.showToast("ShortClose Reason is mandatory", 5, 1);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                            (ActivityAddDelivery.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder
                            .setMessage("Are you sure, you want to save Delivery?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i = 0; i < lvDispatchItems.getCount(); i++) {
                                        LinearLayout layout1 = (LinearLayout) lvDispatchItems.getChildAt(i);
                                        TextView tvDispatchId = (TextView) layout1.getChildAt(0);
                                        TextView tvBookingId = (TextView) layout1.getChildAt(1);
                                        LinearLayout layout2 = (LinearLayout) layout1.getChildAt(2);
                                        TextView tvDispatchItemId = (TextView) layout2.getChildAt(1);
                                        EditText etDeliveryQty = (EditText) layout2.getChildAt(3);
                                        dba.open();
                                        dba.insertDeliveryDetailsForDispatch(
                                                tvDispatchId.getText().toString(),
                                                tvBookingId.getText().toString(),
                                                tvDispatchItemId.getText().toString(),
                                                etDeliveryQty.getText().toString());
                                        dba.close();

                                        Intent intent = new Intent(ActivityAddDelivery.this,
                                                ActivityAddPayment.class);
                                        intent.putExtra("dispatchId", tvDispatchId.getText().toString());
                                        intent.putExtra("driverName", driverName);
                                        intent.putExtra("driverMobileNo", driverMobileNo);
                                        intent.putExtra("dispatchForName", dispatchForName);
                                        intent.putExtra("dispatchForMobile", dispatchForMobile);
                                        startActivity(intent);
                                        finish();
                                    }
                                    if (!String.valueOf(((CustomType) spShortClose.getSelectedItem()).getId()).trim().isEmpty()) {
                                        dba.open();
                                        dba.updatePendingDispatchForDeliveryShortCloseReason(
                                                tvDispatchId.getText().toString(),
                                                String.valueOf(((CustomType) spShortClose.getSelectedItem()).getId()));
                                        dba.close();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                /*Just close the dialog without doing anything*/
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

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

    /* <editor-fold desc="Back button pressed"> */
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
    //</editor-fold>

    private Integer getValue(String val) {
        return Integer.valueOf(val.trim().isEmpty() ? "0" : val);
    }

    public static class ViewHolder {
        TextView tvDispatchId, tvBookingId, tvDispatchItem, tvDispatchItemId, tvDispatchItemQty;
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

            holder.tvDispatchId = convertView.findViewById(R.id.tvDispatchId);
            holder.tvBookingId = convertView.findViewById(R.id.tvBookingId);
            holder.tvDispatchItem = convertView.findViewById(R.id.tvDispatchItem);
            holder.tvDispatchItemQty = convertView.findViewById(R.id.tvDispatchItemQty);
            holder.tvDispatchItemId = convertView.findViewById(R.id.tvDispatchItemId);
            holder.etDeliveryQty = convertView.findViewById(R.id.etDeliveryQty);

            final HashMap<String, String> itemData = _listData.get(position);
            holder.tvDispatchId.setText(itemData.get("DispatchId"));
            holder.tvBookingId.setText(itemData.get("BookingId"));
            holder.tvDispatchItem.setText(itemData.get("PolybagTitle"));
            holder.tvDispatchItemId.setText(itemData.get("PolybagId"));
            holder.tvDispatchItemQty.setText(itemData.get("Quantity"));

            tvTotalDelivery.setText("");

            holder.etDeliveryQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Integer totalDelivery = 0;
                    Integer totalDispatch = 0;
                    if (!hasFocus) {
                        for (int i = 0; i < lvDispatchItems.getCount(); i++) {
                            LinearLayout layout1 = (LinearLayout) lvDispatchItems.getChildAt(i);
                            /*TextView tvDispatchId = (TextView) layout1.getChildAt(0);
                            TextView tvBookingId = (TextView) layout1.getChildAt(1);*/
                            LinearLayout layout2 = (LinearLayout) layout1.getChildAt(2);
                            /*TextView tvDispatchItemId = (TextView) layout2.getChildAt(1);*/
                            TextView tvDispatchItemQty = (TextView) layout2.getChildAt(2);
                            EditText etDeliveryQty = (EditText) layout2.getChildAt(3);
                            etDeliveryQty.setHint("Maximum: " + tvDispatchItemQty.getText());
                            if (getValue(etDeliveryQty.getEditableText().toString()) > getValue(tvDispatchItemQty.getText().toString())) {
                                common.showToast("Cannot be greater than " + tvDispatchItemQty.getText(), Toast.LENGTH_LONG, 0);
                                etDeliveryQty.setText("");
                            }
                            totalDelivery = totalDelivery + getValue(etDeliveryQty.getEditableText().toString());
                            totalDispatch = totalDispatch + getValue(tvDispatchItemQty.getText().toString());

                           /* dba.open();
                            dba.insertDeliveryDetailsForDispatch(tvDispatchId.getText().toString(), tvBookingId.getText().toString(), tvDispatchItemId.getText().toString(), etDeliveryQty.getText().toString());
                            dba.close();*/
                        }
                        tvTotalDispatch.setText(totalDispatch.toString());
                        tvTotalDelivery.setText(totalDelivery.toString());
                    }
                }
            });

            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
}
