package lateralpraxis.lpsmartfarm.farmer;

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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import lateralpraxis.type.AttachmentDetails;
import lateralpraxis.type.ProofType;

public class FarmerPOAPOI extends Activity {
    /*---------------Variable for Capturing Image---------------*/
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    private static final int PICK_Camera_IMAGE = 0;
    private final Context mContext = this;
    /*------------------Code for Class Declaration---------------*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;
    CustomAdapter Cadapter;
    Bitmap bitmap;
    Uri uri;
    /*----------Variable Declaration for Storing Image----------*/
    String photoPath;
    String uuidFinal;
    Intent picIntent = null;
    File destination, file;
    private ImageLoadingUtils utils;
    /*-----------Start of Code for control declaration-----------*/
    private Spinner spDocType, spDocName;
    private EditText etDocumentNumber;
    private Button btnUpload, btnReset, btnAdd, btnSubmit;
    private TextView tvEmpty, tvDocImageUploaded, tvNameData, tvMobile;
    private ListView lvList;
    /*----------End of Code for control declaration---------------*/

    /*--------------Start of Code for variable declaration-----------*/
    private String farmerUniqueId = "", farmerName = "", farmerMobile = "", fromPhoto = "";
    private String level1Dir;
    private String level2Dir;
    private String level3Dir;
    private String level4Dir;
    private String fullPath;
    private static String lang;
    private int fileCount = 0;
    private int lsize = 0;
    private ArrayList<HashMap<String, String>> DocDetails;
    private File[] listFile;
    private String[] FilePathStrings;
    private String[] FileNameStrings;

    /*-----------Method to calculate image size------------------------*/
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /*-----------Method to generate Random Number for File Name------------------------*/
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

    /*----------------End of Code for variable declaration-----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_poapoi);

        /*------------------------Start of code for setting action bar----------------------------*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /*------------------------End of code for setting action bar------------------------------*/

        /*------------------------Start of code for creating instance of class--------------------*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        DocDetails = new ArrayList<HashMap<String, String>>();
        lang = session.getDefaultLang();
        /*------------------------End of code for creating instance of class--------------------*/

