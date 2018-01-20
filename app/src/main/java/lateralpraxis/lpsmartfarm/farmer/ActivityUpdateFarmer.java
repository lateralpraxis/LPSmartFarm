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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
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
import lateralpraxis.type.OperationalBlocks;

public class ActivityUpdateFarmer extends Activity {
    private static final int PICK_Camera_IMAGE = 0;
    /*------------------------Start of code for fixed flag Declaration------------------------------*/
    private static final int PICK_FSSAI_Camera_IMAGE = 3;
    final Context context = this;
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
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmerUniqueId, tvFarmerCode, tvFarmerType, tvLanguage, tvFarmerName, tvFarmerImage, tvFatherName, tvGenderName, tvBirthDate, tvAddress, tvMobile, tvFSSAIImage, tvPassBookImage;
    private Spinner spEducationLevel;
    private EditText etTotalAcreage, etAlternate1, etAlternate2, etMail, etFSSAIRegDate, etFSSAIExpDate, etFSSAI, etAccount, etIFSC;
    private LinearLayout llBankAttach, llBank, llExpDate, llRegDate, llFSSAIAttach, llAccount, llFSSAIHead;
    private Button btnViewPhoto, btnUpdateFarmer, btnFarmBlock, btnFSSAIUpload, btnViewAttach, btnUpload, btnViewFSSAIAttach, btnSave;
    /*-------------------------Code for Variable Declaration---------------------------------------*/
    private ArrayList<String> farmerData;
    private String farmerUniqueId;
    private static String lang;
    private long regtime, exptime, birthTime;
    private String fullfssaiPath, fssaiphotoPath, uuidfssaiImg, newfullpassbookPath, newfullfssaiPath, newfullfarmerPath;
    private String level1Dir, level2Dir, level3Dir, fullpassbookPath, passbookphotoPath, uuidpassbookImg, farmerphotoPath, uuidfarmerImg;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;
    private UserSessionManager session;
    private Intent intent;
    private SimpleDateFormat dateFormatter;
    private ImageLoadingUtils utils;
    private String uploadedFilePathPassBook, uploadedFilePathFSSAI;
    private ArrayList<HashMap<String, String>> attachmentdetails;
    /*-----------Code for Variable Declaration---------------------*/
    private File[] listFilePassBook, listFileFarmer, listFileFSSAI;
    private String[] FilePathStringsPassBook, FilePathStringsFSSAI;
    private String[] FileNameStringsPassBook, FileNameStringsFSSAI;

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
        setContentView(R.layout.activity_update_farmer);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        lang = session.getDefaultLang();
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    /*-----------------Code to set Farmer Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
        }
     /*-----------Code to delete files from temporary table-----------------*/
        dba.open();
        //Code to delete Blank Images
        dba.deleteBlankImages();
        dba.DeleteTempFarmerByUniqueId(farmerUniqueId);
        dba.close();

     /*----------------------------Code to find controls---------------------------*/
        tvFarmerUniqueId = (TextView) findViewById(R.id.tvFarmerUniqueId);
        tvFarmerCode = (TextView) findViewById(R.id.tvFarmerCode);
        tvFarmerType = (TextView) findViewById(R.id.tvFarmerType);
        tvLanguage = (TextView) findViewById(R.id.tvLanguage);
        tvFarmerName = (TextView) findViewById(R.id.tvFarmerName);
        tvFarmerImage = (TextView) findViewById(R.id.tvFarmerImage);
        tvFatherName = (TextView) findViewById(R.id.tvFatherName);
        tvGenderName = (TextView) findViewById(R.id.tvGenderName);
        tvBirthDate = (TextView) findViewById(R.id.tvBirthDate);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFSSAIImage = (TextView) findViewById(R.id.tvFSSAIImage);
        tvPassBookImage = (TextView) findViewById(R.id.tvPassBookImage);
        etTotalAcreage = (EditText) findViewById(R.id.etTotalAcreage);
        etTotalAcreage.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etTotalAcreage.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etAlternate1 = (EditText) findViewById(R.id.etAlternate1);
        etAlternate2 = (EditText) findViewById(R.id.etAlternate2);
        etMail = (EditText) findViewById(R.id.etMail);
        etIFSC = (EditText) findViewById(R.id.etIFSC);
        etAccount = (EditText) findViewById(R.id.etAccount);
        spEducationLevel = (Spinner) findViewById(R.id.spEducationLevel);
        etFSSAIRegDate = (EditText) findViewById(R.id.etFSSAIRegDate);
        etFSSAIExpDate = (EditText) findViewById(R.id.etFSSAIExpDate);
        etFSSAI = (EditText) findViewById(R.id.etFSSAI);

