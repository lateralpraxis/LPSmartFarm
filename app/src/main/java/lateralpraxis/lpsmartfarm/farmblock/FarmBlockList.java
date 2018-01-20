package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.farmer.ActivityFarmerView;
import lateralpraxis.type.FarmBlockData;

public class FarmBlockList extends Activity {
    final Context context = this;
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    private Intent intent;
    /*--------------Start of Code for variable declaration-----------*/
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> label_addFarmBlock;
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile,farmerCode,userRole;
    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView list;
    private Button btnUpdate;
    private TextView tvEmpty, linkFarmBlock, tvFarmer, tvFarmerCode;
    private LinearLayout llFarmerData;
    private View tvDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farm_block_list);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        label_addFarmBlock = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvFarmerCode = (TextView) findViewById(R.id.tvFarmerCode);
        tvDivider= (View) findViewById(R.id.tvDivider);
        linkFarmBlock = (TextView) findViewById(R.id.linkFarmBlock);
        list = (ListView) findViewById(R.id.list);
        llFarmerData = (LinearLayout) findViewById(R.id.llFarmerData);
        dba.openR();
        Boolean isFarmerEditable=false;

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmerCode = extras.getString("farmerCode");
            tvFarmer.setText(farmerName);
            tvFarmerCode.setText(farmerCode);
            userRole = dba.getAllRoles();
            isFarmerEditable =dba.isFarmerEditable(farmerUniqueId);
            if(isFarmerEditable || userRole.contains("Farmer"))
                linkFarmBlock.setVisibility(View.VISIBLE);
            else
                linkFarmBlock.setVisibility(View.GONE);
        }

        linkFarmBlock.setOnClickListener(new View.OnClickListener() {
            //On click of view delivery button
            @Override
            public void onClick(View arg0) {
                intent = new Intent(context, FarmBlock.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", "0");
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", "0");
                intent.putExtra("farmerCode", farmerCode);
                intent.putExtra("entryType", "add");
                startActivity(intent);
                finish();
            }
        });

        /*------------Code to Fetch and Bind Data from Farm Block Table*/
        dba.open();
        List<FarmBlockData> lables = dba.getFarmBlockList(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            llFarmerData.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("FarmBlockUniqueId", lables.get(i).getFarmBlockUniqueId());
                hm.put("FarmBlockCode", String.valueOf(lables.get(i).getFarmBlockCode()));
                hm.put("KhataNo", String.valueOf(lables.get(i).getKhataNo()));
                hm.put("KhasraNo", String.valueOf(lables.get(i).getKhasraNo()));
                hm.put("Acreage", String.valueOf(lables.get(i).getAcreage()));
                hm.put("ContractDate", String.valueOf(lables.get(i).getContractDate()));
                hm.put("Address", String.valueOf(lables.get(i).getAddress()));
                label_addFarmBlock.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
            llFarmerData.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(FarmBlockList.this, label_addFarmBlock);
        if (lsize > 0)
            list.setAdapter(Cadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                //To view detail of selected farm block
                Intent intent = new Intent(FarmBlockList.this, FarmBlockView.class);
                intent.putExtra("farmBlockUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
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
                Intent intent = new Intent(FarmBlockList.this, ActivityFarmerView.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockList.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {*/
                                Intent homeScreenIntent = new Intent(FarmBlockList.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
                            /*}
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
                alertDialog.show();*/
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

        Intent intent = new Intent(FarmBlockList.this, ActivityFarmerView.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
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

    public static class ViewHolder {
        TextView tvUniqueId, tvCode,tvKhasraKhata, tvArea, tvContractDate,tvAddress;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            label_addFarmBlock = list;
        }

        @Override
        public int getCount() {
            return label_addFarmBlock.size();
        }

        @Override
        public Object getItem(int arg0) {
            return label_addFarmBlock.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.farm_block_list_item, null);
                holder = new ViewHolder();

                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvCode = (TextView) arg1.findViewById(R.id.tvCode);
                holder.tvKhasraKhata = (TextView) arg1.findViewById(R.id.tvKhasraKhata);
                holder.tvArea = (TextView) arg1.findViewById(R.id.tvArea);
                holder.tvContractDate = (TextView) arg1.findViewById(R.id.tvContractDate);
                holder.tvAddress = (TextView) arg1.findViewById(R.id.tvAddress);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvUniqueId.setText(label_addFarmBlock.get(arg0).get("FarmBlockUniqueId"));
            holder.tvCode.setText(":\t"+label_addFarmBlock.get(arg0).get("FarmBlockCode"));
            holder.tvKhasraKhata.setText(":\t"+label_addFarmBlock.get(arg0).get("KhataNo")+" / "+label_addFarmBlock.get(arg0).get("KhasraNo"));
            holder.tvArea.setText(":\t"+label_addFarmBlock.get(arg0).get("Acreage"));
            holder.tvContractDate.setText(":\t"+common.convertToDisplayDateFormat(label_addFarmBlock.get(arg0).get("ContractDate")));
            holder.tvAddress.setText(":\t"+label_addFarmBlock.get(arg0).get("Address"));
            if (TextUtils.isEmpty(label_addFarmBlock.get(arg0).get("FarmBlockCode")))
                holder.tvCode.setVisibility(View.GONE);
            else
                holder.tvCode.setVisibility(View.VISIBLE);
           arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
