package com.yehancha.jay.ncure;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NCureDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "NCure.db";
    private static NCureDbHelper dbHelperInstance;

    public static synchronized NCureDbHelper getInstance(Context context) {
        if (dbHelperInstance == null) {
            dbHelperInstance = new NCureDbHelper(context.getApplicationContext());
        }
        return dbHelperInstance;
    }

    private NCureDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NCureContract.SQL_CREATE_APPOINTMENTS);
        db.execSQL(NCureContract.SQL_CREATE_PATIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NCureContract.SQL_DELETE_APPOINTMENTS);
        db.execSQL(NCureContract.SQL_DELETE_PATIENTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
