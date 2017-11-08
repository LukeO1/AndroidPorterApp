package com.example.lucas.porterapp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskInfo {

    private String destination;
    private String patientName;
    private String ward;
    private int priority;
    private String timeStamp;
    private String minutes;

    public TaskInfo() {
    }

    public TaskInfo(String destination, String patientName, String ward, int priority, String timeStamp) {
        this.destination = destination;
        this.patientName = patientName;
        this.ward = ward;
        this.priority = priority;
        this.timeStamp = timeStamp;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setpatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public int getPriority(){

        return priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public String getTimeStamp(){

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            Date currentTimeStamp = new Date();
            Date oldTimeStamp = dateFormat.parse(timeStamp);

            long timer = currentTimeStamp.getTime() - oldTimeStamp.getTime();
            minutes = "" + TimeUnit.MILLISECONDS.toMinutes(timer);
            return minutes;
        }
        catch(Exception e){
        }
        return minutes;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

}