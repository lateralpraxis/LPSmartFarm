package lateralpraxis.lpsmartfarm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ActivityChangePassword extends Activity {
    //***************Start of Variable Declaration****************************//
    private static String responseJSON;
    private String JSONStr;
    private String imei;
    final Context context = this;
    //***************End of Variable Declaration****************************//

    //***************Start of Class Declaration****************************///
    private Common common;
    private UserSessionManager session;
    private DatabaseAdapter db;
    //***************End of Class Declaration****************************//

    //***************Start of Control Declaration****************************//
    TextView tvInstructions, tvPasswordExpired;
    EditText etOldPassword, etNewPassword, etConfirmPassword;
    CheckBox ckShowPass;
    Button btnChangePassword, btnLogout;
    //***************End of Control Declaration****************************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Creating instance of Classes Declared
        common = new Common(this);
        db = new DatabaseAdapter(this);
        session = new UserSessionManager(this);

        //Code to find controls in layout
        tvInstructions = (TextView) findViewById(R.id.tvInstructions);
        ckShowPass = (CheckBox) findViewById(R.id.ckShowPass);
        tvPasswordExpired = (TextView) findViewById(R.id.tvPasswordExpired);
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        db.openR();
       /* if(db.IsTempLogoutAllowed() && db.IsLogoutAllowed())
            btnLogout.setVisibility(View.VISIBLE);
        else*/
        btnLogout.setVisibility(View.GONE);

        //Code to fetch IMEI Number
        imei = common.getIMEI();

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("fromwhere").equals("home")) {
                tvPasswordExpired.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
            } else
                tvPasswordExpired.setVisibility(View.VISIBLE);
        }

        tvInstructions.setText(Html.fromHtml(
                "&#8226; Password must be at least 8 characters long<br>" +
                        "&#8226; Password must contain two lower case alphabets<br>" +
                        "&#8226; Password must contain two upper case alphabets<br>" +
                        "&#8226; Password must contain a numeric character<br>" +
                        "&#8226; Password must contain a special character<br>" +
                        /*						"&#8226; Password must not repeat a character more than half the length of the password<br>" +*/
                        "&#8226; Both passwords must match<br>"));

        btnChangePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (String.valueOf(etOldPassword.getText()).trim().length() == 0)
                    common.showToast("Old Password is mandatory.");
                else if (String.valueOf(etNewPassword.getText()).trim().length() == 0)
                    common.showToast("New Password is mandatory.");
                else if (etConfirmPassword.getText().toString().trim().length() == 0)
                    common.showToast("Confirm Password is mandatory.");
                else if ((etOldPassword.getText().toString().trim().equals(String.valueOf(etNewPassword.getText()).trim())))
                    common.showToast("Old and New Password cannot be same.");
                else if (etNewPassword.getText().toString().trim().length() < 8)
                    common.showToast("New Password should be at least 8 characters long.");
                else if (etConfirmPassword.getText().toString().trim().length() < 8)
                    common.showToast("Confirm Password should be at least 8 characters long.");
                else if (!(etConfirmPassword.getText().toString().trim().equals(String.valueOf(etNewPassword.getText()).trim())))
                    common.showToast("New and Confirm Password should match.");
                else {
                    HashMap<String, String> user = session.getLoginUserDetails();

                    try {
                        if (common.isConnected()) {
                            db.openR();
                            JSONObject json = new JSONObject();
                            try {
                                String seedValue = "lpsmartfarm";
                                json.put("username", Encrypt(user.get(UserSessionManager.KEY_USERNAME), seedValue));
                                json.put("oldPassword", Encrypt(String.valueOf(etOldPassword.getText()).trim(), seedValue));
                                json.put("newPassword", Encrypt(String.valueOf(etNewPassword.getText()).trim(), seedValue));
                                json.put("imei", common.getIMEI());
                                json.put("version", db.getVersion());
                                json.put("ipAddr", common.getDeviceIPAddress(true));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                //e.printStackTrace();

                            }

                            JSONStr = json.toString();

                            AsyncChangePasswordWSCall task = new AsyncChangePasswordWSCall();
                            task.execute();

                        } else {

                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        common.showToast(e.toString());
                    }

                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //"Log Out",
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityChangePassword.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to Log Out ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                db.openR();
                                //First check if logout is Allowed
                                /*if(!db.IsTempLogoutAllowed())
                                    common.showAlert(ActivityChangePassword.this, "You cannot be logged out! There are some files which have been saved for "+strMonthYear+". Either the files need to be submitted and synchronized or deleted.",false);*/
                               /* else if(db.IsLogoutAllowed())
                                {*/
                                AsyncLogOutWSCall task = new AsyncLogOutWSCall();
                                task.execute();
                                /*}*/
                              /*  else
                                    common.showAlert(ActivityChangePassword.this, "There are Document(s) pending to be synced with the server. Kindly Sync the pending document(s).",false);*/

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        ckShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int oldStart, oldEnd;
                int newStart, newEnd;
                int confirmStart, confirmEnd;

                if (!isChecked) {
                    oldStart = etOldPassword.getSelectionStart();
                    oldEnd = etOldPassword.getSelectionEnd();
                    etOldPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ;
                    etOldPassword.setSelection(oldStart, oldEnd);

                    newStart = etNewPassword.getSelectionStart();
                    newEnd = etNewPassword.getSelectionEnd();
                    etNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ;
                    etNewPassword.setSelection(newStart, newEnd);

                    confirmStart = etConfirmPassword.getSelectionStart();
                    confirmEnd = etConfirmPassword.getSelectionEnd();
                    etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ;
                    etConfirmPassword.setSelection(confirmStart, confirmEnd);

                } else {
                    oldStart = etOldPassword.getSelectionStart();
                    oldEnd = etOldPassword.getSelectionEnd();
                    etOldPassword.setTransformationMethod(null);
                    etOldPassword.setSelection(oldStart, oldEnd);

                    newStart = etNewPassword.getSelectionStart();
                    newEnd = etNewPassword.getSelectionEnd();
                    etNewPassword.setTransformationMethod(null);
                    etNewPassword.setSelection(newStart, newEnd);

                    confirmStart = etConfirmPassword.getSelectionStart();
                    confirmEnd = etConfirmPassword.getSelectionEnd();
                    etConfirmPassword.setTransformationMethod(null);
                    etConfirmPassword.setSelection(confirmStart, confirmEnd);
                }

            }
        });
    }

    //*********Encryption Method***************************//
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


    @Override
    public void onBackPressed() {
        Intent homeScreenIntent = new Intent(this, ActivityHome.class);
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeScreenIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.basemenu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, want to Log Out ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if(common.isConnected())
                                {
                                    db.open();
                                    if(db.IslogoutAllowed())
                                    {
                                        db.deleteAllData();
                                        db.close();
                                        AsyncLogOutWSCall task = new AsyncLogOutWSCall();
                                        task.execute();
                                    }
                                    else{
                                        common.showToast("Unable to logout as data is pending for synchronize.");
                                    }
                                }
                                else{
                                    common.showToast("Unable to connect to Internet !");
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void actionBarSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setIcon(R.mipmap.ic_launcher);
            ab.setHomeButtonEnabled(true);
        }
    }

    //Method to delete files from Directory
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private class AsyncChangePasswordWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityChangePassword.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                responseJSON = common.invokeJSONWS(JSONStr, "json", "ChangeUserPassword", common.url);
            } catch (SocketTimeoutException e) {

                return "ERROR: TimeOut Exception. Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                //e.printStackTrace();

                return "ERROR: " + e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            try {
                if (!result.contains("ERROR: ")) {
                    if (responseJSON.toLowerCase(Locale.US).contains("LoginFailed".toLowerCase(Locale.US))) {
                        common.showAlert(ActivityChangePassword.this, "Invalid Old Password", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("REPEAT_PASSWORD".toLowerCase(Locale.US))) {
                        common.showAlert(ActivityChangePassword.this, "You cannot repeat last " + responseJSON.split("~")[1] + " passwords.", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("SHOW_RULES".toLowerCase(Locale.US))) {
                        common.showAlert(ActivityChangePassword.this, "Your password is not as per required rule.", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("SUCCESS".toLowerCase(Locale.US))) {
                        session.updatePassword(etNewPassword.getText().toString().trim());
                        etOldPassword.setText("");
                        etNewPassword.setText("");
                        etConfirmPassword.setText("");
                        common.showToast("Password Changed Successfully!");
                        Intent homeScreenIntent = new Intent(ActivityChangePassword.this, ActivityHome.class);
                        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeScreenIntent);
                    }
                } else {
                    common.showAlert(ActivityChangePassword.this, "Unable to fetch response from server.", false);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                common.showAlert(ActivityChangePassword.this, "Error: " + e.getMessage(), false);
            }
        }

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Changing your password..");
            Dialog.setCancelable(false);
            Dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    //AysnTask class to send logout details to Portal and logout user from Android on receiving Success from Portal
    private class AsyncLogOutWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(ActivityChangePassword.this);

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
                json.put("version", db.getVersion());
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
                    db.open();
                    //Code to delete all data from tables on logout
                    db.deleteTransactionData();
                    db.close();

                    File dir = new File(Environment.getExternalStorageDirectory() + "/" + "LPSMARTFARM");
                    deleteRecursive(dir);

                    common.showToast("You have been Logged Out successfully!",5,3);
                    finish();
                } else {
                    common.showAlert(ActivityChangePassword.this, "Unable to fetch response from server.", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                common.showAlert(ActivityChangePassword.this, "Log out failed: " + e.toString(), false);
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
