package com.example.covid_19_tracker;

import java.util.ArrayList;

public class Patient {
    private String name;
    private String phone_number;
    private String mac_address;
    public ArrayList<String> visitied_aps;

    public Patient(String name, String phone_number, String mac_address)
    {
        this.name = name;
        this.phone_number = phone_number;
        this.mac_address = mac_address;
        visitied_aps = new ArrayList<>();
    }

    public String getName()
    {
        return this.name;
    }

    public String getNumber()
    {
        return this.phone_number;
    }

    public String getMac()
    {
        return this.mac_address;
    }

    public String summarizeAps() {
        String result = "";
        if (visitied_aps.size() == 0)
        {
            return "Did not connect to any AP recently";
        }

        result = "Connected to " + visitied_aps.size() + " APs recently:\n";

        for(int i = 0; i < visitied_aps.size() - 1; i++)
        {
            result += "• " + visitied_aps.get(i) + "\n";
        }
        return result + "• " + visitied_aps.get(visitied_aps.size()-1);
    }
    @Override
    public String toString()
    {
        return "Name: " + name + "\n" + "Number: " + phone_number + "\n" + "MAC: " + mac_address + "\n" + summarizeAps();
    }
}