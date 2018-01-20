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
import android.widget.RadioButton;
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

public class FarmBlockOther extends Activity {
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
    private EditText etDripperSpacing, etDischargeRate;
    private Spinner spExistingLandUse, spCommunityUse, spExistingHazard, spAnyLegalDispute, spNearestRiver, spNearestDam, spIrrigationSystem, spWaterSource, spElectricitySource;
    private Button btnBack, btnNext;
    private LinearLayout llFarmBlock;
    private RadioButton radioNA, radioLT, radioHT;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    /*------------------------End of code for class Declaration------------------------------*/
    private Common common;
    private SimpleDateFormat dateFormatter;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private ArrayList<String> otherData;
    private static String lang;
    /*------------------------Start of code for class Declaration------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block_other);

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

        etDripperSpacing = (EditText) findViewById(R.id.etDripperSpacing);
        etDischargeRate = (EditText) findViewById(R.id.etDischargeRate);

        spExistingLandUse = (Spinner) findViewById(R.id.spExistingLandUse);
        spCommunityUse = (Spinner) findViewById(R.id.spCommunityUse);
        spExistingHazard = (Spinner) findViewById(R.id.spExistingHazard);
        spAnyLegalDispute = (Spinner) findViewById(R.id.spAnyLegalDispute);
        spNearestRiver = (Spinner) findViewById(R.id.spNearestRiver);
        spNearestDam = (Spinner) findViewById(R.id.spNearestDam);
        spIrrigationSystem = (Spinner) findViewById(R.id.spIrrigationSystem);
        spWaterSource = (Spinner) findViewById(R.id.spWaterSource);
        spElectricitySource = (Spinner) findViewById(R.id.spElectricitySource);

        radioNA = (RadioButton) findViewById(R.id.radioNA);
        radioLT = (RadioButton) findViewById(R.id.radioLT);
        radioHT = (RadioButton) findViewById(R.id.radioHT);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        /*---------------Start of code to set Filters and allowed 2 decimal value -------------------------*/
        etDripperSpacing.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etDripperSpacing.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etDischargeRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etDischargeRate.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

