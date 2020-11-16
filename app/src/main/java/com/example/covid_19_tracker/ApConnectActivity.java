package com.example.covid_19_tracker;

import android.content.DialogInterface;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ApConnectActivity extends AppCompatActivity {

    DatabaseHelper myDatabaseHelper;
    private TextInputEditText txtIpAddress, txtPortNumber;
    private Button btnConnect, btnCheck;
    int SERVER_PORT;
    String SERVER_IP;
    Thread Thread1 = null;
    private ArrayList<Patient> connectivityList;
    private TextView alertTextView;
    private boolean connected = false;

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

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertTextView.setVisibility(View.VISIBLE);
            }
        });
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
        btnCheck = (Button) findViewById(R.id.btnCheck);
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
                    System.out.println("here");
                    alert("Fields are missing", "Error");
                }

            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected) {
                    String message = "GetConnectivityList";
                    new Thread(new Thread3(message)).start();
                    int cases = myDatabaseHelper.checkCases(connectivityList);
                    alert("The total number of covid patients who were connected to this AP is: " + cases, "Results");
                }
                else {
                    alert("Please connect to Access Point first", "Connection Error");
                }
            }
        });

    }

    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert("Successfully connected to Access Point " + SERVER_IP + ":" + SERVER_PORT, "Success");
                        connected=true;
                    }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                alert("Unable to connect to Access Point " + SERVER_IP + ":" + SERVER_PORT, "Error");
                e.printStackTrace();
            }
        }
    }
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert("server: " + message + "\n", "Server Response");
                            }
                        });
                    } else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectivityList = new ArrayList<Patient>();
                    while (true) {
                        try {
                            final String patient = input.readLine();
                            String[] patientDetails = patient.split("-");
                            if (patientDetails[0].length() != 0 && patientDetails[1].length() != 0 && patientDetails[2].length() != 0) {
                                connectivityList.add(new Patient(patientDetails[0], patientDetails[1], patientDetails[2]));
                            } else {
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
