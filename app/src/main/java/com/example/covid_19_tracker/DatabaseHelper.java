package com.example.covid_19_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import com.example.covid_19_tracker.Patient;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String PATIENTS_TABLE_NAME = "covid_patients";
    private static final String PATIENTS_PHONE = "phone_number";
    private static final String PATIENTS_NAME = "patient_name";
    private static final String PATIENTS_MAC = "mac_address";

    public DatabaseHelper(@Nullable Context context) {
        super(context, PATIENTS_TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + PATIENTS_TABLE_NAME + " ( "+ PATIENTS_PHONE + " TEXT PRIMARY KEY, " +
                PATIENTS_NAME + " TEXT, " +
                PATIENTS_MAC + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + PATIENTS_TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public boolean addPatient(Patient patient)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PATIENTS_PHONE, patient.getNumber());
        values.put(PATIENTS_NAME, patient.getName());
        values.put(PATIENTS_MAC, patient.getMac());

        Log.d(TAG, "addPatient: adding patient " + patient.getName() + " to " + PATIENTS_TABLE_NAME);

        long result = db.insert(PATIENTS_TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        }

        return true;
    }

    public Patient getPatient(String name, String phone, String mac)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String getPatient = "SELECT * FROM "+ PATIENTS_TABLE_NAME;

        Cursor cursor = db.query(PATIENTS_TABLE_NAME, new String[] { PATIENTS_PHONE,
                        PATIENTS_NAME, PATIENTS_MAC }, PATIENTS_PHONE + "=? and " + PATIENTS_NAME + "=? and " + PATIENTS_MAC + "=?",
                new String[] { phone, name, mac }, null, null, null, null);

        if(cursor.getCount() <= 0)
            return null;


        cursor.moveToFirst();
        Patient patient = new Patient(cursor.getString(1), cursor.getString(0), cursor.getString(2));
        return patient;
    }

    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patientList = new ArrayList<Patient>();
        String selectQuery = "SELECT * FROM " + PATIENTS_TABLE_NAME;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Patient patient = new Patient(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                patientList.add(patient);
            } while (cursor.moveToNext());
        }

        return patientList;
    }

    public void deletePatient(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PATIENTS_TABLE_NAME, PATIENTS_PHONE + "=" + phone, null);
    }

    public int checkCases(ArrayList<Patient> connections)
    {
        int cases = 0;

        if(connections.isEmpty())
            return cases;

        for(Patient p: connections)
        {
            Patient result = getPatient(p.getName(), p.getNumber(), p.getMac());
            if(result != null)
            {
                cases++;
            }
        }

        return cases;
    }
}

