package com.example.lucas.porterapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String USER_ID = "USER_ID";

    //Columns for pedometer table
    public static final String STEPS_TAKEN = "STEPS_TAKEN";

    //Call to create a database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //SQL query to create a table with columns initialized
        db.execSQL("create table " + TABLE_COMPLETED_TASKS + "( ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TASK_ID TEXT, WARD_NAME TEXT, PATIENT_NAME TEXT, DESTINATION TEXT, " +
                "TIMESTAMP_CREATED TEXT, TIMESTAMP_COMPLETED TEXT, TIMETAKEN TEXT, USER_ID TEXT);" );

        db.execSQL("create table " + TABLE_PEDOMETER + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, STEPS_TAKEN INTEGER);");
//        System.out.println("DB ONCREATE!");
    }

    public void startPedoCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_PEDOMETER;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int rowCount = cursor.getInt(0);
        System.out.println("ROW COUNT: " + rowCount);
        if(rowCount > 0) {
            // ignore
        }
        else {
            // populate table
            ContentValues contentValues = new ContentValues();
            contentValues.put(STEPS_TAKEN, 0);
            db.insert(TABLE_PEDOMETER, null, contentValues);
//        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_COMPLETED_TASKS + " ORDER BY " + orderBy + " DESC" , null); //Select all from db
//        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_PEDOMETER, null); //Select all from db
//        String[] aaa = cursor.getColumnNames();
//        for(String a : aaa){
//            System.out.println("Column: " + a);
//        }
//        System.out.println(cursor.getColumnIndex("STEPS_TAKEN"));
//        cursor.moveToFirst();
//        System.out.println(cursor.getInt(2));
        }

    }

    public int getSteps(){
        startPedoCount();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_PEDOMETER, null); //Select all from db
        cursor.moveToFirst();
//        cursor.getColumnNames();
//        System.out.println("NEW COUNT: " + cursor.getInt(2));
//        return 1;
        return cursor.getInt(2);
    }

    public void countStep(){
//        System.out.println("COUNTED!!!!!");
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_PEDOMETER + " SET STEPS_TAKEN = STEPS_TAKEN + 1;");
//        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_PEDOMETER, null); //Select all from db
//        cursor.moveToFirst();
//        System.out.println("NEW COUNT: " + cursor.getInt(2));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLETED_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDOMETER);
        onCreate(db);
    }

// -------------------------------------------------------------------------------------------------

    public boolean insertDataCompleted(String taskId, String wardName, String patientName,
                                       String destination, String timeStampCreated,
                                       String timeStampCompleted, String timeTaken, String userID){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_ID, taskId);
        contentValues.put(WARD_NAME, wardName);
        contentValues.put(PATIENT_NAME, patientName);
        contentValues.put(DESTINATION, destination);
        contentValues.put(TIMESTAMP_CREATED, timeStampCreated);
        contentValues.put(TIMESTAMP_COMPLETED, timeStampCompleted);
        contentValues.put(TIMETAKEN, timeTaken);
        contentValues.put(USER_ID, userID);

        long result = db.insert(TABLE_COMPLETED_TASKS, null, contentValues);

        if (result == -1){return false;}
        else{return true;}

    }
// -------------------------------------------------------------------------------------------------
    /**
     * Query all data from the database and store in a cursor
     * @return
     */
    public Cursor createCursor(String orderBy) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id as _id,* from " + TABLE_COMPLETED_TASKS + " ORDER BY " + orderBy + " DESC" , null); //Select all from db

        if(cursor!=null && cursor.getCount()>0){ return cursor; }
        return null;
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