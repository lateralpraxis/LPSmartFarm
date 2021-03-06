package lateralpraxis.lpsmartfarm.delivery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.CustomType;

public class ActivityAddPayment extends Activity {

    private static final int PICK_Camera_IMAGE = 0;
    protected static final int GALLERY_REQUEST = 1;

    private final Context mContext = this;

    private TextView tvDispatchForName, tvDispatchForMobile, tvBookedQty, tvAmount, tvPaymentFile, tvBalance;
    private Spinner spPaymentMode;
    //private TextView tvPaymentAmount, tvPaymentRemarks;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String userId;
    private String lang;

    private String dispatchId, driverName, driverMobileNo, dispatchForId, dispatchForName, dispatchForMobile;
    private String payImgUID, level1dir, level2dir, level3dir, fileName, payImgPath, fullPayImgPath;
    private ArrayList<HashMap<String, String>> attachments;

    File destination;
    Uri uri;
    Intent picIntent;
    File[] listFiles;
    String[] filePathStrings, fileNameStrings;

    private Button btnUpload, btnViewAttach, btnBack, btnNext;
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
        tvBalance = findViewById(R.id.tvBalance);
        tvPaymentFile = findViewById(R.id.tvPaymentFile);

        spPaymentMode = findViewById(R.id.spPaymentMode);
        etPaymentAmount = findViewById(R.id.etPaymentAmount);
        etPaymentRemarks = findViewById(R.id.etPaymentRemarks);

        btnUpload = findViewById(R.id.btnUpload);
        btnViewAttach = findViewById(R.id.btnViewAttach);
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
        }
        //</editor-fold>


        //<editor-fold desc="Add data to controls">
        tvDispatchForName.setText(dispatchForName);
        tvDispatchForMobile.setText(dispatchForMobile);
        spPaymentMode.setAdapter(DataAdapter("paymentmode", ""));
        //</editor-fold>

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvPaymentFile.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("Attache Payment File");
                    dialog.setMessage("Are you sure, you want attach the Payment File?");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tvPaymentFile.setText("");
                                    btnViewAttach.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("DeliveryPayment");
                                    dba.close();
                                    startPaymentDialog();
                                }
                            }).setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = dialog.create();
                    alert.show();
                } else {
                    startPaymentDialog();
                }
            }
        });

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

        btnViewAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Check SD Card
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        common.showToast("ERROR: No SD Card found");
                    } else {
                        dba.open();
                        attachments = dba.GetTempAttachment("DeliveryPayment");
                        dba.close();

                        if (attachments.size() > 0) {
                            for (HashMap<String, String> map : attachments) {
                                for (String key : map.keySet()) {
                                    if (key.contains("FileName"))
                                        fileName = map.get(key);
                                }
                            }
                            File file = new File(fileName);
                            destination = new File(file.getParent());
                        }
                    }

                    if (destination.isDirectory()) {
                        listFiles = destination.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return fileName.endsWith(".jpeg")
                                        || fileName.endsWith(".bmp")
                                        || fileName.endsWith(".jpg")
                                        || fileName.endsWith(".png")
                                        || fileName.endsWith(".gif");
                            }
                        });
                        filePathStrings = new String[listFiles.length];
                        fileNameStrings = new String[listFiles.length];

                        for (int i = 0; i < filePathStrings.length; i++) {
                            filePathStrings[i] = listFiles[i].getAbsolutePath();
                            fileNameStrings[i] = listFiles[i].getName();

                            Intent i1 = new Intent(ActivityAddPayment.this, ViewImage.class);
                            i1.putExtra("filepath", filePathStrings);
                            i1.putExtra("filename", fileNameStrings);
                            i1.putExtra("position", 0);
                            startActivity(i1);
                        }
                    }
                } catch (Exception e) {
                    common.showAlert(ActivityAddPayment.this, "ERROR: " + e.getMessage(), false);
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

    private void startPaymentDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityAddPayment.this);
        dialog.setTitle("Select Image Source");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ActivityAddPayment.this,
                android.R.layout.select_dialog_singlechoice);
        adapter.add("Capture Image");
        adapter.add("Select from Gallery");

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setAdapter(
                adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String option = adapter.getItem(which);
                        payImgUID = UUID.randomUUID().toString();
                        level1dir = "LPSMARTFARM";
                        level2dir = level1dir + "/" + "DeliveryPayment";
                        level3dir = level2dir + "/" + payImgUID;
                        fullPayImgPath = Environment.getExternalStorageDirectory() + "/" + level3dir;
                        fileName = common.random() + ".jpg";
                        destination = new File(fullPayImgPath, fileName);
                        if (option.equals("Capture Image")) {
                            if (common.createDirectory(level1dir) && common.createDirectory(level2dir) && common.createDirectory(level3dir)) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                                startActivityForResult(intent, PICK_Camera_IMAGE);
                                btnViewAttach.setVisibility(View.GONE);
                            }

                        } else if (option.equals("Select from Gallery")) {
                            picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            picIntent.putExtra("return_data", true);
                            startActivityForResult(picIntent, GALLERY_REQUEST);

                        } else {
                            common.showToast("No file available for preview");
                        }
                    }
                });
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == 0 && data == null) {
            //Reset image name and hide reset button
            tvPaymentFile.setText("");
        } else if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    if (uri != null) {
                        payImgPath = common.getRealPathFromUri(uri);
                        if (common.createDirectory(level1dir) && common.createDirectory(level2dir) && common.createDirectory(level3dir)) {
                            common.copyFile(payImgPath, fullPayImgPath, destination.getPath());
                        }
                        btnViewAttach.setVisibility(View.VISIBLE);
                        tvPaymentFile.setText(fileName);
                        dba.open();
                        dba.Insert_TempFile("DeliveryPayment", fullPayImgPath + "/" + fileName);
                        dba.close();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                tvPaymentFile.setText("");
            }
        } else if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                payImgPath = fullPayImgPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1);
                //payImgPath = common.compressImage(payImgPath);
                dba.open();
                dba.Insert_TempFile("DeliveryPayment", payImgPath);
                dba.close();
                btnViewAttach.setVisibility(View.VISIBLE);
                tvPaymentFile.setText(destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(payImgPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == PICK_Camera_IMAGE) {
                tvPaymentFile.setText("");
                btnViewAttach.setVisibility(View.GONE);
            }
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

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
}
