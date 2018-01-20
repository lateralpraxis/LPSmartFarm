package lateralpraxis.lpsmartfarm.farmreporting;

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
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;

public class UnplannedView extends Activity {
    final Context context = this;
    private final Context mContext = this;
    //-------Varaibles used in Capture GPS---------//
    protected boolean isGPSEnabled = false;
    protected boolean canGetLocation = false;
    protected String latitude = "NA", longitude = "NA", accuracy = "NA";
    protected String latitudeN = "NA", longitudeN = "NA";
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    File destination, file;
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId, farmerName, farmerMobile, farmblockCode, farmblockUniqueId, plantationUniqueId, plantation, jobcardUniqueId, userId,EntryFor;
    private ArrayList<HashMap<String, String>> PlantationDetails;
    private int lsize = 0;
    private static String lang;
    //Variable for displaying File
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvFarmer, tvPlantation, tvFarmBlock, tvEmpty, linkAdd;
    private View tvDivider;
    private ListView lvUnPlannedActivity;
    private Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unplanned_view);
         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        //Setting UserId
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
         /*------------------------Code for finding controls-----------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        lvUnPlannedActivity = (ListView) findViewById(R.id.lvUnPlannedActivity);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmblockCode = extras.getString("farmblockCode");
            farmblockUniqueId = extras.getString("farmblockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantation = extras.getString("plantation");
            jobcardUniqueId = extras.getString("jobcardUniqueId");
            EntryFor = extras.getString("EntryFor");
            tvFarmer.setText(farmerName);
            tvPlantation.setText(plantation);
            tvFarmBlock.setText(farmblockCode);
        }

        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewAdditional = dba.getTempAdditionalActivity();
        lsize = listDetailForViewAdditional.size();
        if (listDetailForViewAdditional.size() != 0) {
            lvUnPlannedActivity.setAdapter(new AdditionalListAdapter(context, listDetailForViewAdditional));

            ViewGroup.LayoutParams params = lvUnPlannedActivity.getLayoutParams();
            params.height = 500;
            lvUnPlannedActivity.setLayoutParams(params);
            lvUnPlannedActivity.requestLayout();
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvUnPlannedActivity.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UnplannedView.this, AddUnplanned.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();
            }
        });

        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UnplannedView.this, ReportingStep4.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();

            }
        });

        //To validate and move to plantation population screen
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                latitude = "NA";
                longitude = "NA";
                accuracy = "NA";
                latitudeN = "NA";
                longitudeN = "NA";
                // create class object
                gps = new GPSTracker(UnplannedView.this);
                if (gps.canGetLocation()) {
                    flatitude = gps.getLatitude();
                    flongitude = gps.getLongitude();
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set title
                    alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to save reporting details?":"क्या आप निश्चित हैं, आप रिपोर्टिंग विवरण सहेजना चाहते हैं?")
                            .setCancelable(false)
                            .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String createByRole="";
                                    final HashMap<String, String> user = session.getLoginUserDetails();
                                    if(user.get(UserSessionManager.KEY_USERROLES).contains("Farmer"))
                                        createByRole="Farmer";
                                    else
                                        createByRole="Service Provider";

                                    dba.open();
                                    String weekNo = dba.getWeekNoForPlantation(plantationUniqueId);
                                    dba.InsertUpdate_JobCard(jobcardUniqueId, "FarmBlock", farmblockUniqueId, "0", plantationUniqueId, weekNo, userId, longitudeN, latitudeN, accuracy,createByRole);
                                    dba.close();
                                    Intent intent1 = new Intent(UnplannedView.this, FarmReportList.class);
                                    startActivity(intent1);
                                    finish();
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

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(UnplannedView.this, ReportingStep4.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmblockUniqueId", farmblockUniqueId);
                intent.putExtra("farmblockCode", farmblockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(UnplannedView.this);
                // set title
                alertDialogBuilderh.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilderh
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard all data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह सभी डेटा को छोड़ देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(UnplannedView.this, ActivityHome.class);
                                startActivity(intent1);
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
                AlertDialog alertDialogh = alertDialogBuilderh.create();
                // show it
                alertDialogh.show();
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
        Intent intent = new Intent(UnplannedView.this, ReportingStep4.class);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmblockUniqueId", farmblockUniqueId);
        intent.putExtra("farmblockCode", farmblockCode);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantation", plantation);
        intent.putExtra("jobcardUniqueId", jobcardUniqueId);
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


    public static class viewHolder {
        TextView tvUniqueId, tvFilePath, tvName, tvQuantity;
        Button btnViewAttach, btnDelete;
        int ref;
    }

    private class AdditionalListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listAdditionalActivity;
        String _type;
        private Context context2;

        public AdditionalListAdapter(Context context,
                                     ArrayList<HashMap<String, String>> listAdditionalActivity) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listAdditionalActivity = listAdditionalActivity;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listAdditionalActivity.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final viewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_additional_activity, null);
                holder = new viewHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvUniqueId = (TextView) convertView
                    .findViewById(R.id.tvUniqueId);
            holder.tvFilePath = (TextView) convertView
                    .findViewById(R.id.tvFilePath);
            holder.tvName = (TextView) convertView
                    .findViewById(R.id.tvName);
            holder.tvQuantity = (TextView) convertView
                    .findViewById(R.id.tvQuantity);
            holder.btnViewAttach = (Button) convertView
                    .findViewById(R.id.btnViewAttach);
            holder.btnDelete = (Button) convertView
                    .findViewById(R.id.btnDelete);

            final HashMap<String, String> itemAdditionalActivity = _listAdditionalActivity.get(position);

            holder.tvUniqueId.setText(itemAdditionalActivity.get("UniqueId"));
            holder.tvFilePath.setText(itemAdditionalActivity.get("FileName"));

            if (TextUtils.isEmpty(itemAdditionalActivity.get("FileName"))) {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Green
                holder.btnViewAttach.setEnabled(false);
            } else {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Black
                holder.btnViewAttach.setEnabled(true);
            }

            if (!TextUtils.isEmpty(itemAdditionalActivity.get("SubActivityName")))
                holder.tvName.setText(itemAdditionalActivity.get("ActivityName") + " - " + itemAdditionalActivity.get("SubActivityName"));
            else
                holder.tvName.setText(itemAdditionalActivity.get("ActivityName"));
            holder.tvQuantity.setText(itemAdditionalActivity.get("Quantity") + " " + itemAdditionalActivity.get("UOM"));
            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    try {

                        String actPath = itemAdditionalActivity.get("FileName");
                        int pathLen = actPath.split("/").length;
                        String newPath = actPath.split("/")[pathLen - 4];

                        // common.showToast("New Actual Path="+newPath);
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!");
                        } else {
                            // Locate the image folder in your SD Card
                            File file1 = new File(actPath);
                            file = new File(file1.getParent());
                        }

                        if (file.isDirectory()) {

                            listFile = file.listFiles(new FilenameFilter() {
                                public boolean accept(File directory, String fileName) {
                                    return fileName.endsWith(".jpeg") || fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for FilePathStrings
                            FilePathStrings = new String[listFile.length];
                            // Create a String array for FileNameStrings
                            FileNameStrings = new String[listFile.length];

                            for (int i = 0; i < listFile.length; i++) {

                                // Get the path of the image file
                                if (!listFile[i].getName().toString().toLowerCase().equals(".nomedia")) {
                                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                                    // Get the name image file
                                    FileNameStrings[i] = listFile[i].getName();

                                    Intent i1 = new Intent(UnplannedView.this, ViewImage.class);
                                    // Pass String arrays FilePathStrings
                                    i1.putExtra("filepath", FilePathStrings);
                                    // Pass String arrays FileNameStrings
                                    i1.putExtra("filename", FileNameStrings);
                                    // Pass click position
                                    i1.putExtra("position", 0);
                                    startActivity(i1);
                                }
                            }
                        }


                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(UnplannedView.this, "Error: " + except.getMessage(), false);

                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to delete this unplanned activity detail?":"क्या आप वाकई इस अनियोजित गतिविधि के विवरण को हटाना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"OK":"ठीक",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.DeleteUnPlannedActivitiesFromTempTable(holder.tvUniqueId.getText().toString());
                                    dba.close();
                                    common.showToast(lang.equalsIgnoreCase("en")?"Unplanned activity details deleted successfully.":"अनियोजित गतिविधि विवरण सफलतापूर्वक हटाए गए।", 5, 3);
                                    dba.openR();
                                    ArrayList<HashMap<String, String>> listDetailForViewAdditional = dba.getTempAdditionalActivity();
                                    lsize = listDetailForViewAdditional.size();
                                    if (listDetailForViewAdditional.size() != 0) {
                                        lvUnPlannedActivity.setAdapter(new AdditionalListAdapter(context, listDetailForViewAdditional));

                                        ViewGroup.LayoutParams params = lvUnPlannedActivity.getLayoutParams();
                                        params.height = 500;
                                        lvUnPlannedActivity.setLayoutParams(params);
                                        lvUnPlannedActivity.requestLayout();
                                        tvEmpty.setVisibility(View.GONE);
                                        tvDivider.setVisibility(View.VISIBLE);
                                    } else {
                                        lvUnPlannedActivity.setAdapter(null);
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvDivider.setVisibility(View.GONE);
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
            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
}
