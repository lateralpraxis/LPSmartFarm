package lateralpraxis.lpsmartfarm.farmblock;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import lateralpraxis.type.LandCharacteristic;

public class FarmBlockCharacterstic extends Activity {
    CustomAdapter Cadapter;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvFarmer, tvMobile, tvFarmBlock, tvEmpty;
    private View tvDivider;
    private Button btnBack, btnNext;
    private LinearLayout llFarmBlock;
    private ListView lvCharacterstic;
    /*------------------------End of code for controls Declaration------------------------------*/
     /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, farmerUniqueId, farmBlockUniqueId = "0", farmerName, farmerMobile, farmBlockCode, entryType, farmerCode;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> CharacteristicDetails;
    private static String lang;
    /*------------------------End of code for Variable Declaration------------------------------*/
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    /*------------------------End of code for class Declaration------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_block_characterstic);

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        CharacteristicDetails = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            entryType = extras.getString("entryType");
            farmerCode = extras.getString("farmerCode");
        }

         /*------------------------Start of code for controls Declaration--------------------------*/
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        llFarmBlock = (LinearLayout) findViewById(R.id.llFarmBlock);
        tvFarmBlock = (TextView) findViewById(R.id.tvFarmBlock);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider= (View) findViewById(R.id.tvDivider);
        lvCharacterstic = (ListView) findViewById(R.id.lvCharacterstic);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);

        /*--------Code to set Farmer Details---------------------*/
        tvFarmer.setText(farmerName);
        tvMobile.setText(farmerMobile);
        if (entryType.equalsIgnoreCase("add"))
            llFarmBlock.setVisibility(View.GONE);
        else {
            tvFarmBlock.setText(farmBlockCode);
            llFarmBlock.setVisibility(View.VISIBLE);
        }

        /*---------Code to bind list of Land Characteristic Details---------------------------------*/
        dba.open();
        List<LandCharacteristic> lables = dba.getLandCharacteristicByFarmBlockUniqueId(farmBlockUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("Name", String.valueOf(lables.get(i).getTitle()));
                hm.put("CharacteristicId", String.valueOf(lables.get(i).getCharacteristicId()));
                CharacteristicDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(FarmBlockCharacterstic.this, CharacteristicDetails);
        if (lsize > 0)
            lvCharacterstic.setAdapter(Cadapter);
        /*---------------Start of code to set Click Event for Button Back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move back Farm Block Cropping Pattern page
                Intent intent = new Intent(FarmBlockCharacterstic.this, FarmBlockCroppingPattern.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("entryType", entryType);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dba.open();
                dba.deleteFarmBlockLandCharacteristicByFarmBlockUniqueId(farmBlockUniqueId);
                dba.close();
                int chkCount = 0;
                for (int i = 0; i < lvCharacterstic.getCount(); i++) {
                    LinearLayout layout1 = (LinearLayout) lvCharacterstic
                            .getChildAt(i);
                    TextView tvId = (TextView) layout1.getChildAt(0);
                    LinearLayout alayout1 = (LinearLayout) layout1.getChildAt(1);
                    CheckBox chkCharacteristic = (CheckBox) alayout1.getChildAt(1);
                    if (chkCharacteristic.isChecked()) {
                        chkCount = chkCount + 1;
                        dba.open();
                        dba.insertFarmBlockLandCharacteristic(farmBlockUniqueId, tvId.getText().toString());
                        dba.close();
                    }
                }
                if (chkCount > 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Characteristic details saved successfully.":"विशेषता विवरण सफलतापूर्वक सहेजे गए।", 5, 3);
                //To move next Farm Block Issues page
                Intent intent = new Intent(FarmBlockCharacterstic.this, FarmBlockIssues.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("entryType", entryType);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FarmBlockCharacterstic.this, FarmBlockCroppingPattern.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                intent.putExtra("farmerName", farmerName);
                intent.putExtra("farmerMobile", farmerMobile);
                intent.putExtra("farmBlockCode", farmBlockCode);
                intent.putExtra("entryType", entryType);
                intent.putExtra("farmerCode", farmerCode);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmBlockCharacterstic.this);
                // set title
                alertDialogBuilder.setTitle(lang.equalsIgnoreCase("en")?"Confirmation":"पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to leave this module it will discard any unsaved data?":"क्या आप निश्चित हैं, क्या आप इस मॉड्यूल को छोड़ना चाहते हैं, यह किसी भी सहेजे न गए डेटा को त्याग देगा?")
                        .setCancelable(false)
                        .setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmBlockCharacterstic.this, ActivityHome.class);
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

        Intent intent = new Intent(FarmBlockCharacterstic.this, FarmBlockCroppingPattern.class);
        intent.putExtra("farmerUniqueId", farmerUniqueId);
        intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
        intent.putExtra("farmerName", farmerName);
        intent.putExtra("farmerMobile", farmerMobile);
        intent.putExtra("farmBlockCode", farmBlockCode);
        intent.putExtra("entryType", entryType);
        intent.putExtra("farmerCode", farmerCode);
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
        TextView tvId, tvCharacteristic;
        CheckBox chkCharacteristic;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvCharacterstic) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            CharacteristicDetails = lvCharacterstic;
        }

        @Override
        public int getCount() {
            return CharacteristicDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return CharacteristicDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_land_characteristic, null);
                holder = new ViewHolder();

                holder.tvId = (TextView) arg1.findViewById(R.id.tvId);
                holder.tvCharacteristic = (TextView) arg1.findViewById(R.id.tvCharacteristic);
                holder.chkCharacteristic = (CheckBox) arg1.findViewById(R.id.chkCharacteristic);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvId.setText(CharacteristicDetails.get(arg0).get("Id"));
            holder.tvCharacteristic.setText(CharacteristicDetails.get(arg0).get("Name"));
            if (CharacteristicDetails.get(arg0).get("CharacteristicId").equalsIgnoreCase("0"))
                holder.chkCharacteristic.setChecked(false);
            else
                holder.chkCharacteristic.setChecked(true);
            return arg1;
        }

    }
}
