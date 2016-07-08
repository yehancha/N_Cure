package com.yehancha.jay.ncure;

import android.provider.BaseColumns;

public class NCureContract {
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String COMA_SEP = ", ";

    public static final String SQL_CREATE_APPOINTMENTS =
            "CREATE TABLE " + Appointment.TABLE_NAME + " (" +
                    Appointment._ID + TYPE_INTEGER + " PRIMARY KEY" + COMA_SEP +
                    Appointment.COLUMN_NAME_ID + TYPE_INTEGER  + COMA_SEP +
                    Appointment.COLUMN_NAME_USER_ID + TYPE_TEXT + COMA_SEP +
                    Appointment.COLUMN_NAME_TIME + TYPE_INTEGER + COMA_SEP +
                    Appointment.COLUMN_NAME_DESCRIPTION + TYPE_TEXT +
                    ")";

    public static final String SQL_CREATE_PATIENTS =
            "CREATE TABLE " + Patient.TABLE_NAME + " (" +
                    Patient._ID + TYPE_INTEGER + " PRIMARY KEY" + COMA_SEP +
                    Patient.COLUMN_NAME_ID + TYPE_INTEGER  + COMA_SEP +
                    Patient.COLUMN_NAME_NAME + TYPE_TEXT + COMA_SEP +
                    Patient.COLUMN_NAME_ADDRESS + TYPE_TEXT + COMA_SEP +
                    Patient.COLUMN_NAME_CITY + TYPE_TEXT + COMA_SEP +
                    Patient.COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMA_SEP +
                    Patient.COLUMN_NAME_DISEASE + TYPE_TEXT + COMA_SEP +
                    Patient.COLUMN_NAME_LAST_UPDATED + TYPE_INTEGER +
                    ")";

    public static final String SQL_DELETE_APPOINTMENTS = "DROP TABLE IF EXISTS " + Appointment.TABLE_NAME;
    public static final String SQL_DELETE_PATIENTS = "DROP TABLE IF EXISTS " + Patient.TABLE_NAME;

    private NCureContract() {}

    public static abstract class Appointment implements NCureBaseColumns {
        public static final String TABLE_NAME = "appointment";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public static abstract class Patient implements NCureBaseColumns {
        public static final String TABLE_NAME = "patient";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DISEASE = "disease";
        public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
    }

    public interface NCureBaseColumns extends BaseColumns {
        String COLUMN_NAME_ID = "id";
    }
}
