package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

/**
 * Created by amol.marathe on 10-10-2017.
 */

public class ActivityAddPlantation extends Activity {

    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    /*Code for control declaration*/
    private Spinner spPlantationCrop, spPlantationVariety, spPlantationMonthAge, spPlantationSystem, spPlantationType;
    private TextView tvFarmerName, tvFarmerMobile, tvFarmBlockCode,
            tvPlantationTotal;
    private EditText etPlantationArea, etPlantationRow, etPlantationColumn, etPlantationBalance, etPlantationDate;
    private Button btnBack, btnSave;
    /*Variable declaration*/
    private String userId;
    private static String lang;
    private int varietyId = 0;
    private double fbArea =0.0;
    private long plantationTime;
    private Calendar calendar;
    private int year, month, day;
    private ArrayList<String> farmerData;
    private String plantationUniqueId = "", farmerUniqueId, farmBlockUniqueId, farmerName, farmerMobile, farmerCode, farmBlockCode;
    private SimpleDateFormat dateFormatter;

    /*Override onCreate method*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plantation);

        /*Setup ActionBar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*Database Instance*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        /*Finding Controls*/
        tvFarmerName = (TextView) findViewById(R.id.tvFarmerName);
        tvFarmerMobile = (TextView) findViewById(R.id.tvFarmerMobile);
        tvFarmBlockCode = (TextView) findViewById(R.id.tvFarmBlockCode);
        etPlantationDate = (EditText) findViewById(R.id.etPlantationDate);

        spPlantationCrop = (Spinner) findViewById(R.id.spPlantationCrop);
        spPlantationVariety = (Spinner) findViewById(R.id.spPlantationVariety);
        spPlantationMonthAge = (Spinner) findViewById(R.id.spPlantationMonthAge);
        spPlantationType = (Spinner) findViewById(R.id.spPlantationType);
        spPlantationSystem = (Spinner) findViewById(R.id.spPlantationSystem);

