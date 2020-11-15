package com.example.covid_19;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "covid_patients";
    private static final String COL0 = "patient_id";
    private static final String COL1 = "patient_name";
    private static final String COL2 = "phone_number";
    private static final String COL3 = "mac_address";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public boolean addPatient(Patient patient)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1, patient.getName());
        values.put(COL2, patient.getNumber());
        values.put(COL3, patient.getMac());

        Log.d(TAG, "addPatient: adding patient " + patient.getName() + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        }

        return true;
    }

    public void getPatient(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String getPatient = "SELECT * FROM "+ TABLE_NAME;
        Cursor data = db.rawQuery(getPatient, null);
    }
}
