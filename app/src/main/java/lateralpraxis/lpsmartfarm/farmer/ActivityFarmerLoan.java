package lateralpraxis.lpsmartfarm.farmer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.LoanDetails;

public class ActivityFarmerLoan extends Activity {
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
    CustomAdapter Cadapter;
    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId = "", farmerName = "", farmerMobile = "", userId = "";
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> LoanDetails;
    private static String lang;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvNameData, tvMobile, tvEmpty;
    private View tvDivider;
    private EditText etLoanAmt, etBalAmt, etTenure, etROI;
    private Spinner spSource, spLoanType;
    private Button btnAdd, btnSubmit, btnNA;
    private ListView lvLoanInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_loan);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        LoanDetails = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        //Setting UserId
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        /*------------------------End of code for creating instance of class--------------------*/


            /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
         /*------------------------Start of code for finding controls-----------------------*/
        tvNameData = (TextView) findViewById(R.id.tvNameData);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        etLoanAmt = (EditText) findViewById(R.id.etLoanAmt);
        etBalAmt = (EditText) findViewById(R.id.etBalAmt);
        etTenure = (EditText) findViewById(R.id.etTenure);
        etROI = (EditText) findViewById(R.id.etROI);
        spSource = (Spinner) findViewById(R.id.spSource);
        spLoanType = (Spinner) findViewById(R.id.spLoanType);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnNA= (Button) findViewById(R.id.btnNA);

        etLoanAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etLoanAmt.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etBalAmt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etBalAmt.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etROI.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etROI.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        lvLoanInfoList = (ListView) findViewById(R.id.lvLoanInfoList);
        /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("Name");
            farmerMobile = extras.getString("Mobile");
            tvNameData.setText(farmerName);
            tvMobile.setText(farmerMobile);
        }
        /*--------------Code to delete Loan data from Temporary table--------------*/
        dba.open();
        dba.deleteLoanDetailsTempData();
        dba.close();
        /*--------------Code to insert Farmer Loan Details from Main to Temp Table for Edit--------------*/
        dba.open();
        dba.Insert_FarmerLoanMainToTemp(farmerUniqueId);
        dba.close();
        /*--------------End of Code to insert Farmer Loan Details from Main to Temp Table for Edit--------------*/
          /*------------------------Start of code for binding data in Spinner-----------------------*/
        spSource.setAdapter(DataAdapter("loansource", ""));
        spLoanType.setAdapter(DataAdapter("loantype", ""));

        /*---------Code to bind list of Loan Details---------------------------------*/
        dba.open();
        List<LoanDetails> lables = dba.getLoanDetailsByFarmerUniqueId(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            btnNA.setVisibility(View.GONE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getLoanId());
                hm.put("LoanSource", String.valueOf(lables.get(i).getLoanSource()));
                hm.put("LoanType", String.valueOf(lables.get(i).getLoanType()));
                hm.put("ROIPercentage", String.valueOf(lables.get(i).getROIPercentage()));
                hm.put("LoanAmount", String.valueOf(lables.get(i).getLoanAmount()));
                hm.put("BalanceAmount", String.valueOf(lables.get(i).getBalanceAmount()));
                hm.put("Tenure", String.valueOf(lables.get(i).getTenure()));
                LoanDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
            btnNA.setVisibility(View.VISIBLE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ActivityFarmerLoan.this, LoanDetails);
        if (lsize > 0) {
            lvLoanInfoList.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvLoanInfoList.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            lvLoanInfoList.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
        }
        //Code to Validate Loan Amount Entered is Valid Number or Not
        etLoanAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etLoanAmt.getText())) {

                    } else
                        etLoanAmt.setText("");

                }
            }
        });

        //Code to Validate Balance Amount Entered is Valid Number or Not
        etBalAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etBalAmt.getText())) {

                    } else
                        etBalAmt.setText("");

                }
            }
        });

        //Code to Validate Balance Amount Entered is Valid Number or Not
        etROI.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etROI.getText())) {

                    } else
                        etROI.setText("");

                }
            }
        });




        /*----Code on Add Button event for adding family members-----------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etLoanAmt.clearFocus();
                etBalAmt.clearFocus();
                etROI.clearFocus();
                if (spSource.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan source is mandatory":"ऋण स्रोत अनिवार्य है", 5, 1);
                else if (spLoanType.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan type is mandatory":"ऋण प्रकार अनिवार्य है", 5, 1);
                else if (TextUtils.isEmpty(etLoanAmt.getText().toString().trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan amount is mandatory":"ऋण राशि अनिवार्य है", 5, 1);
                else if (!TextUtils.isEmpty(etLoanAmt.getText().toString().trim()) && Double.valueOf(etLoanAmt.getText().toString().trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan Amount cannot be zero.":"ऋण राशि शून्य नहीं हो सकती।", 5, 1);
                else if (TextUtils.isEmpty(etBalAmt.getText().toString().trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Balance amount is mandatory":"शेष राशि अनिवार्य है", 5, 1);
                else if (!TextUtils.isEmpty(etBalAmt.getText().toString().trim()) && Double.valueOf(etBalAmt.getText().toString().trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Balance cannot be zero.":"बैलेंस शून्य नहीं हो सकता।", 5, 1);
                else if (Double.valueOf(etBalAmt.getText().toString()) > Double.valueOf(etLoanAmt.getText().toString()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Balance cannot exceed loan amount":"शेष राशि ऋण राशि से अधिक नहीं हो सकती", 5, 1);
                else if (TextUtils.isEmpty(etTenure.getText().toString().trim()))
                    common.showToast(lang.equalsIgnoreCase("en")?"Tenure is mandatory":"कार्यकाल अनिवार्य है", 5, 1);
                else if (!TextUtils.isEmpty(etTenure.getText().toString().trim()) && Double.valueOf(etTenure.getText().toString().trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Tenure cannot be zero.":"कार्यकाल शून्य नहीं हो सकता।", 5, 1);
                else if (String.valueOf(etROI.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"ROI is mandatory":"आरओआई अनिवार्य है", 5, 1);
                else if (!TextUtils.isEmpty(etROI.getText().toString().trim()) && Double.valueOf(etROI.getText().toString().trim()) <= 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"ROI cannot be zero.":"आरओआई शून्य नहीं हो सकता।", 5, 1);
                else if (!TextUtils.isEmpty(etROI.getText().toString().trim()) && Double.valueOf(etROI.getText().toString()) > 100)
                    common.showToast(lang.equalsIgnoreCase("en")?"ROI cannot exceed 100.":"आरओआई 100 से अधिक नहीं हो सकता", 5, 1);
                else {

                    dba.open();
                    dba.insertFarmerLoanDetailsTemp(farmerUniqueId, String.valueOf(((CustomType) spSource.getSelectedItem()).getId()), String.valueOf(((CustomType) spLoanType.getSelectedItem()).getId()), etROI.getText().toString(), etLoanAmt.getText().toString(), etBalAmt.getText().toString(), etTenure.getText().toString(), userId);
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan detail added successfully.":"ऋण विवरण सफलतापूर्वक जोड़ा गया।", 5, 3);
                    etLoanAmt.setText("");
                    etBalAmt.setText("");
                    etTenure.setText("");
                    etROI.setText("");
                    spSource.setSelection(0);
                    spLoanType.setSelection(0);
                    LoanDetails.clear();

                    dba.open();
                    List<LoanDetails> lables = dba.getLoanDetailsByFarmerUniqueId(farmerUniqueId);
                    lsize = lables.size();
                    if (lsize > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        btnNA.setVisibility(View.GONE);
                        for (int i = 0; i < lables.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("Id", lables.get(i).getLoanId());
                            hm.put("LoanSource", String.valueOf(lables.get(i).getLoanSource()));
                            hm.put("LoanType", String.valueOf(lables.get(i).getLoanType()));
                            hm.put("ROIPercentage", String.valueOf(lables.get(i).getROIPercentage()));
                            hm.put("LoanAmount", String.valueOf(lables.get(i).getLoanAmount()));
                            hm.put("BalanceAmount", String.valueOf(lables.get(i).getBalanceAmount()));
                            hm.put("Tenure", String.valueOf(lables.get(i).getTenure()));
                            LoanDetails.add(hm);
                        }
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        btnNA.setVisibility(View.VISIBLE);
                    }
                    dba.close();

                    Cadapter = new CustomAdapter(ActivityFarmerLoan.this, LoanDetails);
                    if (lsize > 0) {
                        lvLoanInfoList.setAdapter(Cadapter);
                        tvEmpty.setVisibility(View.GONE);
                        lvLoanInfoList.setVisibility(View.VISIBLE);
                        tvDivider.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        lvLoanInfoList.setVisibility(View.GONE);
                        tvDivider.setVisibility(View.GONE);
                    }
                    dba.close();
                }
            }
        });
        /*-------------------Code on Submit Button Click Event to Move Data from Temp To Main Table----------------------------*/
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                dba.open();
                int loancount = 0;
                loancount = dba.loanDetailCount(farmerUniqueId);
                dba.Insert_FarmerLoanTempToMain(farmerUniqueId);
                if (loancount > 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Loan details saved successfully":"ऋण विवरण सफलतापूर्वक सहेजा गया", 5, 3);
                dba.close();
                Intent intent = new Intent(ActivityFarmerLoan.this, ActivityFarmerAssets.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                finish();
                System.gc();

            }
        });
        /*---------------End of code on Submit Button Click Event to Move Data from Temp To Main Table-------------------------*/

        /*-------------------Code on Not Applicable Button Click Event to Move Data from Temp To Main Table----------------------------*/
        btnNA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, no loan is applicable for the farmer?":"क्या आप निश्चित हैं कि किसान के लिए कोई ऋण लागू नहीं है?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dba.open();
                                dba.updateLoanStatus(farmerUniqueId);
                                dba.close();
                                Intent intent = new Intent(ActivityFarmerLoan.this, ActivityFarmerAssets.class);
                                intent.putExtra("farmerUniqueId", farmerUniqueId);
                                intent.putExtra("Name", farmerName);
                                intent.putExtra("Mobile", farmerMobile);
                                startActivity(intent);
                                finish();
                                System.gc();
                            }
                        })
                        .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
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
        });
        /*---------------End of code on Submit Button Click Event to Move Data from Temp To Main Table-------------------------*/

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
                Intent intent = new Intent(this, ActivityFarmerRelatives.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityFarmerLoan.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityFarmerLoan.this, ActivityHome.class);
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

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityFarmerLoan.this, ActivityFarmerRelatives.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
        i.putExtra("Name", farmerName);
        i.putExtra("Mobile", farmerMobile);
        startActivity(i);
        this.finish();
        System.gc();
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvLoanId, tvLoanDetails, tvTenureDetails, tvAmountDetails;
        Button btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvLoanInfoList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            LoanDetails = lvLoanInfoList;
        }

        @Override
        public int getCount() {
            return LoanDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return LoanDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_loan_details, null);
                holder = new ViewHolder();

                holder.tvLoanId = (TextView) arg1.findViewById(R.id.tvLoanId);
                holder.tvLoanDetails = (TextView) arg1.findViewById(R.id.tvLoanDetails);
                holder.tvTenureDetails = (TextView) arg1.findViewById(R.id.tvTenureDetails);
                holder.tvAmountDetails = (TextView) arg1.findViewById(R.id.tvAmountDetails);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvLoanId.setText(LoanDetails.get(arg0).get("Id"));
            holder.tvLoanDetails.setText(LoanDetails.get(arg0).get("LoanSource") + " , " + LoanDetails.get(arg0).get("LoanType"));
            holder.tvAmountDetails.setText("Rs. " + LoanDetails.get(arg0).get("LoanAmount") + " , " + "Rs. " + LoanDetails.get(arg0).get("BalanceAmount"));
            holder.tvTenureDetails.setText(LoanDetails.get(arg0).get("Tenure") + " Months" + " , " + LoanDetails.get(arg0).get("ROIPercentage") + "%");
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Delete Loan Detail":"ऋण विवरण हटाएं");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure you want to delete this loan detail?":"क्या आप निश्चित रूप से इस ऋण के विवरण को हटाना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"OK":"ठीक",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.deleteFarmerLoanDetailsTemp(String.valueOf(holder.tvLoanId.getText()), farmerUniqueId);
                                    dba.close();
                                    common.showToast(lang.equalsIgnoreCase("en")?"Loan Detail deleted successfully.":"ऋण विस्तारपूर्वक सफलतापूर्वक हटा दिया गया।");
                                    LoanDetails.clear();

                                    dba.open();
                                    List<LoanDetails> lables = dba.getLoanDetailsByFarmerUniqueId(farmerUniqueId);
                                    lsize = lables.size();
                                    if (lsize > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                        btnNA.setVisibility(View.GONE);
                                        for (int i = 0; i < lables.size(); i++) {
                                            HashMap<String, String> hm = new HashMap<String, String>();
                                            hm.put("Id", lables.get(i).getLoanId());
                                            hm.put("LoanSource", String.valueOf(lables.get(i).getLoanSource()));
                                            hm.put("LoanType", String.valueOf(lables.get(i).getLoanType()));
                                            hm.put("ROIPercentage", String.valueOf(lables.get(i).getROIPercentage()));
                                            hm.put("LoanAmount", String.valueOf(lables.get(i).getLoanAmount()));
                                            hm.put("BalanceAmount", String.valueOf(lables.get(i).getBalanceAmount()));
                                            hm.put("Tenure", String.valueOf(lables.get(i).getTenure()));
                                            LoanDetails.add(hm);
                                        }
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        btnSubmit.setVisibility(View.GONE);
                                        btnNA.setVisibility(View.VISIBLE);
                                    }
                                    dba.close();

                                    Cadapter = new CustomAdapter(ActivityFarmerLoan.this, LoanDetails);
                                    if (lsize > 0) {
                                        lvLoanInfoList.setAdapter(Cadapter);
                                        tvEmpty.setVisibility(View.GONE);
                                        lvLoanInfoList.setVisibility(View.VISIBLE);
                                        tvDivider.setVisibility(View.VISIBLE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        lvLoanInfoList.setVisibility(View.GONE);
                                        tvDivider.setVisibility(View.GONE);
                                    }
                                    dba.close();
                                }
                            })
                            .setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं", new DialogInterface.OnClickListener() {
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
