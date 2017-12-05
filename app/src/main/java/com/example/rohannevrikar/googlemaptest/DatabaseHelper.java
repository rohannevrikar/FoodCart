package com.example.rohannevrikar.googlemaptest;

/**
 * Created by Rohan Nevrikar on 02-12-2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rohan Nevrikar on 23-10-2017.
 */

class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "tastifaisample";
    String DB_PATH = null;
    public static final String USERS_TABLE = "users";
    public static final String RESTAURANTS_TABLE = "restaurants";

    public static final String COL1 = "id";
    public static final String COL2 = "email";
    public static final String COL3 = "password";
    public static final String COL4 = "name";
    public static final String COL5 = "phonenumber";
    private String TAG ="message";
    private final Context myContext;
    private SQLiteDatabase database;
    private String assetPath;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.assetPath = assetPath;
        this.DB_PATH = "/data/data/"
                + context.getApplicationContext().getPackageName() + "/databases/";
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getWritableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
//    private boolean checkDataBase() {
//        SQLiteDatabase checkDB = null;
//        try {
//            String myPath = DB_PATH + DATABASE_NAME;
//            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//        } catch (SQLiteException e) {
//            throw new SQLiteException("Error while checking");
//        }
//        if (checkDB != null) {
//            checkDB.close();
//        }
//        return checkDB != null;
//    }
private boolean checkDataBase() throws IOException {

    File databaseFile = new File(DB_PATH + DATABASE_NAME);
    return databaseFile.exists();

}
    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DATABASE_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + USERS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                " email TEXT, "+
//                " password TEXT, " +
//                " name TEXT, " +
//                " phonenumber TEXT)";
//        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + USERS_TABLE);
        onCreate(db);
    }

    public boolean addUser(String email, String passowrd, String name,String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, email);
        contentValues.put(COL3, passowrd);
        contentValues.put(COL4, name);
        contentValues.put(COL5, phoneNumber);
        //Log.d("Values", contentValues.toString());
        long result = db.insert(USERS_TABLE, null, contentValues);

        //Log.d("Result", result + "");

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getUser(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM " + USERS_TABLE + " WHERE email = '" + email + "' AND password = '" + password + "'";
        Cursor userData = db.rawQuery(query, null);
        Log.d(TAG,email);
        Log.d(TAG,password);
        Log.d(TAG,query);
        Log.d(TAG,String.valueOf(userData.getCount()));
        return userData;
    }
    public Cursor getRestaurants(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + RESTAURANTS_TABLE;
        Cursor restaurantsData = db.rawQuery(query, null);
        restaurantsData.moveToFirst();
        Log.d(TAG,restaurantsData.getString(0) + " " + restaurantsData.getString(1));
        return restaurantsData;
    }
}
