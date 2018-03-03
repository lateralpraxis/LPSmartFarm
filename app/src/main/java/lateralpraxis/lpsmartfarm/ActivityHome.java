package lateralpraxis.lpsmartfarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lateralpraxis.lpsmartfarm.confirmation.PendingJobCard;
import lateralpraxis.lpsmartfarm.confirmation.PendingNurseryJobCard;
import lateralpraxis.lpsmartfarm.delivery.ActivityViewDelivery;
import lateralpraxis.lpsmartfarm.farmer.ActivityFarmerList;
import lateralpraxis.lpsmartfarm.farmer.ActivityFarmerView;
import lateralpraxis.lpsmartfarm.farmer.FarmerCreateStep1;
import lateralpraxis.lpsmartfarm.farmreporting.FarmReportList;
import lateralpraxis.lpsmartfarm.nursery.Nursery;
import lateralpraxis.lpsmartfarm.nurseryreporting.NurseryReportList;
import lateralpraxis.lpsmartfarm.recommendation.RecommendationList;
import lateralpraxis.lpsmartfarm.recommendation.RecommendationNurseryList;
import lateralpraxis.lpsmartfarm.returns.ActivityMainStockReturn;
import lateralpraxis.lpsmartfarm.visits.RoutineVisitList;
import lateralpraxis.lpsmartfarm.visits.RoutineVisitNurseryList;

public class ActivityHome extends Activity {

    static final int ITEM_PER_ROW = 2;
    private static String curusrlang;
    /*---------Start of Class Declaration-----------------------------*/
    final Context context = this;
    Common common;
    List<Integer> views = Arrays.asList(
            R.layout.btn_farmer_view,
            R.layout.btn_sync);
    LinearLayout btnLayout;
    /*---------End of Class Declaration-----------------------------*/
    Button go, btn;
    TableLayout tl;
    TableRow tr;
    /*---------End of Variable Declaration-----------------------------*/
    private TextView tvHeader;
    private Intent intent;
    private UserSessionManager session;
    private DatabaseAdapter dba;
    /*---------Start of Variable Declaration-----------------------------*/
    private String userRole, password, userId, loginId, imei, responseJSON = "", sendJSon = "", syncFrom = "", syncByRole = "";
    private ArrayList<String> farmerData;
    /*---------Start of Control Declaration-----------------------------*/

    /*---------End of Control Declaration-----------------------------*/

