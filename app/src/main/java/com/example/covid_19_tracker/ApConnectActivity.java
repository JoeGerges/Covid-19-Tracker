package com.example.covid_19_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ApConnectActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private TextInputEditText txtIpAddress, txtPortNumber;
    private Button btnConnect;
    int SERVER_PORT, totalCases;
    String SERVER_IP;
    Thread Thread1 = null;
    private ArrayList<Patient> connectivityList;
    private TextView alertTextView;
    private boolean connected = false;
    public String serverResponse = "";
    private ArrayList<Patient> infectedPatients;

    public void alert(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApConnectActivity.this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        if(infectedPatients.isEmpty() || title.equals("Error"))
        {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertTextView.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertTextView.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(ApConnectActivity.this, ListSurroundingsActivity.class);
                    Bundle b = new Bundle();
                    ArrayList<String> parsedInfections = new ArrayList<>();
                    for (Patient p : infectedPatients) {
                        parsedInfections.add(p.toString());
                    }
                    b.putStringArrayList("infectionList", parsedInfections);
                    intent.putExtra("BUNDLE", b);
                    startActivity(intent);
                }
            });
        }
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_ap);
        myDatabaseHelper = new DatabaseHelper(this);

        txtIpAddress = (TextInputEditText) findViewById(R.id.txtIpAddress);
        txtPortNumber = (TextInputEditText) findViewById(R.id.txtPortNumber);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        alertTextView = (TextView) findViewById(R.id.AlertTextView2);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtIpAddress.getText().toString().length() != 0 && txtPortNumber.getText().toString().length() != 0)
                {
                    SERVER_IP = txtIpAddress.getText().toString().trim();
                    SERVER_PORT = Integer.parseInt(txtPortNumber.getText().toString().trim());
                    txtIpAddress.setText("");
                    txtPortNumber.setText("");
                    Thread1 = new Thread(new Thread1());
                    Thread1.start();
                }
                else {
                    alert("Fields are missing", "Error");
                }

            }
        });
    }

    private DataOutputStream output;
    private DataInputStream input;
    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                connectivityList = new ArrayList<>();
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                output.writeUTF("GetConnectivityList");

                String apResponse = input.readUTF();

                if(!apResponse.equals("empty")) {
                    String[] allUsers = apResponse.split("/");
                    for (String user : allUsers) {
                        if (user != null) {
                            String userDetails[] = user.split("-");
                            connectivityList.add(new Patient(userDetails[0], userDetails[1], userDetails[2]));
                        }
                    }
                }

                infectedPatients = myDatabaseHelper.checkCases(connectivityList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(infectedPatients.isEmpty())
                        {
                            alert("No Covid-19 infected patients were connected to this access point.", "SAFE");
                        }
                        else
                        {
                            alert("Be safe, " + infectedPatients.size() + " covid-19 infected patients were connected to this access point.", "WARNING");
                        }
                    }
                });

                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert("Unable to connect to Access Point " + SERVER_IP + ":" + SERVER_PORT, "Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }


}
