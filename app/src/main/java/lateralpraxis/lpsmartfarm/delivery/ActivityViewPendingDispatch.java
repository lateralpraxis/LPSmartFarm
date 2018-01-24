package lateralpraxis.lpsmartfarm.delivery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;

public class ActivityViewPendingDispatch extends Activity {

    private final Context mContext = this;
    private TextView tvVehicle, tvDriver, tvMobile;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;

    private String userId;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending_dispatch);

        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Find controls by Id">
        tvVehicle = findViewById(R.id.tvVehicle);
        tvDriver = findViewById(R.id.tvDriver);
        tvMobile = findViewById(R.id.tvMobile);
        //</editor-fold>

        //<editor-fold desc="Get Extra values from Intent call">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {

        }
        //</editor-fold>
    }

    /**
     * Funtion to fetch and bind data to the controls
     *
     * @param dispatchUniqueId DispatchUniqueId
     */
    private void BindData() {
        dba.open();
        // TODO: get the dispatch data
        dba.close();
    }
}
