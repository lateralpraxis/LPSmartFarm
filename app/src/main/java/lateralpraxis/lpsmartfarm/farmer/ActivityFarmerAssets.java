package lateralpraxis.lpsmartfarm.farmer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import lateralpraxis.type.FarmerAssetData;

public class ActivityFarmerAssets extends Activity {
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;

    /*------------------Code for Variable Declaration---------------*/
    private String farmerUniqueId = "", farmerName = "", farmerMobile = "", userId = "";
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> AssetDetails;
    private static String lang;
    /*------------------Code for Control Declaration---------------*/
    private TextView tvNameData, tvMobile, tvEmpty;
    private View tvDivider;
    private Button btnSubmit;
    private ListView lvAssets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_assets);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        AssetDetails = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        //Setting UserId
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        /*------------------------End of code for creating instance of class--------------------*/

         /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*------------------------Start of code for finding controls-----------------------*/
        tvNameData = (TextView) findViewById(R.id.tvNameData);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider =(View)findViewById(R.id.tvDivider);
        lvAssets = (ListView) findViewById(R.id.lvAssets);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

         /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("Name");
            farmerMobile = extras.getString("Mobile");
            tvNameData.setText(farmerName);
            tvMobile.setText(farmerMobile);
        }


        /*---------Code to bind list of Asset Details---------------------------------*/
        dba.open();
        List<FarmerAssetData> lables = dba.getAssetDetailsByFarmerUniqueId(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getAssetId());
                hm.put("Name", String.valueOf(lables.get(i).getAssetName()));
                hm.put("Quantity", String.valueOf(lables.get(i).getAssetQty()));
                AssetDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        if(lables.size()>0)
            tvDivider.setVisibility(View.VISIBLE);
        else
            tvDivider.setVisibility(View.GONE);
        dba.close();

        Cadapter = new CustomAdapter(ActivityFarmerAssets.this, AssetDetails);
        if (lsize > 0)
            lvAssets.setAdapter(Cadapter);

        /*-------------------Code on Submit Button Click Event to Move Data from Temp To Main Table----------------------------*/
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int cnNoAct = 0;
                for (int i = 0; i < lvAssets.getCount(); i++) {
                    LinearLayout layout = (LinearLayout) lvAssets
                            .getChildAt(i);
                    LinearLayout alayout = (LinearLayout) layout.getChildAt(1);
                    EditText etQty = (EditText) alayout.getChildAt(1);
                    if (etQty.getText().toString().trim().length() > 0)
                        cnNoAct = cnNoAct + 1;
                }
                if(cnNoAct>0)
                {
                    dba.open();
                    dba.deleteFarmerAssetDetailsByFarmerUniqueId(farmerUniqueId);
                    dba.close();
                    for (int i = 0; i < lvAssets.getCount(); i++) {
                        LinearLayout layout1 = (LinearLayout) lvAssets
                                .getChildAt(i);
                        TextView tvAssetId = (TextView) layout1.getChildAt(0);
                        LinearLayout alayout1 = (LinearLayout) layout1.getChildAt(1);
                        EditText etQty = (EditText) alayout1.getChildAt(1);
                        if(etQty.getText().toString().trim().length() > 0)
                        {
                            dba.open();
                            dba.insertFarmerAssetsDetails(farmerUniqueId, tvAssetId.getText().toString(), etQty.getText().toString(),userId);
                            dba.close();
                        }
                        common.showToast(lang.equalsIgnoreCase("en")?"Asset detail saved successfully.":"संपत्ति का विस्तार सफलतापूर्वक सहेजा गया।",5,3);
                    }

                    Intent intent = new Intent(ActivityFarmerAssets.this, UpdateBlockAssignment.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("Name", farmerName);
                    intent.putExtra("Mobile", farmerMobile);
                    startActivity(intent);
                    finish();
                    System.gc();
                }
                else
                {
                    Intent intent = new Intent(ActivityFarmerAssets.this, UpdateBlockAssignment.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("Name", farmerName);
                    intent.putExtra("Mobile", farmerMobile);
                    startActivity(intent);
                    finish();
                    System.gc();
                }
            }
        });
        /*---------------End of code on Submit Button Click Event to Move Data from Temp To Main Table-------------------------*/
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvAssetId, tvAsset;
        EditText etQuantity;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvAssets) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            AssetDetails = lvAssets;
        }

        @Override
        public int getCount() {
            return AssetDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return AssetDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_farmer_assets, null);
                holder = new ViewHolder();

                holder.tvAssetId = (TextView) arg1.findViewById(R.id.tvAssetId);
                holder.tvAsset = (TextView) arg1.findViewById(R.id.tvAsset);
                holder.etQuantity = (EditText) arg1.findViewById(R.id.etQuantity);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvAssetId.setText(AssetDetails.get(arg0).get("Id"));
            holder.tvAsset.setText(AssetDetails.get(arg0).get("Name"));
            holder.etQuantity.setText(AssetDetails.get(arg0).get("Quantity"));

            holder.etQuantity.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (holder.etQuantity.getText().toString().trim()
                            .length() > 0 ) {
                        if(Double.valueOf(holder.etQuantity.getText().toString())<=0)
                            holder.etQuantity.setText("");

                    }
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    /*if (holder.etQuantity.getText().toString().trim()
                            .length() > 0 ) {
                        if(Double.valueOf(holder.etQuantity.getText().toString())<=0)
                            holder.etQuantity.setText("");

                    }*/
                }

                public void afterTextChanged(Editable s) {
               /*     if (holder.etQuantity.getText().toString().trim()
                            .length() > 0 ) {
                        if(Double.valueOf(holder.etQuantity.getText().toString())<=0)
                            holder.etQuantity.setText("");

                    }*/
                }
            });

            holder.etQuantity
                    .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean gainFocus) {
                            // onFocus
                            if (gainFocus) {

                            }
                            // onBlur
                            else {
                                if (holder.etQuantity.getText().toString()
                                        .trim().length() > 0) {
                                    if (Double.valueOf(holder.etQuantity
                                            .getText().toString().trim()) <=0) {
                                        common.showToast(lang.equalsIgnoreCase("en")?"Please enter valid quantity":"कृपया वैध मात्रा दर्ज करें");
                                        holder.etQuantity.setText("");
                                    }
                                }
                            }
                        }
                    });
            //arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }

    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityFarmerLoan.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityFarmerAssets.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(ActivityFarmerAssets.this, ActivityHome.class);
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
        Intent i = new Intent(ActivityFarmerAssets.this, ActivityFarmerLoan.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
        i.putExtra("Name", farmerName);
        i.putExtra("Mobile", farmerMobile);
        startActivity(i);
        this.finish();
        System.gc();
    }
}
