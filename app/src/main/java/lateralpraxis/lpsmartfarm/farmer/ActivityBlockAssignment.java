package lateralpraxis.lpsmartfarm.farmer;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.OperationalBlocks;

public class ActivityBlockAssignment extends Activity {

    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*-----------Start of Code for control declaration-----------*/
    private Spinner spDistrict, spBlock;
    private Button btnSubmit,btnAdd;
    private ListView lvAssignedBlockList;
    private TableLayout tableLayoutHeader;
    private TextView  tvEmpty;
    private View tvDivider;
    /*-----------End of Code for control declaration-----------*/

    /*--------------Start of Code for variable declaration-----------*/
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> AssignedBlockDetails;
    private String farmerUniqueId;

    /*--------------End of Code for variable declaration-----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_assignment);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        AssignedBlockDetails = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Start of code for finding controls-----------------------*/
        spDistrict = (Spinner) findViewById(R.id.spDistrict);
        spBlock = (Spinner) findViewById(R.id.spBlock);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        tableLayoutHeader = (TableLayout) findViewById(R.id.tableLayoutHeader);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        lvAssignedBlockList = (ListView) findViewById(R.id.lvDocOperationalBlocks);
        /*-------Code to delete Assigned Blocks in temporary table-----------*/
        dba.open();
        dba.DeleteTempAssignedBlocks();
        dba.close();

        /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
        }
       /*------------Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
        dba.open();
        List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                AssignedBlockDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tableLayoutHeader.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(ActivityBlockAssignment.this, AssignedBlockDetails);
        if (lsize > 0)
        {
            lvAssignedBlockList.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvAssignedBlockList.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.VISIBLE);
        }
        else
        {
            tvEmpty.setVisibility(View.VISIBLE);
            lvAssignedBlockList.setVisibility(View.GONE);
            tvDivider.setVisibility(View.GONE);
        }


        /*------------------------Start of code for binding data in Spinner-----------------------*/
        spDistrict.setAdapter(DataAdapter("alldistrict", ""));

        /*------------------------Code on Select Item Change to bind Blocks-----------------------*/
        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spBlock.setAdapter(DataAdapter("block", String.valueOf(((CustomType) spDistrict.getSelectedItem()).getId())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    /*---------------Start of code to be executed on Click Event Of buttons-------------------------*/
    /*------------Code to be executed on submit button click---------------------------------*/
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dba.openR();
                if ((spDistrict.getSelectedItemPosition() == 0))
                    common.showToast("District is mandatory",5,1);
                else if ((spBlock.getSelectedItemPosition() == 0))
                    common.showToast("Block is mandatory",5,1);
                else if (dba.isBlockAlreadyAdded(farmerUniqueId, ((CustomType) spDistrict.getSelectedItem()).getId().toString(), ((CustomType) spBlock.getSelectedItem()).getId().toString()).equals(true)) {
                    common.showToast("Block is already assigned",5,1);
                } else {
                    dba.open();
                    dba.insertFarmerOperatingBlocksTemp(farmerUniqueId, ((CustomType) spDistrict.getSelectedItem()).getId().toString(), ((CustomType) spBlock.getSelectedItem()).getId().toString());
                    dba.close();
                    common.showToast("Block added successfully.",5,3);
                    spDistrict.setSelection(0);
                    spBlock.setSelection(0);
                    AssignedBlockDetails.clear();
                    dba.openR();
                    List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
                    lsize = lables.size();
                    if (lsize > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        tableLayoutHeader.setVisibility(View.VISIBLE);
                        for (int i = 0; i < lables.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("Id", lables.get(i).getId());
                            hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                            hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                            AssignedBlockDetails.add(hm);
                        }
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tableLayoutHeader.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                    }
                    Cadapter = new CustomAdapter(ActivityBlockAssignment.this, AssignedBlockDetails);
                    if (lsize > 0)
                    {
                        lvAssignedBlockList.setAdapter(Cadapter);
                        tvEmpty.setVisibility(View.GONE);
                        lvAssignedBlockList.setVisibility(View.VISIBLE);
                        tvDivider.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvEmpty.setVisibility(View.VISIBLE);
                        lvAssignedBlockList.setVisibility(View.GONE);
                        tvDivider.setVisibility(View.GONE);
                    }

                }
            }
        });
        /*------------Code to be executed on submit button click---------------------------------*/
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle("Confirmation");
                builder1.setMessage("Are you sure, you want to submit Assigned Blocks and Farmer details?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                    dba.open();
                                    dba.insertFarmer(farmerUniqueId);
                                    dba.close();
                                    common.showToast("Farmer details saved successfully.",5,3);
                                    Intent intent = new Intent(ActivityBlockAssignment.this, ActivityHome.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    System.gc();
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
        /*---------------End of code to be executed on Click Event Of buttons-------------------------*/
    }

    // When press back button go to home screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, FarmerCreateStep1.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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


    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ActivityBlockAssignment.this, FarmerCreateStep1.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
        startActivity(i);
        this.finish();
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
        TextView tvId, tvDistrict, tvBlock;
        Button btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvAssignedBlockList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            AssignedBlockDetails = lvAssignedBlockList;
        }

        @Override
        public int getCount() {
            return AssignedBlockDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return AssignedBlockDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_assigned_blocks, null);
                holder = new ViewHolder();
                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvDistrict = (TextView) arg1.findViewById(R.id.tvDistrict);
                holder.tvBlock = (TextView) arg1.findViewById(R.id.tvBlock);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(AssignedBlockDetails.get(arg0).get("Id"));
            holder.tvDistrict.setText(AssignedBlockDetails.get(arg0).get("DistrictName"));
            holder.tvBlock.setText(AssignedBlockDetails.get(arg0).get("BlockName"));
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Delete Operational Block");
                    builder1.setMessage("Are you sure you want to delete this Block?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    dba.DeleteTempAssignedBlocksById(String.valueOf(holder.tvId.getText()));
                                    dba.close();
                                    common.showToast("Block deleted successfully.",5,3);
                                    AssignedBlockDetails.clear();
                                    dba.open();
                                    List<OperationalBlocks> lables = dba.getOperationalDistrictTemp(farmerUniqueId);
                                    lsize = lables.size();
                                    if (lsize > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                        tableLayoutHeader.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < lables.size(); i++) {
                                            HashMap<String, String> hm = new HashMap<String, String>();
                                            hm.put("Id", lables.get(i).getId());
                                            hm.put("DistrictName", String.valueOf(lables.get(i).getDistrictName()));
                                            hm.put("BlockName", String.valueOf(lables.get(i).getBlockName()));
                                            AssignedBlockDetails.add(hm);
                                        }
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tableLayoutHeader.setVisibility(View.GONE);
                                        btnSubmit.setVisibility(View.GONE);
                                    }
                                    dba.close();

                                    Cadapter = new CustomAdapter(ActivityBlockAssignment.this, AssignedBlockDetails);
                                    if (lsize > 0)
                                    {
                                        lvAssignedBlockList.setAdapter(Cadapter);
                                        tvEmpty.setVisibility(View.GONE);
                                        lvAssignedBlockList.setVisibility(View.VISIBLE);
                                        tvDivider.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        lvAssignedBlockList.setVisibility(View.GONE);
                                        tvDivider.setVisibility(View.GONE);
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
         arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
