package lateralpraxis.lpsmartfarm.returns;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityMainStockReturn extends Activity {

    //<editor-fold desc="Code For Class Declaration">
    final Context context = this;
    private final Context mContext = this;
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    HashMap<String, String> map = null;
    //</editor-fold>
    private Intent intent;
    //<editor-fold desc="Code For Control Declaration">
    private ListView lvPendingDispatch;
    private View tvDivider;
    //</editor-fold>
    private TextView tvEmpty;
    //<editor-fold desc="Code For Variable Declaration">
    private String responseJSON,userId;
    private int listSize = 0;
    private ArrayList<HashMap<String, String>> wordList = null;
    //</editor-fold>

    //<editor-fold desc="Code to be executed on On Create">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stock_return);

        //<editor-fold desc="Code For Setting Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Code For Creating Instance of Class">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //<editor-fold desc="Code For Finding Text View">
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvDivider = (View) findViewById(R.id.tvDivider);
        lvPendingDispatch = (ListView) findViewById(R.id.lvPendingDispatch);
        //</editor-fold>

        //<editor-fold desc="Code For Setting User Id">
        final HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        //</editor-fold>

        if(common.isConnected())
        {
            AsyncPendingDispatchWSCall task = new AsyncPendingDispatchWSCall();
            task.execute();
        }
        else
        {
            Intent intent = new Intent(mContext, ActivityHome.class);
            startActivity(intent);
            finish();
        }
        lvPendingDispatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> lv, View item, int position, long id) {
                Intent intent = new Intent(ActivityMainStockReturn.this, ActivityStockReturnDetail.class);
                intent.putExtra("DispatchId", String.valueOf(((TextView) item.findViewById(R.id.tvDispatchId)).getText().toString()));
                startActivity(intent);
                finish();
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Code for Providing Functionality on Back key Press And Action Bar Back and Home Button">
    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityMainStockReturn.this, ActivityHome.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_go_to_home:
                Intent intent1 = new Intent(ActivityMainStockReturn.this, ActivityHome.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // When press back button go to home screen
    @Override
    public void onBackPressed() {
        Intent homeScreenIntent = new Intent(ActivityMainStockReturn.this, ActivityHome.class);
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeScreenIntent);
        finish();
    }
    // To create menu on inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
//</editor-fold>

    //<editor-fold desc="Code Binding Data In List">
    public static class viewHolder {
        TextView tvDispatchId, tvDispatchDetails, tvDispatchToDetails;
        int ref;
    }
    //</editor-fold>

    //<editor-fold desc="Code for fetching Pending Dispatches from Portal By User Id">
    private class AsyncPendingDispatchWSCall extends
            AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(
                ActivityMainStockReturn.this);

        @Override
        protected String doInBackground(String... params) {
            try {

                String[] name = { };
                String[] value = {  };
                // Call method of web service to Get Pending Dispatch Data
                responseJSON = "";
                responseJSON = common.invokeJSONWS(userId, "userId", "GetPendingDispatchForAndroid", common.url);
                return "";
            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }

        }

        // After execution of product web service
        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR")) {
                    String data="";
                    // To display message after response from server
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    wordList = new ArrayList<HashMap<String, String>>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            map = new HashMap<String, String>();
                            map.put("Id", jsonArray.getJSONObject(i)
                                    .getString("Id"));
                            map.put("Code", jsonArray.getJSONObject(i)
                                    .getString("Code"));
                            map.put("VehicleNo", jsonArray.getJSONObject(i)
                                    .getString("VehicleNo"));
                            map.put("DispatchToName", jsonArray.getJSONObject(i)
                                    .getString("DispatchToName"));
                            map.put("TotalDispatch", jsonArray.getJSONObject(i)
                                    .getString("TotalDispatch"));
                            map.put("NoOfCrates", jsonArray.getJSONObject(i)
                                    .getString("NoOfCrates"));
                            wordList.add(map);
                        }
                        listSize = wordList.size();
                        if (listSize != 0) {
                            lvPendingDispatch.setAdapter(new ReportListAdapter(mContext, wordList));

                            ViewGroup.LayoutParams params = lvPendingDispatch.getLayoutParams();
                            lvPendingDispatch.setLayoutParams(params);
                            lvPendingDispatch.requestLayout();
                            tvEmpty.setVisibility(View.GONE);
                            tvDivider.setVisibility(View.VISIBLE);
                        } else {
                            lvPendingDispatch.setAdapter(null);
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvDivider.setVisibility(View.GONE);
                        }

                    } else {
                        common.showToast("There is no pending dispatch!", 2);
                    }

                } else {
                    if (result.contains("null") || result == "")
                        result = "Server not responding. Please try again later.";
                    common.showToast(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                common.showToast("Pending Dispatch Downloading failed: " + e.toString(),0);
            }
            Dialog.dismiss();
        }

        // To display message on screen within process
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Pending Dispatch..");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    private class ReportListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> _listData;
        String _type;
        private Context context2;

        public ReportListAdapter(Context context,
                                 ArrayList<HashMap<String, String>> listData) {
            this.context2 = context;
            inflater = LayoutInflater.from(context2);
            _listData = listData;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _listData.size();
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_pending_dispatch, null);
                holder = new viewHolder();
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.tvDispatchId = (TextView) convertView
                    .findViewById(R.id.tvDispatchId);
            holder.tvDispatchDetails = (TextView) convertView
                    .findViewById(R.id.tvDispatchDetails);
            holder.tvDispatchToDetails = (TextView) convertView
                    .findViewById(R.id.tvDispatchToDetails);

            final HashMap<String,String> itemData = _listData.get(position);
            holder.tvDispatchId.setText(itemData.get("Id"));
            holder.tvDispatchDetails.setText(itemData.get("VehicleNo")+" - "+itemData.get("Code"));
            holder.tvDispatchToDetails.setText(itemData.get("DispatchToName")+" - "+itemData.get("NoOfCrates")+" - "+itemData.get("TotalDispatch"));

            convertView.setBackgroundColor(Color.parseColor((position % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return convertView;
        }

    }
    //</editor-fold>
}
