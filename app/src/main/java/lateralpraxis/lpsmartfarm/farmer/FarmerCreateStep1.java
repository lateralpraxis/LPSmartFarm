package lateralpraxis.lpsmartfarm.farmer;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.ImageLoadingUtils;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.CustomType;

public class FarmerCreateStep1 extends Activity {

    /*------------------------Start of code for fixed flag Declaration------------------------------*/

    private static final int PICK_Camera_IMAGE = 0;
    protected static final int GALLERY_REQUEST = 1;
    private static final int PICK_FSSAI_Camera_IMAGE = 3;
    private static final int PICK_Farmer_Camera_IMAGE = 5;
     /*------------------------End of code for fixed flag Declaration------------------------------*/

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
    private final Context mContext = this;
    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    Uri uri, uriFSSAI;
    Intent picIntent = null;
    File destination, filePBook;
    File destinationFarmer, fileFBook;
    File destinationfssai, fileFssai;
    Bitmap bitmap;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String contactnoPattern = "^((\\+)?(\\d{2}[-])?(\\d{10}){1})?(\\d{11}){0,1}?$";
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    private DatabaseAdapter dba;
    private UserSessionManager session;
    /*------------------------End of code for class Declaration------------------------------*/
    private Common common;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private EditText etFirstName, etMiddleName, etLastName, etFatherFirstName, etFatherMiddleName, etFatherLastName, etBirthDate, etMobile, etIFSC, etAccount, etStreet1, etStreet2, etTotalAcreage, etFSSAIRegDate, etFSSAIExpDate, etFSSAI, etMail, etAlternate1, etAlternate2;
    private TextView tvPassBookImage, tvFSSAIImage, tvFarmerImage, tvFarmerUniqueId;
    private Spinner spSalutation, spFatherSalutation, spState, spDistrict, spBlock, spPanchayat, spVillage, spCity, spPincode, spFarmerType, spEducationLevel, spLanguage, spSoilType, spIrrigationSystem, spRiver, spDam, spWaterSource, spElectricity;
    private LinearLayout llDistrict, llBlock, llPanchayat, llVillage, llCity, llPincode, llBankAttach, llBank, llExpDate, llRegDate, llFSSAIAttach, llAccount, llFSSAIHead;
    private RadioButton RadioDistrict, RadioCity, RadioMale, RadioFemale;
    private RadioGroup RadioAddressType, RadioGenderType;
    private Button btnUpload, btnViewAttach, btnFetchBank, btnSave, btnFSSAIUpload, btnViewFSSAIAttach, btnUploadPhoto, btnViewPhoto;
    /*------------------------End of code for controls Declaration------------------------------*/
    private DatePickerDialog dpkColdate;
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String fullfssaiPath, photoPath, fssaiphotoPath, uuidfssaiImg, newfullpassbookPath, newfullfssaiPath, newfullfarmerPath;
    private String level1Dir, level2Dir, level3Dir, fullpassbookPath, passbookphotoPath, uuidpassbookImg, fullfarmerPath, farmerphotoPath, uuidfarmerImg, strAddressType, userId, userRole;
    private int stateId = 0, districtId = 0, blockId = 0, panchayatId = 0, villageId = 0, pincodeId = 0, cityId = 0, farmerTypeId = 0, languageId = 0, educationlevelId = 0;
    private long regtime, exptime, birthTime;
    private SimpleDateFormat dateFormatter;
    private ImageLoadingUtils utils;
    private ArrayList<HashMap<String, Object>> RecordList;
    private int currentSelection = 0;
    private ArrayList<HashMap<String, String>> attachmentdetails;
    private String uploadedFilePathPassBook, uploadedFilePathFSSAI;
    private String farmerUniqueId;
    private ArrayList<String> farmerData;
    /*------------------------End of code for variable Declaration------------------------------*/
    private File[] listFilePassBook, listFileFarmer, listFileFSSAI;
    private String[] FilePathStringsPassBook, FilePathStringsFSSAI;
    private String[] FileNameStringsPassBook, FileNameStringsFSSAI;
    private String[] FilePathStringsFarmer, FileNameStringsFarmer;
    /*------------------------End of code for Regular Expression for Validating Decimal Values------------------------------*/


    /*---------------Start of code to generate random number and return the same-------------------------*/
    //Method to generate random number and return the same
    public static String random() {
        Random r = new Random();

        char[] choices = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "01234567890").toCharArray();

