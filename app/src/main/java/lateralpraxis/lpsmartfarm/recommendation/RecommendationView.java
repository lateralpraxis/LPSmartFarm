package lateralpraxis.lpsmartfarm.recommendation;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.VisitNurseryData;

public class RecommendationView extends Activity {
    final Context context = this;
    CustomAdapter adapter;
    File file;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvRecommendationHeader, tvFarmerHeader, tvMobileHeader, tvFarmBlockHeader, tvFarmer, tvMobile, tvFarmBlock, tvPlantation, tvEmpty;
    private ListView lvList;
    private View tvDivider;
    /*-------------------------Code for Variable Declaration---------------------------------------*/
    private List<VisitNurseryData> obj;
    private String uniqueId, FromPage;
    private int lsize = 0, cnt = 0;
    //Variable for displaying File
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommendation_view);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*------------------------Start of code for finding Controls--------------------------*/
        tvRecommendationHeader = (TextView) findViewById(R.id.tvRecommendationHeader);
        tvFarmerHeader = (TextView) findViewById(R.id.tvFarmerHeader);
        tvMobileHeader = (TextView) findViewById(R.id.tvMobileHeader);
        tvFarmBlockHeader = (TextView) findViewById(R.id.tvFarmBlockHeader);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvPlantation = (TextView) findViewById(R.id.tvPlantation);
        lvList = (ListView) findViewById(R.id.lvList);
        tvDivider = (View) findViewById(R.id.tvDivider);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
     /*------------------------End of code for finding Controls--------------------------*/

    /*-----------------Code to set Farm Block Unique Id--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            uniqueId = extras.getString("uniqueId");
            FromPage = extras.getString("FromPage");
            if (FromPage.equalsIgnoreCase("FarmBlock"))
                tvRecommendationHeader.setText("View Farm Block Recommendation");
            else
                tvRecommendationHeader.setText("View Nursery Recommendation");
        }

     /*---------------Start of code to bind Farm Block details-------------------------*/
        if (uniqueId != null) {
            dba.openR();
            obj = dba.GetRecommendationByUniqueId(uniqueId);
            tvFarmer.setText(obj.get(0).getNurseryType());
            tvMobile.setText(obj.get(0).getNursery());
            tvFarmBlock.setText(obj.get(0).getZone());
            tvPlantation.setText(obj.get(0).getPlantation());
            if (obj.get(0).getNurseryType().equalsIgnoreCase("Main") || obj.get(0).getNurseryType().equalsIgnoreCase("Mini")) {
                tvFarmerHeader.setText("Type");
                tvMobileHeader.setText("Nursery");
                tvFarmBlockHeader.setText("Zone");
            } else {
                tvFarmerHeader.setText("Farmer");
                tvMobileHeader.setText("Mobile");
                tvFarmBlockHeader.setText("Farm Block");
            }

            /*---------Code to bind list of Recommendation Details---------------------------------*/
            BindData(uniqueId);
        }
    }


    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(RecommendationView.this, ActivityHome.class);
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
        Intent intent;
        if (FromPage.equalsIgnoreCase("FarmBlock"))
            intent = new Intent(RecommendationView.this, RecommendationList.class);
        else
            intent = new Intent(RecommendationView.this, RecommendationNurseryList.class);
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

    /*---------Code to bind list of Recommendation Details---------------------------------*/
    private void BindData(String uniqueId) {
        dba.openR();
        ArrayList<HashMap<String, String>> list = dba.GetRecommendationDetailByUniqueId(uniqueId);
        dba.close();
        lsize = list.size();
        if (list.size() != 0) {
            lvList.setAdapter(new CustomAdapter(context, list));
            ViewGroup.LayoutParams params = lvList.getLayoutParams();
            params.height = 500;
            lvList.setLayoutParams(params);
            lvList.requestLayout();
            tvEmpty.setVisibility(View.GONE);
tvDivider.setVisibility(View.VISIBLE);
        } else {
            lvList.setAdapter(null);
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);	
        }
    }

    /*-----------Code for Handling Data Binding---------------------------*/
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

                                    Intent i1 = new Intent(RecommendationView.this, ViewImage.class);
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
                        common.showAlert(RecommendationView.this, "Error: " + except.getMessage(), false);

                    }
                }
            });

            holder.btnDelete.setVisibility(View.GONE);
            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
}
