package com.yehancha.jay.ncure;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class PatientActivity extends ActionButtonActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_PATIENT_ID = "EXTRA_PATIENT_ID";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TYPE_IMAGE_FOLDER = "image";
    private static final String FILE_NAME_TEMP_FILE = "tempFile.jpg";

    private static String FILE_PATH_TEMP_FILE;

    private boolean newPatient;
    private long patientId;
    private Patient patient;
    private SQLiteDatabase db;

    private TextView tvDate;
    private TextView tvTime;
    private EditText etName;
    private EditText etAddress;
    private EditText etCity;
    private EditText etDescription;
    private EditText etDisease;
    private EditText etId;
    private ImageView iv;
    private Button btnSave;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initFields();
        findViews();
        setViewData();
        setListeners();
    }

    private void initFields() {
        FILE_PATH_TEMP_FILE = getExternalFilesDir(TYPE_IMAGE_FOLDER) + File.separator + FILE_NAME_TEMP_FILE;

        patientId = getExtraPatientId();
        newPatient = patientId == 0l;
        db = NCureDbHelper.getInstance(this).getWritableDatabase();

        if (!newPatient) {
            loadPatient();
            Date lastUpdated = patient.getLastUpdated();
            if (lastUpdated != null) {
                calendar.setTime(patient.getLastUpdated());
            }
        }
    }

    private long getExtraPatientId() {
        Bundle extras = getIntent().getExtras();
        return extras != null ? extras.getLong(EXTRA_PATIENT_ID, 0l) : 0l;
    }

    private void loadPatient() {
        patient = new Patient().select(db, patientId);
    }

    private void findViews() {
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        etName = (EditText) findViewById(R.id.et_name);
        etAddress = (EditText) findViewById(R.id.et_address);
        etCity = (EditText) findViewById(R.id.et_city);
        etDescription = (EditText) findViewById(R.id.et_description);
        etDisease = (EditText) findViewById(R.id.et_disease);
        etId = (EditText) findViewById(R.id.et_id);
        iv = (ImageView) findViewById(R.id.iv);
        btnSave = (Button) findViewById(R.id.btn_save);
    }

    private void setViewData() {
        showDateTimeValues();
        if (!newPatient) {
            etName.setText(patient.getName());
            etAddress.setText(patient.getAddress());
            etCity.setText(patient.getCity());
            etDescription.setText(patient.getDescription());
            etDisease.setText(patient.getDisease());
            etId.setText("" + patient.getId());
        }
    }

    private void showDateTimeValues() {
        Date date = calendar.getTime();
        tvDate.setText(DateTimeManager.DATE_FORMAT.format(date));
        tvTime.setText(DateTimeManager.TIME_FORMAT.format(date));
    }

    private void setListeners() {
        btnSave.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        iv.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        DateTimeManager.onSavedInstanceState(outState, calendar);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DateTimeManager.onRestoreInstanceState(savedInstanceState, calendar);
        showDateTimeValues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                onSaveClick();
                break;

            case R.id.tv_date:
                onDateClick();
                break;

            case R.id.tv_time:
                onTimeClick();
                break;

            case R.id.iv:
                onImageViewClick();
                break;

            default:
                super.onClick(v);
        }
    }

    private void onSaveClick() {
        if (newPatient) {
            patient = new Patient();
        }

        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String city = etCity.getText().toString();
        String description = etDescription.getText().toString();
        String disease = etDisease.getText().toString();
        Date lastUpdated = new Date();

        patient.setName(name);
        patient.setAddress(address);
        patient.setCity(city);
        patient.setDescription(description);
        patient.setDisease(disease);
        patient.setLastUpdated(lastUpdated);

        if (!validatePatient(patient)) {
            return;
        }

        patient.save(NCureDbHelper.getInstance(this).getWritableDatabase());
        onBackPressed();
    }

    private boolean validatePatient(Patient patient) {
        if (!Utils.isValidString(patient.getName())) {
            showToast(getString(R.string.msg_empty_patient_name));
            etName.requestFocus();
            return false;
        } else if (!Utils.isValidString(patient.getCity())) {
            showToast(getString(R.string.msg_empty_city));
            etCity.requestFocus();
            return false;
        } else if (!Utils.isValidString(patient.getDisease())) {
            showToast(getString(R.string.msg_empty_disease));
            etDisease.requestFocus();
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onDateClick() {
        DateTimeManager.showDatePicker(calendar, getSupportFragmentManager());
    }

    private void onTimeClick() {
        DateTimeManager.showTimePicker(calendar, getSupportFragmentManager());
    }

    private void onImageViewClick() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        PackageManager pm = getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Snackbar.make(iv, "No camera functionality found!", Snackbar.LENGTH_LONG).show();
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(pm) != null) {
            startPhotoActivity(takePictureIntent);
        }
    }

    private void startPhotoActivity(Intent takePictureIntent) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(new File(FILE_PATH_TEMP_FILE).toURI().toString()));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(FILE_PATH_TEMP_FILE);
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            iv.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        showDateTimeValues();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        showDateTimeValues();
    }
}
