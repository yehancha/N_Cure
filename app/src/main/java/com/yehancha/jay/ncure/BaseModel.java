package com.yehancha.jay.ncure;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;

public abstract class BaseModel<T extends BaseModel> {
    final transient Class<T> classT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    final transient Class<T[]> classArrayT = (Class<T[]>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    private static final String SELECTION_ROW_ID = NCureContract.NCureBaseColumns._ID + "=?";

    private static final SimpleDateFormat datetFormat = new SimpleDateFormat("yyyy-MM-dd");

    private transient long _id;
    private int id;

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

    public T select(SQLiteDatabase db, long _id) {
        T[] models = selectAll(db, SELECTION_ROW_ID, getSelectionArgsForRowId(_id), null);
        if (models.length > 0) {
            return models[0];
        } else {
            return null;
        }
    }

    public T[] selectAll(SQLiteDatabase db) {
        return selectAll(db, null, null, null);
    }

    public T[] selectAll(SQLiteDatabase db, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor = db.query(getTableName(), null, selection, selectionArgs, null, null, orderBy);
        T[] models = (T[]) Array.newInstance(classArrayT, cursor.getCount());
        for (int i = 0; cursor.moveToNext();) {
            models[i++] = fromCursor(cursor);
        }
        return models;
    }

    private T fromCursor(Cursor cursor) {
        T model = null;
        try {
            model = (T) classT.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        model.set_id(cursor.getLong(cursor.getColumnIndex(NCureContract.Appointment._ID)));
        model.setId(cursor.getInt(cursor.getColumnIndex(NCureContract.Appointment.COLUMN_NAME_ID)));

        onCursor(cursor, model);

        return model;
    }

    public void onCursor(Cursor cursor, T model) {
        throw new UnsupportedOperationException();
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
        _id = db.insert(getTableName(), null, getContentValues());
        return _id;
    }

    public boolean update(SQLiteDatabase db) {
        int numberOfRowsAffected = db.update(getTableName(), getContentValues(), SELECTION_ROW_ID, getSelectionArgsForRowId(_id));
        return numberOfRowsAffected > 0;
    }

    public String getTableName() {
        throw new UnsupportedOperationException();
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(NCureContract.Appointment.COLUMN_NAME_ID, id);

        onContentValues(values);

        return values;
    }

    protected void onContentValues(ContentValues values) {
        throw new UnsupportedOperationException();
    }

    private static String[] getSelectionArgsForRowId(long _id) {
        return new String[] { String.valueOf(_id) };
    }
}
