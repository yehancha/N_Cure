package com.yehancha.jay.ncure;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class PatientActivity extends ActionButtonActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView tvDate;
    private TextView tvTime;
    private EditText etName;
    private EditText etAddress;
    private EditText etCity;
    private EditText etDescription;
    private EditText etDisease;
    private EditText etId;
    private Button btnSave;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        findViews();
        showDateTimeValues();
        setListeners();
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
        btnSave = (Button) findViewById(R.id.btn_save);
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