    //Method to encrypt string using key
    @SuppressLint("TrulyRandom")
    private static String Encrypt(String text, String key)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        String result = Base64.encodeToString(results, Base64.DEFAULT);
        return result;
    }

    /*---------Start of Code on On Create Method-----------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    /*--------Start of Class Initialization-----------------------------*/
        //To create object of user session
        session = new UserSessionManager(getApplicationContext());

        //To create object of common class
        common = new Common(getApplicationContext());

        //To create object of database
        dba = new DatabaseAdapter(getApplicationContext());
        curusrlang = session.getDefaultLang();
        //Code to delete Blank Images
        dba.open();
        dba.deleteBlankImages();
        dba.close();
     /*--------End of Class Initialization-----------------------------*/

    /*--------Start of Code to find controls -----------------------------*/
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        go = (Button) findViewById(R.id.btnGo);
        tl = (TableLayout) findViewById(R.id.tlmainMenu);
        tl.setColumnStretchable(0, true);
        tl.setColumnStretchable(1, true);
    /*--------End of Code to find controls -----------------------------*/

     /*--------Start of Code to check User Session and set values in variables--------------------*/
        //To check user login session is exist or not
        if (session.checkLogin())
            finish();
        else {
            //To store required information of login user
            imei = common.getIMEI();
            final HashMap<String, String> user = session.getLoginUserDetails();

            userId = user.get(UserSessionManager.KEY_ID);
            loginId = user.get(UserSessionManager.KEY_CODE);
            password = user.get(UserSessionManager.KEY_PWD);
            userRole = user.get(UserSessionManager.KEY_USERROLES);


            tvHeader.setText("Welcome, " + user.get(UserSessionManager.KEY_FULLNAME));// + " [ " + Html.fromHtml(userRole.replace(",", ", ")) + " ]");

            try {
                dba.openR();

                String lang = session.getDefaultLang();
                Locale myLocale = new Locale(lang);
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);

                if (dba.IsSyncRequired()) {
                    if (common.isConnected()) {
                        String[] myTaskParams = {"masters"};
                        syncFrom = "masters";
                        //Calling async method for validating credentials
                        AsyncValidatePasswordWSCall task = new AsyncValidatePasswordWSCall();
                        task.execute(myTaskParams);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                common.showToast("Error: " + ex.getMessage());
            }
        }

        //Data Base Backup
        try {
            common.copyDBToSDCard("lpfarmart.db");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        dba.openR();
        userRole = dba.getAllRoles();

        syncByRole = (userRole.contains("Farmer")) ? "Farmer" : "";
        if (userRole.contains("Area Manager")
                || userRole.contains("Executive")
                || userRole.contains("FA Administrator")
                || userRole.contains("Management User")
                || userRole.contains("Mini Nursery Admin")
                || userRole.contains("Tele Caller"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_sync);
        else if (userRole.contains("Service Provider")
                && !userRole.contains("Mini Nursery Service Provider"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_confirm,
                    R.layout.btn_recommend,
                    R.layout.btn_visits,
                    R.layout.btn_farm_activity,
                    R.layout.btn_delivery,
                    R.layout.btn_sync);
        else if (userRole.contains("Mini Nursery Service Provider"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_visits,
                    R.layout.btn_farm_activity,
                    R.layout.btn_sync);
        else if (userRole.contains("Mini Nursery User")
                || userRole.contains("Nursery Supervisor"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_nursery,
                    R.layout.btn_farm_activity,
                    R.layout.btn_stock_return,
                    R.layout.btn_sync);
        else if (userRole.contains("Service Provider")
                && !userRole.contains("Mini Nursery Service Provider")
                && userRole.contains("Nursery Supervisor"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_nursery,
                    R.layout.btn_confirm,
                    R.layout.btn_recommend,
                    R.layout.btn_visits,
                    R.layout.btn_farm_activity,
                    R.layout.btn_sync);
        else if (userRole.contains("Agronomist"))
            views = Arrays.asList(R.layout.btn_farmer_create,
                    R.layout.btn_farmer_view,
                    R.layout.btn_confirm,
                    R.layout.btn_recommend,
                    R.layout.btn_visits,
                    R.layout.btn_sync);
        else if (userRole.contains("Farmer"))
            views = Arrays.asList(R.layout.btn_farmer_view,
                    R.layout.btn_farm_activity,
                    R.layout.btn_sync);

        go.performClick();

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGo:
                int index = 0;
                tl.removeAllViews();
                while (index < views.size()) {
                    tr = new TableRow(this);
                    TableLayout.LayoutParams trParams =
                            new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT);
                    trParams.setMargins(0, 8, 0, 0);
                    tr.setLayoutParams(trParams);
                    tr.setId(index + 1);
                    tr.setWeightSum(1);
                    for (int k = 0; k < ITEM_PER_ROW; k++) {
                        if (index < views.size()) {
                            btnLayout = createButton(views.get(index));
                            if (k == 0)
                                btnLayout.setPadding(8, 2, 4, 0);
                            else
                                btnLayout.setPadding(4, 2, 8, 0);
                            tr.addView(btnLayout);
                            index++;
                        }
                    }
                    tl.addView(tr);
                }
                break;
        }
    }

    private LinearLayout createButton(int resource) {
        btnLayout = (LinearLayout) View.inflate(this, resource, null);
        switch (resource) {
            case R.layout.btn_farmer_create:
                btn = (Button) btnLayout.findViewById(R.id.btnFarmerCreate);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, FarmerCreateStep1.class);
                        intent.putExtra("farmerUniqueId", "0");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.layout.btn_farmer_view:
                btn = (Button) btnLayout.findViewById(R.id.btnFarmerView);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dba.openR();
                        if (dba.IsSyncRequired()) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityHome.this);
                            // set title
                            alertDialogBuilder.setTitle(curusrlang.equalsIgnoreCase("en") ? "Confirmation" : "पुष्टीकरण");
                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(curusrlang.equalsIgnoreCase("en") ? "Some masters are missing, do you want to synchronize?" : "कुछ मास्टर्स उपलब्ध नहीं हैं, क्या आप सिंक्रनाइज़ करना चाहते हैं?")
                                    .setCancelable(false)
                                    .setPositiveButton(curusrlang.equalsIgnoreCase("en") ? "Yes" : "हाँ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            if (common.isConnected()) {
                                                String[] myTaskParams = {"masters"};
                                                syncFrom = "masters";
                                                //Calling async method for validating credentials
                                                AsyncValidatePasswordWSCall task = new AsyncValidatePasswordWSCall();
                                                task.execute(myTaskParams);
                                            }
                                        }
                                    })
                                    .setNegativeButton(curusrlang.equalsIgnoreCase("en") ? "No" : "नहीं", new DialogInterface.OnClickListener() {
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
                        } else {
                            if (userRole.contains("Farmer")) {
                                dba.openR();
                                farmerData = dba.getFarmerDetailsForFarmerLogin();
                                if (farmerData.size() > 0) {
                                    intent = new Intent(context, ActivityFarmerView.class);
                                    intent.putExtra("farmerUniqueId", farmerData.get(0));
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityHome.this);
                                    // set title
                                    alertDialogBuilder.setTitle(curusrlang.equalsIgnoreCase("en") ? "Confirmation" : "पुष्टीकरण");
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage(curusrlang.equalsIgnoreCase("en") ? "Some masters are missing, do you want to synchronize?" : "कुछ मास्टर्स उपलब्ध नहीं हैं, क्या आप सिंक्रनाइज़ करना चाहते हैं?")
                                            .setCancelable(false)
                                            .setPositiveButton(curusrlang.equalsIgnoreCase("en") ? "Yes" : "हाँ", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {

                                                    if (common.isConnected()) {
                                                        String[] myTaskParams = {"masters"};
                                                        syncFrom = "masters";
                                                        //Calling async method for validating credentials
                                                        AsyncValidatePasswordWSCall task = new AsyncValidatePasswordWSCall();
                                                        task.execute(myTaskParams);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(curusrlang.equalsIgnoreCase("en") ? "No" : "नहीं", new DialogInterface.OnClickListener() {
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
                                }
                            } else {
                                intent = new Intent(context, ActivityFarmerList.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
                break;
            case R.layout.btn_sync:
                btn = (Button) btnLayout.findViewById(R.id.btnSync);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityHome.this);
                        // set title
                        alertDialogBuilder.setTitle(curusrlang.equalsIgnoreCase("en") ? "Confirmation" : "पुष्टीकरण");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(curusrlang.equalsIgnoreCase("en") ? "Are you sure, you want to Synchronize?" : "क्या आप वाकई सिंक्रनाइज़ करना चाहते हैं?")
                                .setCancelable(false)
                                .setPositiveButton(curusrlang.equalsIgnoreCase("en") ? "Yes" : "हाँ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (common.isConnected()) {
                                            syncFrom = "transactions";
                                            AsyncNewFarmerWSCall task = new AsyncNewFarmerWSCall();
                                            task.execute();
                                        }

                                    }
                                })
                                .setNegativeButton(curusrlang.equalsIgnoreCase("en") ? "No" : "नहीं", new DialogInterface.OnClickListener() {
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
                    }
                });
                break;
            case R.layout.btn_confirm:
                btn = (Button) btnLayout.findViewById(R.id.btnJobCardConfirmation);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //*********************Start of Code to Check User Role AND Navigate to Proper Intent********************//*

                        if ((userRole.contains("Service Provider") && userRole.contains("Agronomist"))) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityHome.this);
                            builderSingle.setTitle("Select Job Card Confirmation For");
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityHome.this, android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.add("Farm Block");
                            arrayAdapter.add("Nursery");
                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.setAdapter(
                                    arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            if (strName.equals("Farm Block")) {
                                                intent = new Intent(context, PendingJobCard.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (strName.equals("Nursery")) {
                                                intent = new Intent(context, PendingNurseryJobCard.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                common.showToast("Please select Appropriate option.");
                                            }
                                        }
                                    });
                            builderSingle.show();
                        } else if (userRole.contains("Service Provider")) {
                            intent = new Intent(context, PendingJobCard.class);
                            startActivity(intent);
                            finish();
                        } else if (userRole.contains("Agronomist")) {
                            intent = new Intent(context, PendingNurseryJobCard.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
            case R.layout.btn_recommend:
                btn = (Button) btnLayout.findViewById(R.id.btnRecommendation);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //*********************Start of Code to Check User Role AND Navigate to Proper Intent********************//*

                        if ((userRole.contains("Agronomist"))) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityHome.this);
                            builderSingle.setTitle("Select Recommendation For");

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityHome.this, android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.add("Farm Block");
                            arrayAdapter.add("Nursery");
                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.setAdapter(
                                    arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            if (strName.equals("Farm Block")) {
                                                intent = new Intent(context, RecommendationList.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (strName.equals("Nursery")) {
                                                intent = new Intent(context, RecommendationNurseryList.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                common.showToast("Please select Appropriate option.");
                                            }
                                        }
                                    });
                            builderSingle.show();
                        } else if (userRole.contains("Service Provider")) {
                            intent = new Intent(context, RecommendationList.class);
                            startActivity(intent);
                            finish();
                        }
                        //*********************End of Code to Check User Role AND Navigate to Proper Intent********************//*
                    }
                });
                break;
            case R.layout.btn_visits:
                btn = (Button) btnLayout.findViewById(R.id.btnVisitReports);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //*********************Start of Code to Check User Role AND Navigate to Proper Intent********************//*

                        if ((userRole.contains("Service Provider") || userRole.contains("Mini Nursery Service Provider")) && (userRole.contains("Agronomist"))) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityHome.this);
                            builderSingle.setTitle("Select Routine Visit For");

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityHome.this, android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.add("Farm Block");
                            arrayAdapter.add("Nursery");
                            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.setAdapter(
                                    arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            if (strName.equals("Farm Block")) {
                                                intent = new Intent(context, RoutineVisitList.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (strName.equals("Nursery")) {
                                                intent = new Intent(context, RoutineVisitNurseryList.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                common.showToast("Please select Appropriate option.");
                                            }
                                        }
                                    });
                            builderSingle.show();
                        } else if (userRole.contains("Service Provider") || userRole.contains("Mini Nursery Service Provider")) {
                            intent = new Intent(context, RoutineVisitList.class);
                            startActivity(intent);
                            finish();
                        } else if (userRole.contains("Agronomist")) {
                            intent = new Intent(context, RoutineVisitNurseryList.class);
                            startActivity(intent);
                            finish();
                        }
                        //*********************End of Code to Check User Role AND Navigate to Proper Intent********************//*
                    }
                });
                break;
            case R.layout.btn_farm_activity:
                btn = (Button) btnLayout.findViewById(R.id.btnFarmActivity);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //*********************Start of Code to Check User Role AND Navigate to Proper Intent********************//*

                        if (userRole.contains("Nursery Supervisor") && userRole.contains("Service Provider")) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityHome.this);
                            builderSingle.setTitle("Select Reporting For");

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    ActivityHome.this,
                                    android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.add("Farm Block Reporting");
                            arrayAdapter.add("Nursery Reporting");
                            builderSingle.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builderSingle.setAdapter(
                                    arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            if (strName.equals("Farm Block Reporting")) {
                                                intent = new Intent(context, FarmReportList.class);
                                                intent.putExtra("EntryFor", "FarmJobCard");
                                                startActivity(intent);
                                                finish();
                                            } else if (strName.equals("Nursery Reporting")) {
                                                intent = new Intent(context, NurseryReportList.class);
                                                intent.putExtra("EntryFor", "NurseryJobCard");
                                                startActivity(intent);
                                                finish();
                                            } else {

                                                common.showToast("Please select Appropriate option.");

                                            }
                                        }
                                    });
                            builderSingle.show();
                        } else if (userRole.contains("Service Provider") || userRole.contains("Farmer")) {
                            intent = new Intent(context, FarmReportList.class);
                            intent.putExtra("EntryFor", "FarmJobCard");
                            startActivity(intent);
                            finish();
                        } else {
                            intent = new Intent(context, NurseryReportList.class);
                            intent.putExtra("EntryFor", "NurseryJobCard");
                            startActivity(intent);
                            finish();
                        }
                        //*********************End of Code to Check User Role AND Navigate to Proper Intent********************//*
                    }
                });
                break;
            case R.layout.btn_stock_return:
                btn = (Button) btnLayout.findViewById(R.id.btnStockReturn);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ActivityMainStockReturn.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.layout.btn_nursery:
                btn = (Button) btnLayout.findViewById(R.id.btnNursery);
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, Nursery.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.layout.btn_delivery:
                btn = btnLayout.findViewById(R.id.btnDelivery);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(context, ActivityViewDelivery.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.layout.btn_blank:
                break;

            default:
                break;
        }

        return btnLayout;
    }
    /*---------End of Code on On Create Method-----------------------------*/

    // When press back button go to home screen
    @Override
    public void onBackPressed() {
        common.BackPressed(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (userRole.contains("Farmer"))
            inflater.inflate(R.menu.menu_farmer, menu);
        else
            inflater.inflate(R.menu.basemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set title
                alertDialogBuilder.setTitle(curusrlang.equalsIgnoreCase("en") ? "Confirmation" : "पुष्टीकरण");
                // set dialog message
                alertDialogBuilder
                        .setMessage(curusrlang.equalsIgnoreCase("en") ? "Are you sure, want to Log Out ?" : "क्या आप वाकई लॉग आउट करना चाहते हैं?")
                        .setCancelable(false)
                        .setPositiveButton(curusrlang.equalsIgnoreCase("en") ? "Yes" : "हाँ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (common.isConnected()) {
                                    dba.open();
                                    if (dba.IslogoutAllowed()) {
                                        dba.deleteAllData();
                                        dba.close();
                                        AsyncLogOutWSCall task = new AsyncLogOutWSCall();
                                        task.execute();
                                    } else {
                                        common.showToast(curusrlang.equalsIgnoreCase("en") ? "Unable to logout as data is pending for synchronize." : "लॉग आउट करने में असमर्थ क्यूंकि डेटा सिंक्रनाइज़ करने के लिए लंबित है।");
                                    }
                                } else {
                                    common.showToast(curusrlang.equalsIgnoreCase("en") ? "Unable to connect to Internet !" : "इंटरनेट से जुड़ने में असमर्थ!");
                                }
                            }
                        })
                        .setNegativeButton(curusrlang.equalsIgnoreCase("en") ? "No" : "नहीं", new DialogInterface.OnClickListener() {
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
            case R.id.action_changelanguage: {
                ArrayList<String> myLanguagelist = new ArrayList<String>();
                myLanguagelist.clear();
                final HashMap<String, String> user = session.getLoginUserDetails();
                String[] items = user.get(UserSessionManager.KEY_OPTLANG)
                        .split(",");

                for (String st : items) {
                    if (st.equals("English")) {
                        myLanguagelist.add(getResources().getString(
                                R.string.lang_eng));
                    } else if (st.equals("Hindi")) {
                        myLanguagelist.add(getResources().getString(
                                R.string.lang_hindi));
                    }
                }

                final AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        ActivityHome.this);
                builderSingle.setTitle(getResources().getString(
                        R.string.change_language));

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        ActivityHome.this,
                        android.R.layout.select_dialog_singlechoice, myLanguagelist);

                // radio button highlight
                int selected = 0;
                String lang = session.getDefaultLang();

                if (lang.equalsIgnoreCase("en")) {
                    selected = 0;

                } else if (lang.equalsIgnoreCase("hi")) {
                    selected = 1;

                }

                builderSingle.setSingleChoiceItems(arrayAdapter, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {

                                String setLang = arrayAdapter.getItem(item);

                                String langCode = "";
                                if (setLang.equalsIgnoreCase(getResources()
                                        .getString(R.string.lang_eng))) {
                                    langCode = "en";

                                } else if (setLang.equalsIgnoreCase(getResources()
                                        .getString(R.string.lang_hindi))) {
                                    langCode = "hi";

                                }

                                session.updatePrefLanguage(langCode);
                                dialog.cancel();

                                Locale myLocale = new Locale(langCode);
                                Resources res = getResources();
                                DisplayMetrics dm = res.getDisplayMetrics();
                                Configuration conf = res.getConfiguration();
                                conf.locale = myLocale;
                                res.updateConfiguration(conf, dm);
                                common.showToast("You have selected " + setLang);
                                Intent refresh = new Intent(context,
                                        ActivityHome.class);
                                startActivity(refresh);

                            }
                        });
                builderSingle.show();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Method to display change password dialog
    private void showChangePassWindow(final String source, final String resp) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_password_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(promptsView);
        //Code to find controls in dialog
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.etPassword);

        final CheckBox ckShowPass = (CheckBox) promptsView
                .findViewById(R.id.ckShowPass);

        final TextView tvMsg = (TextView) promptsView
                .findViewById(R.id.tvMsg);

        tvMsg.setText(resp);

        ckShowPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start, end;

                if (!isChecked) {
                    start = userInput.getSelectionStart();
                    end = userInput.getSelectionEnd();
                    userInput.setTransformationMethod(new PasswordTransformationMethod());
                    ;
                    userInput.setSelection(start, end);
                } else {
                    start = userInput.getSelectionStart();
                    end = userInput.getSelectionEnd();
                    userInput.setTransformationMethod(null);
                    userInput.setSelection(start, end);
                }

            }
        });


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String password = userInput.getText().toString().trim();
                                if (password.length() > 0) {
                                    //Code to update password in session and call validate Async Method
                                    session.updatePassword(password);

                                    String[] myTaskParams = {source};
                                    AsyncValidatePasswordWSCall task = new AsyncValidatePasswordWSCall();
                                    task.execute(myTaskParams);
                                } else {
                                    //Display message if password is not enetered
                                    common.showToast("Password is mandatory");
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    //Method to delete files from Directory
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    //Method to compress, create and return byte array for document
    private String getByteArrayFromImage(Bitmap bitmap) throws FileNotFoundException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] data = bos.toByteArray();
        String file = Base64.encodeToString(data, Base64.DEFAULT);

        return file;
    }

    //To make web service class to post data of New Farmers
    private class AsyncNewFarmerWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get New Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getUnSyncNewFarmer();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post New Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmerUniqueId", insp.get("FarmerUniqueId"));
                        jsonins.put("EducationLevelId", insp.get("EducationLevelId"));
                        jsonins.put("FarmerTypeId", insp.get("FarmerTypeId"));
                        jsonins.put("Salutation", insp.get("Salutation"));
                        jsonins.put("FirstName", insp.get("FirstName"));
                        jsonins.put("MiddleName", insp.get("MiddleName"));
                        jsonins.put("LastName", insp.get("LastName"));
                        jsonins.put("FatherSalutation", insp.get("FatherSalutation"));
                        jsonins.put("FatherFirstName", insp.get("FatherFirstName"));
                        jsonins.put("FatherMiddleName", insp.get("FatherMiddleName"));
                        jsonins.put("FatherLastName", insp.get("FatherLastName"));
                        jsonins.put("EmailId", insp.get("EmailId"));
                        jsonins.put("Mobile", insp.get("Mobile"));
                        jsonins.put("Mobile1", insp.get("Mobile1"));
                        jsonins.put("Mobile2", insp.get("Mobile2"));
                        jsonins.put("BirthDate", insp.get("BirthDate"));
                        jsonins.put("Gender", insp.get("Gender"));
                        jsonins.put("TotalAcreage", insp.get("TotalAcreage"));
                        jsonins.put("LanguageId", insp.get("LanguageId"));
                        jsonins.put("CreateBy", userId);
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("Street1", insp.get("Street1"));
                        jsonins.put("Street2", insp.get("Street2"));
                        jsonins.put("StateId", insp.get("StateId"));
                        jsonins.put("DistrictId", insp.get("DistrictId"));
                        jsonins.put("BlockId", insp.get("BlockId"));
                        jsonins.put("PanchayatId", insp.get("PanchayatId"));
                        jsonins.put("VillageId", insp.get("VillageId"));
                        jsonins.put("CityId", insp.get("CityId"));
                        jsonins.put("PinCodeId", insp.get("PinCodeId"));
                        jsonins.put("AddressType", insp.get("AddressType"));
                        jsonins.put("SoilTypeId", insp.get("SoilTypeId"));
                        jsonins.put("IrrigationSystemId", insp.get("IrrigationSystemId"));
                        jsonins.put("RiverId", insp.get("RiverId"));
                        jsonins.put("DamId", insp.get("DamId"));
                        jsonins.put("WaterSourceId", insp.get("WaterSourceId"));
                        jsonins.put("ElectricitySourceId", insp.get("ElectricitySourceId"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        jsonins.put("PhotoFileName", insp.get("PhotoFileName") == null ? "" : insp.get("PhotoFileName").substring(insp.get("PhotoFileName").lastIndexOf("/") + 1));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);

                    JSONObject jsonBlocks = new JSONObject();
                    //To get Assigned Block details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getUnSyncBlockAssignment();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Assigned Block details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsondet.put("BlockId", insd.get("BlockId"));
                            arraydet.put(jsondet);
                        }
                        jsonBlocks.put("Blocks", arraydet);
                    }

                    sendJSon = jsonFarmer + "~" + jsonBlocks;

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertNewFarmer", common.url);
                } else {
                    return "No prospective farmer pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create New Farmer On Portal
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_NewFarmerIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of get Farmer Code for Farmers
                        AsyncFarmerWithCodeWSCall task = new AsyncFarmerWithCodeWSCall();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);

                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting Prospective Farmers...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of Farmers
    private class AsyncFarmerWithCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getUnSyncFarmers();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmerUniqueId", insp.get("FarmerUniqueId"));
                        jsonins.put("EducationLevelId", insp.get("EducationLevelId"));
                        jsonins.put("FarmerTypeId", insp.get("FarmerTypeId"));
                        jsonins.put("EmailId", insp.get("EmailId"));
                        jsonins.put("Mobile", insp.get("Mobile"));
                        jsonins.put("Mobile1", insp.get("Mobile1"));
                        jsonins.put("Mobile2", insp.get("Mobile2"));
                        jsonins.put("TotalAcreage", insp.get("TotalAcreage"));
                        jsonins.put("BankAccountNo", insp.get("BankAccountNo"));
                        jsonins.put("IFSCCode", insp.get("IFSCCode"));
                        jsonins.put("BankFileName", (insp.get("BankFileName") == null || insp.get("BankFileName") == "") ? "" : insp.get("BankFileName").substring(insp.get("BankFileName").lastIndexOf("/") + 1));
                        jsonins.put("FSSAINumber", insp.get("FSSAINumber"));
                        jsonins.put("RegistrationDate", insp.get("RegistrationDate"));
                        jsonins.put("ExpiryDate", insp.get("ExpiryDate"));
                        jsonins.put("FSSAIFileName", (insp.get("FSSAIFileName") == null || insp.get("FSSAIFileName") == "") ? "" : insp.get("FSSAIFileName").substring(insp.get("FSSAIFileName").lastIndexOf("/") + 1));
                        jsonins.put("CreateBy", userId);
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        jsonins.put("PhotoFileName", insp.get("PhotoFileName") == null ? "" : insp.get("PhotoFileName").substring(insp.get("PhotoFileName").lastIndexOf("/") + 1));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("IsLoanNa", insp.get("IsLoanNA"));
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);

                    JSONObject jsonBlocks = new JSONObject();
                    //To get Assigned Block details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getUnSyncBlockAssignmentOld();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Assigned Block Details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsondet.put("BlockId", insd.get("BlockId"));
                            arraydet.put(jsondet);
                        }
                        jsonBlocks.put("Blocks", arraydet);
                    }
                    //JSON for Farmer Family Details

                    JSONObject jsonFamily = new JSONObject();
                    //To get UnSync Family Details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> infdet = dba.getUnSyncFamilyDetails();
                    dba.close();
                    if (infdet != null && infdet.size() > 0) {
                        //To make json string to post Family Details
                        JSONArray arrayfdet = new JSONArray();
                        for (HashMap<String, String> insd : infdet) {
                            JSONObject jsonfdet = new JSONObject();
                            jsonfdet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsonfdet.put("MemberName", insd.get("MemberName"));
                            jsonfdet.put("Gender", insd.get("Gender"));
                            jsonfdet.put("BirthDate", insd.get("BirthDate"));
                            jsonfdet.put("RelationShipId", insd.get("RelationShipId"));
                            jsonfdet.put("IsNominee", insd.get("IsNominee"));
                            jsonfdet.put("NomineePercentage", insd.get("NomineePercentage"));

                            arrayfdet.put(jsonfdet);
                        }
                        jsonFamily.put("Family", arrayfdet);
                    }

                    //JSON for Farmer Asset Details

                    JSONObject jsonAsset = new JSONObject();
                    //To get UnSync Asset Details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insadet = dba.getUnSyncAssetDetails();
                    dba.close();
                    if (insadet != null && insadet.size() > 0) {
                        //To make json string to post Asset Details
                        JSONArray arrayadet = new JSONArray();
                        for (HashMap<String, String> insd : insadet) {
                            JSONObject jsonadet = new JSONObject();
                            jsonadet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsonadet.put("FarmAssetsId", insd.get("FarmAssetsId"));
                            jsonadet.put("FarmAssetsNos", insd.get("FarmAssetsNo"));

                            arrayadet.put(jsonadet);
                        }
                        jsonAsset.put("Asset", arrayadet);
                    }


                    //JSON for Farmer POAPOI Details

                    JSONObject jsonPOAPOI = new JSONObject();
                    //To get UnSync POAPOI Details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> inspdet = dba.getUnSyncProofDetails();
                    dba.close();
                    if (inspdet != null && inspdet.size() > 0) {
                        //To make json string to post Asset Details
                        JSONArray arraypdet = new JSONArray();
                        for (HashMap<String, String> insd : inspdet) {
                            JSONObject jsonpdet = new JSONObject();
                            jsonpdet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsonpdet.put("UniqueId", insd.get("UniqueId"));
                            jsonpdet.put("POAPOIId", insd.get("POAPOIId"));
                            jsonpdet.put("POAPOINumber", insd.get("DocumentNo"));
                            jsonpdet.put("FileName", insd.get("FileName"));

                            arraypdet.put(jsonpdet);
                        }
                        jsonPOAPOI.put("PAOPOI", arraypdet);
                    }

                    //JSON for Farmer Loan Details

                    JSONObject jsonLoan = new JSONObject();
                    //To get UnSync Farmer Loan Details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insldet = dba.getUnSyncLoanDetails();
                    dba.close();
                    if (insldet != null && insldet.size() > 0) {
                        //To make json string to post Loan Details
                        JSONArray arrayldet = new JSONArray();
                        for (HashMap<String, String> insd : insldet) {
                            JSONObject jsonldet = new JSONObject();
                            jsonldet.put("FarmerUniqueId", insd.get("FarmerUniqueId"));
                            jsonldet.put("LoanSourceId", insd.get("LoanSourceId"));
                            jsonldet.put("LoanTypeId", insd.get("LoanTypeId"));
                            jsonldet.put("ROIPercentage", insd.get("ROIPercentage"));
                            jsonldet.put("LoanAmount", insd.get("LoanAmount"));
                            jsonldet.put("BalanceAmount", insd.get("BalanceAmount"));
                            jsonldet.put("Tenure", insd.get("Tenure"));

                            arrayldet.put(jsonldet);
                        }
                        jsonLoan.put("Loan", arrayldet);
                    }

                    sendJSon = jsonFarmer + "~" + jsonBlocks + "~" + jsonFamily + "~" + jsonAsset + "~" + jsonPOAPOI + "~" + jsonLoan;

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateFarmer", common.url);
                } else {
                    return "No farmer pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Update Farmer
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_FarmerIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method To Sync New Farmers
                        AsyncNewFarmBlock task = new AsyncNewFarmBlock();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);

                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting Farmers...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of New Farm Block
    private class AsyncNewFarmBlock extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmBlock = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getNewFarmBlocks();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("FarmBlockUniqueId", insp.get("FarmBlockUniqueId"));
                        jsonins.put("FarmerUniqueId", insp.get("FarmerUniqueId"));
                        jsonins.put("LandTypeId", insp.get("LandTypeId"));
                        jsonins.put("FPOId", insp.get("FPOId"));
                        jsonins.put("KhataNo", insp.get("KhataNo"));
                        jsonins.put("KhasraNo", insp.get("KhasraNo"));
                        jsonins.put("ContractDate", insp.get("ContractDate"));
                        jsonins.put("Acerage", insp.get("Acerage"));
                        jsonins.put("SoilTypeId", insp.get("SoilTypeId"));
                        jsonins.put("ElevationMSL", insp.get("ElevationMSL"));
                        jsonins.put("SoliPH", insp.get("SoliPH"));
                        jsonins.put("Nitrogen", insp.get("Nitrogen"));
                        jsonins.put("Potash", insp.get("Potash"));
                        jsonins.put("Phosphorus", insp.get("Phosphorus"));
                        jsonins.put("OrganicCarbonPerc", insp.get("OrganicCarbonPerc"));
                        jsonins.put("Magnesium", insp.get("Magnesium"));
                        jsonins.put("Calcium", insp.get("Calcium"));
                        jsonins.put("ExistingUseId", insp.get("ExistingUseId"));
                        jsonins.put("CommunityUseId", insp.get("CommunityUseId"));
                        jsonins.put("ExistingHazardId", insp.get("ExistingHazardId"));
                        jsonins.put("RiverId", insp.get("RiverId"));
                        jsonins.put("DamId", insp.get("DamId"));
                        jsonins.put("IrrigationId", insp.get("IrrigationId"));
                        jsonins.put("OverheadTransmission", insp.get("OverheadTransmission"));
                        jsonins.put("LegalDisputeId", insp.get("LegalDisputeId"));
                        jsonins.put("SourceWaterId", insp.get("SourceWaterId"));
                        jsonins.put("ElectricitySourceId", insp.get("ElectricitySourceId"));
                        jsonins.put("DripperSpacing", insp.get("DripperSpacing"));
                        jsonins.put("DischargeRate", insp.get("DischargeRate"));
                        jsonins.put("CreateBy", userId);
                        jsonins.put("AndroidDate", insp.get("AndroidDate"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("Street1", insp.get("Street1"));
                        jsonins.put("Street2", insp.get("Street2"));
                        jsonins.put("StateId", insp.get("StateId"));
                        jsonins.put("DistrictId", insp.get("DistrictId"));
                        jsonins.put("BlockId", insp.get("BlockId"));
                        jsonins.put("PanchayatId", insp.get("PanchayatId"));
                        jsonins.put("VillageId", insp.get("VillageId"));
                        jsonins.put("PinCodeId", insp.get("PinCodeId"));
                        jsonins.put("AddressType", insp.get("AddressType"));
                        jsonins.put("LandCharacteristic", insp.get("LandCharacteristic"));
                        jsonins.put("LandIssue", insp.get("LandIssue"));
                        jsonins.put("OwnerName", insp.get("OwnerName"));
                        jsonins.put("OwnerMobile", insp.get("OwnerMobile"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonFarmBlock.put("Master", array);

                    JSONObject jsonPattern = new JSONObject();
                    //To get New Cropping Pattern details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getNewCroppingPattern();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Cropping Pattern Details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmBlockUniqueId", insd.get("FarmBlockUniqueId"));
                            jsondet.put("CropVarietyId", insd.get("CropVarietyId"));
                            jsondet.put("Acreage", insd.get("Acreage"));
                            jsondet.put("SeasonId", insd.get("SeasonId"));
                            arraydet.put(jsondet);
                        }
                        jsonPattern.put("CPattern", arraydet);
                    }

                    JSONObject jsonCoord = new JSONObject();
                    //To get New Farm Block Coordinates details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insCoord = dba.getNewCoordinates();
                    dba.close();
                    if (insCoord != null && insCoord.size() > 0) {
                        //To make json string to post Farm Block Coordinates
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insCoord) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmBlockUniqueId", insd.get("FarmBlockUniqueId"));
                            jsondet.put("Latitude", insd.get("Latitude"));
                            jsondet.put("Longitude", insd.get("Longitude"));
                            jsondet.put("Accuracy", insd.get("Accuracy"));
                            jsondet.put("OrderId", insd.get("OrderId"));
                            arraydet.put(jsondet);
                        }
                        jsonCoord.put("Coord", arraydet);
                    }


                    sendJSon = jsonFarmBlock + "~" + jsonPattern + "~" + jsonCoord;

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertNewFarmBlock", common.url);
                } else {
                    return "No farm block pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create New Farm Block
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_NewFarBlockIsSync();
                        dba.close();
                    }

                    if (common.isConnected()) {
                        //call method to Sync Edited Farm Block With Codes
                        AsyncFarmBlockWIthCode task = new AsyncFarmBlockWIthCode();
                        task.execute();
                    }
                   /* if (common.isConnected()) {
                        //call method of get Farmer Code for Farmers
                        AsyncFarmerCodeWSCall task = new AsyncFarmerCodeWSCall();
                        task.execute();
                    }*/
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting New Farm Blocks...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of Farm Block WIth Code
    private class AsyncFarmBlockWIthCode extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmBlock = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getFarmBlocksWithCode();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("FarmBlockUniqueId", insp.get("FarmBlockUniqueId"));
                        jsonins.put("KhataNo", insp.get("KhataNo"));
                        jsonins.put("KhasraNo", insp.get("KhasraNo"));
                        jsonins.put("Acerage", insp.get("Acerage"));
                        jsonins.put("SoilTypeId", insp.get("SoilTypeId"));
                        jsonins.put("ElevationMSL", insp.get("ElevationMSL"));
                        jsonins.put("SoliPH", insp.get("SoliPH"));
                        jsonins.put("Nitrogen", insp.get("Nitrogen"));
                        jsonins.put("Potash", insp.get("Potash"));
                        jsonins.put("Phosphorus", insp.get("Phosphorus"));
                        jsonins.put("OrganicCarbonPerc", insp.get("OrganicCarbonPerc"));
                        jsonins.put("Magnesium", insp.get("Magnesium"));
                        jsonins.put("Calcium", insp.get("Calcium"));
                        jsonins.put("ExistingUseId", insp.get("ExistingUseId"));
                        jsonins.put("CommunityUseId", insp.get("CommunityUseId"));
                        jsonins.put("ExistingHazardId", insp.get("ExistingHazardId"));
                        jsonins.put("RiverId", insp.get("RiverId"));
                        jsonins.put("DamId", insp.get("DamId"));
                        jsonins.put("IrrigationId", insp.get("IrrigationId"));
                        jsonins.put("OverheadTransmission", insp.get("OverheadTransmission"));
                        jsonins.put("LegalDisputeId", insp.get("LegalDisputeId"));
                        jsonins.put("SourceWaterId", insp.get("SourceWaterId"));
                        jsonins.put("ElectricitySourceId", insp.get("ElectricitySourceId"));
                        jsonins.put("DripperSpacing", insp.get("DripperSpacing"));
                        jsonins.put("DischargeRate", insp.get("DischargeRate"));
                        jsonins.put("CreateBy", userId);
                        jsonins.put("AndroidDate", insp.get("AndroidDate"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("LandCharacteristic", insp.get("LandCharacteristic"));
                        jsonins.put("LandIssue", insp.get("LandIssue"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonFarmBlock.put("Master", array);

                    JSONObject jsonPattern = new JSONObject();
                    //To get New Cropping Pattern details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getFarmBlockCroppingPattern();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Cropping Pattern Details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmBlockUniqueId", insd.get("FarmBlockUniqueId"));
                            jsondet.put("CropVarietyId", insd.get("CropVarietyId"));
                            jsondet.put("Acreage", insd.get("Acreage"));
                            jsondet.put("SeasonId", insd.get("SeasonId"));
                            arraydet.put(jsondet);
                        }
                        jsonPattern.put("CPattern", arraydet);
                    }

                    JSONObject jsonCoord = new JSONObject();
                    //To get New Farm Block Coordinates details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insCoord = dba.getFarmBlockCoordinates();
                    dba.close();
                    if (insCoord != null && insCoord.size() > 0) {
                        //To make json string to post Farm Block Coordinates
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insCoord) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("FarmBlockUniqueId", insd.get("FarmBlockUniqueId"));
                            jsondet.put("Latitude", insd.get("Latitude"));
                            jsondet.put("Longitude", insd.get("Longitude"));
                            jsondet.put("Accuracy", insd.get("Accuracy"));
                            jsondet.put("OrderId", insd.get("OrderId"));
                            arraydet.put(jsondet);
                        }
                        jsonCoord.put("Coord", arraydet);
                    }


                    sendJSon = jsonFarmBlock + "~" + jsonPattern + "~" + jsonCoord;

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateFarmBlock", common.url);
                } else {
                    return "No farm block pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to update Farm Block WIth Code
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_ExistingFarBlockIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of get Unsync Plantation
                        AsyncPlantation task = new AsyncPlantation();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Farm Blocks...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of Plantation
    private class AsyncPlantation extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {
                responseJSON = "";

                JSONObject jsonPlantation = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getPlantationForSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Plantation
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("FarmBlockUniqueId", insp.get("FarmBlockUniqueId"));
                        jsonins.put("ZoneId", insp.get("ZoneId"));
                        jsonins.put("CropVarietyId", insp.get("CropVarietyId"));
                        jsonins.put("MonthAgeId", insp.get("MonthAgeId"));
                        jsonins.put("Acerage", insp.get("Acreage"));
                        jsonins.put("PlantationDate", insp.get("PlantationDate"));
                        jsonins.put("PlantTypeId", insp.get("PlantTypeId"));
                        jsonins.put("PlantingSystemId", insp.get("PlantingSystemId"));
                        jsonins.put("PlantRow", insp.get("PlantRow"));
                        jsonins.put("PlantColumn", insp.get("PlantColumn"));
                        jsonins.put("Balance", insp.get("Balance"));
                        jsonins.put("TotalPlant", insp.get("TotalPlant"));
                        jsonins.put("CreateBy", insp.get("CreateBy"));
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonPlantation.put("Master", array);


                    sendJSon = jsonPlantation.toString();

                    //To invoke json web service to create New Plantation On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertPlantation", common.url);
                } else {
                    return "No farm block pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Update Farm Block WIth Code
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_ExistingPlantationIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of get Unsync Intercropping
                        AsyncInterCropping task = new AsyncInterCropping();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Plantation...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of Intercropping
    private class AsyncInterCropping extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonInterCropping = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getInterCroppingForSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Intercropping
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("InterCroppingUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CropVarietyId", insp.get("CropVarietyId"));
                        jsonins.put("Acreage", insp.get("Acreage"));
                        jsonins.put("SeasonId", insp.get("SeasonId"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("CreateBy", insp.get("CreateBy"));
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonInterCropping.put("Master", array);


                    sendJSon = jsonInterCropping.toString();

                    //To invoke json web service to create New Plantation On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertInterCropping", common.url);
                } else {
                    return "No farm block pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create InterCropping
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_ExistingIntercroppingIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method to Sync Edited visit report With Codes
                        AsyncVisitReport task = new AsyncVisitReport();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Inter Cropping...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of New visit report
    private class AsyncVisitReport extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {
                responseJSON = "";

                JSONObject jsonMaster = new JSONObject();
                dba.open();
                //to get visit report from database
                ArrayList<HashMap<String, String>> insmast = dba.GetVisitReportSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post visit report
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmBlockNurseryType", insp.get("FarmBlockNurseryType"));
                        jsonins.put("FarmBlockNurseryId", insp.get("FarmBlockNurseryId"));
                        jsonins.put("ZoneId", insp.get("ZoneId"));
                        jsonins.put("PlantationUniqueId", insp.get("PlantationId"));
                        jsonins.put("VisitDate", insp.get("VisitDate"));
                        jsonins.put("PlantHeight", insp.get("PlantHeight"));
                        jsonins.put("PlantStatusId", insp.get("PlantStatusId"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("UserId", insp.get("UserId"));
                        jsonins.put("IpAddress", common.getDeviceIPAddress(true));
                        jsonins.put("MachineName", common.getIMEI());
                        jsonins.put("VisitUniqueId", insp.get("VisitUniqueId"));
                        jsonins.put("NoOfDays", insp.get("NoOfDays"));
                        array.put(jsonins);
                    }
                    jsonMaster.put("Master", array);

                    JSONObject jsonDetail = new JSONObject();
                    //To get New visit report defect details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.GetVisitReportDetailSync();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post visit report defect details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("VisitUniqueId", insd.get("VisitUniqueId"));
                            jsondet.put("UniqueId", insd.get("UniqueId"));
                            jsondet.put("DefectId", insd.get("DefectId"));
                            jsondet.put("Remarks", insd.get("Remarks"));
                            jsondet.put("FileName", insd.get("FileName"));
                            arraydet.put(jsondet);
                        }
                        jsonDetail.put("Detail", arraydet);
                    }

                    sendJSon = jsonMaster + "~" + jsonDetail;

                    //To invoke json web service to create New visit report on portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertVisitReport", common.url);
                } else {
                    return "No visit report pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create Visit Report
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.UpdateVisitReportIsSync();
                        dba.close();
                    }


                    if (common.isConnected()) {
                        //call method to POST Recommendation
                        AsyncRecommendation task = new AsyncRecommendation();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting visit report...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to post data of recommendation
    private class AsyncRecommendation extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {
                responseJSON = "";

                JSONObject jsonMaster = new JSONObject();
                dba.open();
                //to get recommendation from database
                ArrayList<HashMap<String, String>> insmast = dba.GetRecommendationSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post recommendation
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmBlockNurseryType", insp.get("FarmBlockNurseryType"));
                        jsonins.put("FarmBlockNurseryId", insp.get("FarmBlockNurseryId"));
                        jsonins.put("ZoneId", insp.get("ZoneId"));
                        jsonins.put("PlantationUniqueId", insp.get("PlantationId"));
                        jsonins.put("VisitDate", insp.get("Date"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("UserId", insp.get("UserId"));
                        jsonins.put("IpAddress", common.getDeviceIPAddress(true));
                        jsonins.put("MachineName", common.getIMEI());
                        jsonins.put("RecommendationUniqueId", insp.get("UniqueId"));
                        array.put(jsonins);
                    }
                    jsonMaster.put("Master", array);

                    JSONObject jsonDetail = new JSONObject();
                    //To get new recommendation details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.GetRecommendationDetailSync();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post recommendation defect details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("RecommendationUniqueId", insd.get("RecommendationUniqueId"));
                            jsondet.put("WeekNo", insd.get("WeekNo"));
                            jsondet.put("FarmActivityId", insd.get("FarmActivityId"));
                            jsondet.put("FarmSubActivityId", insd.get("FarmSubActivityId"));
                            jsondet.put("UomId", insd.get("UomId"));
                            jsondet.put("Remarks", insd.get("Remarks"));
                            jsondet.put("ActivityValue", insd.get("ActivityValue"));
                            jsondet.put("FileName", insd.get("FileName").substring(insd.get("FileName").lastIndexOf("/") + 1));
                            jsondet.put("UniqueId", insd.get("UniqueId"));
                            arraydet.put(jsondet);
                        }
                        jsonDetail.put("Detail", arraydet);
                    }

                    sendJSon = jsonMaster + "~" + jsonDetail;

                    //To invoke json web service to create New recommendation on portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertRecommendation", common.url);
                } else {
                    return "No recommendation pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create recommendation
        @Override
        protected void onPostExecute(String result) {
            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.UpdateRecommendationIsSync();
                        dba.close();
                    }

                    if (common.isConnected()) {
                        /*//call method to POST Job Card
                        AsyncJobCardWSCall task = new AsyncJobCardWSCall();
                        task.execute();*/
                        /*AsyncPendingDispatchesForDeliveryWSCall task = new AsyncPendingDispatchesForDeliveryWSCall();
                        task.execute();*/
                        AsyncDeliveryWSCall task = new AsyncDeliveryWSCall();
                        task.execute();

                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }
            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting Recommendation...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }



    //To make web service class to post data of Job Card
    private class AsyncJobCardWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonJobCard = new JSONObject();
                dba.open();
                //to get New Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getUnSyncJobCards();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post New Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonjc = new JSONObject();
                        jsonjc.put("UniqueId", insp.get("UniqueId"));
                        jsonjc.put("FBNurseryType", insp.get("FBNurseryType"));
                        jsonjc.put("FBNurseryUniqueId", insp.get("FBNurseryUniqueId"));
                        jsonjc.put("ZoneId", insp.get("ZoneId"));
                        jsonjc.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonjc.put("WeekNo", insp.get("WeekNo"));
                        jsonjc.put("AndroidDate", insp.get("AndroidDate"));
                        jsonjc.put("Longitude", insp.get("Longitude"));
                        jsonjc.put("Latitude", insp.get("Latitude"));
                        jsonjc.put("Accuracy", insp.get("Accuracy"));
                        jsonjc.put("CreateBy", insp.get("CreateBy"));
                        jsonjc.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonjc.put("CreateMachine", common.getIMEI());
                        jsonjc.put("AndroidDate", insp.get("AndroidDate"));
                        jsonjc.put("VisitDate", insp.get("VisitDate"));
                        if (!TextUtils.isEmpty(insp.get("CreateByRole")))
                            jsonjc.put("CreateByRole", insp.get("CreateByRole"));
                        else {
                            if (insp.get("FBNurseryType").equalsIgnoreCase("FarmBlock") && userRole.contains("Service Provider"))
                                jsonjc.put("CreateByRole", "Service Provider");
                            else if (insp.get("FBNurseryType").equalsIgnoreCase("FarmBlock") && !userRole.contains("Service Provider"))
                                jsonjc.put("CreateByRole", "Farmer");
                            else {
                                dba.openR();
                                String userRole = "";
                                String createByRole = "";
                                userRole = dba.getAllRoles();
                                if (userRole.contains("Nursery Supervisor"))
                                    createByRole = "Nursery Supervisor";
                                else if (userRole.contains("Mini Nursery Service Provider"))
                                    createByRole = "Mini Nursery Service Provider";
                                else
                                    createByRole = "Mini Nursery User";
                                jsonjc.put("CreateByRole", createByRole);
                            }
                        }


                        array.put(jsonjc);
                    }
                    jsonJobCard.put("Master", array);

                    JSONObject jsonDetail = new JSONObject();
                    //To get Un Sync Job Card Details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getUnSyncJobCardDetail();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Assigned Block details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();

                            jsondet.put("UniqueId", insd.get("UniqueId"));
                            jsondet.put("FarmActivityId", insd.get("FarmActivityId"));
                            jsondet.put("FarmSubActivityId", insd.get("FarmSubActivityId"));
                            jsondet.put("ActivityValue", insd.get("ActivityValue"));
                            jsondet.put("ActivityType", insd.get("ActivityType"));
                            jsondet.put("PlannerDetailId", insd.get("PlannerDetailId"));
                            jsondet.put("ParameterDetailId", insd.get("ParameterDetailId").replace(".0", ""));
                            jsondet.put("JCDUniqueId", insd.get("JCDUniqueId"));
                            jsondet.put("FileNames", insd.get("FileNames").substring(insd.get("FileNames").lastIndexOf("/") + 1));


                            arraydet.put(jsondet);
                        }
                        jsonDetail.put("Detail", arraydet);
                    }

                    sendJSon = jsonJobCard + "~" + jsonDetail;

                    //To invoke json web service to Insert Uodate Job Cards On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertUpdateJobCard", common.url);
                } else {
                    return "No job cards pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Create Job Card
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_JobCardIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {

                        //call method of get Job Cards from Server By User Id
                        AsyncServerJobCardDetailWSCall task = new AsyncServerJobCardDetailWSCall();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting Job Cards...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }



    //Async class to handle ServerJob Card Details WS call as separate UI Thread
    private class AsyncServerJobCardDetailWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadJobCardByUser", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteJobCards();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.InsertServerJobCards(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"));
                    }
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.InsertServerJobCardDetails(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"), jsonArray.getJSONObject(i).getString("L"), jsonArray.getJSONObject(i).getString("M"), jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("O"));
                    }
                    dba.close();
                    if (syncFrom.equalsIgnoreCase("masters"))
                        common.showAlert(ActivityHome.this, curusrlang.equalsIgnoreCase("en") ? "Synchronization completed successfully." : "सिंक्रनाइज़ेशन सफलतापूर्वक पूरा हुआ।", false);
                    else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        // set title
                        alertDialogBuilder.setTitle(curusrlang.equalsIgnoreCase("en") ? "Sync Successful" : "सिंक्रनाइज़ेशन सफलतापूर्वक पूरा हुआ");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(curusrlang.equalsIgnoreCase("en") ? "Transaction Synchronization completed successfully. It is recommended to synchronize master data. Do you want to continue?" : "ट्रांसक्शन्स सिंक्रनाइज़ेशन सफलतापूर्वक पूरा हुआ। मास्टर डेटा को सिंक्रनाइज़ करने के लिए अनुशंसित है। क्या आप जारी रखना चाहते हैं?")
                                .setCancelable(false)
                                .setPositiveButton(curusrlang.equalsIgnoreCase("en") ? "Yes" : "हाँ", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (common.isConnected()) {
                                            AsyncUserRoleWSCall task = new AsyncUserRoleWSCall();
                                            task.execute();
                                        }
                                    }
                                })
                                .setNegativeButton(curusrlang.equalsIgnoreCase("en") ? "No" : "नहीं", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        dialog.cancel();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();

                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Job Cards Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Job Cards..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //AysnTask class to handle Job Card Confirmation WS call as separate UI Thread
    private class AsyncJobCardConfirmationSyncWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonDetails = new JSONObject();
                dba.open();
                ArrayList<HashMap<String, String>> insdet = dba.getConfimedPendingJobCardForSync();
                dba.close();
                if (insdet != null && insdet.size() > 0) {
                    JSONArray arraydet = new JSONArray();
                    try {
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("JobCardId", insd.get("JobCardId"));
                            jsondet.put("FarmBlockNurseryType", insd.get("FarmBlockNurseryType"));
                            jsondet.put("FarmBlockNurseryUniqueId", insd.get("UniqueId"));
                            jsondet.put("PlantationUniqueId", insd.get("PlantationUniqueId"));
                            jsondet.put("JobCardDetailId", insd.get("JobCardDetailId"));
                            jsondet.put("ConfirmedValue", insd.get("ConfirmValue"));
                            jsondet.put("CreateBy", userId);
                            jsondet.put("CreateDate", insd.get("CreateDate"));
                            jsondet.put("CreateIP", common.getDeviceIPAddress(true));
                            jsondet.put("CreateMachine", common.getIMEI());
                            arraydet.put(jsondet);
                        }
                        jsonDetails.put("Detail", arraydet);
                        responseJSON = common.invokeJSONWS(jsonDetails.toString(), "json", "InsertJobCardConfirmation", common.url);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                    }
                } else {
                    return "No Confirmed Job Card pending for synchronize.";
                }
                return "";
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                if (!result.contains("ERROR")) {
                    dba.open();
                    dba.DeletePendingJobCard();
                    dba.close();
                    if (common.isConnected()) {
                        //call method of get Farmer Code for Farmers
                        AsyncFarmerCodeWSCall task = new AsyncFarmerCodeWSCall();
                        task.execute();
                    }
                } else {
                    if (result.contains("null") || result == "")
                        result = "Server not responding. Please try again later.";
                    common.showAlert(ActivityHome.this, result, false);
                }

            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Synchronizing failed: " + "Unable to get response from server.", false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Uploading Confirmed Job Cards..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    //Async Task to send all attachments on the Portal
    private class Async_AllAttachments_WSCall extends AsyncTask<String, String, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";

                JSONObject jsonDocs = new JSONObject();

                dba.open();
                //Code to fetch data from database and store in hash map
                ArrayList<HashMap<String, String>> docDet = dba.getAttachmentsForSync();

                if (docDet != null && docDet.size() > 0) {
                    JSONArray array = new JSONArray();
                    try {
                        int totalFilesCount = docDet.size();
                        int currentCount = 0;
                        //Code to loop through hash map and create JSON
                        for (HashMap<String, String> mast : docDet) {
                            JSONObject jsonDoc = new JSONObject();

                            currentCount++;

                            jsonDoc.put("FarmerUniqueId", mast.get("FarmerUniqueId"));
                            jsonDoc.put("Type", mast.get("Type"));
                            if (mast.get("Type").equalsIgnoreCase("VisitReport"))
                                jsonDoc.put("ImageName", mast.get("FileName"));
                            else if (mast.get("Type").equalsIgnoreCase("JobCard"))
                                jsonDoc.put("ImageName", mast.get("FileName").substring(mast.get("FileName").lastIndexOf("/") + 1));
                            else
                                jsonDoc.put("ImageName", mast.get("Type").equalsIgnoreCase("Proof") ? mast.get("FileName") : mast.get("FileName").substring(mast.get("FileName").lastIndexOf("/") + 1));
                            File fle = new File(mast.get("FilePath"));
                            String flArray = "";
                            //Code to check if file exists and create byte array to be passed to json
                            if (fle.exists() && (fle.getAbsolutePath().contains(".jpg") || fle.getAbsolutePath().contains(".png") || fle.getAbsolutePath().contains(".gif") || fle.getAbsolutePath().contains(".jpeg") || fle.getAbsolutePath().contains(".bmp"))) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ALPHA_8;
                                Bitmap bitmap = BitmapFactory.decodeFile(fle.getAbsolutePath(), options);
                                flArray = getByteArrayFromImage(bitmap);

                                jsonDoc.put("FileArray", flArray);

                                array.put(jsonDoc);
                                jsonDocs.put("Attachment", array);
                                String sendJSon = jsonDocs.toString();

                                //Code to send json to portal and store response stored in responseJSON
                                responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertFarmerAttachments", common.url);
                                //Check responseJSON and update attachment status
                                if (responseJSON.equals("SUCCESS")) {
                                    dba.open();
                                    dba.updateAttachmentStatus(mast.get("FilePath"), mast.get("Type"));
                                    dba.close();
                                    publishProgress("Attachment(s) Uploaded: " + currentCount + "/" + totalFilesCount);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                        return "ERROR: Unable to fetch response from server.";
                    }

                }

                return responseJSON;
            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Internet is slow";
            } catch (Exception e) {
                // TODO: handle exception

                return "ERROR: Unable to fetch response from server.";
            }


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Dialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            try {
                if (!result.contains("ERROR")) {
                    File dir = new File(Environment.getExternalStorageDirectory() + "/" + "LPSMARTFARM");
                    deleteRecursive(dir);
                    if (common.isConnected()) {
                        //call method of get Farmer From Server
                        AsyncServerFarmerDetailsWSCall task = new AsyncServerFarmerDetailsWSCall();
                        task.execute();
                    }

                } else {
                    if (result == null || result == "null" || result.equals("ERROR: null"))
                        common.showAlert(ActivityHome.this, "Unable to get response from server.", false);
                    else
                        common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {

                common.showAlert(ActivityHome.this, "Synchronizing failed - Upload Attachments: " + e.getMessage(), false);
            }

        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Uploading Attachments..");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //Async Method to Validate User Current Credentials from Portal
    private class AsyncValidatePasswordWSCall extends AsyncTask<String, Void, String> {
        String source = "";
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {    // if this button is clicked, close

                source = params[0];
                String seedValue = "lpsmartfarm";
                HashMap<String, String> user = session.getLoginUserDetails();
                //Creation of JSON string for posting validating data
                JSONObject json = new JSONObject();
                json.put("username", Encrypt(user.get(UserSessionManager.KEY_USERNAME), seedValue));
                json.put("password", Encrypt(user.get(UserSessionManager.KEY_PWD), seedValue));
                json.put("imei", imei);
                json.put("version", dba.getVersion());
                String JSONStr = json.toString();
                //Store response fetched  from server in responseJSON variable
                responseJSON = common.invokeJSONWS(JSONStr, "json", "ValidatePassword", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                //e.printStackTrace();
                return "ERROR: Unable to fetch response from server.";
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            try {
                //Check if result contains error
                if (!result.contains("ERROR")) {
                    String passExpired = responseJSON.split("~")[0];
                    String passServer = responseJSON.split("~")[1];
                    String membershipError = responseJSON.split("~")[2];
                    //Check if password is expire and open change password intent
                    if (passExpired.toLowerCase(Locale.US).equals("yes")) {
                        Intent intent = new Intent(context, ActivityChangePassword.class);
                        intent.putExtra("fromwhere", "login");
                        startActivity(intent);
                        finish();
                    }
                    //Code to check other validations
                    else if (passServer.toLowerCase(Locale.US).equals("no")) {
                        String resp = "";

                        if (membershipError.toLowerCase(Locale.US).contains("NO_USER".toLowerCase(Locale.US))) {
                            resp = "There is no user in the system";
                        } else if (membershipError.toLowerCase(Locale.US).contains("BARRED".toLowerCase(Locale.US))) {
                            resp = "Your account has been barred by the Administrator.";
                        } else if (membershipError.toLowerCase(Locale.US).contains("LOCKED".toLowerCase(Locale.US))) {
                            resp = "Your account has been locked out because " +
                                    "you have exceeded the maximum number of incorrect login attempts. " +
                                    "Please contact the System Admin to " +
                                    "unblock your account.";
                        } else if (membershipError.toLowerCase(Locale.US).contains("LOGINFAILED".toLowerCase(Locale.US))) {
                            resp = "Invalid password. " +
                                    "Password is case-sensitive. " +
                                    "Access to the system will be disabled after " + responseJSON.split("~")[3] + " " +
                                    "consecutive wrong attempts.\n" +
                                    "Number of Attempts remaining: " + responseJSON.split("~")[4];
                        } else {
                            resp = "Password mismatched. Enter latest password!";
                        }


                        showChangePassWindow(source, resp);
                    }
                    //Code to check source of request
                    else if (source.equals("masters")) {
                        //If version does not match logout user
                        if (responseJSON.contains("NOVERSION")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Application is running an older version. Please install latest version.!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                           /* dba.open();
                                            int cnt = dba.getUnSyncCount();
                                            dba.close();
                                            if (cnt == 0) {*/
                                            AsyncLogOutWSCall task = new AsyncLogOutWSCall();
                                            task.execute();
                                            /*} else {
                                                common.showAlert(ActivityHome.this, "Please Synchronize pending data before log out.", false);
                                            }*/
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            //Calling async method for master synchronization
                            AsyncUserRoleWSCall task = new AsyncUserRoleWSCall();
                            task.execute();
                        }

                    } else {
                        common.showToast("Error in checking password");
                    }
                } else {
                    common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
                }

            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Validating credentials failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Validating your credentials..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    //Async class to handle FarmerCode WS call as separate UI Thread
    private class AsyncFarmerCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get Farmer Unique Id
                ArrayList<HashMap<String, String>> insmast = dba.getFarmerUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmer Unique Id
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmerUniqueId", insp.get("FarmerUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);


                    sendJSon = jsonFarmer.toString();

                    //To invoke json web service to Read Farmer Code
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadFarmerCode", common.url);
                } else {
                    return "No Farmer pending for fetching Farmer Code.";
                }

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updateFarmerCode(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of get Farm Block Code for Farmers
                        AsyncFarmBlockCodeWSCall task = new AsyncFarmBlockCodeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Code Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Code..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Farm Block Code WSCall as separate UI Thread
    private class AsyncFarmBlockCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get Farm Block Unique Id
                ArrayList<HashMap<String, String>> insmast = dba.getFarmBlockUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to Farm Block Unique Id
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmBlockUniqueId", insp.get("FarmBlockUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);


                    sendJSon = jsonFarmer.toString();

                    //To invoke json web service to Read Farm Block  Code
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadFarmBlockCode", common.url);
                } else {
                    return "No Farm Block pending for fetching Farm Block Code.";
                }


            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updateFarmBlockCode(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of Fetch Plantation Codes
                        AsyncPlantationCodeWSCall task = new AsyncPlantationCodeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Code Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Block Code..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Plantation Code WSCall as separate UI Thread
    private class AsyncPlantationCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get Plantation Unique Id from database
                ArrayList<HashMap<String, String>> insmast = dba.getPlantationUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post PlantationUniqueId
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmBlockUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);


                    sendJSon = jsonFarmer.toString();

                    //To invoke json web service to Read Plantation Code
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadPlantationCode", common.url);
                } else {
                    return "No Plantation pending for fetching Plantation Code.";
                }


            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updatePlantationCode(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method to Fetch Fin Year For Inter Cropping
                        AsyncInterCropFinYearWSCall task = new AsyncInterCropFinYearWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Code Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Block Code..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Inter Cropping Fin Year WS call as separate UI Thread
    private class AsyncInterCropFinYearWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";

                JSONObject jsonFarmer = new JSONObject();
                dba.open();
                //to get Inter Cropping Unique Id from database
                ArrayList<HashMap<String, String>> insmast = dba.getInterCropFinUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post InterCropping Unique Id
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("FarmBlockUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonFarmer.put("Master", array);


                    sendJSon = jsonFarmer.toString();

                    //To invoke json web service to Inter Cropping Financial Year
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadInterCropFinYear", common.url);
                } else {
                    return "No Farmer pending for fetching Farmer Code.";
                }


            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updateInterCropFinYear(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of Sync Nursery Zone Coordinates
                        AsyncNurseryZoneCoordinateSyncWSCall task = new AsyncNurseryZoneCoordinateSyncWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Financial Year Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Financial Year..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle UserRole WS call as separate UI Thread
    private class AsyncUserRoleWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {

                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadUserRoleDetails", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    //Code to delete all data from tables
                    dba.deleteAllData();
                    dba.close();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUserRoles(jsonArray.getJSONObject(i).getString("UserId"), jsonArray.getJSONObject(i).getString("UserName"), jsonArray.getJSONObject(i).getString("RoleName"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("NurseryId"));
                    }
                    dba.close();
                    dba.openR();
                    userRole = dba.getAllRoles();

                    if (userRole.contains("Farmer"))
                        syncByRole = "Farmer";
                    else
                        syncByRole = "";

                    if (userRole.contains("Area Manager")
                            || userRole.contains("Executive")
                            || userRole.contains("FA Administrator")
                            || userRole.contains("Management User")
                            || userRole.contains("Mini Nursery Admin")
                            || userRole.contains("Tele Caller"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_sync);
                    else if (userRole.contains("Service Provider")
                            && !userRole.contains("Mini Nursery Service Provider"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_confirm,
                                R.layout.btn_recommend,
                                R.layout.btn_visits,
                                R.layout.btn_farm_activity,
                                R.layout.btn_delivery,
                                R.layout.btn_sync);
                    else if (userRole.contains("Mini Nursery Service Provider"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_visits,
                                R.layout.btn_farm_activity,
                                R.layout.btn_sync);
                    else if (userRole.contains("Mini Nursery User")
                            || userRole.contains("Nursery Supervisor"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_nursery,
                                R.layout.btn_farm_activity,
                                R.layout.btn_stock_return,
                                R.layout.btn_sync);
                    else if (userRole.contains("Service Provider")
                            && !userRole.contains("Mini Nursery Service Provider")
                            && userRole.contains("Nursery Supervisor"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_nursery,
                                R.layout.btn_confirm,
                                R.layout.btn_recommend,
                                R.layout.btn_visits,
                                R.layout.btn_farm_activity,
                                R.layout.btn_sync);
                    else if (userRole.contains("Agronomist"))
                        views = Arrays.asList(R.layout.btn_farmer_create,
                                R.layout.btn_farmer_view,
                                R.layout.btn_confirm,
                                R.layout.btn_recommend,
                                R.layout.btn_visits,
                                R.layout.btn_sync);
                    else if (userRole.contains("Farmer"))
                        views = Arrays.asList(R.layout.btn_farmer_view,
                                R.layout.btn_farm_activity,
                                R.layout.btn_sync);


                    go.performClick();

                    //Code to Check Whether to display Nursery Button Or Not
                  /*  if (userRole.contains("Nursery Supervisor") || userRole.contains("Mini Nursery User"))
                        btnNursery.setVisibility(View.VISIBLE);
                    else
                        btnNursery.setVisibility(View.GONE);


                    //Code to Check Whether to display Confirmation/Recommendation Button Or Not
                    if ((userRole.contains("Service Provider") && !userRole.contains("Mini Nursery Service Provider")) || userRole.contains("Agronomist")) {
                        btnJobCardConfirmation.setVisibility(View.VISIBLE);
                        btnRecommendation.setVisibility(View.VISIBLE);
                        trConfirmRecommend.setVisibility(View.VISIBLE);
                    } else {
                        btnJobCardConfirmation.setVisibility(View.GONE);
                        btnRecommendation.setVisibility(View.GONE);
                        trConfirmRecommend.setVisibility(View.GONE);
                    }


                    //Code to Check Whether to display Visit Report Button Or Not
                    if (userRole.contains("Service Provider") || userRole.contains("Agronomist") || userRole.contains("Mini Nursery Service Provider"))
                        btnVisitReports.setVisibility(View.VISIBLE);
                    else
                        btnVisitReports.setVisibility(View.GONE);

                    //Code to Check Whether to display Activity Button Or Not
                    if (userRole.contains("Service Provider") || userRole.contains("Nursery Supervisor") || userRole.contains("Mini Nursery Service Provider") || userRole.contains("Mini Nursery User"))
                        btnFarmActivity.setVisibility(View.VISIBLE);
                    else
                        btnFarmActivity.setVisibility(View.GONE);*/
                    if (common.isConnected()) {
                        AsyncStateWSCall task = new AsyncStateWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "User Roles Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading User Roles..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle State WS call as separate UI Thread
    private class AsyncStateWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
/*                dba.open();
                //Code to delete all data from tables on logout
                dba.deleteAllData();
                dba.close();*/
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadState", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertState(jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("StateName"), jsonArray.getJSONObject(i).getString("ShortName"), jsonArray.getJSONObject(i).getString("StateCode"), jsonArray.getJSONObject(i).getString("StateNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncDistrictWSCall task = new AsyncDistrictWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "State Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading State..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle District WS call as separate UI Thread
    private class AsyncDistrictWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadDistrict", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertDistrict(jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictName"), jsonArray.getJSONObject(i).getString("DistrictNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncCityWSCall task = new AsyncCityWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "District Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading District..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle City WS call as separate UI Thread
    private class AsyncCityWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadCity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertCity(jsonArray.getJSONObject(i).getString("CityId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("CityName"), jsonArray.getJSONObject(i).getString("CityNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncBlockWSCall task = new AsyncBlockWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "City Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading City..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Block WS call as separate UI Thread
    private class AsyncBlockWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadBlock", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertBlock(jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockName"), jsonArray.getJSONObject(i).getString("BlockNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPanchayatWSCall task = new AsyncPanchayatWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Block Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Block..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Panchayat WS call as separate UI Thread
    private class AsyncPanchayatWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPanchayat", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPanchayat(jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("PanchayatName"), jsonArray.getJSONObject(i).getString("PanchayatNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncVillageWSCall task = new AsyncVillageWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Panchayat Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Panchayat..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Village WS call as separate UI Thread
    private class AsyncVillageWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadVillage", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertVillage(jsonArray.getJSONObject(i).getString("VillageId"), jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("VillageName"), jsonArray.getJSONObject(i).getString("VillageNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPinCodeWSCall task = new AsyncPinCodeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Village Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Village..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle PinCode WS call as separate UI Thread
    private class AsyncPinCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPincode", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPinCode(jsonArray.getJSONObject(i).getString("PinCodeId"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("CityId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("VillageId"), jsonArray.getJSONObject(i).getString("PinCode"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncShortCloseReasonWSCall task = new AsyncShortCloseReasonWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Pincode Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Pincode..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle PinCode WS call as separate UI Thread
    private class AsyncShortCloseReasonWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadShortCloseReason", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertShortCloseReason(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncFarmerTypeWSCall task = new AsyncFarmerTypeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "ShortClose Reason Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading ShortClose Reason..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle FarmerType WS call as separate UI Thread
    private class AsyncFarmerTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertFarmerType(jsonArray.getJSONObject(i).getString("FarmerTypeId"), jsonArray.getJSONObject(i).getString("FarmerType"), jsonArray.getJSONObject(i).getString("FarmerTypeLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLanguagesWSCall task = new AsyncLanguagesWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Type Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Type..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Languages WS call as separate UI Thread
    private class AsyncLanguagesWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLanguages", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLanguages(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncEducationLevelWSCall task = new AsyncEducationLevelWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Language Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Language..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Education Level WS call as separate UI Thread
    private class AsyncEducationLevelWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadEducationLevel", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertEducationLevel(jsonArray.getJSONObject(i).getString("EducationLevelId"), jsonArray.getJSONObject(i).getString("EducationLevel"), jsonArray.getJSONObject(i).getString("EducationLevelLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncProofTypeWSCall task = new AsyncProofTypeWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Education Level Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Education Level..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Proof Type WS call as separate UI Thread
    private class AsyncProofTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadProofType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertProofType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPOAPOIWSCall task = new AsyncPOAPOIWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Proof Type Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Proof Type..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle POAPOI WS call as separate UI Thread
    private class AsyncPOAPOIWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPOAPOI", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPOAPOI(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        AsyncOrganizerWSCall task = new AsyncOrganizerWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "POA / POI Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading POA / POI..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Organizer WS call as separate UI Thread
    private class AsyncOrganizerWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadOrganizer", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertOrganizer(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLandIssueWSCall task = new AsyncLandIssueWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Organizer Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Organizer..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LandIssue WS call as separate UI Thread
    private class AsyncLandIssueWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLandIssue", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLandIssue(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncSoilTypeWSCall task = new AsyncSoilTypeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "LandIssue Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading LandIssue..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle SoilType WS call as separate UI Thread
    private class AsyncSoilTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadSoilType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertSoilType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLandTypeWSCall task = new AsyncLandTypeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "SoilType Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading SoilType..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LandType WS call as separate UI Thread
    private class AsyncLandTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLandType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLandType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncExistingUseWSCall task = new AsyncExistingUseWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Ownership Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Ownership..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ExistingUse WS call as separate UI Thread
    private class AsyncExistingUseWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadExistingUse", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertExistingUse(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncCommunityUseWSCall task = new AsyncCommunityUseWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "ExistingUse Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading ExistingUse..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle CommunityUse WS call as separate UI Thread
    private class AsyncCommunityUseWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadCommunityUse", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertCommunityUse(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncExistingHazardWSCall task = new AsyncExistingHazardWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "CommunityUse Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading CommunityUse..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ExistingHazard WS call as separate UI Thread
    private class AsyncExistingHazardWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadExistingHazard", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertExistingHazard(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLegalDisputeWSCall task = new AsyncLegalDisputeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "ExistingHazard Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading ExistingHazard..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LegalDispute WS call as separate UI Thread
    private class AsyncLegalDisputeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLegalDispute", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLegalDispute(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncNearestDamWSCall task = new AsyncNearestDamWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "LegalDispute Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading LegalDispute..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    //Async class to handle NearestDam WS call as separate UI Thread
    private class AsyncNearestDamWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNearestDam", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNearestDam(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncNearestRiverWSCall task = new AsyncNearestRiverWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "NearestDam Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading NearestDam..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle NearestRiver WS call as separate UI Thread
    private class AsyncNearestRiverWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNearestRiver", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNearestRiver(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncElectricitySourceWSCall task = new AsyncElectricitySourceWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "NearestRiver Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading NearestRiver..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ElectricitySource WS call as separate UI Thread
    private class AsyncElectricitySourceWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadElectricitySource", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertElectricitySource(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncWaterSourceWSCall task = new AsyncWaterSourceWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "ElectricitySource Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading ElectricitySource..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle WaterSource WS call as separate UI Thread
    private class AsyncWaterSourceWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadWaterSource", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertWaterSource(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncIrrigationSystemWSCall task = new AsyncIrrigationSystemWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "WaterSource Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading WaterSource..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle IrrigationSystem WS call as separate UI Thread
    private class AsyncIrrigationSystemWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadIrrigationSystem", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertIrrigationSystem(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLandCharacteristicWSCall task = new AsyncLandCharacteristicWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "IrrigationSystem Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading IrrigationSystem..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LandCharacteristic WS call as separate UI Thread
    private class AsyncLandCharacteristicWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLandCharacteristic", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLandCharacteristic(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncSeasonWSCall task = new AsyncSeasonWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "LandCharacteristic Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading LandCharacteristic..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Season WS call as separate UI Thread
    private class AsyncSeasonWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadSeason", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertSeason(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncCropWSCall task = new AsyncCropWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Season Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Season..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Crop WS call as separate UI Thread
    private class AsyncCropWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadCrop", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertCrop(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncVarietyWSCall task = new AsyncVarietyWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Crop Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Crop..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Variety WS call as separate UI Thread
    private class AsyncVarietyWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadVariety", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertVariety(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncRelationShipWSCall task = new AsyncRelationShipWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Variety Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Variety..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle RelationShip WS call as separate UI Thread
    private class AsyncRelationShipWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadRelationShip", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertRelationShip(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncFarmAssteWSCall task = new AsyncFarmAssteWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Relationship Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Relationship..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle FarmAssets WS call as separate UI Thread
    private class AsyncFarmAssteWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmAssets", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertFarmAsset(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLoanSourceWSCall task = new AsyncLoanSourceWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Asset Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Asset..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LoanSource WS call as separate UI Thread
    private class AsyncLoanSourceWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLoanSource", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLoanSource(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncLoanTypeWSCall task = new AsyncLoanTypeWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Loan Source Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Loan Source..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle LoanType WS call as separate UI Thread
    private class AsyncLoanTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadLoanType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertLoanType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncMonthageWSCall task = new AsyncMonthageWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Loan Type Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Loan Type..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Monthage WS call as separate UI Thread
    private class AsyncMonthageWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadMonthAge", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertMonthage(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncNurseryWSCall task = new AsyncNurseryWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Monthage Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Monthage..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //<editor-fold desc="AsyncTask class to handle Nursery WS call as separate UI Thread">
    //Async class to handle Nursery WS call as separate UI Thread
    private class AsyncNurseryWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNursery", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNursery(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"), jsonArray.getJSONObject(i).getString("L"), jsonArray.getJSONObject(i).getString("M"), jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i).getString("O"), jsonArray.getJSONObject(i).getString("P"), jsonArray.getJSONObject(i).getString("Q"), jsonArray.getJSONObject(i).getString("R"), jsonArray.getJSONObject(i).getString("S"), jsonArray.getJSONObject(i).getString("T"), jsonArray.getJSONObject(i).getString("U"), jsonArray.getJSONObject(i).getString("V"), jsonArray.getJSONObject(i).getString("W"), jsonArray.getJSONObject(i).getString("X"), jsonArray.getJSONObject(i).getString("Y"), jsonArray.getJSONObject(i).getString("Z"), jsonArray.getJSONObject(i).getString("AA"), jsonArray.getJSONObject(i).getString("AB"), jsonArray.getJSONObject(i).getString("AC"), jsonArray.getJSONObject(i).getString("AD"), jsonArray.getJSONObject(i).getString("AE"), jsonArray.getJSONObject(i).getString("AF"), jsonArray.getJSONObject(i).getString("AG"), jsonArray.getJSONObject(i).getString("AH"), jsonArray.getJSONObject(i).getString("AI"), jsonArray.getJSONObject(i).getString("AJ"), jsonArray.getJSONObject(i).getString("AK"), jsonArray.getJSONObject(i).getString("AL"), jsonArray.getJSONObject(i).getString("AM"), jsonArray.getJSONObject(i).getString("AN"), jsonArray.getJSONObject(i).getString("AO"), jsonArray.getJSONObject(i).getString("AP"), jsonArray.getJSONObject(i).getString("AQ"), jsonArray.getJSONObject(i).getString("AR"), jsonArray.getJSONObject(i).getString("AS"), jsonArray.getJSONObject(i).getString("AT"), jsonArray.getJSONObject(i).getString("AU"), jsonArray.getJSONObject(i).getString("AV"), jsonArray.getJSONObject(i).getString("AW"), jsonArray.getJSONObject(i).getString("AX"), jsonArray.getJSONObject(i).getString("AY"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        AsyncServerNurseryAddressWSCall task = new AsyncServerNurseryAddressWSCall();
                        task.execute();
                    }


                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryAddress WS call as separate UI Thread">
    //Async class to handle Server Nursery Address WS call as separate UI Thread
    private class AsyncServerNurseryAddressWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryAddress", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertServerNurseryAddress(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncNurseryAccountDetailWSCall task = new AsyncNurseryAccountDetailWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Address Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Address..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryAccountDetail WS call as separate UI Thread">
    //Async class to handle NurseryAccountDetail WS call as separate UI Thread
    private class AsyncNurseryAccountDetailWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryAccountDetail", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryAccountDetail(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerNurseryCroppingPatternWSCall task = new AsyncServerNurseryCroppingPatternWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Account Detail Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Account Detail..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryCroppingPattern WS call as separate UI Thread">
    //Async class to handle NurseryCroppingPattern WS call as separate UI Thread
    private class AsyncServerNurseryCroppingPatternWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryCroppingPattern", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryCroppingPattern(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerNurseryLandCharacteristicWSCall task = new AsyncServerNurseryLandCharacteristicWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Cropping Pattern Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Cropping Pattern Detail..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryLandCharacteristic WS call as separate UI Thread">
    //Async class to handle NurseryLandCharacteristic WS call as separate UI Thread
    private class AsyncServerNurseryLandCharacteristicWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryLandCharacteristic", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryLandCharacteristic(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerNurseryLandIssueWSCall task = new AsyncServerNurseryLandIssueWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Land Characteristic Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Land Characteristic Detail..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryLandIssue WS call as separate UI Thread">
    //Async class to handle NurseryLandIssue WS call as separate UI Thread
    private class AsyncServerNurseryLandIssueWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryLandIssue", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryLandIssue(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncNurseryZoneWSCall task = new AsyncNurseryZoneWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Land Issue Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Land Issue Detail..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryZone WS call as separate UI Thread">
    //Async class to handle NurseryZone Coordinates WS call as separate UI Thread
    private class AsyncNurseryZoneWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryZone", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryZone(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerNurseryCoordinatesWSCall task = new AsyncServerNurseryCoordinatesWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Zone Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Zone..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle NurseryCoordinates WS call as separate UI Thread">
    //Async class to handle AsyncServerNurseryCoordinatesWSCall Coordinates WS call as separate UI Thread
    private class AsyncServerNurseryCoordinatesWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryZoneCoordinates", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("UniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerNurseryCoordinates(jsonArray.getJSONObject(i).getString("UniqueId"),
                                jsonArray.getJSONObject(i).getString("NurseryId"),
                                jsonArray.getJSONObject(i).getString("NurseryZoneId"),
                                jsonArray.getJSONObject(i).getString("Latitude"),
                                jsonArray.getJSONObject(i).getString("Longitude"),
                                jsonArray.getJSONObject(i).getString("Accuracy"), flag, userId);
                        prevData = jsonArray.getJSONObject(i).getString("UniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {

                        AsyncServerNurseryPlantationWSCall task = new AsyncServerNurseryPlantationWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Coordinates Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Coordinates..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AysnTask class to handle ServerNurseryPlantataion WS call as separate UI Thread">
    private class AsyncServerNurseryPlantationWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryPlantation", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertNurseryPlantationFromServer(
                                jsonArray.getJSONObject(i).getString("UniqueId"),
                                jsonArray.getJSONObject(i).getString("NurseryUniqueId"),
                                jsonArray.getJSONObject(i).getString("NurseryId"),
                                jsonArray.getJSONObject(i).getString("PlantTypeId"),
                                jsonArray.getJSONObject(i).getString("ZoneId"),
                                jsonArray.getJSONObject(i).getString("CropVarietyId"),
                                jsonArray.getJSONObject(i).getString("MonthAgeId"),
                                jsonArray.getJSONObject(i).getString("Acreage"),
                                jsonArray.getJSONObject(i).getString("PlantationDate"),
                                jsonArray.getJSONObject(i).getString("PlantingSystemId"),
                                jsonArray.getJSONObject(i).getString("PlantRow"),
                                jsonArray.getJSONObject(i).getString("PlantColumn"),
                                jsonArray.getJSONObject(i).getString("Balance"),
                                jsonArray.getJSONObject(i).getString("TotalPlant"),
                                jsonArray.getJSONObject(i).getString("PlantationCode"),
                                jsonArray.getJSONObject(i).getString("CropId"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        //call method of Insert Nursery Inter Cropping  Details In NurseryInterCropping Table
                        AsyncServerNurseryInterCroppingWSCall task = new AsyncServerNurseryInterCroppingWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Plantation Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Plantation..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AysnTask class to handle Server Nursery Inter Cropping WS call as separate UI Thread">
    private class AsyncServerNurseryInterCroppingWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadNurseryPlantationInterCropping", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertServerNurseryInterCropping(
                                jsonArray.getJSONObject(i).getString("UniqueId"),
                                jsonArray.getJSONObject(i).getString("PlantationUniqueId"),
                                jsonArray.getJSONObject(i).getString("CropVarietyId"),
                                jsonArray.getJSONObject(i).getString("SeasonId"),
                                jsonArray.getJSONObject(i).getString("Acreage"),
                                jsonArray.getJSONObject(i).getString("FinancialYear"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        //call method of Plant Type
                        AsyncPaymentModeWSCall task = new AsyncPaymentModeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Inter Cropping Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Inter Cropping..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle Payment Mode WS call as separate UI Thread">
    //Async class to handle Payment Mode WS call as separate UI Thread
    private class AsyncPaymentModeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPaymentMode", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPaymentMode(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPolyBagRateWSCall task = new AsyncPolyBagRateWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Payment Mode Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Payment Mode..");
            Dialog.setCancelable(false);
            Dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle PolyBag Rate WS call as separate UI Thread">
    //Async class to handle Poly Bag Rate WS call as separate UI Thread
    private class AsyncPolyBagRateWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPolyBagRate", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPolyBagRate(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerBookingCollectionDetailWSCall task = new AsyncServerBookingCollectionDetailWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Polybag Rate Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Polybag Rate..");
            Dialog.setCancelable(false);
            Dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>


    //<editor-fold desc="AsyncTask class to handle Booking Collection Details WS call as separate UI Thread">
    //Async class to handle Server Booking Collection Details WS call as separate UI Thread
    private class AsyncServerBookingCollectionDetailWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadBookingCollectionData", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteBookingCollections();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.InsertServerBookingCollections(jsonArray.getJSONObject(i).getString("AndroidUniqueId"), jsonArray.getJSONObject(i).getString("BookingFor"), jsonArray.getJSONObject(i).getString("BookingForId"), jsonArray.getJSONObject(i).getString("FarmBlockId"), jsonArray.getJSONObject(i).getString("Street1"), jsonArray.getJSONObject(i).getString("Street2"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("VillageId"), jsonArray.getJSONObject(i).getString("CityId"), jsonArray.getJSONObject(i).getString("PinCodeId"), jsonArray.getJSONObject(i).getString("AddressType"), jsonArray.getJSONObject(i).getString("BookingDate"), jsonArray.getJSONObject(i).getString("DeliveryDate"), jsonArray.getJSONObject(i).getString("Quantity"), jsonArray.getJSONObject(i).getString("Rate"), jsonArray.getJSONObject(i).getString("TotalAmount"), jsonArray.getJSONObject(i).getString("PaymentMode"), jsonArray.getJSONObject(i).getString("TotalAmount"), jsonArray.getJSONObject(i).getString("Remarks"), jsonArray.getJSONObject(i).getString("PaymentFileName"), "", jsonArray.getJSONObject(i).getString("BalanceQuantity"), jsonArray.getJSONObject(i).getString("ShortCloseQuantity"), jsonArray.getJSONObject(i).getString("ShortCloseReasonId"), jsonArray.getJSONObject(i).getString("ShortCloseBy"), jsonArray.getJSONObject(i).getString("ShortCloseDate"), jsonArray.getJSONObject(i).getString("CreateBy"), jsonArray.getJSONObject(i).getString("CreateDate"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPlantTypeWSCall task = new AsyncPlantTypeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Booking Collection Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Booking Collections..");
            Dialog.setCancelable(false);
            Dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle PlantType WS call as separate UI Thread">
    private class AsyncPlantTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPlantType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlantType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        if (userRole.contains("Agronomist") || userRole.contains("Service Provider") || userRole.contains("Nursery Supervisor") || userRole.contains("Mini Nursery User") || userRole.contains("Mini Nursery Service Provider")) {
                            AsyncUOMWSCall task = new AsyncUOMWSCall();
                            task.execute();
                        } else {
                            AsyncPlantingSystemWSCall task = new AsyncPlantingSystemWSCall();
                            task.execute();
                        }
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "PlantType Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading PlantType..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle UOM WS call as separate UI Thread">
    private class AsyncUOMWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadUom", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUOM(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncFarmActivityTypeWSCall task = new AsyncFarmActivityTypeWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "UOM Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading UOM..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle FarmActivity Type WS call as separate UI Thread">
    private class AsyncFarmActivityTypeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmActivityType", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertFarmActivityType(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncFarmActivityWSCall task = new AsyncFarmActivityWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Activity Type Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Activity Type..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle FarmActivity WS call as separate UI Thread">
    private class AsyncFarmActivityWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmActivity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertFarmActivity(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncFarmSubActivityWSCall task = new AsyncFarmSubActivityWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Activity Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Activity..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle FarmSubActivity WS call as separate UI Thread">
    private class AsyncFarmSubActivityWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmSubActivity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertFarmSubActivity(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPlannedWSCall task = new AsyncPlannedWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Sub Activity Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Sub Activity..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle Planned WS call as separate UI Thread">
    private class AsyncPlannedWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPlannedActivity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deletePlannedActivity();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlannedActivity(jsonArray.getJSONObject(i).getString("FarmBlockNurseryType"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryId"), jsonArray.getJSONObject(i).getString("PlantationId"), jsonArray.getJSONObject(i).getString("PlantationUniqueId"), jsonArray.getJSONObject(i).getString("PlannerDetailId"), jsonArray.getJSONObject(i).getString("ActivityId"), jsonArray.getJSONObject(i).getString("SubActivityId"), jsonArray.getJSONObject(i).getString("WeekNo"), jsonArray.getJSONObject(i).getString("UOMId"), jsonArray.getJSONObject(i).getString("Quantity"), jsonArray.getJSONObject(i).getString("Remarks"), jsonArray.getJSONObject(i).getString("ParameterDetailId"), jsonArray.getJSONObject(i).getString("PlantationDate"), jsonArray.getJSONObject(i).getString("FromDate"), jsonArray.getJSONObject(i).getString("ToDate"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryUniqueId"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncRecommendedWSCall task = new AsyncRecommendedWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Planned Activity Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Planned Activity..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle Recommended WS call as separate UI Thread">
    private class AsyncRecommendedWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadRecommendedActivity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteRecommendedActivity();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertRecommendedActivity(jsonArray.getJSONObject(i).getString("FarmBlockNurseryType"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryId"), jsonArray.getJSONObject(i).getString("PlantationId"), jsonArray.getJSONObject(i).getString("PlantationUniqueId"), jsonArray.getJSONObject(i).getString("FarmActivityId"), jsonArray.getJSONObject(i).getString("FarmSubActivityId"), jsonArray.getJSONObject(i).getString("JobCardWeekNo"), jsonArray.getJSONObject(i).getString("UOMId"), jsonArray.getJSONObject(i).getString("ActivityValue"), jsonArray.getJSONObject(i).getString("Remarks"), jsonArray.getJSONObject(i).getString("JobCardFromDate"), jsonArray.getJSONObject(i).getString("JobCardToDate"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryUniqueId"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncAllActivitiesWSCall task = new AsyncAllActivitiesWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Recommended Activity Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Recommended Activity..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle AllActivities WS call as separate UI Thread">
    private class AsyncAllActivitiesWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadJobCardAllActivity", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteAllActivity();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertAllActivities(jsonArray.getJSONObject(i).getString("FarmBlockNurseryType"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryId"), jsonArray.getJSONObject(i).getString("PlantationId"), jsonArray.getJSONObject(i).getString("PlantationUniqueId"), jsonArray.getJSONObject(i).getString("ActivityId"), jsonArray.getJSONObject(i).getString("SubActivityId"), jsonArray.getJSONObject(i).getString("UOMId"), jsonArray.getJSONObject(i).getString("Remarks"), jsonArray.getJSONObject(i).getString("FarmBlockNurseryUniqueId"), jsonArray.getJSONObject(i).getString("WeekNo"), jsonArray.getJSONObject(i).getString("Quantity").replace(".0", ""));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncPlantationWeekWSCall task = new AsyncPlantationWeekWSCall();
                        task.execute();

                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "All Activities Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading All Activities..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle PlantationWeek WS call as separate UI Thread">
    private class AsyncPlantationWeekWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPlantationWeek", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteAllPlantationWeek();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlantationWeek(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("WeekNo"), jsonArray.getJSONObject(i).getString("FromDate"), jsonArray.getJSONObject(i).getString("ToDate"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerJobCardDetailDuringLoginWSCall task = new AsyncServerJobCardDetailDuringLoginWSCall();
                        task.execute();

                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Plantation Week Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Plantation Week..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>


    //Async class to handle ServerJob Card Details During Login WS call as separate UI Thread
    private class AsyncServerJobCardDetailDuringLoginWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadJobCardByUser", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deleteJobCards();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.InsertServerJobCards(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"));
                    }
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.InsertServerJobCardDetails(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"), jsonArray.getJSONObject(i).getString("L"), jsonArray.getJSONObject(i).getString("M"), jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("O"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of get Plant Status Master
                        AsyncPlantStatusWSCall task = new AsyncPlantStatusWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Job Cards Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Job Cards..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle PlantStatus WS call as separate UI Thread
    private class AsyncPlantStatusWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPlantStatus", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlantStatus(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncDefectWSCall task = new AsyncDefectWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "PlantStatus Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading PlantStatus..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //AysnTask class to handle NurseryZoneCoordinate WS call as separate UI Thread
    private class AsyncNurseryZoneCoordinateSyncWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonDetails = new JSONObject();
                dba.open();
                ArrayList<HashMap<String, String>> insdet = dba.getNurseryZoneCoordinates();
                dba.close();
                if (insdet != null && insdet.size() > 0) {
                    JSONArray arraydet = new JSONArray();
                    try {
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("Id", insd.get("Id"));
                            jsondet.put("UniqueId", insd.get("UniqueId"));
                            jsondet.put("NurseryId", insd.get("NurseryId"));
                            jsondet.put("NurseryZoneId", insd.get("NurseryZoneId"));
                            jsondet.put("Latitude", insd.get("Latitude"));
                            jsondet.put("Longitude", insd.get("Longitude"));
                            jsondet.put("Accuracy", insd.get("Accuracy"));
                            jsondet.put("CreateBy", insd.get("CreateBy"));
                            jsondet.put("CreateDate", insd.get("CreateDate"));
                            jsondet.put("CreateIP", common.getDeviceIPAddress(true));
                            jsondet.put("CreateMachine", common.getIMEI());
                            arraydet.put(jsondet);
                        }
                        jsonDetails.put("Detail", arraydet);
                        responseJSON = common.invokeJSONWS(jsonDetails.toString(), "json", "InsertNurseryZoneCoordinates", common.url);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                    }
                } else {
                    return "No Nursery - Zone Coordinates pending for synchronize.";
                }
                return "";
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                if (!result.contains("ERROR")) {
                    dba.open();
                    dba.Update_NZCIsSync();
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Update Nursery Plantation Code
                        AsyncNurseryPlantation task = new AsyncNurseryPlantation();
                        task.execute();
                    }
                } else {
                    if (result.contains("null") || result == "")
                        result = "Server not responding. Please try again later.";
                    common.showAlert(ActivityHome.this, result, false);
                }

            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Synchronizing failed: " + "Unable to get response from server.", false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Uploading Nursery Zone Coordinates..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    //<editor-fold desc="To make web service class to post data of Nursery Plantation">
    private class AsyncNurseryPlantation extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();
                dba.open();

                /*Get Nursery Plantation for Sync from database*/
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryPlantationForSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();

                    /*Create json string to post Nursery Plantation*/
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("NurseryUniqueId", insp.get("NurseryUniqueId"));
                        jsonins.put("ZoneId", insp.get("ZoneId"));
                        jsonins.put("CropVarietyId", insp.get("CropVarietyId"));
                        jsonins.put("MonthAgeId", insp.get("MonthAgeId"));
                        jsonins.put("Acerage", insp.get("Acreage"));
                        jsonins.put("PlantationDate", insp.get("PlantationDate"));
                        jsonins.put("PlantTypeId", insp.get("PlantTypeId"));
                        jsonins.put("PlantingSystemId", insp.get("PlantingSystemId"));
                        jsonins.put("PlantRow", insp.get("PlantRow"));
                        jsonins.put("PlantColumn", insp.get("PlantColumn"));
                        jsonins.put("Balance", insp.get("Balance"));
                        jsonins.put("TotalPlant", insp.get("TotalPlant"));
                        jsonins.put("CreateBy", insp.get("CreateBy"));
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);

                    sendJSon = jsonObject.toString();

                    /*To invoke json web service to create New Nursery Plantation On Portal*/
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertNurseryPlantation", common.url);
                } else {
                    return "No Nursery Plantation pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        /*After execution of json web service to call for Nursery Inter Cropping Async post*/
        @Override
        protected void onPostExecute(String result) {

            try {
                /*To display message after response from server*/
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_ExistingNurseryPlantationIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        /*call method of get Async Nursery Inter Cropping*/
                        AsyncNurseryInterCropping task = new AsyncNurseryInterCropping();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        /*To display message on screen within process*/
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Updating Nursery Plantation...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="To make web service class to post data of Nursery Inter Cropping">
    private class AsyncNurseryInterCropping extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();
                dba.open();

                /*Get Nursery Inter Cropping for Sync from database*/
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryInterCroppingForSync();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();

                    /*To make json string to post Nursery Inter Cropping*/
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("InterCroppingUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CropVarietyId", insp.get("CropVarietyId"));
                        jsonins.put("Acreage", insp.get("Acreage"));
                        jsonins.put("SeasonId", insp.get("SeasonId"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("CreateBy", insp.get("CreateBy"));
                        jsonins.put("AndroidDate", insp.get("CreateDate"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);

                    sendJSon = jsonObject.toString();

                    /*To invoke json web service to create New Nursery Inter Cropping On Portal*/
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "InsertNurseryInterCropping", common.url);
                } else {
                    return "No Nursery Inter Cropping pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        /*After execution of json web service to call for Farmer Code Async post*/
        @Override
        protected void onPostExecute(String result) {
            try {
                /*To display message after response from server*/
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.Update_ExistingNurseryInterCroppingIsSync();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of get Plantation Code
                        AsyncNurseryPlantationCodeWSCall task = new AsyncNurseryPlantationCodeWSCall();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }
            Dialog.dismiss();
        }

        /*To display message on screen within process*/
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Updating Nursery Inter Cropping...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="AysnTask class to handle Nursery Plantation Code WSCall as separate UI Thread">
    private class AsyncNurseryPlantationCodeWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();

                dba.open();
                //to get payment from database
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryPlantationUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);
                    sendJSon = jsonObject.toString();

                    /*To invoke json web service to Read Nursery Plantation Code*/
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadNurseryPlantationCode", common.url);
                } else {
                    return "No Plantation pending for fetching Plantation Code.";
                }
                //To invoke json web service to create payment


            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updateNurseryPlantationCode(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method to Fetch Fin Year For Inter Cropping
                        AsyncNurseryInterCropFinYearWSCall task = new AsyncNurseryInterCropFinYearWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Nursery Plantation Code Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Nursery Plantation Code..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AysnTask class to handle Nursery Inter Cropping Fin Year WS call as separate UI Thread">
    private class AsyncNurseryInterCropFinYearWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();

                dba.open();
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryInterCropFinUniqueId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();
                        jsonins.put("PlantationUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("CreateBy", userId);
                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);


                    sendJSon = jsonObject.toString();

                    //To invoke json web service to Read Farmer Code
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "ReadNurseryInterCropFinYear", common.url);
                } else {
                    return "No Nursery Inter Cropping pending for fetching Nursery Inter Cropping Code.";
                }
            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR:")) {
                    if (responseJSON.contains("[")) {
                        JSONArray jsonArray = new JSONArray(responseJSON);
                        dba.open();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            dba.updateNurseryInterCropFinYear(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"));
                        }
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of Sync All Attachments
                        Async_AllAttachments_WSCall task = new Async_AllAttachments_WSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Financial Year Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Financial Year..");
            Dialog.setCancelable(false);
            Dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //Async class to handle Defect WS call as separate UI Thread
    private class AsyncDefectWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadDefect", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertDefect(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncAgronomistPendingJobCardWSCall task = new AsyncAgronomistPendingJobCardWSCall();
                        task.execute();

                    }
                } else {
                    common.showAlert(ActivityHome.this, result, true);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Defect Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Defect..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Agronomist Pending JobCard for Confirmation WS call as separate UI Thread
    private class AsyncAgronomistPendingJobCardWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("GetAgronomistPendingJobCard", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    dba.deletePendingJobCard();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPendingJobCard(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"), jsonArray.getJSONObject(i).getString("L"), jsonArray.getJSONObject(i).getString("M"), jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i).getString("O"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        AsyncServiceProviderPendingJobCardWSCall task = new AsyncServiceProviderPendingJobCardWSCall();
                        task.execute();
                    }


                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Agronomist Pending JobCard Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Agronomist Pending JobCard..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //Async class to handle Service Provider Pending JobCard for Confirmation WS call as separate UI Thread
    private class AsyncServiceProviderPendingJobCardWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("GetServiceProviderPendingJobCard", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPendingJobCard(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"), jsonArray.getJSONObject(i).getString("D"), jsonArray.getJSONObject(i).getString("E"), jsonArray.getJSONObject(i).getString("F"), jsonArray.getJSONObject(i).getString("G"), jsonArray.getJSONObject(i).getString("H"), jsonArray.getJSONObject(i).getString("I"), jsonArray.getJSONObject(i).getString("J"), jsonArray.getJSONObject(i).getString("K"), jsonArray.getJSONObject(i).getString("L"), jsonArray.getJSONObject(i).getString("M"), jsonArray.getJSONObject(i).getString("N"), jsonArray.getJSONObject(i).getString("O"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        AsyncPlantingSystemWSCall task = new AsyncPlantingSystemWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Service Provider Pending JobCard Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Service Provider Pending JobCard..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //<editor-fold desc="AsyncTask class to handle PlantingSystem WS call as separate UI Thread">
    private class AsyncPlantingSystemWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadPlantingSystem", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlantingSystem(jsonArray.getJSONObject(i).getString("A"), jsonArray.getJSONObject(i).getString("B"), jsonArray.getJSONObject(i).getString("C"));
                    }
                    dba.close();
                    if (!syncFrom.equalsIgnoreCase("transactions")) {
                        if (common.isConnected()) {
                            AsyncServerFarmerDetailsWSCall task = new AsyncServerFarmerDetailsWSCall();
                            task.execute();
                        }
                    } else
                        common.showAlert(ActivityHome.this, curusrlang.equalsIgnoreCase("en") ? "Synchronization completed successfully." : "सिंक्रनाइज़ेशन सफलतापूर्वक पूरा हुआ।", false);

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "PlantingSystem Downloading failed: " + e.toString(), true);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading PlantingSystem..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>

    //Async class to handle ServerFarmerDetails WS call as separate UI Thread
    private class AsyncServerFarmerDetailsWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmers", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUpdateServerFarmer(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("FarmerCode"), jsonArray.getJSONObject(i).getString("EducationLevelId"), jsonArray.getJSONObject(i).getString("FarmerTypeId"), jsonArray.getJSONObject(i).getString("Salutation"), jsonArray.getJSONObject(i).getString("FirstName"), jsonArray.getJSONObject(i).getString("MiddleName"), jsonArray.getJSONObject(i).getString("LastName"), jsonArray.getJSONObject(i).getString("FatherSalutation"), jsonArray.getJSONObject(i).getString("FatherFirstName"), jsonArray.getJSONObject(i).getString("FatherMiddleName"), jsonArray.getJSONObject(i).getString("FatherLastName"), jsonArray.getJSONObject(i).getString("EmailId"), jsonArray.getJSONObject(i).getString("Mobile"), jsonArray.getJSONObject(i).getString("Mobile1"), jsonArray.getJSONObject(i).getString("Mobile2"), jsonArray.getJSONObject(i).getString("BirthDate"), jsonArray.getJSONObject(i).getString("Gender"), jsonArray.getJSONObject(i).getString("BankAccountNo"), jsonArray.getJSONObject(i).getString("IFSCCode"), jsonArray.getJSONObject(i).getString("TotalAcreage"), jsonArray.getJSONObject(i).getString("FSSAINumber"), jsonArray.getJSONObject(i).getString("RegistrationDate"), jsonArray.getJSONObject(i).getString("ExpiryDate"), jsonArray.getJSONObject(i).getString("LanguageId"), jsonArray.getJSONObject(i).getString("SalutationLocal"), jsonArray.getJSONObject(i).getString("FirstNameLocal"), jsonArray.getJSONObject(i).getString("MiddleNameLocal"), jsonArray.getJSONObject(i).getString("LastNameLocal"), jsonArray.getJSONObject(i).getString("FatherSalutationLocal"), jsonArray.getJSONObject(i).getString("FatherFirstNameLocal"), jsonArray.getJSONObject(i).getString("FatherMiddleNameLocal"), jsonArray.getJSONObject(i).getString("FatherLastNameLocal"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerImageWSCall task = new AsyncServerFarmerImageWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerImage WS call as separate UI Thread
    private class AsyncServerFarmerImageWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerImages", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerDocument(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("PhotoType"), jsonArray.getJSONObject(i).getString("PhotoFileName"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerAddressWSCall task = new AsyncServerFarmerAddressWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Images Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Images..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerAddress WS call as separate UI Thread
    private class AsyncServerFarmerAddressWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerAddress", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUpdateServerFarmerAddress(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("Street1"), jsonArray.getJSONObject(i).getString("Street2"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("VillageId"), jsonArray.getJSONObject(i).getString("CityId"), jsonArray.getJSONObject(i).getString("PinCodeId"), jsonArray.getJSONObject(i).getString("AddressType"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerAssignedBlocksWSCall task = new AsyncServerAssignedBlocksWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Address Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Address..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    //Async class to handle ServerAssignedBlocks WS call as separate UI Thread
    private class AsyncServerAssignedBlocksWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadAssignedBlocks", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerOperationalBlockDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockId"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerFamilyWSCall task = new AsyncServerFarmerFamilyWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Operational Blocks Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Operational Blocks..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerFamily WS call as separate UI Thread
    private class AsyncServerFarmerFamilyWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerFamily", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerFamilyDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("MemberName"), jsonArray.getJSONObject(i).getString("Gender"), jsonArray.getJSONObject(i).getString("BirthDate"), jsonArray.getJSONObject(i).getString("RelationshipId"), jsonArray.getJSONObject(i).getString("IsNominee"), jsonArray.getJSONObject(i).getString("NomineePercentage"), flag, jsonArray.getJSONObject(i).getString("MemberNameLocal"));
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerLoanWSCall task = new AsyncServerFarmerLoanWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Family Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Family..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerLoan WS call as separate UI Thread
    private class AsyncServerFarmerLoanWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {

                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerLoan", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerLoanDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("LoanSourceId"), jsonArray.getJSONObject(i).getString("LoanTypeId"), jsonArray.getJSONObject(i).getString("ROIPercentage"), jsonArray.getJSONObject(i).getString("LoanAmount"), jsonArray.getJSONObject(i).getString("BalanceAmount"), jsonArray.getJSONObject(i).getString("Tenure"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerProofWSCall task = new AsyncServerFarmerProofWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Loan Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Loan..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerProof WS call as separate UI Thread
    private class AsyncServerFarmerProofWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerPOAPOI", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerProofDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("POAPOIId"), jsonArray.getJSONObject(i).getString("POAPOINumber"), jsonArray.getJSONObject(i).getString("FileName"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmerAssetWSCall task = new AsyncServerFarmerAssetWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Proof Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Proof..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerAsset WS call as separate UI Thread
    private class AsyncServerFarmerAssetWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmerAsset", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("FarmerUniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmerAssetDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("FarmAssetsId"), jsonArray.getJSONObject(i).getString("FarmAssetsNos"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("FarmerUniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Insert / Update Farmer Other Details In FarmerOtherDetails Table
                        AsyncServerFarmerOtherDetailsWSCall task = new AsyncServerFarmerOtherDetailsWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Asset Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Asset..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmerOtherDetails WS call as separate UI Thread
    private class AsyncServerFarmerOtherDetailsWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadOtherDetails", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUpdateServerFarmerOtherDetails(jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("SoilTypeId"), jsonArray.getJSONObject(i).getString("IrrigationSystemId"), jsonArray.getJSONObject(i).getString("RiverId"), jsonArray.getJSONObject(i).getString("DamId"), jsonArray.getJSONObject(i).getString("WaterSourceId"), jsonArray.getJSONObject(i).getString("ElectricitySourceId"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Update Farmer Sync Status on Portal
                        AsyncFarmerSyncStatus task = new AsyncFarmerSyncStatus();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farmer Other Details Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farmer Other Details..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //To make web service class to update Farmer Sync Status
    private class AsyncFarmerSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmerUniqueId = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getFarmerUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("FarmerUniqueId", insp.get("FarmerUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonFarmerUniqueId.put("Master", array);

                    sendJSon = jsonFarmerUniqueId.toString();

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateFarmerSyncStatus", common.url);
                } else {
                    return "No farm unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Send Farm Block Sync Status
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeleteFarmerSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of Insert / Update Farm Block Detail from Server
                        AsyncServerFarmBlockDetailsWSCall task = new AsyncServerFarmBlockDetailsWSCall();
                        task.execute();
                    }

                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Farmer Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //Async class to handle ServerFarmBlockDetails WS call as separate UI Thread
    private class AsyncServerFarmBlockDetailsWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlock", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUpdateServerFarmBlock(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("FarmerUniqueId"), jsonArray.getJSONObject(i).getString("FarmBlockCode"), jsonArray.getJSONObject(i).getString("LandTypeId"), jsonArray.getJSONObject(i).getString("FPOId"), jsonArray.getJSONObject(i).getString("KhataNo"), jsonArray.getJSONObject(i).getString("KhasraNo"), jsonArray.getJSONObject(i).getString("ContractDate"), jsonArray.getJSONObject(i).getString("Acerage"), jsonArray.getJSONObject(i).getString("SoilTypeId"), jsonArray.getJSONObject(i).getString("ElevationMSL"), jsonArray.getJSONObject(i).getString("SoliPH"), jsonArray.getJSONObject(i).getString("Nitrogen"), jsonArray.getJSONObject(i).getString("Potash"), jsonArray.getJSONObject(i).getString("Phosphorus"), jsonArray.getJSONObject(i).getString("OrganicCarbonPerc"), jsonArray.getJSONObject(i).getString("Magnesium"), jsonArray.getJSONObject(i).getString("Calcium"), jsonArray.getJSONObject(i).getString("ExistingUseId"), jsonArray.getJSONObject(i).getString("CommunityUseId"), jsonArray.getJSONObject(i).getString("ExistingHazardId"), jsonArray.getJSONObject(i).getString("RiverId"), jsonArray.getJSONObject(i).getString("DamId"), jsonArray.getJSONObject(i).getString("IrrigationId"), jsonArray.getJSONObject(i).getString("OverheadTransmission"), jsonArray.getJSONObject(i).getString("LegalDisputeId"), jsonArray.getJSONObject(i).getString("SourceWaterId"), jsonArray.getJSONObject(i).getString("ElectricitySourceId"), jsonArray.getJSONObject(i).getString("DripperSpacing"), jsonArray.getJSONObject(i).getString("DischargeRate"), jsonArray.getJSONObject(i).getString("OwnerName"), jsonArray.getJSONObject(i).getString("OwnerMobile"), jsonArray.getJSONObject(i).getString("JobCardAllowed"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmBlockAddressWSCall task = new AsyncServerFarmBlockAddressWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Blocks..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmBlockAddress WS call as separate UI Thread
    private class AsyncServerFarmBlockAddressWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockAddress", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertUpdateServerFarmerAddress(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("Street1"), jsonArray.getJSONObject(i).getString("Street2"), jsonArray.getJSONObject(i).getString("StateId"), jsonArray.getJSONObject(i).getString("DistrictId"), jsonArray.getJSONObject(i).getString("BlockId"), jsonArray.getJSONObject(i).getString("PanchayatId"), jsonArray.getJSONObject(i).getString("VillageId"), jsonArray.getJSONObject(i).getString("CityId"), jsonArray.getJSONObject(i).getString("PinCodeId"), jsonArray.getJSONObject(i).getString("AddressType"));
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //Async Method to Fetch Farm Coordinates
                        AsyncServerFarmCoordinatesWSCall task = new AsyncServerFarmCoordinatesWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Address Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Block Address..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    //Async class to handle ServerFarmBlock Coordinates WS call as separate UI Thread
    private class AsyncServerFarmCoordinatesWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockCoordinates", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("UniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmCoordinates(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("Latitude"), jsonArray.getJSONObject(i).getString("Longitude"), jsonArray.getJSONObject(i).getString("Accuracy"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("UniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        AsyncServerFarmCroppingPatternWSCall task = new AsyncServerFarmCroppingPatternWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Coordinates Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Coordinates..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmBlock Cropping Pattern WS call as separate UI Thread
    private class AsyncServerFarmCroppingPatternWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockCroppingPattern", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("UniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmCroppings(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("CropId"), jsonArray.getJSONObject(i).getString("CropVarietyId"), jsonArray.getJSONObject(i).getString("SeasonId"), jsonArray.getJSONObject(i).getString("Acreage"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("UniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Insert / Update Farm Characteristic Details In FarmCharacteristic Table
                        AsyncServerFarmCharacteristicWSCall task = new AsyncServerFarmCharacteristicWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Cropping Pattern Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Cropping Pattern..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmBlock Characteristic WS call as separate UI Thread
    private class AsyncServerFarmCharacteristicWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockLandCharacteristic", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("UniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmCharacteristic(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("LandCharacteristicId"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("UniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Insert / Update Farm Issues In FarmBlockLandIssue Table
                        AsyncServerFarmIssueWSCall task = new AsyncServerFarmIssueWSCall();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Characteristic Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Characteristic..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle ServerFarmBlock Issue WS call as separate UI Thread
    private class AsyncServerFarmIssueWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockLandIssue", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    String prevData = "";
                    String flag = "No";
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        if (!prevData.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("UniqueId")))
                            flag = "Yes";
                        dba.insertUpdateServerFarmIssues(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("LandIssueId"), flag);
                        prevData = jsonArray.getJSONObject(i).getString("UniqueId");
                        flag = "No";
                    }
                    dba.close();
                    if (common.isConnected()) {
                        //call method of Update Farm Block Sync Status on Portal
                        AsyncFarmBlockSyncStatus task = new AsyncFarmBlockSyncStatus();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Issue Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Issue..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //To make web service class to update FarmBlock Sync Status
    private class AsyncFarmBlockSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmerUniqueId = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getFarmBlockUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("FarmBlockUniqueId", insp.get("FarmBlockUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonFarmerUniqueId.put("Master", array);

                    sendJSon = jsonFarmerUniqueId.toString();

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateFarmBlockSyncStatus", common.url);
                } else {
                    return "No farmblock unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Set Farm Block Sync Status
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeleteFarmBlockSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        //call method of Insert Plantation  Details In FarmerPlantation Table
                        AsyncServerPlantationWSCall task = new AsyncServerPlantationWSCall();
                        task.execute();
                    }

                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating FarmBlock Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //Async class to handle ServerPlantataion WS call as separate UI Thread
    private class AsyncServerPlantationWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockPlantation", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertPlantationFromServer(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("FarmBlockUniqueId"), jsonArray.getJSONObject(i).getString("PlantTypeId"), jsonArray.getJSONObject(i).getString("ZoneId"), jsonArray.getJSONObject(i).getString("CropVarietyId"), jsonArray.getJSONObject(i).getString("MonthAgeId"), jsonArray.getJSONObject(i).getString("Acreage"), jsonArray.getJSONObject(i).getString("PlantationDate"), jsonArray.getJSONObject(i).getString("PlantingSystemId"), jsonArray.getJSONObject(i).getString("PlantRow"), jsonArray.getJSONObject(i).getString("PlantColumn"), jsonArray.getJSONObject(i).getString("Balance"), jsonArray.getJSONObject(i).getString("TotalPlant"), jsonArray.getJSONObject(i).getString("PlantationCode"), jsonArray.getJSONObject(i).getString("CropId"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        //call method of Insert Inter Cropping  Details In InterCropping Table
                        AsyncServerInterCroppingWSCall task = new AsyncServerInterCroppingWSCall();
                        task.execute();
                    }

                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Plantation Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Block Plantation..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //Async class to handle Server Inter Cropping WS call as separate UI Thread
    private class AsyncServerInterCroppingWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                responseJSON = common.invokeTwinJSONWS("ReadFarmBlockPlantationInterCropping", "action", userId, "userId", syncByRole, "role", "ReadMaster", common.url);

            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (!result.contains("ERROR: ")) {
                    JSONArray jsonArray = new JSONArray(responseJSON);
                    dba.open();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        dba.insertServerInterCropping(jsonArray.getJSONObject(i).getString("UniqueId"), jsonArray.getJSONObject(i).getString("PlantationUniqueId"), jsonArray.getJSONObject(i).getString("CropVarietyId"), jsonArray.getJSONObject(i).getString("SeasonId"), jsonArray.getJSONObject(i).getString("Acreage"), jsonArray.getJSONObject(i).getString("FinancialYear"));
                    }
                    dba.close();

                    if (common.isConnected()) {
                        //call method of Plantation Sync Status  In PlantationSync Table
                        AsyncPlantationSyncStatus task = new AsyncPlantationSyncStatus();
                        task.execute();
                    }
                } else {
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Farm Block Inter Cropping Downloading failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Farm Block Inter Cropping..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //To make web service class to update Plantation Sync Status
    private class AsyncPlantationSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmerUniqueId = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getPlantationUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonFarmerUniqueId.put("Master", array);

                    sendJSon = jsonFarmerUniqueId.toString();

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdatePlantationSyncStatus", common.url);
                } else {
                    return "No plantation unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to set Plantation Sync Status
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeletePlantationSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        AsyncIntercroppingSyncStatus task = new AsyncIntercroppingSyncStatus();
                        task.execute();
                    }

                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Plantation Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //To make web service class to update Intercropping Sync Status
    private class AsyncIntercroppingSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {


                responseJSON = "";

                JSONObject jsonFarmerUniqueId = new JSONObject();
                dba.open();
                //to get Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getIntercroppingUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("PlantationUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonFarmerUniqueId.put("Master", array);

                    sendJSon = jsonFarmerUniqueId.toString();

                    //To invoke json web service to Inter cropping status on portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateInterCroppingSyncStatus", common.url);
                } else {
                    return "No intercropping unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to Update Intercropping Sync Status
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeletePlantationSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        AsyncNurseryPlantationSyncStatus task = new AsyncNurseryPlantationSyncStatus();
                        task.execute();
                    }


                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Intercropping Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //<editor-fold desc="To make web service class to update Nursery Plantation Sync Status">
    private class AsyncNurseryPlantationSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();

                dba.open();
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryPlantationUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("PlantationUniqueId", insp.get("PlantationUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);

                    sendJSon = jsonObject.toString();

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateNurseryPlantationSyncStatus", common.url);
                } else {
                    return "No plantation unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create payment
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeleteNurseryPlantationSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        AsyncNurseryInterCroppingSyncStatus task = new AsyncNurseryInterCroppingSyncStatus();
                        task.execute();
                    }

                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Plantation Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="To make web service class to update Nursery InterCropping Sync Status">
    private class AsyncNurseryInterCroppingSyncStatus extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = "";
                JSONObject jsonObject = new JSONObject();

                dba.open();
                ArrayList<HashMap<String, String>> insmast = dba.getNurseryInterCroppingUniqueUpdateId();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("InterCroppingUniqueId", insp.get("InterCroppingUniqueId"));
                        jsonins.put("CreateBy", userId);

                        array.put(jsonins);
                    }
                    jsonObject.put("Master", array);

                    sendJSon = jsonObject.toString();

                    //To invoke json web service to Inter cropping status on portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "UpdateNurseryInterCroppingSyncStatus", common.url);
                } else {
                    return "No Nursery InterCropping unique id pending to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                return "ERROR: " + "Unable to get response from server.";
            }
        }

        //After execution of json web service to create payment
        @Override
        protected void onPostExecute(String result) {

            try {
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (responseJSON.equalsIgnoreCase("success")) {
                        dba.open();
                        dba.DeleteNurseryInterCroppingSyncTable();
                        dba.close();
                    }
                    if (common.isConnected()) {
                        AsyncServerJobCardDetailWSCall task = new AsyncServerJobCardDetailWSCall();
                        task.execute();
                    }


                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);
                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Updating Intercropping Sync Status...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }
    //</editor-fold>

    private class AsyncDeliveryWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            try {
                responseJSON = "";

                JSONObject jsonMaster = new JSONObject();
                dba.open();
                sendJSon = "";
                //to get New Farmers from database
                ArrayList<HashMap<String, String>> insmast = dba.getUnSyncDeliveryMasterDetails();
                dba.close();
                if (insmast != null && insmast.size() > 0) {
                    JSONArray array = new JSONArray();
                    //To make json string to post New Farmers
                    for (HashMap<String, String> insp : insmast) {
                        JSONObject jsonins = new JSONObject();

                        jsonins.put("FarmerNurseryType", insp.get("FarmerNurseryType"));
                        jsonins.put("FarmerNurseryId", insp.get("FarmerNurseryId"));
                        jsonins.put("DispatchId", insp.get("DispatchId"));
                        jsonins.put("ShortCloseReasonId", insp.get("ShortCloseReasonId"));
                        jsonins.put("UniqueId", insp.get("UniqueId"));
                        jsonins.put("AndroidDate", insp.get("AndroidDate"));
                        jsonins.put("PaymentModeId", insp.get("PaymentModeId"));
                        jsonins.put("PaymentAmount", insp.get("PaymentAmount"));
                        jsonins.put("Remarks", insp.get("Remarks"));
                        jsonins.put("PaymentFileName", insp.get("PaymentFileName"));
                        jsonins.put("CreateBy", insp.get("CreateBy"));
                        jsonins.put("Latitude", insp.get("Latitude"));
                        jsonins.put("Longitude", insp.get("Longitude"));
                        jsonins.put("Accuracy", insp.get("Accuracy"));
                        jsonins.put("CreateIP", common.getDeviceIPAddress(true));
                        jsonins.put("CreateMachine", common.getIMEI());
                        array.put(jsonins);
                    }
                    jsonMaster.put("Master", array);

                    JSONObject jsonDetail = new JSONObject();
                    //To get Assigned Block details from database
                    dba.open();
                    ArrayList<HashMap<String, String>> insdet = dba.getUnSyncDeliveryDetails();
                    dba.close();
                    if (insdet != null && insdet.size() > 0) {
                        //To make json string to post Assigned Block details
                        JSONArray arraydet = new JSONArray();
                        for (HashMap<String, String> insd : insdet) {
                            JSONObject jsondet = new JSONObject();
                            jsondet.put("UniqueId", insd.get("UniqueId"));
                            jsondet.put("BookingCollectionId", insd.get("BookingId"));
                            jsondet.put("PolyBagTypeId", insd.get("PolyBagTypeId"));
                            jsondet.put("Quantity", insd.get("Quantity"));
                            jsondet.put("Rate", insd.get("Rate"));
                            jsondet.put("Amount", insd.get("Amount"));
                            arraydet.put(jsondet);
                        }
                        jsonDetail.put("Detail", arraydet);
                    }

                    sendJSon = jsonMaster + "~" + jsonDetail;

                    //To invoke json web service to create New Farmers On Portal
                    responseJSON = common.invokeJSONWS(sendJSon, "json", "SubmitDelivery", common.url);
                } else {
                    return "No Pending Dispatch remaining to be send.";
                }
                return responseJSON;
            } catch (Exception e) {
                // TODO: handle exception
                //return "ERROR: " + "Unable to get response from server.";
                return "ERROR: " + e.getMessage();
            }
        }

        //After execution of json web service to create New Farmer On Portal
        @Override
        protected void onPostExecute(String result) {
            try {
                dba.open();
                dba.clearDeliveryDetailsForDispatch("");
                dba.clearPaymentAgainstDispatchDelivery("");
                dba.close();
                //To display message after response from server
                if (!result.contains("ERROR")) {
                    if (common.isConnected()) {
                        AsyncPendingDispatchesForDeliveryWSCall task = new AsyncPendingDispatchesForDeliveryWSCall();
                        task.execute();
                    }
                } else {
                    if (result.contains("null"))
                        result = "Server not responding.";
                    common.showAlert(ActivityHome.this, result, false);

                }
            } catch (Exception e) {
                common.showAlert(ActivityHome.this, e.getMessage(), false);
                //common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
            }

            Dialog.dismiss();
        }

        //To display message on screen within process
        @Override
        protected void onPreExecute() {

            Dialog.setMessage("Posting Pending Dispatches...");
            Dialog.setCancelable(false);
            Dialog.show();
        }
    }

    //<editor-fold desc="AsyncTask class to handle Pending Dispatch for Delivery WS call as separate UI Thread">
    private class AsyncPendingDispatchesForDeliveryWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            if (userRole.contains("Farmer")
                    || userRole.contains("Mini Nursery User")
                    || userRole.contains("Service Provider")) {
                try {
                    responseJSON = "";
                    responseJSON = common.invokeTwinJSONWS("GetPendingDispatchForDelivery",
                            "action", userId, "userId", userRole.replace(',', ' ').trim(),
                            "userRole",
                            "GetPendingDispatchForDelivery", common.url);
                } catch (SocketTimeoutException e) {
                    return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
                } catch (final Exception e) {
                    e.printStackTrace();
                    //return "ERROR: " + "Unable to fetch response from server.";
                    return "ERROR: " + e.getMessage();
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (userRole.contains("Farmer")
                    || userRole.contains("Mini Nursery User")
                    || userRole.contains("Service Provider")) {
                try {
                    if (!result.contains("ERROR: ")) {
                        JSONArray jsonArrayMst = new JSONArray(responseJSON.split("~")[0]);
                        JSONArray jsonArrayDet = new JSONArray(responseJSON.split("~")[1]);
                        JSONArray jsonArrayBal = new JSONArray((responseJSON.split("~")[2]));
                        dba.open();
                        dba.clearPendingDispatchForDelivery();
                        for (int i = 0; i < jsonArrayMst.length(); i++) {
                            dba.insertPendingDispatchForDelivery(
                                    String.valueOf(jsonArrayMst.getJSONObject(i).getInt("Id")),
                                    jsonArrayMst.getJSONObject(i).getString("Code"),
                                    jsonArrayMst.getJSONObject(i).getString("DispatchForId"),
                                    jsonArrayMst.getJSONObject(i).getString("DispatchForName"),
                                    jsonArrayMst.getJSONObject(i).getString("DispatchForMobile"),
                                    jsonArrayMst.getJSONObject(i).getString("VehicleNo"),
                                    jsonArrayMst.getJSONObject(i).getString("DriverName"),
                                    jsonArrayMst.getJSONObject(i).getString("DriverMobileNo"));
                        }

                        dba.clearPendingDispatchDetailForDelivery();
                        for (int i = 0; i < jsonArrayDet.length(); i++) {
                            dba.insertPendingDispatchDetailForDelivery(
                                    String.valueOf(jsonArrayDet.getJSONObject(i).getInt("DispatchId")),
                                    String.valueOf(jsonArrayDet.getJSONObject(i).getInt("BookingId")),
                                    jsonArrayDet.getJSONObject(i).getString("Rate"),
                                    jsonArrayDet.getJSONObject(i).getString("PolybagTypeId"),
                                    jsonArrayDet.getJSONObject(i).getString("PolybagTitle"),
                                    Integer.valueOf(jsonArrayDet.getJSONObject(i).getString("Quantity")));
                        }

                        dba.clearBalanceDetailsForFarmerNursery();
                        for (int i = 0; i < jsonArrayBal.length(); i++) {
                            dba.insertBalanceDetailsForFarmerNursery(
                                    jsonArrayBal.getJSONObject(i).getString("FarmerNursery"),
                                    jsonArrayBal.getJSONObject(i).getString("FarmerNurseryId"),
                                    jsonArrayBal.getJSONObject(i).getString("BalanceAmount"));
                        }
                        dba.close();
                        if (common.isConnected()) {
                            AsyncServerJobCardDetailWSCall task = new AsyncServerJobCardDetailWSCall();
                            task.execute();
                        }
                    } else {
                        common.showAlert(ActivityHome.this, result, true);
                    }
                } catch (Exception e) {
                    common.showAlert(ActivityHome.this, "Pending Dispatch Downloading failed: " + e
                            .toString(), true);
                }
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Downloading Pending Dispatch Details ..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
    //</editor-fold>


    //Async class to send logout details to Portal and logout user from Android on receiving Success from Portal
    private class AsyncLogOutWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityHome.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                String seedValue = "lpsmartfarm";
                HashMap<String, String> user = session.getLoginUserDetails();
                //Creation of JSON string
                JSONObject json = new JSONObject();
                json.put("username", Encrypt(user.get(UserSessionManager.KEY_USERNAME), seedValue));
                json.put("password", Encrypt(user.get(UserSessionManager.KEY_PWD), seedValue));
                json.put("imei", imei);
                json.put("ipAddr", common.getDeviceIPAddress(true));
                json.put("version", dba.getVersion());
                String JSONStr = json.toString();
                responseJSON = "";

                //Code to pass json to async method and store response received from server in responseJSON
                responseJSON = common.invokeJSONWS(JSONStr, "json", "LogoutUserAndroid", common.url);
            } catch (SocketTimeoutException e) {
                return "ERROR: TimeOut Exception. Either Server is busy or Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return "ERROR: " + "Unable to fetch response from server.";
            }
            return responseJSON;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.contains("success")) {
                    //Code to remove user data from session
                    session.logoutUser();
                    dba.open();
                    //Code to delete all data from tables on logout
                    dba.deleteTransactionData();
                    dba.close();

                    File dir = new File(Environment.getExternalStorageDirectory() + "/" + "LPSMARTFARM");
                    deleteRecursive(dir);

                    common.showToast("You have been Logged Out successfully!", 5, 3);
                    finish();
                } else {
                    common.showAlert(ActivityHome.this, "Unable to fetch response from server.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                common.showAlert(ActivityHome.this, "Log out failed: " + e.toString(), false);
            }
            Dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Logging out ..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }
}
