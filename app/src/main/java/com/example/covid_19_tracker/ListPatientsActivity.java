package com.example.covid_19_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListPatientsActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private ListView lsPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_patients);
        lsPatients = (ListView) findViewById(R.id.lsPatients);
        myDatabaseHelper = new DatabaseHelper(this);
        ArrayList<Patient> patientList = myDatabaseHelper.getAllPatients();

        populateListView();
    }

    private void populateListView() {
        ArrayList<Patient> patientList = myDatabaseHelper.getAllPatients();
        Collections.sort(patientList, new Comparator<Patient>() {
            @Override
            public int compare(Patient p1, Patient p2) {
                if(p1.visitied_aps.size() >= p2.visitied_aps.size())
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        });

        if(!patientList.isEmpty())
        {
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
            lsPatients.setAdapter(adapter);
        }
        else
        {
            toastMessage("There are no patients available");
        }
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
