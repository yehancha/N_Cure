package com.yehancha.jay.ncure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppointmentsActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_APPOINTMENT_ID = "EXTRA_APPOINTMENT_ID";

    private static final String EXTRA_YEAR = "EXTRA_YEAR";
    private static final String EXTRA_MONTH = "EXTRA_MONTH";
    private static final String EXTRA_DAY_OF_MONTH = "EXTRA_DAY_OF_MONTH";
    private static final String EXTRA_HOUR_OF_DAY = "EXTRA_HOUR_OF_DAY";
    private static final String EXTRA_MINUTE = "EXTRA_MINUTE";
    private static final String SAVED_STATE_DATE_TIME = "SAVED_STATE_DATE_TIME";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

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
        appointment = Appointment.select(db, appointmentId);
    }

    private void setViewData() {
        setDateTimeValues();
        if (!newAppointment) {
            etDescription.setText(appointment.getDescription());
        }
    }

    private void setDateTimeValues() {
        Date date = calendar.getTime();
        tvDate.setText(DATE_FORMAT.format(date));
        tvTime.setText(TIME_FORMAT.format(date));
    }

    private void setListeners() {
        btnSave.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(SAVED_STATE_DATE_TIME, getDateTimeBundle());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle dateTimeBundle = savedInstanceState.getBundle(SAVED_STATE_DATE_TIME);
        restoreDateTime(dateTimeBundle);
    }

    private void restoreDateTime(Bundle dateTimeBundle) {
        calendar.set(
                dateTimeBundle.getInt(EXTRA_YEAR),
                dateTimeBundle.getInt(EXTRA_MONTH),
                dateTimeBundle.getInt(EXTRA_DAY_OF_MONTH),
                dateTimeBundle.getInt(EXTRA_HOUR_OF_DAY),
                dateTimeBundle.getInt(EXTRA_MINUTE)
        );
        setDateTimeValues();
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
        }
    }

    private void onSaveClick() {
        if (newAppointment) {
            appointment = new Appointment();
            appointment.setUserId(Utils.getUserId(this));
        }

        appointment.setTime(calendar.getTime());
        appointment.setDescription(etDescription.getText().toString());

        if (!isValidAppointment(appointment)) {
            return;
        }

        appointment.save(db);
        onBackPressed();
    }

    private boolean isValidAppointment(Appointment appointment) {
        if (!isValidString(appointment.getUserId())) {
            showToast(getString(R.string.msg_invalid_login_data));
            startLoginActivity();
            return false;
        } else if (!isValidString(appointment.getDescription())) {
            showToast(getString(R.string.msg_empty_description));
            return false;
        }

        return true;
    }

    private boolean isValidString(String s) {
        return s != null && !s.isEmpty() && !s.trim().isEmpty();
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
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(getDateTimeBundle());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void onTimeClick() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(getDateTimeBundle());
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Bundle getDateTimeBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_YEAR, calendar.get(Calendar.YEAR));
        bundle.putInt(EXTRA_MONTH, calendar.get(Calendar.MONTH));
        bundle.putInt(EXTRA_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        bundle.putInt(EXTRA_HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        bundle.putInt(EXTRA_MINUTE, calendar.get(Calendar.MINUTE));
        return bundle;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        setDateTimeValues();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        setDateTimeValues();
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();

            int year = arguments.getInt(EXTRA_YEAR);
            int month = arguments.getInt(EXTRA_MONTH);
            int day = arguments.getInt(EXTRA_DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();

            int hour = arguments.getInt(EXTRA_HOUR_OF_DAY);
            int minute = arguments.getInt(EXTRA_MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getContext(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, false /* is not 24 hour format*/);
        }
    }
}
