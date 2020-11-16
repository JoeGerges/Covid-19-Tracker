package com.example.covid_19_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.covid_19_tracker.DatabaseHelper;
import com.example.covid_19_tracker.Patient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private TextInputEditText name, phone, mac;
    private Button addPatient, viewPatients, btnApConnect;
    private TextView alertTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (TextInputEditText) findViewById(R.id.name);
        phone = (TextInputEditText) findViewById(R.id.phone);
        mac = (TextInputEditText) findViewById(R.id.mac);
        addPatient = (Button) findViewById(R.id.addPatient);
        viewPatients = (Button) findViewById(R.id.viewPatients);
        btnApConnect = (Button) findViewById(R.id.btnApConnect);
        alertTextView = (TextView) findViewById(R.id.AlertTextView);

        myDatabaseHelper = new DatabaseHelper(this);
        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patient_name = name.getText().toString();
                String patient_phone = phone.getText().toString();
                String patient_mac = mac.getText().toString();
                
                if (patient_name.length() != 0 || patient_phone.length() != 0 || patient_mac.length() != 0)
                {
                    Patient patient = new Patient(patient_name, patient_phone, patient_mac);
                    addData(patient);
                }
                else
                {
                    alert("All fields must be entered", "Error");
                }
            }
            
        });

        viewPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListPatientsActivity.class);
                startActivity(intent);
            }

        });

        btnApConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ApConnectActivity.class);
                startActivity(intent);
            }

        });
    }

    public void addData(Patient patient)
    {
        boolean result = myDatabaseHelper.addPatient(patient);

        if (result)
        {
            alert("Successfully added the new patient to the database", "Success");
        }
        else
        {
            alert("Error in adding the new patient to the database", "Error");
        }
    }

    public Patient getPatient(String phone_number)
    {
        return myDatabaseHelper.getPatient(phone_number);
    }

    public List<Patient> getAllPatients()
    {
        return myDatabaseHelper.getAllPatients();
    }

    private void toastMessage(String message)
    {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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

    public void alert(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertTextView.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }
}