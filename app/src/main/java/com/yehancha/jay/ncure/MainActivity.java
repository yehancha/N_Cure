package com.yehancha.jay.ncure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Appointment> appointments = Appointment.selectAll(NCureDbHelper.getInstance(this).getReadableDatabase());
        ArrayAdapter<Appointment> appointmentAdapter = new ArrayAdapter<Appointment>(this, android.R.layout.simple_list_item_1);
        appointmentAdapter.addAll(appointments);

        ((ListView) findViewById(R.id.lv_appointments)).setAdapter(appointmentAdapter);
    }
}
