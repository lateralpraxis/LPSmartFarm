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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityAddPayment extends Activity {

    private final Context mContext = this;

    private TextView tvDispatchForName, tvDispatchForMobile, tvBookedQty, tvAmount, tvAdvance, tvBalance;
    private Spinner spPaymentMode;
    //private TextView tvPaymentAmount, tvPaymentRemarks;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String userId;
    private String lang;

    private String dispatchId, driverName, driverMobileNo, dispatchForId, dispatchForName, dispatchForMobile, totalDispatch, totalAmount;

    private Button btnBack, btnNext;
    private EditText etPaymentAmount, etPaymentRemarks;

    private ArrayList<HashMap<String, String>> dispatchData = null;


    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";

    double flatitude = 0.0, flongitude = 0.0;

    GPSTracker gps;

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
        /*tvAdvance = findViewById(R.id.tvAdvance);*/
        tvBalance = findViewById(R.id.tvBalance);

        spPaymentMode = findViewById(R.id.spPaymentMode);
        etPaymentAmount = findViewById(R.id.etPaymentAmount);
        etPaymentRemarks = findViewById(R.id.etPaymentRemarks);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
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
            /*totalDispatch = extras.getString("totalDispatch");
            totalAmount = extras.getString("totalAmount");*/
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
                Intent i = new Intent(ActivityAddPayment.this, ActivityAddDelivery.class);
                i.putExtra("dispatchId", dispatchId);
                i.putExtra("driverName", driverName);
                i.putExtra("driverMobileNo", driverMobileNo);
                i.putExtra("dispatchForId", dispatchForId);
                i.putExtra("dispatchForName", dispatchForName);
                i.putExtra("dispatchForMobile", dispatchForMobile);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Check delivery quantity against dispatch quantity */

                if ((spPaymentMode.getSelectedItemPosition()) == 0) {
                    common.showToast("Payment Mode is Mandatory", 5, 1);
                } else if (String.valueOf(etPaymentAmount.getText()).trim().equals("")) {
                    common.showToast("Payment Amount is Mandatory.");
                } else {
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";

                    gps = new GPSTracker(ActivityAddPayment.this);

                    if (gps.canGetLocation()) {
                        flatitude = gps.getLatitude();
                        flongitude = gps.getLongitude();

                        latitude = String.valueOf(flatitude);
                        longitude = String.valueOf(flongitude);

                        if (!latitude.equals("NA") && !longitude.equals("NA")) {
                            latitudeN = latitude.toString();
                            longitudeN = longitude.toString();
                            accuracy = common.stringToOneDecimal(String.valueOf(gps.accuracy)) + " mts";
                        } else if (latitude.equals("NA") || longitude.equals("NA")) {
                            flatitude = gps.getLatitude();
                            flongitude = gps.getLongitude();
                        }

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
                                                etPaymentAmount.getText().toString().trim(), etPaymentRemarks.getText().toString().trim(), userId, longitudeN, latitudeN, accuracy);
                                        dba.close();
                                        Intent intent = new Intent(ActivityAddPayment.this,
                                                ActivityViewDelivery.class);
                                        startActivity(intent);
                                        finish();
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
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
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
     */
    private void BindData() {
        dba.open();
        dispatchData = dba.getPendingDispatchItemsForDelivery(dispatchId);
        int totalQuantity = 0, totalAmount = 0;
        for (int i = 0; i < dispatchData.size(); i++) {
            totalQuantity += Integer.valueOf(dispatchData.get(i).get("Quantity"));
            totalAmount += (Double.valueOf(dispatchData.get(i).get("Rate")) * Integer.valueOf(dispatchData.get(i).get("DeliveryQuantity")));
        }
        int balance = dba.getBalanceForFarmerNursery(dispatchForId);
        tvBookedQty.setText(String.valueOf(totalQuantity));
        tvAmount.setText(Double.valueOf(totalAmount).toString());
        tvBalance.setText(Double.valueOf(balance).toString());
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
        i.putExtra("dispatchId", dispatchId);
        i.putExtra("driverName", driverName);
        i.putExtra("driverMobileNo", driverMobileNo);
        i.putExtra("dispatchForId", dispatchForId);
        i.putExtra("dispatchForName", dispatchForName);
        i.putExtra("dispatchForMobile", dispatchForMobile);
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
                onBackPressed();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder confirm = new AlertDialog.Builder
                        (ActivityAddPayment.this);
                AlertDialog confirmDialog;
                confirm
                        .setTitle("Confirmation")
                        .setMessage("Delivery data would be cleared. Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent homeScreenIntent = new Intent(ActivityAddPayment.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*Just close the dialog without doing anything*/
                                dialog.cancel();
                            }
                        });
                confirmDialog = confirm.create();
                confirmDialog.show();
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
