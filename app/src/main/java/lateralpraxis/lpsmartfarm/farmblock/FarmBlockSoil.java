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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.FarmerOtherData;

public class FarmBlockSoil extends Activity {
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
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock;
    private EditText etMSLElevation, etPHChemical, etNitrogen, etPotash, etPhosphorous, etOrganicCarbon, etMagnesium, etCalcium;
    private Spinner spSoilType;
    private Button btnBack, btnNext;
    private LinearLayout llFarmBlock;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;
    private SimpleDateFormat dateFormatter;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private ArrayList<String> soilData;
    private static String lang;
    /*------------------------End of code for variable Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block_soil);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
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
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);

        etMSLElevation = (EditText) findViewById(R.id.etMSLElevation);
        etPHChemical = (EditText) findViewById(R.id.etPHChemical);
        etNitrogen = (EditText) findViewById(R.id.etNitrogen);
        etPotash = (EditText) findViewById(R.id.etPotash);
        etPhosphorous = (EditText) findViewById(R.id.etPhosphorous);
        etOrganicCarbon = (EditText) findViewById(R.id.etOrganicCarbon);
        etMagnesium = (EditText) findViewById(R.id.etMagnesium);
        etCalcium = (EditText) findViewById(R.id.etCalcium);
        spSoilType = (Spinner) findViewById(R.id.spSoilType);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        //Allowed only 2 decimal value
        etMSLElevation.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etMSLElevation.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPHChemical.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etPHChemical.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etNitrogen.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etNitrogen.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPotash.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etPotash.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPhosphorous.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etPhosphorous.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etMagnesium.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etMagnesium.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etCalcium.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etCalcium.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etOrganicCarbon.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etOrganicCarbon.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);


        /*---------------Start of code to set Filters and allowed 2 decimal value -------------------------*/
        etMSLElevation.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etMSLElevation.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPHChemical.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etPHChemical.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etNitrogen.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etNitrogen.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPotash.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etPotash.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPhosphorous.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etPhosphorous.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etOrganicCarbon.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etOrganicCarbon.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etMagnesium.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etMagnesium.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etCalcium.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etCalcium.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);


         /*------------------------Start of code for binding data in Spinner-----------------------*/
        spSoilType.setAdapter(DataAdapter("soiltype", ""));


        /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }

        //Code to Validate MSLElevation Entered is Valid Number or Not
        etMSLElevation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etMSLElevation.getText())) {
                    } else
                        etMSLElevation.setText("");
                }
            }
        });

        //Code to Validate PHChemical Entered is Valid Number or Not
        etPHChemical.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etPHChemical.getText())) {
                    } else
                        etPHChemical.setText("");
                }
            }
        });

        //Code to Validate Nitrogen Entered is Valid Number or Not
        etNitrogen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etNitrogen.getText())) {
                    } else
                        etNitrogen.setText("");
                }
            }
        });

        //Code to Validate Potash Entered is Valid Number or Not
        etPotash.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etPotash.getText())) {
                    } else
                        etPotash.setText("");
                }
            }
        });


        //Code to Validate Phosphorous Entered is Valid Number or Not
        etPhosphorous.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etPhosphorous.getText())) {
                    } else
                        etPhosphorous.setText("");
                }
            }
        });

        //Code to Validate Organic Carbon Entered is Valid Number or Not
        etOrganicCarbon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etOrganicCarbon.getText())) {
                    } else
                        etOrganicCarbon.setText("");
                }
            }
        });

        //Code to Validate Magnesium Entered is Valid Number or Not
        etMagnesium.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etMagnesium.getText())) {
                    } else
                        etMagnesium.setText("");
                }
            }
        });

        //Code to Validate Calcium Entered is Valid Number or Not
        etCalcium.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etCalcium.getText())) {
                    } else
                        etCalcium.setText("");
                }
            }
        });

        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move on farm block first page
                Intent intent;
                if (entryType.equalsIgnoreCase("add"))
                    intent = new Intent(FarmBlockSoil.this, FarmBlock.class);
                else
                    intent = new Intent(FarmBlockSoil.this, FarmBlockUpdate.class);
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

        //To validate and save records
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                etMSLElevation.clearFocus();
                etPHChemical.clearFocus();
                etNitrogen.clearFocus();
                etPotash.clearFocus();
                etPhosphorous.clearFocus();
                etOrganicCarbon.clearFocus();
                etMagnesium.clearFocus();
                etCalcium.clearFocus();
                if ((spSoilType.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Soil Type is mandatory!":"मिट्टी प्रकार अनिवार्य है!",5,1);
                else if (!String.valueOf(etMSLElevation.getText()).trim().equals("") && Double.valueOf(etMSLElevation.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"MSL Elevation cannot be 0!":"एमएसएल ऊंचाई शून्य नहीं हो सकती!",5,1);
                else if (!String.valueOf(etPHChemical.getText()).trim().equals("") && Double.valueOf(etPHChemical.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"PH (Chemical) cannot be 0!":"पीएच (रासायनिक) शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etNitrogen.getText()).trim().equals("") && Double.valueOf(etNitrogen.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Nitrogen cannot be 0!":"नाइट्रोजन शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etPotash.getText()).trim().equals("") && Double.valueOf(etPotash.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Potash cannot be 0!":"पोटाश शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etPhosphorous.getText()).trim().equals("") && Double.valueOf(etPhosphorous.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Phosphorous cannot be 0!":"Phosphorous cannot be zero!",5,1);
                else if (!String.valueOf(etOrganicCarbon.getText()).trim().equals("") && Double.valueOf(etOrganicCarbon.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Organic Carbon(%) cannot be 0!":"कार्बनिक कार्बन(%) शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etOrganicCarbon.getText()).trim().equals("") && Double.valueOf(etOrganicCarbon.getText().toString()) > 100.00)
                    common.showToast(lang.equalsIgnoreCase("en")?"Organic carbon (%) cannot exceed 100!":"कार्बनिक कार्बन (%) 100 से अधिक नहीं हो सकता!",5,1);
                else if (!String.valueOf(etMagnesium.getText()).trim().equals("") && Double.valueOf(etMagnesium.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Magnesium cannot be 0!":"मैग्नेशियम शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etCalcium.getText()).trim().equals("") && Double.valueOf(etCalcium.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Calcium cannot be 0!":"कैल्शियम शून्य नहीं हो सकता!",5,1);
                else {
                    //To update farm block details
                    dba.open();
                    dba.updateFarmBlockSoil(farmBlockUniqueId, String.valueOf(((CustomType) spSoilType.getSelectedItem()).getId()), etMSLElevation.getText().toString().trim(), etPHChemical.getText().toString().trim(), etNitrogen.getText().toString().trim(), etPotash.getText().toString().trim(), etPhosphorous.getText().toString().trim(), etOrganicCarbon.getText().toString().trim(), etMagnesium.getText().toString().trim(), etCalcium.getText().toString().trim());
                    dba.close();
                    common.showToast(lang.equalsIgnoreCase("en")?"Soil details saved successfully.":"मिट्टी विवरण सफलतापूर्वक सहेजे गए।",5,3);
                    //To move farm block other page
                    Intent intent = new Intent(FarmBlockSoil.this, FarmBlockOther.class);
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
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/

        /*---------------Start of code to bind Soil Details for Farm Block-------------------------*/

        dba.openR();
        soilData = dba.getSoilDetailsOfFarmBlockByUniqueId(farmBlockUniqueId);
        int spstypeCnt = spSoilType.getAdapter().getCount();
        for (int i = 0; i < spstypeCnt; i++) {
            if (((CustomType) spSoilType.getItemAtPosition(i)).getId().equals(Integer.valueOf(soilData.get(0))))
                spSoilType.setSelection(i);
        }

        int fbCount = 0;
        fbCount = dba.getFarmBlockCountByFarmerId(farmerUniqueId);
        soilData = dba.getSoilDetailsOfFarmBlockByUniqueId(farmBlockUniqueId);
        if (fbCount == 1 && Integer.valueOf(soilData.get(0)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spstypeCn = spSoilType.getAdapter().getCount();
                for (int i = 0; i < spstypeCn; i++) {
                    if (((CustomType) spSoilType.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getSoilTypeId())))
                        spSoilType.setSelection(i);
                }
            }
        }
        if (!TextUtils.isEmpty(soilData.get(1)))
            etMSLElevation.setText(soilData.get(1));
        if (!TextUtils.isEmpty(soilData.get(2)))
            etPHChemical.setText(soilData.get(2));
        if (!TextUtils.isEmpty(soilData.get(3)))
            etNitrogen.setText(soilData.get(3));
        if (!TextUtils.isEmpty(soilData.get(4)))
            etPotash.setText(soilData.get(4));
        if (!TextUtils.isEmpty(soilData.get(5)))
            etPhosphorous.setText(soilData.get(5));
        if (!TextUtils.isEmpty(soilData.get(6)))
            etOrganicCarbon.setText(soilData.get(6));
        if (!TextUtils.isEmpty(soilData.get(7)))
            etMagnesium.setText(soilData.get(7));
        if (!TextUtils.isEmpty(soilData.get(8)))
            etCalcium.setText(soilData.get(8));
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
                Intent intent;
                if (entryType.equalsIgnoreCase("add"))
                    intent = new Intent(FarmBlockSoil.this, FarmBlock.class);
                else
                    intent = new Intent(FarmBlockSoil.this, FarmBlockUpdate.class);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockSoil.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockSoil.this, ActivityHome.class);
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
        Intent intent;
        if (entryType.equalsIgnoreCase("add"))
            intent = new Intent(FarmBlockSoil.this, FarmBlock.class);
        else
            intent = new Intent(FarmBlockSoil.this, FarmBlockUpdate.class);
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