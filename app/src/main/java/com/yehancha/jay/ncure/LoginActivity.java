package com.yehancha.jay.ncure;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private static final int LENGTH_USER_ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utils.getUserId(this) != null) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        ((EditText) findViewById(R.id.et_user_id)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                final String userId = s.toString();
                if (userId.length() == LENGTH_USER_ID) {
                    if (userId.equals("0000")) {
                        Utils.setUserId(LoginActivity.this, userId.toString());
                        startMainActivity();
                    } else {
                        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Login in", "Please wait while we log you in.");
                        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                        StringRequest request = new StringRequest(Request.Method.GET, Utils.SERVER_URL + "users.php/" + s.toString(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Utils.setUserId(LoginActivity.this, userId.toString());
                                startMainActivity();
                                updateAppointments(userId);
                                updatePatients();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                    showAlertDialog("Wrong user id", "You have entered a wrong user id. Please try again.");
                                } else {
                                    showAlertDialog("Login failed", "Something went wrong when trying to log you in. Please try again.");
                                }
                            }
                        });
                        requestQueue.add(request);
                    }
                }
            }
        });
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateAppointments(String userId) {
        StringRequest request = new StringRequest(
                Request.Method.GET, Utils.SERVER_URL + "/appointments.php/user/" + userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Appointment> appointments = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(response, new TypeToken<ArrayList<Appointment>>(){}.getType());

                SQLiteDatabase db = NCureDbHelper.getInstance(LoginActivity.this).getWritableDatabase();
                for (Appointment appointment : appointments) {
                    appointment.insert(db);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void updatePatients() {
        StringRequest request = new StringRequest(
                Request.Method.GET, Utils.SERVER_URL + "/patients.php/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Patient> patients = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(response, new TypeToken<ArrayList<Patient>>(){}.getType());

                SQLiteDatabase db = NCureDbHelper.getInstance(LoginActivity.this).getWritableDatabase();
                for (Patient patient : patients) {
                    patient.insert(db);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(request);
    }
}
