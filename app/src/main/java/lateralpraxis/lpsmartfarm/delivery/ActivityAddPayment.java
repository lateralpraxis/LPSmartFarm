package lateralpraxis.lpsmartfarm.delivery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityAddPayment extends Activity {

    private final Context mContext = this;

    private TextView tvName, tvMobile, tvBookedQty, tvAmount, tvAdvance, tvBalance;
    private Spinner spMode;
    private TextView tvPaymentAmount, tvPaymentRemarks;

    private DatabaseAdapter dba;
    private UserSessionManager session;
    private Common common;


    private String userId;
    private String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);


        //<editor-fold desc="Create database instance">
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        //</editor-fold>

        //interCroppingDetail = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);
        lang = session.getDefaultLang();

        //<editor-fold desc="Set Action Bar">
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>

        //<editor-fold desc="Find controls by Id">
        tvName = findViewById(R.id.tvName);
        tvMobile = findViewById(R.id.tvMobile);
        tvBookedQty = findViewById(R.id.tvBookedQty);
        tvAmount = findViewById(R.id.tvAmount);
        tvAdvance = findViewById(R.id.tvAdvance);
        tvBalance = findViewById(R.id.tvBalance);
        spMode = findViewById(R.id.spMode);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        tvPaymentRemarks = findViewById(R.id.tvPaymentRemarks);
        //</editor-fold>

        //<editor-fold desc="Get Extra values from Intent call">
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {

        }
        //</editor-fold>

        spMode.setAdapter(DataAdapter("paymentmode", ""));
    }

    /**
     * Funtion to fetch and bind data to the controls
     *
     * @param dispatchUniqueId DispatchUniqueId
     */
    private void BindData(String dispatchUniqueId) {
        dba.open();
        // TODO: get the dispatch data
        dba.close();
    }

    /**
     * Adapter to bind the Spinners
     *
     * @param masterType Table name
     * @param filter     Data filter
     * @return List of CustomType
     */
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }
}
