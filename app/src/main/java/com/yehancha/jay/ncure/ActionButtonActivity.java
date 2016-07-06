package com.yehancha.jay.ncure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActionButtonActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnNewPatient;

    @Override
    protected void onResume() {
        super.onResume();
        findViews();
        setListeners();
    }

    private void findViews() {
        btnNewPatient = (Button) findViewById(R.id.btn_new_patient);
    }

    private void setListeners() {
        btnNewPatient.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_patient:
                onNewPatientClick();
        }
    }

    private void onNewPatientClick() {
        startActivity(new Intent(this, PatientActivity.class));
    }
}
