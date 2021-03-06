package com.yehancha.jay.ncure;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Patient extends BaseModel<Patient> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private String name;
    private String address;
    private String city;
    private String description;
    private String disease;
    private Date last_updated;
    private String imageFiles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public String getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(String imageFiles) {
        this.imageFiles = imageFiles;
    }

    @Override
    public void onCursor(Cursor cursor, Patient patient) {
        patient.name = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_NAME));
        patient.address = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_ADDRESS));
        patient.city = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_CITY));
        patient.description = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_DESCRIPTION));
        patient.disease = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_DISEASE));
        patient.imageFiles = cursor.getString(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_IMAGE_FILES));

        long lastUpdated = cursor.getLong(cursor.getColumnIndex(NCureContract.Patient.COLUMN_NAME_LAST_UPDATED));
        if (lastUpdated != 0) {
            patient.last_updated = new Date(lastUpdated);
        }

    }

    @Override
    protected void onContentValues(ContentValues values) {
        values.put(NCureContract.Patient.COLUMN_NAME_NAME, name);
        values.put(NCureContract.Patient.COLUMN_NAME_ADDRESS, address);
        values.put(NCureContract.Patient.COLUMN_NAME_CITY, city);
        values.put(NCureContract.Patient.COLUMN_NAME_DESCRIPTION, description);
        values.put(NCureContract.Patient.COLUMN_NAME_DISEASE, disease);
        values.put(NCureContract.Patient.COLUMN_NAME_IMAGE_FILES, imageFiles);

        if (last_updated != null) {
            values.put(NCureContract.Patient.COLUMN_NAME_LAST_UPDATED, last_updated.getTime());
        }
    }

    @Override
    public String getTableName() {
        return NCureContract.Patient.TABLE_NAME;
    }

    @Override
    public String toString() {
        return (last_updated != null ? dateFormat.format(last_updated) : "") + ": " + name;
    }
}
