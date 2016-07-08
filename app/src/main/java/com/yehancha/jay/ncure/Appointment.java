package com.yehancha.jay.ncure;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Represents a nurses appointment
 */
public class Appointment extends BaseModel<Appointment> {
    private String user_id;
    private Date time;
    private String description;

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

    @Override
    public void onCursor(Cursor cursor, Appointment appointment) {
        appointment.user_id = cursor.getString(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_USER_ID));
        appointment.time = new Date(cursor.getLong(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_TIME)));
        appointment.description = cursor.getString(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_DESCRIPTION));
    }

    @Override
    protected void onContentValues(ContentValues values) {
        values.put(NCureContract.Appointment.COLUMN_NAME_USER_ID, user_id);
        values.put(NCureContract.Appointment.COLUMN_NAME_DESCRIPTION, description);

        if (time != null) {
            values.put(NCureContract.Appointment.COLUMN_NAME_TIME, time.getTime());
        }
    }

    @Override
    public String toString() {
        return DateTimeManager.DATE_FORMAT.format(time) + ": " + description;
    }

    @Override
    public String getTableName() {
        return NCureContract.Appointment.TABLE_NAME;
    }
}
