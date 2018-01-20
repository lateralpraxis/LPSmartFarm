package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.FarmBlockViewData;

public class FarmBlockUpdate extends Activity {
    /*------------------------Start of code for Regular Expression for Validating Decimal Values------------------------------*/
    final String Digits = "(\\p{Digit}+)";
    final String HexDigits = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    final String Exp = "[eE][+-]?" + Digits;
    final String fpRegex =
            ("[\\x00-\\x20]*" + // Optional leading "whitespace"
                    "[+-]?(" +         // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.(" + Digits + ")(" + Exp + ")?)|" +

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");
    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    /*------------------------Start of code for class Declaration------------------------------*/
    private List<FarmBlockViewData> obj;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvOwnershipType, tvFpoCooperativeName, tvContractDate, tvStreet1, tvStreet2, tvState, tvDistrict, tvBlock, tvPanchayat, tvVillage, tvPincode, tvOwner, tvOwnerMobile;
    private EditText etKhata, etKhasra, etArea;
    private SimpleDateFormat dateFormatter_display;
    private Button btnNext;
    private LinearLayout llFarmBlock, llNameMobile;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    /*------------------------End of code for variable Declaration------------------------------*/
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private ArrayList<String> farmBlockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block_update);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        dateFormatter_display = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            entryType = extras.getString("entryType");
            farmerCode = extras.getString("farmerCode");
        }

         /*------------------------Start of code for controls Declaration--------------------------*/

        llNameMobile = (LinearLayout) findViewById(R.id.llNameMobile);
        tvOwner = (TextView) findViewById(R.id.tvOwner);
        tvOwnerMobile = (TextView) findViewById(R.id.tvOwnerMobile);

        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);

        etKhata = (EditText) findViewById(R.id.etKhata);
        etKhasra = (EditText) findViewById(R.id.etKhasra);
        tvOwnershipType = (TextView) findViewById(R.id.tvOwnershipType);
        tvFpoCooperativeName = (TextView) findViewById(R.id.tvFpoCooperativeName);
        tvContractDate = (TextView) findViewById(R.id.tvContractDate);
        etArea = (EditText) findViewById(R.id.etArea);
        tvStreet1 = (TextView) findViewById(R.id.tvStreet1);
        tvStreet2 = (TextView) findViewById(R.id.tvStreet2);
        tvState = (TextView) findViewById(R.id.tvState);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);
        tvBlock = (TextView) findViewById(R.id.tvBlock);
        tvPanchayat = (TextView) findViewById(R.id.tvPanchayat);
        tvVillage = (TextView) findViewById(R.id.tvVillage);
        tvPincode = (TextView) findViewById(R.id.tvPincode);

        btnNext = (Button) findViewById(R.id.btnNext);

        llNameMobile.setVisibility(View.GONE);
        /*---------------Start of code to set Filters and Input Type for Area and Contract Date-------------------------*/
        etArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

        /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }

        //Code to Validate Total Acreage Entered is Valid Number or Not
        etArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Pattern.matches(fpRegex, etArea.getText())) {

                    } else
                        etArea.setText("");
                }
            }
        });

        /*---------------Start of code to set Click Event for Button Save & Next-------------------------*/
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                latitude = "NA";
                longitude = "NA";
                accuracy = "NA";
                latitudeN = "NA";
                longitudeN = "NA";
                // create class object
                gps = new GPSTracker(FarmBlockUpdate.this);
                if (gps.canGetLocation()) {
                    flatitude = gps.getLatitude();
                    flongitude = gps.getLongitude();
                        /*if (flatitude == 0.0 || flongitude == 0.0) {
                            common.showAlert(FarmerCreateStep1.this, "Unable to fetch coordinates. Please try again.", false);
                        } else {*/
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
                    if (String.valueOf(etKhata.getText()).trim().equals(""))
                        common.showToast("Khata# is mandatory!");
                    else if (String.valueOf(etKhasra.getText()).trim().equals(""))
                        common.showToast("Khasra# is mandatory!");
                    else if (String.valueOf(etArea.getText()).trim().equals(""))
                        common.showToast("Area is mandatory!");
                    else if (!Pattern.matches(fpRegex, etArea.getText()))
                        common.showToast("Please enter valid Area!");
                    else if (Double.valueOf(String.valueOf(etArea.getText()).trim()) < .01)
                        common.showToast("Area cannot be less than .01!");
                    else {
                        //To update farm block details
                        dba.open();
                        dba.updateFarmBlockById(farmBlockUniqueId, etKhata.getText().toString().trim(), etKhasra.getText().toString().trim(), etArea.getText().toString().trim(), longitudeN, latitudeN, accuracy);
                        dba.close();

                        //To move farm block soil page
                        Intent intent = new Intent(FarmBlockUpdate.this, FarmBlockSoil.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("entryType", entryType);
                        intent.putExtra("farmerCode", farmerCode);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/

        /*---------------Start of code to bind Farm Block details-------------------------*/
        if (!farmBlockUniqueId.equalsIgnoreCase("0")) {
            dba.openR();
            obj = dba.getFarmBlockById(farmBlockUniqueId);
            tvFarmer.setText(farmerName);
            tvMobile.setText(farmerMobile);
            tvFarmBlock.setText(obj.get(0).getFarmBlockCode());
            tvOwnershipType.setText(obj.get(0).getOwnershipType());
            if ((String.valueOf(obj.get(0).getOwnershipType())).equalsIgnoreCase("Contract") || (String.valueOf(obj.get(0).getOwnershipType())).equalsIgnoreCase("Leased")) {
                llNameMobile.setVisibility(View.VISIBLE);
                tvOwner.setText(obj.get(0).getOwnerName());
                tvOwnerMobile.setText(obj.get(0).getOwnerMobile());
            }
            else
            {
                llNameMobile.setVisibility(View.GONE);
                tvOwner.setText("");
                tvOwnerMobile.setText("");
            }
            tvFpoCooperativeName.setText(obj.get(0).getFPO());
            etKhata.setText(obj.get(0).getKhataNo());
            etKhasra.setText(obj.get(0).getKhasraNo());
            String dateString="";
            if (String.valueOf(obj.get(0).getContractDate()).trim().equals(""))
                dateString ="";
            else {
                Date date = new Date();
                try {
                    date = dateFormatter_display.parse(obj.get(0).getContractDate().toString().trim());
                    dateString = dateFormatter_display.format(date);
                } catch (ParseException e1) {
                }
            }
            tvContractDate.setText(dateString);
            etArea.setText(obj.get(0).getAcerage());
            tvStreet1.setText(obj.get(0).getStreet1());
            tvStreet2.setText(obj.get(0).getStreet2());
            tvState.setText(obj.get(0).getStateName());
            tvDistrict.setText(obj.get(0).getDistrictName());
            tvBlock.setText(obj.get(0).getBlockName());
            tvPanchayat.setText(obj.get(0).getPanchayatName());
            tvVillage.setText(obj.get(0).getVillageName());
            tvPincode.setText(obj.get(0).getPinCode());
        }
    }

    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FarmBlockUpdate.this, FarmBlockView.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("entryType", entryType);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockUpdate.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockUpdate.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(FarmBlockUpdate.this, FarmBlockView.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("entryType", entryType);
        intent.putExtra("farmerCode", farmerCode);
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
}
