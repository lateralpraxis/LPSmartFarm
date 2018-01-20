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
import lateralpraxis.type.NurseryZoneData;

public class NurseryZone extends Activity {
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*--------------Start of Code for variable declaration-----------*/

    private int lsize = 0;
    private ArrayList<HashMap<String, String>> NurseryZoneDetails;
    private String nurseryUniqueId, nurseryId, nurseryZoneId, nurType, nurName;

    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView listSelectNurseryZone;
    private TextView tvEmpty, tvHeaderData;
    private View tvDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nursery_zone_list);

         /*------------------------Start of code for setting action
         bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurType = extras.getString("nurType");
            nurName = extras.getString("nurName");
        }
        /*------------------------Start of code for creating instance of
         class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        NurseryZoneDetails = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvHeaderData = (TextView) findViewById(R.id.tvHeaderData);
        tvDivider = (View) findViewById(R.id.tvDivider);
        tvHeaderData.setText("Nursery Type : " + nurType + ", Name : " + nurName);
        listSelectNurseryZone = (ListView) findViewById(R.id.listSelectNurseryZone);
        dba.open();
        List<NurseryZoneData> lables = dba.getNurseryZoneList(nurseryId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("UniqueId", lables.get(i).getUniqueId());
                hm.put("NurseryId", lables.get(i).getNurseryId());
                hm.put("NurseryZoneId", lables.get(i).getNurseryZoneId());
                hm.put("NurseryZoneName", String.valueOf(lables.get(i).getNurseryZoneName()));
                hm.put("NurseryZoneArea", String.valueOf(lables.get(i).getNurseryZoneArea()));
                NurseryZoneDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(NurseryZone.this, NurseryZoneDetails);
        if (lsize > 0)
            listSelectNurseryZone.setAdapter(Cadapter);

        listSelectNurseryZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id
                        .tvNurseryZoneName)).getText().toString()))) {
                    Intent intent = new Intent(NurseryZone.this, NurseryZoneView.class);
                    intent.putExtra("nurseryId", String.valueOf(((TextView) item.findViewById(R
                            .id.tvNurseryId)).getText().toString()));
                    intent.putExtra("nurseryZoneId", String.valueOf(((TextView) item.findViewById
                            (R.id.tvNurseryZoneId)).getText().toString()));
                    intent.putExtra("nurType", nurType);
                    intent.putExtra("nurName", nurName);
                    intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(NurseryZone.this, NurseryView.class);
        i.putExtra("nurseryUniqueId", nurseryUniqueId);
        i.putExtra("nurseryId", nurseryId);
        i.putExtra("nurType", nurType);
        i.putExtra("nurName", nurName);
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        this.finish();
        System.gc();
    }

    // When press back button go to home screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, NurseryView.class);
                intent.putExtra("nurseryUniqueId", nurseryUniqueId);
                intent.putExtra("nurseryId", nurseryId);
                intent.putExtra("nurType", nurType);
                intent.putExtra("nurName", nurName);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent homeScreenIntent = new Intent(this, ActivityHome.class);
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
        TextView tvNurseryId, tvNurseryZoneId, tvUniqueId, tvNurseryZoneName, tvArea;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>>
                listSelectNurseryZone) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            NurseryZoneDetails = listSelectNurseryZone;
        }

        @Override
        public int getCount() {
            return NurseryZoneDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return NurseryZoneDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_nurseries_zone, null);
                holder = new ViewHolder();
                holder.tvNurseryId = (TextView) arg1.findViewById(R.id.tvNurseryId);
                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvNurseryZoneId = (TextView) arg1.findViewById(R.id.tvNurseryZoneId);
                holder.tvNurseryZoneName = (TextView) arg1.findViewById(R.id.tvNurseryZoneName);
                holder.tvArea = (TextView) arg1.findViewById(R.id.tvArea);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }
            holder.tvNurseryId.setText(NurseryZoneDetails.get(arg0).get("NurseryId"));
            holder.tvUniqueId.setText(NurseryZoneDetails.get(arg0).get("UniqueId"));
            holder.tvNurseryZoneId.setText(NurseryZoneDetails.get(arg0).get("NurseryZoneId"));
            holder.tvNurseryZoneName.setText(NurseryZoneDetails.get(arg0).get("NurseryZoneName"));
            holder.tvArea.setText(NurseryZoneDetails.get(arg0).get("NurseryZoneArea"));
            if (TextUtils.isEmpty(NurseryZoneDetails.get(arg0).get("NurseryZoneName")))
                holder.tvNurseryZoneName.setVisibility(View.GONE);
            else
                holder.tvNurseryZoneName.setVisibility(View.VISIBLE);
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
