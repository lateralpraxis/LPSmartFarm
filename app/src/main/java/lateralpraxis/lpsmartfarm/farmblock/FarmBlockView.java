package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import lateralpraxis.type.FarmBlockCroppingPatternData;
import lateralpraxis.type.FarmBlockViewData;
import lateralpraxis.type.LandCharacteristic;

public class FarmBlockView extends Activity {
    final Context context = this;
    CustomAdapter Cadapter;
    CustomAdapterCharacteristic CadapterCharacterstic;
    CustomAdapterLandIssue CadapterLandIssue;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvOwnershipType, tvFpoCooperativeName, tvKhata, tvKhasra, tvContractDate, tvArea, tvStreet1, tvStreet2, tvState, tvDistrict, tvBlock, tvPanchayat, tvVillage, tvPincode, tvSoilType, tvMSLElevation, tvPHChemical, tvNitrogen, tvPotash, tvPhosphorous, tvOrganicCarbon, tvMagnesium, tvCalcium, tvExistingLandUse, tvCommunityUse, tvExistingHazard, tvAnyLegalDispute, tvNearestRiver, tvNearestDam, tvIrrigationSystem, tvWaterSource, tvElectricitySource, tvDripperSpacing, tvDischargeRate, tvOverheadTrans, tvOwner, tvOwnerMobile;
    private TextView linkUpdateFarmer, tvEmptyCroppingPattern, tvEmptyCharacterstic, tvEmptyLandIssue, tvEmptyCoordinates, linkPlantation;
    private View tvDividerCroppingPattern, tvDividerCharacteristic, tvDividerIssues, tvDividerCoordinates;
    private ListView lvInfoListCroppingPattern, lvCharacterstic, lvIssues, lvCoordinates;
    private LinearLayout llNameMobile;
    private SimpleDateFormat dateFormatter_display;
    /*-------------------------Code for Variable Declaration---------------------------------------*/
    private List<FarmBlockViewData> obj;
    private String farmBlockUniqueId, farmerUniqueId, farmerName, farmerMobile, farmerCode,userRole;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> farmBlockCroppingPatternData, CharacteristicDetails, IssueDetails;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block_view);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        farmBlockCroppingPatternData = new ArrayList<HashMap<String, String>>();
        CharacteristicDetails = new ArrayList<HashMap<String, String>>();
        IssueDetails = new ArrayList<HashMap<String, String>>();
        dateFormatter_display = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    /*-----------------Code to set Farm Block Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmerCode = extras.getString("farmerCode");
        }

        /*------------------------Start of code for finding Controls--------------------------*/
        llNameMobile = (LinearLayout) findViewById(R.id.llNameMobile);
        tvOwner = (TextView) findViewById(R.id.tvOwner);
        tvOwnerMobile = (TextView) findViewById(R.id.tvOwnerMobile);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvOwnershipType = (TextView) findViewById(R.id.tvOwnershipType);
        linkPlantation = (TextView) findViewById(R.id.linkPlantation);
        tvFpoCooperativeName = (TextView) findViewById(R.id.tvFpoCooperativeName);
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

        tvDividerCroppingPattern = (View) findViewById(R.id.tvDividerCroppingPattern);
        tvDividerCharacteristic = (View) findViewById(R.id.tvDividerCharacteristic);
        tvDividerIssues = (View) findViewById(R.id.tvDividerIssues);
        tvDividerCoordinates = (View) findViewById(R.id.tvDividerCoordinates);
        linkUpdateFarmer = (TextView) findViewById(R.id.linkUpdateFarmer);

        tvEmptyCroppingPattern = (TextView) findViewById(R.id.tvEmptyCroppingPattern);
        lvInfoListCroppingPattern = (ListView) findViewById(R.id.lvInfoListCroppingPattern);
        tvEmptyCharacterstic = (TextView) findViewById(R.id.tvEmptyCharacterstic);
        lvCharacterstic = (ListView) findViewById(R.id.lvCharacterstic);
        lvIssues = (ListView) findViewById(R.id.lvIssues);
        lvCoordinates = (ListView) findViewById(R.id.lvCoordinates);
        tvEmptyLandIssue = (TextView) findViewById(R.id.tvEmptyLandIssue);
        tvEmptyCoordinates = (TextView) findViewById(R.id.tvEmptyCoordinates);
     /*------------------------End of code for finding Controls--------------------------*/

     /*---------------Start of code to bind Farm Block details-------------------------*/
        dba.openR();
        obj = dba.getFarmBlockById(farmBlockUniqueId);
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        tvFarmBlock.setText(obj.get(0).getFarmBlockCode());
        tvOwnershipType.setText(obj.get(0).getOwnershipType());
        if ((String.valueOf(obj.get(0).getOwnershipType())).equalsIgnoreCase("Contract") || (String.valueOf(obj.get(0).getOwnershipType())).equalsIgnoreCase("Leased")) {
            llNameMobile.setVisibility(View.VISIBLE);
            tvOwner.setText(obj.get(0).getOwnerName());
            tvOwnerMobile.setText(obj.get(0).getOwnerMobile());
        } else {
            llNameMobile.setVisibility(View.GONE);
            tvOwner.setText("");
            tvOwnerMobile.setText("");
        }
        tvFpoCooperativeName.setText(obj.get(0).getFPO());
        tvKhata.setText(obj.get(0).getKhataNo());
        tvKhasra.setText(obj.get(0).getKhasraNo());
    /*    String dateString = "";
        if (String.valueOf(obj.get(0).getContractDate()).trim().equals(""))
            dateString = "";
        else {
            Date date = new Date();
            try {
                date = dateFormatter_display.parse(obj.get(0).getContractDate().toString().trim());
                dateString = dateFormatter_display.format(date);
            } catch (ParseException e1) {
            }
        }
        tvContractDate.setText(dateString);*/
        if (String.valueOf(obj.get(0).getContractDate()).trim().equals(""))
            tvContractDate.setText("");
        else
        tvContractDate.setText(common.convertToDisplayDateFormat(obj.get(0).getContractDate()));
        tvArea.setText(obj.get(0).getAcerage());
        tvStreet1.setText(obj.get(0).getStreet1());
        tvStreet2.setText(obj.get(0).getStreet2());
        tvState.setText(obj.get(0).getStateName());
        tvDistrict.setText(obj.get(0).getDistrictName());
        tvBlock.setText(obj.get(0).getBlockName());
        tvPanchayat.setText(obj.get(0).getPanchayatName());
        tvVillage.setText(obj.get(0).getVillageName());
        tvPincode.setText(obj.get(0).getPinCode());
        tvSoilType.setText(obj.get(0).getSoilType());
        tvMSLElevation.setText(obj.get(0).getElevationMSL());
        tvPHChemical.setText(obj.get(0).getPHChemical());
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
        dba.openR();
        Boolean isFarmerEditable = false;
        userRole = dba.getAllRoles();
        isFarmerEditable = dba.isFarmerEditable(farmerUniqueId);
        if (isFarmerEditable  || userRole.contains("Farmer"))
            linkUpdateFarmer.setVisibility(View.VISIBLE);
        else
            linkUpdateFarmer.setVisibility(View.GONE);

        /*---------Code to bind list of Land Characteristic Details---------------------------------*/
        dba.open();
        List<LandCharacteristic> lables = dba.getLandCharacteristicCheckedByFarmBlockUniqueId(farmBlockUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmptyCharacterstic.setVisibility(View.GONE);
            tvDividerCharacteristic.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("Name", String.valueOf(lables.get(i).getTitle()));
                hm.put("CharacteristicId", String.valueOf(lables.get(i).getCharacteristicId()));
                CharacteristicDetails.add(hm);
            }
        } else {
            tvEmptyCharacterstic.setVisibility(View.VISIBLE);
            tvDividerCharacteristic.setVisibility(View.GONE);
        }

        CadapterCharacterstic = new CustomAdapterCharacteristic(FarmBlockView.this, CharacteristicDetails);
        if (lsize > 0)
            lvCharacterstic.setAdapter(CadapterCharacterstic);

        /*---------Code to bind list of Land Issue Details---------------------------------*/
        dba.open();
        List<LandCharacteristic> lable = dba.getLandIssueCheckedByFarmBlockUniqueId(farmBlockUniqueId);
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

        CadapterLandIssue = new CustomAdapterLandIssue(FarmBlockView.this, IssueDetails);
        if (lsize > 0)
            lvIssues.setAdapter(CadapterLandIssue);

        BindCoordinates();


        /*--------Code to be executed on click event of Update Farm Block buttons---------------------*/
        linkUpdateFarmer.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FarmBlockView.this, FarmBlockUpdate.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", tvFarmBlock.getText().toString().trim());
                intent.putExtra("entryType", "update");
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
        /*--------Code to be executed on click event of Add Plantation link---------------------*/
        linkPlantation.setOnClickListener(new View.OnClickListener() {
            //On click of view button
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FarmBlockView.this, ActivityPlantationList.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", tvFarmBlock.getText().toString().trim());
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
    }


    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FarmBlockView.this, FarmBlockList.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockView.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockView.this, ActivityHome.class);
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

        Intent intent = new Intent(FarmBlockView.this, FarmBlockList.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmerCode", farmerCode);
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
        farmBlockCroppingPatternData.clear();
        dba.open();
        List<FarmBlockCroppingPatternData> lables = dba.getFarmBlockCroppingPatternByUniqueId(farmBlockUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmptyCroppingPattern.setVisibility(View.GONE);
            tvDividerCroppingPattern.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("FarmBlockUniqueId", String.valueOf(lables.get(i).getFarmBlockUniqueId()));
                hm.put("CropId", String.valueOf(lables.get(i).getCropId()));
                hm.put("Crop", String.valueOf(lables.get(i).getCrop()));
                hm.put("CropVarietyId", String.valueOf(lables.get(i).getCropVarietyId()));
                hm.put("CropVariety", String.valueOf(lables.get(i).getCropVariety()));
                hm.put("SeasonId", String.valueOf(lables.get(i).getSeasonId()));
                hm.put("Season", String.valueOf(lables.get(i).getSeason()));
                hm.put("Acreage", String.valueOf(lables.get(i).getAcreage()));
                farmBlockCroppingPatternData.add(hm);
            }
        } else {
            tvEmptyCroppingPattern.setVisibility(View.VISIBLE);
            tvDividerCroppingPattern.setVisibility(View.GONE);
        }

        Cadapter = new CustomAdapter(FarmBlockView.this, farmBlockCroppingPatternData);
        if (lsize > 0)
            lvInfoListCroppingPattern.setAdapter(Cadapter);
    }

    //Method to bind coordinates in list view
    public int BindCoordinates() {
        //<editor-fold desc="Code to bind POA/POI Details">
        List<HashMap<String, Object>> coordList = new ArrayList<HashMap<String, Object>>();
        dba.open();

        List<CustomCoord> lables = dba.GetFarmBlockCoordinates(farmBlockUniqueId);
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
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), coordList, R.layout.list_farm_coordinates, from, to);
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
            farmBlockCroppingPatternData = lvInfoList;
        }

        @Override
        public int getCount() {
            return farmBlockCroppingPatternData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return farmBlockCroppingPatternData.get(arg0);
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

            holder.tvId.setText(farmBlockCroppingPatternData.get(arg0).get("Id"));
            holder.tvFirst.setText(farmBlockCroppingPatternData.get(arg0).get("Crop") + " - " + farmBlockCroppingPatternData.get(arg0).get("CropVariety"));
            holder.tvSecond.setText(farmBlockCroppingPatternData.get(arg0).get("Season") + " , " + farmBlockCroppingPatternData.get(arg0).get("Acreage") + " Acre");

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }

    }

    public class CustomAdapterCharacteristic extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapterCharacteristic(Context context, ArrayList<HashMap<String, String>> lvCharacterstic) {
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

        public CustomAdapterLandIssue(Context context, ArrayList<HashMap<String, String>> lvIssues) {
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
