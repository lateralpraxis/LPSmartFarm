package lateralpraxis.lpsmartfarm.farmer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import lateralpraxis.type.FamilyMember;

public class ActivityFarmerRelatives extends Activity {
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
    private String farmerUniqueId = "", farmerName = "", farmerMobile = "", userId;
    private SimpleDateFormat dateFormatter;
    private long birthTime;
    private int isNominee = 0;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> FamilyDetails;
    private static String lang;
    /*------------------Code for Control Declaration---------------*/
    private EditText etName, etBirthDate, etShare;
    private Spinner spRelationShip;
    private TextView tvNameData, tvMobile, tvEmpty;
    private View tvDivider;
    private CheckBox ckIsNominee;
    private Button btnAdd, btnSubmit;
    private RadioButton RadioMale, RadioFemale;
    private RadioGroup RadioGenderType;
    private ListView lvRelativeInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_relatives);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        FamilyDetails = new ArrayList<HashMap<String, String>>();
        dateFormatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);

        HashMap<String, String> user = session.getLoginUserDetails();
        lang = session.getDefaultLang();
        //Setting UserId
        userId = user.get(UserSessionManager.KEY_ID);
        /*------------------------End of code for creating instance of class--------------------*/

         /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*------------------------Start of code for finding controls-----------------------*/
        etName = (EditText) findViewById(R.id.etName);
        etBirthDate = (EditText) findViewById(R.id.etBirthDate);
        etShare = (EditText) findViewById(R.id.etShare);
        spRelationShip = (Spinner) findViewById(R.id.spRelationShip);
        tvNameData = (TextView) findViewById(R.id.tvNameData);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider=(View) findViewById(R.id.tvDivider);
        ckIsNominee = (CheckBox) findViewById(R.id.ckIsNominee);
        etShare.setInputType(InputType.TYPE_NULL);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        RadioMale = (RadioButton) findViewById(R.id.RadioMale);
        RadioFemale = (RadioButton) findViewById(R.id.RadioFemale);
        RadioGenderType = (RadioGroup) findViewById(R.id.RadioGenderType);
        lvRelativeInfoList = (ListView) findViewById(R.id.lvRelativeInfoList);
        etShare.setInputType(InputType.TYPE_NULL);
        /*------------------------End of code for finding controls-----------------------*/

        /*------------------------Start of code for binding data in Spinner-----------------------*/
        spRelationShip.setAdapter(DataAdapter("relationship", ""));

        etBirthDate.setInputType(InputType.TYPE_NULL);
        /*---------------Start of code to set calendar for Birth Date-------------------------*/
        etBirthDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(ActivityFarmerRelatives.this, new DatePickerDialog.OnDateSetListener() {
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
        /*--------------Code to delete family data from Temporary table--------------*/
        dba.open();
        dba.deleteFamilyMembersTempData();
        dba.close();
        /*---------------End of code to set calendar for Birth Date-------------------------*/
 /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("Name");
            farmerMobile = extras.getString("Mobile");
            tvNameData.setText(farmerName);
            tvMobile.setText(farmerMobile);
        }
        /*--------------Code to insert Farmer family Details from Main to Temp Table for Edit--------------*/
        dba.open();
        dba.Insert_FarmerFamilyMainToTemp(farmerUniqueId);
        dba.close();
        /*--------------End of Code to insert Farmer family Details from Main to Temp Table for Edit--------------*/

        /*---------Code to bind list of Family Members---------------------------------*/
        dba.open();
        List<FamilyMember> lables = dba.getFamilyMembers(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("MemberName", String.valueOf(lables.get(i).getMemberName()));
                hm.put("Gender", String.valueOf(lables.get(i).getGender()));
                hm.put("BirthDate", String.valueOf(lables.get(i).getBirthDate()));
                hm.put("Relationship", String.valueOf(lables.get(i).getRelationship()));
                hm.put("IsNominee", String.valueOf(lables.get(i).getIsNominee()));
                hm.put("NomineePercentage", String.valueOf(lables.get(i).getNomineePercentage()));
                FamilyDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ActivityFarmerRelatives.this, FamilyDetails);
        if (lsize > 0) {
            lvRelativeInfoList.setAdapter(Cadapter);
            lvRelativeInfoList.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvRelativeInfoList.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
         /*------------------------Start of code for binding data in Spinner-----------------------*/
        //Code to Validate Total Nominee Percentage Entered is Valid Number or Not
        etShare.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (Pattern.matches(fpRegex, etShare.getText())) {

                    } else
                        etShare.setText("");

                }
            }
        });

        //Check box on check event to display password
        ckIsNominee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etShare.setText("");
                if (!isChecked) {
                    etShare.setInputType(InputType.TYPE_NULL);
                    isNominee = 0;
                } else {
                    etShare.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
                    etShare.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    isNominee = 1;
                }

            }
        });

        /*----Code on Add Button event for adding family members-----------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etShare.clearFocus();
                if (String.valueOf(etName.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Name is mandatory":"नाम अनिवार्य है", 5, 1);
                else if (String.valueOf(etBirthDate.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Birth Date is mandatory":"जन्म तिथि अनिवार्य है", 5, 1);
                else if (spRelationShip.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Relationship is mandatory":"संबंध अनिवार्य है", 5, 1);
                else if (isNominee == 1 && String.valueOf(etShare.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Percentage is mandatory for nominee":"नामांकन के लिए प्रतिशत अनिवार्य है", 5, 1);
                else if (isNominee == 1 && Double.valueOf(etShare.getText().toString()) < .01)
                    common.showToast(lang.equalsIgnoreCase("en")?"Percentage cannot be less than .01":"प्रतिशत .01 से कम नहीं हो सकता", 5, 1);
                else if (isNominee == 1 && Double.valueOf(etShare.getText().toString()) > 100.00)
                    common.showToast(lang.equalsIgnoreCase("en")?"Percentage cannot exceed 100":"प्रतिशत 100 से अधिक नहीं हो सकता", 5, 1);
                else {
                    dba.openR();
                    String gender = "";
                    if (RadioMale.isChecked())
                        gender = "Male";
                    else if (RadioFemale.isChecked())
                        gender = "Female";
                    if (isNominee == 1) {
                        Double totPercentage = dba.totalNomineePercentage(farmerUniqueId, Double.valueOf(etShare.getText().toString()));
                        if (totPercentage <= 100.00) {
                            dba.open();
                            dba.insertFarmerFamilyMemberTemp(farmerUniqueId, etName.getText().toString(), gender, etBirthDate.getText().toString(), String.valueOf(((CustomType) spRelationShip.getSelectedItem()).getId()), String.valueOf(isNominee), etShare.getText().toString(), userId);
                            common.showToast(lang.equalsIgnoreCase("en")?"Family detail added successfully.":"परिवार विवरण सफलतापूर्वक जोड़ा गया।", 5, 3);
                            etName.setText("");
                            etBirthDate.setText("");
                            etShare.setText("");
                            spRelationShip.setSelection(0);
                            ckIsNominee.setChecked(false);
                            FamilyDetails.clear();

                            List<FamilyMember> lables = dba.getFamilyMembers(farmerUniqueId);
                            lsize = lables.size();
                            if (lsize > 0) {
                                tvEmpty.setVisibility(View.GONE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                for (int i = 0; i < lables.size(); i++) {
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("Id", lables.get(i).getId());
                                    hm.put("MemberName", String.valueOf(lables.get(i).getMemberName()));
                                    hm.put("Gender", String.valueOf(lables.get(i).getGender()));
                                    hm.put("BirthDate", String.valueOf(lables.get(i).getBirthDate()));
                                    hm.put("Relationship", String.valueOf(lables.get(i).getRelationship()));
                                    hm.put("IsNominee", String.valueOf(lables.get(i).getIsNominee()));
                                    hm.put("NomineePercentage", String.valueOf(lables.get(i).getNomineePercentage()));
                                    FamilyDetails.add(hm);
                                }
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                            }
                            dba.close();
                            Cadapter = new CustomAdapter(ActivityFarmerRelatives.this, FamilyDetails);
                            if (lsize > 0) {
                                lvRelativeInfoList.setAdapter(Cadapter);
                                lvRelativeInfoList.setVisibility(View.VISIBLE);
                            } else {
                                lvRelativeInfoList.setVisibility(View.GONE);
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        } else
                            common.showToast(lang.equalsIgnoreCase("en")?"Nominee percentage cannot exceed 100":"नामांकित प्रतिशत 100 से अधिक नहीं हो सकता", 5, 1);
                    } else {
                        dba.open();
                        dba.insertFarmerFamilyMemberTemp(farmerUniqueId, etName.getText().toString(), gender, etBirthDate.getText().toString(), String.valueOf(((CustomType) spRelationShip.getSelectedItem()).getId()), String.valueOf(isNominee), etShare.getText().toString(), userId);
                        common.showToast(lang.equalsIgnoreCase("en")?"Family detail added successfully.":"परिवार विवरण सफलतापूर्वक जोड़ा गया।", 5, 3);
                        etName.setText("");
                        etBirthDate.setText("");
                        etShare.setText("");
                        spRelationShip.setSelection(0);
                        ckIsNominee.setChecked(false);
                        FamilyDetails.clear();

                        List<FamilyMember> lables = dba.getFamilyMembers(farmerUniqueId);
                        lsize = lables.size();
                        if (lsize > 0) {
                            tvEmpty.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            for (int i = 0; i < lables.size(); i++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("Id", lables.get(i).getId());
                                hm.put("MemberName", String.valueOf(lables.get(i).getMemberName()));
                                hm.put("Gender", String.valueOf(lables.get(i).getGender()));
                                hm.put("BirthDate", String.valueOf(lables.get(i).getBirthDate()));
                                hm.put("Relationship", String.valueOf(lables.get(i).getRelationship()));
                                hm.put("IsNominee", String.valueOf(lables.get(i).getIsNominee()));
                                hm.put("NomineePercentage", String.valueOf(lables.get(i).getNomineePercentage()));
                                FamilyDetails.add(hm);
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                        }

                        Cadapter = new CustomAdapter(ActivityFarmerRelatives.this, FamilyDetails);
                        if (lsize > 0) {
                            lvRelativeInfoList.setAdapter(Cadapter);
                            lvRelativeInfoList.setVisibility(View.VISIBLE);
                            tvDivider.setVisibility(View.VISIBLE);
                        } else {
                            lvRelativeInfoList.setVisibility(View.GONE);
                            tvDivider.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
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
                int familyCount = 0;
                familyCount = dba.familyDetailCount(farmerUniqueId);
                dba.Insert_FarmerFamilyTempToMain(farmerUniqueId);
                if (familyCount > 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Family details saved successfully":"परिवार के विवरण सफलतापूर्वक सहेजे गए", 5, 3);
                dba.close();
                Intent intent = new Intent(ActivityFarmerRelatives.this, ActivityFarmerLoan.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                finish();
                System.gc();

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
                Intent intent = new Intent(this, FarmerPOAPOI.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityFarmerRelatives.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ"
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityFarmerRelatives.this, ActivityHome.class);
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
        Intent i = new Intent(ActivityFarmerRelatives.this, FarmerPOAPOI.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
        i.putExtra("Name", farmerName);
        i.putExtra("Mobile", farmerMobile);
        startActivity(i);
        this.finish();
        System.gc();
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvFamilyMemberId, tvMemberDetails, tvRelationShip;

        Button btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvRelativeInfoList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            FamilyDetails = lvRelativeInfoList;
        }

        @Override
        public int getCount() {
            return FamilyDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return FamilyDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_family_members, null);
                holder = new ViewHolder();
                holder.tvFamilyMemberId = (TextView) arg1.findViewById(R.id.tvFamilyMemberId);
                holder.tvMemberDetails = (TextView) arg1.findViewById(R.id.tvMemberDetails);
                holder.tvRelationShip = (TextView) arg1.findViewById(R.id.tvRelationShip);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvFamilyMemberId.setText(FamilyDetails.get(arg0).get("Id"));
            holder.tvMemberDetails.setText(FamilyDetails.get(arg0).get("MemberName") + " , " + FamilyDetails.get(arg0).get("Gender") + " - " + FamilyDetails.get(arg0).get("BirthDate").replace("/", "-"));
            if (!TextUtils.isEmpty(FamilyDetails.get(arg0).get("NomineePercentage")))
                holder.tvRelationShip.setText(FamilyDetails.get(arg0).get("Relationship") + " - " + FamilyDetails.get(arg0).get("NomineePercentage") + "%");
            else
                holder.tvRelationShip.setText(FamilyDetails.get(arg0).get("Relationship"));
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Delete family member Detail":"परिवार के सदस्यों का विवरण हटाएं");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure you want to delete this family member detail?":"क्या आप परिवार के इस सदस्य के विवरण को हटाना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"OK":"ठीक",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.deleteFamilyMembersTemp(String.valueOf(holder.tvFamilyMemberId.getText()), farmerUniqueId);
                                    dba.close();
                                    common.showToast(lang.equalsIgnoreCase("en")?"Family member Detail deleted successfully.":"परिवार के सदस्य का विवरण सफलतापूर्वक हटा दिया गया।");
                                    FamilyDetails.clear();

                                    dba.openR();
                                    List<FamilyMember> lables = dba.getFamilyMembers(farmerUniqueId);
                                    lsize = lables.size();
                                    if (lsize > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < lables.size(); i++) {
                                            HashMap<String, String> hm = new HashMap<String, String>();
                                            hm.put("Id", lables.get(i).getId());
                                            hm.put("MemberName", String.valueOf(lables.get(i).getMemberName()));
                                            hm.put("Gender", String.valueOf(lables.get(i).getGender()));
                                            hm.put("BirthDate", String.valueOf(lables.get(i).getBirthDate()));
                                            hm.put("Relationship", String.valueOf(lables.get(i).getRelationship()));
                                            hm.put("IsNominee", String.valueOf(lables.get(i).getIsNominee()));
                                            hm.put("NomineePercentage", String.valueOf(lables.get(i).getNomineePercentage()));
                                            FamilyDetails.add(hm);
                                        }
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                    }

                                    Cadapter = new CustomAdapter(ActivityFarmerRelatives.this, FamilyDetails);
                                    if (lsize > 0) {
                                        lvRelativeInfoList.setAdapter(Cadapter);
                                        lvRelativeInfoList.setVisibility(View.VISIBLE);
                                        tvDivider.setVisibility(View.VISIBLE);
                                    } else {
                                        lvRelativeInfoList.setVisibility(View.GONE);
                                        tvDivider.setVisibility(View.GONE);
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
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
