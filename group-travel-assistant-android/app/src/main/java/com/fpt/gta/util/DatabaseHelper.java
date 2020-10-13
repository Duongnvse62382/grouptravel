package com.fpt.gta.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fpt.gta.data.dto.LastMessageTime;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "mess_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(LastMessageTime.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + LastMessageTime.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertMessageTime(String time) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LastMessageTime.COLUMN_TIMESTAMP, time);

        // insert row
        long id = db.insert(LastMessageTime.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public LastMessageTime getLastMessageTime(String id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LastMessageTime.TABLE_NAME,
                new String[]{LastMessageTime.COLUMN_ID, LastMessageTime.COLUMN_TIMESTAMP},
                LastMessageTime.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        LastMessageTime lastMessageTime = new LastMessageTime(
                cursor.getString(cursor.getColumnIndex(LastMessageTime.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(LastMessageTime.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return lastMessageTime;
    }

    public List<LastMessageTime> getAllLastMessageTime() {
        List<LastMessageTime> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + LastMessageTime.TABLE_NAME + " ORDER BY " +
                LastMessageTime.COLUMN_TIMESTAMP;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LastMessageTime lastMessageTime = new LastMessageTime();
                lastMessageTime.setId(cursor.getString(cursor.getColumnIndex(LastMessageTime.COLUMN_ID)));
                lastMessageTime.setTimestamp(cursor.getString(cursor.getColumnIndex(LastMessageTime.COLUMN_TIMESTAMP)));

                notes.add(lastMessageTime);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + LastMessageTime.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateMessages(LastMessageTime lastMessageTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LastMessageTime.COLUMN_TIMESTAMP, lastMessageTime.getTimestamp());

        // updating row
        return db.update(LastMessageTime.TABLE_NAME, values, LastMessageTime.COLUMN_ID + " = ?",
                new String[]{String.valueOf(lastMessageTime.getId())});
    }
}
