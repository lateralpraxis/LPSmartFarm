package lateralpraxis.lpsmartfarm.farmer;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.farmblock.FarmBlockList;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.type.AttachmentDetails;
import lateralpraxis.type.FamilyMember;
import lateralpraxis.type.FarmerAssetData;
import lateralpraxis.type.LoanDetails;
import lateralpraxis.type.OperationalBlocks;

public class ActivityFarmerView extends Activity {
    final Context context = this;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmerCode, tvFarmerType, tvLanguage, tvFarmerName, tvFarmerImage, tvFatherName, tvGenderName, tvBirthDate, tvEducation, tvTotalAcreage, tvEmail, tvMobile, tvAlternate1, tvAlternate2, tvStreet1, tvStreet2, tvState, tvDistrict, tvBlock, tvPanchayat, tvVillage, tvCity, tvPincode, tvAccount, tvIFSC, tvFSSAI, tvFSSAIReg, tvFSSAIExp, tvBankImage, tvFssaiImage, tvDocEmpty, tvRelativeEmpty, tvLoanEmpty, tvAssetEmpty, tvAssignedBlockEmpty;
    private LinearLayout llBlock, llPanchayat, llVillage, llCity, llBankDetails, llFSSAIDetails;
    private Button btnViewPhoto;
    private ListView lvDocInfoList, lvRelativeInfoList, lvLoanInfoList, lvAssets, lvDocOperationalBlocks;
    private TextView linkUpdateFarmer, linkFarmBlock;
    private View tvDividerOperatingBlock, tvDividerAssets, tvDividerLoan, tvDividerRelative, tvDividerDoc;
    /*-------------------------Code for Variable Declaration---------------------------------------*/
    private ArrayList<String> farmerData;
    private String farmerUniqueId,userRole;
    private int ldocsize = 0;
    private ArrayList<HashMap<String, String>> DocDetails;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;
    UserSessionManager session;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_view);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        //Code to delete Blank Images
        dba.open();
        dba.deleteBlankImages();
        dba.close();
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        DocDetails = new ArrayList<HashMap<String, String>>();
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    /*-----------------Code to set Farmer Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
        }
        /*------------------------Start of code for finding Controls--------------------------*/
        tvFarmerCode = (TextView) findViewById(R.id.tvFarmerCode);
        tvFarmerType = (TextView) findViewById(R.id.tvFarmerType);
        tvLanguage = (TextView) findViewById(R.id.tvLanguage);
        tvFarmerName = (TextView) findViewById(R.id.tvFarmerName);
        tvFarmerImage = (TextView) findViewById(R.id.tvFarmerImage);
        tvFatherName = (TextView) findViewById(R.id.tvFatherName);
        tvGenderName = (TextView) findViewById(R.id.tvGenderName);
        tvBirthDate = (TextView) findViewById(R.id.tvBirthDate);
        tvEducation = (TextView) findViewById(R.id.tvEducation);
        tvTotalAcreage = (TextView) findViewById(R.id.tvTotalAcreage);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvAlternate1 = (TextView) findViewById(R.id.tvAlternate1);
        tvAlternate2 = (TextView) findViewById(R.id.tvAlternate2);
        tvStreet1 = (TextView) findViewById(R.id.tvStreet1);
        tvStreet2 = (TextView) findViewById(R.id.tvStreet2);
        tvState = (TextView) findViewById(R.id.tvState);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);
        tvBlock = (TextView) findViewById(R.id.tvBlock);
        tvPanchayat = (TextView) findViewById(R.id.tvPanchayat);
        tvVillage = (TextView) findViewById(R.id.tvVillage);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvPincode = (TextView) findViewById(R.id.tvPincode);
        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvIFSC = (TextView) findViewById(R.id.tvIFSC);
        tvFSSAI = (TextView) findViewById(R.id.tvFSSAI);
        tvFSSAIReg = (TextView) findViewById(R.id.tvFSSAIReg);
        tvFSSAIExp = (TextView) findViewById(R.id.tvFSSAIExp);
        tvFssaiImage = (TextView) findViewById(R.id.tvFssaiImage);
        tvBankImage = (TextView) findViewById(R.id.tvBankImage);
        tvDocEmpty = (TextView) findViewById(R.id.tvDocEmpty);
        tvRelativeEmpty = (TextView) findViewById(R.id.tvRelativeEmpty);
        tvLoanEmpty = (TextView) findViewById(R.id.tvLoanEmpty);
        tvAssetEmpty = (TextView) findViewById(R.id.tvAssetEmpty);
        tvAssignedBlockEmpty = (TextView) findViewById(R.id.tvAssignedBlockEmpty);
        tvDividerOperatingBlock = (View) findViewById(R.id.tvDividerOperatingBlock);
        tvDividerAssets = (View) findViewById(R.id.tvDividerAssets);
        tvDividerLoan = (View) findViewById(R.id.tvDividerLoan);
        tvDividerRelative = (View) findViewById(R.id.tvDividerRelative);
        tvDividerDoc = (View) findViewById(R.id.tvDividerDoc);
        btnViewPhoto = (Button) findViewById(R.id.btnViewPhoto);
        linkUpdateFarmer = (TextView) findViewById(R.id.linkUpdateFarmer);
        linkFarmBlock = (TextView) findViewById(R.id.linkFarmBlock);

        llBlock = (LinearLayout) findViewById(R.id.llBlock);
        llPanchayat = (LinearLayout) findViewById(R.id.llPanchayat);
        llVillage = (LinearLayout) findViewById(R.id.llVillage);
        llCity = (LinearLayout) findViewById(R.id.llCity);
        llBankDetails = (LinearLayout) findViewById(R.id.llBankDetails);
        llFSSAIDetails = (LinearLayout) findViewById(R.id.llFSSAIDetails);

        lvDocInfoList = (ListView) findViewById(R.id.lvDocInfoList);
        lvRelativeInfoList = (ListView) findViewById(R.id.lvRelativeInfoList);
        lvLoanInfoList = (ListView) findViewById(R.id.lvLoanInfoList);
        lvAssets = (ListView) findViewById(R.id.lvAssets);
        lvDocOperationalBlocks = (ListView) findViewById(R.id.lvDocOperationalBlocks);
        dba.openR();

        userRole = dba.getAllRoles();
        Boolean isFarmerEditable = false;
        isFarmerEditable = dba.isFarmerEditable(farmerUniqueId);
        if (isFarmerEditable || userRole.contains("Farmer"))
            linkUpdateFarmer.setVisibility(View.VISIBLE);
        else
            linkUpdateFarmer.setVisibility(View.GONE);
     /*------------------------End of code for finding Controls--------------------------*/

     /*---------------Start of code to bind Farmer details-------------------------*/
        dba.openR();
        String lang = session.getDefaultLang();
        farmerData = dba.getFarmerDetailsForViewByUniqueId(farmerUniqueId, lang);
        tvFarmerCode.setText(farmerData.get(0));
        tvFarmerType.setText(farmerData.get(2));
        tvLanguage.setText(farmerData.get(27));
        tvFarmerName.setText(farmerData.get(3));
        tvFatherName.setText(farmerData.get(4));
        tvGenderName.setText(farmerData.get(10));
        tvBirthDate.setText(farmerData.get(9).replace("/", "-"));
        tvEducation.setText(farmerData.get(1));
        tvTotalAcreage.setText(farmerData.get(13));
        tvEmail.setText(farmerData.get(5));
        tvMobile.setText(farmerData.get(6));
        tvAlternate1.setText(farmerData.get(7));
        tvAlternate2.setText(farmerData.get(8));
        tvStreet1.setText(farmerData.get(17));
        tvStreet2.setText(farmerData.get(18));
        tvState.setText(farmerData.get(19));
        tvDistrict.setText(farmerData.get(20));
        tvBlock.setText(farmerData.get(21));
        tvPanchayat.setText(farmerData.get(22));
        tvVillage.setText(farmerData.get(23));
        tvCity.setText(farmerData.get(24));
        tvPincode.setText(farmerData.get(25));
        tvFarmerImage.setText(farmerData.get(34) == null ? "" : farmerData.get(34).substring(farmerData.get(34).lastIndexOf("/") + 1));
        if (farmerData.get(26).equalsIgnoreCase("District Based")) {
            llBlock.setVisibility(View.VISIBLE);
            llPanchayat.setVisibility(View.VISIBLE);
            llVillage.setVisibility(View.VISIBLE);
            llCity.setVisibility(View.GONE);
        } else {
            llBlock.setVisibility(View.GONE);
            llPanchayat.setVisibility(View.GONE);
            llVillage.setVisibility(View.GONE);
            llCity.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(farmerData.get(11))) {
            tvAccount.setText(farmerData.get(11));
            tvIFSC.setText(farmerData.get(12));
            tvBankImage.setText(farmerData.get(35).substring(farmerData.get(35).lastIndexOf("/") + 1));
            llBankDetails.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(farmerData.get(14))) {
            tvFSSAI.setText(farmerData.get(14));
            tvFSSAIReg.setText(farmerData.get(15));
            tvFSSAIExp.setText(farmerData.get(16));
            tvFssaiImage.setText(farmerData.get(36).substring(farmerData.get(36).lastIndexOf("/") + 1));
            llFSSAIDetails.setVisibility(View.VISIBLE);
        }
        /*--------Code to be executed on click event of buttons---------------------*/
        linkUpdateFarmer.setOnClickListener(new View.OnClickListener() {
            //On click of view delivery button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, ActivityUpdateFarmer.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                startActivity(intent);
                finish();
            }
        });

        linkFarmBlock.setOnClickListener(new View.OnClickListener() {
            //On click of view delivery button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, FarmBlockList.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerCode", tvFarmerCode.getText());
                intent.putExtra("farmBlockUniqueId", "0");
                intent.putExtra("farmerName", tvFarmerName.getText());
                intent.putExtra("farmerMobile", tvMobile.getText());
                startActivity(intent);
                finish();
            }
        });


        //<editor-fold desc="Code to bind POA/POI Details">
        List<HashMap<String, Object>> poapoiList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<AttachmentDetails> lables = dba.getProofDocumentFromMain(farmerUniqueId,lang);

        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("docType", String.valueOf(lables.get(i).getDocumentType()));
            hm.put("docName", String.valueOf(lables.get(i).getDocumentName()));
            hm.put("docNumber", String.valueOf(lables.get(i).getDocumentNumber()));
            poapoiList.add(hm);
            tvDocEmpty.setVisibility(View.GONE);
        }
        if(lables.size()>0)
            tvDividerDoc.setVisibility(View.VISIBLE);
        else
            tvDividerDoc.setVisibility(View.GONE);
        dba.close();

        // Keys used in Hashmap
        String[] from = {"docType", "docName", "docNumber"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvDocumentType, R.id.tvDocumentName, R.id.tvDocumentNumberValue};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), poapoiList, R.layout.list_attached_documents, from, to);
        lvDocInfoList.setAdapter(adapter);
        //</editor-fold>

        //<editor-fold desc="Code to bind Family Member Details">
        List<HashMap<String, Object>> familyList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<FamilyMember> lablesFamily = dba.getFamilyMemberDetails(farmerUniqueId,lang);

        for (int i = 0; i < lablesFamily.size(); i++) {
            HashMap<String, Object> hmfamily = new HashMap<String, Object>();
            hmfamily.put("FamilyDetais", lablesFamily.get(i).getMemberName() + " , " + lablesFamily.get(i).getGender() + " - " + lablesFamily.get(i).getBirthDate().replace("/", "-"));
            if (!TextUtils.isEmpty(lablesFamily.get(i).getNomineePercentage()))
                hmfamily.put("NomineeDetails", lablesFamily.get(i).getRelationship() + " - " + lablesFamily.get(i).getNomineePercentage() + "%");
            else
                hmfamily.put("NomineeDetails", lablesFamily.get(i).getRelationship());
            familyList.add(hmfamily);
            tvRelativeEmpty.setVisibility(View.GONE);

        }
        if(lablesFamily.size()>0)
            tvDividerRelative.setVisibility(View.VISIBLE);
        else
            tvDividerRelative.setVisibility(View.GONE);
        dba.close();

        // Keys used in Hashmap
        String[] fromFamily = {"FamilyDetais", "NomineeDetails"};
        // Ids of views in listview_layout
        int[] toFamily = {R.id.tvMemberDetails, R.id.tvRelationShip};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter familyadapter = new SimpleAdapter(getBaseContext(), familyList, R.layout.list_family_members, fromFamily, toFamily);
        lvRelativeInfoList.setAdapter(familyadapter);
        //</editor-fold>

        //<editor-fold desc="Code to bind Loan Details">
        List<HashMap<String, Object>> loanList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<LoanDetails> lablesLoan = dba.getLoanDetailsFromMainByFarmerUniqueId(farmerUniqueId,lang);

        for (int i = 0; i < lablesLoan.size(); i++) {
            HashMap<String, Object> hmloan = new HashMap<String, Object>();
            hmloan.put("LoanDetails", lablesLoan.get(i).getLoanSource() + " , " + lablesLoan.get(i).getLoanType());
            hmloan.put("AmountDetails", "Rs. " + lablesLoan.get(i).getLoanAmount() + " , " + "Rs. " + lablesLoan.get(i).getBalanceAmount());
            hmloan.put("TenureDetails", lablesLoan.get(i).getTenure() + " Months" + " , " + lablesLoan.get(i).getROIPercentage() + "%");
            loanList.add(hmloan);
            tvLoanEmpty.setVisibility(View.GONE);
        }
        if(lablesLoan.size()>0)
            tvDividerLoan.setVisibility(View.VISIBLE);
        else
            tvDividerLoan.setVisibility(View.GONE);
        dba.close();

        // Keys used in Hashmap
        String[] fromLoan = {"LoanDetails", "AmountDetails", "TenureDetails"};
        // Ids of views in listview_layout
        int[] toLoan = {R.id.tvLoanDetails, R.id.tvAmountDetails, R.id.tvTenureDetails};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter loanadapter = new SimpleAdapter(getBaseContext(), loanList, R.layout.list_loan_details, fromLoan, toLoan);
        lvLoanInfoList.setAdapter(loanadapter);
        //</editor-fold>

        //<editor-fold desc="Code to bind Asset Details">
        List<HashMap<String, Object>> assetList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<FarmerAssetData> lablesasset = dba.getAssetDetailsFromMainByFarmerUniqueId(farmerUniqueId,lang);

        for (int i = 0; i < lablesasset.size(); i++) {
            HashMap<String, Object> hmasset = new HashMap<String, Object>();
            hmasset.put("Name", lablesasset.get(i).getAssetName());
            hmasset.put("Qty", lablesasset.get(i).getAssetQty());

            assetList.add(hmasset);
            tvAssetEmpty.setVisibility(View.GONE);
        }
        if(lablesasset.size()>0)
            tvDividerAssets.setVisibility(View.VISIBLE);
        else
            tvDividerAssets.setVisibility(View.GONE);
        dba.close();

        // Keys used in Hashmap
        String[] fromAsset = {"Name", "Qty"};
        // Ids of views in listview_layout
        int[] toAsset = {R.id.tvAsset, R.id.tvAssetQty};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter assetadapter = new SimpleAdapter(getBaseContext(), assetList, R.layout.list_asset_view, fromAsset, toAsset);
        lvAssets.setAdapter(assetadapter);
        //</editor-fold>

        //<editor-fold desc="Code to bind Operational Blocks">
        List<HashMap<String, Object>> blocksList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<OperationalBlocks> lableblocks = dba.getOperationalDistrictMain(farmerUniqueId,lang);

        for (int i = 0; i < lableblocks.size(); i++) {
            HashMap<String, Object> hmblocks = new HashMap<String, Object>();
            hmblocks.put("District", lableblocks.get(i).getDistrictName());
            hmblocks.put("Block", lableblocks.get(i).getBlockName());

            blocksList.add(hmblocks);
            tvAssignedBlockEmpty.setVisibility(View.GONE);
        }
        if(lableblocks.size()>0)
            tvDividerOperatingBlock.setVisibility(View.VISIBLE);
        else
            tvDividerOperatingBlock.setVisibility(View.GONE);
        dba.close();

        // Keys used in Hashmap
        String[] fromBlocks = {"District", "Block"};
        // Ids of views in listview_layout
        int[] toBlocks = {R.id.tvDistrictName, R.id.tvBlockName};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter blocksadapter = new SimpleAdapter(getBaseContext(), blocksList, R.layout.list_view_assigned_blocks, fromBlocks, toBlocks);
        lvDocOperationalBlocks.setAdapter(blocksadapter);
        //</editor-fold>
    }

    // When press back button go to home screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                if(userRole.contains("Farmer"))
                    intent = new Intent(ActivityFarmerView.this, ActivityHome.class);
                else
                    intent = new Intent(ActivityFarmerView.this, ActivityFarmerList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(this, ActivityHome.class);
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

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i;
        if(userRole.contains("Farmer"))
        i = new Intent(ActivityFarmerView.this, ActivityHome.class);
        else
            i = new Intent(ActivityFarmerView.this, ActivityFarmerList.class);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
    }

    // To create menu on inflater for displaying home button
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }*/

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
