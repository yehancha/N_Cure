package com.yehancha.jay.ncure;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncActivity extends AppCompatActivity implements RequestQueue.RequestFinishedListener<Object> {
    private TextView tvStatusApointments;
    private TextView tvStatusPatients;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SQLiteDatabase db;
    private RequestQueue rq;
    private ArrayList<Request> pushRequests = new ArrayList<>();
    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        tvStatusApointments = (TextView) findViewById(R.id.tv_status_appointments);
        tvStatusPatients = (TextView) findViewById(R.id.tv_status_patients);

        db = NCureDbHelper.getInstance(this).getReadableDatabase();
        rq = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start_sync:
                onStartSyncSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onStartSyncSelected() {
        rq.addRequestFinishedListener(this);
        Appointment[] appointments = new Appointment().selectAll(db);
        for (Appointment appointment : appointments) {
            pushAppointments(appointment);
        }
        if (appointments.length == 0) {
            rq.removeRequestFinishedListener(this);
            pullAppointments();
        }
    }

    private void pushAppointments(final Appointment appointment) {
        setText(tvStatusApointments, "Syncing...");
        StringRequest request = new StringRequest(
                Request.Method.POST, Utils.SERVER_URL + "appointments.php/user/" + Utils.getUserId(SyncActivity.this), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                appointment.delete(db);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();

                int id = appointment.getId();
                if (id != 0) {
                    params.put("id", String.valueOf(id));
                }
                params.put("time", dateFormat.format(appointment.getTime()));
                params.put("description", appointment.getDescription());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        pushRequests.add(request);
        rq.add(request);
    }

    @Override
    public void onRequestFinished(Request<Object> request) {
        pushRequests.remove(request);
        if (pushRequests.isEmpty()) {
            rq.removeRequestFinishedListener(this);
            Log.d("Sync", "Appointment pushed.");
            pullAppointments();
        }
    }

    private void pullAppointments() {
        StringRequest request = new StringRequest(
                Request.Method.GET, Utils.SERVER_URL + "/appointments.php/user/" + Utils.getUserId(SyncActivity.this), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Appointment> appointments = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(response, new TypeToken<ArrayList<Appointment>>(){}.getType());

                SQLiteDatabase db = NCureDbHelper.getInstance(SyncActivity.this).getWritableDatabase();
                for (Appointment appointment : appointments) {
                    appointment.insert(db);
                }
                Log.d("Sync", "Appointment pulled.");
                setText(tvStatusApointments, "Synced.");
                try {
                    pushPatient(new Patient().selectAll(db));
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(request);
    }

    private void pushPatient(final Patient[] patients) throws JSONException {
        setText(tvStatusPatients, "Syncing...");
        JsonArrayRequest request = new JsonArrayRequest (
                Request.Method.POST, Utils.SERVER_URL + "patients.php", new JSONArray(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(patients)), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (Patient patient : patients) {
                    patient.delete(db);
                }
                Log.d("Sync", "Patient pushed.");
                pullPatients();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        pushRequests.add(request);
        rq.add(request);
    }

    private void pullPatients() {
        StringRequest request = new StringRequest(
                Request.Method.GET, Utils.SERVER_URL + "patients.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Patient> patients = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(response, new TypeToken<ArrayList<Patient>>(){}.getType());

                SQLiteDatabase db = NCureDbHelper.getInstance(SyncActivity.this).getWritableDatabase();
                for (Patient patient : patients) {
                    patient.insert(db);
                }
                Log.d("Sync", "Patient pulled.");
                setText(tvStatusPatients, "Synced.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        active = false;
        super.onPause();
    }

    private void setText(final TextView tv, final String text) {
        if (!active) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
            }
        });
    }
}