        llBankAttach = (LinearLayout) findViewById(R.id.llBankAttach);
        llBank = (LinearLayout) findViewById(R.id.llBank);
        llAccount = (LinearLayout) findViewById(R.id.llAccount);
        llFSSAIHead = (LinearLayout) findViewById(R.id.llFSSAIHead);
        llExpDate = (LinearLayout) findViewById(R.id.llExpDate);
        llRegDate = (LinearLayout) findViewById(R.id.llRegDate);
        llFSSAIAttach = (LinearLayout) findViewById(R.id.llFSSAIAttach);

        btnFSSAIUpload = (Button) findViewById(R.id.btnFSSAIUpload);
        btnViewAttach = (Button) findViewById(R.id.btnViewAttach);
        btnViewPhoto = (Button) findViewById(R.id.btnViewPhoto);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnViewFSSAIAttach = (Button) findViewById(R.id.btnViewFSSAIAttach);
        btnSave = (Button) findViewById(R.id.btnSave);

        /*---------Code to bind Education Level------------------------*/
        spEducationLevel.setAdapter(DataAdapter("educationlevel", ""));
        /*---------------Start of code to bind Farmer details-------------------------*/
        dba.openR();
        farmerData = dba.getFarmerDetailsForUpdateUniqueId(farmerUniqueId);
        tvFarmerCode.setText(farmerData.get(0));
        tvFarmerType.setText(farmerData.get(2));
        tvLanguage.setText(farmerData.get(27));
        tvFarmerName.setText(farmerData.get(3));
        tvFatherName.setText(farmerData.get(4));
        tvGenderName.setText(farmerData.get(10));
        tvBirthDate.setText(farmerData.get(9).replace("/", "-"));
        tvFarmerImage.setText(farmerData.get(28) == null ? "" : farmerData.get(28).substring(farmerData.get(28).lastIndexOf("/") + 1));
        String farmerAddress = "";
        farmerAddress = farmerData.get(17) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(18)))
            farmerAddress = farmerAddress + farmerData.get(18) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(23)))
            farmerAddress = farmerAddress + farmerData.get(23) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(22)))
            farmerAddress = farmerAddress + farmerData.get(22) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(21)))
            farmerAddress = farmerAddress + farmerData.get(21) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(24)))
            farmerAddress = farmerAddress + farmerData.get(24) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(20)))
            farmerAddress = farmerAddress + farmerData.get(20) + ",<br>";
        if (!TextUtils.isEmpty(farmerData.get(19)))
            farmerAddress = farmerAddress + farmerData.get(19) + "-" + farmerData.get(25);
        tvAddress.setSingleLine(false);
        tvAddress.setText(Html.fromHtml(farmerAddress));
        etTotalAcreage.setText(farmerData.get(13));
        tvMobile.setText(farmerData.get(6));
        etAlternate1.setText(farmerData.get(7));
        etAlternate2.setText(farmerData.get(8));
        etMail.setText(farmerData.get(5));
        etAccount.setText(farmerData.get(11));
        etIFSC.setText(farmerData.get(12));
        etFSSAI.setText(farmerData.get(14));
        etFSSAIRegDate.setText(farmerData.get(15));
        etFSSAIExpDate.setText(farmerData.get(16));
        tvFSSAIImage.setText(farmerData.get(30) == null ? "" : farmerData.get(30).substring(farmerData.get(30).lastIndexOf("/") + 1));
        tvPassBookImage.setText(farmerData.get(29) == null ? "" : farmerData.get(29).substring(farmerData.get(29).lastIndexOf("/") + 1));
        int eduCount = spEducationLevel.getAdapter().getCount();
        for (int i = 0; i < eduCount; i++) {
            if (((CustomType) spEducationLevel.getItemAtPosition(i)).getId().equals(Integer.valueOf(farmerData.get(1))))
                spEducationLevel.setSelection(i);
        }

        //Code to Display FSSAI Details
        if (!TextUtils.isEmpty(farmerData.get(14))) {
            llExpDate.setVisibility(View.VISIBLE);
            llRegDate.setVisibility(View.VISIBLE);
            llFSSAIAttach.setVisibility(View.VISIBLE);
        } else {
            llExpDate.setVisibility(View.GONE);
            llRegDate.setVisibility(View.GONE);
            llFSSAIAttach.setVisibility(View.GONE);
        }
        //Code to Display BANK Details
        if (!TextUtils.isEmpty(farmerData.get(11))) {
            llBank.setVisibility(View.VISIBLE);
            llBankAttach.setVisibility(View.VISIBLE);
        } else {
            llBank.setVisibility(View.GONE);
            llBankAttach.setVisibility(View.GONE);
        }

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

        etMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {

                }
                // onBlur
                else {
                    if (!etMail.getEditableText().toString().trim().matches(emailPattern) && etMail.getEditableText().toString().trim().length() != 0)
                        common.showToast(lang.equalsIgnoreCase("en")?"Invalid email address":"ईमेल अमान्य", 5);
                }
            }
        });
        /*---------End of code for validating email-----------------------*/
        /*---------Start of code for validating Mobile-----------------------*/
        etAlternate1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {

                }
                // onBlur
                else {
                    if (!etAlternate1.getEditableText().toString().trim().matches(contactnoPattern) && etAlternate1.getEditableText().toString().trim().length() < 10)
                        common.showToast(lang.equalsIgnoreCase("en")?"Invalid mobile":"मोबाइल अमान्य", 5);
                }
            }
        });

        etAlternate2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {

                }
                // onBlur
                else {
                    if (!etAlternate2.getEditableText().toString().trim().matches(contactnoPattern) && etAlternate2.getEditableText().toString().trim().length() < 10)
                        common.showToast(lang.equalsIgnoreCase("en")?"Invalid mobile":"मोबाइल अमान्य", 5);
                }
            }
        });
        /*---------End of code for validating Mobile-----------------------*/

        /*---------------Start of code to set calendar for FSSAI Registration Date-------------------------*/
        etFSSAIRegDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(ActivityUpdateFarmer.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etFSSAIRegDate.setText(dateFormatter.format(newDate.getTime()));
                        regtime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle(lang.equalsIgnoreCase("en")?"Select Registration date":"पंजीकरण की तारीख का चयन करें");
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
                DatePickerDialog mDatePicker = new DatePickerDialog(ActivityUpdateFarmer.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        etFSSAIExpDate.setText(dateFormatter.format(newDate.getTime()));
                        exptime = newDate.getTimeInMillis();
                    }
                }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle(lang.equalsIgnoreCase("en")?"Select Registration date":"पंजीकरण की तारीख का चयन करें");
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
                    etIFSC.setText("");
                    dba.open();
                    dba.DeleteTempFarmerDocumentByType("PassBook", farmerUniqueId);
                    dba.close();
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
                    etIFSC.setText("");
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
                    etIFSC.setText("");
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
                    dba.open();
                    dba.DeleteTempFarmerDocumentByType("FSSAI", farmerUniqueId);
                    dba.close();
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
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvPassBookImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Attach Bank Passbook":"बैंक पासबुक संलग्न करें");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to remove existing Passbook picture and upload new Passbook picture?":"क्या आप वाकई मौजूदा पासबुक चित्र को हटाना चाहते हैं और नया पासबुक चित्र अपलोड करना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvPassBookImage.setText("");
                                    btnViewAttach.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFarmerDocumentByType("PassBook", farmerUniqueId);
                                    dba.close();
                                    startPassbookDialog();
                                }
                            }).setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं",
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
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Attach FSSAI Proof":"एफएसएसएआई प्रूफ संलग्न करें");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to remove existing FSSAI Proof picture and upload new FSSAI Proof?":"क्या आप वाकई मौजूदा एफएसएसएआई प्रूफ तस्वीर को हटाना चाहते हैं और नए एफएसएसएआई प्रूफ को अपलोड करना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvFSSAIImage.setText("");
                                    btnViewFSSAIAttach.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFarmerDocumentByType("FSSAI", farmerUniqueId);
                                    dba.close();
                                    startFSSAIDialog();
                                }
                            }).setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं",
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
        btnViewAttach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {

                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!");
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempFarmerDocument("PassBook", farmerUniqueId);
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

                            Intent i1 = new Intent(ActivityUpdateFarmer.this,
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
                    common.showAlert(ActivityUpdateFarmer.this, "Error: " + except.getMessage(), false);

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
                        common.showToast("Error! No SDCARD Found!");
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempFarmerDocument("FSSAI", farmerUniqueId);
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

                            Intent i1 = new Intent(ActivityUpdateFarmer.this,
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
                    common.showAlert(ActivityUpdateFarmer.this, "Error: " + except.getMessage(), false);

                }
            }
        });
        /*---------------End of code to set Click Event for Viewing FSSAI Attachment-------------------------*/

         /*---------------Start of code to set Click Event for Button Save & Next-------------------------*/
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etTotalAcreage.clearFocus();
                if ((spEducationLevel.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Education Level is mandatory.":"शिक्षा स्तर अनिवार्य है।");
                else if (String.valueOf(etTotalAcreage.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Total Acreage is mandatory.":"कुल रकबा अनिवार्य है।");
                else if (Double.valueOf(String.valueOf(etTotalAcreage.getText()).trim()) < .01)
                    common.showToast(lang.equalsIgnoreCase("en")?"Total Acreage cannot be less than .01.":"कुल रकबा .01 से कम नहीं हो सकता।");
                else if (!String.valueOf(etAlternate1.getText()).trim().equals("") && !isValidPhone(String.valueOf(etAlternate1.getText()).trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Alternate number 1 is invalid.":"वैकल्पिक नंबर 1 अमान्य है।", 5, 1);
                else if (!String.valueOf(etAlternate1.getText()).trim().equals("") && String.valueOf(etAlternate1.getText()).trim().length() < 10)
                    common.showToast(lang.equalsIgnoreCase("en")?"Alternate number 1 is invalid.":"वैकल्पिक नंबर 1 अमान्य है।", 5, 1);
                else if (!String.valueOf(etAlternate2.getText()).trim().equals("") && !isValidPhone(String.valueOf(etAlternate2.getText()).trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Alternate number 2 is invalid.":"वैकल्पिक नंबर 2 अमान्य है।", 5, 1);
                else if (!String.valueOf(etAlternate2.getText()).trim().equals("") && String.valueOf(etAlternate2.getText()).trim().length() < 10)
                    common.showToast(lang.equalsIgnoreCase("en")?"Alternate number 2 is invalid.":"वैकल्पिक नंबर 2 अमान्य है।", 5, 1);
                else if (!String.valueOf(etMail.getText()).trim().equals("") && !etMail.getEditableText().toString().trim().matches(emailPattern))
                    common.showToast(lang.equalsIgnoreCase("en")?"Please enter valid Email.":"कृपया वैध ईमेल दर्ज़ करें।");
                else if (etAccount.getText().toString().trim().length() > 0 && String.valueOf(etIFSC.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"IFSC Code is mandatory.":"आईएफएससी कोड अनिवार्य है।");
                else if (etAccount.getText().toString().trim().length() > 0 && String.valueOf(tvPassBookImage.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Passbook attachment is mandatory.":"पासबुक लगाव अनिवार्य है।");
                else if (etFSSAI.getText().toString().trim().length() > 0 && etFSSAI.getText().toString().trim().length() < 14)
                    common.showToast(lang.equalsIgnoreCase("en")?"FSSAI number must have 14 digits.":"एफएसएसएआई नंबर में 14 अंक होंगे।");
                else if (etFSSAI.getText().toString().trim().length() == 14 && String.valueOf(etFSSAIRegDate.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Registration Date is mandatory.":"पंजीकरण की तारीख अनिवार्य है।");
                else if (etFSSAI.getText().toString().trim().length() == 14 && String.valueOf(etFSSAIExpDate.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Expiry Date is mandatory.":"समाप्ति तिथि अनिवार्य है।");
                else if (etFSSAI.getText().toString().trim().length() == 14 && String.valueOf(tvFSSAIImage.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"FSSAI Image is mandatory.":"एफएसएसएआई छवि अनिवार्य है।");
                else if (!TextUtils.isEmpty(etAlternate1.getText().toString()) && etAlternate1.getText().toString().equalsIgnoreCase(tvMobile.getText().toString())) {
                    common.showToast(lang.equalsIgnoreCase("en")?"Mobile and Alternate 1 cannot be same.":"मोबाइल और वैकल्पिक 1 समान नहीं हो सकते।");
                } else if (!TextUtils.isEmpty(etAlternate2.getText().toString()) && etAlternate2.getText().toString().equalsIgnoreCase(tvMobile.getText().toString())) {
                    common.showToast(lang.equalsIgnoreCase("en")?"Mobile and Alternate 2 cannot be same.":"मोबाइल और वैकल्पिक 2 समान नहीं हो सकते।");
                } else if (!TextUtils.isEmpty(etAlternate2.getText().toString()) && !TextUtils.isEmpty(etAlternate1.getText().toString()) && etAlternate1.getText().toString().equalsIgnoreCase(etAlternate2.getText().toString())) {
                    common.showToast(lang.equalsIgnoreCase("en")?"Alternate 1 and Alternate 2 cannot be same.":"वैकल्पिक 1 और वैकल्पिक 2 समान नहीं हो सकते।");
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
                    gps = new GPSTracker(ActivityUpdateFarmer.this);
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
                        dba.open();
                                    /*----------Code to insert data in Temp Farmer and Address Table-----------------------*/
                        dba.updateFarmerDetails(farmerUniqueId, String.valueOf(((CustomType) spEducationLevel.getSelectedItem()).getId()),
                                etMail.getText().toString().trim(),
                                etAlternate1.getText().toString().trim(), etAlternate2.getText().toString().trim(),
                                etAccount.getText().toString().trim(), etIFSC.getText().toString().trim(),
                                etTotalAcreage.getText().toString().trim(), etFSSAI.getText().toString().trim(),
                                etFSSAIRegDate.getText().toString().trim(), etFSSAIExpDate.getText().toString().trim(), longitudeN, latitudeN, accuracy);
                                    /*----------Code to Move data from Temporary Table to FarmerDocuments Table-----------------------*/
                        dba.Update_FarmerDocuments(farmerUniqueId);
                        dba.close();

                        Intent intent = new Intent(ActivityUpdateFarmer.this, FarmerPOAPOI.class);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("Name", tvFarmerName.getText());
                        intent.putExtra("Mobile", tvMobile.getText());
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


            btnViewFSSAIAttach.setVisibility(View.GONE);
        }
    }

    //Code to be executed after action done for attaching
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data == null) {
            //Reset image name and hide reset button
            tvPassBookImage.setText("");
        } else if (requestCode == PICK_Camera_IMAGE) {
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
                dba.insertTempFarmerDocument(farmerUniqueId, "PassBook", newfullpassbookPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
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
                dba.insertTempFarmerDocument(farmerUniqueId, "FSSAI", newfullfssaiPath + "/" + destinationfssai.getAbsolutePath().substring(destinationfssai.getAbsolutePath().lastIndexOf("/") + 1));
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
                btnViewAttach.setVisibility(View.GONE);
            }
        }
    }
    /*---------------End of code to delete file recursively-------------------------*/

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
    /*---------------End of code to compress image-------------------------*/

    /*---------------End of code to generate random number and return the same-------------------------*/
    /*---------------Start of code to delete file recursively-------------------------*/
    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }
    /*---------------End of code to create new directory-------------------------*/

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
    /*---------------End of code to create No Media File in directory-------------------------*/

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
    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    // When press back button go to home screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityFarmerView.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityUpdateFarmer.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityUpdateFarmer.this, ActivityHome.class);
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
        Intent i = new Intent(ActivityUpdateFarmer.this, ActivityFarmerView.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
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

    public static class ActivityUpdateBlockAssignment extends Activity {
        private final Context mContext = this;
        /*------------------Code for Class Declaration---------------*/
        Common common;
        DatabaseAdapter dba;
        UserSessionManager session;
        CustomAdapter Cadapter;
        /*-----------Start of Code for control declaration-----------*/
        private Spinner spDistrict, spBlock;
        private Button btnSubmit,btnAdd;
        private ListView lvAssignedBlockList;
        private TableLayout tableLayoutHeader;
        private TextView tvEmpty,tvNameData, tvMobile;
        private View tvDivider;
        /*-----------End of Code for control declaration-----------*/

        /*--------------Start of Code for variable declaration-----------*/
        private int lsize = 0;
        private ArrayList<HashMap<String, String>> AssignedBlockDetails;
        private String farmerUniqueId = "", farmerName = "", farmerMobile = "";

        /*--------------End of Code for variable declaration-----------*/
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_update_block_assignment);

             /*------------------------Start of code for setting action bar----------------------------*/
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            /*------------------------End of code for setting action bar------------------------------*/

             /*------------------------Start of code for creating instance of class--------------------*/
            dba = new DatabaseAdapter(this);
            common = new Common(this);
            session = new UserSessionManager(getApplicationContext());
            AssignedBlockDetails = new ArrayList<HashMap<String, String>>();
            /*------------------------End of code for creating instance of class--------------------*/

            /*------------------------Start of code for finding controls-----------------------*/
            spDistrict = (Spinner) findViewById(R.id.spDistrict);
            spBlock = (Spinner) findViewById(R.id.spBlock);
            btnAdd = (Button) findViewById(R.id.btnAdd);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            tableLayoutHeader = (TableLayout) findViewById(R.id.tableLayoutHeader);
            tvEmpty = (TextView) findViewById(R.id.tvEmpty);
            tvNameData = (TextView) findViewById(R.id.tvNameData);
            tvMobile = (TextView) findViewById(R.id.tvMobile);
            tvDivider= (View) findViewById(R.id.tvDivider);
            lvAssignedBlockList = (ListView) findViewById(R.id.lvDocOperationalBlocks);
            /*-------Code to delete Assigned Blocks in temporary table-----------*/
            dba.open();
            dba.DeleteTempAssignedBlocks();
            dba.close();

            /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
            Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                farmerUniqueId = extras.getString("farmerUniqueId");
                farmerName = extras.getString("Name");
                farmerMobile = extras.getString("Mobile");
                tvNameData.setText(farmerName);
                tvMobile.setText(farmerMobile);
            }
            /*---------Code to move operational Block Data from Main To Temp Table-----------------*/
            dba.open();
            dba.Insert_MainOperationalBlockDetailToTemp(farmerUniqueId);
            dba.close();
           /*------------Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
            dba.open();
            List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
            lsize = lables.size();
            if (lsize > 0) {
                tvEmpty.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                tvDivider.setVisibility(View.VISIBLE);
                for (int i = 0; i < lables.size(); i++) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("Id", lables.get(i).getId());
                    hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                    hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                    AssignedBlockDetails.add(hm);
                }
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                tableLayoutHeader.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                tvDivider.setVisibility(View.GONE);
            }
            dba.close();

            Cadapter = new CustomAdapter(ActivityUpdateBlockAssignment.this, AssignedBlockDetails);
            if (lsize > 0)
                lvAssignedBlockList.setAdapter(Cadapter);

            /*------------------------Start of code for binding data in Spinner-----------------------*/
            spDistrict.setAdapter(DataAdapter("alldistrict", ""));

            /*------------------------Code on Select Item Change to bind Blocks-----------------------*/
            spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    spBlock.setAdapter(DataAdapter("block", String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId())));
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        /*---------------Start of code to be executed on Click Event Of buttons-------------------------*/
        /*------------Code to be executed on submit button click---------------------------------*/
            btnAdd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dba.openR();
                    if ((spDistrict.getSelectedItemPosition() == 0))
                        common.showToast("District is mandatory",5,1);
                    else if ((spBlock.getSelectedItemPosition() == 0))
                        common.showToast("Block is mandatory",5,1);
                    else if (dba.isBlockAlreadyAdded(farmerUniqueId, ((CustomType) spDistrict.getSelectedItem()).getId().toString(), ((CustomType) spBlock.getSelectedItem()).getId().toString()).equals(true)) {
                        common.showToast("Block is already assigned",5,1);
                    } else {
                        dba.open();
                        dba.insertFarmerOperatingBlocksTemp(farmerUniqueId, ((CustomType) spDistrict.getSelectedItem()).getId().toString(), ((CustomType) spBlock.getSelectedItem()).getId().toString());
                        dba.close();
                        common.showToast("Block added successfully.",5,3);
                        spDistrict.setSelection(0);
                        spBlock.setSelection(0);
                        AssignedBlockDetails.clear();
                        dba.openR();
                        List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
                        lsize = lables.size();
                        if (lsize > 0) {
                            tvEmpty.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            tvDivider.setVisibility(View.VISIBLE);
                            tableLayoutHeader.setVisibility(View.VISIBLE);
                            for (int i = 0; i < lables.size(); i++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("Id", lables.get(i).getId());
                                hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                                hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                                AssignedBlockDetails.add(hm);
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tableLayoutHeader.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                            tvDivider.setVisibility(View.GONE);
                        }
                        Cadapter = new CustomAdapter(ActivityUpdateBlockAssignment.this, AssignedBlockDetails);
                        if (lsize > 0)
                            lvAssignedBlockList.setAdapter(Cadapter);
                    }
                }
            });
            /*------------Code to be executed on submit button click---------------------------------*/
            btnSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
           /*         AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle("Submit Proof");
                    builder1.setMessage("Are you sure, you want to submit Assigned Blocks?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {*/
                                    dba.open();
                                    dba.updateFarmerOperatingBlocks(farmerUniqueId);
                                    dba.close();
                                    common.showToast("Farmer details updated successfully.",5,3);
                                    Intent intent = new Intent(ActivityUpdateBlockAssignment.this, ActivityFarmerList.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    System.gc();
                           /*     }
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
                    alertnew.show();*/

                }
            });
            /*---------------End of code to be executed on Click Event Of buttons-------------------------*/
        }
        // When press back button go to home screen
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    Intent intent = new Intent(this, ActivityFarmerAssets.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("Name", farmerName);
                    intent.putExtra("Mobile", farmerMobile);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.action_go_to_home:

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityUpdateBlockAssignment.this);
                    // set title
                    alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                            .setCancelable(false)
                            .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent homeScreenIntent = new Intent(ActivityUpdateBlockAssignment.this, ActivityHome.class);
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
        /*---------------Method to fetch data and bind spinners-------------------------*/
        private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
            dba.open();
            List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
            ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dba.close();
            return dataAdapter;
        }


        /*---------------Method to view intent on Back Press Click-------------------------*/
        @Override
        public void onBackPressed() {
            Intent i = new Intent(ActivityUpdateBlockAssignment.this, ActivityFarmerAssets.class);
            i.putExtra("farmerUniqueId", farmerUniqueId);
            i.putExtra("Name", farmerName);
            i.putExtra("Mobile", farmerMobile);
            startActivity(i);
            this.finish();
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

        public static class ViewHolder {
            TextView tvId, tvDistrict, tvBlock;
            Button btnDelete;
        }

        public class CustomAdapter extends BaseAdapter {
            private Context docContext;
            private LayoutInflater mInflater;

            public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvAssignedBlockList) {
                this.docContext = context;
                mInflater = LayoutInflater.from(docContext);
                AssignedBlockDetails = lvAssignedBlockList;
            }

            @Override
            public int getCount() {
                return AssignedBlockDetails.size();
            }

            @Override
            public Object getItem(int arg0) {
                return AssignedBlockDetails.get(arg0);
            }

            @Override
            public long getItemId(int arg0) {
                return arg0;
            }

            @Override
            public int getViewTypeCount() {

                return getCount();
            }

            @Override
            public int getItemViewType(int position) {

                return position;
            }

            @Override
            public View getView(final int arg0, View arg1, ViewGroup arg2) {


                final ViewHolder holder;
                if (arg1 == null) {
                    arg1 = mInflater.inflate(R.layout.list_assigned_blocks, null);
                    holder = new ViewHolder();
                    holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                    holder.tvDistrict = (TextView) arg1.findViewById(R.id.tvDistrict);
                    holder.tvBlock = (TextView) arg1.findViewById(R.id.tvBlock);
                    holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                    arg1.setTag(holder);

                } else {

                    holder = (ViewHolder) arg1.getTag();
                }

                holder.tvId.setText(AssignedBlockDetails.get(arg0).get("Id"));
                holder.tvDistrict.setText(AssignedBlockDetails.get(arg0).get("DistrictName"));
                holder.tvBlock.setText(AssignedBlockDetails.get(arg0).get("BlockName"));
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setTitle("Delete Operational Block");
                        builder1.setMessage("Are you sure you want to delete this Block?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dba.open();
                                        dba.DeleteTempAssignedBlocksById(String.valueOf(holder.tvId.getText()));
                                        dba.close();
                                        common.showToast("Block deleted successfully.",5,3);
                                        AssignedBlockDetails.clear();
                                        dba.open();
                                        List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
                                        lsize = lables.size();
                                        if (lsize > 0) {
                                            tvEmpty.setVisibility(View.GONE);
                                            btnSubmit.setVisibility(View.VISIBLE);
                                            tvDivider.setVisibility(View.VISIBLE);
                                            tableLayoutHeader.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < lables.size(); i++) {
                                                HashMap<String, String> hm = new HashMap<String, String>();
                                                hm.put("Id", lables.get(i).getId());
                                                hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                                                hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                                                AssignedBlockDetails.add(hm);
                                            }
                                        } else {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tableLayoutHeader.setVisibility(View.GONE);
                                            btnSubmit.setVisibility(View.GONE);
                                            tvDivider.setVisibility(View.GONE);
                                        }
                                        dba.close();

                                        Cadapter = new CustomAdapter(ActivityUpdateBlockAssignment.this, AssignedBlockDetails);
                                        if (lsize > 0)
                                            lvAssignedBlockList.setAdapter(Cadapter);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertnew = builder1.create();
                        alertnew.show();

                    }
                });
                arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
                return arg1;
            }
        }
    }
}