        StringBuilder salt = new StringBuilder(10);
        for (int i = 0; i < 10; ++i)
            salt.append(choices[r.nextInt(choices.length)]);
        return "img_" + salt.toString();
    }

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
        setContentView(R.layout.activity_farmer_create_step1);

    /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        HashMap<String, String> user = session.getLoginUserDetails();
        //Setting UserId
        userId = user.get(UserSessionManager.KEY_ID);
        userRole = user.get(UserSessionManager.KEY_USERROLES);
    /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //Code to set default value in  AddressType Variable
        strAddressType = "District Based";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
        }
     /*-----------Code to delete Temporaray File Data ---------------*/
        if (farmerUniqueId.equalsIgnoreCase("0")) {
            dba.open();
            dba.DeleteTempFiles();
            dba.close();
        }
      /*------------------------Start of code for controls Declaration--------------------------*/
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etMiddleName = (EditText) findViewById(R.id.etMiddleName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etFatherFirstName = (EditText) findViewById(R.id.etFatherFirstName);
        etFatherMiddleName = (EditText) findViewById(R.id.etFatherMiddleName);
        etFatherLastName = (EditText) findViewById(R.id.etFatherLastName);
        etIFSC = (EditText) findViewById(R.id.etIFSC);
        etAccount = (EditText) findViewById(R.id.etAccount);
        etStreet1 = (EditText) findViewById(R.id.etStreet1);
        etStreet2 = (EditText) findViewById(R.id.etStreet2);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etTotalAcreage = (EditText) findViewById(R.id.etTotalAcreage);
        etTotalAcreage.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etTotalAcreage.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

        etBirthDate = (EditText) findViewById(R.id.etBirthDate);
        etMail = (EditText) findViewById(R.id.etMail);
        etAlternate1 = (EditText) findViewById(R.id.etAlternate1);
        etAlternate2 = (EditText) findViewById(R.id.etAlternate2);
        tvPassBookImage = (TextView) findViewById(R.id.tvPassBookImage);
        tvFarmerImage = (TextView) findViewById(R.id.tvFarmerImage);
        tvFarmerUniqueId = (TextView) findViewById(R.id.tvFarmerUniqueId);
        spSalutation = (Spinner) findViewById(R.id.spSalutation);
        spFatherSalutation = (Spinner) findViewById(R.id.spFatherSalutation);
        spState = (Spinner) findViewById(R.id.spState);
        spDistrict = (Spinner) findViewById(R.id.spDistrict);
        spBlock = (Spinner) findViewById(R.id.spBlock);
        spPanchayat = (Spinner) findViewById(R.id.spPanchayat);
        spVillage = (Spinner) findViewById(R.id.spVillage);
        spCity = (Spinner) findViewById(R.id.spCity);
        spPincode = (Spinner) findViewById(R.id.spPincode);
        spFarmerType = (Spinner) findViewById(R.id.spFarmerType);
        spEducationLevel = (Spinner) findViewById(R.id.spEducationLevel);
        spLanguage = (Spinner) findViewById(R.id.spLanguage);
        spSoilType = (Spinner) findViewById(R.id.spSoilType);
        spIrrigationSystem = (Spinner) findViewById(R.id.spIrrigationSystem);
        spRiver = (Spinner) findViewById(R.id.spRiver);
        spDam = (Spinner) findViewById(R.id.spDam);
        spWaterSource = (Spinner) findViewById(R.id.spWaterSource);
        spElectricity = (Spinner) findViewById(R.id.spElectricity);
        llDistrict = (LinearLayout) findViewById(R.id.llDistrict);
        llBlock = (LinearLayout) findViewById(R.id.llBlock);
        llPanchayat = (LinearLayout) findViewById(R.id.llPanchayat);
        llVillage = (LinearLayout) findViewById(R.id.llVillage);
        llCity = (LinearLayout) findViewById(R.id.llCity);
        llPincode = (LinearLayout) findViewById(R.id.llPincode);
        llBankAttach = (LinearLayout) findViewById(R.id.llBankAttach);
        llBank = (LinearLayout) findViewById(R.id.llBank);
        llAccount = (LinearLayout) findViewById(R.id.llAccount);
        llFSSAIHead = (LinearLayout) findViewById(R.id.llFSSAIHead);

        etFSSAIRegDate = (EditText) findViewById(R.id.etFSSAIRegDate);
        etFSSAIExpDate = (EditText) findViewById(R.id.etFSSAIExpDate);
        etFSSAI = (EditText) findViewById(R.id.etFSSAI);
        tvFSSAIImage = (TextView) findViewById(R.id.tvFSSAIImage);
        llExpDate = (LinearLayout) findViewById(R.id.llExpDate);
        llRegDate = (LinearLayout) findViewById(R.id.llRegDate);
        llFSSAIAttach = (LinearLayout) findViewById(R.id.llFSSAIAttach);
        btnFSSAIUpload = (Button) findViewById(R.id.btnFSSAIUpload);
        btnViewFSSAIAttach = (Button) findViewById(R.id.btnViewFSSAIAttach);
        RadioDistrict = (RadioButton) findViewById(R.id.RadioDistrict);
        RadioCity = (RadioButton) findViewById(R.id.RadioCity);
        RadioMale = (RadioButton) findViewById(R.id.RadioMale);
        RadioFemale = (RadioButton) findViewById(R.id.RadioFemale);
        RadioAddressType = (RadioGroup) findViewById(R.id.RadioAddressType);
        RadioGenderType = (RadioGroup) findViewById(R.id.RadioGenderType);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnViewAttach = (Button) findViewById(R.id.btnViewAttach);
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        btnViewPhoto = (Button) findViewById(R.id.btnViewPhoto);
        btnSave = (Button) findViewById(R.id.btnSave);

        etBirthDate.setInputType(InputType.TYPE_NULL);
        /*---------------Start of code to set calendar for Birth Date-------------------------*/
        etBirthDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(FarmerCreateStep1.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etBirthDate.setText(dateFormatter.format(newDate.getTime()));
                        birthTime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("Select Birth date");

                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.show();
            }
        });
        /*---------------End of code to set calendar for Birth Date-------------------------*/

        /*--------------------------End of code for controls Declaration--------------------------*/

     /*------------------------Start of code for binding data in Spinner-----------------------*/
        spSalutation.setAdapter(DataAdapter("salutation", ""));
        spFatherSalutation.setAdapter(DataAdapter("fathersalutation", ""));
        spState.setAdapter(DataAdapter("state", ""));
        spFarmerType.setAdapter(DataAdapter("farmertype", ""));
        spEducationLevel.setAdapter(DataAdapter("educationlevel", ""));
        spLanguage.setAdapter(DataAdapter("language", ""));

        spSoilType.setAdapter(DataAdapter("soiltype", ""));
        spIrrigationSystem.setAdapter(DataAdapter("irrigationsystem", ""));
        spRiver.setAdapter(DataAdapter("nearestriver", ""));
        spDam.setAdapter(DataAdapter("nearestdam", ""));
        spWaterSource.setAdapter(DataAdapter("watersource", ""));
        spElectricity.setAdapter(DataAdapter("electricitysource", ""));

      /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
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

        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spBlock.setAdapter(DataAdapter("addressblock", String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId())));
                spCity.setAdapter(DataAdapter("city", String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId())));
                if (cityId > 0) {
                    int spcityCnt = spCity.getAdapter().getCount();
                    for (int i = 0; i < spcityCnt; i++) {
                        if (((CustomType) spCity.getItemAtPosition(i)).getId().equals(cityId))
                            spCity.setSelection(i);
                    }
                }
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

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spPincode.setAdapter(DataAdapter("citypincode", String.valueOf(((CustomType) spCity.getSelectedItem()).getId())));
                if (cityId > 0 && pincodeId > 0) {
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
        /*-----------End of code for binding data on Spinner Item Change-----------------------*/

        //Code to Validate Total Acreage Entered is Valid Number or Not
        etTotalAcreage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etTotalAcreage.getText())) {

                    } else
                        etTotalAcreage.setText("");

                }
            }
        });
        /*---------Start of code for validating email-----------------------*/
        etMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {

                }
                // onBlur
                else {
                    if (!etMail.getEditableText().toString().trim().matches(emailPattern) && etMail.getEditableText().toString().trim().length() != 0)
                        common.showToast("Invalid email address", 5,1);
                }
            }
        });
        /*---------End of code for validating email-----------------------*/

        /*---------Start of code for validating Mobile-----------------------*/
        etMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {

                }
                // onBlur
                else {
                    if (!etMobile.getEditableText().toString().trim().matches(contactnoPattern) && etMobile.getEditableText().toString().trim().length() < 10)
                        common.showToast("Invalid mobile", 5,1);
                }
            }
        });
        /*---------End of code for validating Mobile-----------------------*/
      /*---------------Click Event Of Radio Group to show / hide layouts-------------------------*/
        RadioAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = RadioAddressType.findViewById(checkedId);
                int index = RadioAddressType.indexOfChild(radioButton);
                spState.setSelection(0);
                spDistrict.setSelection(0);
                spBlock.setSelection(0);
                spPanchayat.setSelection(0);
                spVillage.setSelection(0);
                spCity.setSelection(0);
                spPincode.setSelection(0);
                if (index == 0) {
                    strAddressType = "District Based";
                    llCity.setVisibility(View.GONE);
                    llDistrict.setVisibility(View.VISIBLE);
                    llBlock.setVisibility(View.VISIBLE);
                    llPanchayat.setVisibility(View.VISIBLE);
                    llVillage.setVisibility(View.VISIBLE);
                } else {
                    strAddressType = "City Based";
                    llCity.setVisibility(View.VISIBLE);
                    llDistrict.setVisibility(View.VISIBLE);
                    llBlock.setVisibility(View.GONE);
                    llPanchayat.setVisibility(View.GONE);
                    llVillage.setVisibility(View.GONE);
                }
            }
        });
 /*---------------Start of code to set calendar for FSSAI Registration Date-------------------------*/
        etFSSAIRegDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(FarmerCreateStep1.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etFSSAIRegDate.setText(dateFormatter.format(newDate.getTime()));
                        regtime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("Select Registration date");
                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.show();
            }
        });
        /*---------------End of code to set calendar for FSSAI Registration Date-------------------------*/

        /*---------------Start of code to set calendar for FSSAI Expiry Date-------------------------*/
        etFSSAIExpDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(FarmerCreateStep1.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etFSSAIExpDate.setText(dateFormatter.format(newDate.getTime()));
                        exptime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("Select Registration date");
                mDatePicker.getDatePicker().setMinDate(new Date().getTime() - 1000);
                mDatePicker.show();
            }
        });
        /*---------------End of code to set calendar for FSSAI Expiry Date-------------------------*/
 /*---------------Start of code to set Text Watcher ON Account Number-------------------------*/
        TextWatcher textWatcherBank = new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (etAccount.getText().toString().trim().length() > 0) {
                    llBank.setVisibility(View.VISIBLE);
                    llBankAttach.setVisibility(View.VISIBLE);
                } else {
                    llBank.setVisibility(View.GONE);
                    llBankAttach.setVisibility(View.GONE);
                    tvPassBookImage.setText("");
                 /*   dba.open();
                    dba.DeleteTempFileByType("PassBook");
                    dba.close();*/
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (etAccount.getText().toString().trim().length() > 0) {
                    llBank.setVisibility(View.VISIBLE);
                    llBankAttach.setVisibility(View.VISIBLE);
                } else {
                    llBank.setVisibility(View.GONE);
                    llBankAttach.setVisibility(View.GONE);
                    tvPassBookImage.setText("");
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etAccount.getText().toString().trim().length() > 0) {
                    llBank.setVisibility(View.VISIBLE);
                    llBankAttach.setVisibility(View.VISIBLE);
                } else {
                    llBank.setVisibility(View.GONE);
                    llBankAttach.setVisibility(View.GONE);
                    tvPassBookImage.setText("");
                }
            }
        };
        etAccount.addTextChangedListener(textWatcherBank);
        /*---------------End of code to set Text Watcher ON Account Number-------------------------*/

        /*---------------Start of code to set Text Watcher ON FSSAI Number-------------------------*/
        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (etFSSAI.getText().toString().trim().length() == 14) {
                    llExpDate.setVisibility(View.VISIBLE);
                    llRegDate.setVisibility(View.VISIBLE);
                    llFSSAIAttach.setVisibility(View.VISIBLE);
                } else {
                    etFSSAIExpDate.setText("");
                    etFSSAIRegDate.setText("");
                    tvFSSAIImage.setText("");
                    llExpDate.setVisibility(View.GONE);
                    llRegDate.setVisibility(View.GONE);
                    llFSSAIAttach.setVisibility(View.GONE);
                    /*dba.open();
                    dba.DeleteTempFileByType("FSSAI");
                    dba.close();*/
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (etFSSAI.getText().toString().trim().length() == 14) {
                    llExpDate.setVisibility(View.VISIBLE);
                    llRegDate.setVisibility(View.VISIBLE);
                    llFSSAIAttach.setVisibility(View.VISIBLE);
                } else {
                    etFSSAIExpDate.setText("");
                    etFSSAIRegDate.setText("");
                    tvFSSAIImage.setText("");
                    llExpDate.setVisibility(View.GONE);
                    llRegDate.setVisibility(View.GONE);
                    llFSSAIAttach.setVisibility(View.GONE);
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etFSSAI.getText().toString().trim().length() == 14) {
                    llExpDate.setVisibility(View.VISIBLE);
                    llRegDate.setVisibility(View.VISIBLE);
                    llFSSAIAttach.setVisibility(View.VISIBLE);
                } else {
                    etFSSAIExpDate.setText("");
                    etFSSAIRegDate.setText("");
                    tvFSSAIImage.setText("");
                    llExpDate.setVisibility(View.GONE);
                    llRegDate.setVisibility(View.GONE);
                    llFSSAIAttach.setVisibility(View.GONE);
                }
            }
        };
        etFSSAI.addTextChangedListener(textWatcher);
        /*---------------End of code to set Text Watcher ON FSSAI Number-------------------------*/
        /*---------------Start of code to set Click Event for Button Upload-------------------------*/
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvFarmerImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle("Attach Farmer Photo");
                    builder1.setMessage("Are you sure, you want to remove existing Farmer picture and upload new Farmer picture?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvFarmerImage.setText("");
                                    btnViewPhoto.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("Farmer");
                                    dba.close();
                                    startFarmerDialog();
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
                } else
                    startFarmerDialog();
            }
        });
        /*---------------End of code to set Click Event for Button Upload-------------------------*/
