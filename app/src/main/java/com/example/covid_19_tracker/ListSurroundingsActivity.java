package com.example.covid_19_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListSurroundingsActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private ListView lsSurroundings;
    ArrayList<String> infectedSurroundings;
    private TextView alertTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_surroundings);
        lsSurroundings = (ListView) findViewById(R.id.lsInfections);
        myDatabaseHelper = new DatabaseHelper(this);
        alertTextView = (TextView) findViewById(R.id.AlertTextView3);

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("BUNDLE");
        infectedSurroundings = (ArrayList<String>) b.getStringArrayList("infectionList");
        populateListView();
    }

    private void populateListView() {
        if(!infectedSurroundings.isEmpty())
        {
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infectedSurroundings);
            lsSurroundings.setAdapter(adapter);
        }
        else
        {
            alert("Nothing to list", "Not Found");
        }
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }




    public void alert(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListSurroundingsActivity.this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertTextView.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }
}
