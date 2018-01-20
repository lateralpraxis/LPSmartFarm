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
import lateralpraxis.type.NurseryData;

public class Nursery extends Activity {
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    /*--------------Start of Code for variable declaration-----------*/

    private int lsize = 0;
    private ArrayList<HashMap<String, String>> NurseryDetails;

    /*--------------End of Code for variable declaration-----------*/

    /*-----------Start of Code for control declaration-----------*/
    private ListView listSelectNursery;
    private TextView tvEmpty;
    private View tvDivider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nursery_list);

         /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

         /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        NurseryDetails = new ArrayList<HashMap<String, String>>();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Code for finding controls-----------------------*/
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        listSelectNursery = (ListView) findViewById(R.id.listSelectNursery);
        /*------------Code to Fetch and Bind Data from Temporary Assignment Table by Farmer Unique Id*/
        dba.open();
        List<NurseryData> lables = dba.getNurseryList();
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            tvDivider.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("NurseryType", lables.get(i).getNurseryType());
                hm.put("NurseryUniqueId", lables.get(i).getNurseryUniqueId());
                hm.put("NurseryId", lables.get(i).getNurseryId());
                hm.put("NurseryName", String.valueOf(lables.get(i).getNurseryName()));
                hm.put("KhataNo", String.valueOf(lables.get(i).getKhataNo()));
                hm.put("KhasraNo", String.valueOf(lables.get(i).getKhasraNo()));
                hm.put("Address", String.valueOf(lables.get(i).getAddress()));
                hm.put("NurseryArea", String.valueOf(lables.get(i).getNurseryArea()));
                NurseryDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvDivider.setVisibility(View.GONE);
        }
        dba.close();

        Cadapter = new CustomAdapter(Nursery.this, NurseryDetails);
        if (lsize > 0)
            listSelectNursery.setAdapter(Cadapter);

        listSelectNursery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                if (!TextUtils.isEmpty(String.valueOf(((TextView) item.findViewById(R.id.tvNurseryName)).getText().toString()))) {
                    Intent intent = new Intent(Nursery.this, NurseryView.class);
                    intent.putExtra("nurseryUniqueId", String.valueOf(((TextView) item.findViewById(R.id.tvUniqueId)).getText().toString()));
                    intent.putExtra("nurseryId", String.valueOf(((TextView) item.findViewById(R.id.tvNurseryId)).getText().toString()));
                    intent.putExtra("nurType", String.valueOf(((TextView) item.findViewById(R.id.tvNurseryType)).getText().toString()));
                    intent.putExtra("nurName", String.valueOf(((TextView) item.findViewById(R.id.tvNurseryName)).getText().toString()));
                    intent.putExtra("nurseryZoneId", "0");
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Nursery.this, ActivityHome.class);
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
                Intent intent = new Intent(this, ActivityHome.class);
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
        TextView tvNurseryType, tvNurseryId, tvUniqueId, tvNurseryName, tvKhata, tvKhasra,tvArea,tvAddress;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> listSelectNursery) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            NurseryDetails = listSelectNursery;
        }

        @Override
        public int getCount() {
            return NurseryDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return NurseryDetails.get(arg0);
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
                arg1 = mInflater.inflate(R.layout.list_nurseries, null);
                holder = new ViewHolder();
                holder.tvNurseryType = (TextView) arg1.findViewById(R.id.tvNurseryType);
                holder.tvNurseryId = (TextView) arg1.findViewById(R.id.tvNurseryId);
                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvNurseryName = (TextView) arg1.findViewById(R.id.tvNurseryName);
                holder.tvKhata = (TextView) arg1.findViewById(R.id.tvKhata);
                holder.tvKhasra = (TextView) arg1.findViewById(R.id.tvKhasra);
                holder.tvArea = (TextView) arg1.findViewById(R.id.tvArea);
                holder.tvAddress = (TextView) arg1.findViewById(R.id.tvAddress);

                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }
            holder.tvNurseryType.setText(NurseryDetails.get(arg0).get("NurseryType"));
            holder.tvNurseryId.setText(NurseryDetails.get(arg0).get("NurseryId"));
            holder.tvUniqueId.setText(NurseryDetails.get(arg0).get("NurseryUniqueId"));
            holder.tvNurseryName.setText(NurseryDetails.get(arg0).get("NurseryName"));
            holder.tvKhata.setText(NurseryDetails.get(arg0).get("KhataNo"));
            holder.tvKhasra.setText(NurseryDetails.get(arg0).get("KhasraNo"));
            holder.tvArea.setText(NurseryDetails.get(arg0).get("NurseryArea"));
            holder.tvAddress.setText(NurseryDetails.get(arg0).get("Address"));
            if(TextUtils.isEmpty(NurseryDetails.get(arg0).get("NurseryName")))
                holder.tvNurseryName.setVisibility(View.GONE);
            else
                holder.tvNurseryName.setVisibility(View.VISIBLE);
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }
    }
}
