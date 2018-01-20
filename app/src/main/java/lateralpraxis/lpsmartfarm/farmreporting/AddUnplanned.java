package lateralpraxis.lpsmartfarm.farmreporting;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.ImageLoadingUtils;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.CustomData;

public class AddUnplanned extends Activity {
    private static final int PICK_Camera_IMAGE = 0;
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
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    File destination, fileAdditional;
    private ImageLoadingUtils utils;
    /*------------------------Start of code for fixed flag Declaration------------------------------*/
    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId, farmerName, farmerMobile, farmblockCode, farmblockUniqueId, plantationUniqueId, plantation,jobcardUniqueId,EntryFor;
    private int activityId = 0, subactivityId = 0, isSubActivityAllowed = 0;
    /*------------------Code for Control Declaration---------------*/
    private Spinner spActivity, spSubActivity;
    private TextView tvRemarks, tvFarmerImage, tvUOM;
    private EditText etQuantity;
    private Button btnUploadPhoto, btnViewPhoto, btnBack, btnNext;
    private File[] listFileAdditional;
    private String[] FilePathStringsAdditional, FileNameStringsAdditional;
    private String uploadedFilePathAdditional;
    private ArrayList<HashMap<String, String>> attachmentdetails;
    /*------------------------Start of code for Regular Expression for Validating Decimal Values------------------------------*/
    private String level1Dir, level2Dir, level3Dir, uuidadditionalImg, fulladditionalPath, newfulladditionalPath, additionalPath;
    private static String lang;
    //<editor-fold desc="Method to Generate Random Number">
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
    //</editor-fold>

    //<editor-fold desc="Code to be executed on On Create">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unplanned);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

