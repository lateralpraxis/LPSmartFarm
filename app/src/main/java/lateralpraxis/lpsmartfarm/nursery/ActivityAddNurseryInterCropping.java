package lateralpraxis.lpsmartfarm.nursery;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import lateralpraxis.type.NurseryInterCroppingData;

public class ActivityAddNurseryInterCropping extends Activity {
    private final Context mContext = this;

    /*Variables used to capture GPS*/
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    double flatitude = 0.0, flongitude = 0.0;
    CustomAdapter customAdapter;

    /*Instance of GPSTracker class*/
    GPSTracker gps;

    /*Control declaration*/
    private TextView tvPlatationUniqueId, tvPlantationCode, tvNurseryUniqueId, tvNurseryId, tvNurseryType, tvNurseryName,
            tvZoneUniqueId, tvZoneId, tvZone, tvEmpty;
    private View tvDivider;
    private Spinner spCrop, spVariety, spSeason;
    private EditText etArea;
    private Button btnBack, btnNext,btnAdd;
    private ListView listCroppingPattern;
    private RelativeLayout rlCroppingPattern;

    /*Local variable declarations*/
    private String userId, plantationUniqueId, plantationCode, nurseryUniqueId, nurseryId, nurseryType, nurseryName,
            nurseryZoneUniqueId, nurseryZoneId, nurseryZone,plarea;
    private int lsize;
    private ArrayList<HashMap<String, String>> interCroppingDetail;

    /*Database related variable declarations*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nursery_intercropping);

        /*Create instances of Database variables*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        interCroppingDetail = new ArrayList<>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*Set Action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*Find Controls*/
        tvPlatationUniqueId = (TextView) findViewById(R.id.tvPlantationUniqueId);
        tvPlantationCode = (TextView) findViewById(R.id.tvPlantationCode);
        tvNurseryUniqueId = (TextView) findViewById(R.id.tvNurseryUniqueId);
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvPlatationUniqueId = (TextView) findViewById(R.id.tvPlantationUniqueId);
        tvZoneUniqueId = (TextView) findViewById(R.id.tvZoneUniqueId);
        tvZoneId = (TextView) findViewById(R.id.tvZoneId);
        tvZone = (TextView) findViewById(R.id.tvZone);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        spCrop = (Spinner) findViewById(R.id.spCrop);
        spVariety = (Spinner) findViewById(R.id.spVariety);
        spSeason = (Spinner) findViewById(R.id.spSeason);

        etArea = (EditText) findViewById(R.id.etArea);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        rlCroppingPattern = (RelativeLayout) findViewById(R.id.rlCroppingPattern);
        listCroppingPattern = (ListView) findViewById(R.id.listCroppingPattern);

