package com.example.lucas.porterapp;

/**
 * PhoneDirectoryInfo class used to create phoneDirectory objects that are added to the firebase database
 * and used to populate the PhoneDirectory activity
 */

public class PhoneDirectoryInfo {
    private String wardName;
    private String floorNumber;
    private String phoneNumber; // string used for phone number to hold potential leading 0's

    // ---------------------------------------------------------------------------------------------

    // constructors
    // default constructor
    public PhoneDirectoryInfo(){

    }

    // constructor using set arguments
    public PhoneDirectoryInfo(String wardName, String floorNumber, String phoneNumber){
        this.wardName = wardName;
        this.floorNumber = floorNumber;
        this.phoneNumber = phoneNumber;
    }

    // ---------------------------------------------------------------------------------------------

    // getters used to retrieve the ward contact details

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

    // setters used to set the ward contact details

    public void setWardName(String wardName){
        this.wardName = wardName;
    }

    public void setFloorNumber(String floorNumber){
        this.floorNumber = floorNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    // ---------------------------------------------------------------------------------------------

}


