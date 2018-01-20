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
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class FarmBlock extends Activity {
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
    /*------------------------Start of code for class Declaration------------------------------*/
    //-------Varaibles used in Capture GPS---------//
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock;
    private EditText etKhata, etKhasra, etContractDate, etArea, etStreet1, etStreet2, etOwner, etMobile;
    private Spinner spOwnershipType, spFpoCooperativeName, spState, spDistrict, spBlock, spPanchayat, spVillage, spPincode;
    private Button btnBack, btnNext;
    private LinearLayout llFarmBlock, llNameMobile;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    /*------------------------End of code for variable Declaration------------------------------*/
    private Common common;
    private SimpleDateFormat dateFormatter_display, dateFormatter_database;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private int stateId = 0, districtId = 0, blockId = 0, panchayatId = 0, villageId = 0, pincodeId = 0, fpoId = 0, ownerShipId = 0;
    private ArrayList<String> farmBlockData;
    private static String lang;
    /*------------------------End of code for variable Declaration------------------------------*/
    //Method to count number of occurence of  substring in a string
    public static boolean isValidPhone(String phone) {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        dateFormatter_display = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        dateFormatter_database = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
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
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        llNameMobile = (LinearLayout) findViewById(R.id.llNameMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);

        etKhata = (EditText) findViewById(R.id.etKhata);
        etKhasra = (EditText) findViewById(R.id.etKhasra);
        etContractDate = (EditText) findViewById(R.id.etContractDate);
        etArea = (EditText) findViewById(R.id.etArea);
        etStreet1 = (EditText) findViewById(R.id.etStreet1);
        etStreet2 = (EditText) findViewById(R.id.etStreet2);
        etOwner = (EditText) findViewById(R.id.etOwner);
        etMobile = (EditText) findViewById(R.id.etMobile);

        spOwnershipType = (Spinner) findViewById(R.id.spOwnershipType);
        spFpoCooperativeName = (Spinner) findViewById(R.id.spFpoCooperativeName);
        spState = (Spinner) findViewById(R.id.spState);
        spDistrict = (Spinner) findViewById(R.id.spDistrict);
        spBlock = (Spinner) findViewById(R.id.spBlock);
        spPanchayat = (Spinner) findViewById(R.id.spPanchayat);
        spVillage = (Spinner) findViewById(R.id.spVillage);
        spPincode = (Spinner) findViewById(R.id.spPincode);

        btnBack= (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        llNameMobile.setVisibility(View.GONE);
        /*---------------Start of code to set Filters and Input Type for Area and Contract Date-------------------------*/
        etArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etContractDate.setInputType(InputType.TYPE_NULL);

        /*---------------Start of code to set calendar for Contract Date-------------------------*/
        etContractDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(FarmBlock.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etContractDate.setText(dateFormatter_display.format(newDate.getTime()));

                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("Select Contract Date");
                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.show();
            }
        });
        /*---------------End of code to set calendar for Contract Date-------------------------*/

         /*------------------------Start of code for binding data in Spinner-----------------------*/
        spOwnershipType.setAdapter(DataAdapter("landtype", ""));
        spFpoCooperativeName.setAdapter(DataAdapter("fpoOrganizer", ""));
        spState.setAdapter(DataAdapter("state", ""));

        /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }

        /*---------------Start of code to bind form block details-------------------------*/
        if (!farmBlockUniqueId.equalsIgnoreCase("0")) {
            dba.openR();
            farmBlockData = dba.getPrimaryFarmBlockDetailsByUniqueId(farmBlockUniqueId);
            etStreet1.setText(farmBlockData.get(8));
            etStreet2.setText(farmBlockData.get(9));
            stateId = Integer.valueOf(farmBlockData.get(10));
            districtId = Integer.valueOf(farmBlockData.get(11));
            blockId = Integer.valueOf(farmBlockData.get(12));
            panchayatId = Integer.valueOf(farmBlockData.get(13));
            villageId = Integer.valueOf(farmBlockData.get(14));
            pincodeId = Integer.valueOf(farmBlockData.get(15));
            etArea.setText(farmBlockData.get(7));
            etKhasra.setText(farmBlockData.get(5));
            etKhata.setText(farmBlockData.get(4));
            if(!farmBlockData.get(6).equalsIgnoreCase(""))
                etContractDate.setText(common.convertToDisplayDateFormat(farmBlockData.get(6)));
            else
                etContractDate.setText("");
            fpoId = Integer.valueOf(farmBlockData.get(3));
            ownerShipId = Integer.valueOf(farmBlockData.get(2));

            int spsCnt = spState.getAdapter().getCount();
            for (int i = 0; i < spsCnt; i++) {
                if (((CustomType) spState.getItemAtPosition(i)).getId().equals(stateId))
                    spState.setSelection(i);
            }

            int spfpoCnt = spFpoCooperativeName.getAdapter().getCount();
            for (int i = 0; i < spfpoCnt; i++) {
                if (((CustomType) spFpoCooperativeName.getItemAtPosition(i)).getId().equals(fpoId))
                    spFpoCooperativeName.setSelection(i);
            }

            int spownshipCnt = spOwnershipType.getAdapter().getCount();
            for (int i = 0; i < spownshipCnt; i++) {
                if (((CustomType) spOwnershipType.getItemAtPosition(i)).getId().equals(ownerShipId))
                    spOwnershipType.setSelection(i);
            }

            if ((String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Contract") || (String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Leased"))
            {
                llNameMobile.setVisibility(View.VISIBLE);
                etOwner.setText(farmBlockData.get(17));
                etMobile.setText(farmBlockData.get(18));
            }
            else
            {
                llNameMobile.setVisibility(View.GONE);
                etOwner.setText("");
                etMobile.setText("");
            }
        }
        /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
        //on selection of ownership type
        spOwnershipType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

               if ((String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Contract") || (String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Leased"))
                {
                    llNameMobile.setVisibility(View.VISIBLE);
                }
                else {
                   llNameMobile.setVisibility(View.GONE);
                   etOwner.setText("");
                   etMobile.setText("");
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //on selection of state
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spDistrict.setAdapter(DataAdapter("district", String.valueOf(((CustomType) spState.getSelectedItem()).getId())));
                if (districtId > 0) {
                    int spdCnt = spDistrict.getAdapter().getCount();
                    for (int i = 0; i < spdCnt; i++) {
                        if (((CustomType) spDistrict.getItemAtPosition(i)).getId().equals(districtId))
                            spDistrict.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //on selection of district
        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spBlock.setAdapter(DataAdapter("addressblock", String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId())));
                if (blockId > 0) {
                    int spbCnt = spBlock.getAdapter().getCount();
                    for (int i = 0; i < spbCnt; i++) {
                        if (((CustomType) spBlock.getItemAtPosition(i)).getId().equals(blockId))
                            spBlock.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //on selection of block
        spBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spPanchayat.setAdapter(DataAdapter("panchayat", String.valueOf(((CustomType) spBlock.getSelectedItem()).getId())));

                if (panchayatId > 0) {
                    int sppCnt = spPanchayat.getAdapter().getCount();
                    for (int i = 0; i < sppCnt; i++) {
                        if (((CustomType) spPanchayat.getItemAtPosition(i)).getId().equals(panchayatId))
                            spPanchayat.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //on selection of panchayat
        spPanchayat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spVillage.setAdapter(DataAdapter("village", String.valueOf(((CustomType) spPanchayat.getSelectedItem()).getId())));

                if (villageId > 0) {
                    int spvCnt = spVillage.getAdapter().getCount();
                    for (int i = 0; i < spvCnt; i++) {
                        if (((CustomType) spVillage.getItemAtPosition(i)).getId().equals(villageId))
                            spVillage.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //on selection of village
        spVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spPincode.setAdapter(DataAdapter("villagepincode", String.valueOf(((CustomType) spVillage.getSelectedItem()).getId())));
                if (villageId > 0 && pincodeId > 0) {
                    int sppcdCnt = spPincode.getAdapter().getCount();
                    for (int i = 0; i < sppcdCnt; i++) {
                        if (((CustomType) spPincode.getItemAtPosition(i)).getId().equals(pincodeId))
                            spPincode.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move farm block to FarmBlockList page
                Intent intent = new Intent(FarmBlock.this, FarmBlockList.class);
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
    });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String isOwner="false";
                if ((String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Contract") || (String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Leased"))
                    isOwner="true";
                if ((spOwnershipType.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Ownership is mandatory!":"स्वामित्व अनिवार्य है!",5,1);
               /* else  if ((String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Contract") || (String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getName())).equalsIgnoreCase("Leased"))
                {*/
                    else if (isOwner.equalsIgnoreCase("true") && String.valueOf(etOwner.getText()).trim().equals(""))
                        common.showToast(lang.equalsIgnoreCase("en")?"Owner Name is mandatory!":"स्वामी का नाम अनिवार्य है!",5,1);
                    else if (isOwner.equalsIgnoreCase("true") && String.valueOf(etMobile.getText()).trim().equals(""))
                        common.showToast(lang.equalsIgnoreCase("en")?"Mobile# is mandatory!":"मोबाइल # अनिवार्य है!",5,1);
                    else if (isOwner.equalsIgnoreCase("true") && String.valueOf(etMobile.getText()).trim().length()<10)
                        common.showToast(lang.equalsIgnoreCase("en")?"Mobile# is invalid":"मोबाइल # अमान्य है",5,1);
                    else if (isOwner.equalsIgnoreCase("true") && !isValidPhone(String.valueOf(etMobile.getText()).trim()))
                        common.showToast(lang.equalsIgnoreCase("en")?"Mobile# is invalid":"मोबाइल # अमान्य है",5,1);
            /*}*/
                else if (String.valueOf(etKhata.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Khata# is mandatory!":"खाता # अनिवार्य है!",5,1);
                else if (String.valueOf(etKhasra.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Khasra# is mandatory!":"खसरा # अनिवार्य है!",5,1);
                else if (String.valueOf(etArea.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Area is mandatory!":"क्षेत्र अनिवार्य है!",5,1);
                else if (!Pattern.matches(fpRegex, etArea.getText()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Please enter valid Area!":"कृपया मान्य क्षेत्र दर्ज करें!",5,1);
                else if (Double.valueOf(String.valueOf(etArea.getText()).trim()) < .01)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot be less than .01!":"क्षेत्र .01 से कम नहीं हो सकता!",5,1);
                else if (String.valueOf(etStreet1.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Line 1 is mandatory!":"लाइन 1 अनिवार्य है!",5,1);
                else if ((spState.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"State is mandatory!":"राज्य अनिवार्य है!",5,1);
                else if (spDistrict.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"District is mandatory!":"जिला अनिवार्य है!",5,1);
                else if (spBlock.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Block is mandatory!":"ब्लॉक अनिवार्य है!",5,1);
                else if (spPanchayat.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Panchayat is mandatory!":"Panchayat is mandatory!",5,1);
                else if (spVillage.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Village is mandatory!":"गांव अनिवार्य है!",5,1);
                else if (spPincode.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Pincode is mandatory!":"पिनकोड अनिवार्य है!",5,1);
                else {
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(FarmBlock.this);
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

                        String dateString="";
                        if (String.valueOf(etContractDate.getText()).trim().equals(""))
                            dateString ="";
                        else {
                            Date date = new Date();
                            try {
                                date = dateFormatter_display.parse(etContractDate.getText().toString().trim());
                                dateString = dateFormatter_database.format(date);
                            } catch (ParseException e1) {
                            }
                        }

                        if (farmBlockUniqueId.equalsIgnoreCase("0")) {
                            //To Add farm block details
                            dba.open();
                            farmBlockUniqueId = UUID.randomUUID().toString();
                                    /*----------Code to insert data in Farm Block Table-----------------------*/
                            dba.insertFarmBlock(farmBlockUniqueId, farmerUniqueId, String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getId()), String.valueOf(((CustomType) spFpoCooperativeName.getSelectedItem()).getId()), etKhata.getText().toString().trim(), etKhasra.getText().toString().trim(), dateString, etArea.getText().toString().trim(), userId, etStreet1.getText().toString().trim(), etStreet2.getText().toString().trim(), String.valueOf(((CustomType) spState.getSelectedItem()).getId()), String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId()), String.valueOf(((CustomType) spBlock.getSelectedItem()).getId()), String.valueOf(((CustomType) spPanchayat.getSelectedItem()).getId()), String.valueOf(((CustomType) spVillage.getSelectedItem()).getId()), String.valueOf(((CustomType) spPincode.getSelectedItem()).getId()), longitudeN, latitudeN, accuracy, etOwner.getText().toString().trim(), etMobile.getText().toString().trim());
                            dba.close();
                        } else {
                            //To update farm block details
                            dba.open();
                            dba.updateFarmBlock(farmBlockUniqueId, String.valueOf(((CustomType) spOwnershipType.getSelectedItem()).getId()), String.valueOf(((CustomType) spFpoCooperativeName.getSelectedItem()).getId()), etKhata.getText().toString().trim(), etKhasra.getText().toString().trim(), dateString, etArea.getText().toString().trim(), etStreet1.getText().toString().trim(), etStreet2.getText().toString().trim(), String.valueOf(((CustomType) spState.getSelectedItem()).getId()), String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId()), String.valueOf(((CustomType) spBlock.getSelectedItem()).getId()), String.valueOf(((CustomType) spPanchayat.getSelectedItem()).getId()), String.valueOf(((CustomType) spVillage.getSelectedItem()).getId()), String.valueOf(((CustomType) spPincode.getSelectedItem()).getId()), longitudeN, latitudeN, accuracy, etOwner.getText().toString().trim(), etMobile.getText().toString().trim());
                            dba.close();
                        }
                        common.showToast(lang.equalsIgnoreCase("en")?"Farm Block details saved successfully.":"फार्म ब्लॉक विवरण सफलतापूर्वक सहेजा गया।",5,3);
                        //To move next farm block soil page
                        Intent intent = new Intent(FarmBlock.this, FarmBlockSoil.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("entryType", entryType);
                        intent.putExtra("farmerCode", farmerCode);
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
                Intent intent = new Intent(FarmBlock.this, FarmBlockList.class);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlock.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlock.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(FarmBlock.this, FarmBlockList.class);
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
