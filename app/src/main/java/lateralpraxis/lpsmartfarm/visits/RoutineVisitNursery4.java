package lateralpraxis.lpsmartfarm.visits;

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
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.ObservationData;

public class RoutineVisitNursery4 extends Activity {
    private final Context mContext = this;
    CustomAdapter adapter;
    File fileFBook;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvType, tvNursery, tvNurseryZone, tvPlantation, tvEmpty;
    private ListView lpList;
    private View tvDivider;
    private Button btnBack, btnNext, btnAdd;
    /*------------------------End of code for controls Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    /*------------------------End of code for variable Declaration------------------------------*/
    private Common common;
    /*------------------------End of code for class Declaration------------------------------*/
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, type, nurseryId, nursery, zoneId, zone, EntryFor, FromPage, visitUniqueId, plantationUniqueId, plantationName, uploadedFilePath;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> list, attachmentDetails;
    private File[] fileList;
    private String[] filePathStrings, fileNameStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit_nursery4);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        list = new ArrayList<HashMap<String, String>>();

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls Declaration--------------------------*/
        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        lpList = (ListView) findViewById(R.id.lpList);
        tvDivider = (View) findViewById(R.id.tvDivider);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnAdd = (Button) findViewById(R.id.btnAdd);

          /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationName = extras.getString("plantationName");
            visitUniqueId = extras.getString("visitUniqueId");
            EntryFor = extras.getString("EntryFor");
            FromPage = extras.getString("FromPage");
            tvType.setText(type);
            tvNursery.setText(nursery);
            tvNurseryZone.setText(zone);
            tvPlantation.setText(plantationName);
        }

        /*---------------Start of code to set Click Event for Back and Next Button -------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move from RoutineVisitNursery4 to RoutineVisitNursery3 page
                Intent intent = new Intent(RoutineVisitNursery4.this, RoutineVisitNursery3.class);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("visitUniqueId", visitUniqueId);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle("Confirmation");
                builder1.setMessage("Are you sure, you want to save visit details?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dba.open();
                                dba.UpdateSaveFlagVisitReport(visitUniqueId);
                                dba.close();

                                //To move routine visit 4 to routine visit list page
                                Intent intent = new Intent(RoutineVisitNursery4.this, RoutineVisitNurseryList.class);
                                intent.putExtra("type", type);
                                intent.putExtra("nurseryId", nurseryId);
                                intent.putExtra("nursery", nursery);
                                intent.putExtra("zoneId", zoneId);
                                intent.putExtra("zone", zone);
                                intent.putExtra("plantationUniqueId", plantationUniqueId);
                                intent.putExtra("plantationName", plantationName);
                                intent.putExtra("visitUniqueId", visitUniqueId);
                                intent.putExtra("EntryFor", EntryFor);
                                intent.putExtra("FromPage", FromPage);
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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move from RoutineVisitNursery4 to RoutineVisit5 page
                Intent intent = new Intent(RoutineVisitNursery4.this, RoutineVisit5.class);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("visitUniqueId", visitUniqueId);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
            }
        });
        /*---------------End of code to set Click Event for Back and Next Button -------------------------*/

        /*---------Code to bind list of Observation Details---------------------------------*/
        BindData(visitUniqueId);
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RoutineVisitNursery4.this, RoutineVisitNursery3.class);
                intent.putExtra("type", type);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nursery", nursery);
                intent.putExtra("zoneId", zoneId);
                intent.putExtra("zone", zone);
                intent.putExtra("plantationUniqueId", plantationUniqueId);
                intent.putExtra("plantationName", plantationName);
                intent.putExtra("visitUniqueId", visitUniqueId);
                intent.putExtra("EntryFor", EntryFor);
                intent.putExtra("FromPage", FromPage);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RoutineVisitNursery4.this, ActivityHome.class);
                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeScreenIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*---------Code to bind list of Observation Details---------------------------------*/
    private void BindData(String visitUniqueId) {
        list.clear();
        dba.open();
        List<ObservationData> lables = dba.getObservationList(visitUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            lpList.setVisibility(View.VISIBLE);
tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("DefectId", lables.get(i).getDefectId());
                hm.put("Title", String.valueOf(lables.get(i).getTitle()));
                hm.put("Remark", String.valueOf(lables.get(i).getRemark()));
                hm.put("IsAttachment", lables.get(i).getIsAttachment());
                hm.put("IsSync", String.valueOf(lables.get(i).getIsSync()));
                list.add(hm);
            }
            adapter = new CustomAdapter(RoutineVisitNursery4.this, list);
            lpList.setAdapter(adapter);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
            lpList.setVisibility(View.GONE);
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
        Intent intent = new Intent(RoutineVisitNursery4.this, RoutineVisitNursery3.class);
        intent.putExtra("type", type);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nursery", nursery);
        intent.putExtra("zoneId", zoneId);
        intent.putExtra("zone", zone);
        intent.putExtra("plantationUniqueId", plantationUniqueId);
        intent.putExtra("plantationName", plantationName);
        intent.putExtra("visitUniqueId", visitUniqueId);
        intent.putExtra("EntryFor", EntryFor);
        intent.putExtra("FromPage", FromPage);
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

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvId, tvDefectId, tvTitle, tvRemark;
        Button btnAttachment, btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lpList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            list = lpList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_routine_visit4, null);
                holder = new ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvDefectId = (TextView) arg1.findViewById(R.id.tvDefectId);
                holder.tvTitle = (TextView) arg1.findViewById(R.id.tvTitle);
                holder.tvRemark = (TextView) arg1.findViewById(R.id.tvRemark);
                holder.btnAttachment = (Button) arg1.findViewById(R.id.btnAttachment);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(list.get(arg0).get("Id"));
            holder.tvDefectId.setText(list.get(arg0).get("DefectId"));
            holder.tvTitle.setText(list.get(arg0).get("Title"));
            holder.tvRemark.setText(list.get(arg0).get("Remark"));
            if (list.get(arg0).get("IsAttachment").equalsIgnoreCase("1")) {
                holder.btnAttachment.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Green
                holder.btnAttachment.setEnabled(true);
            }
            else {
                holder.btnAttachment.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Black
                holder.btnAttachment.setEnabled(false);
            }
            if (list.get(arg0).get("IsSync").equalsIgnoreCase("1"))
                holder.btnDelete.setVisibility(View.GONE);
            else
                holder.btnDelete.setVisibility(View.VISIBLE);
            /*---------------Start of code to set Click Event for Viewing Attachment-------------------------*/
            holder.btnAttachment.setOnClickListener(new View.OnClickListener() {
                public void onClick(View viewIn) {
                    try {
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!", 5, 0);
                        } else {

                            dba.openR();
                            attachmentDetails = dba.GetVisitReportPhoto(visitUniqueId, list.get(arg0).get("DefectId"));
                            if (attachmentDetails.size() > 0) {
                                for (HashMap<String, String> hashMap : attachmentDetails) {
                                    for (String key : hashMap.keySet()) {
                                        if (key.equals("FilePath"))
                                            uploadedFilePath = hashMap.get(key);
                                    }
                                }
                                File file = new File(uploadedFilePath);
                                fileFBook = new File(file.getParent());
                            }
                        }

                        if (fileFBook.isDirectory()) {
                            fileList = fileFBook.listFiles(new FilenameFilter() {
                                public boolean accept(File directory,
                                                      String fileName) {
                                    return fileName.endsWith(".jpeg")
                                            || fileName.endsWith(".bmp")
                                            || fileName.endsWith(".jpg")
                                            || fileName.endsWith(".png")
                                            || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for filePathStrings
                            filePathStrings = new String[fileList.length];
                            // Create a String array for FileNameStrings
                            fileNameStrings = new String[fileList.length];

                            for (int i = 0; i < fileList.length; i++) {

                                filePathStrings[i] = fileList[i].getAbsolutePath();
                                // Get the name image file
                                fileNameStrings[i] = fileList[i].getName();

                                Intent i1 = new Intent(RoutineVisitNursery4.this,
                                        ViewImage.class);
                                // Pass String arrays filePathStrings
                                i1.putExtra("filepath", filePathStrings);
                                // Pass String arrays FileNameStrings
                                i1.putExtra("filename", fileNameStrings);
                                // Pass click position
                                i1.putExtra("position", 0);
                                startActivity(i1);
                            /* } */
                            }
                        }

                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(RoutineVisitNursery4.this, "Error: " + except.getMessage(), false);
                    }
                }
            });
        /*---------------End of code to set Click Event for Viewing Attachment-------------------------*/
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Delete Defect Detail");
                    builder1.setMessage("Are you sure you want to delete this defect detail?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.DeleteDefectVisitReportDetail(String.valueOf(holder.tvId.getText()), visitUniqueId, String.valueOf(holder.tvDefectId.getText()));
                                    dba.close();
                                    common.showToast("Defect deleted successfully.", 5, 3);
                                    /*---------Code to bind list of Observation Details---------------------------------*/
                                    BindData(visitUniqueId);
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
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
