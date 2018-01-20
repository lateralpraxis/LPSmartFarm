package lateralpraxis.lpsmartfarm.nursery;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * Created by amol.marathe on 02-11-2017.
 */

public class ActivityAddNurseryPlantation extends Activity {

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;

    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;

    /*Code for control declaration*/
    private Spinner spPlantationCrop, spPlantationVariety, spPlantationMonthAge,
            spPlantationSystem, spPlantationType;
    private TextView tvNurseryId, tvNurseryType, tvNurseryName, tvPlantationDate,
            tvPlantationTotal, tvNurseryZoneName;
    private EditText etPlantationArea, etPlantationRow, etPlantationColumn, etPlantationBalance;
    private Button btnBack, btnSave;

    /*Variable declaration*/
    private String userId;
    private int varietyId = 0;
    private double zoneArea =0.0;
    private Calendar calendar;
    private int year, month, day;
    private SimpleDateFormat dateFormatter;
    private String plantationUniqueId = "", nurseryUniqueId, nurseryId, nurseryType, nurseryName,
            nurseryZoneName, zoneId;
    //<editor-fold desc="Methods to display the Calendar">
    private DatePickerDialog.OnDateSetListener plantationDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    calendar.set(arg1, arg2, arg3);
                    showDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar
                            .getTime()));
                }
            };

    /*Override onCreate method*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nursery_plantation);

        /*Setup ActionBar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*Database Instance*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*Finding Controls*/
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvPlantationDate = (TextView) findViewById(R.id.tvPlantationDate);
        tvNurseryZoneName = (TextView) findViewById(R.id.tvNurseryZoneName);

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

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime()));

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            nurseryZoneName = extras.getString("nurseryZone");
            zoneId = extras.getString("nurseryZoneId");
            dba.openR();
            zoneArea =Double.valueOf(dba.getZoneAreaByZoneId(zoneId));
            tvNurseryId.setText(nurseryId);
            tvNurseryType.setText(nurseryType);
            tvNurseryName.setText(nurseryName);
            tvNurseryZoneName.setText(nurseryZoneName);
        }
        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        /*Binding data to Spinners*/
        spPlantationCrop.setAdapter(DataAdapter("plantationcrop", ""));
        spPlantationMonthAge.setAdapter(DataAdapter("monthAge", ""));
        spPlantationType.setAdapter(DataAdapter("plantType", ""));
        spPlantationSystem.setAdapter(DataAdapter("plantingSystem", ""));


        /*Binding data on Spinner item selected*/
        spPlantationCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spPlantationVariety.setAdapter(DataAdapter("variety", String.valueOf((
                        (CustomType) spPlantationCrop.getSelectedItem()).getId())));
                if (varietyId > 0) {
                    int cnt = spPlantationCrop.getAdapter().getCount();
                    for (int i = 0; i < cnt; i++) {
                        if (((CustomType) spPlantationVariety.getItemAtPosition(i)).getId()
                                .equals(varietyId))
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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAddNurseryPlantation.this,
                        ActivityViewNurseryPlantationList.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
                startActivity(intent);
                finish();
            }
        });

        /*Save button click Listener*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Check mandatory fields*/

                if ((spPlantationCrop.getSelectedItemPosition() == 0)) {
                    common.showToast("Crop is mandatory",5,1);
                } else if ((spPlantationVariety.getSelectedItemPosition() == 0)) {
                    common.showToast("Variety is mandatory",5,1);
                } else if ((spPlantationType.getSelectedItemPosition() == 0)) {
                    common.showToast("Plant Type is mandatory",5,1);
                } else if ((spPlantationMonthAge.getSelectedItemPosition() == 0)) {
                    common.showToast("Month Age is mandatory",5,1);
                }  else if (String.valueOf(etPlantationArea.getText()).trim().equals(""))
                    common.showToast("Area is mandatory.",5,1);
                else if (Double.valueOf(String.valueOf(etPlantationArea.getText()).trim()) <= 0)
                    common.showToast("Area cannot be 0.",5,1);
                else if(Double.valueOf(etPlantationArea.getText().toString())>zoneArea)
                    common.showToast("Area cannot exceed Zone area.",5,1);
                else if (String.valueOf(tvPlantationDate.getText()).trim().equals("")) {
                    common.showToast("Date is mandatory",5,1);
                }
                else if ((spPlantationSystem.getSelectedItemPosition() == 0)) {
                    common.showToast("System is mandatory",5,1);
                }
                 else if (String.valueOf(etPlantationRow.getText()).trim().equals("")) {
                    common.showToast("Plant Row is mandatory",5,1);
                } else if (Integer.valueOf(String.valueOf(etPlantationRow.getText()).trim()) <= 0) {
                    common.showToast("Plant Row cannot be 0",5,1);
                } else if (String.valueOf(etPlantationColumn.getText()).trim().equals("")) {
                    common.showToast("Plant Column is mandatory",5,1);
                } else if (Integer.valueOf(String.valueOf(etPlantationColumn.getText()).trim())
                        <= 0) {
                    common.showToast("Plant Column cannot be 0",5,1);
                } else if (spPlantationMonthAge.getSelectedItem().toString().trim() == "0"
                        && spPlantationType.getSelectedItem().toString().toLowerCase().trim() !=
                        "seedling") {
                    common.showToast("MonthAge 0 is allowed only for Seedling type",5,1);
                } else {

                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(ActivityAddNurseryPlantation.this);
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
                            (ActivityAddNurseryPlantation.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder
                            .setMessage("Are you sure, you want to save Nursery Plantation?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    plantationUniqueId = UUID.randomUUID().toString();
                                    String balance = TextUtils.isEmpty(etPlantationBalance.getText().toString().trim()) ? "0" : etPlantationBalance.getText().toString().trim();
                                    dba.open();
                                    dba.insertNurseryPlantation(plantationUniqueId, nurseryId,
                                            zoneId,
                                            String.valueOf(((CustomType) spPlantationCrop
                                                    .getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationVariety
                                                    .getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationType
                                                    .getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationMonthAge
                                                    .getSelectedItem()).getId()),
                                            etPlantationArea.getText().toString().trim(),
                                            tvPlantationDate.getText().toString().trim(),
                                            String.valueOf(((CustomType) spPlantationSystem
                                                    .getSelectedItem()).getId()),
                                            etPlantationRow.getText().toString().trim(),
                                            etPlantationColumn.getText().toString().trim(),
                                            balance,
                                            tvPlantationTotal.getText().toString().trim(),
                                            userId, longitudeN, latitudeN, accuracy);
                                    dba.close();

                                    Intent intent = new Intent(ActivityAddNurseryPlantation.this,
                                            ActivityViewNurseryPlantationList.class);
                                    intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                                    intent.putExtra("nurseryId", nurseryId);
                                    intent.putExtra("nurseryType", nurseryType);
                                    intent.putExtra("nurseryName", nurseryName);
                                    intent.putExtra("nurseryZone", nurseryZoneName);
                                    intent.putExtra("nurseryZoneId", zoneId);
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
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {

            DatePickerDialog dialog = new DatePickerDialog(this, plantationDateListener, year, month, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }
        return null;
    }

    private void showDate(String date) {
        tvPlantationDate.setText(date.replace(" ","-"));
    }
    //</editor-fold>

    //<editor-fold desc="Method to fetch data and bind to spinner">
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R
                .layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }
    //</editor-fold>

    //<editor-fold desc="Calculate the Total Plantation value based on Row, Column and Balance">
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
    //</editor-fold>

    //<editor-fold desc="Method to handle when Back button is pressed">
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityAddNurseryPlantation.this,
                ActivityViewNurseryPlantationList.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryType", nurseryType);
        intent.putExtra("nurseryName", nurseryName);
        intent.putExtra("nurseryZone", nurseryZoneName);
        intent.putExtra("nurseryZoneId", zoneId);
        startActivity(intent);
        finish();
        System.gc();
    }
    //</editor-fold>

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //To move from visit details to plantation details page
                Intent intent = new Intent(ActivityAddNurseryPlantation.this,
                        ActivityViewNurseryPlantationList.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(ActivityAddNurseryPlantation.this, ActivityHome.class);
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
}
