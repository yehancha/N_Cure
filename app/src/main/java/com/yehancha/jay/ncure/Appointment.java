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
    private static final String SELECTION_ROW_ID = NCureContract.Appointment._ID + "=?";

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

    public static Appointment select(SQLiteDatabase db, long _id) {
        ArrayList<Appointment> appointments = selectAll(db, SELECTION_ROW_ID, getSelectionArgsForRowId(_id), null);
        if (appointments.size() > 0) {
            return appointments.get(0);
        } else {
            return null;
        }
    }

    public static ArrayList<Appointment> selectAll(SQLiteDatabase db) {
        return selectAll(db, null, null, null);
    }

    public static ArrayList<Appointment> selectAll(SQLiteDatabase db, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor = db.query(NCureContract.Appointment.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
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

    public long save(SQLiteDatabase db) {
        if (_id == 0l) {
            return insert(db);
        } else {
            boolean updated = update(db);
            return updated ? 0l : -1l;
        }
    }

    public long insert(SQLiteDatabase db) {
        _id = db.insert(NCureContract.Appointment.TABLE_NAME, null, getContentValues());
        return _id;
    }

    public boolean update(SQLiteDatabase db) {
        int numberOfRowsAffected = db.update(NCureContract.Appointment.TABLE_NAME, getContentValues(), SELECTION_ROW_ID, getSelectionArgsForRowId(_id));
        return numberOfRowsAffected > 0;
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(NCureContract.Appointment.COLUMN_NAME_ID, id);
        values.put(NCureContract.Appointment.COLUMN_NAME_USER_ID, user_id);
        values.put(NCureContract.Appointment.COLUMN_NAME_TIME, time.getTime());
        values.put(NCureContract.Appointment.COLUMN_NAME_DESCRIPTION, description);
        return values;
    }

    private static String[] getSelectionArgsForRowId(long _id) {
        return new String[] { String.valueOf(_id) };
    }

    @Override
    public String toString() {
        return datetFormat.format(time) + ": " + description;
    }
}
