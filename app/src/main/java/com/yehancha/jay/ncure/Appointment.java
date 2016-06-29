package com.yehancha.jay.ncure;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a nurses appointment
 */
public class Appointment {
    private static final SimpleDateFormat datetFormat = new SimpleDateFormat("yyyy-MM-dd");
    private transient long _id;
    private int id;
    private String user_id;
    private Date time;
    private String description;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ArrayList<Appointment> selectAll(SQLiteDatabase db) {
        Cursor cursor = db.query(NCureContract.Appointment.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Appointment> appointments = new ArrayList<>();
        while (cursor.moveToNext()) {
            appointments.add(fromCursor(cursor));
        }
        return appointments;
    }

    private static Appointment fromCursor(Cursor cursor) {
        Appointment appointment = new Appointment();
        appointment._id = cursor.getLong(cursor.getColumnIndex(NCureContract.Appointment._ID));
        appointment.id = cursor.getInt(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_ID));
        appointment.user_id = cursor.getString(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_USER_ID));
        appointment.time = new Date(cursor.getLong(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_TIME)));
        appointment.description = cursor.getString(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_DESCRIPTION));

        return appointment;
    }

    public long insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(NCureContract.Appointment.COLUMN_NAME_ID, id);
        values.put(NCureContract.Appointment.COLUMN_NAME_USER_ID, user_id);
        values.put(NCureContract.Appointment.COLUMN_NAME_TIME, time.getTime());
        values.put(NCureContract.Appointment.COLUMN_NAME_DESCRIPTION, description);

        long _id = db.insert(NCureContract.Appointment.TABLE_NAME, null, values);

        this._id = _id;

        return _id;
    }

    @Override
    public String toString() {
        return datetFormat.format(time) + ": " + description;
    }
}
