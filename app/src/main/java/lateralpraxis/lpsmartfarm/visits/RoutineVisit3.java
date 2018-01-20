package lateralpraxis.lpsmartfarm.visits;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.VisitViewData;

public class RoutineVisit3 extends Activity {
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
    private final Context mContext = this;
    //-------Varaibles used in Capture GPS---------//
    GPSTracker gps;
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvFarmBlock, tvPlantation;
    private EditText etAvgHeightOfPlant, etAgronomistVisitRecmd;
    private Spinner spPlantStatus;
    private CheckBox cbAgronomistVisitRecmd;
    private Button btnBack, btnNext;
    /*------------------------End of code for controls Declaration------------------------------*/

    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/

    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, visitUniqueId = "0", farmerUniqueId, farmBlockUniqueId, farmerName, farmerMobile, farmBlockCode, plantationUniqueId, plantationName, plantStatusId = "0", EntryFor;
    private ArrayList<String> farmBlockData;
    /*------------------------End of code for variable Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit3);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);

        etAvgHeightOfPlant = (EditText) findViewById(R.id.etAvgHeightOfPlant);
        etAgronomistVisitRecmd = (EditText) findViewById(R.id.etAgronomistVisitRecmd);

        spPlantStatus = (Spinner) findViewById(R.id.spPlantStatus);

        cbAgronomistVisitRecmd = (CheckBox) findViewById(R.id.cbAgronomistVisitRecmd);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

           /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationName = extras.getString("plantationName");
            visitUniqueId = extras.getString("visitUniqueId");
            EntryFor = extras.getString("EntryFor");
            tvFarmer.setText(farmerName);
            tvFarmBlock.setText(farmBlockCode);
            tvPlantation.setText(plantationName);
        }

        etAgronomistVisitRecmd.setEnabled(false);

        //Allowed only 1 decimal value
        etAvgHeightOfPlant.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 1)});
        etAvgHeightOfPlant.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (visitUniqueId != null) {
            dba.open();
            List<VisitViewData> obj;
            obj = dba.GetVisitViewDetail(visitUniqueId);
            dba.close();
            etAvgHeightOfPlant.setText(obj.get(0).getPlantHeight());
            etAgronomistVisitRecmd.setText(obj.get(0).getDays());
            plantStatusId = obj.get(0).getPlantStatusId();
            if (obj.get(0).getDays().equalsIgnoreCase(""))
            {
                cbAgronomistVisitRecmd.setChecked(false);
                etAgronomistVisitRecmd.setEnabled(false);
            }
            else {
                cbAgronomistVisitRecmd.setChecked(true);
                etAgronomistVisitRecmd.setEnabled(true);
            }
        }


//Check box on check event to show edittext
        cbAgronomistVisitRecmd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etAgronomistVisitRecmd.setText("");
                if (isChecked) {
                    etAgronomistVisitRecmd.setEnabled(true);
                } else {
                    etAgronomistVisitRecmd.setEnabled(false);
                }
            }
        });
        //To bind plant status
        spPlantStatus.setAdapter(DataAdapter("plantstatus", ""));
        if (!plantStatusId.equalsIgnoreCase("0")) {
            int spdCnt = spPlantStatus.getAdapter().getCount();
            for (int i = 0; i < spdCnt; i++) {
                if (((CustomType) spPlantStatus.getItemAtPosition(i)).getId().toString().equals(plantStatusId))
                    spPlantStatus.setSelection(i);
            }
        }

        /*---------------Start of code to set Click Event for Button back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle("Confirmation");
                builder1.setMessage("Are you sure, you want to leave this module it will discard any unsaved data?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dba.open();
                                dba.DeleteTempVisitReport();
                                dba.close();

                                //To move routine visit 3 to routine visit 2 page
                                Intent intent = new Intent(RoutineVisit3.this, RoutineVisit2.class);
                                intent.putExtra("farmerUniqueId", farmerUniqueId);
                                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                                intent.putExtra("farmerName", farmerName);
                                intent.putExtra("farmerMobile", farmerMobile);
                                intent.putExtra("farmBlockCode", farmBlockCode);
                                intent.putExtra("plantationUniqueId", plantationUniqueId);
                                intent.putExtra("plantationName", plantationName);
                                intent.putExtra("visitUniqueId", visitUniqueId);
                                intent.putExtra("EntryFor", EntryFor);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                AlertDialog alertnew = builder1.create();
                alertnew.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String isError = "false";
                if (String.valueOf(etAvgHeightOfPlant.getText()).trim().equals("")) {
                    common.showToast("Avg. Plant Height (Mtr.) is mandatory!", 5, 1);
                    isError = "true";
                }
                else if (!Pattern.matches(fpRegex, etAvgHeightOfPlant.getText())) {
                    common.showToast("Please enter valid avg. plant height!", 5, 1);
                    isError = "true";
                } else if (Double.valueOf(etAvgHeightOfPlant.getText().toString())==0) {
                    common.showToast("Avg. Plant Height can not be zero!", 5, 1);
                    isError = "true";
                } else if ((spPlantStatus.getSelectedItemPosition() == 0)) {
                    common.showToast("Plant Status is mandatory!");
                    isError = "true";
                } else if (String.valueOf(etAgronomistVisitRecmd.getText()).trim().equals("") && cbAgronomistVisitRecmd.isChecked()) {
                    common.showToast("Agronomist Visit Recmd. is mandatory!", 5, 1);
                    isError = "true";
                } else if (cbAgronomistVisitRecmd.isChecked()) {
                    if ((Double.valueOf(etAgronomistVisitRecmd.getText().toString()) < 1 || Double.valueOf(etAgronomistVisitRecmd.getText().toString()) > 30)) {
                        common.showToast("No of days should be between 1 to 30!", 5, 1);
                        isError = "true";
                    }
                }
                if (isError.equalsIgnoreCase("false")) {
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(RoutineVisit3.this);
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
                        if (visitUniqueId == null) {
                            visitUniqueId = UUID.randomUUID().toString();
                            dba.open();
                            dba.InsertVisitReport(visitUniqueId, farmerUniqueId, farmBlockUniqueId, "FarmBlock", "0", plantationUniqueId, String.valueOf(etAvgHeightOfPlant.getText()).trim(), String.valueOf(((CustomType) spPlantStatus.getSelectedItem()).getId()), etAgronomistVisitRecmd.getText().toString(), latitudeN, longitudeN, accuracy, userId);
                            dba.close();
                        } else {
                            dba.open();
                            dba.UpdateVisitReport(visitUniqueId, farmerUniqueId, farmBlockUniqueId, "FarmBlock", "0", plantationUniqueId, String.valueOf(etAvgHeightOfPlant.getText()).trim(), String.valueOf(((CustomType) spPlantStatus.getSelectedItem()).getId()), etAgronomistVisitRecmd.getText().toString(), latitudeN, longitudeN, accuracy);
                            dba.close();
                        }

                        //To move from visit details to observation details page
                        Intent intent = new Intent(RoutineVisit3.this, RoutineVisit4.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("plantationUniqueId", plantationUniqueId);
                        intent.putExtra("plantationName", plantationName);
                        intent.putExtra("visitUniqueId", visitUniqueId);
                        intent.putExtra("EntryFor", EntryFor);
                        startActivity(intent);
                        finish();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/
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
                //To move from visit details to plantation details page
                Intent intent = new Intent(RoutineVisit3.this, RoutineVisit2.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("EntryFor", EntryFor);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(RoutineVisit3.this, ActivityHome.class);
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

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        //To move from visit details to plantation details page
        Intent intent = new Intent(RoutineVisit3.this, RoutineVisit2.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantationName", plantationName);
        intent.putExtra("EntryFor", EntryFor);
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
