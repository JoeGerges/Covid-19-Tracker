package com.example.covid_19_tracker;

public class Patient {
    private String name;
    private String phone_number;
    private String mac_address;

    public Patient(String name, String phone_number, String mac_address)
    {
        this.name = name;
        this.phone_number = phone_number;
        this.mac_address = mac_address;
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

    @Override
    public String toString()
    {
        return name + "\n" + phone_number + "\n" + mac_address;
    }
}