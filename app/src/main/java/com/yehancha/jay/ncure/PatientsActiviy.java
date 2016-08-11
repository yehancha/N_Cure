package com.yehancha.jay.ncure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class PatientsActiviy extends ActionButtonActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ArrayAdapter<Patient> patientAdapter;

    private ImageButton ibNewPatient;
    private ListView lvPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_activiy);
        initFields();
        findViews();
        initViews();
    }

    private void initFields() {
        patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    }

    private void findViews() {
        ibNewPatient = (ImageButton) findViewById(R.id.ib_new_patient);
        lvPatients = (ListView) findViewById(R.id.lv_patients);
    }

    private void initViews() {
        lvPatients.setAdapter(patientAdapter);
        lvPatients.setOnItemClickListener(this);
        ibNewPatient.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatients();
    }

    private void loadPatients() {
        Patient[] patients = new Patient().selectAll(NCureDbHelper.getInstance(this).getReadableDatabase());
        patientAdapter.clear();
        patientAdapter.addAll(patients);
        patientAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Patient patient = patientAdapter.getItem(position);
        startPatientActivity(false, patient.get_id());
    }

    private void startPatientActivity(boolean newPatient, long patientId) {
        Intent intent = new Intent(this, PatientActivity.class);

        if (!newPatient) {
            intent.putExtra(PatientActivity.EXTRA_PATIENT_ID, patientId);
        }

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_new_patient:
                startPatientActivity(true, 0l);
                break;

            default:
                super.onClick(v);
        }
    }
}
