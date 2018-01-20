package lateralpraxis.lpsmartfarm.recommendation;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.GPSTracker;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.visits.RoutineVisit2;
import lateralpraxis.lpsmartfarm.visits.RoutineVisitNursery2;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;

public class Recommendation2 extends Activity {
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
    File file;
    double flatitude = 0.0, flongitude = 0.0;
    // GPSTracker class
    GPSTracker gps;
    /*------------------Code for Variable Declaration---------------*/
    private String uniqueId, farmerUniqueId, type, nurseryId, nursery, zoneId, zone, farmerName, farmerMobile, farmBlockCode, farmBlockUniqueId, plantationUniqueId, plantationName, userId, EntryFor, FromPage;
    private ArrayList<HashMap<String, String>> PlantationDetails;
    private int lsize = 0, cnt = 0;
    //Variable for displaying File
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvFarmer, tvPlantation, tvFarmBlock, tvType, tvNursery, tvNurseryZone, tvEmpty, linkAdd;
    private ListView lvList;
	private View tvDivider;
    private LinearLayout llFarmBlock, llNursery;
    private Button btnBack, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation2);
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
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        llNursery = (LinearLayout) findViewById(R.id.llNursery);

        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        lvList = (ListView) findViewById(R.id.lvList);
        tvDivider = (View) findViewById(R.id.tvDivider);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            uniqueId = extras.getString("uniqueId");
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationName = extras.getString("plantationName");
            EntryFor = extras.getString("EntryFor");
            FromPage = extras.getString("FromPage");
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
            llFarmBlock.setVisibility(View.GONE);
            llNursery.setVisibility(View.GONE);
            if (FromPage.equalsIgnoreCase("FarmBlock")) {
                llFarmBlock.setVisibility(View.VISIBLE);
                tvFarmer.setText(farmerName);
                tvFarmBlock.setText(farmBlockCode);
            } else {
                llNursery.setVisibility(View.VISIBLE);
                tvType.setText(type);
                tvNursery.setText(nursery);
                tvNurseryZone.setText(zone);
            }
            tvPlantation.setText(plantationName);
        }

        dataBind(uniqueId);

        //To add recommendation details
        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Recommendation2.this, Recommendation3.class);
                if (uniqueId == null)
                    uniqueId = UUID.randomUUID().toString();
                intent.putExtra("uniqueId", uniqueId);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                startActivity(intent);
                finish();
            }
        });

        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });

        //To validate and move to plantationName population screen
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle("Confirmation");
                builder1.setMessage("Are you sure, you want to save details?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dba.open();
                                dba.UpdateSaveFlagRecommendation(uniqueId);
                                dba.close();
                                Intent intent;
                                if (FromPage.equalsIgnoreCase("FarmBlock"))
                                    //To move from Recommendation2 to RecommendationList page
                                    intent = new Intent(Recommendation2.this, RecommendationList.class);
                                else
                                    //To move from Recommendation2 to RecommendationNurseryList page
                                    intent = new Intent(Recommendation2.this, RecommendationNurseryList.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // if this button is clicked, just close
                                dialog.cancel();
                            }
                        });
                AlertDialog alertnew = builder1.create();
                alertnew.show();
            }
        });
    }

    private void dataBind(String uniqueId) {
        dba.openR();
        ArrayList<HashMap<String, String>> list = dba.GetRecommendationDetailByUniqueId(uniqueId);
        lsize = list.size();
        if (list.size() != 0) {
            lvList.setAdapter(new CustomAdapter(context, list));

            ViewGroup.LayoutParams params = lvList.getLayoutParams();
            params.height = 500;
            lvList.setLayoutParams(params);
            lvList.requestLayout();
            tvEmpty.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvList.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);	
            btnNext.setVisibility(View.GONE);
        }
        dba.close();
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_go_to_home:
                AlertDialog.Builder alertDialogBuilderh = new AlertDialog.Builder(Recommendation2.this);
                // set title
                alertDialogBuilderh.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilderh
                        .setMessage("Are you sure, you want to leave this module it will discard all data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent1 = new Intent(Recommendation2.this, ActivityHome.class);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(
                mContext);
        builder1.setTitle("Confirmation");
        builder1.setMessage("Are you sure, you want to leave this module it will discard any unsaved data?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dba.open();
                        dba.DeleteTempRecommendation();
                        dba.close();

                        Intent intent;
                        if (FromPage.equalsIgnoreCase("FarmBlock"))
                            //Move from Recommendation2 to RoutineVisit2 page
                            intent = new Intent(Recommendation2.this, RoutineVisit2.class);
                        else
                            //Move from Recommendation2 to RoutineVisitNursery2 page
                            intent = new Intent(Recommendation2.this, RoutineVisitNursery2.class);
                        //intent.putExtra("uniqueId", uniqueId);
                        intent.putExtra("farmerUniqueId", farmerUniqueId);
                        intent.putExtra("farmerName", farmerName);
                        intent.putExtra("farmerMobile", farmerMobile);
                        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                        intent.putExtra("farmBlockCode", farmBlockCode);
                        intent.putExtra("EntryFor", EntryFor);
                        intent.putExtra("FromPage", FromPage);
                        intent.putExtra("type", type);
                        intent.putExtra("nurseryId", nurseryId);
                        intent.putExtra("nursery", nursery);
                        intent.putExtra("zoneId", zoneId);
                        intent.putExtra("zone", zone);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        // if this button is clicked, just close
                        dialog.cancel();
                    }
                });
        AlertDialog alertnew = builder1.create();
        alertnew.show();
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
        TextView tvUniqueId, tvFilePath, tvName, tvQuantity, tvWeek, tvRemarks;
        Button btnViewAttach, btnDelete;
        int ref;
    }

    private class CustomAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> list;
        String _type;
        private Context context2;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listCA) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            list = listCA;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
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
            cnt = cnt + 1;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recommendation2_list, null);
                holder = new viewHolder();
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvUniqueId = (TextView) convertView.findViewById(R.id.tvUniqueId);
            holder.tvFilePath = (TextView) convertView.findViewById(R.id.tvFilePath);
            holder.tvWeek = (TextView) convertView.findViewById(R.id.tvWeek);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvQuantity = (TextView) convertView.findViewById(R.id.tvQuantity);
            holder.tvRemarks = (TextView) convertView.findViewById(R.id.tvRemarks);
            holder.btnViewAttach = (Button) convertView.findViewById(R.id.btnViewAttach);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

            final HashMap<String, String> item = list.get(position);

            holder.tvUniqueId.setText(item.get("UniqueId"));
            holder.tvFilePath.setText(item.get("FileName"));

            if (position == 0) {
                cnt = 1;
                holder.tvWeek.setVisibility(View.VISIBLE);
                if (item.get("Week").equalsIgnoreCase("1"))
                    holder.tvWeek.setText("For Next Week");
                else if (item.get("Week").equalsIgnoreCase("2"))
                    holder.tvWeek.setText("For Next Week + 1");
                else if (item.get("Week").equalsIgnoreCase("3"))
                    holder.tvWeek.setText("For Next Week + 2");
                else if (item.get("Week").equalsIgnoreCase("4"))
                    holder.tvWeek.setText("For Next Week + 3");
            } else {
                if (list.get(position - 1).get("Week").equals(list.get(position).get("Week"))) {
                    holder.tvWeek.setVisibility(View.GONE);
                } else {
                    cnt = 1;
                    holder.tvWeek.setVisibility(View.VISIBLE);
                    if (item.get("Week").equalsIgnoreCase("1"))
                        holder.tvWeek.setText("For Next Week");
                    else if (item.get("Week").equalsIgnoreCase("2"))
                        holder.tvWeek.setText("For Next Week + 1");
                    else if (item.get("Week").equalsIgnoreCase("3"))
                        holder.tvWeek.setText("For Next Week + 2");
                    else if (item.get("Week").equalsIgnoreCase("4"))
                        holder.tvWeek.setText("For Next Week + 3");
                }
            }

            if (TextUtils.isEmpty(item.get("FileName"))) {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Green
                holder.btnViewAttach.setEnabled(false);
            } else {
                holder.btnViewAttach.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Black
                holder.btnViewAttach.setEnabled(true);
            }

            if (!TextUtils.isEmpty(item.get("SubActivityName")))
                holder.tvName.setText(item.get("ActivityName") + " - " + item.get("SubActivityName"));
            else
                holder.tvName.setText(item.get("ActivityName"));
            holder.tvQuantity.setText(item.get("Quantity") + " " + item.get("UOM"));
            holder.tvRemarks.setText(item.get("Remark"));
            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    try {

                        String actPath = item.get("FileName");
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

                                    Intent i1 = new Intent(Recommendation2.this, ViewImage.class);
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
                        common.showAlert(Recommendation2.this, "Error: " + except.getMessage(), false);

                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View vw) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Confirmation");
                    builder1.setMessage("Are you sure, you want to delete this recommendation detail?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.DeleteRecommendationByUniqueId(holder.tvUniqueId.getText().toString());
                                    dba.close();
                                    common.showToast("Recommendation details deleted successfully.", 5, 3);
                                    dataBind(uniqueId);
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
