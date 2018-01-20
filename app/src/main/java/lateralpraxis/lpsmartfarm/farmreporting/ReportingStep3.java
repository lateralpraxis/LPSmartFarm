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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class ReportingStep3 extends Activity {
    private static final int PICK_Camera_IMAGE = 0;
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
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    File destination, filePlanned;
    private ImageLoadingUtils utils;
    private int plListSize = 0;
    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId, farmerName, farmerMobile, farmblockCode, farmblockUniqueId, plantationUniqueId, plantation, jobcardUniqueId, EntryFor;
    private File[] listFilePlanned;
    private String[] FilePathStringsPlanned, FileNameStringsPlanned;
    private String uploadedFilePathPlanned;
    private ArrayList<HashMap<String, String>> attachmentdetails;
    private static String lang;
    private String level1Dir, level2Dir, level3Dir, uuidplannedImg, fullplannedPath, newfullplannedPath, plannedPath, newActivityId, newSubActivityId, newActualValue, newplannerDetailId, newparameterDetailId, newUniqueId;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvFarmer, tvPlantation, tvFarmBlock, tvEmpty;
    private View tvDivider;
    private ListView lvPlannedActivity;
    private Button btnBack, btnNext;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_step3);
         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/
         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        lang = session.getDefaultLang();
         /*------------------------Code for finding controls-----------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        lvPlannedActivity = (ListView) findViewById(R.id.lvPlannedActivity);

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
            jobcardUniqueId = extras.getString("jobcardUniqueId");
            EntryFor = extras.getString("EntryFor");
            tvFarmer.setText(farmerName);
            tvPlantation.setText(plantation.replace("/","-"));
            tvFarmBlock.setText(farmblockCode);
        }
        dba.openR();


        ArrayList<HashMap<String, String>> listDetailForViewPlanned = dba.getPlannedActivitiesForPlantation(plantationUniqueId);
        plListSize = listDetailForViewPlanned.size();
        if (listDetailForViewPlanned.size() != 0) {
            lvPlannedActivity.setAdapter(new PlannedListAdapter(context, listDetailForViewPlanned));

            ViewGroup.LayoutParams params = lvPlannedActivity.getLayoutParams();
            params.height = 500;
            lvPlannedActivity.setLayoutParams(params);
            lvPlannedActivity.requestLayout();
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvPlannedActivity.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();
         /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(ReportingStep3.this);
                // set title
                alertDialogBuilderh.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilderh
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को हटा देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //To move on Reporting Step 2
                                Intent intent = new Intent(ReportingStep3.this, ReportingStep2.class);
                                intent.putExtra("EntryFor", EntryFor);
                                intent.putExtra("farmerUniqueId", farmerUniqueId);
                                intent.putExtra("farmerName", farmerName);
                                intent.putExtra("farmerMobile", farmerMobile);
                                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                                intent.putExtra("farmblockCode", farmblockCode);
                                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                                startActivity(intent);
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

            }
        });

        //To validate and move to plantation population screen
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                lvPlannedActivity.clearFocus();
                int lessCount = 0;
                String emptyActivities = "";
                if (plListSize > 0) {
                    for (int i = 0; i < lvPlannedActivity.getCount(); i++) {
                        LinearLayout layout = (LinearLayout) lvPlannedActivity
                                .getChildAt(i);
                        TableLayout tableLayoutTotal = (TableLayout) layout.getChildAt(4);
                        TableRow tableRow = (TableRow) tableLayoutTotal.getChildAt(0);
                        EditText etActualValue = (EditText) tableRow.getChildAt(3);
                        TextView tvPreviousValue = (TextView) tableRow.getChildAt(4);
                        TextView tvName = (TextView) tableRow.getChildAt(0);
                        if (etActualValue.getText().toString().trim().length() == 0)
                            emptyActivities = emptyActivities + tvName.getText().toString() + "<br>";
                        if (!TextUtils.isEmpty(tvPreviousValue.getText().toString().trim())) {
                            if (TextUtils.isEmpty(etActualValue.getText().toString().trim()))
                                lessCount = lessCount + 1;
                            if (!TextUtils.isEmpty(etActualValue.getText().toString().trim()) && Double.valueOf(etActualValue.getText().toString()) < Double.valueOf(tvPreviousValue.getText().toString()))
                                lessCount = lessCount + 1;
                        }
                    }
                    if (!emptyActivities.equalsIgnoreCase(""))
                        emptyActivities = emptyActivities.substring(0, emptyActivities.length() - 1);
                }
                if (lessCount == 0) {
                    if (plListSize > 0 && !emptyActivities.equalsIgnoreCase("")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportingStep3.this);
                        // set title
                        alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(lang.equalsIgnoreCase("en")?Html.fromHtml("Actual values for following planned activities are not entered <br><br>" + emptyActivities + " <br><br>are you sure, you want to save planned details?"):Html.fromHtml("निम्नलिखित नियोजित गतिविधियों के लिए वास्तविक मात्रा दर्ज नहीं किए गए हैं <br><br>" + emptyActivities + " <br><br>क्या आप निश्चित हैं, आप नियोजित विवरणों को सहेजना चाहते हैं?"))
                                .setCancelable(false)
                                .setPositiveButton(lang.equalsIgnoreCase("en")?"Ok":"ठीक", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        for (int i = 0; i < lvPlannedActivity.getCount(); i++) {
                                            LinearLayout layout1 = (LinearLayout) lvPlannedActivity
                                                    .getChildAt(i);
                                            TextView tvPlannerDetailId = (TextView) layout1.getChildAt(0);
                                            TextView tvParameterDetailId = (TextView) layout1.getChildAt(1);
                                            TextView tvActivityId = (TextView) layout1.getChildAt(2);
                                            TextView tvSubActivityId = (TextView) layout1.getChildAt(3);
                                            TextView tvUniqueId = (TextView) layout1.getChildAt(6);
                                            LinearLayout layout = (LinearLayout) lvPlannedActivity
                                                    .getChildAt(i);
                                            TableLayout tableLayoutTotal = (TableLayout) layout.getChildAt(4);
                                            TableRow tableRow = (TableRow) tableLayoutTotal.getChildAt(0);
                                            EditText etActualValue = (EditText) tableRow.getChildAt(3);
                                            dba.open();
                                            dba.insertFarmPlannedDetailsTemp(tvActivityId.getText().toString(), tvSubActivityId.getText().toString(), etActualValue.getText().toString(), "P", tvPlannerDetailId.getText().toString(), tvParameterDetailId.getText().toString(), tvUniqueId.getText().toString(), "");
                                            dba.close();
                                        }
                                        common.showToast(lang.equalsIgnoreCase("en")?"Plannned details saved successfully.":"नियोजित विवरण सफलतापूर्वक सहेजे गए", 5, 3);

                                        Intent intent = new Intent(ReportingStep3.this, ReportingStep4.class);
                                        intent.putExtra("EntryFor", "FarmBlock");
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
                                })
                                .setNegativeButton(lang.equalsIgnoreCase("en")?"Cancel":"रद्द करें", new DialogInterface.OnClickListener() {
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
                    } else if (plListSize > 0 && emptyActivities.equalsIgnoreCase("")) {
                        for (int i = 0; i < lvPlannedActivity.getCount(); i++) {
                            LinearLayout layout1 = (LinearLayout) lvPlannedActivity
                                    .getChildAt(i);
                            TextView tvPlannerDetailId = (TextView) layout1.getChildAt(0);
                            TextView tvParameterDetailId = (TextView) layout1.getChildAt(1);
                            TextView tvActivityId = (TextView) layout1.getChildAt(2);
                            TextView tvSubActivityId = (TextView) layout1.getChildAt(3);
                            LinearLayout layout = (LinearLayout) lvPlannedActivity
                                    .getChildAt(i);
                            TableLayout tableLayoutTotal = (TableLayout) layout.getChildAt(4);
                            TableRow tableRow = (TableRow) tableLayoutTotal.getChildAt(0);
                            EditText etActualValue = (EditText) tableRow.getChildAt(3);
                            dba.open();
                            dba.insertFarmPlannedDetailsTemp(tvActivityId.getText().toString(), tvSubActivityId.getText().toString(), etActualValue.getText().toString(), "P", tvPlannerDetailId.getText().toString(), tvParameterDetailId.getText().toString(), UUID.randomUUID().toString(), "");
                            dba.close();
                        }
                        common.showToast(lang.equalsIgnoreCase("en")?"Plannned details saved successfully.":"नियोजित विवरण सफलतापूर्वक सहेजे गए", 5, 3);
                        Intent intent = new Intent(ReportingStep3.this, ReportingStep4.class);
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
                    } else {
                        Intent intent = new Intent(ReportingStep3.this, ReportingStep4.class);
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
                } else
                    common.showToast(lang.equalsIgnoreCase("en")?"Value entered cannot be less than previous value.":"दर्ज की गई मान पिछली मान से कम नहीं हो सकती।", 5, 1);

            }
        });
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportingStep3.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(ReportingStep3.this, ReportingStep2.class);
                                intent.putExtra("EntryFor", EntryFor);
                                intent.putExtra("farmerUniqueId", farmerUniqueId);
                                intent.putExtra("farmerName", farmerName);
                                intent.putExtra("farmerMobile", farmerMobile);
                                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                                intent.putExtra("farmblockCode", farmblockCode);
                                startActivity(intent);
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
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(ReportingStep3.this);
                // set title
                alertDialogBuilderh.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilderh
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(ReportingStep3.this, ActivityHome.class);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportingStep3.this);
        // set title
        alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
        // set dialog message
        alertDialogBuilder
                .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                .setCancelable(false)
                .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ReportingStep3.this, ReportingStep2.class);
                        intent.putExtra("EntryFor", EntryFor);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                        intent.putExtra("farmblockCode", farmblockCode);
                        startActivity(intent);
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

    //<editor-fold desc="code to open Camera for Capturing Image">
    private void startPictureDialog() {

        //Setting directory structure
        uuidplannedImg = UUID.randomUUID().toString();
        level1Dir = "LPSMARTFARM";
        level2Dir = level1Dir + "/" + "Planned";
        level3Dir = level2Dir + "/" + uuidplannedImg;
        String imageName = random() + ".jpg";
        fullplannedPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
        destination = new File(fullplannedPath, imageName);
        //Check if directory exists else create directory
        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
            //Code to open camera intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(destination));
            startActivityForResult(intent, PICK_Camera_IMAGE);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to Insert Captured Image in temp table">
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data == null) {

        } else if (requestCode == PICK_Camera_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidplannedImg = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "Planned";
                level3Dir = level2Dir + "/" + uuidplannedImg;
                newfullplannedPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                plannedPath = fullplannedPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(plannedPath, newfullplannedPath);
                }
                dba.open();
                dba.Insert_TempJobCardFile(newActivityId, newSubActivityId, "Planned", newfullplannedPath + "/" + destination.getAbsolutePath().substring(destination.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                File dir = new File(fullplannedPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == PICK_Camera_IMAGE) {
            }
        }
        //<editor-fold desc="Method to Bind Data Again in temp table">
        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewPlanned = dba.getPlannedActivitiesForPlantation(plantationUniqueId);
        plListSize = listDetailForViewPlanned.size();
        if (listDetailForViewPlanned.size() != 0) {
            lvPlannedActivity.setAdapter(new PlannedListAdapter(context, listDetailForViewPlanned));

            ViewGroup.LayoutParams params = lvPlannedActivity.getLayoutParams();
            params.height = 500;
            lvPlannedActivity.setLayoutParams(params);
            lvPlannedActivity.requestLayout();
            tvEmpty.setVisibility(View.GONE);
        } else {
            lvPlannedActivity.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        dba.close();
        //</editor-fold>
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

    public static class viewHolder {
        TextView tvPlannerDetailId, tvParameterDetailId, tvActivityId, tvSubActivityId, tvName, tvRemarks, tvUom, tvUniqueId, tvFileName, tvPreviousValue, tvPlanned,tvTotal;
        Button btnViewAttach;
        EditText etActualValue;
        int ref;
    }


    private class PlannedListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listPlannedActivity;
        String _type;
        private Context context2;

        public PlannedListAdapter(Context context,
                                  ArrayList<HashMap<String, String>> listPlannedActivity) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listPlannedActivity = listPlannedActivity;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listPlannedActivity.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final viewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_planned_activity, null);
                holder = new viewHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;
            holder.tvPlannerDetailId = (TextView) convertView
                    .findViewById(R.id.tvPlannerDetailId);
            holder.tvParameterDetailId = (TextView) convertView
                    .findViewById(R.id.tvParameterDetailId);
            holder.tvActivityId = (TextView) convertView
                    .findViewById(R.id.tvActivityId);
            holder.tvSubActivityId = (TextView) convertView
                    .findViewById(R.id.tvSubActivityId);

            holder.tvName = (TextView) convertView
                    .findViewById(R.id.tvName);
            holder.tvUom = (TextView) convertView
                    .findViewById(R.id.tvUom);
            holder.tvRemarks = (TextView) convertView
                    .findViewById(R.id.tvRemarks);
            holder.tvTotal = (TextView) convertView
                    .findViewById(R.id.tvTotal);
            holder.tvPlanned = (TextView) convertView
                    .findViewById(R.id.tvPlanned);
            holder.btnViewAttach = (Button) convertView
                    .findViewById(R.id.btnViewAttach);
            holder.etActualValue = (EditText) convertView
                    .findViewById(R.id.etActualValue);
            holder.tvPreviousValue = (TextView) convertView
                    .findViewById(R.id.tvPreviousValue);
            holder.tvUniqueId = (TextView) convertView
                    .findViewById(R.id.tvUniqueId);
            holder.tvFileName = (TextView) convertView
                    .findViewById(R.id.tvFileName);
            final HashMap<String, String> itemPlannedActivity = _listPlannedActivity.get(position);

            holder.tvPlannerDetailId.setText(itemPlannedActivity.get("PlannerDetailId"));
            holder.tvParameterDetailId.setText(itemPlannedActivity.get("ParameterDetailId"));
            holder.tvActivityId.setText(itemPlannedActivity.get("FarmActivityId"));
            holder.tvSubActivityId.setText(itemPlannedActivity.get("FarmSubActivityId"));
            if (!TextUtils.isEmpty(itemPlannedActivity.get("SubActivityName")))
                holder.tvName.setText(itemPlannedActivity.get("ActivityName") + " - " + itemPlannedActivity.get("SubActivityName"));
            else
                holder.tvName.setText(itemPlannedActivity.get("ActivityName"));
            holder.tvPlanned.setText(String.format("%.2f",Double.valueOf(itemPlannedActivity.get("Quantity"))));

            if (!TextUtils.isEmpty(itemPlannedActivity.get("TotalValue")))
                holder.tvTotal.setText(String.format("%.2f",Double.valueOf(itemPlannedActivity.get("TotalValue"))));

            if (TextUtils.isEmpty(itemPlannedActivity.get("FileName"))) {
                holder.tvName.setTextColor(Color.BLACK);
                holder.tvName.setEnabled(false);
            } else {
                holder.tvName.setTextColor(Color.BLUE);
                holder.tvName.setEnabled(true);
            }
            holder.tvUom.setText(" "+itemPlannedActivity.get("UOM"));
            holder.tvRemarks.setText(itemPlannedActivity.get("Remarks"));
            holder.tvPreviousValue.setText(itemPlannedActivity.get("PreviousValue"));
            holder.etActualValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
            holder.etActualValue.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
            holder.etActualValue.setText(itemPlannedActivity.get("ActualQty"));

            holder.etActualValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (Pattern.matches(fpRegex, holder.etActualValue.getText())) {
                            if (!TextUtils.isEmpty(holder.etActualValue.getText().toString()) && Double.valueOf(holder.etActualValue.getText().toString()) < .01)
                                holder.etActualValue.setText("");
                        } else
                            holder.etActualValue.setText("");
                    }
                }
            });

            if (!TextUtils.isEmpty(itemPlannedActivity.get("UniqueId")))
                holder.tvUniqueId.setText(itemPlannedActivity.get("UniqueId"));
            else
                holder.tvUniqueId.setText(UUID.randomUUID().toString());
            holder.tvFileName.setText(itemPlannedActivity.get("FileName"));

            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    try {

                        String actPath = itemPlannedActivity.get("FileName");
                        int pathLen = actPath.split("/").length;
                        String newPath = actPath.split("/")[pathLen - 4];

                        // common.showToast("New Actual Path="+newPath);
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!");
                        } else {
                            // Locate the image folder in your SD Card
                            File file1 = new File(actPath);
                            filePlanned = new File(file1.getParent());
                        }

                        if (filePlanned.isDirectory()) {

                            listFilePlanned = filePlanned.listFiles(new FilenameFilter() {
                                public boolean accept(File directory, String fileName) {
                                    return fileName.endsWith(".jpeg") || fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for FilePathStrings
                            FilePathStringsPlanned = new String[listFilePlanned.length];
                            // Create a String array for FileNameStrings
                            FileNameStringsPlanned = new String[listFilePlanned.length];

                            for (int i = 0; i < listFilePlanned.length; i++) {

                                // Get the path of the image file
                                if (!listFilePlanned[i].getName().toString().toLowerCase().equals(".nomedia")) {
                                    FilePathStringsPlanned[i] = listFilePlanned[i].getAbsolutePath();
                                    // Get the name image file
                                    FileNameStringsPlanned[i] = listFilePlanned[i].getName();

                                    Intent i1 = new Intent(ReportingStep3.this, ViewImage.class);
                                    // Pass String arrays FilePathStrings
                                    i1.putExtra("filepath", FilePathStringsPlanned);
                                    // Pass String arrays FileNameStrings
                                    i1.putExtra("filename", FileNameStringsPlanned);
                                    // Pass click position
                                    i1.putExtra("position", 0);
                                    startActivity(i1);
                                }
                            }
                        }


                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(ReportingStep3.this, lang.equalsIgnoreCase("en")?"Image available on FarmArt portal. Report already synchronized.":"रिपोर्ट पहले से ही सिंक्रनाइज़ है।छवि FarmArt पोर्टल पर उपलब्ध है", false);

                    }
                }
            });

            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    newActivityId = holder.tvActivityId.getText().toString();
                    newSubActivityId = holder.tvSubActivityId.getText().toString();
                    newActualValue = holder.etActualValue.getText().toString();
                    newplannerDetailId = holder.tvPlannerDetailId.getText().toString();
                    newparameterDetailId = holder.tvParameterDetailId.getText().toString();
                    newUniqueId = holder.tvUniqueId.getText().toString();
                    /*dba.open();
                    dba.insertFarmPlannedDetailsTemp(newActivityId, newSubActivityId,newActualValue, "P", newplannerDetailId, newparameterDetailId, newUniqueId, "");
                    dba.close();*/

                    for (int i = 0; i < lvPlannedActivity.getCount(); i++) {
                        LinearLayout layout1 = (LinearLayout) lvPlannedActivity
                                .getChildAt(i);
                        TextView tvPlannerDetailId = (TextView) layout1.getChildAt(0);
                        TextView tvParameterDetailId = (TextView) layout1.getChildAt(1);
                        TextView tvActivityId = (TextView) layout1.getChildAt(2);
                        TextView tvSubActivityId = (TextView) layout1.getChildAt(3);
                        TextView tvUniqueId = (TextView) layout1.getChildAt(6);
                        LinearLayout layout = (LinearLayout) lvPlannedActivity
                                .getChildAt(i);
                        TableLayout tableLayoutTotal = (TableLayout) layout.getChildAt(4);
                        TableRow tableRow = (TableRow) tableLayoutTotal.getChildAt(0);
                        EditText etActualValue = (EditText) tableRow.getChildAt(3);
                        dba.open();
                        dba.insertFarmPlannedDetailsTemp(tvActivityId.getText().toString(), tvSubActivityId.getText().toString(), etActualValue.getText().toString(), "P", tvPlannerDetailId.getText().toString(), tvParameterDetailId.getText().toString(), tvUniqueId.getText().toString(), "");
                        dba.close();
                    }
                    dba.openR();
                    if (dba.isFileAlreadeUploaded(newActivityId, newSubActivityId, "Planned")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(
                                mContext);
                        builder1.setTitle(lang.equalsIgnoreCase("en")?"Attach Photo":"फोटो अनुलग्न करें");
                        builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to remove existing picture and upload new picture?":"क्या आप वाकई मौजूदा तस्वीर को हटाना चाहते हैं और नई तस्वीर अपलोड करना चाहते हैं?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        holder.tvFileName.setText("");
                                        dba.open();
                                        dba.DeleteJobCardTempFileByType(newActivityId, newSubActivityId, "Planned");
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

            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }

}