        /*Code to get data from posted page*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationCode = extras.getString("plantationCode");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            nurseryZoneUniqueId = extras.getString("nurseryZoneUniqueId");
            nurseryZoneId = extras.getString("nurseryZoneId");
            nurseryZone = extras.getString("nurseryZone");
            plarea=extras.getString("plarea");

            /*Code to set Nursery and Zone details*/
            tvNurseryUniqueId.setText(nurseryUniqueId);
            tvNurseryId.setText(nurseryId);
            tvNurseryType.setText(nurseryType);
            tvNurseryName.setText(nurseryName);
            tvZoneUniqueId.setText(nurseryZoneUniqueId);
            tvZoneId.setText(nurseryZoneId);
            tvZone.setText(nurseryZone);
            tvPlatationUniqueId.setText(plantationUniqueId);
            tvPlantationCode.setText(plantationCode);
        }

        /*Code for binding data in Spinner*/
        spCrop.setAdapter(DataAdapter("crop", ""));

        /*Allow only 2 decimals places in Area*/
        etArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etArea.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);

        /*Binding data to Variety on selection Crop*/
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

        /*Bind and view Crop variety details*/
        BindData(plantationUniqueId);

        /*Code for OnClickListener for btnAdd*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (spCrop.getSelectedItemPosition() == 0)
                    common.showToast("Crop is mandatory!");
                else if (spVariety.getSelectedItemPosition() == 0)
                    common.showToast("Variety is mandatory!");
                else if (spSeason.getSelectedItemPosition() == 0)
                    common.showToast("Season is mandatory!");
                else if (String.valueOf(etArea.getText()).trim().equals("")) {
                    common.showToast("Area is mandatory!");
                } else if (!String.valueOf(etArea.getText()).trim().equals("") && Double.valueOf(etArea.getText().toString()) == 0.0) {
                    common.showToast("Area cannot be 0!");
                }
                else if(Double.valueOf(plarea)< Double.valueOf(etArea.getText().toString()))
                    common.showToast("Area cannot exceed Plantation Area");
                else {
                    dba.open();
                    boolean isAlreadyExists = dba.isVarietySeasonNurseryInterCroppingAlreadyAdded(
                            plantationUniqueId,
                            String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()),
                            String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()));
                    dba.close();
                    if (isAlreadyExists) {
                        common.showToast("Inter Cropping detail already added!", Toast.LENGTH_LONG, 0);
                    } else {
                        latitude = "NA";
                        longitude = "NA";
                        accuracy = "NA";
                        latitudeN = "NA";
                        longitudeN = "NA";

                        /*Create GPSTracker object*/
                        gps = new GPSTracker(ActivityAddNurseryInterCropping.this);
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
                            dba.insertNurseryInterCropping(
                                    interCroppingUniqueId,
                                    nurseryId,
                                    plantationUniqueId,
                                    String.valueOf(((CustomType) spVariety.getSelectedItem()).getId()),
                                    String.valueOf(((CustomType) spSeason.getSelectedItem()).getId()),
                                    etArea.getText().toString(),
                                    userId, longitudeN, latitudeN, accuracy);
                            dba.close();
                            common.showToast("Nursery Inter Cropping detail added successfully.", Toast.LENGTH_LONG, 3);
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

        /*Back button pressed*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*To move ActivityViewNurseryInterCropping page*/
                Intent intent = new Intent(ActivityAddNurseryInterCropping.this, ActivityViewNurseryInterCropping.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurseryZone", nurseryZone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("plarea", plarea);
                startActivity(intent);
                finish();
            }
        });

        /*Next button pressed*/
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move ActivityPlantationList page
                Intent intent = new Intent(ActivityAddNurseryInterCropping.this, ActivityViewNurseryInterCropping.class);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurseryZone", nurseryZone);
                intent.putExtra("plarea", plarea);
                startActivity(intent);
                finish();
            }
        });
    }



    //<editor-fold desc="Method to view intent on Back Press Click">
    @Override
    public void onBackPressed() {
        //Go to ActivityViewPlantation page
        Intent intent = new Intent(ActivityAddNurseryInterCropping.this, ActivityViewNurseryInterCropping.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurseryType", nurseryType);
        intent.putExtra("nurseryName", nurseryName);
        intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
        intent.putExtra("nurseryZoneId", nurseryZoneId);
        intent.putExtra("nurseryZone", nurseryZone);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantationCode", plantationCode);
        intent.putExtra("plarea", plarea);
        startActivity(intent);
        finish();
    }
    //</editor-fold>

    //<editor-fold desc="Method to view Intent on Action bar">
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //To move ActivityViewInterCropping page
                Intent intent = new Intent(ActivityAddNurseryInterCropping.this, ActivityViewNurseryInterCropping.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZoneUniqueId", nurseryZoneUniqueId);
                intent.putExtra("nurseryZoneId", nurseryZoneId);
                intent.putExtra("nurseryZone", nurseryZone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationCode", plantationCode);
                intent.putExtra("plarea", plarea);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityAddNurseryInterCropping.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityAddNurseryInterCropping.this, ActivityHome.class);
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
    //</editor-fold>

    //<editor-fold desc="Method to fetch data and bind Spinner">
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }
    //</editor-fold>

    //<editor-fold desc="Bind data for controls">
    private void BindData(String plantationUniqueId) {
        interCroppingDetail.clear();
        dba.open();
        List<NurseryInterCroppingData> data = dba.getNurseryInterCroppingByPlantationUniqueId(plantationUniqueId);
        dba.close();
        lsize = data.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            btnNext.setText("Submit");
            btnNext.setVisibility(View.VISIBLE);
            for (int i = 0; i < data.size(); i++) {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("Id", data.get(i).getId());
                hm.put("PlantationUniqueId", data.get(i).getPlantationUniqueId());
                hm.put("CropId", data.get(i).getCropId());
                hm.put("Crop", data.get(i).getCrop());
                hm.put("CropVarietyId", data.get(i).getCropVarietyId());
                hm.put("CropVariety", data.get(i).getCropVariety());
                hm.put("SeasonId", data.get(i).getSeasonId());
                hm.put("Season", data.get(i).getSeason());
                hm.put("Acreage", data.get(i).getAcreage());
                hm.put("IsSync", data.get(i).getIsSync());
                interCroppingDetail.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
            tvDivider.setVisibility(View.GONE);
        }

        customAdapter = new CustomAdapter(
                ActivityAddNurseryInterCropping.this, interCroppingDetail);
        if (lsize > 0) {
            listCroppingPattern.setAdapter(customAdapter);
            tvEmpty.setVisibility(View.GONE);
            listCroppingPattern.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            listCroppingPattern.setVisibility(View.GONE);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewHolder Class">
    public static class ViewHolder {
        TextView tvId, tvFirst, tvSecond;
        Button btnDelete;
    }
    //</editor-fold>

    //<editor-fold desc="CustomerAdapter Class">
    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            interCroppingDetail = list;
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
            holder.tvSecond.setText(interCroppingDetail.get(arg0).get("Crop") + " - " + interCroppingDetail.get(arg0).get("CropVariety") + " , " + interCroppingDetail.get(arg0).get("Acreage") + " Sq.Ft.");
            holder.btnDelete.setVisibility((interCroppingDetail.get(arg0).get("IsSync").equalsIgnoreCase("1"))
                    ? View.GONE
                    : View.VISIBLE);

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Delete Inter Cropping Detail");
                    builder1.setMessage("Are you sure, you want to delete this inter cropping detail?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.deleteNurseryInterCropping(String.valueOf(holder.tvId.getText()));
                                    dba.close();
                                    common.showToast("Inter Cropping deleted successfully.", 1);
                                    BindData(plantationUniqueId);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertNew = builder1.create();
                    alertNew.show();
                }
            });
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
    //</editor-fold>
}
