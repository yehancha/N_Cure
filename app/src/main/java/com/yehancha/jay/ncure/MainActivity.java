package com.yehancha.jay.ncure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter testAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        testAdapter.addAll(new String[] {"2013-08-22: Visit Clinic A", "2013-09-02: Visit Hospital B for…", "2013-09-06: Training Session at C", "2013-09-11: Visit Clinic A", "2013-09-11: Visit Clinic B"});

        ((ListView) findViewById(R.id.lv_appointments)).setAdapter(testAdapter);
    }
}
