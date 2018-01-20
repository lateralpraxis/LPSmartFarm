package lateralpraxis.lpsmartfarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends Activity {

    //Start Variable Declaration
    private static String responseJSON;
    private static int SPLASH_TIME_OUT = 2000;
    final Context context = this;

    //Start Control Declaration
    Button btnLogin;
    EditText etUsername, etPassword;
    TextView lnForgotPassword;
    String imei;
    Common common;
    UserSessionManager session;

    // Start private variables declaration
    private CheckBox ckShowPass;
    private String username = "", password = "";
    private String origusername = "", origpassword = "";
    private String JSONStr;
    private DatabaseAdapter databaseAdapter;
    private String TAG = "tag";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Creating Instance of Classes Declared
        common = new Common(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());
        databaseAdapter = new DatabaseAdapter(getApplicationContext());

        //Code to fetch IMEI Number
        imei = common.getIMEI();

        //Code to check for active session
        if (session.checkLoginShowHome()) {
            finish();
        }

        //Code to find controls
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        ckShowPass = (CheckBox) findViewById(R.id.ckShowPass);
        etPassword.getParent().getParent().requestChildFocus(etPassword, etPassword);
        lnForgotPassword = (TextView) findViewById(R.id.linkForgotPassword);

        lnForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(context, ActivityChangePassword.class);
                startActivity(intent);
                finish();*/
            }
        });

        //Button login click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Validate mandatory options
                if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
                    etUsername.setError("Please Enter Username");
                    etUsername.requestFocus();
                } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
                    etPassword.setError("Please Enter Password");
                    etPassword.requestFocus();
                } else {
                    //Encrypt User Name and Password
                    String seedValue = "lpsmartfarm";
                    username = etUsername.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    origusername = etUsername.getText().toString().trim();
                    origpassword = etPassword.getText().toString().trim();
                    try {
                        username = Encrypt(username, seedValue);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        //e1.printStackTrace();
                    }
                    try {
                        password = Encrypt(password, seedValue);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        //e1.printStackTrace();
                    }
                    try {
                        //Call Async activity to send json to server for Login Validation
                        if (common.isConnected()) {
                           /* FetchExternalIP task = new FetchExternalIP();
                            task.execute();*/
                            JSONObject json = new JSONObject();
                            try {
                                json.put("username", username);
                                json.put("password", password);
                                json.put("imei", imei);
                                json.put("ipAddr", common.getDeviceIPAddress(true));
                                json.put("version", databaseAdapter.getVersion());
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block

                                databaseAdapter.insertExceptions(e.getMessage(), "ActivityLogin.java", e.getMessage());
                            }

                            JSONStr = json.toString();
                            AsyncLoginWSCall task = new AsyncLoginWSCall();
                            task.execute();
                        } else {
                            databaseAdapter.insertExceptions("Unable to connect to Internet !", "ActivityLogin.java", "onCreate()");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        common.showToast("Unable to fetch response from server.");
                        databaseAdapter.insertExceptions("Unable to fetch response from server.", "ActivityLogin.java", "onCreate()");
                    }
                }
            }
        });

        //Text change event for User Name Edit Text
        etUsername.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    etUsername.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Text change event for Password Edit Text
        etPassword.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    etPassword.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        //Check box on check event to display password
        ckShowPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start, end;

                if (!isChecked) {
                    start = etPassword.getSelectionStart();
                    end = etPassword.getSelectionEnd();
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ;
                    etPassword.setSelection(start, end);
                } else {
                    start = etPassword.getSelectionStart();
                    end = etPassword.getSelectionEnd();
                    etPassword.setTransformationMethod(null);
                    etPassword.setSelection(start, end);
                }

            }
        });
    }

    //Async Class to send Credentials
    private class AsyncLoginWSCall extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected String doInBackground(String... params) {
            try {
                //Sending JSON
                responseJSON = common.invokeJSONWS(JSONStr, "json", "GetUserDetails", common.url);
            } catch (SocketTimeoutException e) {
                databaseAdapter.insertExceptions("TimeOut Exception. Internet is slow", "ActivityLogin.java", "AsyncLoginWSCall");
                return "ERROR: TimeOut Exception. Internet is slow";
            } catch (final Exception e) {
                // TODO: handle exception
                databaseAdapter.insertExceptions(e.getMessage(), "ActivityLogin.java", "AsyncLoginWSCall");
                return "ERROR: " + e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            try {
                //Checking result fetched from server
                if (!result.contains("ERROR: ")) {
                    //Check response received in json and display specific message
                    if (responseJSON.toLowerCase(Locale.US).contains("DEFAULT_LOGIN_FAILED".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "Invalid Username or Password", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("NO_USER".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "There is no user in the system as - " + etUsername.getText().toString(), false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("BARRED".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "Your account has been barred by the Administrator.", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("LOCKED".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "Your account has been locked out because " +
                                "you have exceeded the maximum number of incorrect login attempts. " +
                                "Please contact the System Admin to " +
                                "unblock your account.", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("LOGINFAILED".toLowerCase(Locale.US))) {
                        String[] resp = responseJSON.split("~");
                        common.showAlert(LoginActivity.this, "Invalid password. " +
                                "Password is case-sensitive. " +
                                "Access to the system will be disabled after " + resp[1] + " " +
                                "consecutive wrong attempts.\n" +
                                "Number of Attempts remaining: " + resp[2], false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("LoginFailed".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "Invalid Username or Password", false);
                    } else if (responseJSON.toLowerCase(Locale.US).contains("Error".toLowerCase(Locale.US))) {
                        common.showAlert(LoginActivity.this, "Error in validating your credentials!", false);
                    } else if (responseJSON.contains("NOVERSION")) {
                        common.showAlert(LoginActivity.this, "Application is running an older version. Please install latest version!", false);
                    } else if (responseJSON.contains("imeiexists")) {
                        common.showAlert(LoginActivity.this, "User is already logged in from some other device!", false);
                    } else if (responseJSON.contains("nouser")) {
                        common.showAlert(LoginActivity.this, "Logged in user is not Valid user!", false);
                    } else if (responseJSON.contains("notdeviceuser")) {
                        common.showAlert(LoginActivity.this, "User is not allowed to do data entry from device!", false);
                    } else {
                        //If validation is complete fetch user details from JOSN
                        JSONObject reader = new JSONObject(responseJSON);
                        String id = reader.getString("Id");
                        String code = reader.getString("Code");
                        String name = reader.getString("Name");
                        String membershipId = reader.getString("MembershipId");
                        String userType = "";
                        String role = reader.getString("Role");
                        String passExpired = reader.getString("PassExpired");
                        /*if(role.contains("Service Provider")) {*/
                            //Code to set user details in session inside UserSessionManager dictionary
                            session.createUserLoginSession(id, code, origusername, name, role, imei, membershipId, userType, origpassword,"English,Hindi,Gujarati,Marathi");
                            //If Password is expired display change password intent
                            if (passExpired.toLowerCase(Locale.US).equals("yes")) {
                                Intent intent = new Intent(context, ActivityChangePassword.class);
                                intent.putExtra("fromwhere", "login");
                                startActivity(intent);
                                finish();
                            } else {
                                //Display homescreen intent
                                Intent intent = new Intent(context, ActivityHome.class);
                                startActivity(intent);
                                finish();
                            }
                       /* }
                        else
                        {
                            common.showAlert(LoginActivity.this, "This application is only for Service Provider role!", false);
                        }*/
                    }
                } else {
                    //In case no response is received display message
                    common.showAlert(LoginActivity.this, " Unable to fetch response from server.", false);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //Store error occurred in exception table
                databaseAdapter.insertExceptions(e.getMessage(), "LoginActivity.java", "AsyncLoginWSCall");
                //Display error message
                common.showAlert(LoginActivity.this, "Error: " + e.getMessage(), false);
            }
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

    // When press back button go to home screen
    @Override
    public void onBackPressed() {
        common.BackPressed(this);
    }
}
