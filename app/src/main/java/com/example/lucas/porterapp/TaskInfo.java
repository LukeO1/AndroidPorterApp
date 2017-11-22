package com.example.lucas.porterapp;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * TaskInfo class stores worklist data in TaskInfo Object
 */
public class TaskInfo implements Serializable {

    private String taskID;
    private String destination;
    private String patientName;
    private String ward;
    private int priority;
    private String timeStamp;
    private String minutes;
    private String inProgress;
    private long inProgressSince;
    private String userID;
    private String patientID;

    public TaskInfo() {
    }

    public TaskInfo(String taskID, String destination, String patientName, String ward,
                    int priority, String timeStamp, String inProgress, long inProgressSince,
                    String userID, String patientID) {
        this.taskID = taskID;
        this.destination = destination;
        this.patientName = patientName;
        this.ward = ward;
        this.priority = priority;
        this.timeStamp = timeStamp;
        this.inProgress = inProgress;
        this.inProgressSince = inProgressSince;
        this.userID = userID;
        this.patientID = patientID;
    }

    public String getTaskID(){
        return taskID;
    }

    public void setTaskID(String taskID){
        this.taskID = taskID;
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
        return timeStamp;

    }

    public String getMinutes(){
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

    public String getInProgress(){
        return inProgress;
    }

    public void setInProgress(String inProgress){
        this.inProgress = inProgress;
    }

    public long getInProgressSince(){
        return inProgressSince;
    }

    public void setInProgressSince(long inProgressSince){
        this.inProgressSince = inProgressSince;
    }

    public String getUserID(){
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public String getPatientID(){
        return patientID;
    }

    public void setPatientID(){
        this.patientID = patientID;
    }

}