package lateralpraxis.lpsmartfarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by LPNOIDA01 on 9/26/2017.
 */

public class UserSessionManager {

    public static final String PREFER_NAME = "MyPrefsFile";
    public static final String KEY_ID = "sp_id";
    public static final String KEY_CODE = "sp_code";
    public static final String KEY_FULLNAME = "sp_fullname";
    public static final String KEY_MEMBERSHIPID = "sp_membershipid";
    public static final String KEY_USERTYPE = "sp_usertype";
    public static final String KEY_USERROLES = "spuser_roles";
    public static final String KEY_IMEI = "spimei";
    public static final String KEY_USERNAME = "sp_username";
    public static final String KEY_PWD = "sp_pwd";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    //current activated lang
    public static final String KEY_PREFLANG = "pref_lang";
    //all languages options from server
    public static final String KEY_OPTLANG = "opt_lang";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void createUserLoginSession(String id, String code, String userName, String fullName, String roles,
                                       String imei, String membershipId, String userType, String password,String optionLang) {

        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_CODE, code);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_MEMBERSHIPID, membershipId);
        editor.putString(KEY_USERTYPE, userType);
        editor.putString(KEY_USERROLES, roles);
        editor.putString(KEY_IMEI, imei);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_PWD, password);
        editor.putString(KEY_PREFLANG, "en");
        editor.putString(KEY_OPTLANG, optionLang);

        editor.commit();
    }


    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        } else {
            return false;
        }
    }

    public void updatePrefLanguage(String lang){
        editor.putString(KEY_PREFLANG, lang);
        editor.commit();

    }

    public String getDefaultLang(){
        return pref.getString(KEY_PREFLANG, null);
    }

    public boolean checkLoginShowHome() {
        if (this.isUserLoggedIn()) {
            Intent i = new Intent(_context, ActivityHome.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        } else {
            return false;
        }
    }

    public void updatePassword(String pwd) {
        editor.putString(KEY_PWD, pwd);
        editor.commit();
    }

    public HashMap<String, String> getLoginUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_PWD, pref.getString(KEY_PWD, null));
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, null));
        user.put(KEY_USERROLES, pref.getString(KEY_USERROLES, null));
        user.put(KEY_IMEI, pref.getString(KEY_IMEI, null));
        user.put(KEY_CODE, pref.getString(KEY_CODE, null));
        user.put(KEY_MEMBERSHIPID, pref.getString(KEY_MEMBERSHIPID, null));
        user.put(KEY_USERTYPE, pref.getString(KEY_USERTYPE, null));
        user.put(KEY_OPTLANG, pref.getString(KEY_OPTLANG, null));


        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
