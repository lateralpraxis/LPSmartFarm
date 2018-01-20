package lateralpraxis.lpsmartfarm.nurseryreporting;

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

public class NurseryUnplanned extends Activity {
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
    private String type, nurseryId, nursery, zoneId, zone, EntryFor, nurseryUniqueId, plantationUniqueId, plantation, jobcardUniqueId,userId;
    private ArrayList<HashMap<String, String>> PlantationDetails;
    private int lsize = 0;
    //Variable for displaying File
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvType, tvNursery, tvNurseryZone, tvPlantation, tvEmpty, linkAdd;
    private View tvDivider;
    private ListView lvUnPlannedActivity;
    private Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_unplanned);
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
         /*------------------------Code for finding controls-----------------------*/
        /*------------------------Code for finding controls-----------------------*/
        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        lvUnPlannedActivity = (ListView) findViewById(R.id.lvUnPlannedActivity);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
            EntryFor = extras.getString("EntryFor");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantation = extras.getString("plantation");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            jobcardUniqueId = extras.getString("jobcardUniqueId");
            EntryFor = extras.getString("EntryFor");

            tvType.setText(type);
            tvNursery.setText(nursery);
            tvNurseryZone.setText(zone);
            tvPlantation.setText(plantation.replace("/","-"));
        }

        dba.openR();
        ArrayList<HashMap<String, String>> listDetailForViewAdditional = dba.getTempAdditionalActivity();
        lsize = listDetailForViewAdditional.size();
        if (lsize> 0) {
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
                Intent intent = new Intent(NurseryUnplanned.this,NurseryAdditional.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
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
                Intent intent = new Intent(NurseryUnplanned.this, NurseryReporting4.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
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
                gps = new GPSTracker(NurseryUnplanned.this);
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
                    alertDialogBuilder.setTitle("Confirmation");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure, you want to save reporting details?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String createByRole = "";
                                    final HashMap<String, String> user = session.getLoginUserDetails();
                                    dba.openR();
                                    String userRole="";
                                    userRole=dba.getAllRoles();
                                    if (userRole.contains("Nursery Supervisor"))
                                        createByRole = "Nursery Supervisor";
                                    else if (userRole.contains("Mini Nursery Service Provider"))
                                        createByRole = "Mini Nursery Service Provider";
                                    else
                                        createByRole ="Mini Nursery User";


                                    dba.open();
                                    String weekNo = dba.getWeekNoForPlantation(plantationUniqueId);
                                    dba.InsertUpdate_JobCard(jobcardUniqueId, "Nursery", nurseryUniqueId, zoneId, plantationUniqueId, weekNo, userId, longitudeN, latitudeN, accuracy, createByRole);
                                    dba.close();
                                    Intent intent1 = new Intent(NurseryUnplanned.this, NurseryReportList.class);
                                    startActivity(intent1);
                                    finish();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                Intent intent = new Intent(NurseryUnplanned.this, NurseryReporting4.class);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantation", plantation);
                intent.putExtra("jobcardUniqueId", jobcardUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(NurseryUnplanned.this);
                // set title
                alertDialogBuilderh.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilderh
                        .setMessage("Are you sure, you want to leave this module it will discard all data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(NurseryUnplanned.this, ActivityHome.class);
                                startActivity(intent1);
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
        Intent intent = new Intent(NurseryUnplanned.this, NurseryReporting4.class);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("type", type);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nursery", nursery);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("zoneId", zoneId);
        intent.putExtra("zone", zone);
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

                                    Intent i1 = new Intent(NurseryUnplanned.this, ViewImage.class);
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
                        common.showAlert(NurseryUnplanned.this, "Error: " + except.getMessage(), false);

                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Confirmation");
                    builder1.setMessage("Are you sure, you want to delete this unplanned activity detail?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.DeleteUnPlannedActivitiesFromTempTable(holder.tvUniqueId.getText().toString());
                                    dba.close();
                                    common.showToast("Unplanned activity details deleted successfully.", 5, 3);
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
                                    } else {
                                        lvUnPlannedActivity.setAdapter(null);
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
