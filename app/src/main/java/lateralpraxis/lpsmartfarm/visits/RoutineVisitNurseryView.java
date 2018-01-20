package lateralpraxis.lpsmartfarm.visits;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.ObservationData;
import lateralpraxis.type.VisitNurseryViewData;

public class RoutineVisitNurseryView extends Activity {
    final Context context = this;
    File fileFBook;
    CustomAdapter adapter;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvType, tvNursery, tvNurseryZone, tvPlantation, tvAvgHeightPlant, tvPlantStatus, tvEmpty;
    private ListView lvList;
    private View tvDivider;
    /*-------------------------Code for Variable Declaration---------------------------------------*/
    private List<VisitNurseryViewData> obj;
    private String visitUniqueId, uploadedFilePath;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> list, attachmentDetails;
    private File[] fileList;
    private String[] filePathStrings, fileNameStrings;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit_nursery_view);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        list = new ArrayList<HashMap<String, String>>();

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    /*-----------------Code to set Farm Block Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            visitUniqueId = extras.getString("visitUniqueId");
        }

        /*------------------------Start of code for finding Controls--------------------------*/
        tvType = (TextView) findViewById(R.id.tvType);
        tvNursery = (TextView) findViewById(R.id.tvNursery);
        tvNurseryZone = (TextView) findViewById(R.id.tvNurseryZone);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        tvAvgHeightPlant = (TextView) findViewById(R.id.tvAvgHeightPlant);
        tvPlantStatus = (TextView) findViewById(R.id.tvPlantStatus);
        lvList = (ListView) findViewById(R.id.lvList);
        tvDivider = (View) findViewById(R.id.tvDivider);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
     /*------------------------End of code for finding Controls--------------------------*/

     /*---------------Start of code to bind Farm Block details-------------------------*/
        if (visitUniqueId != null) {
            dba.openR();
            obj = dba.GetVisitNurseryViewDetail(visitUniqueId);
            tvType.setText(obj.get(0).getNurseryType());
            tvNursery.setText(obj.get(0).getNursery());
            tvNurseryZone.setText(obj.get(0).getZone());
            tvPlantation.setText(obj.get(0).getPlantation());
            tvAvgHeightPlant.setText(String.valueOf(Double.valueOf(obj.get(0).getPlantHeight())));
            tvPlantStatus.setText(obj.get(0).getPlantStatus());

        /*---------Code to bind list of Observation Details---------------------------------*/
            BindData(visitUniqueId);
        }
    }


    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RoutineVisitNurseryView.this, RoutineVisitNurseryList.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RoutineVisitNurseryView.this, ActivityHome.class);
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
        Intent intent = new Intent(RoutineVisitNurseryView.this, RoutineVisitNurseryList.class);
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

    /*---------Code to bind list of Observation Details---------------------------------*/
    private void BindData(String visitUniqueId) {
        list.clear();
        dba.open();
        List<ObservationData> lables = dba.getObservationList(visitUniqueId);
        dba.close();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            lvList.setVisibility(View.VISIBLE);
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
            adapter = new CustomAdapter(RoutineVisitNurseryView.this, list);
            lvList.setAdapter(adapter);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);	
            lvList.setVisibility(View.GONE);
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
            final RoutineVisit4.ViewHolder holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_routine_visit4, null);
                holder = new RoutineVisit4.ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvDefectId = (TextView) arg1.findViewById(R.id.tvDefectId);
                holder.tvTitle = (TextView) arg1.findViewById(R.id.tvTitle);
                holder.tvRemark = (TextView) arg1.findViewById(R.id.tvRemark);
                holder.btnAttachment = (Button) arg1.findViewById(R.id.btnAttachment);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);
            } else {
                holder = (RoutineVisit4.ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(list.get(arg0).get("Id"));
            holder.tvDefectId.setText(list.get(arg0).get("DefectId"));
            holder.tvTitle.setText(list.get(arg0).get("Title"));
            holder.tvRemark.setText(list.get(arg0).get("Remark"));
            if (list.get(arg0).get("IsAttachment").equalsIgnoreCase("1")) {
                holder.btnAttachment.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach_green, 0, 0, 0);//Green
                holder.btnAttachment.setEnabled(true);
            } else {
                holder.btnAttachment.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_attach, 0, 0, 0);//Black
                holder.btnAttachment.setEnabled(false);
            }
            //if (list.get(arg0).get("IsSync").equalsIgnoreCase("1"))
            holder.btnDelete.setVisibility(View.GONE);
            //else
            //holder.btnDelete.setVisibility(View.VISIBLE);
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

                                Intent i1 = new Intent(RoutineVisitNurseryView.this,
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
                        common.showAlert(RoutineVisitNurseryView.this, "Error: " + except.getMessage(), false);
                    }
                }
            });
        /*---------------End of code to set Click Event for Viewing Attachment-------------------------*/

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
