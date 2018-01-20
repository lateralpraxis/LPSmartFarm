package lateralpraxis.lpsmartfarm.nursery;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.type.CustomCoord;
import lateralpraxis.type.LandCharacteristic;
import lateralpraxis.type.NurseryCroppingPatternData;
import lateralpraxis.type.NurseryViewData;

public class NurseryView extends Activity {
    final Context context = this;
    CustomAdapter Cadapter;
    CustomAdapterCharacteristic CadapterCharacterstic;
    CustomAdapterLandIssue CadapterLandIssue;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvNurseryType, tvNurseryName, tvConactPerson, tvConactPersonEmail, tvType, tvRegDate,
            tvOffPremises, tvMobile1, tvMobile2, tvGSTNo, tvGSTDate, tvOwnerName, tvLogInId,
            tvContactNo, tvCertifiedBy, tvRegNo, tvRegistrationDate, tvAssociateMainNur, tvKhata,
            tvKhasra, tvContractDate, tvArea, tvStreet1, tvStreet2, tvState, tvDistrict, tvBlock,
            tvPanchayat, tvVillage, tvPincode, tvSoilType, tvMSLElevation, tvPHChemical,
            tvNitrogen, tvPotash, tvPhosphorous, tvOrganicCarbon, tvMagnesium, tvCalcium,
            tvExistingLandUse, tvCommunityUse, tvExistingHazard, tvAnyLegalDispute,
            tvNearestRiver, tvNearestDam, tvIrrigationSystem, tvWaterSource, tvElectricitySource,
            tvDripperSpacing, tvDischargeRate, tvOverheadTrans, tvEmptyAccount, tvCity;

    private TextView linkZone, tvEmptyCroppingPattern, tvEmptyCharacterstic, tvEmptyLandIssue,
            tvEmptyCoordinates;
    private View tvDividerCoordinates, tvDividerIssues, tvDividerCharacterstic, tvDividerCroppingPattern;
    private Button btnAddGps;
    private LinearLayout llMiniNurseryDetails,llBlock, llPanchayat, llVillage, llCity;
    private ListView lvInfoListCroppingPattern, lvCharacterstic, lvIssues, lvCoordinates, lvAccount;

