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
    private static final String VISITED_TABLE_NAME = "visited_accesspoints";
    private static final String ACCESSPOINT_IP = "accesspoint_ip";

    public DatabaseHelper(@Nullable Context context) {
        super(context, PATIENTS_TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + PATIENTS_TABLE_NAME + " ( "+ PATIENTS_PHONE + " TEXT PRIMARY KEY, " +
                PATIENTS_NAME + " TEXT, " +
                PATIENTS_MAC + " TEXT, " +
                "UNIQUE(" + PATIENTS_MAC + "))";
        db.execSQL(createTable);

        String createSecondTable = "CREATE TABLE " + VISITED_TABLE_NAME + " ( "+ PATIENTS_MAC + " TEXT, " +
                ACCESSPOINT_IP + " TEXT, " +
                "PRIMARY KEY (" + PATIENTS_MAC + "," + ACCESSPOINT_IP +"))";
        db.execSQL(createSecondTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + PATIENTS_TABLE_NAME;
        String dropSecondTable = "DROP TABLE IF EXISTS " +  VISITED_TABLE_NAME;
        db.execSQL(dropTable);
        db.execSQL(dropSecondTable);
        onCreate(db);
    }

    public void restart()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String dropTable = "DROP TABLE IF EXISTS " + PATIENTS_TABLE_NAME;
        String dropSecondTable = "DROP TABLE IF EXISTS " +  VISITED_TABLE_NAME;
        db.execSQL(dropTable);
        db.execSQL(dropSecondTable);

        String createTable = "CREATE TABLE " + PATIENTS_TABLE_NAME + " ( "+ PATIENTS_PHONE + " TEXT PRIMARY KEY, " +
                PATIENTS_NAME + " TEXT, " +
                PATIENTS_MAC + " TEXT, " +
                "UNIQUE(" + PATIENTS_MAC + "))";
        db.execSQL(createTable);

        String createSecondTable = "CREATE TABLE " + VISITED_TABLE_NAME + " ( "+ PATIENTS_MAC + " TEXT, " +
                ACCESSPOINT_IP + " TEXT, " +
                "PRIMARY KEY (" + PATIENTS_MAC + "," + ACCESSPOINT_IP +"))";
        db.execSQL(createSecondTable);
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

    public boolean addVisitedAp(String mac, String ip)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PATIENTS_MAC, mac);
        values.put(ACCESSPOINT_IP, ip);

        Log.d(TAG, "addPatient: adding patient with mac address: " + mac + " to " + VISITED_TABLE_NAME);

        long result = db.insert(VISITED_TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        }

        return true;
    }

    public Patient getPatient(String name, String phone, String mac)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor getPatient = db.query(PATIENTS_TABLE_NAME, new String[] { PATIENTS_PHONE,
                        PATIENTS_NAME, PATIENTS_MAC }, PATIENTS_PHONE + "=? and " + PATIENTS_NAME + "=? and " + PATIENTS_MAC + "=?",
                new String[] { phone, name, mac }, null, null, null, null);

        if(getPatient.getCount() <= 0)
            return null;


        getPatient.moveToFirst();
        Patient patient = new Patient(getPatient.getString(1), getPatient.getString(0), getPatient.getString(2));
        patient.visitied_aps = getApsIps(patient);
        return patient;
    }

    public ArrayList<String> getApsIps(Patient patient)
    {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> connected_aps = new ArrayList<>();

        Cursor getConnectedAp = db.query(VISITED_TABLE_NAME, new String[] { PATIENTS_MAC,
                        ACCESSPOINT_IP }, PATIENTS_MAC + "=?",
                new String[] { patient.getMac() }, null, null, null, null);


        if (getConnectedAp.moveToFirst()) {
            do {
                connected_aps.add(getConnectedAp.getString(1));
            } while (getConnectedAp.moveToNext());
        }

        return connected_aps;
    }

    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patientList = new ArrayList<Patient>();
        String selectQuery = "SELECT * FROM " + PATIENTS_TABLE_NAME;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Patient patient = new Patient(cursor.getString(1), cursor.getString(0), cursor.getString(2));
                patient.visitied_aps = getApsIps(patient);
                patientList.add(patient);
            } while (cursor.moveToNext());
        }

        return patientList;
    }

    public void deletePatient(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PATIENTS_TABLE_NAME, PATIENTS_PHONE + "='" + phone + "'", null);
    }

    public void deleteVisitedAp(String mac, String ip)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VISITED_TABLE_NAME, PATIENTS_MAC + "='" + mac + "' AND " + ACCESSPOINT_IP + "='" + ip +"'", null);
    }

    public ArrayList<Patient> checkCases(ArrayList<Patient> connections)
    {
        ArrayList<Patient> infected = new ArrayList<>();

        if(connections.isEmpty())
            return infected;

        for(Patient p: connections)
        {
            Patient result = getPatient(p.getName(), p.getNumber(), p.getMac());
            if(result != null)
            {
                result.visitied_aps = getApsIps(result);
                infected.add(result);
            }
        }

        return infected;
    }
}

