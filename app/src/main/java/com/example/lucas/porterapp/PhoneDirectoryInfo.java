package com.example.lucas.porterapp;

/**
 * Created by Tom on 09/11/2017.
 */

public class PhoneDirectoryInfo {
    private String wardName;
    private String floorNumber;
    private String phoneNumber; // string for phone number to hold potential leading 0's

    // ---------------------------------------------------------------------------------------------

    // constructors

    public PhoneDirectoryInfo(){

    }

    public PhoneDirectoryInfo(String wardName, String floorNumber, String phoneNumber){
        this.wardName = wardName;
        this.floorNumber = floorNumber;
        this.phoneNumber = phoneNumber;
    }

    // ---------------------------------------------------------------------------------------------

    // getters

    public String getWardName(){
        return this.wardName;
    }

    public String getFloorNumber(){
        return this.floorNumber;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    // ---------------------------------------------------------------------------------------------

}


