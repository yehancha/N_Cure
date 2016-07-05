package com.yehancha.jay.ncure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayAdapter<Appointment> appointmentAdapter;

    private ImageButton ibNewAppointment;
    private ListView lvAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        findViews();
        initViews();
    }

    private void findViews() {
        ibNewAppointment = (ImageButton) findViewById(R.id.ib_new_appointment);
        lvAppointments = (ListView) findViewById(R.id.lv_appointments);
    }

    private void initFields() {
        appointmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    }

    private void initViews() {
        lvAppointments.setAdapter(appointmentAdapter);
        lvAppointments.setOnItemClickListener(this);
        ibNewAppointment.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        ArrayList<Appointment> appointments = Appointment.selectAll(NCureDbHelper.getInstance(this).getReadableDatabase());
        appointmentAdapter.clear();
        appointmentAdapter.addAll(appointments);
        appointmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        startAppointmentActivity(true, 0l);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Appointment appointment = appointmentAdapter.getItem(position);
        startAppointmentActivity(false, appointment.get_id());
    }

    private void startAppointmentActivity(boolean newAppointment, long appointmentId) {
        Intent intent = new Intent(MainActivity.this, AppointmentsActivity.class);

        if (!newAppointment) {
            intent.putExtra(AppointmentsActivity.EXTRA_APPOINTMENT_ID, appointmentId);
        }

        startActivity(intent);
    }
}
