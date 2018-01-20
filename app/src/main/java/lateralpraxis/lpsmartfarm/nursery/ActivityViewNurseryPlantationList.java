package lateralpraxis.lpsmartfarm.nursery;

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
import lateralpraxis.type.NurseryPlantationData;

public class ActivityViewNurseryPlantationList extends Activity {

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter customAdapter;

    /*Variables declaration*/
    private ArrayList<HashMap<String, String>> NurseryPlantationData;
    private String nurseryUniqueId, nurseryId, nurseryType, nurseryName, nurseryZoneName, zoneId;

    /*Code for control declaration*/
    private ListView listSelectPlantation;
    private TextView linkAdd, tvEmpty, tvNurseryUniqueId, tvNurseryId, tvNurseryType,
            tvNurseryName, tvNurseryZoneName;
    private View tvDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nursery_plantation_list);

        /*Code for setting action bar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        /*Code for creating instance of class*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        NurseryPlantationData = new ArrayList<>();

        /*Code for finding controls*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        linkAdd = (TextView) findViewById(R.id.linkAdd);
        tvNurseryUniqueId = (TextView) findViewById(R.id.tvNurseryUniqueId);
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvNurseryZoneName = (TextView) findViewById(R.id.tvNurseryZoneName);
        tvDivider= (View) findViewById(R.id.tvDivider);
        listSelectPlantation = (ListView) findViewById(R.id.listSelectPlantation);


        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            nurseryZoneName = extras.getString("nurseryZone");
            zoneId = extras.getString("nurseryZoneId");
            tvNurseryUniqueId.setText(nurseryUniqueId);
            tvNurseryId.setText(nurseryId);
            tvNurseryType.setText(nurseryType);
            tvNurseryName.setText(nurseryName);
            tvNurseryZoneName.setText(nurseryZoneName);
        }

        linkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityViewNurseryPlantationList.this,
                        ActivityAddNurseryPlantation.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurseryType", nurseryType);
                intent.putExtra("nurseryName", nurseryName);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
                startActivity(intent);
                finish();
            }
        });

        /*Code to Fetch and Bind Data from Temporary Assignment Table by Nursery Id*/
        /*dba.open();
        List<NurseryPlantationData> lables = dba.getNurseryPlantationList();
        int lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("PlantationUniqueId", lables.get(i).getPlantationUniqueId());
                hm.put("PlantType", lables.get(i).getPlantType());
                hm.put("Crop", lables.get(i).getCrop());
                hm.put("PlantationDate", lables.get(i).getPlantationDate());
                NurseryPlantationData.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
        dba.close();*/

         /*Code to Fetch and Bind Data from Temporary Assignment Table by Nursery Id*/
        dba.open();
        List<NurseryPlantationData> data = dba.getNurseryPlantationList(nurseryId,zoneId);
        int lsize = data.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < data.size(); i++) {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("PlantationUniqueId", data.get(i).getPlantationUniqueId());
                hm.put("PlantationCode", data.get(i).getPlantationCode());
                hm.put("PlantType", data.get(i).getPlantType());
                hm.put("Crop", data.get(i).getCrop());
                hm.put("PlantationDate", data.get(i).getPlantationDate().replace("/","-"));
                NurseryPlantationData.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        customAdapter = new CustomAdapter(ActivityViewNurseryPlantationList.this,
                NurseryPlantationData);
        if (lsize > 0)
            listSelectPlantation.setAdapter(customAdapter);

        listSelectPlantation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id
                        .tvPlantationUniqueId)).getText().toString()))) {
                    Intent intent = new Intent(ActivityViewNurseryPlantationList.this,
                            ActivityViewNurseryPlantation.class);
                    intent.putExtra("plantationUniqueId", String.valueOf(((TextView) item
                            .findViewById(R.id.tvPlantationUniqueId)).getText().toString()));
                    intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                    intent.putExtra("nurseryId", nurseryId);
                    intent.putExtra("nurseryType", nurseryType);
                    intent.putExtra("nurseryName", nurseryName);
                    intent.putExtra("nurseryZoneName", nurseryZoneName);
                    intent.putExtra("zoneId", zoneId);
                    intent.putExtra("plantationCode", String.valueOf(((TextView) item.findViewById(R.id.tvPlantationCode)).getText().toString()));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    //<editor-fold desc="Method to handle when Back button is pressed">
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityViewNurseryPlantationList.this,
                NurseryZoneView.class);
        intent.putExtra("nurseryUniqueId", nurseryUniqueId);
        intent.putExtra("nurseryId", nurseryId);
        intent.putExtra("nurType", nurseryType);
        intent.putExtra("nurName", nurseryName);
        intent.putExtra("nurseryZone", nurseryZoneName);
        intent.putExtra("nurseryZoneId", zoneId);
        startActivity(intent);
        finish();
        System.gc();
    }
    //</editor-fold>

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //To move from visit details to plantation details page
                Intent intent = new Intent(ActivityViewNurseryPlantationList.this,
                        NurseryZoneView.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurType", nurseryType);
                intent.putExtra("nurName", nurseryName);
                intent.putExtra("nurseryZone", nurseryZoneName);
                intent.putExtra("nurseryZoneId", zoneId);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(ActivityViewNurseryPlantationList.this, ActivityHome.class);
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

    //<editor-fold desc="Method to check android version ad load action bar appropriately">
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
        TextView tvPlantationUniqueId, tvPlantType, tvPlantationCrop, tvPlantationDate, tvPlantationCode;
    }

    //</editor-fold>
    //</editor-fold>
    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>>
                listSelectPlantation) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            NurseryPlantationData = listSelectPlantation;
        }

        @Override
        public int getCount() {
            return NurseryPlantationData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return NurseryPlantationData.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_nursery_plantation, null);
                holder = new ViewHolder();

                holder.tvPlantationUniqueId = (TextView) arg1.findViewById(R.id
                        .tvPlantationUniqueId);
                holder.tvPlantType = (TextView) arg1.findViewById(R.id.tvPlantType);
                holder.tvPlantationCrop = (TextView) arg1.findViewById(R.id.tvPlantationCrop);
                holder.tvPlantationDate = (TextView) arg1.findViewById(R.id.tvPlantationDate);
                holder.tvPlantationCode = (TextView) arg1.findViewById(R.id.tvPlantationCode);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvPlantationUniqueId.setText(NurseryPlantationData.get(arg0).get
                    ("PlantationUniqueId"));
            holder.tvPlantType.setText(NurseryPlantationData.get(arg0).get("PlantType"));
            holder.tvPlantationCrop.setText(NurseryPlantationData.get(arg0).get("Crop"));
            holder.tvPlantationCode.setText(NurseryPlantationData.get(arg0).get("PlantationCode"));
            holder.tvPlantationDate.setText(NurseryPlantationData.get(arg0).get("PlantationDate"));

            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }

}
