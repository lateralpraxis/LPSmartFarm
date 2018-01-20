package lateralpraxis.lpsmartfarm.visits;

import android.annotation.TargetApi;
import android.app.ActionBar;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import lateralpraxis.lpsmartfarm.ActivityHome;
import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.ImageLoadingUtils;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.lpsmartfarm.ViewImage;
import lateralpraxis.type.CustomType;

public class RoutineVisit5 extends Activity {
    private static final int PICK_CAMERA_IMAGE = 5;
    private final Context mContext = this;
    File fileDestination, fileFBook;
    /*------------------------Start of code for controls Declaration------------------------------*/
    private TextView tvImage;
    private EditText etRemarks;
    /*------------------------End of code for controls Declaration------------------------------*/
    private Spinner spDefect;
    private Button btnBack, btnNext, btnUploadPhoto, btnViewPhoto;
    /*------------------------Start of code for class Declaration------------------------------*/
    private DatabaseAdapter dba;
    /*------------------------End of code for class Declaration------------------------------*/
    private UserSessionManager session;
    private Common common;
    /*------------------------Start of code for variable Declaration------------------------------*/
    private String userId, type, nurseryId, nursery, zoneId, zone, visitUniqueId, farmerUniqueId, farmBlockUniqueId, farmerName, farmerMobile, farmBlockCode, plantationUniqueId, plantationName, defectId = "0", fileName = "", filePath = "", EntryFor;
    private String level1Dir, level2Dir, level3Dir, fullPhotoPath, photoPath, newPhotoPath, uuidImage, uploadedFilePath;
    private File[] fileList;
    private ImageLoadingUtils utils;
    private ArrayList<HashMap<String, String>> attachmentDetails;
    private String[] filePathStrings, fileNameStrings;
    /*------------------------End of code for variable Declaration------------------------------*/

    //Method to generate random number and return the same
    public static String random() {
        Random r = new Random();

        char[] choices = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "01234567890").toCharArray();