        /*------------------------Start of code for finding controls-----------------------*/
        spDocType = (Spinner) findViewById(R.id.spDocType);
        spDocName = (Spinner) findViewById(R.id.spDocName);
        etDocumentNumber = (EditText) findViewById(R.id.etDocumentNumber);
        etDocumentNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        tvNameData = (TextView) findViewById(R.id.tvNameData);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvDocImageUploaded = (TextView) findViewById(R.id.tvDocImageUploaded);
        lvList = (ListView) findViewById(R.id.lvDocInfoList);
        /*--------------Start of code for getting Farmer Unique Id from previous intent----------------*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            farmerUniqueId = extras.getString("farmerUniqueId");
            farmerName = extras.getString("Name");
            farmerMobile = extras.getString("Mobile");
            tvNameData.setText(farmerName);
            tvMobile.setText(farmerMobile);
        }
         /*Start of code to delete data of proof from temporary table*/
        dba.open();
        dba.DeleteTempProofs();
        dba.Insert_FarmerMainToTemp(farmerUniqueId);
        dba.close();
        /*End of code to delete data of proof from temporary table*/
        /*Code for Binding Document Data from Temp Table */
        dba.open();
        List<AttachmentDetails> lables = dba.getProofDocument(farmerUniqueId);
        lsize = lables.size();
        if (lsize > 0) {
            tvEmpty.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            for (int i = 0; i < lables.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("Id", lables.get(i).getId());
                hm.put("DocumentType", String.valueOf(lables.get(i).getDocumentType()));
                hm.put("DocumentName", String.valueOf(lables.get(i).getDocumentName()));
                hm.put("DocumentNumber", String.valueOf(lables.get(i).getDocumentNumber()));
                hm.put("ImagePath", String.valueOf(lables.get(i).getImagePath()));
                hm.put("ImageName", String.valueOf(lables.get(i).getImageName()));
                hm.put("IsSync", String.valueOf(lables.get(i).getIsSync()));
                DocDetails.add(hm);
            }
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            // btnSubmit.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        dba.close();

        Cadapter = new CustomAdapter(FarmerPOAPOI.this, DocDetails);
        if (lsize > 0) {
            lvList.setAdapter(Cadapter);
            tvEmpty.setVisibility(View.GONE);
            lvList.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            lvList.setVisibility(View.GONE);
        }
         /*------------------------Start of code for binding data in Spinner-----------------------*/
        spDocType.setAdapter(DataAdapter("type", ""));

        /*-----------Start of code for binding data on Spinner Item Change-----------------------*/
        spDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spDocName.setAdapter(DataAdapter("proof", String.valueOf(((ProofType) spDocType.getSelectedItem()).getId())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        /*---------------Start of code to be executed on Click Event Of buttons-------------------------*/
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (tvDocImageUploaded.getText().toString().trim().length() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            mContext);
                    builder1.setTitle(lang.equalsIgnoreCase("en")?"Attach Proof":"सबूत संलग्न करें");
                    builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to remove existing Proof picture and upload new Proof picture?":"क्या आप वाकई मौजूदा साक्ष्य चित्र को हटाना चाहते हैं और नए सबूत तस्वीर अपलोड करना चाहते हैं?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    startDialog();
                                }
                            }).setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं",
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

        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle(lang.equalsIgnoreCase("en")?"Reset Proof":"प्रूफ रीसेट करें");
                builder1.setMessage(lang.equalsIgnoreCase("en")?"Are you sure, you want to reset file chosen?":"क्या आप निश्चित हैं, आप फ़ाइल को रीसेट करना चाहते हैं?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(lang.equalsIgnoreCase("en")?"Yes":"हाँ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                tvDocImageUploaded.setText("");
                                photoPath = "";
                            }
                        }).setNegativeButton(lang.equalsIgnoreCase("en")?"No":"नहीं",
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
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if ((spDocType.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Type is mandatory":"प्रकार अनिवार्य है", 5, 1);
                else if ((spDocName.getSelectedItemPosition() == 0))
                    common.showToast(lang.equalsIgnoreCase("en")?"Name is mandatory":"नाम अनिवार्य है", 5, 1);
                else if (String.valueOf(etDocumentNumber.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Document number is mandatory":"दस्तावेज़ संख्या अनिवार्य है", 5, 1);
                else if (String.valueOf(tvDocImageUploaded.getText()).trim().equals(""))
                    common.showToast(lang.equalsIgnoreCase("en")?"Attachment is mandatory":"अनुलग्नक अनिवार्य है", 5, 1);
                else {
                    String uniqueId = UUID.randomUUID().toString();
                    String outdir;
                    level1Dir = "LPSMARTFARM";
                    level2Dir = level1Dir + "/" + uniqueId;
                    level3Dir = level2Dir + "/" + uuidFinal;
                    level4Dir = level3Dir + "/IdentityProof";

                    fullPath = Environment.getExternalStorageDirectory() + "/" + level4Dir;
                    outdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + level4Dir;
                    String imagePath = "";

                    String selectedPhotoPath ="";
                    if (fromPhoto.equalsIgnoreCase("Gallery")) {
                        selectedPhotoPath = tvDocImageUploaded.getText().toString().trim();
                        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir) && createDirectory(level4Dir)) {
                            imagePath = copyFileWithName(photoPath, outdir, tvDocImageUploaded.getText().toString().trim());
                        }
                    } else {
                        selectedPhotoPath = photoPath.substring(photoPath.lastIndexOf("/") + 1);
                        if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir) && createDirectory(level4Dir)) {
                            imagePath = copyFile(photoPath, outdir);
                        }
                    }
                    dba.open();
                    dba.Insert_FarmerProofTemp(farmerUniqueId, uniqueId, ((ProofType) spDocName.getSelectedItem()).getId(), etDocumentNumber.getText().toString(), selectedPhotoPath, imagePath);
                    dba.close();
                    common.showToast(lang.equalsIgnoreCase("en")?"Document added successfully.":"दस्तावेज़ सफलतापूर्वक जोड़ा गया।", 5, 3);
                    etDocumentNumber.setText("");
                    spDocType.setSelection(0);
                    spDocName.setSelection(0);
                    tvDocImageUploaded.setText("");
                    DocDetails.clear();
                    dba.open();
                    List<AttachmentDetails> lables = dba.getProofDocument(farmerUniqueId);
                    lsize = lables.size();
                    if (lsize > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        for (int i = 0; i < lables.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("Id", lables.get(i).getId());
                            hm.put("DocumentType", String.valueOf(lables.get(i).getDocumentType()));
                            hm.put("DocumentName", String.valueOf(lables.get(i).getDocumentName()));
                            hm.put("DocumentNumber", String.valueOf(lables.get(i).getDocumentNumber()));
                            hm.put("ImagePath", String.valueOf(lables.get(i).getImagePath()));
                            hm.put("ImageName", String.valueOf(lables.get(i).getImageName()));
                            hm.put("IsSync", String.valueOf(lables.get(i).getIsSync()));
                            DocDetails.add(hm);
                        }
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                    dba.close();
                    Cadapter = new CustomAdapter(FarmerPOAPOI.this, DocDetails);
                    if (lsize > 0) {
                        lvList.setAdapter(Cadapter);
                        tvEmpty.setVisibility(View.GONE);
                        lvList.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        lvList.setVisibility(View.GONE);
                    }

                    photoPath = "";
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               /* AlertDialog.Builder builder1 = new AlertDialog.Builder(
                        mContext);
                builder1.setTitle("Submit Proof");
                builder1.setMessage("Are you sure, you want to submit Proof Documents?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {*/
                dba.openR();
                int poaCnt = 0;
                int poiCnt = 0;
                poaCnt = dba.getPOACountByFarmerId(farmerUniqueId);
                poiCnt = dba.getPOICountByFarmerId(farmerUniqueId);
                                /*if (poaCnt == 0)
                                    common.showToast("Please attach at least one Proof of address document.");
                                else if (poiCnt == 0)
                                    common.showToast("Please attach at least one Proof of identity document.");
                                else {*/
                HashMap<String, String> user = session.getLoginUserDetails();
                //Setting UserId
                String userId = user.get(UserSessionManager.KEY_ID);
                dba.open();
                dba.Insert_FarmerProof(userId, farmerUniqueId);
                dba.close();
                if (poaCnt + poiCnt > 0)
                    common.showToast(lang.equalsIgnoreCase("en")?"Farmer proofs submitted successfully.":"किसान सबूत सफलतापूर्वक सबमिट किए गए।", 5, 3);
                Intent intent = new Intent(FarmerPOAPOI.this, ActivityFarmerRelatives.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                intent.putExtra("Name", farmerName);
                intent.putExtra("Mobile", farmerMobile);
                startActivity(intent);
                finish();
                System.gc();
                                /*}*/
                           /* }
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
                alertnew.show();*/

            }
        });
        /*---------------End of code to be executed on Click Event Of buttons-------------------------*/
    }

    /*Method to open dialog to select mode for selecting image*/
    private void startDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(FarmerPOAPOI.this);
        builderSingle.setTitle("Select Image source");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                FarmerPOAPOI.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Capture Image");
        arrayAdapter.add("Select from Gallery");


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
                        if (strName.equals("Capture Image")) {
                            uuidFinal = UUID.randomUUID().toString();
                            level1Dir = "LPSMARTFARM";
                            level2Dir = level1Dir + "/" + uuidFinal;
                            level3Dir = level2Dir + "/" + "IdentityProof";
                            String latestImageName = random() + ".jpg";
                            fullPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                            destination = new File(fullPath, latestImageName);

                            if (createDirectory(level1Dir) && createDirectory(level2Dir) && createDirectory(level3Dir)) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(destination));
                                startActivityForResult(intent, PICK_Camera_IMAGE);
                            }
                            photoPath = fullPath + "/" + latestImageName;
                            tvDocImageUploaded.setText(latestImageName);

                        } else if (strName.equals("Select from Gallery")) {
                            picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            picIntent.putExtra("return_data", true);
                            startActivityForResult(picIntent, GALLERY_REQUEST);
                            //finish();
                        } else {
                            common.showToast("No File available for review.");
                        }
                    }
                });
        builderSingle.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data == null) {
            tvDocImageUploaded.setText("");
        } else if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    if (uri != null) {
                        photoPath = getRealPathFromUri(uri);

                        uuidFinal = UUID.randomUUID().toString();
                        level1Dir = "LPSMARTFARM";
                        level2Dir = level1Dir + "/" + uuidFinal;
                        level3Dir = level2Dir + "/" + "IdentityProof";
                        String latestImageName = random() + ".jpg";
                        // fullPath = Environment.getExternalStorageDirectory() + "/" + level3Dir;
                        //destination = new File(fullPath, latestImageName);
                        tvDocImageUploaded.setText(latestImageName);
                        fromPhoto = "Gallery";
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                tvDocImageUploaded.setText("");
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {


            } else
                tvDocImageUploaded.setText("");
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled",
                    Toast.LENGTH_SHORT).show();
            tvDocImageUploaded.setText("");
        }
    }

    /*---------Method To get Real Path From URI------------------*/
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

    /*---------Method To Count Number of Files------------------*/
    public int CountFiles(File[] files) {
        if (files == null || files.length == 0) {
            return 0;
        } else {
            for (File file : files) {
                if (file.isDirectory()) {
                    CountFiles(file.listFiles());
                } else {
                    if (!file.getAbsolutePath().contains(".nomedia"))
                        fileCount++;
                }
            }
            return fileCount;
        }
    }

    /*---------Method To Create Directory------------------*/
    private boolean createDirectory(String dirName) {
        //Code to Create Directory for Inspection (Parent)
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            if (dirName.contains("IdentityProof"))
                copyNoMediaFile(dirName);
            return true;
        } else {
            return false;
        }
    }

    /*---------Method To Create No Media File------------------*/
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
                            + level4Dir, ".nomedia");
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

    /*---------------Start of code to Copy file from one place to another with file name-------------------------*/
    private String copyFileWithName(String inputPath, String outputPath, String outputPathWithName) {

        File f = new File(outputPathWithName);
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
    /*---------------End of code to Copy file from one place to another with file name -------------------------*/


    public String compressImage(String path) {

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

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            //exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
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
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            //Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                //Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                //Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                //Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(destination);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

        return destination.getAbsolutePath();

    }

    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }

    /*---------------Method to fetch data and bind spinners-------------------------*/
    private ArrayAdapter<ProofType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<ProofType> lables = dba.GetProofDetails(masterType, filter);
        ArrayAdapter<ProofType> dataAdapter = new ArrayAdapter<ProofType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }


    /*---------------Method to view intent on Back Press Click-------------------------*/
    @Override
    public void onBackPressed() {
        Intent i = new Intent(FarmerPOAPOI.this, ActivityUpdateFarmer.class);
        i.putExtra("farmerUniqueId", farmerUniqueId);
        startActivity(i);
        this.finish();
        System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityUpdateFarmer.class);
                intent.putExtra("farmerUniqueId", farmerUniqueId);
                startActivity(intent);
                this.finish();
                System.gc();
                return true;
            case R.id.action_go_to_home:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FarmerPOAPOI.this);
                // set title
                alertDialogBuilder.setTitle("Confirmation");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure, you want to leave this module it will discard any unsaved data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent homeScreenIntent = new Intent(FarmerPOAPOI.this, ActivityHome.class);
                                homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeScreenIntent);
                                finish();
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

    // To create menu on inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /*---------------Method to view intent on Action Bar Click-------------------------*/

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    /*-----------Code for Handling Data Binding---------------------------*/
    public static class ViewHolder {
        TextView tvDocumentId, tvDocumentPath, tvDocumentType, tvDocumentName, tvDocumentNumberValue, tvUniqueId;

        Button btnViewAttach, btnDelete;
    }

    public class CustomAdapter extends BaseAdapter {
        private Context docContext;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> lvList) {
            this.docContext = context;
            mInflater = LayoutInflater.from(docContext);
            DocDetails = lvList;
        }

        @Override
        public int getCount() {
            return DocDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            return DocDetails.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getViewTypeCount() {

            return getCount();
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public View getView(final int arg0, View arg1, ViewGroup arg2) {


            final ViewHolder holder;
            if (arg1 == null) {
                arg1 = mInflater.inflate(R.layout.list_attached_documents, null);
                holder = new ViewHolder();
                holder.tvDocumentId = (TextView) arg1.findViewById(R.id.tvDocumentId);
                holder.tvDocumentType = (TextView) arg1.findViewById(R.id.tvDocumentType);
                holder.tvDocumentName = (TextView) arg1.findViewById(R.id.tvDocumentName);
                holder.tvDocumentPath = (TextView) arg1.findViewById(R.id.tvDocumentPath);
                holder.tvUniqueId = (TextView) arg1.findViewById(R.id.tvUniqueId);
                holder.tvDocumentNumberValue = (TextView) arg1.findViewById(R.id.tvDocumentNumberValue);
                holder.btnDelete = (Button) arg1.findViewById(R.id.btnDelete);
                holder.btnViewAttach = (Button) arg1.findViewById(R.id.btnViewAttach);
                holder.btnViewAttach.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
                arg1.setTag(holder);

            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            holder.tvDocumentId.setText(DocDetails.get(arg0).get("Id"));
            holder.tvDocumentType.setText(Html.fromHtml("<font color=#004C00> " + DocDetails.get(arg0).get("DocumentType") + "</font>"));
            holder.tvDocumentPath.setText(DocDetails.get(arg0).get("ImagePath"));
            holder.tvDocumentName.setText(DocDetails.get(arg0).get("DocumentName"));
            holder.tvDocumentNumberValue.setText(DocDetails.get(arg0).get("DocumentNumber"));
            holder.tvUniqueId.setText(DocDetails.get(arg0).get("UniqueId"));
            if (!TextUtils.isEmpty(DocDetails.get(arg0).get("IsSync")) && DocDetails.get(arg0).get("IsSync").equalsIgnoreCase("1"))
                holder.btnViewAttach.setVisibility(View.GONE);
            else
                holder.btnViewAttach.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setTitle("Delete Proof Detail");
                    builder1.setMessage("Are you sure you want to delete this proof detail?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.open();
                                    level1Dir = "LPSMARTFARM";
                                    level2Dir = level1Dir + "/" + holder.tvUniqueId.getText().toString() + "/IdentityProof";
                                    fullPath = Environment.getExternalStorageDirectory() + "/" + level2Dir;
                                    File dir = new File(level2Dir);
                                    DeleteRecursive(dir);
                                    dba.deleteProofDetails(String.valueOf(holder.tvDocumentId.getText()));
                                    dba.close();
                                    common.showToast("Proof Detail deleted successfully.", 5, 3);
                                    DocDetails.clear();

                                    dba.open();
                                    List<AttachmentDetails> lables = dba.getProofDocument(farmerUniqueId);
                                    lsize = lables.size();
                                    if (lsize > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < lables.size(); i++) {
                                            HashMap<String, String> hm = new HashMap<String, String>();
                                            hm.put("Id", lables.get(i).getId());
                                            hm.put("DocumentType", String.valueOf(lables.get(i).getDocumentType()));
                                            hm.put("DocumentName", String.valueOf(lables.get(i).getDocumentName()));
                                            hm.put("DocumentNumber", String.valueOf(lables.get(i).getDocumentNumber()));
                                            hm.put("ImagePath", String.valueOf(lables.get(i).getImagePath()));
                                            hm.put("ImageName", String.valueOf(lables.get(i).getImageName()));
                                            hm.put("IsSync", String.valueOf(lables.get(i).getIsSync()));
                                            DocDetails.add(hm);
                                        }
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        btnSubmit.setVisibility(View.VISIBLE);
                                    }
                                    dba.close();

                                    Cadapter = new CustomAdapter(FarmerPOAPOI.this, DocDetails);
                                    if (lsize > 0) {
                                        lvList.setAdapter(Cadapter);
                                        tvEmpty.setVisibility(View.GONE);
                                        lvList.setVisibility(View.VISIBLE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        lvList.setVisibility(View.GONE);
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
                    AlertDialog alertnew = builder1.create();
                    alertnew.show();

                }
            });
            holder.btnViewAttach.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    try {

                        String actPath = holder.tvDocumentPath.getText().toString();
                        int pathLen = actPath.split("/").length;
                        //common.showToast("Actual Path="+actPath);
                        String newPath = actPath.split("/")[pathLen - 4];

                        // common.showToast("New Actual Path="+newPath);
                        // Check for SD Card
                        if (!Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            common.showToast("Error! No SDCARD Found!");
                        } else {
                            // Locate the image folder in your SD Card
                            File file1 = new File(actPath);
                            file = new File(file1.getParent());
                        }

                        if (file.isDirectory()) {

                            listFile = file.listFiles(new FilenameFilter() {
                                public boolean accept(File directory, String fileName) {
                                    return fileName.endsWith(".jpeg") || fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
                                }
                            });
                            // Create a String array for FilePathStrings
                            FilePathStrings = new String[listFile.length];
                            // Create a String array for FileNameStrings
                            FileNameStrings = new String[listFile.length];

                            for (int i = 0; i < listFile.length; i++) {

                                // Get the path of the image file
                                if (!listFile[i].getName().toString().toLowerCase().equals(".nomedia")) {
                                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                                    // Get the name image file
                                    FileNameStrings[i] = listFile[i].getName();

                                    Intent i1 = new Intent(FarmerPOAPOI.this, ViewImage.class);
                                    // Pass String arrays FilePathStrings
                                    i1.putExtra("filepath", FilePathStrings);
                                    // Pass String arrays FileNameStrings
                                    i1.putExtra("filename", FileNameStrings);
                                    // Pass click position
                                    i1.putExtra("position", 0);
                                    startActivity(i1);
                                }
                            }
                        }


                    } catch (Exception except) {
                        //except.printStackTrace();
                        common.showAlert(FarmerPOAPOI.this, "Error: " + except.getMessage(), false);

                    }

                }
            });
            arg1.setBackgroundColor(Color.parseColor((arg0 % 2 == 1) ? "#EEEEEE" : "#FFFFFF"));
            return arg1;
        }

    }

}
