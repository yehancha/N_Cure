package com.yehancha.jay.ncure;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AppointmentsActivity extends ActionButtonActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_APPOINTMENT_ID = "EXTRA_APPOINTMENT_ID";

    private boolean newAppointment;
    private long appointmentId;
    private Appointment appointment;
    private SQLiteDatabase db;

    private Button btnSave;
    private EditText etDescription;
    private TextView tvDate;
    private TextView tvTime;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        initFields();
        findViews();
        setViewData();
        setListeners();
    }

    private void findViews() {
        btnSave = (Button) findViewById(R.id.btn_save);
        etDescription = (EditText) findViewById(R.id.et_description);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
    }

    private void initFields() {
        appointmentId = getExtraAppointmentId();
        newAppointment = appointmentId == 0l;
        db = NCureDbHelper.getInstance(this).getWritableDatabase();

        if (!newAppointment) {
            loadAppointment();
            calendar.setTime(appointment.getTime());
        }
    }

    private long getExtraAppointmentId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getLong(EXTRA_APPOINTMENT_ID, 0l);
        } else {
            return 0l;
        }
    }

    private void loadAppointment() {
        appointment = new Appointment().select(db, appointmentId);
    }

    private void setViewData() {
        showDateTimeValues();
        if (!newAppointment) {
            etDescription.setText(appointment.getDescription());
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
            default:
                super.onClick(v);
        }
    }

    private void onSaveClick() {
        if (newAppointment) {
            appointment = new Appointment();
            appointment.setUserId(Utils.getUserId(this));
        }

        appointment.setTime(calendar.getTime());
        appointment.setDescription(etDescription.getText().toString());

        if (!validateAppointment(appointment)) {
            return;
        }

        appointment.save(db);
        onBackPressed();
    }

    private boolean validateAppointment(Appointment appointment) {
        if (!Utils.isValidString(appointment.getUserId())) {
            showToast(getString(R.string.msg_invalid_login_data));
            startLoginActivity();
            return false;
        } else if (!Utils.isValidString(appointment.getDescription())) {
            showToast(getString(R.string.msg_empty_description));
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onDateClick() {
        DateTimeManager.showDatePicker(calendar, getSupportFragmentManager());
    }

    private void onTimeClick() {
        DateTimeManager.showTimePicker(calendar, getSupportFragmentManager());
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