        etPlantationArea = (EditText) findViewById(R.id.etPlantationArea);
        etPlantationArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etPlantationArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPlantationRow = (EditText) findViewById(R.id.etPlantationRow);
        etPlantationColumn = (EditText) findViewById(R.id.etPlantationColumn);
        etPlantationBalance = (EditText) findViewById(R.id.etPlantationBalance);
        tvPlantationTotal = (TextView) findViewById(R.id.tvPlantationTotal);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);

        etPlantationDate.setInputType(InputType.TYPE_NULL);
        /*---------------Start of code to set calendar for Birth Date-------------------------*/
        etPlantationDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(ActivityAddPlantation.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etPlantationDate.setText(dateFormatter.format(newDate.getTime()));
                        plantationTime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle(lang.equalsIgnoreCase("en")?"Select Plantation date":"वृक्षारोपण तिथि का चयन करें");

                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.show();
            }
        });
        /*---------------End of code to set calendar for Birth Date-------------------------*/

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmerCode = extras.getString("farmerCode");
            farmBlockCode = extras.getString("farmBlockCode");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            tvFarmerName.setText(farmerName);
            tvFarmerMobile.setText(farmerMobile);
            tvFarmBlockCode.setText(farmBlockCode);
        }

        /*Binding data to Spinners*/
        spPlantationCrop.setAdapter(DataAdapter("plantationcrop", ""));
        spPlantationCrop.setAdapter(DataAdapter("plantationcrop", ""));
        spPlantationMonthAge.setAdapter(DataAdapter("monthAge", ""));
        spPlantationType.setAdapter(DataAdapter("plantTypeFarmBlock", ""));
        spPlantationSystem.setAdapter(DataAdapter("plantingSystem", ""));

         /*Code to get Farm Block Area*/
        dba.openR();
        fbArea =dba.getFarmBlockArea(farmBlockUniqueId);

        /*Binding data on Spinner item selected*/
        spPlantationCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spPlantationVariety.setAdapter(DataAdapter("variety", String.valueOf(((CustomType) spPlantationCrop.getSelectedItem()).getId())));
                if (varietyId > 0) {
                    int cnt = spPlantationCrop.getAdapter().getCount();
                    for (int i = 0; i < cnt; i++) {
                        if (((CustomType) spPlantationVariety.getItemAtPosition(i)).getId().equals(varietyId))
                            spPlantationVariety.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        /*Binding onFocusChangeLister to etPlantationRow*/
        etPlantationRow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        etPlantationRow.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        /*Binding onFocusChangeLister to etPlantationColumn*/
        etPlantationColumn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        etPlantationColumn.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        /*Binding onFocusChangeLister to etPlantationBalance*/
        etPlantationBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        etPlantationBalance.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });


        /*Save button click Listener*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Check mandatory fields*/

                etPlantationArea.clearFocus();
                etPlantationRow.clearFocus();
                etPlantationColumn.clearFocus();
                etPlantationBalance.clearFocus();
                etPlantationDate.clearFocus();
                if ((spPlantationCrop.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Crop is mandatory.":"फसल अनिवार्य है।",5,1);
                else if ((spPlantationVariety.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Variety is mandatory.":"विविधता अनिवार्य है।",5,1);
                else if ((spPlantationType.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Plant Type is mandatory.":"वनस्पति प्रकार अनिवार्य है।",5,1);
                else if ((spPlantationMonthAge.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Month Age is mandatory.":"मासिक आयु अनिवार्य है।",5,1);
                else if (String.valueOf(etPlantationArea.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Area is mandatory.":"क्षेत्र अनिवार्य है।",5,1);
                else if (Double.valueOf(String.valueOf(etPlantationArea.getText()).trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot be 0.":"क्षेत्र शून्य नहीं हो सकता।",5,1);
                else if(Double.valueOf(etPlantationArea.getText().toString())>fbArea)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot exceed Farm Block area.":"क्षेत्र फार्म ब्लॉक क्षेत्र से अधिक नहीं हो सकता।",5,1);
                else if (String.valueOf(etPlantationDate.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Date is mandatory.":"तिथि अनिवार्य है।",5,1);
                else if ((spPlantationSystem.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"System is mandatory.":"सिस्टम अनिवार्य है।",5,1);
                else if (String.valueOf(etPlantationRow.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Plant Row is mandatory":"प्लांट रो अनिवार्य है",5,1);
                else if (Integer.valueOf(String.valueOf(etPlantationRow.getText()).trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Plant Row cannot be 0.":"प्लांट पंक्ति शून्य नहीं हो सकती।",5,1);
                else if (String.valueOf(etPlantationColumn.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Plant Column is mandatory":"प्लांट कॉलम अनिवार्य है",5,1);
                else if (Integer.valueOf(String.valueOf(etPlantationColumn.getText()).trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Plant Column cannot be 0.":"प्लांट कॉलम शून्य नहीं हो सकता।",5,1);
                else if (Integer.valueOf(String.valueOf(tvPlantationTotal.getText()).trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Total cannot be 0.":"कुल शून्य नहीं हो सकता।",5,1);
                else {
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(ActivityAddPlantation.this);
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

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddPlantation.this);
                        alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                        alertDialogBuilder
                                .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to save Plantation Details?":"क्या आप निश्चित हैं, आप वृक्षारोपण विवरण सहेजना चाहते हैं?")
                                .setCancelable(false)
                                .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        plantationUniqueId = UUID.randomUUID().toString();
                                        dba.open();
                                        String balance = TextUtils.isEmpty(etPlantationBalance.getText().toString().trim()) ? "0" : etPlantationBalance.getText().toString().trim();
                                        int zoneId = dba.getZoneIdByFarmerId(farmBlockUniqueId);
                                        dba.insertFarmerPlantation(plantationUniqueId, farmerUniqueId, farmBlockUniqueId, String.valueOf(zoneId),
                                                String.valueOf(((CustomType) spPlantationCrop.getSelectedItem()).getId()),
                                                String.valueOf(((CustomType) spPlantationVariety.getSelectedItem()).getId()),
                                                String.valueOf(((CustomType) spPlantationType.getSelectedItem()).getId()),
                                                String.valueOf(((CustomType) spPlantationMonthAge.getSelectedItem()).getId()),
                                                etPlantationArea.getText().toString().trim(),
                                                etPlantationDate.getText().toString().trim(),
                                                String.valueOf(((CustomType) spPlantationSystem.getSelectedItem()).getId()),
                                                etPlantationRow.getText().toString().trim(),
                                                etPlantationColumn.getText().toString().trim(),
                                                balance,
                                                tvPlantationTotal.getText().toString().trim(),
                                                userId, longitudeN, latitudeN, accuracy);
                                        dba.close();

                                        Intent intent = new Intent(ActivityAddPlantation.this, ActivityPlantationList.class);
                                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                                        intent.putExtra("farmerName", farmerName);
                                        intent.putExtra("farmerMobile", farmerMobile);
                                        intent.putExtra("farmBlockCode", farmBlockCode);
                                        intent.putExtra("farmerCode", farmerCode);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                /*Just close the dialog without doing anything*/
                                        dialog.cancel();
                                    }
                                });
                /*Create and Show alert dialog box*/
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

        /*dba.openR();
        farmerData = dba.getFarmerDetailsByUniqueId(farmerUniqueId);
        tvFarmerName.setText(farmerData.get(4) + ' ' + farmerData.get(5) + ' ' + farmerData.get(6));
        tvFarmerMobile.setText(farmerData.get(13));*/
    }


    /*Method to fetch data and bind to spinner*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    /*Calculate the Total Plantation value based on Row, Column and Balance*/
    private String getPlantationTotal() {
        Integer row, column, bal, total;
        row = Integer.valueOf((etPlantationRow.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationRow.getEditableText().toString().trim()));
        column = Integer.valueOf((etPlantationColumn.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationColumn.getEditableText().toString().trim()));
        bal = Integer.valueOf((etPlantationBalance.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationBalance.getEditableText().toString().trim()));
        total = (row * column) + bal;
        return total.toString();
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityAddPlantation.this, ActivityPlantationList.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("farmerCode", farmerCode);
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

        Intent intent = new Intent(ActivityAddPlantation.this, ActivityPlantationList.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
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