         /*------------------------Start of code for binding data in Spinner-----------------------*/
        spExistingLandUse.setAdapter(DataAdapter("existinguse", ""));
        spCommunityUse.setAdapter(DataAdapter("communityuse", ""));
        spExistingHazard.setAdapter(DataAdapter("existinghazard", ""));
        spAnyLegalDispute.setAdapter(DataAdapter("legaldispute", ""));
        spNearestRiver.setAdapter(DataAdapter("nearestriver", ""));
        spNearestDam.setAdapter(DataAdapter("nearestdam", ""));
        spIrrigationSystem.setAdapter(DataAdapter("irrigationsystem", ""));
        spWaterSource.setAdapter(DataAdapter("watersource", ""));
        spElectricitySource.setAdapter(DataAdapter("electricitysource", ""));

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
        etDripperSpacing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etDripperSpacing.getText())) {
                    } else
                        etDripperSpacing.setText("");
                }
            }
        });

        //Code to Validate PHChemical Entered is Valid Number or Not
        etDischargeRate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etDischargeRate.getText())) {
                    } else
                        etDischargeRate.setText("");
                }
            }
        });

        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move back farm block first page
                Intent intent = new Intent(FarmBlockOther.this, FarmBlockSoil.class);
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
                etDripperSpacing.clearFocus();
                etDischargeRate.clearFocus();
                if ((spExistingLandUse.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Existing Land Use is mandatory!":"मौजूदा भूमि उपयोग अनिवार्य है!",5,1);
                else if ((spCommunityUse.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Community Use is mandatory!":"सामुदायिक उपयोग अनिवार्य है!",5,1);
                else if ((spExistingHazard.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Existing Hazard is mandatory!":"मौजूदा ख़तरा अनिवार्य है!",5,1);
                else if ((spAnyLegalDispute.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Any Legal Dispute is mandatory!":"कोई कानूनी विवाद अनिवार्य है!",5,1);
                else if ((spNearestRiver.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Nearest River is mandatory!":"निकटतम नदी अनिवार्य है!",5,1);
                else if ((spNearestDam.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Nearest Dam is mandatory!":"निकटतम बांध अनिवार्य है!",5,1);
                else if ((spIrrigationSystem.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Irrigation System is mandatory!":"सिंचाई प्रणाली अनिवार्य है!",5,1);
                else if ((spWaterSource.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Water Source is mandatory!":"जल स्रोत अनिवार्य है!",5,1);
                else if ((spElectricitySource.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Electricity Source is mandatory!":"बिजली स्रोत अनिवार्य है!",5,1);
                else if (!String.valueOf(etDripperSpacing.getText()).trim().equals("") && Double.valueOf(etDripperSpacing.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Dripper Spacing cannot be 0!":"ड्रिपर स्पेसिंग शून्य नहीं हो सकता!",5,1);
                else if (!String.valueOf(etDischargeRate.getText()).trim().equals("") && Double.valueOf(etDischargeRate.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Discharge Rate cannot be 0!":"डिस्चार्ज रेट शून्य नहीं हो सकता!",5,1);
                else {
                    String transmission = "";
                    if (radioNA.isChecked())
                        transmission = "NA";
                    else if (radioLT.isChecked())
                        transmission = "LT";
                    else if (radioHT.isChecked())
                        transmission = "HT";

                    //To update farm block other details
                    dba.open();
                    dba.updateFarmBlockOther(farmBlockUniqueId, String.valueOf(((CustomType) spExistingLandUse.getSelectedItem()).getId()), String.valueOf(((CustomType) spCommunityUse.getSelectedItem()).getId()), String.valueOf(((CustomType) spExistingHazard.getSelectedItem()).getId()), String.valueOf(((CustomType) spNearestRiver.getSelectedItem()).getId()), String.valueOf(((CustomType) spNearestDam.getSelectedItem()).getId()), String.valueOf(((CustomType) spIrrigationSystem.getSelectedItem()).getId()), transmission, String.valueOf(((CustomType) spAnyLegalDispute.getSelectedItem()).getId()), String.valueOf(((CustomType) spWaterSource.getSelectedItem()).getId()), String.valueOf(((CustomType) spElectricitySource.getSelectedItem()).getId()), etDripperSpacing.getText().toString().trim(), etDischargeRate.getText().toString().trim());
                    dba.close();
                    common.showToast(lang.equalsIgnoreCase("en")?"Other details saved successfully.":"अन्य विवरण सफलतापूर्वक सहेजे गए।",5,3);
                    //To move FarmBlockCroppingPattern page
                    Intent intent = new Intent(FarmBlockOther.this, FarmBlockCroppingPattern.class);
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
          /*---------------Start of code to bind Other Details for Farm Block-------------------------*/
        dba.openR();
        int fbCount = 0;
        fbCount = dba.getFarmBlockCountByFarmerId(farmerUniqueId);
        otherData = dba.getOtherDetailsOfFarmBlockByUniqueId(farmBlockUniqueId);
        int spexuseCnt = spExistingLandUse.getAdapter().getCount();
        for (int i = 0; i < spexuseCnt; i++) {
            if (((CustomType) spExistingLandUse.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(0))))
                spExistingLandUse.setSelection(i);
        }

        int spcomuseCnt = spCommunityUse.getAdapter().getCount();
        for (int i = 0; i < spcomuseCnt; i++) {
            if (((CustomType) spCommunityUse.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(1))))
                spCommunityUse.setSelection(i);
        }

        int sphazCnt = spExistingHazard.getAdapter().getCount();
        for (int i = 0; i < sphazCnt; i++) {
            if (((CustomType) spExistingHazard.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(2))))
                spExistingHazard.setSelection(i);
        }

        int splegCnt = spAnyLegalDispute.getAdapter().getCount();
        for (int i = 0; i < splegCnt; i++) {
            if (((CustomType) spAnyLegalDispute.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(7))))
                spAnyLegalDispute.setSelection(i);
        }

        int spnrCnt = spNearestRiver.getAdapter().getCount();
        for (int i = 0; i < spnrCnt; i++) {
            if (((CustomType) spNearestRiver.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(3))))
                spNearestRiver.setSelection(i);
        }
        if (fbCount == 1 && Integer.valueOf(otherData.get(3)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spnrCn = spNearestRiver.getAdapter().getCount();
                for (int i = 0; i < spnrCn; i++) {
                    if (((CustomType) spNearestRiver.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getRiverId())))
                        spNearestRiver.setSelection(i);
                }
            }
        }

        int spndCnt = spNearestDam.getAdapter().getCount();
        for (int i = 0; i < spndCnt; i++) {
            if (((CustomType) spNearestDam.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(4))))
                spNearestDam.setSelection(i);
        }
        if (fbCount == 1 && Integer.valueOf(otherData.get(4)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spndCn = spNearestDam.getAdapter().getCount();
                for (int i = 0; i < spndCn; i++) {
                    if (((CustomType) spNearestDam.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getDamId())))
                        spNearestDam.setSelection(i);
                }
            }
        }

        int spisCnt = spIrrigationSystem.getAdapter().getCount();
        for (int i = 0; i < spisCnt; i++) {
            if (((CustomType) spIrrigationSystem.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(5))))
                spIrrigationSystem.setSelection(i);
        }
        if (fbCount == 1 && Integer.valueOf(otherData.get(5)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spisCn = spIrrigationSystem.getAdapter().getCount();
                for (int i = 0; i < spisCn; i++) {
                    if (((CustomType) spIrrigationSystem.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getIrrigationSystemId())))
                        spIrrigationSystem.setSelection(i);
                }
            }
        }

        int spwsCnt = spWaterSource.getAdapter().getCount();
        for (int i = 0; i < spwsCnt; i++) {
            if (((CustomType) spWaterSource.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(8))))
                spWaterSource.setSelection(i);
        }
        if (fbCount == 1 && Integer.valueOf(otherData.get(8)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spwsCn = spWaterSource.getAdapter().getCount();
                for (int i = 0; i < spwsCn; i++) {
                    if (((CustomType) spWaterSource.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getWaterSourceId())))
                        spWaterSource.setSelection(i);
                }
            }
        }

        int spesCnt = spElectricitySource.getAdapter().getCount();
        for (int i = 0; i < spesCnt; i++) {
            if (((CustomType) spElectricitySource.getItemAtPosition(i)).getId().equals(Integer.valueOf(otherData.get(9))))
                spElectricitySource.setSelection(i);
        }
        if (fbCount == 1 && Integer.valueOf(otherData.get(9)) == 0) {
            List<FarmerOtherData> list = dba.getFarmerOtherData(farmerUniqueId);
            if (list.size() > 0) {
                int spesCn = spElectricitySource.getAdapter().getCount();
                for (int i = 0; i < spesCn; i++) {
                    if (((CustomType) spElectricitySource.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(0).getElectricitySourceId())))
                        spElectricitySource.setSelection(i);
                }
            }
        }

        if (!TextUtils.isEmpty(otherData.get(10)))
            etDripperSpacing.setText(otherData.get(10));
        if (!TextUtils.isEmpty(otherData.get(11)))
            etDischargeRate.setText(otherData.get(11));

        if (otherData.get(6).equalsIgnoreCase("HT")) {
            radioNA.setChecked(false);
            radioLT.setChecked(false);
            radioHT.setChecked(true);
        } else if (otherData.get(6).equalsIgnoreCase("LT")) {
            radioNA.setChecked(false);
            radioLT.setChecked(true);
            radioHT.setChecked(false);
        } else {
            radioNA.setChecked(true);
            radioLT.setChecked(false);
            radioHT.setChecked(false);
        }
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
                Intent intent = new Intent(FarmBlockOther.this, FarmBlockSoil.class);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockOther.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockOther.this, ActivityHome.class);
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

        Intent intent = new Intent(FarmBlockOther.this, FarmBlockSoil.class);
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