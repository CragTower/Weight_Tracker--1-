package com.example.weight_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// Database setup
public class AppDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 5;
    private static final String DATABASE_NAME = "weight_tracker_app.db";

    private static AppDatabase mWeightTrackerDb;

    public static AppDatabase getInstance(Context context) {
        if (mWeightTrackerDb == null) {
            mWeightTrackerDb = new AppDatabase(context);
        }
        return mWeightTrackerDb;
    }

    // Constructor
    public AppDatabase (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Table details for users and passwords
    private static final class UserAccountTable {
        private static final String TABLE = "useraccounts";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    // Table details for user's daily weights
    private static final class UserDailyWeightTable {
        private static final String TABLE = "daily_weight";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_WEIGHT = "weight";
        private static final String COL_TIME_STAMP = "timestamp";
    }

    // Table details for user's goal weight
    private static final class UserGoalWeightTable {
        private static final String TABLE = "goal_weight";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_WEIGHT = "weight";
    }

    // On the first creation of a database, this method creates all associated tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + UserAccountTable.TABLE + " (" +
                    UserAccountTable.COL_USERNAME + " primary key, " +
                    UserAccountTable.COL_PASSWORD + " text)");

        db.execSQL("create table " + UserDailyWeightTable.TABLE + " (" +
                UserDailyWeightTable.COL_ID + " integer primary key autoincrement, " +
                UserDailyWeightTable.COL_USERNAME + " text, " +
                UserDailyWeightTable.COL_WEIGHT + " text, " +
                UserDailyWeightTable.COL_TIME_STAMP + " text)");

        db.execSQL("create table " + UserGoalWeightTable.TABLE + " (" +
                UserGoalWeightTable.COL_ID + " integer primary key autoincrement, " +
                UserGoalWeightTable.COL_USERNAME + " text, " +
                UserGoalWeightTable.COL_WEIGHT + " text)");
    }

    // On creation of a new version, this method drops all tables and entries
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserAccountTable.TABLE);
        db.execSQL("drop table if exists " + UserDailyWeightTable.TABLE);
        db.execSQL("drop table if exists " + UserGoalWeightTable.TABLE);
        onCreate(db);
    }

    // Adds username and password to users table
    public long addUsernamePassword (String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_USERNAME, username);
        values.put(UserAccountTable.COL_PASSWORD, password);

        long userId = db.insert(UserAccountTable.TABLE, null, values);
        return userId;
    }

    // Finds if a username exists in the table
    public boolean findUsername (String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + UserAccountTable.TABLE +
                    " WHERE username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    // If username exists, finds if username and password match
    public boolean findUsernamePassword (String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + UserAccountTable.TABLE +
                    " WHERE username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    // Deletes user from table
    public boolean deleteUser (String username) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(UserAccountTable.TABLE, UserAccountTable.COL_USERNAME + " = ?",
                new String[] {username});
        return rowsDeleted > 0;
    }

    // Adds daily weight to daily weight table and ties it to the username
    public boolean addDailyWeight(String username, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Creates timestamp upon method call to add to entry
        Date date = new Date();
        Timestamp timeStamp = new Timestamp(date.getTime());

        ContentValues values = new ContentValues();
        values.put(UserDailyWeightTable.COL_USERNAME, username);
        values.put(UserDailyWeightTable.COL_WEIGHT, weight);
        values.put(UserDailyWeightTable.COL_TIME_STAMP, timeStamp.toString());

        long added = db.insert(UserDailyWeightTable.TABLE, null, values);
        if (added > 0)
            return true;
        else
            return false;
    }

    // Adds goal weight to goal weight table and ties it to the username
    public boolean addGoalWeight(String username, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Gets goal weight from database
        String result = getGoalWeight(username);
        // If there is a goal weight in the table, delete it
        if (!result.equals(""))
            deleteGoalWeight(username, result);

        ContentValues values = new ContentValues();
        values.put(UserGoalWeightTable.COL_USERNAME, username);
        values.put(UserGoalWeightTable.COL_WEIGHT, weight);

        long added = db.insert(UserGoalWeightTable.TABLE, null, values);
        if (added > 0)
            return true;
        else
            return false;
    }

    // Returns an array list of tables entries
    public ArrayList<HashMap<String, String>> getDailyWeight(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> weightList = new ArrayList<>();

        String sql = "SELECT * FROM " + UserDailyWeightTable.TABLE + " WHERE username = ? ORDER BY " +
                        UserDailyWeightTable.COL_TIME_STAMP + " DESC";
        Cursor cursor = db.rawQuery(sql, new String[] {username});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> user = new HashMap<>();
                user.put("weight", cursor.getString(2));
                user.put("timestamp", cursor.getString(3));
                weightList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return weightList;
    }

    // Gets user's goal weight
    public String getGoalWeight(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String goalWeight = "";

        String sql = "SELECT * FROM " + UserGoalWeightTable.TABLE + " WHERE username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {username});
        if (cursor.moveToFirst())
            goalWeight = cursor.getString(2);

        return goalWeight;
    }

    // Deletes daily weight entry
    public boolean deleteEntry(String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(UserDailyWeightTable.TABLE, UserDailyWeightTable.COL_TIME_STAMP + " = ?",
                new String[] {timestamp});
        return rowsDeleted > 0;
    }

    // Deletes users goal weight
    public boolean deleteGoalWeight(String username, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(UserGoalWeightTable.TABLE, UserGoalWeightTable.COL_WEIGHT + " = ?",
                new String[] {weight});
        return rowsDeleted > 0;
    }
}
