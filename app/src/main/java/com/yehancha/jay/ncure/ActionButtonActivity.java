package com.yehancha.jay.ncure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ActionButtonActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnNewPatient;
    private Button btnPatients;

    @Override
    protected void onResume() {
        super.onResume();
        findViews();
        setListeners();
    }

    private void findViews() {
        btnNewPatient = (Button) findViewById(R.id.btn_new_patient);
        btnPatients = (Button) findViewById(R.id.btn_patients);
    }

    private void setListeners() {
        btnNewPatient.setOnClickListener(this);
        btnPatients.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                onSyncSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSyncSelected() {
        startActivity(new Intent(this, SyncActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_patient:
                onNewPatientClick();
                break;

            case R.id.btn_patients:
                onPatientsClick();
        }
    }

    private void onNewPatientClick() {
        startActivity(new Intent(this, PatientActivity.class));
    }

    private void onPatientsClick() {
        startActivity(new Intent(this, PatientsActiviy.class));
    }
}
