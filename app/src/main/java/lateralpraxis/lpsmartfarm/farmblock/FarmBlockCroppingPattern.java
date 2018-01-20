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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.DecimalDigitsInputFilter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.FarmBlockCroppingPatternData;

public class FarmBlockCroppingPattern extends Activity {
    private final Context mContext = this;
    CustomAdapter Cadapter;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvEmpty;
    private LinearLayout llFarmBlock;
    private Spinner spCrop, spVariety, spSeason;
    private EditText etArea;
    private Button btnAdd, btnBack, btnNext;
    private View tvDivider;
    private ListView lvInfoList;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private int lsize = 0, dataSize = 0;
    private double fbArea = 0.0;
    private ArrayList<HashMap<String, String>> farmBlockCroppingPatternData;
    private static String lang;
    /*------------------------End of code for Variable Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    /*------------------------End of code for class Declaration------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_block_cropping_pattern);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        farmBlockCroppingPatternData = new ArrayList<HashMap<String, String>>();
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
        /*Code to get farm block area*/
        dba.openR();
        fbArea = dba.getFarmBlockArea(farmBlockUniqueId);
        /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        spCrop = (Spinner) findViewById(R.id.spCrop);
        spVariety = (Spinner) findViewById(R.id.spVariety);
        spSeason = (Spinner) findViewById(R.id.spSeason);
        etArea = (EditText) findViewById(R.id.etArea);
        lvInfoList = (ListView) findViewById(R.id.lvInfoList);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
         /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }
         /*------------------------Start of code for binding data in Spinner-----------------------*/
        spCrop.setAdapter(DataAdapter("crop", ""));

        //Allowed only 2 decimal places in area
        etArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

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
        dataSize = BindData();
        /*---------------Start of code to set Click Event for Button Add, Back & Next-------------------------*/

        //Code on Add Button event for adding crop, variety, season and area-----------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (spSeason.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Season is mandatory.":"सीज़न अनिवार्य है।", 5, 1);
                else if (spCrop.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Crop is mandatory.":"फसल अनिवार्य है।", 5, 1);
                else if (spVariety.getSelectedItemPosition() == 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Variety is mandatory.":"विविधता अनिवार्य है।", 5, 1);
                else if (String.valueOf(etArea.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Area is mandatory.":"क्षेत्र अनिवार्य है।", 5, 1);
                else if (!String.valueOf(etArea.getText()).trim().equals("") && Double.valueOf(etArea.getText().toString()) == 0.0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot be 0.":"क्षेत्र शून्य नहीं हो सकता।", 5, 1);
                else if (Double.valueOf(etArea.getText().toString()) > fbArea) {
                    common.showToast(lang.equalsIgnoreCase("en")?"Area cannot exceed Farm Block area!":"क्षेत्र फार्म ब्लॉक क्षेत्र से अधिक नहीं हो सकता!", 5, 1);
                } else {
                    boolean isAlreadyExists = false;
                    dba.open();
                    isAlreadyExists = dba.isVarietySeasonAlreadyAdded(farmBlockUniqueId, String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()), String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()));
                    dba.close();
                    if (isAlreadyExists) {
                        common.showToast(lang.equalsIgnoreCase("en")?"Cropping pattern detail already added.":"क्रॉपिंग पैटर्न विवरण पहले ही जोड़ चुके हैं।", 5, 1);
                    } else {
                        dba.open();
                        dba.insertFarmBlockCroppingPattern(farmBlockUniqueId, String.valueOf(((CustomType) spCrop.getSelectedItem()).getId()), String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()), String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()), etArea.getText().toString());
                        dba.close();
                        common.showToast(lang.equalsIgnoreCase("en")?"Cropping pattern detail added successfully.":"क्रॉपिंग पैटर्न विवरण सफलतापूर्वक जोड़ा गया।", 5, 3);
                        spCrop.setSelection(0);
                        spVariety.setSelection(0);
                        spSeason.setSelection(0);
                        etArea.setText("");
                        BindData();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//To move back farm block other page
                Intent intent = new Intent(FarmBlockCroppingPattern.this, FarmBlockOther.class);
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
                //To move next farm block Characteristic page
                if (dataSize > 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Cropping pattern saved successfully.":"फसल का आकार सफलतापूर्वक सहेजा गया।",5,3);
                Intent intent = new Intent(FarmBlockCroppingPattern.this, FarmBlockCharacterstic.class);
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
                Intent intent = new Intent(FarmBlockCroppingPattern.this, FarmBlockOther.class);
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockCroppingPattern.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockCroppingPattern.this, ActivityHome.class);
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
        Intent intent = new Intent(FarmBlockCroppingPattern.this, FarmBlockOther.class);
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

    private int BindData() {
        farmBlockCroppingPatternData.clear();
        dba.open();
        List<FarmBlockCroppingPatternData> lables = dba.getFarmBlockCroppingPatternByUniqueId(farmBlockUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            //btnNext.setVisibility(View.VISIBLE);
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
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
            //btnNext.setVisibility(View.GONE);
        }

        Cadapter = new CustomAdapter(FarmBlockCroppingPattern.this, farmBlockCroppingPatternData);
        if (lsize > 0) {
            lvInfoList.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvInfoList.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            lvInfoList.setVisibility(View.GONE);
        }
        return lsize;
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
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(farmBlockCroppingPatternData.get(arg0).get("Id"));
            holder.tvFirst.setText(farmBlockCroppingPatternData.get(arg0).get("Crop") + " - " + farmBlockCroppingPatternData.get(arg0).get("CropVariety"));
            holder.tvSecond.setText(farmBlockCroppingPatternData.get(arg0).get("Season") + " , " + farmBlockCroppingPatternData.get(arg0).get("Acreage") + " Acre");
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Delete Cropping Pattern Detail":"क्रॉपिंग पैटर्न विवरण हटाएं");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to delete this cropping pattern detail?":"क्या आप निश्चित हैं, आप इस फसल पैटर्न को हटाना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"OK":"ठीक",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.deleteFarmBlockCroppingPattern(String.valueOf(holder.tvId.getText()));
                                    dba.close();
                                    common.showToast(lang.equalsIgnoreCase("en")?"Cropping pattern deleted successfully.":"क्रॉपिंग पैटर्न सफलतापूर्वक हटा दिया गया।",5,3);
                                    BindData();
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
