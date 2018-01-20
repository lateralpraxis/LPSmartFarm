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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import lateralpraxis.type.VisitNurseryViewData;

public class RoutineVisitNursery3 extends Activity {
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
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    //-------Varaibles used in Capture GPS---------//
    GPSTracker gps;
    /*------------------------End of code for controls Declaration------------------------------*/
    double flatitude = 0.0, flongitude = 0.0;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvType, tvNursery, tvNurseryZone, tvPlantation;
    private EditText etAvgHeightOfPlant;
    /*------------------------End of code for class Declaration------------------------------*/
    private Spinner spPlantStatus;
    private CheckBox cbAgronomistVisitRecmd;
    private Button btnBack, btnNext;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, type, nurseryId, nursery, zoneId, zone, EntryFor, visitUniqueId, plantationUniqueId, plantationName, plantStatusId = "0", FromPage;
    /*------------------------End of code for variable Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit_nursery3);

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
        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        etAvgHeightOfPlant = (EditText) findViewById(R.id.etAvgHeightOfPlant);
        spPlantStatus = (Spinner) findViewById(R.id.spPlantStatus);
        cbAgronomistVisitRecmd = (CheckBox) findViewById(R.id.cbAgronomistVisitRecmd);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

           /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationName = extras.getString("plantationName");
            visitUniqueId = extras.getString("visitUniqueId");
            EntryFor = extras.getString("EntryFor");
            FromPage = extras.getString("FromPage");
            tvType.setText(type);
            tvNursery.setText(nursery);
            tvNurseryZone.setText(zone);
            tvPlantation.setText(plantationName);
        }

        //Allowed only 1 decimal value
        etAvgHeightOfPlant.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 1)});
        etAvgHeightOfPlant.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (visitUniqueId != null) {
            dba.open();
            List<VisitNurseryViewData> obj;
            obj = dba.GetVisitNurseryViewDetail(visitUniqueId);
            dba.close();
            etAvgHeightOfPlant.setText(obj.get(0).getPlantHeight());
            plantStatusId = obj.get(0).getPlantStatusId();
        }

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

                                //To move from RoutineVisitNursery3 to RoutineVisitNursery2 page
                                Intent intent = new Intent(RoutineVisitNursery3.this, RoutineVisitNursery2.class);
                                intent.putExtra("type", type);
                                intent.putExtra("nurseryId", nurseryId);
                                intent.putExtra("nursery", nursery);
                                intent.putExtra("zoneId", zoneId);
                                intent.putExtra("zone", zone);
                                intent.putExtra("plantationUniqueId", plantationUniqueId);
                                intent.putExtra("plantationName", plantationName);
                                intent.putExtra("visitUniqueId", visitUniqueId);
                                intent.putExtra("EntryFor", EntryFor);
                                intent.putExtra("FromPage", FromPage);
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
                }
                else if (Double.valueOf(etAvgHeightOfPlant.getText().toString())==0) {
                    common.showToast("Avg. Plant Height can not be zero!", 5, 1);
                    isError = "true";
                }else if ((spPlantStatus.getSelectedItemPosition() == 0)) {
                    common.showToast("Plant Status is mandatory!");
                    isError = "true";
                }
                if (isError.equalsIgnoreCase("false")) {
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(RoutineVisitNursery3.this);
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
                            dba.InsertVisitReport(visitUniqueId, "0", nurseryId, "Nursery", zoneId, plantationUniqueId, String.valueOf(etAvgHeightOfPlant.getText()).trim(), String.valueOf(((CustomType) spPlantStatus.getSelectedItem()).getId()), "", latitudeN, longitudeN, accuracy, userId);
                            dba.close();
                        } else {
                            dba.open();
                            dba.UpdateVisitReport(visitUniqueId, "0", nurseryId, "Nursery", zoneId, plantationUniqueId, String.valueOf(etAvgHeightOfPlant.getText()).trim(), String.valueOf(((CustomType) spPlantStatus.getSelectedItem()).getId()), "", latitudeN, longitudeN, accuracy);
                            dba.close();
                        }

                        //To move from RoutineVisitNursery3 to RoutineVisitNursery4 page
                        Intent intent = new Intent(RoutineVisitNursery3.this, RoutineVisitNursery4.class);
                        intent.putExtra("type", type);
                        intent.putExtra("nurseryId", nurseryId);
                        intent.putExtra("nursery", nursery);
                        intent.putExtra("zoneId", zoneId);
                        intent.putExtra("zone", zone);
                        intent.putExtra("plantationUniqueId", plantationUniqueId);
                        intent.putExtra("plantationName", plantationName);
                        intent.putExtra("visitUniqueId", visitUniqueId);
                        intent.putExtra("EntryFor", EntryFor);
                        intent.putExtra("FromPage", FromPage);
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
                Intent intent = new Intent(RoutineVisitNursery3.this, RoutineVisitNursery2.class);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(RoutineVisitNursery3.this, ActivityHome.class);
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
        Intent intent = new Intent(RoutineVisitNursery3.this, RoutineVisitNursery2.class);
        intent.putExtra("type", type);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nursery", nursery);
        intent.putExtra("zoneId", zoneId);
        intent.putExtra("zone", zone);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantationName", plantationName);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("FromPage", FromPage);
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
