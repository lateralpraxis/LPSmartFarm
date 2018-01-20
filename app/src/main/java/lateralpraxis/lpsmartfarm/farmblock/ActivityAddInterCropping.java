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
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.InterCroppingData;

public class ActivityAddInterCropping extends Activity {

    private final Context mContext = this;
    /*------------------------End of code for class Declaration------------------------------*/
    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    CustomAdapter Cadapter;
    // GPSTracker class
    GPSTracker gps;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvPlantationCode, tvEmpty;
    private View tvDivider;
    private Spinner spCrop, spVariety, spSeason;
    private EditText etArea;
    private Button btnAdd, btnBack, btnNext;
    private ListView lvInfoList;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId, plantationUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType,farmerCode, plantationCode;
    private int lsize = 0;
    private double fbArea =0.0;
    private static String lang;
    private ArrayList<HashMap<String, String>> interCroppingDetail;
    /*------------------------End of code for Variable Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_intercropping);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        interCroppingDetail = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
    /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvDivider= (View) findViewById(R.id.tvDivider);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvPlantationCode = (TextView) findViewById(R.id.tvPlantationCode);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        spCrop = (Spinner) findViewById(R.id.spCrop);
        spVariety = (Spinner) findViewById(R.id.spVariety);
        spSeason = (Spinner) findViewById(R.id.spSeason);
        etArea = (EditText) findViewById(R.id.etArea);
        lvInfoList = (ListView) findViewById(R.id.lvInfoList);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);


     /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            plantationCode = extras.getString("plantationCode");
            entryType = extras.getString("entryType");
            farmerCode = extras.getString("farmerCode");
            /*--------Code to set Farmer Details and Plantation Code---------------------*/
            tvFarmer.setText(farmerName);
            tvMobile.setText(farmerMobile);
            tvFarmBlock.setText(farmBlockCode);
            tvPlantationCode.setText(plantationCode);
        }

        /*------------------------Start of code for binding data in Spinner-----------------------*/
        spCrop.setAdapter(DataAdapter("crop", ""));

        //Allowed only 2 decimal places in area
        etArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        /*Code to get Farm Block Area*/
        dba.openR();
        fbArea =dba.getFarmBlockArea(farmBlockUniqueId);
        /*Binding data on Spinner item selected*/
        spCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spVariety.setAdapter(DataAdapter("variety", String.valueOf(((CustomType) spCrop.getSelectedItem()).getId())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spSeason.setAdapter(DataAdapter("season", ""));

        //To bind and view crop variety details
        BindData(plantationUniqueId);

        /*---------------Start of code to set Click Event for Button Add, Back & Next-------------------------*/

        //Code on Add Button event for adding crop, variety, season and area-----------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (spSeason.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Season is mandatory.":"सीज़न अनिवार्य है।",5,1);
                else if (spCrop.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Crop is mandatory.":"फसल अनिवार्य है।",5,1);
                else if (spVariety.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Variety is mandatory.":"विविधता अनिवार्य है।",5,1);
                else if (String.valueOf(etArea.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Area is mandatory.":"क्षेत्र अनिवार्य है।",5,1);
                else if (!String.valueOf(etArea.getText()).trim().equals("") && Double.valueOf(etArea.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot be 0.":"क्षेत्र शून्य नहीं हो सकता।",5,1);
                else if(Double.valueOf(etArea.getText().toString())>fbArea)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot exceed Farm Block area.":"क्षेत्र फार्म ब्लॉक क्षेत्र से अधिक नहीं हो सकता।",5,1);
                else {
                    boolean isAlreadyExists = false;
                    dba.open();
                    isAlreadyExists = dba.isVarietySeasonInterCroppingAlreadyAdded(plantationUniqueId, String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()), String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()));
                    dba.close();
                    if (isAlreadyExists) {
                        common.showToast(lang.equalsIgnoreCase("en")?"Inter Cropping detail already added.":"इंटर क्रॉपिंग विस्तार पहले ही जोड़ा गया है।",5,3);
                    } else {
                        latitude = "NA";
                        longitude = "NA";
                        accuracy = "NA";
                        latitudeN = "NA";
                        longitudeN = "NA";
                        // create class object
                        gps = new GPSTracker(ActivityAddInterCropping.this);
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


                            String interCroppingUniqueId = UUID.randomUUID().toString();
                            dba.open();
                            dba.insertInterCropping(interCroppingUniqueId, farmerUniqueId, farmBlockUniqueId, plantationUniqueId, String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()), String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()), etArea.getText().toString(), userId, longitudeN, latitudeN, accuracy);
                            dba.close();
                            common.showToast(lang.equalsIgnoreCase("en")?"Inter Cropping detail added successfully.":"इंटर क्रॉपिंग विस्तार सफलतापूर्वक जोड़ा गया।");
                            spCrop.setSelection(0);
                            spVariety.setSelection(0);
                            spSeason.setSelection(0);
                            etArea.setText("");
                            BindData(plantationUniqueId);
                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gps.showSettingsAlert();
                        }
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move ActivityViewInterCropping page
                Intent intent = new Intent(ActivityAddInterCropping.this, ActivityViewInterCropping.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move ActivityPlantationList page
                Intent intent = new Intent(ActivityAddInterCropping.this, ActivityViewInterCropping.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/

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
                //To move ActivityViewInterCropping page
                Intent intent = new Intent(ActivityAddInterCropping.this, ActivityViewPlantation.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddInterCropping.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityAddInterCropping.this, ActivityHome.class);
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

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        //To move ActivityViewInterCropping page
        Intent intent = new Intent(ActivityAddInterCropping.this, ActivityViewInterCropping.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("plantationCode", plantationCode);
        intent.putExtra("farmerCode", farmerCode);
        startActivity(intent);
        finish();
    }
    // To create menu on inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
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

    private void BindData(String plantationUniqueId) {
        interCroppingDetail.clear();
        dba.open();
        List<InterCroppingData> lables = dba.getInterCroppingByUniqueId(plantationUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnNext.setText("Submit");
            btnNext.setVisibility(View.VISIBLE);
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
                hm.put("IsActive", String.valueOf(lables.get(i).getIsActive()));
                interCroppingDetail.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        }

        Cadapter = new CustomAdapter(ActivityAddInterCropping.this, interCroppingDetail);
        if (lsize > 0) {
            lvInfoList.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvInfoList.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.VISIBLE);
        }
        else
        {
            tvEmpty.setVisibility(View.VISIBLE);
            lvInfoList.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
        }
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvId, tvFirst, tvSecond;
        Button btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvInfoList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            interCroppingDetail = lvInfoList;
        }

        @Override
        public int getCount() {
            return interCroppingDetail.size();
        }

        @Override
        public Object getItem(int arg0) {
            return interCroppingDetail.get(arg0);
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
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(interCroppingDetail.get(arg0).get("Id"));
            holder.tvFirst.setText(interCroppingDetail.get(arg0).get("Season"));
            holder.tvSecond.setText(interCroppingDetail.get(arg0).get("Crop") + " - " + interCroppingDetail.get(arg0).get("CropVariety")+ " , " + interCroppingDetail.get(arg0).get("Acreage") + " Acre");
            if (interCroppingDetail.get(arg0).get("IsActive").equalsIgnoreCase("1"))
                holder.btnDelete.setVisibility(View.GONE);
            else
                holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Delete Inter Cropping Detail":"इंटर क्रॉपिंग विवरण हटाएं");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to delete this inter cropping detail?":"क्या आप वाकई इस इंटर क्रॉपिंग को हटाना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"OK":"ठीक",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.deleteInterCropping(String.valueOf(holder.tvId.getText()));
                                    dba.close();
                                    common.showToast(lang.equalsIgnoreCase("en")?"Inter Cropping deleted successfully.":"इंटर क्रॉपिंग सफलतापूर्वक हटा दी गई।");
                                    BindData(plantationUniqueId);
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
