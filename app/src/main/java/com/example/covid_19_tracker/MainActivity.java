package com.example.covid_19_tracker;

import android.os.Bundle;

import com.example.covid_19.DatabaseHelper;
import com.example.covid_19.Patient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private EditText name, phone, mac;
    private Button addPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabaseHelper = new DatabaseHelper(this);

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patient_name = name.getText().toString();
                String patient_phone = phone.getText().toString();
                String patient_mac = mac.getText().toString();
                
                if (patient_name.length() != 0 || patient_phone.length() != 0 || patient_mac.length() != 0)
                {
                    Patient patient = new Patient(patient_name, patient_phone, patient_phone);
                    addData(patient);
                }
                else
                {
                    toastMessage("All fields must be entered");
                }
            }
            
        });
    }

    public void addData(Patient patient)
    {
        boolean result = myDatabaseHelper.addPatient(patient);

        if (result)
        {
            toastMessage("Successfully added the new patient to the database");
        }
        else
        {
            toastMessage("Error in adding the new patient to the database");
        }
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}