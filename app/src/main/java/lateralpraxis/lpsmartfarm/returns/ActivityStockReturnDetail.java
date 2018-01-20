package lateralpraxis.lpsmartfarm.returns;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityStockReturnDetail extends Activity {


    //<editor-fold desc="Code For Class Declaration">
    final Context context = this;
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    private Intent intent;
    private final Context mContext = this;
    //</editor-fold>

    //<editor-fold desc="Code For Control Declaration">
    private TextView tvName,tvMobile,tvReturnTo;
    //</editor-fold>

    //<editor-fold desc="Code For Variable Declaration">
    private String responseJSON,userId,dispatchId;
    private int listSize = 0;
    private ArrayList<HashMap<String, String>> wordList = null;
    HashMap<String, String> map = null;
    //</editor-fold>

    //<editor-fold desc="Code to be executed on onCreate">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_return_detail);

        //<editor-fold desc="Code For Creating Instance of Class">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //<editor-fold desc="Code For Setting Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Code For Finding Text View">
        tvName = (TextView) findViewById(R.id.tvName);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvReturnTo = (TextView) findViewById(R.id.tvReturnTo);
        //</editor-fold>

        //<editor-fold desc="Code to get data from posted page">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            dispatchId = extras.getString("DispatchId");
        }
        //</editor-fold>

        if(common.isConnected())
        {
            AsyncPendingDispatchDetailWSCall task = new AsyncPendingDispatchDetailWSCall();
            task.execute();
        }
        else
        {
            Intent intent = new Intent(mContext, ActivityMainStockReturn.class);
            startActivity(intent);
            finish();
        }

    }
    //</editor-fold>

    //<editor-fold desc="Service for fetching Dispatch Detail by DIspatch Id from server">
    private class AsyncPendingDispatchDetailWSCall extends
            AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(
                ActivityStockReturnDetail.this);

        @Override
        protected String doInBackground(String... params) {
            try {

                // Call method of web service to Read Pending Dispatch Details for Stock Return
                responseJSON = "";
                responseJSON = common.invokeJSONWS(dispatchId, "id","GetDispatchDetailForStockReturn", common.url);
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
                    JSONArray jsonArray = new JSONArray(responseJSON.split("~")[0]);

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            tvName.setText(jsonArray.getJSONObject(i)
                                    .getString("DispatchTo"));
                            tvMobile.setText(jsonArray.getJSONObject(i)
                                    .getString("Mobile"));
                            tvReturnTo.setText(jsonArray.getJSONObject(i)
                                    .getString("DispatchFromName"));
                        }

                    } else {
                        common.showToast("There is no data in dispatch pending for return!");
                    }

                } else {
                    if (result.contains("null") || result == "")
                        result = "Server not responding. Please try again later.";
                    common.showToast(result,1);
                    Intent intent = new Intent(mContext, ActivityMainStockReturn.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                common.showToast("Dispatch Details Downloading failed: " + e.toString(),1);
                Intent intent = new Intent(mContext, ActivityMainStockReturn.class);
                startActivity(intent);
                finish();
            }
            Dialog.dismiss();
        }

        // To display message on screen within process
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Dispatch Payment Details..");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }
    //</editor-fold>
}
