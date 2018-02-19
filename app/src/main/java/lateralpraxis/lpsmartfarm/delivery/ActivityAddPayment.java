package lateralpraxis.lpsmartfarm.delivery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityAddPayment extends Activity {

    private final Context mContext = this;

    private TextView tvDispatchForName, tvDispatchForMobile, tvBookedQty, tvAmount, tvAdvance, tvBalance;
    private Spinner spPaymentMode;
    private TextView tvPaymentAmount, tvPaymentRemarks;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String userId;
    private String lang;

    private String dispatchId, driverName, driverMobileNo, dispatchForName, dispatchForMobile, totalDispatch, totalAmount;

    private Button btnBack, btnSave;
    private EditText etPaymentAmount, etPaymentRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);


        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //interCroppingDetail = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Find controls by Id">
        tvDispatchForName = findViewById(R.id.tvDispatchForName);
        tvDispatchForMobile = findViewById(R.id.tvDispatchForMobile);
        tvBookedQty = findViewById(R.id.tvBookedQty);
        tvAmount = findViewById(R.id.tvAmount);
        tvAdvance = findViewById(R.id.tvAdvance);
        tvBalance = findViewById(R.id.tvBalance);

        spPaymentMode = findViewById(R.id.spPaymentMode);
        etPaymentAmount = findViewById(R.id.etPaymentAmount);
        etPaymentRemarks = findViewById(R.id.etPaymentRemarks);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        //</editor-fold>

        //<editor-fold desc="Get Extra values from Intent call">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            dispatchId = extras.getString("dispatchId");
            driverName = extras.getString("driverName");
            driverMobileNo = extras.getString("driverMobileNo");
            dispatchForName = extras.getString("dispatchForName");
            dispatchForMobile = extras.getString("dispatchForMobile");
            totalDispatch = extras.getString("totalDispatch");
            totalAmount = extras.getString("totalAmount");
        }
        //</editor-fold>


        //<editor-fold desc="Add data to controls">
        tvDispatchForName.setText(dispatchForName);
        tvDispatchForMobile.setText(dispatchForMobile);
        tvBookedQty.setText(totalDispatch);
        tvAmount.setText(totalAmount);
       /* double balance = (getValue(totalAmount) - getValue(tvAdvance.getText().toString()));
        tvBalance.setText(String.valueOf(balance));*/
        spPaymentMode.setAdapter(DataAdapter("paymentmode", ""));
        //</editor-fold>

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAddPayment.this, ActivityViewPendingDispatch.class);
                startActivity(intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Check delivery quantity against dispatch quantity */

                if ((spPaymentMode.getSelectedItemPosition()) == 0) {
                    common.showToast("Payment Mode is Mandatory", 5, 1);
                } else if (String.valueOf(etPaymentAmount.getText()).trim().equals("")) {
                    common.showToast("Payment Amount is Mandatory.");
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                            (ActivityAddPayment.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder
                            .setMessage("Are you sure, you want save Payment details?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dba.open();
                                    dba.insertPaymentDetailsPendingDispatchDelivery(dispatchId, dispatchId, tvAmount.getText().toString(),
                                            tvBalance.getText().toString(), String.valueOf(((CustomType) spPaymentMode.getSelectedItem()).getId()),
                                            etPaymentAmount.getText().toString().trim(), etPaymentRemarks.getText().toString().trim());
                                    dba.close();
                                    /*for (int i = 0; i < lvDispatchItems.getCount(); i++) {
                                        LinearLayout layout1 = (LinearLayout) lvDispatchItems.getChildAt(i);
                                        TextView tvDispatchId = (TextView) layout1.getChildAt(0);
                                        TextView tvBookingId = (TextView) layout1.getChildAt(1);
                                        LinearLayout layout2 = (LinearLayout) layout1.getChildAt(2);
                                        TextView tvDispatchItemId = (TextView) layout2.getChildAt(1);
                                        EditText etDeliveryQty = (EditText) layout2.getChildAt(5);
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
                                        intent.putExtra("totalDispatch", tvTotalDispatch.getText().toString());
                                        intent.putExtra("totalAmount", tvTotalAmount.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                    if (!String.valueOf(((CustomType) spShortClose.getSelectedItem()).getId()).trim().isEmpty()) {
                                        dba.open();
                                        dba.updatePendingDispatchForDeliveryShortCloseReason(
                                                tvDispatchId.getText().toString(),
                                                String.valueOf(((CustomType) spShortClose.getSelectedItem()).getId()));
                                        dba.close();
                                    }*/
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

    /*private Double getValue(String val) {
        return Double.valueOf(val.trim().isEmpty() ? "0" : val);
    }*/

    /**
     * Funtion to fetch and bind data to the controls
     *
     * @param dispatchUniqueId DispatchUniqueId
     */
    private void BindData(String dispatchUniqueId) {
        dba.open();
        // TODO: get the dispatch data
        dba.close();
    }

    /**
     * Adapter to bind the Spinners
     *
     * @param masterType Table name
     * @param filter     Data filter
     * @return List of CustomType
     */
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> labels = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, labels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    //<editor-fold desc="Functions related to show home menu and trap back button press">

    /**
     * Method to load previous Activity if back is pressed
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityAddPayment.this, ActivityAddDelivery.class);
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