        StringBuilder salt = new StringBuilder(10);
        for (int i = 0; i < 10; ++i)
            salt.append(choices[r.nextInt(choices.length)]);
        return "img_" + salt.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_visit5);

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*-----------------Code to set Action Bar--------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

         /*------------------------Start of code for controls Declaration--------------------------*/
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        spDefect = (Spinner) findViewById(R.id.spDefect);
        tvImage = (TextView) findViewById(R.id.tvImage);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        btnViewPhoto = (Button) findViewById(R.id.btnViewPhoto);

           /*-----------------Code to get data from posted page--------------------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmBlockUniqueId = extras.getString("farmBlockUniqueId");
            farmerName = extras.getString("farmerName");
            farmerMobile = extras.getString("farmerMobile");
            farmBlockCode = extras.getString("farmBlockCode");
            plantationUniqueId = extras.getString("plantationUniqueId");
            plantationName = extras.getString("plantationName");
            visitUniqueId = extras.getString("visitUniqueId");
            EntryFor = extras.getString("EntryFor");
            type = extras.getString("type");
            nurseryId = extras.getString("nurseryId");
            nursery = extras.getString("nursery");
            zoneId = extras.getString("zoneId");
            zone = extras.getString("zone");
        }


        //To bind defect
        spDefect.setAdapter(DataAdapter("defect", ""));
        if (!defectId.equalsIgnoreCase("0")) {
            int spdCnt = spDefect.getAdapter().getCount();
            for (int i = 0; i < spdCnt; i++) {
                if (((CustomType) spDefect.getItemAtPosition(i)).getId().equals(defectId))
                    spDefect.setSelection(i);
            }
        }

/*---------------Start of code to set Click Event for Button Upload-------------------------*/
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (tvImage.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle("Attach Defect Photo");
                    builder1.setMessage("Are you sure, you want to remove existing defect picture and upload new defect picture?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    tvImage.setText("");
                                    btnViewPhoto.setVisibility(View.GONE);
                                    dba.open();
                                    dba.DeleteTempFileByType("Defect");
                                    dba.close();
                                    startDialog();
                                }
                            }).setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // if this button is clicked, just close
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertnew = builder1.create();
                    alertnew.show();
                } else
                    startDialog();
            }
        });
        /*---------------End of code to set Click Event for Button Upload-------------------------*/

        /*---------------Start of code to set Click Event for Button back & Next-------------------------*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //To move routine visit add observation to view observation page
                if (EntryFor.equalsIgnoreCase("visit")) {
                    Intent intent = new Intent(RoutineVisit5.this, RoutineVisit4.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmBlockCode", farmBlockCode);
                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                    intent.putExtra("plantationName", plantationName);
                    intent.putExtra("visitUniqueId", visitUniqueId);
                    intent.putExtra("EntryFor", EntryFor);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RoutineVisit5.this, RoutineVisitNursery4.class);
                    intent.putExtra("type", type);
                    intent.putExtra("nurseryId", nurseryId);
                    intent.putExtra("nursery", nursery);
                    intent.putExtra("zoneId", zoneId);
                    intent.putExtra("zone", zone);
                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                    intent.putExtra("plantationName", plantationName);
                    intent.putExtra("visitUniqueId", visitUniqueId);
                    intent.putExtra("EntryFor", EntryFor);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ((spDefect.getSelectedItemPosition() == 0))
                    common.showToast("Defect/Diseases Name is mandatory!", 5, 1);
                else if (String.valueOf(etRemarks.getText()).trim().equals(""))
                    common.showToast("Remarks is mandatory!", 5, 1);
                else {
                    dba.openR();
                    boolean isExist = dba.isExistDefectVisitReportDetail(visitUniqueId, String.valueOf(((CustomType) spDefect.getSelectedItem()).getId()));
                    if (isExist)
                        common.showToast("Defect/Diseases already added!", 5, 1);
                    else {
                        dba.open();
                        dba.InsertVisitReportDetail(visitUniqueId, String.valueOf(((CustomType) spDefect.getSelectedItem()).getId()), String.valueOf(etRemarks.getText()).trim(), String.valueOf(tvImage.getText()).trim());
                        dba.close();

                        common.showToast("Defect/Diseases successfully added.", 5, 3);

                        //To move from visit details to observation details page
                        if (EntryFor.equalsIgnoreCase("visit")) {
                            Intent intent = new Intent(RoutineVisit5.this, RoutineVisit4.class);
                            intent.putExtra("farmerUniqueId", farmerUniqueId);
                            intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                            intent.putExtra("farmerName", farmerName);
                            intent.putExtra("farmerMobile", farmerMobile);
                            intent.putExtra("farmBlockCode", farmBlockCode);
                            intent.putExtra("plantationUniqueId", plantationUniqueId);
                            intent.putExtra("plantationName", plantationName);
                            intent.putExtra("visitUniqueId", visitUniqueId);
                            intent.putExtra("EntryFor", EntryFor);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(RoutineVisit5.this, RoutineVisitNursery4.class);
                            intent.putExtra("type", type);
                            intent.putExtra("nurseryId", nurseryId);
                            intent.putExtra("nursery", nursery);
                            intent.putExtra("zoneId", zoneId);
                            intent.putExtra("zone", zone);
                            intent.putExtra("plantationUniqueId", plantationUniqueId);
                            intent.putExtra("plantationName", plantationName);
                            intent.putExtra("visitUniqueId", visitUniqueId);
                            intent.putExtra("EntryFor", EntryFor);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
        /*---------------End of code to set Click Event for Button Save & Next-------------------------*/

        /*---------------Start of code to set Click Event for Viewing Attachment-------------------------*/
        btnViewPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewIn) {
                try {
                    // Check for SD Card
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        common.showToast("Error! No SDCARD Found!", 5, 0);
                    } else {

                        dba.open();
                        attachmentDetails = dba.GetTempAttachment("Defect");
                        if (attachmentDetails.size() > 0) {
                            for (HashMap<String, String> hashMap : attachmentDetails) {
                                for (String key : hashMap.keySet()) {
                                    if (key.equals("FileName"))
                                        uploadedFilePath = hashMap.get(key);
                                }
                            }
                            File file = new File(uploadedFilePath);
                            fileFBook = new File(file.getParent());
                        }
                    }

                    if (fileFBook.isDirectory()) {
                        fileList = fileFBook.listFiles(new FilenameFilter() {
                            public boolean accept(File directory,
                                                  String fileName) {
                                return fileName.endsWith(".jpeg")
                                        || fileName.endsWith(".bmp")
                                        || fileName.endsWith(".jpg")
                                        || fileName.endsWith(".png")
                                        || fileName.endsWith(".gif");
                            }
                        });
                        // Create a String array for filePathStrings
                        filePathStrings = new String[fileList.length];
                        // Create a String array for FileNameStrings
                        fileNameStrings = new String[fileList.length];

                        for (int i = 0; i < fileList.length; i++) {

                            filePathStrings[i] = fileList[i].getAbsolutePath();
                            // Get the name image file
                            fileNameStrings[i] = fileList[i].getName();

                            Intent i1 = new Intent(RoutineVisit5.this,
                                    ViewImage.class);
                            // Pass String arrays filePathStrings
                            i1.putExtra("filepath", filePathStrings);
                            // Pass String arrays FileNameStrings
                            i1.putExtra("filename", fileNameStrings);
                            // Pass click position
                            i1.putExtra("position", 0);
                            startActivity(i1);
                            /* } */
                        }
                    }

                } catch (Exception except) {
                    //except.printStackTrace();
                    common.showAlert(RoutineVisit5.this, "Error: " + except.getMessage(), false);
                }
            }
        });
        /*---------------End of code to set Click Event for Viewing Attachment-------------------------*/
    }

    //Code to open camera for attaching defect photo
    private void startDialog() {

        //Setting directory structure
        uuidImage = UUID.randomUUID().toString();
        level1Dir = "LPSMARTFARM";
        level2Dir = level1Dir + "/" + "Defect";
        level3Dir = level2Dir + "/" + uuidImage;
        String imageName = random() + ".jpg";
        fullPhotoPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
        fileDestination = new File(fullPhotoPath, imageName);
        //Check if directory exists else create directory
        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
            //Code to open camera intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(fileDestination));
            startActivityForResult(intent, PICK_CAMERA_IMAGE);
        }
    }

    //Code to be executed after action done for attaching
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CAMERA_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Camera request and result code is ok
                uuidImage = UUID.randomUUID().toString();
                level1Dir = "LPSMARTFARM";
                level2Dir = level1Dir + "/" + "Defect";
                level3Dir = level2Dir + "/" + uuidImage;
                newPhotoPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                photoPath = fullPhotoPath + "/" + fileDestination.getAbsolutePath().substring(fileDestination.getAbsolutePath().lastIndexOf("/") + 1);
                if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                    copyFile(photoPath, newPhotoPath);
                }
                dba.open();
                dba.Insert_TempFile("Defect", newPhotoPath + "/" + fileDestination.getAbsolutePath().substring(fileDestination.getAbsolutePath().lastIndexOf("/") + 1));
                dba.close();
                btnViewPhoto.setVisibility(View.VISIBLE);
                tvImage.setText(fileDestination.getAbsolutePath().substring(fileDestination.getAbsolutePath().lastIndexOf("/") + 1));
                File dir = new File(fullPhotoPath);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }
        }
    }

    //Method to get Actual path of image
    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*---------------End of code to generate random number and return the same-------------------------*/
    /*---------------Start of code to delete file recursively-------------------------*/
    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }
    /*---------------End of code to delete file recursively-------------------------*/

    /*---------------Start of code to compress image-------------------------*/
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
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(imagePath);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        return imagePath.getAbsolutePath();

    }
    /*---------------End of code to compress image-------------------------*/

    /*---------------Start of code to create new directory-------------------------*/
    private boolean createDirectory(String dirName) {
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
    /*---------------End of code to create new directory-------------------------*/

    /*---------------Start of code to create No Media File in directory-------------------------*/
    private void copyNoMediaFile(String dirName) {
        try {
            // Open your local db as the input stream
            //boolean D= true;
            String storageState = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                try {
                    File noMedia = new File(Environment
                            .getExternalStorageDirectory()
                            + "/"
                            + level2Dir, ".nomedia");
                    if (noMedia.exists()) {


                    }

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
    /*---------------End of code to create No Media File in directory-------------------------*/

    /*---------------Start of code to Copy file from one place to another-------------------------*/
    private String copyFile(String inputPath, String outputPath) {

        File f = new File(inputPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + f.getName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            compressImage(outputPath + "/" + f.getName());

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;


        } catch (FileNotFoundException fnfe1) {
            //Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            //Log.e("tag", e.getMessage());
        }
        return outputPath + "/" + f.getName();
    }
    /*---------------End of code to Copy file from one place to another-------------------------*/

    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //To move from visit details to plantation details page
                if (EntryFor.equalsIgnoreCase("visit")) {
                    Intent intent = new Intent(RoutineVisit5.this, RoutineVisit4.class);
                    intent.putExtra("farmerUniqueId", farmerUniqueId);
                    intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
                    intent.putExtra("farmerName", farmerName);
                    intent.putExtra("farmerMobile", farmerMobile);
                    intent.putExtra("farmBlockCode", farmBlockCode);
                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                    intent.putExtra("plantationName", plantationName);
                    intent.putExtra("visitUniqueId", visitUniqueId);
                    intent.putExtra("EntryFor", EntryFor);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RoutineVisit5.this, RoutineVisitNursery4.class);
                    intent.putExtra("type", type);
                    intent.putExtra("nurseryId", nurseryId);
                    intent.putExtra("nursery", nursery);
                    intent.putExtra("zoneId", zoneId);
                    intent.putExtra("zone", zone);
                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                    intent.putExtra("plantationName", plantationName);
                    intent.putExtra("visitUniqueId", visitUniqueId);
                    intent.putExtra("EntryFor", EntryFor);
                    startActivity(intent);
                    finish();
                }
                return true;
            case R.id.action_go_to_home:
                //To move from visit details to home page
                Intent homeScreenIntent = new Intent(RoutineVisit5.this, ActivityHome.class);
                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeScreenIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // To create menu on inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        if (EntryFor.equalsIgnoreCase("visit")) {
            //To move from RoutineVisit5 to RoutineVisit4 details page
            Intent intent = new Intent(RoutineVisit5.this, RoutineVisit4.class);
            intent.putExtra("farmerUniqueId", farmerUniqueId);
            intent.putExtra("farmBlockUniqueId", farmBlockUniqueId);
            intent.putExtra("farmerName", farmerName);
            intent.putExtra("farmerMobile", farmerMobile);
            intent.putExtra("farmBlockCode", farmBlockCode);
            intent.putExtra("plantationUniqueId", plantationUniqueId);
            intent.putExtra("plantationName", plantationName);
            intent.putExtra("visitUniqueId", visitUniqueId);
            intent.putExtra("EntryFor", EntryFor);
            startActivity(intent);
            finish();
        } else {
            //To move from RoutineVisit5 to RoutineVisitNursery4 details page
            Intent intent = new Intent(RoutineVisit5.this, RoutineVisitNursery4.class);
            intent.putExtra("type", type);
            intent.putExtra("nurseryId", nurseryId);
            intent.putExtra("nursery", nursery);
            intent.putExtra("zoneId", zoneId);
            intent.putExtra("zone", zone);
            intent.putExtra("plantationUniqueId", plantationUniqueId);
            intent.putExtra("plantationName", plantationName);
            intent.putExtra("visitUniqueId", visitUniqueId);
            intent.putExtra("EntryFor", EntryFor);
            startActivity(intent);
            finish();
        }
    }

    //Method to check android version ad load action bar appropriately
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void actionBarSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setIcon(R.mipmap.ic_launcher);
            ab.setHomeButtonEnabled(true);
        }
    }
}