/*---------------Start of code to set Click Event for Button Upload-------------------------*/
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvPassBookImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle("Attach Bank Passbook");
                    builder1.setMessage("Are you sure, you want to remove existing Passbook picture and upload new Passbook picture?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvPassBookImage.setText("");
                                    btnViewAttach.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("PassBook");
                                    dba.close();
                                    startPassbookDialog();
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
                } else
                    startPassbookDialog();
            }
        });
        /*---------------End of code to set Click Event for Button Upload-------------------------*/

         /*---------------Start of code to set Click Event for Button FSSAI Upload-------------------------*/
        btnFSSAIUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvFSSAIImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle("Attach FSSAI Proof");
                    builder1.setMessage("Are you sure, you want to remove existing FSSAI Proof picture and upload new FSSAI Proof?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvFSSAIImage.setText("");
                                    btnViewFSSAIAttach.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("FSSAI");
                                    dba.close();
                                    startFSSAIDialog();
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
                } else
                    startFSSAIDialog();
            }
        });
        /*---------------End of code to set Click Event for Button FSSAI Upload-------------------------*/

        /*---------------Start of code to set Click Event for Viewing Passbook Attachment-------------------------*/
        btnViewPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {

                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!");
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempAttachment("Farmer");
                        if (attachmentdetails.size() > 0) {
                            for (HashMap<String, String> hashMap : attachmentdetails) {

                                for (String key : hashMap.keySet()) {

                                    if (key.equals("FileName"))
                                        uploadedFilePathPassBook = hashMap.get(key);
                                }
                            }

                            File file = new File(uploadedFilePathPassBook);
                            fileFBook = new File(file.getParent());
                        }
                    }

                    if (fileFBook.isDirectory()) {

                        listFileFarmer = fileFBook.listFiles(new FilenameFilter() {
                            public boolean accept(File directory,
                                                  String fileName) {
                                return fileName.endsWith(".jpeg")
                                        || fileName.endsWith(".bmp")
                                        || fileName.endsWith(".jpg")
                                        || fileName.endsWith(".png")
                                        || fileName.endsWith(".gif");
                            }
                        });
                        // Create a String array for FilePathStrings
                        FilePathStringsFarmer = new String[listFileFarmer.length];
                        // Create a String array for FileNameStrings
                        FileNameStringsFarmer = new String[listFileFarmer.length];

                        for (int i = 0; i < listFileFarmer.length; i++) {

                            FilePathStringsFarmer[i] = listFileFarmer[i].getAbsolutePath();
                            // Get the name image file
                            FileNameStringsFarmer[i] = listFileFarmer[i].getName();

                            Intent i1 = new Intent(FarmerCreateStep1.this,
                                    ViewImage.class);
                            // Pass String arrays FilePathStrings
                            i1.putExtra("filepath", FilePathStringsFarmer);
                            // Pass String arrays FileNameStrings
                            i1.putExtra("filename", FileNameStringsFarmer);
                            // Pass click position
                            i1.putExtra("position", 0);
                            startActivity(i1);
                            /* } */
                        }
                    }

                } catch (Exception except) {
                    //except.printStackTrace();
                    common.showAlert(FarmerCreateStep1.this, "Error: " + except.getMessage(), false);

                }
            }
        });
        /*---------------End of code to set Click Event for Viewing Passbook Attachment-------------------------*/

       /*---------------Start of code to set Click Event for Viewing Passbook Attachment-------------------------*/
        btnViewAttach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {

                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!");
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempAttachment("PassBook");
                        if (attachmentdetails.size() > 0) {
                            for (HashMap<String, String> hashMap : attachmentdetails) {

                                for (String key : hashMap.keySet()) {

                                    if (key.equals("FileName"))
                                        uploadedFilePathPassBook = hashMap.get(key);
                                }
                            }

                            File file = new File(uploadedFilePathPassBook);
                            filePBook = new File(file.getParent());
                        }
                    }

                    if (filePBook.isDirectory()) {

                        listFilePassBook = filePBook.listFiles(new FilenameFilter() {
                            public boolean accept(File directory,
                                                  String fileName) {
                                return fileName.endsWith(".jpeg")
                                        || fileName.endsWith(".bmp")
                                        || fileName.endsWith(".jpg")
                                        || fileName.endsWith(".png")
                                        || fileName.endsWith(".gif");
                            }
                        });
                        // Create a String array for FilePathStrings
                        FilePathStringsPassBook = new String[listFilePassBook.length];
                        // Create a String array for FileNameStrings
                        FileNameStringsPassBook = new String[listFilePassBook.length];

                        for (int i = 0; i < listFilePassBook.length; i++) {

                            FilePathStringsPassBook[i] = listFilePassBook[i].getAbsolutePath();
                            // Get the name image file
                            FileNameStringsPassBook[i] = listFilePassBook[i].getName();

                            Intent i1 = new Intent(FarmerCreateStep1.this,
                                    ViewImage.class);
                            // Pass String arrays FilePathStrings
                            i1.putExtra("filepath", FilePathStringsPassBook);
                            // Pass String arrays FileNameStrings
                            i1.putExtra("filename", FileNameStringsPassBook);
                            // Pass click position
                            i1.putExtra("position", 0);
                            startActivity(i1);
                            /* } */
                        }
                    }

                } catch (Exception except) {
                    //except.printStackTrace();
                    common.showAlert(FarmerCreateStep1.this, "Error: " + except.getMessage(), false);

                }
            }
        });
        /*---------------End of code to set Click Event for Viewing Passbook Attachment-------------------------*/

        /*---------------Start of code to set Click Event for Viewing FSSAI Attachment-------------------------*/
        btnViewFSSAIAttach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {

                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!",5,0);
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempAttachment("FSSAI");
                        if (attachmentdetails.size() > 0) {
                            for (HashMap<String, String> hashMap : attachmentdetails) {

                                for (String key : hashMap.keySet()) {

                                    if (key.equals("FileName"))
                                        uploadedFilePathFSSAI = hashMap.get(key);

                                }
                            }

                            File file = new File(uploadedFilePathFSSAI);
                            fileFssai = new File(file.getParent());
                        }
                    }

                    if (fileFssai.isDirectory()) {

                        listFileFSSAI = fileFssai.listFiles(new FilenameFilter() {
                            public boolean accept(File directory,
                                                  String fileName) {
                                return fileName.endsWith(".jpeg")
                                        || fileName.endsWith(".bmp")
                                        || fileName.endsWith(".jpg")
                                        || fileName.endsWith(".png")
                                        || fileName.endsWith(".gif");
                            }
                        });
                        // Create a String array for FilePathStrings
                        FilePathStringsFSSAI = new String[listFileFSSAI.length];
                        // Create a String array for FileNameStrings
                        FileNameStringsFSSAI = new String[listFileFSSAI.length];

                        for (int i = 0; i < listFileFSSAI.length; i++) {

                            FilePathStringsFSSAI[i] = listFileFSSAI[i].getAbsolutePath();
                            // Get the name image file
                            FileNameStringsFSSAI[i] = listFileFSSAI[i].getName();

                            Intent i1 = new Intent(FarmerCreateStep1.this,
                                    ViewImage.class);
                            // Pass String arrays FilePathStrings
                            i1.putExtra("filepath", FilePathStringsFSSAI);
                            // Pass String arrays FileNameStrings
                            i1.putExtra("filename", FileNameStringsFSSAI);
                            // Pass click position
                            i1.putExtra("position", 0);
                            startActivity(i1);
                            /* } */
                        }
                    }

                } catch (Exception except) {
                    //except.printStackTrace();
                    common.showAlert(FarmerCreateStep1.this, "Error: " + except.getMessage(), false);

                }
            }
        });
        /*---------------End of code to set Click Event for Viewing FSSAI Attachment-------------------------*/
        /*---------------Start of code to set Click Event for Button Save & Next-------------------------*/
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etTotalAcreage.clearFocus();

                if ((spLanguage.getSelectedItemPosition() == 0))
                    common.showToast("Language is mandatory.",5,1);
                else if ((spSalutation.getSelectedItemPosition() == 0))
                    common.showToast("Farmer Salutation is mandatory.",5,1);
                else if (String.valueOf(etFirstName.getText()).trim().equals(""))
                    common.showToast("Farmer First Name is mandatory.",5,1);
                else if (String.valueOf(etLastName.getText()).trim().equals(""))
                    common.showToast("Farmer Last Name is mandatory.",5,1);
                else if (String.valueOf(tvFarmerImage.getText()).trim().equals(""))
                    common.showToast("Farmer Photo is mandatory.",5,1);
                else if ((spFatherSalutation.getSelectedItemPosition() == 0))
                    common.showToast("Father Salutation is mandatory.",5,1);
                else if (String.valueOf(etFatherFirstName.getText()).trim().equals(""))
                    common.showToast("Father First Name is mandatory.",5,1);
                else if (String.valueOf(etFatherLastName.getText()).trim().equals(""))
                    common.showToast("Father Last Name is mandatory.",5,1);
                else if (String.valueOf(etBirthDate.getText()).trim().equals(""))
                    common.showToast("Birth date is mandatory.",5,1);
                else if ((spEducationLevel.getSelectedItemPosition() == 0))
                    common.showToast("Education Level is mandatory.",5,1);
                else if ((spFarmerType.getSelectedItemPosition() == 0))
                    common.showToast("Farmer Type is mandatory.",5,1);
                else if (String.valueOf(etTotalAcreage.getText()).trim().equals(""))
                    common.showToast("Total Acreage is mandatory.",5,1);
                else if (!Pattern.matches(fpRegex, etTotalAcreage.getText()))
                    common.showToast("Please enter valid Total Acreage.",5,1);
                else if (Double.valueOf(String.valueOf(etTotalAcreage.getText()).trim()) < .01)
                    common.showToast("Total Acreage cannot be less than .01.",5,1);
                else if (String.valueOf(etMobile.getText()).trim().equals(""))
                    common.showToast("Mobile number is mandatory.",5,1);
                else if (String.valueOf(etMobile.getText()).trim().length()<10)
                    common.showToast("Mobile number is invalid.",5,1);
                else if (!isValidPhone(String.valueOf(etMobile.getText()).trim()))
                    common.showToast("Mobile number is invalid.",5,1);
                else if (!String.valueOf(etAlternate1.getText()).trim().equals("") && !isValidPhone(String.valueOf(etAlternate1.getText()).trim()))
                    common.showToast("Alternate number 1 is invalid.",5,1);
                else if (!String.valueOf(etAlternate1.getText()).trim().equals("") && String.valueOf(etAlternate1.getText()).trim().length()<10)
                    common.showToast("Alternate number 1 is invalid.",5,1);
                else if (!String.valueOf(etAlternate2.getText()).trim().equals("") && !isValidPhone(String.valueOf(etAlternate2.getText()).trim()))
                    common.showToast("Alternate number 2 is invalid.",5,1);
                else if (!String.valueOf(etAlternate2.getText()).trim().equals("") && String.valueOf(etAlternate2.getText()).trim().length()<10)
                    common.showToast("Alternate number 2 is invalid.",5,1);
                else if (!String.valueOf(etMail.getText()).trim().equals("") && !etMail.getEditableText().toString().trim().matches(emailPattern))
                    common.showToast("Please enter valid Email.",5,1);
                else if (String.valueOf(etStreet1.getText()).trim().equals(""))
                    common.showToast("Line 1 is mandatory.",5,1);
                else if ((spState.getSelectedItemPosition() == 0))
                    common.showToast("State is mandatory.",5,1);
                else if (spDistrict.getSelectedItemPosition() == 0)
                    common.showToast("District is mandatory.",5,1);
                else if (strAddressType == "District Based" && spBlock.getSelectedItemPosition() == 0)
                    common.showToast("Block is mandatory.",5,1);
                else if (strAddressType == "District Based" && spPanchayat.getSelectedItemPosition() == 0)
                    common.showToast("Panchayat is mandatory.",5,1);
                else if (strAddressType == "District Based" && spVillage.getSelectedItemPosition() == 0)
                    common.showToast("Village is mandatory.",5,1);
                else if (strAddressType == "District Based" && spPincode.getSelectedItemPosition() == 0)
                    common.showToast("Pincode is mandatory.",5,1);
                else if (strAddressType == "City Based" && spCity.getSelectedItemPosition() == 0)
                    common.showToast("City is mandatory.",5,1);
                else if (strAddressType == "City Based" && spPincode.getSelectedItemPosition() == 0)
                    common.showToast("Pincode is mandatory.",5,1);
                else if (etAccount.getText().toString().trim().length() > 0 && String.valueOf(etIFSC.getText()).trim().equals(""))
                    common.showToast("IFSC Code is mandatory.",5,1);
                else if (etAccount.getText().toString().trim().length() > 0 && String.valueOf(tvPassBookImage.getText()).trim().equals(""))
                    common.showToast("Passbook attachment is mandatory.",5,1);
                else if (etFSSAI.getText().toString().trim().length() > 0 && etFSSAI.getText().toString().trim().length() < 14)
                    common.showToast("FSSAI number must have 14 digits.",5,1);
                else if (etFSSAI.getText().toString().trim().length() == 14 && String.valueOf(etFSSAIRegDate.getText()).trim().equals(""))
                    common.showToast("Registration Date is mandatory.",5,1);
                else if (etFSSAI.getText().toString().trim().length() == 14 && String.valueOf(etFSSAIExpDate.getText()).trim().equals(""))
                    common.showToast("Expiry Date is mandatory.",5,1);
                else if (!TextUtils.isEmpty(etAlternate1.getText().toString()) && etAlternate1.getText().toString().equalsIgnoreCase(etMobile.getText().toString())) {
                    common.showToast("Mobile and Alternate 1 cannot be same.",5,1);
                } else if (!TextUtils.isEmpty(etAlternate2.getText().toString()) && etAlternate2.getText().toString().equalsIgnoreCase(etMobile.getText().toString())) {
                    common.showToast("Mobile and Alternate 2 cannot be same.",5,1);
                } else if (!TextUtils.isEmpty(etAlternate2.getText().toString()) && !TextUtils.isEmpty(etAlternate1.getText().toString()) && etAlternate1.getText().toString().equalsIgnoreCase(etAlternate2.getText().toString())) {
                    common.showToast("Alternate 1 and Alternate 2 cannot be same.",5,1);
                } else {
                   /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmerCreateStep1.this);
                    // set title
                    alertDialogBuilder.setTitle("Confirmation");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to save farmer details?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {*/
                    latitude = "NA";
                    longitude = "NA";
                    accuracy = "NA";
                    latitudeN = "NA";
                    longitudeN = "NA";
                    // create class object
                    gps = new GPSTracker(FarmerCreateStep1.this);
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
                        String strstateId = "0", strdistrictId = "0", strblockId = "0", strpanchayatId = "0", strvillageId = "0", strpincodeId = "0", strcityId = "0";
                        if (strAddressType.equalsIgnoreCase("District Based")) {

                            strdistrictId = String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId());
                            strblockId = String.valueOf(((CustomType) spBlock.getSelectedItem()).getId());
                            strpanchayatId = String.valueOf(((CustomType) spPanchayat.getSelectedItem()).getId());
                            strvillageId = String.valueOf(((CustomType) spVillage.getSelectedItem()).getId());

                        } else {
                            strdistrictId = String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId());
                            strcityId = String.valueOf(((CustomType) spCity.getSelectedItem()).getId());
                        }
                        strstateId = String.valueOf(((CustomType) spState.getSelectedItem()).getId());
                        strpincodeId = String.valueOf(((CustomType) spPincode.getSelectedItem()).getId());
                        dba.open();
                        String gender = "";
                        if (RadioMale.isChecked())
                            gender = "Male";
                        else if (RadioFemale.isChecked())
                            gender = "Female";

                                    /*----------Code to insert data in Temp Farmer and Address Table-----------------------*/
                        if (farmerUniqueId.equalsIgnoreCase("0")) {
                            if (farmerUniqueId.equalsIgnoreCase("0"))
                                farmerUniqueId = UUID.randomUUID().toString();
                            dba.insertFarmerTemp(farmerUniqueId, String.valueOf(((CustomType) spEducationLevel.getSelectedItem()).getId()),
                                    String.valueOf(((CustomType) spFarmerType.getSelectedItem()).getId()), String.valueOf(((CustomType) spSalutation.getSelectedItem()).getName()),
                                    etFirstName.getText().toString().trim(), etMiddleName.getText().toString().trim(),
                                    etLastName.getText().toString().trim(),
                                    String.valueOf(((CustomType) spFatherSalutation.getSelectedItem()).getName()),
                                    etFatherFirstName.getText().toString().trim(), etFatherMiddleName.getText().toString().trim(),
                                    etFatherLastName.getText().toString().trim(), etMail.getText().toString().trim(), etMobile.getText().toString().trim(),
                                    etAlternate1.getText().toString().trim(), etAlternate2.getText().toString().trim(),
                                    etBirthDate.getText().toString().trim(), gender,
                                    etAccount.getText().toString().trim(), etIFSC.getText().toString().trim(),
                                    etTotalAcreage.getText().toString().trim(), etFSSAI.getText().toString().trim(),
                                    etFSSAIRegDate.getText().toString().trim(), etFSSAIExpDate.getText().toString().trim(),
                                    strAddressType, etStreet1.getText().toString().trim(),
                                    etStreet2.getText().toString().trim(), strstateId,
                                    strdistrictId, strblockId, strpanchayatId, strvillageId, strcityId, strpincodeId, userId, String.valueOf(((CustomType) spLanguage.getSelectedItem()).getId()), String.valueOf(((CustomType) spSoilType.getSelectedItem()).getId()), String.valueOf(((CustomType) spIrrigationSystem.getSelectedItem()).getId()), String.valueOf(((CustomType) spRiver.getSelectedItem()).getId()), String.valueOf(((CustomType) spDam.getSelectedItem()).getId()), String.valueOf(((CustomType) spWaterSource.getSelectedItem()).getId()), String.valueOf(((CustomType) spElectricity.getSelectedItem()).getId()), longitudeN, latitudeN, accuracy);

                            dba.close();
                        } else {
                            dba.updateTempFarmer(farmerUniqueId, String.valueOf(((CustomType) spEducationLevel.getSelectedItem()).getId()),
                                    String.valueOf(((CustomType) spFarmerType.getSelectedItem()).getId()), String.valueOf(((CustomType) spSalutation.getSelectedItem()).getName()),
                                    etFirstName.getText().toString().trim(), etMiddleName.getText().toString().trim(),
                                    etLastName.getText().toString().trim(),
                                    String.valueOf(((CustomType) spFatherSalutation.getSelectedItem()).getName()),
                                    etFatherFirstName.getText().toString().trim(), etFatherMiddleName.getText().toString().trim(),
                                    etFatherLastName.getText().toString().trim(), etMail.getText().toString().trim(), etMobile.getText().toString().trim(),
                                    etAlternate1.getText().toString().trim(), etAlternate2.getText().toString().trim(),
                                    etBirthDate.getText().toString().trim(), gender,
                                    etAccount.getText().toString().trim(), etIFSC.getText().toString().trim(),
                                    etTotalAcreage.getText().toString().trim(), etFSSAI.getText().toString().trim(),
                                    etFSSAIRegDate.getText().toString().trim(), etFSSAIExpDate.getText().toString().trim(),
                                    strAddressType, etStreet1.getText().toString().trim(),
                                    etStreet2.getText().toString().trim(), strstateId,
                                    strdistrictId, strblockId, strpanchayatId, strvillageId, strcityId, strpincodeId, userId, String.valueOf(((CustomType) spLanguage.getSelectedItem()).getId()), String.valueOf(((CustomType) spSoilType.getSelectedItem()).getId()), String.valueOf(((CustomType) spIrrigationSystem.getSelectedItem()).getId()), String.valueOf(((CustomType) spRiver.getSelectedItem()).getId()), String.valueOf(((CustomType) spDam.getSelectedItem()).getId()), String.valueOf(((CustomType) spWaterSource.getSelectedItem()).getId()), String.valueOf(((CustomType) spElectricity.getSelectedItem()).getId()), longitudeN, latitudeN, accuracy);

                            dba.close();
                        }

                        Intent intent = new Intent(FarmerCreateStep1.this, ActivityBlockAssignment.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        startActivity(intent);
                        finish();
                        //}
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }





                                /*}
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
                    alertDialog.show();*/
                }
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/
        //Code to Hide City Layout By Default
        llCity.setVisibility(View.GONE);
        llExpDate.setVisibility(View.GONE);
        llRegDate.setVisibility(View.GONE);
        llFSSAIAttach.setVisibility(View.GONE);
        llBank.setVisibility(View.GONE);
        llBankAttach.setVisibility(View.GONE);


        /*---------------Start of code to bind Farmer details-------------------------*/
        if (!farmerUniqueId.equalsIgnoreCase("0")) {
            dba.openR();
            farmerData = dba.getTempFarmerDetailsByUniqueId(farmerUniqueId);
            tvFarmerUniqueId.setText(farmerUniqueId);
            etStreet1.setText(farmerData.get(23));
            etStreet2.setText(farmerData.get(24));
            etFirstName.setText(farmerData.get(4));
            etMiddleName.setText(farmerData.get(5));
            etLastName.setText(farmerData.get(6));
            etFatherFirstName.setText(farmerData.get(8));
            etFatherMiddleName.setText(farmerData.get(9));
            etFatherLastName.setText(farmerData.get(10));
            etMail.setText(farmerData.get(11));
            etMobile.setText(farmerData.get(12));
            etAlternate1.setText(farmerData.get(13));
            etAlternate2.setText(farmerData.get(14));
            etBirthDate.setText(farmerData.get(15));
            etTotalAcreage.setText(farmerData.get(19));
            if (farmerData.get(16).equalsIgnoreCase("Male")) {
                RadioMale.setChecked(true);
                RadioFemale.setChecked(false);
            } else {
                RadioMale.setChecked(false);
                RadioFemale.setChecked(true);
            }

            if (farmerData.get(32).equalsIgnoreCase("District Based")) {
                RadioDistrict.setChecked(true);
                RadioCity.setChecked(false);
            } else {
                RadioDistrict.setChecked(false);
                RadioCity.setChecked(true);
            }

            farmerTypeId = Integer.valueOf(farmerData.get(2));
            languageId = Integer.valueOf(farmerData.get(33));
            educationlevelId = Integer.valueOf(farmerData.get(1));
            stateId = Integer.valueOf(farmerData.get(25));
            districtId = Integer.valueOf(farmerData.get(26));
            blockId = Integer.valueOf(farmerData.get(27));
            panchayatId = Integer.valueOf(farmerData.get(28));
            villageId = Integer.valueOf(farmerData.get(29));
            pincodeId = Integer.valueOf(farmerData.get(31));
            cityId = Integer.valueOf(farmerData.get(30));

            int spsCnt = spState.getAdapter().getCount();
            for (int i = 0; i < spsCnt; i++) {
                if (((CustomType) spState.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(25))))
                    spState.setSelection(i);
            }

            int ftypeCount = spFarmerType.getAdapter().getCount();
            for (int i = 0; i < ftypeCount; i++) {
                if (((CustomType) spFarmerType.getItemAtPosition(i)).getId().equals(farmerTypeId))
                    spFarmerType.setSelection(i);
            }

            int langCount = spLanguage.getAdapter().getCount();
            for (int i = 0; i < langCount; i++) {
                if (((CustomType) spLanguage.getItemAtPosition(i)).getId().equals(languageId))
                    spLanguage.setSelection(i);
            }

            int soilTypeCount = spSoilType.getAdapter().getCount();
            for (int i = 0; i < soilTypeCount; i++) {
                if (((CustomType) spSoilType.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(34))))
                    spSoilType.setSelection(i);
            }

            int irrisCount = spIrrigationSystem.getAdapter().getCount();
            for (int i = 0; i < irrisCount; i++) {
                if (((CustomType) spIrrigationSystem.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(35))))
                    spIrrigationSystem.setSelection(i);
            }

            int rivCount = spRiver.getAdapter().getCount();
            for (int i = 0; i < rivCount; i++) {
                if (((CustomType) spRiver.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(36))))
                    spRiver.setSelection(i);
            }

            int damCount = spDam.getAdapter().getCount();
            for (int i = 0; i < damCount; i++) {
                if (((CustomType) spDam.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(37))))
                    spDam.setSelection(i);
            }

            int wsCount = spWaterSource.getAdapter().getCount();
            for (int i = 0; i < wsCount; i++) {
                if (((CustomType) spWaterSource.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(38))))
                    spWaterSource.setSelection(i);
            }

            int esCount = spElectricity.getAdapter().getCount();
            for (int i = 0; i < esCount; i++) {
                if (((CustomType) spElectricity.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(39))))
                    spElectricity.setSelection(i);
            }

            int eduCount = spEducationLevel.getAdapter().getCount();
            for (int i = 0; i < eduCount; i++) {
                if (((CustomType) spEducationLevel.getItemAtPosition(i)).getId().equals(educationlevelId))
                    spEducationLevel.setSelection(i);
            }

            if (!TextUtils.isEmpty(farmerData.get(3))) {
                int salCnt = spSalutation.getAdapter().getCount();
                for (int i = 0; i < salCnt; i++) {
                    if (((CustomType) spSalutation.getItemAtPosition(i)).toString().equals(farmerData.get(3)))
                        spSalutation.setSelection(i);
                }
            }

            if (!TextUtils.isEmpty(farmerData.get(7))) {
                int salFathCnt = spFatherSalutation.getAdapter().getCount();
                for (int i = 0; i < salFathCnt; i++) {
                    if (((CustomType) spFatherSalutation.getItemAtPosition(i)).toString().equals(farmerData.get(7)))
                        spFatherSalutation.setSelection(i);
                }
            }
