package lateralpraxis.lpsmartfarm.delivery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityViewDeliveryDetails extends Activity {

    private final Context mContext = this;

    private ListView lvPendingDispatchForDelivery;
    private ArrayList<HashMap<String, String>> dispatchData = null;
    private String userId, lang;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_delivery_details);

        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        HashMap<String, String> user = session.getLoginUserDetails();
        dispatchData = new ArrayList<>();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();
        //</editor-fold>

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        if (common.isConnected()) {
            BindData();

        }

    private void BindData() {
        dispatchData.clear();
        dba.open();
        dispatchData = dba.getPendingDispatchesForDelivery();
        dba.close();
        if (dispatchData.size() != 0) {
        } else {
        }
    }
}
