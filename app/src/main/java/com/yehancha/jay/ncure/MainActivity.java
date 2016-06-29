package com.yehancha.jay.ncure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Appointment> appointments = Appointment.selectAll(NCureDbHelper.getInstance(this).getReadableDatabase());
        ArrayAdapter<Appointment> appointmentAdapter = new ArrayAdapter<Appointment>(this, android.R.layout.simple_list_item_1);
        appointmentAdapter.addAll(appointments);

        ((ListView) findViewById(R.id.lv_appointments)).setAdapter(appointmentAdapter);

        findViewById(R.id.btn_new_appointment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AppointmentsActivity.class));
            }
        });
    }
}