/*        dba.open();
        dba.Insert_MainDocTempDoc(farmerUniqueId);
        dba.close();*/
            if (!TextUtils.isEmpty(farmerData.get(40))) {
                btnViewPhoto.setVisibility(View.VISIBLE);
                tvFarmerImage.setText(farmerData.get(40).substring(farmerData.get(40).lastIndexOf("/") + 1));
            }
            if (!TextUtils.isEmpty(farmerData.get(41))) {
                btnViewAttach.setVisibility(View.VISIBLE);
                tvPassBookImage.setText(farmerData.get(41).substring(farmerData.get(41).lastIndexOf("/") + 1));
            }
            if (!TextUtils.isEmpty(farmerData.get(42))) {
                btnViewFSSAIAttach.setVisibility(View.VISIBLE);
                tvFSSAIImage.setText(farmerData.get(42).substring(farmerData.get(42).lastIndexOf("/") + 1));
            }
        }
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/

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
                Intent intent = new Intent(this, ActivityHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmerCreateStep1.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmerCreateStep1.this, ActivityHome.class);
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

    //Code to open camera for Attaching Farmer Photo
    private void startFarmerDialog() {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(FarmerCreateStep1.this);
            builderSingle.setTitle("Select Image source");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    FarmerCreateStep1.this,
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Capture Image");
            arrayAdapter.add("Select from Gallery");


            builderSingle.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            if (strName.equals("Capture Image")) {
                                //Setting directory structure
                                uuidfarmerImg = UUID.randomUUID().toString();
                                level1Dir = "LPSMARTFARM";
                                level2Dir = level1Dir + "/" + "Farmer";
                                level3Dir = level2Dir + "/" + uuidfarmerImg;
                                String imageName = random() + ".jpg";
                                fullfarmerPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                                destinationFarmer = new File(fullfarmerPath, imageName);
                                //Check if directory exists else create directory
                                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                                    //Code to open camera intent
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(destinationFarmer));
                                    startActivityForResult(intent, PICK_Farmer_Camera_IMAGE);

                                    btnViewAttach.setVisibility(View.GONE);
                                }
                            } else if (strName.equals("Select from Gallery")) {
                                picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                picIntent.putExtra("return_data", true);
                                startActivityForResult(picIntent, GALLERY_REQUEST);
                                //finish();
                            } else {
                                common.showToast("No File available for review.");
                            }
                        }
                    });
            builderSingle.show();
    }

    //Code to open camera for Attaching Bank Passbook
    private void startPassbookDialog() {

        //Setting directory structure
        uuidpassbookImg = UUID.randomUUID().toString();
        level1Dir = "LPSMARTFARM";
        level2Dir = level1Dir + "/" + "PassBook";
        level3Dir = level2Dir + "/" + uuidpassbookImg;
        String imageName = random() + ".jpg";
        fullpassbookPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
        destination = new File(fullpassbookPath, imageName);
        //Check if directory exists else create directory
        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
            //Code to open camera intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(destination));
            startActivityForResult(intent, PICK_Camera_IMAGE);

            btnViewAttach.setVisibility(View.GONE);
        }

    }

    //Code to open camera for Attaching FSSAI Proof
    private void startFSSAIDialog() {

        //Setting directory structure
        uuidfssaiImg = UUID.randomUUID().toString();
        level1Dir = "LPSMARTFARM";
        level2Dir = level1Dir + "/" + "FSSAI";
        level3Dir = level2Dir + "/" + uuidfssaiImg;
        String imagefssaiName = random() + ".jpg";
        fullfssaiPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
        destinationfssai = new File(fullfssaiPath, imagefssaiName);
        //Check if directory exists else create directory
        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
            //Code to open camera intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(destinationfssai));
            startActivityForResult(intent, PICK_FSSAI_Camera_IMAGE);


            btnViewFSSAIAttach.setVisibility(View.VISIBLE);
        }
    }

    //Code to be executed after action done for attaching
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data == null) {
            //Reset image name and hide reset button
            tvPassBookImage.setText("");
        } else if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    if (uri != null) {
                        photoPath = getRealPathFromUri(uri);
                        uuidfarmerImg = UUID.randomUUID().toString();
                        level1Dir = "LPSMARTFARM";
                        level2Dir = level1Dir + "/" + "Farmer";
                        level3Dir = level2Dir + "/" + uuidfarmerImg;
                        newfullfarmerPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                        String latestupImageName=random() + ".jpg";
                        destination = new File(newfullfarmerPath, latestupImageName);
                        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                            copyFileWithName(photoPath, newfullfarmerPath, destination.getPath());
                        }
                        tvFarmerImage.setText(latestupImageName);
                        dba.open();
                        dba.Insert_TempFile("Farmer", newfullfarmerPath + "/" + latestupImageName);
                        dba.close();
                        btnViewPhoto.setVisibility(View.VISIBLE);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                tvFarmerImage.setText("");
                btnViewPhoto.setVisibility(View.GONE);
            }
        }
        else if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidpassbookImg = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "PassBook";
                level3Dir = level2Dir + "/" + uuidpassbookImg;
                newfullpassbookPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                passbookphotoPath = fullpassbookPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(passbookphotoPath, newfullpassbookPath);
                }
                dba.open();
                dba.Insert_TempFile("PassBook", newfullpassbookPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                btnViewAttach.setVisibility(View.VISIBLE);
                tvPassBookImage.setText(destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(fullpassbookPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }

        } else if (requestCode == PICK_Farmer_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidfarmerImg = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "Farmer";
                level3Dir = level2Dir + "/" + uuidfarmerImg;
                newfullfarmerPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                farmerphotoPath = fullfarmerPath + "/" + destinationFarmer.getAbsolutePath().substring(destinationFarmer.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(farmerphotoPath, newfullfarmerPath);
                }
                dba.open();
                dba.Insert_TempFile("Farmer", newfullfarmerPath + "/" + destinationFarmer.getAbsolutePath().substring(destinationFarmer.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                btnViewPhoto.setVisibility(View.VISIBLE);
                tvFarmerImage.setText(destinationFarmer.getAbsolutePath().substring(destinationFarmer.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(fullfarmerPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }

        } else if (requestCode == PICK_FSSAI_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidfssaiImg = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "FSSAI";
                level3Dir = level2Dir + "/" + uuidfssaiImg;
                newfullfssaiPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                fssaiphotoPath = fullfssaiPath + "/" + destinationfssai.getAbsolutePath().substring(destinationfssai.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(fssaiphotoPath, newfullfssaiPath);
                }
                dba.open();
                dba.Insert_TempFile("FSSAI", newfullfssaiPath + "/" + destinationfssai.getAbsolutePath().substring(destinationfssai.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                btnViewFSSAIAttach.setVisibility(View.VISIBLE);
                tvFSSAIImage.setText(destinationfssai.getAbsolutePath().substring(destinationfssai.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(fullfssaiPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == PICK_FSSAI_Camera_IMAGE) {
                tvFSSAIImage.setText("");
                btnViewFSSAIAttach.setVisibility(View.GONE);
            } else if (requestCode == PICK_Camera_IMAGE) {
                tvPassBookImage.setText("");
                btnViewFSSAIAttach.setVisibility(View.GONE);
            } else if (requestCode == PICK_Farmer_Camera_IMAGE) {
                tvFarmerImage.setText("");
                btnViewPhoto.setVisibility(View.GONE);
            }
        }
    }

    //Method to get Actual path of image
    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*---------------End of code to generate random number and return the same-------------------------*/
    /*---------------Start of code to delete file recursively-------------------------*/
    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }
    /*---------------End of code to delete file recursively-------------------------*/

    /*---------------Start of code to compress image-------------------------*/
    public String compressImage(String path) {

        File imagePath = new File(path);
        String filePath = path;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options,
                actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                middleY - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
        }
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(imagePath);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        return imagePath.getAbsolutePath();

    }
    /*---------------End of code to compress image-------------------------*/

    /*---------------Start of code to create new directory-------------------------*/
    private boolean createDirectory(String dirName) {
        //Code to Create Directory for Inspection (Parent)
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            copyNoMediaFile(dirName);
            return true;
        } else {
            return false;
        }
    }
    /*---------------End of code to create new directory-------------------------*/

    /*---------------Start of code to create No Media File in directory-------------------------*/
    private void copyNoMediaFile(String dirName) {
        try {
            // Open your local db as the input stream
            //boolean D= true;
            String storageState = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                try {
                    File noMedia = new File(Environment
                            .getExternalStorageDirectory()
                            + "/"
                            + level2Dir, ".nomedia");
                    if (noMedia.exists()) {


                    }

                    FileOutputStream noMediaOutStream = new FileOutputStream(noMedia);
                    noMediaOutStream.write(0);
                    noMediaOutStream.close();
                } catch (Exception e) {

                }
            } else {

            }

        } catch (Exception e) {

        }
    }
    /*---------------End of code to create No Media File in directory-------------------------*/

    /*---------------Start of code to Copy file from one place to another-------------------------*/
    private String copyFile(String inputPath, String outputPath) {

        File f = new File(inputPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + f.getName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            compressImage(outputPath + "/" + f.getName());

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;


        } catch (FileNotFoundException fnfe1) {
            //Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            //Log.e("tag", e.getMessage());
        }
        return outputPath + "/" + f.getName();
    }
    /*---------------End of code to Copy file from one place to another-------------------------*/

    /*---------------Start of code to Copy file from one place to another with file name-------------------------*/
    private String copyFileWithName(String inputPath, String outputPath, String outputPathWithName) {

        File f = new File(outputPathWithName);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + f.getName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            compressImage(outputPath + "/" + f.getName());

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;


        } catch (FileNotFoundException fnfe1) {
            //Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            //Log.e("tag", e.getMessage());
        }
        return outputPath + "/" + f.getName();
    }
    /*---------------End of code to Copy file from one place to another with file name -------------------------*/

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(FarmerCreateStep1.this, ActivityHome.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
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
