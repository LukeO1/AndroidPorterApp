package com.example.lucas.porterapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * SQLite database helper class
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    //Initialize database and table
    public static final String DATABASE_NAME = "PortAssist.db";
    public static final String TABLE_COMPLETED_TASKS = "completed_tasks_table";
    public static final String TABLE_PEDOMETER = "pedometer_table";

    //Initialize columns for the completed task table
    public static final String TASK_ID = "TASK_ID";
    public static final String WARD_NAME = "WARD_NAME";
    public static final String PATIENT_NAME = "PATIENT_NAME";
    public static final String DESTINATION = "DESTINATION";
    public static final String TIMESTAMP_CREATED = "TIMESTAMP_CREATED";
    public static final String TIMESTAMP_COMPLETED = "TIMESTAMP_COMPLETED";
    public static final String TIMETAKEN = "TIMETAKEN";

    //Call to create a database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL query to create a table with columns initialized
        db.execSQL("create table " + TABLE_COMPLETED_TASKS + "( ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TASK_ID TEXT, WARD_NAME TEXT, PATIENT_NAME TEXT, DESTINATION TEXT, TIMESTAMP_CREATED TEXT, TIMESTAMP_COMPLETED TEXT, TIMETAKEN TEXT);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLETED_TASKS);
        onCreate(db);
    }

// -------------------------------------------------------------------------------------------------

    /**
     * InsertData into database
     * @param taskId
     * @param wardName
     * @param patientName
     * @param destination
     * @param timeStampCreated
     * @param timeStampCompleted
     * @param timeTaken
     * @return
     */
    public boolean insertData(String taskId, String wardName, String patientName, String destination,
                               String timeStampCreated, String timeStampCompleted, String timeTaken){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_ID, taskId);
        contentValues.put(WARD_NAME, wardName);
        contentValues.put(PATIENT_NAME, patientName);
        contentValues.put(DESTINATION, destination);
        contentValues.put(TIMESTAMP_CREATED, timeStampCreated);
        contentValues.put(TIMESTAMP_COMPLETED, timeStampCompleted);
        contentValues.put(TIMETAKEN, timeTaken);

        long result = db.insert(TABLE_COMPLETED_TASKS, null, contentValues);

        if (result == -1){return false;}
        else{return true;}

    }
// -------------------------------------------------------------------------------------------------

    /**
     * Query al data from the database and store in a cursor
     * @return
     */
    public Cursor createCursor() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_COMPLETED_TASKS, null); //Select all from db

        return cursor;
    }

// -------------------------------------------------------------------------------------------------
    /**
     * Method to delete all data from the DB
     */
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("drop table if exists " + TABLE_COMPLETED_TASKS);
        db.execSQL("delete from "+ TABLE_COMPLETED_TASKS);
    }

}