          /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        lang = session.getDefaultLang();
        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmblockCode = extras.getString("farmblockCode");
            farmblockUniqueId = extras.getString("farmblockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantation = extras.getString("plantation");
            jobcardUniqueId =extras.getString("jobcardUniqueId");
            EntryFor = extras.getString("EntryFor");
        }
        /*------------------------Code for finding controls-----------------------*/
        tvRemarks = (TextView) findViewById(R.id.tvRemarks);
        tvFarmerImage = (TextView) findViewById(R.id.tvFarmerImage);
        tvUOM = (TextView) findViewById(R.id.tvUOM);
        spActivity = (Spinner) findViewById(R.id.spActivity);
        spSubActivity = (Spinner) findViewById(R.id.spSubActivity);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etQuantity.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        spActivity.setAdapter(DataAdapter("unplannedactivity", plantationUniqueId));
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        btnViewPhoto = (Button) findViewById(R.id.btnViewPhoto);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        dba.open();
        dba.DeleteTempFileByType("Additional");
        dba.close();
        /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
        spActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                tvUOM.setText("");
                tvRemarks.setText("");
                String parameter = plantationUniqueId + "~" + ((CustomData) spActivity.getSelectedItem()).getId().split("~")[0];
                activityId = Integer.valueOf(((CustomData) spActivity.getSelectedItem()).getId().split("~")[0]);
                isSubActivityAllowed = Integer.valueOf(((CustomData) spActivity.getSelectedItem()).getId().split("~")[1]);
                spSubActivity.setAdapter(DataAdapter("unplannedsubactivity", parameter));
                dba.openR();
                tvRemarks.setText(dba.getRemarksByActSubActId(plantationUniqueId, String.valueOf(activityId), String.valueOf(subactivityId)));
                if (!((CustomData) spActivity.getSelectedItem()).getId().split("~")[2].equalsIgnoreCase("0"))
                    tvUOM.setText(((CustomData) spActivity.getSelectedItem()).getId().split("~")[2]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                tvUOM.setText("");
                tvRemarks.setText("");
            }

        });

        spSubActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                subactivityId = Integer.valueOf(((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[0]);
                if (!((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[1].equalsIgnoreCase("0")) {
                    tvUOM.setText("");
                    tvRemarks.setText("");
                }
                dba.openR();
                tvRemarks.setText(dba.getRemarksByActSubActId(plantationUniqueId, String.valueOf(activityId), String.valueOf(subactivityId)));
                if (!((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[1].equalsIgnoreCase("0"))
                    tvUOM.setText(((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                tvUOM.setText("");
                tvRemarks.setText("");
            }

        });

        //Code to Validate Quantity Entered is Valid Number or Not
        etQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etQuantity.getText())) {

                    } else
                        etQuantity.setText("");

                }
            }
        });
        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move on Reporting Step 1
                Intent intent = new Intent(AddUnplanned.this, UnplannedView.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();
            }
        });

        //To validate and move to plantation population screen
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dba.openR();
                if (spActivity.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Activity is mandatory":"गतिविधि अनिवार्य है");
                else if (isSubActivityAllowed > 0 && spSubActivity.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Sub Activity is mandatory":"उप गतिविधि अनिवार्य है");
                else if (TextUtils.isEmpty(etQuantity.getText().toString().trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Quantity is mandatory":"मात्रा अनिवार्य है");
                else if (!TextUtils.isEmpty(etQuantity.getText().toString().trim()) && Double.valueOf(etQuantity.getText().toString().trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Quantity cannot be zero.":"मात्रा शून्य नहीं हो सकती।");
                else if (dba.isActivityAlreadyAdded(((CustomData) spActivity.getSelectedItem()).getId().split("~")[0], ((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[0]).equals(true))
                    common.showToast(lang.equalsIgnoreCase("en")?"These Unplanned details are already added.":"ये अनियोजित विवरण पहले ही जोड़े गए हैं।", 5, 1);
                else {
                    etQuantity.clearFocus();
                    dba.open();
                    String fileName = dba.getFileNameForAdditional("Additional");
                    dba.insertFarmPlannedDetailsTemp(((CustomData) spActivity.getSelectedItem()).getId().split("~")[0], ((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[0], etQuantity.getText().toString(), "A", "0", "0", UUID.randomUUID().toString(), fileName);
                    if (!TextUtils.isEmpty(fileName))
                        dba.Insert_TempJobCardFile(((CustomData) spActivity.getSelectedItem()).getId().split("~")[0], ((CustomData) spSubActivity.getSelectedItem()).getId().split("~")[0], "Additional", fileName);
                    dba.close();
                    common.showToast(lang.equalsIgnoreCase("en")?"Unplanned activity details added successfully.":"अनियोजित गतिविधि विवरण सफलतापूर्वक जोड़ा गया।", 5, 3);
                    Intent intent = new Intent(AddUnplanned.this, UnplannedView.class);
                    intent.putExtra("EntryFor", EntryFor);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                    intent.putExtra("farmblockCode", farmblockCode);
                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                    intent.putExtra("plantation", plantation);
                    intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                    startActivity(intent);
                    finish();
                }
            }
        });


        //<editor-fold desc="code to be executed on Button Upload click for Opening Camera">
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvFarmerImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Attach Additonal Photo":"अतिरिक्त फोटो संलग्न करें");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to remove existing picture and upload new picture?":"क्या आप वाकई मौजूदा तस्वीर को हटाना चाहते हैं और नई तस्वीर अपलोड करना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvFarmerImage.setText("");
                                    btnViewPhoto.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("Additional");
                                    dba.close();
                                    startPictureDialog();
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
                    startPictureDialog();
            }
        });
        //</editor-fold>

        //<editor-fold desc="code to be executed on Button View Photo click for Displaying Image">
        btnViewPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {

                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!");
                    } else {

                        dba.open();
                        attachmentdetails = dba.GetTempAttachment("Additional");
                        if (attachmentdetails.size() > 0) {
                            for (HashMap<String, String> hashMap : attachmentdetails) {

                                for (String key : hashMap.keySet()) {

                                    if (key.equals("FileName"))
                                        uploadedFilePathAdditional = hashMap.get(key);
                                }
                            }

                            File file = new File(uploadedFilePathAdditional);
                            fileAdditional = new File(file.getParent());
                        }
                    }

                    if (fileAdditional.isDirectory()) {

                        listFileAdditional = fileAdditional.listFiles(new FilenameFilter() {
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
                        FilePathStringsAdditional = new String[listFileAdditional.length];
                        // Create a String array for FileNameStrings
                        FileNameStringsAdditional = new String[listFileAdditional.length];

                        for (int i = 0; i < listFileAdditional.length; i++) {

                            FilePathStringsAdditional[i] = listFileAdditional[i].getAbsolutePath();
                            // Get the name image file
                            FileNameStringsAdditional[i] = listFileAdditional[i].getName();

                            Intent i1 = new Intent(AddUnplanned.this,
                                    ViewImage.class);
                            // Pass String arrays FilePathStrings
                            i1.putExtra("filepath", FilePathStringsAdditional);
                            // Pass String arrays FileNameStrings
                            i1.putExtra("filename", FileNameStringsAdditional);
                            // Pass click position
                            i1.putExtra("position", 0);
                            startActivity(i1);
                            /* } */
                        }
                    }

                } catch (Exception except) {
                    //except.printStackTrace();
                    common.showAlert(AddUnplanned.this, "Error: " + except.getMessage(), false);

                }
            }
        });
        //</editor-fold>
    }
//</editor-fold>

    //<editor-fold desc="Method to Insert Captured Image in temp table">
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data == null) {
            //Reset image name and hide reset button
            tvFarmerImage.setText("");
        } else if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidadditionalImg = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "Additional";
                level3Dir = level2Dir + "/" + uuidadditionalImg;
                newfulladditionalPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                additionalPath = fulladditionalPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(additionalPath, newfulladditionalPath);
                }
                dba.open();
                dba.Insert_TempFile("Additional", newfulladditionalPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                btnViewPhoto.setVisibility(View.VISIBLE);
                tvFarmerImage.setText(destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(fulladditionalPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == PICK_Camera_IMAGE) {
                tvFarmerImage.setText("");
                btnViewPhoto.setVisibility(View.GONE);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="code to open Camera for Capturing Image">
    private void startPictureDialog() {

        //Setting directory structure
        uuidadditionalImg = UUID.randomUUID().toString();
        level1Dir = "LPSMARTFARM";
        level2Dir = level1Dir + "/" + "Additional";
        level3Dir = level2Dir + "/" + uuidadditionalImg;
        String imageName = random() + ".jpg";
        fulladditionalPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
        destination = new File(fulladditionalPath, imageName);
        //Check if directory exists else create directory
        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
            //Code to open camera intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(destination));
            startActivityForResult(intent, PICK_Camera_IMAGE);

            btnViewPhoto.setVisibility(View.GONE);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Method to fetch data and bind spinners">
    private ArrayAdapter<CustomData> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomData> lables = dba.GetOtherMasterDetails(masterType, filter);
        ArrayAdapter<CustomData> dataAdapter = new ArrayAdapter<CustomData>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }
    //</editor-fold>

    //<editor-fold desc="Method to view intent on Action Bar Click">
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddUnplanned.this, UnplannedView.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(AddUnplanned.this);
                // set title
                alertDialogBuilderh.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilderh
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(AddUnplanned.this, ActivityHome.class);
                                startActivity(intent1);
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
                AlertDialog alertDialogh = alertDialogBuilderh.create();
                // show it
                alertDialogh.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to To create menu on inflater">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Method to view intent on Back Press Click">
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddUnplanned.this, UnplannedView.class);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmblockUniqueId", farmblockUniqueId);
        intent.putExtra("farmblockCode", farmblockCode);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantation", plantation);
        intent.putExtra("jobcardUniqueId", jobcardUniqueId);
        startActivity(intent);
        finish();
    }
    //</editor-fold>

    //<editor-fold desc="Method to check android version ad load action bar appropriately">
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

    //<editor-fold desc="Start of code to Copy file from one place to another">
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
    //</editor-fold>

    //<editor-fold desc="code to compress image">
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
    //</editor-fold>

    //<editor-fold desc="code to create new directory">
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
    //</editor-fold>

    //<editor-fold desc="code to copy no media file in new directory">
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
    //</editor-fold>

    //<editor-fold desc="code to get Real Path From Uri">
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
    //</editor-fold>
}
