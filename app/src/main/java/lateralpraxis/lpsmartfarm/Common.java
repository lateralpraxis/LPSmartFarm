package lateralpraxis.lpsmartfarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by LPNOIDA01 on 9/26/2017.
 */
public class Common {
    private final static String namespace = "http://tempuri.org/";
    private final static String soap_action = "http://tempuri.org/";

    static Context c;
    static HashMap<String, String> user;
    private static String responseJSON;
    //Amazon
    ///public final String domain = "http://54.148.76.44";
    //QA
    //public final String domain = "http://122.180.148.98:81";
    //Development
    //public final String domain = "http://122.180.148.98";
    //public final String domain = "http://www.lateralpraxis.co.in:81";
    //public final String domain = "http://10.0.2.2";
    //Mumbai office
    public final String domain = "http://114.143.190.166";
    public final String url = domain + "/FarmArt/Shared/Services/Android.asmx";
    public String log = "farmart_app";
    public String deviceIP = "";
    UserSessionManager session;
    private DatabaseAdapter databaseAdapter;

    /*Constructor*/
    public Common(Context context) {
        c = context;
        session = new UserSessionManager(c);
        databaseAdapter = new DatabaseAdapter(c);
        user = session.getLoginUserDetails();
    }

    /*Get the IP address of the device on which application is running*/
    public static String getDeviceIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /*Check device has Internet connection*/
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else {
            showToast("Unable to connect to Internet !");
            return false;
        }
    }

    //<editor-fold desc="Show Toast message">
    /*Show Toast Message*/
    /* 0 - Error */
    /* 1 - Warning */
    /* 2 - Notice */
    /* 3 - Success */
    public void showToast(String msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    /*Show Toast Message*/
    /* 0 - Error */
    /* 1 - Warning */
    /* 2 - Notice */
    /* 3 - Success */
    public void showToast(String msg, int duration) {
        showToast(msg, duration, 0);
    }

    /*Show Toast Message*/
    /* 0 - Error */
    /* 1 - Warning */
    /* 2 - Notice */
    /* 3 - Success */
    public void showToast(String msg, int duration, int flag) {
        View customToastRoot = null;
        switch (flag) {
            case 0:
                customToastRoot = View.inflate(c, R.layout.custom_error, null);
                break;
            case 1:
                customToastRoot = View.inflate(c, R.layout.custom_warning, null);
                break;
            case 2:
                customToastRoot = View.inflate(c, R.layout.custom_notice, null);
                break;
            case 3:
                customToastRoot = View.inflate(c, R.layout.custom_success, null);
                break;
        }

        Toast customToast = new Toast(c);
        TextView messageText = (TextView) customToastRoot.findViewById(R.id.textView);
        messageText.setText(msg);
        customToast.setView(customToastRoot);
        customToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        customToast.setDuration(duration);
        customToast.show();
    }
    //</editor-fold>


    /*Show the Alert Message*/
    public void showAlert(final Activity activity, String message, final Boolean appClose) {
        showAlert(activity, message, appClose, "Alert");
    }

    /*Show the Alert Message*/
    public void showAlert(final Activity activity, String message, final Boolean appClose, String title) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(title);
        alertbox.setCancelable(false);
        alertbox.setMessage(message);
        alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (appClose)
                    activity.finish();
            }
        });

        alertbox.show();
    }

    //Method to get current date time
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MMM-yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    //alert on back button press.
    public void BackPressed(final Activity act) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
        // set title
        alertDialogBuilder.setTitle("Confirmation");
        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure, want to close LP Smart Farm Application?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        System.out.println("Yes Pressed");
                        dialog.cancel();
                        //act.finish();
                        Intent intent = new Intent(act, ActivityClose.class);
                        //Clear all activities and start new task
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        act.startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        System.out.println("No Pressed");
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void TerminateSession(final Activity act) {
        session.logoutUser();
    }

    public String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void showGPSSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                c.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    //Method to copy db to sd card
    public String copyDBToSDCard(String DBName) throws Exception {
        try {
            InputStream myInput = new FileInputStream(c.getDatabasePath(DBName));

            File sdDir = Environment.getExternalStorageDirectory();

            File file = new File(sdDir + "/" + DBName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    databaseAdapter.insertExceptions(e.getMessage(), "Common.java", "copyDBToSDCard");
                }
            }

            boolean success = true;
            /*if (!dir.exists()) {
                success = dir.mkdir();
			}*/
            if (success) {
                OutputStream myOutput = new FileOutputStream(sdDir + "/" + DBName);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                //Close the streams
                myOutput.flush();
                myOutput.close();
            } else {
                //showToast("Error in Backup");
            }

            myInput.close();
            return sdDir + "/" + DBName;

        } catch (Exception e) {
            //Log.i("Database_Operation","exception="+e);
            databaseAdapter.insertExceptions(e.getMessage(), "Common.java", "copyDBToSDCard");
            return ("Error: In Database backup--> " + e.getMessage());
        }


    }

    //To show date from "yyyy-MM-dd HH:mm:ss" to "dd-MMM-yyyy" format
    public String convertToDisplayDateFormat(String dateValue)
    {
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String createDateForDB = "";
        Date date = null;
        try {
            date = format.parse(dateValue);

            SimpleDateFormat  dbdateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            createDateForDB = dbdateformat.format(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            databaseAdapter.insertExceptions(e.getMessage(), "Common.java","convertDateFormat");
        }
        return createDateForDB;
    }

    public String convertDateFormat(String dateValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String createDateForDB = "";
        Date date = null;
        try {
            date = format.parse(dateValue);

            SimpleDateFormat dbdateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            createDateForDB = dbdateformat.format(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            databaseAdapter.insertExceptions(e.getMessage(), "Common.java", "convertDateFormat");
        }
        return createDateForDB;
    }

    public String convertNewDateFormat(String dateValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String createDateForDB = "";
        Date date = null;
        try {
            date = format.parse(dateValue);

            SimpleDateFormat dbdateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
            createDateForDB = dbdateformat.format(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            databaseAdapter.insertExceptions(e.getMessage(), "Common.java", "convertDateFormat");
        }
        return createDateForDB;
    }

    public long convertDateStringToMilliSeconds(String dateValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        long longMilliSeconds = 0;
        long remainingTime = 0;
        Date date = null;
        try {
            date = format.parse(dateValue);
            //longMilliSeconds = date.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            longMilliSeconds = calendar.getTimeInMillis();
            remainingTime = longMilliSeconds - System.currentTimeMillis();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            databaseAdapter.insertExceptions(e.getMessage(), "Common.java", "convertDateStringToMilliSeconds");
        }
        return remainingTime;
    }


    public String convertToTwoDecimal(String value) {
        NumberFormat formatter = new DecimalFormat("##,##,##,##,##,##,##,##,##0.00");
        String result = formatter.format(Double.valueOf(value));
        return "Rs. " + result;
    }

    public String convertToThreeDecimal(String value) {
        //This method apply commas and three digits
        NumberFormat formatter = new DecimalFormat("##,##,##,##,##,##,##,##,##0.000");
        String result = formatter.format(Double.valueOf(value));
        return result;
    }

    public String stringToTwoDecimal(String value) {
        NumberFormat formatter = new DecimalFormat("##,##,##,##,##,##,##,##,##0.00");
        return formatter.format(Double.valueOf(value));
    }

    public double stringToFourDecimal(String value) {
        NumberFormat formatter = new DecimalFormat("0.0000");
        return Double.parseDouble(formatter.format(Double.valueOf(value)));
    }

    //overloaded
    public double stringToTwoDecimal(double value) {
        NumberFormat formatter = new DecimalFormat("0.00");
        return Double.parseDouble(formatter.format(Double.valueOf(value)));
    }

    public double stringToThreeDecimal(String value) {
        NumberFormat formatter = new DecimalFormat("0.000");
        return Double.parseDouble(formatter.format(Double.valueOf(value)));
    }

    public double stringToOneDecimal(String value) {
        NumberFormat formatter = new DecimalFormat("0.0");
        return Double.parseDouble(formatter.format(Double.valueOf(value)));
    }

    //To send JSON String
    public String invokeJSONWS(String sendValue, String sendName, String methName, String newUrl) throws Exception {
        // Create request
        SoapObject request = new SoapObject(namespace, methName);

        // Property which holds input parameters
        PropertyInfo paramPI = new PropertyInfo();
        // Set Name
        paramPI.setName(sendName);
        // Set Value
        paramPI.setValue(sendValue);
        // Set dataType
        paramPI.setType(String.class);
        // Add the property to request object

        request.addProperty(paramPI);

        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(newUrl, 50000);

        // Invoke web service
        androidHttpTransport.call(soap_action + methName, envelope);
        // Get the response
        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        // Assign it to static variable
        responseJSON = response.toString();

        return responseJSON;
    }

    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
        /*return "0000000000";*/
    }

    public String invokeTwinJSONWS(String sendValue1, String sendName1, String sendValue2, String sendName2,String sendValue3, String sendName3, String methName, String newUrl) throws Exception {
        // Create request
        SoapObject request = new SoapObject(namespace, methName);

        // Property which holds input parameters
        PropertyInfo paramPI = new PropertyInfo();
        // Set Name
        paramPI.setName(sendName1);
        // Set Value
        paramPI.setValue(sendValue1);


        PropertyInfo paramPI2 = new PropertyInfo();
        // Set Name
        paramPI2.setName(sendName2);
        // Set Value
        paramPI2.setValue(sendValue2);
        // Set dataType
        paramPI2.setType(String.class);

        PropertyInfo paramPI3 = new PropertyInfo();
        // Set Name
        paramPI3.setName(sendName3);
        // Set Value
        paramPI3.setValue(sendValue3);
        // Set dataType
        paramPI3.setType(String.class);
        // Add the property to request object

        request.addProperty(paramPI);
        request.addProperty(paramPI2);
        request.addProperty(paramPI3);

        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(newUrl, 5000);

        // Invoke web service
        androidHttpTransport.call(soap_action + methName, envelope);
        // Get the response
        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        // Assign it to static variable
        responseJSON = response.toString();

        return responseJSON;
    }

    //<editor-fold desc="File related functions">
    private ImageLoadingUtils utils;

    public String random() {
        Random r = new Random();

        char[] choices = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890").toCharArray();

        StringBuilder salt = new StringBuilder(10);
        for (int i = 0; i < 10; ++i)
            salt.append(choices[r.nextInt(choices.length)]);
        return "img_" + salt.toString();
    }

    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }

    public String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};

            cursor = c.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String compressImage(String path) {

        File imagePath = new File(path);
        String filePath = path;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options,
                actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
                middleY - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
        }
        FileOutputStream out;

        try {
            out = new FileOutputStream(imagePath);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        return imagePath.getAbsolutePath();
    }

    public String copyFile(String inputPath, String outputPath, String outputPathWithName) {
        File f = (outputPathWithName.trim().isEmpty()) ? new File(inputPath) : new File(outputPathWithName);
        InputStream in;
        OutputStream out;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + f.getName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            compressImage(outputPath + "/" + f.getName());

            // write the output file (You have now copied the file)
            out.flush();
            out.close();


        } catch (FileNotFoundException fnfe1) {
            //Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            //Log.e("tag", e.getMessage());
        }
        return outputPath + "/" + f.getName();
    }

    public boolean createDirectory(String dirName) {
        //Code to Create Directory for Inspection (Parent)
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            copyNoMediaFile(dirName);
            return true;
        } else {
            return false;
        }
    }

    public void copyNoMediaFile(String dirName) {
        try {
            String storageState = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                try {
                    File noMedia = new File(Environment.getExternalStorageDirectory() + "/" + dirName, ".nomedia");

                    FileOutputStream noMediaOutStream = new FileOutputStream(noMedia);
                    noMediaOutStream.write(0);
                    noMediaOutStream.close();
                } catch (Exception e) {
                }
            } else {
            }
        } catch (Exception e) {
        }
    }
    //</editor-fold>
}
