package lateralpraxis.lpsmartfarm.nursery;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lateralpraxis.lpsmartfarm.Common;
import lateralpraxis.lpsmartfarm.DatabaseAdapter;
import lateralpraxis.lpsmartfarm.R;
import lateralpraxis.lpsmartfarm.UserSessionManager;
import lateralpraxis.type.CustomType;

public class ActivityUpdateNurseryPlantation extends Activity {

    /*Code for Class Declaration*/
    Common common;
    DatabaseAdapter dba;
    UserSessionManager session;

    /*Code for control declaration*/
    private Spinner spPlantationZone, spPlantationCrop, spPlantationVariety, spPlantationMonthAge,
            spPlantationSystem, spPlantationType;
    private TextView tvPlantationId, tvNurseryUniqueId, tvNurseryId, tvNurseryType, tvNurseryName,
            tvPlantationDate, tvPlantationTotal;
    private EditText etPlantationArea, etPlantationRow, etPlantationColumn, etPlantationBalance;
    private Button btnBack, btnSave;

    /*Variable declaration*/
    private String userId;
    private int varietyId = 0;

    private Calendar calendar;
    private int year, month, day;

    private String plantationUniqueId = "", nurseryUniqueId, nurseryId, nurseryType, nurseryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nursery_plantation);

        /*Setup ActionBar*/
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        /*Database Instance*/
        dba = new DatabaseAdapter(this);
        common = new Common(this);
        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getLoginUserDetails();
        userId = user.get(UserSessionManager.KEY_ID);

        /*Finding Controls*/
        tvNurseryUniqueId = (TextView) findViewById(R.id.tvNurseryUniqueId);
        tvPlantationId = (TextView) findViewById(R.id.tvPlantationId);
        tvNurseryId = (TextView) findViewById(R.id.tvNurseryId);
        tvNurseryType = (TextView) findViewById(R.id.tvNurseryType);
        tvNurseryName = (TextView) findViewById(R.id.tvNurseryName);
        tvPlantationDate = (TextView) findViewById(R.id.tvPlantationDate);

        spPlantationZone = (Spinner) findViewById(R.id.spPlantationZone);
        spPlantationCrop = (Spinner) findViewById(R.id.spPlantationCrop);
        spPlantationVariety = (Spinner) findViewById(R.id.spPlantationVariety);
        spPlantationMonthAge = (Spinner) findViewById(R.id.spPlantationMonthAge);
        spPlantationType = (Spinner) findViewById(R.id.spPlantationType);
        spPlantationSystem = (Spinner) findViewById(R.id.spPlantationSystem);

        etPlantationArea = (EditText) findViewById(R.id.etPlantationArea);
        etPlantationRow = (EditText) findViewById(R.id.etPlantationRow);
        etPlantationColumn = (EditText) findViewById(R.id.etPlantationColumn);
        etPlantationBalance = (EditText) findViewById(R.id.etPlantationBalance);
        tvPlantationTotal = (TextView) findViewById(R.id.tvPlantationTotal);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime()));

        /*Code to get data from posted page*/
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            plantationUniqueId = extras.getString("plantationUniqueId");
            nurseryUniqueId = extras.getString("nurseryUniqueId");
            nurseryId = extras.getString("nurseryId");
            nurseryType = extras.getString("nurseryType");
            nurseryName = extras.getString("nurseryName");
            tvNurseryId.setText(nurseryId);
            tvNurseryType.setText(nurseryType);
            tvNurseryName.setText(nurseryName);
        }

         /*Binding data to Spinners*/
        spPlantationZone.setAdapter(DataAdapter("nurseryzone", nurseryId));
        spPlantationCrop.setAdapter(DataAdapter("crop", ""));
        spPlantationMonthAge.setAdapter(DataAdapter("monthAge", ""));
        spPlantationType.setAdapter(DataAdapter("plantType", ""));
        spPlantationSystem.setAdapter(DataAdapter("plantingSystem", ""));


        /*Binding data on Spinner item selected*/
        spPlantationCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spPlantationVariety.setAdapter(DataAdapter("variety", String.valueOf(((CustomType) spPlantationCrop.getSelectedItem()).getId())));
                if (varietyId > 0) {
                    int cnt = spPlantationCrop.getAdapter().getCount();
                    for (int i = 0; i < cnt; i++) {
                        if (((CustomType) spPlantationVariety.getItemAtPosition(i)).getId().equals(varietyId))
                            spPlantationVariety.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        /*Binding onFocusChangeLister to etPlantationRow*/
        etPlantationRow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });


        etPlantationRow.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationRow.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        /*Binding onFocusChangeLister to etPlantationColumn*/
        etPlantationColumn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        etPlantationColumn.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationColumn.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        /*Binding onFocusChangeLister to etPlantationBalance*/
        etPlantationBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus) {
                } else {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        etPlantationBalance.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }

            public void afterTextChanged(Editable s) {
                if (etPlantationBalance.getText().length() > 0) {
                    tvPlantationTotal.setText(getPlantationTotal());
                }
            }
        });

        dba.open();
        ArrayList<String> list = dba.getNurseryPlantationDetailByUniqueId(plantationUniqueId);
        tvNurseryUniqueId.setText(nurseryUniqueId);
        tvNurseryId.setText(nurseryId);
        tvNurseryType.setText(nurseryType);
        tvNurseryName.setText(nurseryName);

        /*Set Zone*/
        for (int i = 0; i < spPlantationZone.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationZone.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(1))))
                spPlantationZone.setSelection(i);
        }

        /*Set Crop*/
        for (int i = 0; i < spPlantationCrop.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationCrop.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(3))))
                spPlantationCrop.setSelection(i);
        }

        /*Set Variety*/
        spPlantationVariety.setAdapter(DataAdapter("variety", String.valueOf( list.get(3))));
        for (int i = 0; i < spPlantationVariety.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationVariety.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(5))))
                spPlantationVariety.setSelection(i);
        }

        /*Set Type*/
        for (int i = 0; i < spPlantationType.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationType.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(7))))
                spPlantationType.setSelection(i);
        }

        /*Set MonthAge*/
        for (int i = 0; i < spPlantationMonthAge.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationMonthAge.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(9))))
                spPlantationMonthAge.setSelection(i);
        }

        etPlantationArea.setText(list.get(11));
        tvPlantationDate.setText(list.get(12).replace("/","-"));

        /*Set PlantingSystem*/
        for (int i = 0; i < spPlantationSystem.getAdapter().getCount(); i++) {
            if (((CustomType) spPlantationSystem.getItemAtPosition(i)).getId().equals(Integer.valueOf(list.get(13))))
                spPlantationSystem.setSelection(i);
        }

        etPlantationRow.setText(list.get(15));
        etPlantationColumn.setText(list.get(16));
        etPlantationBalance.setText(list.get(17));
        tvPlantationTotal.setText(list.get(18));

        /*Save button click Listener*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Check mandatory fields*/

                if ((spPlantationCrop.getSelectedItemPosition() == 0)) {
                    common.showToast("Crop is mandatory");
                } else if ((spPlantationVariety.getSelectedItemPosition() == 0)) {
                    common.showToast("Variety is mandatory");
                } else if ((spPlantationType.getSelectedItemPosition() == 0)) {
                    common.showToast("Plant Type is mandatory");
                } else if ((spPlantationMonthAge.getSelectedItemPosition() == 0)) {
                    common.showToast("Month Age is mandatory");
                } else if ((spPlantationSystem.getSelectedItemPosition() == 0)) {
                    common.showToast("System is mandatory");
                } else if (String.valueOf(etPlantationArea.getText()).trim().equals("")) {
                    /*etPlantationArea.setError("Area is mandatory");
                    etPlantationArea.requestFocus();*/
                    common.showToast("Area is mandatory");
                } else if (Integer.valueOf(String.valueOf(etPlantationArea.getText()).trim()) <= 0) {
                    /*etPlantationArea.setError("Area cannot be 0");
                    etPlantationArea.requestFocus();*/
                    common.showToast("Area cannot be 0");
                } else if (String.valueOf(tvPlantationDate.getText()).trim().equals("")) {
                    /*etPlantationDate.setError("Date is mandatory");
                    etPlantationDate.requestFocus();*/
                    common.showToast("Date is mandatory");
                } else if (String.valueOf(etPlantationRow.getText()).trim().equals("")) {
                    /*etPlantationRow.setError("Plant Row is mandatory");
                    etPlantationRow.requestFocus();*/
                    common.showToast("Plant Row is mandatory");
                } else if (Integer.valueOf(String.valueOf(etPlantationRow.getText()).trim()) <= 0) {
                    /*etPlantationRow.setError("Plant Row cannot be 0");
                    etPlantationRow.requestFocus();*/
                    common.showToast("Plant Row cannot be 0");
                } else if (String.valueOf(etPlantationColumn.getText()).trim().equals("")) {
                    /*etPlantationColumn.setError("Plant Column is mandatory");
                    etPlantationColumn.requestFocus();*/
                    common.showToast("Plant Column is mandatory");
                } else if (Integer.valueOf(String.valueOf(etPlantationColumn.getText()).trim()) <= 0) {
                    /*etPlantationColumn.setError("Plant Column cannot be 0");
                    etPlantationColumn.requestFocus();*/
                    common.showToast("Plant Column cannot be 0");
                /*} else if (Integer.valueOf(String.valueOf(tvPlantationTotal.getText()).trim()) <= 0) {
                    common.showToast("Total cannot be 0");*/
                } else if (spPlantationMonthAge.getSelectedItem().toString().trim() == "0"
                        && spPlantationType.getSelectedItem().toString().toLowerCase().trim() != "seedling") {
                    common.showToast("MonthAge 0 is allowed only for Seedling type");
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityUpdateNurseryPlantation.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder
                            .setMessage("Are you sure you want to save Nursery Plantation?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    plantationUniqueId = UUID.randomUUID().toString();
                                    dba.open();
                                   /* dba.insertNurseryPlantation(plantationUniqueId, nurseryId,
                                            String.valueOf(((CustomType) spPlantationZone.getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationCrop.getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationVariety.getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationType.getSelectedItem()).getId()),
                                            String.valueOf(((CustomType) spPlantationMonthAge.getSelectedItem()).getId()),
                                            etPlantationArea.getText().toString().trim(),
                                            tvPlantationDate.getText().toString().trim(),
                                            String.valueOf(((CustomType) spPlantationSystem.getSelectedItem()).getId()),
                                            etPlantationRow.getText().toString().trim(),
                                            etPlantationColumn.getText().toString().trim(),
                                            etPlantationBalance.getText().toString().trim(),
                                            tvPlantationTotal.getText().toString().trim(),
                                            userId);*/
                                    dba.close();

                                    Intent intent = new Intent(ActivityUpdateNurseryPlantation.this, ActivityViewNurseryPlantationList.class);
                                    intent.putExtra("nurseryId", nurseryId);
                                    intent.putExtra("nurseryType", nurseryType);
                                    intent.putExtra("nurseryName", nurseryName);
                                    intent.putExtra("plantationUniqueId", plantationUniqueId);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                /*Just close the dialog without doing anything*/
                                    dialog.cancel();
                                }
                            });
                /*Create and Show alert dialog box*/
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });
    }

    //<editor-fold desc="Methods to display the Calendar">
    private DatePickerDialog.OnDateSetListener plantationDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    calendar.set(arg1, arg2, arg3);
                    showDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime()));
                }
            };

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, plantationDateListener, year, month, day);
        }
        return null;
    }

    private void showDate(String date) {
        tvPlantationDate.setText(date);
    }
    //</editor-fold>

    //<editor-fold desc="Method to fetch data and bind to spinner">
    private ArrayAdapter<CustomType> DataAdapter(String masterType, String filter) {
        dba.open();
        List<CustomType> lables = dba.GetMasterDetails(masterType, filter);
        ArrayAdapter<CustomType> dataAdapter = new ArrayAdapter<CustomType>(this, android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dba.close();
        return dataAdapter;
    }
    //</editor-fold>

    //<editor-fold desc="Calculate the Total Plantation value based on Row, Column and Balance">
    private String getPlantationTotal() {
        Integer row, column, bal, total;
        row = Integer.valueOf((etPlantationRow.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationRow.getEditableText().toString().trim()));
        column = Integer.valueOf((etPlantationColumn.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationColumn.getEditableText().toString().trim()));
        bal = Integer.valueOf((etPlantationBalance.getEditableText().toString().trim().isEmpty()
                ? "0"
                : etPlantationBalance.getEditableText().toString().trim()));
        total = (row * column) + bal;
        return total.toString();
    }
    //</editor-fold>
}