    private SimpleDateFormat dateFormatter_display;
    /*-------------------------Code for Variable
    Declaration---------------------------------------*/
    private List<NurseryViewData> obj;
    private String nurseryUniqueId, nurseryId, nurseryZoneId, nurType, nurName;
    private int lsize = 0, count = 0;
    private ArrayList<HashMap<String, String>> nurseryCroppingPatternData, CharacteristicDetails,
            IssueDetails;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_view);
         /*----Start of code for creating instance of
         class----*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        nurseryCroppingPatternData = new ArrayList<HashMap<String, String>>();
        CharacteristicDetails = new ArrayList<HashMap<String, String>>();
        IssueDetails = new ArrayList<HashMap<String, String>>();
        dateFormatter_display = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

	    /*-----------------Code to set Nursery Id and Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryZoneId = extras.getString("nurseryZoneId");
            nurType = extras.getString("nurType");
            nurName = extras.getString("nurName");
        }

        /*------------------------Start of code for finding Controls--------------------------*/
        linkZone = (TextView) findViewById(R.id.linkZone);
        btnAddGps = (Button) findViewById(R.id.btnAddGps);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvConactPerson = (TextView) findViewById(R.id.tvConactPerson);
        tvConactPersonEmail = (TextView) findViewById(R.id.tvConactPersonEmail);
        tvType = (TextView) findViewById(R.id.tvType);
        tvRegDate = (TextView) findViewById(R.id.tvRegDate);
        tvOffPremises = (TextView) findViewById(R.id.tvOffPremises);
        tvMobile1 = (TextView) findViewById(R.id.tvMobile1);
        tvMobile2 = (TextView) findViewById(R.id.tvMobile2);
        tvGSTNo = (TextView) findViewById(R.id.tvGSTNo);
        tvGSTDate = (TextView) findViewById(R.id.tvGSTDate);
        tvOwnerName = (TextView) findViewById(R.id.tvOwnerName);
        tvLogInId = (TextView) findViewById(R.id.tvLogInId);
        tvContactNo = (TextView) findViewById(R.id.tvContactNo);
        tvCertifiedBy = (TextView) findViewById(R.id.tvCertifiedBy);
        tvRegNo = (TextView) findViewById(R.id.tvRegNo);
        tvRegistrationDate = (TextView) findViewById(R.id.tvRegistrationDate);
        tvAssociateMainNur = (TextView) findViewById(R.id.tvAssociateMainNur);
        tvKhata = (TextView) findViewById(R.id.tvKhata);
        tvKhasra = (TextView) findViewById(R.id.tvKhasra);
        tvContractDate = (TextView) findViewById(R.id.tvContractDate);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvStreet1 = (TextView) findViewById(R.id.tvStreet1);
        tvStreet2 = (TextView) findViewById(R.id.tvStreet2);
        tvState = (TextView) findViewById(R.id.tvState);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);
        tvBlock = (TextView) findViewById(R.id.tvBlock);
        tvPanchayat = (TextView) findViewById(R.id.tvPanchayat);
        tvVillage = (TextView) findViewById(R.id.tvVillage);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvPincode = (TextView) findViewById(R.id.tvPincode);
        tvSoilType = (TextView) findViewById(R.id.tvSoilType);
        tvMSLElevation = (TextView) findViewById(R.id.tvMSLElevation);
        tvPHChemical = (TextView) findViewById(R.id.tvPHChemical);
        tvNitrogen = (TextView) findViewById(R.id.tvNitrogen);
        tvPotash = (TextView) findViewById(R.id.tvPotash);
        tvPhosphorous = (TextView) findViewById(R.id.tvPhosphorous);
        tvOrganicCarbon = (TextView) findViewById(R.id.tvOrganicCarbon);
        tvMagnesium = (TextView) findViewById(R.id.tvMagnesium);
        tvCalcium = (TextView) findViewById(R.id.tvCalcium);
        tvExistingLandUse = (TextView) findViewById(R.id.tvExistingLandUse);
        tvCommunityUse = (TextView) findViewById(R.id.tvCommunityUse);
        tvExistingHazard = (TextView) findViewById(R.id.tvExistingHazard);
        tvAnyLegalDispute = (TextView) findViewById(R.id.tvAnyLegalDispute);
        tvNearestRiver = (TextView) findViewById(R.id.tvNearestRiver);
        tvNearestDam = (TextView) findViewById(R.id.tvNearestDam);
        tvIrrigationSystem = (TextView) findViewById(R.id.tvIrrigationSystem);
        tvWaterSource = (TextView) findViewById(R.id.tvWaterSource);
        tvElectricitySource = (TextView) findViewById(R.id.tvElectricitySource);
        tvDripperSpacing = (TextView) findViewById(R.id.tvDripperSpacing);
        tvDischargeRate = (TextView) findViewById(R.id.tvDischargeRate);
        tvOverheadTrans = (TextView) findViewById(R.id.tvOverheadTrans);
        tvEmptyCroppingPattern = (TextView) findViewById(R.id.tvEmptyCroppingPattern);
        lvInfoListCroppingPattern = (ListView) findViewById(R.id.lvInfoListCroppingPattern);
        tvEmptyCharacterstic = (TextView) findViewById(R.id.tvEmptyCharacterstic);
        lvCharacterstic = (ListView) findViewById(R.id.lvCharacterstic);
        lvIssues = (ListView) findViewById(R.id.lvIssues);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        tvEmptyLandIssue = (TextView) findViewById(R.id.tvEmptyLandIssue);
        tvEmptyCoordinates = (TextView) findViewById(R.id.tvEmptyCoordinates);

        tvDividerCoordinates = (View) findViewById(R.id.tvDividerCoordinates);
        tvDividerIssues = (View) findViewById(R.id.tvDividerIssues);
        tvDividerCharacterstic = (View) findViewById(R.id.tvDividerCharacterstic);
        tvDividerCroppingPattern = (View) findViewById(R.id.tvDividerCroppingPattern);

        llMiniNurseryDetails = (LinearLayout) findViewById(R.id.llMiniNurseryDetails);
        llBlock = (LinearLayout) findViewById(R.id.llBlock);
        llPanchayat = (LinearLayout) findViewById(R.id.llPanchayat);
        llVillage = (LinearLayout) findViewById(R.id.llVillage);
        llCity = (LinearLayout) findViewById(R.id.llCity);
        tvEmptyAccount = (TextView) findViewById(R.id.tvEmptyAccount);
        lvAccount = (ListView) findViewById(R.id.lvAccount);
     /*------------------------End of code for finding Controls--------------------------*/

     /*---------------Start of code to bind Nursery details-------------------------*/
        dba.openR();
        obj = dba.getNurseryById(nurseryUniqueId);
        tvNurseryType.setText(obj.get(0).getNurseryType());
        tvNurseryName.setText(obj.get(0).getTitle());
        tvConactPerson.setText(obj.get(0).getContactPerson());
        tvConactPersonEmail.setText(obj.get(0).getEmailId());
        tvType.setText(obj.get(0).getLandType());
        tvRegDate.setText(obj.get(0).getRegistryDate());
        tvOffPremises.setText(obj.get(0).getOfficePrimise());
        tvMobile1.setText(obj.get(0).getMobile());
        tvMobile2.setText(obj.get(0).getAlternateMobile());
        tvGSTNo.setText(obj.get(0).getGSTNo());
        tvGSTDate.setText(obj.get(0).getGSTDate());
        tvOwnerName.setText(obj.get(0).getOwnerName());
        tvLogInId.setText(obj.get(0).getLoginId());
        tvContactNo.setText(obj.get(0).getContactNo());
        tvCertifiedBy.setText(obj.get(0).getCertifiedBy());
        tvRegNo.setText(obj.get(0).getRegistrationNo());
        tvRegistrationDate.setText(obj.get(0).getRegistrationDate());
        tvAssociateMainNur.setText(obj.get(0).getMainNursery());
        tvKhata.setText(obj.get(0).getKhataNo());
        tvKhasra.setText(obj.get(0).getKhasraNo());
        if (obj.get(0).getNurseryType().equalsIgnoreCase("mini"))
            llMiniNurseryDetails.setVisibility(View.VISIBLE);
        else
            llMiniNurseryDetails.setVisibility(View.GONE);

        if (obj.get(0).getAddressType().equalsIgnoreCase("District Based")) {
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
        tvContractDate.setText(obj.get(0).getContractDate());
        tvArea.setText(obj.get(0).getAcerage());
        tvStreet1.setText(obj.get(0).getStreet1());
        tvStreet2.setText(obj.get(0).getStreet2());
        tvState.setText(obj.get(0).getStateName());
        tvDistrict.setText(obj.get(0).getDistrictName());
        tvBlock.setText(obj.get(0).getBlockName());
        tvPanchayat.setText(obj.get(0).getPanchayatName());
        tvVillage.setText(obj.get(0).getVillageName());
        tvCity.setText(obj.get(0).getCity());
        tvPincode.setText(obj.get(0).getPinCode());
        tvSoilType.setText(obj.get(0).getSoilType());
        tvMSLElevation.setText(obj.get(0).getElevationMSL());
        tvPHChemical.setText(obj.get(0).getSoilPH());
        tvNitrogen.setText(obj.get(0).getNitrogen());
        tvPotash.setText(obj.get(0).getPotash());
        tvPhosphorous.setText(obj.get(0).getPhosphorus());
        tvOrganicCarbon.setText(obj.get(0).getOrganicCarbonPerc());
        tvMagnesium.setText(obj.get(0).getMagnesium());
        tvCalcium.setText(obj.get(0).getCalcium());
        tvExistingLandUse.setText(obj.get(0).getExistingUse());
        tvCommunityUse.setText(obj.get(0).getCommunityUse());
        tvExistingHazard.setText(obj.get(0).getExistingHazard());
        tvAnyLegalDispute.setText(obj.get(0).getLegalDispute());
        tvNearestRiver.setText(obj.get(0).getRiver());
        tvNearestDam.setText(obj.get(0).getDam());
        tvIrrigationSystem.setText(obj.get(0).getIrrigation());
        tvWaterSource.setText(obj.get(0).getSourceWater());
        tvElectricitySource.setText(obj.get(0).getElectricitySource());
        tvDripperSpacing.setText(obj.get(0).getDripperSpacing());
        tvDischargeRate.setText(obj.get(0).getDischargeRate());
        tvOverheadTrans.setText(obj.get(0).getOverheadTransmission());

        //To bind and view cropping pattern details
        BindData();

        /*---------Code to bind list of Land Characteristic
        Details---------------------------------*/
        dba.open();
        List<LandCharacteristic> lables = dba.getLandCharacteristicCheckedByNurseryUniqueId
                (nurseryUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmptyCharacterstic.setVisibility(View.GONE);
            tvDividerCharacterstic.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("Name", String.valueOf(lables.get(i).getTitle()));
                hm.put("CharacteristicId", String.valueOf(lables.get(i).getCharacteristicId()));
                CharacteristicDetails.add(hm);
            }
        } else {
            tvDividerCharacterstic.setVisibility(View.GONE);
            tvEmptyCharacterstic.setVisibility(View.VISIBLE);
        }

        CadapterCharacterstic = new CustomAdapterCharacteristic(NurseryView.this,
                CharacteristicDetails);
        if (lsize > 0)
            lvCharacterstic.setAdapter(CadapterCharacterstic);

        /*---------Code to bind list of Land Issue Details---------------------------------*/
        dba.open();
        List<LandCharacteristic> lable = dba.getLandIssueCheckedByNurseryUniqueId(nurseryUniqueId);
        lsize = lable.size();
        if (lsize > 0) {
            tvEmptyLandIssue.setVisibility(View.GONE);
            tvDividerIssues.setVisibility(View.VISIBLE);
            for (int i = 0; i < lable.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lable.get(i).getId());
                hm.put("Name", String.valueOf(lable.get(i).getTitle()));
                hm.put("IssueId", String.valueOf(lable.get(i).getCharacteristicId()));
                IssueDetails.add(hm);
            }
        } else {
            tvEmptyLandIssue.setVisibility(View.VISIBLE);
            tvDividerIssues.setVisibility(View.GONE);
        }
        dba.close();

        CadapterLandIssue = new CustomAdapterLandIssue(NurseryView.this, IssueDetails);
        if (lsize > 0)
            lvIssues.setAdapter(CadapterLandIssue);
        BindCoordinates();
        btnAddGps.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                dba.openR();
                boolean isExist = dba.isExistNurseryCoordinates(nurseryId, "0");
                if (isExist)
                    intent = new Intent(NurseryView.this, ActivityNurseryZoneCoordinateUpdate
                            .class);
                else
                    intent = new Intent(NurseryView.this, ActivityNurseryZoneCoordinate.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryZoneId", "0");
                intent.putExtra("nurseryName", nurName);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurseryZone", "");

                startActivity(intent);
                finish();
            }
        });


      /*--------Code to be executed on click event of Zone View link---------------------*/
        linkZone.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(NurseryView.this, NurseryZone.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurName);
                startActivity(intent);
                finish();
            }
        });

        int acc = BindAccount();
    }

    //Method to bind Account in list view
    public int BindAccount() {
        //<editor-fold desc="Code to bind Account Details">
        dba.open();
        ArrayList<HashMap<String, String>> list = dba.GetNurseryMiniAccount(nurseryId);
        dba.close();

        if (list.size() > 0)
            tvEmptyAccount.setVisibility(View.GONE);
        else
            tvEmptyAccount.setVisibility(View.VISIBLE);
        // Keys used in Hashmap
        String[] from = {"IFSC", "Account"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvLatitude, R.id.tvLongitude};
        //</editor-fold>
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), list, R.layout
                .list_farm_coordinates, from, to);
        lvAccount.setAdapter(adapter);
        //</editor-fold>
        return list.size();
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(NurseryView.this, Nursery.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
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

        Intent intent = new Intent(NurseryView.this, Nursery.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        startActivity(intent);
        finish();
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

    private void BindData() {
        nurseryCroppingPatternData.clear();
        dba.open();
        List<NurseryCroppingPatternData> lables = dba.getNurseryCroppingPatternByUniqueId
                (nurseryUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmptyCroppingPattern.setVisibility(View.GONE);
            tvDividerCroppingPattern.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("UniqueId", String.valueOf(lables.get(i).getUniqueId()));
                hm.put("CropId", String.valueOf(lables.get(i).getCropId()));
                hm.put("Crop", String.valueOf(lables.get(i).getCrop()));
                hm.put("CropVarietyId", String.valueOf(lables.get(i).getCropVarietyId()));
                hm.put("CropVariety", String.valueOf(lables.get(i).getCropVariety()));
                hm.put("SeasonId", String.valueOf(lables.get(i).getSeasonId()));
                hm.put("Season", String.valueOf(lables.get(i).getSeason()));
                hm.put("Acreage", String.valueOf(lables.get(i).getAcreage()));
                hm.put("FinancialYear", String.valueOf(lables.get(i).getFinancialYear()));
                nurseryCroppingPatternData.add(hm);
            }
        } else {
            tvEmptyCroppingPattern.setVisibility(View.VISIBLE);
            tvDividerCroppingPattern.setVisibility(View.GONE);
        }

        Cadapter = new CustomAdapter(NurseryView.this, nurseryCroppingPatternData);
        if (lsize > 0)
            lvInfoListCroppingPattern.setAdapter(Cadapter);
    }

    //Method to bind coordinates in list view
    public int BindCoordinates() {
        //<editor-fold desc="Code to bind Coordinates Details">
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<CustomCoord> lables = dba.GetNurseryCoordinates(nurseryId, nurseryZoneId);
        tvEmptyCoordinates.setVisibility(View.VISIBLE);
        tvDividerCoordinates.setVisibility(View.GONE);
        for (int i = 0; i < lables.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("Latitude", lables.get(i).getLatitude());
            hm.put("Longitude", lables.get(i).getLongitude());
            hm.put("Accuracy", String.valueOf(lables.get(i).getAccuracy()));
            coordList.add(hm);
            tvEmptyCoordinates.setVisibility(View.GONE);
            tvDividerCoordinates.setVisibility(View.VISIBLE);
        }
        dba.close();

        // Keys used in Hashmap
        String[] from = {"Latitude", "Longitude"};
        // Ids of views in listview_layout
        int[] to = {R.id.tvLatitude, R.id.tvLongitude};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), coordList, R.layout
                .list_farm_coordinates, from, to);
        lvCoordinates.setAdapter(adapter);
        //</editor-fold>
        return lables.size();
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvId, tvFirst, tvSecond;
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolderCharacterstic {
        TextView tvCharacteristic;
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolderLandIssue {
        TextView tvIssue;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvInfoList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            nurseryCroppingPatternData = lvInfoList;
        }

        @Override
        public int getCount() {
            return nurseryCroppingPatternData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return nurseryCroppingPatternData.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_cropping_pattern_details, null);
                holder = new ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvFirst = (TextView) arg1.findViewById(R.id.tvFirst);
                holder.tvSecond = (TextView) arg1.findViewById(R.id.tvSecond);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(nurseryCroppingPatternData.get(arg0).get("Id"));
            holder.tvFirst.setText(nurseryCroppingPatternData.get(arg0).get("Crop") + " - " +
                    nurseryCroppingPatternData.get(arg0).get("CropVariety"));
            holder.tvSecond.setText(nurseryCroppingPatternData.get(arg0).get("Season") + " , " +
                    nurseryCroppingPatternData.get(arg0).get("Acreage") + " Acre");

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }

    }

    public class CustomAdapterCharacteristic extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapterCharacteristic(Context context, ArrayList<HashMap<String, String>>
                lvCharacterstic) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            CharacteristicDetails = lvCharacterstic;
        }

        @Override
        public int getCount() {
            return CharacteristicDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return CharacteristicDetails.get(arg0);
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


            final ViewHolderCharacterstic holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_land_view_characteristic, null);
                holder = new ViewHolderCharacterstic();

                holder.tvCharacteristic = (TextView) arg1.findViewById(R.id.tvCharacteristic);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolderCharacterstic) arg1.getTag();
            }

            holder.tvCharacteristic.setText(CharacteristicDetails.get(arg0).get("Name"));

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }

    public class CustomAdapterLandIssue extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapterLandIssue(Context context, ArrayList<HashMap<String, String>>
                lvIssues) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            IssueDetails = lvIssues;
        }

        @Override
        public int getCount() {
            return IssueDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return IssueDetails.get(arg0);
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


            final ViewHolderLandIssue holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_land_view_issues, null);
                holder = new ViewHolderLandIssue();

                holder.tvIssue = (TextView) arg1.findViewById(R.id.tvIssue);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolderLandIssue) arg1.getTag();
            }

            holder.tvIssue.setText(IssueDetails.get(arg0).get("Name"));
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }

    }
}
