package com.example.mocom_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "info_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "info";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_LANGUAGE = "language";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_SCHEDULE = "schedule";
    private static final String COLUMN_PLACE = "place";
    private static final String COLUMN_TAG = "tag";
    private static final String COLUMN_GOAL = "goal";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DAY = "day";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_LANGUAGE + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_SCHEDULE + " TEXT, " +
                COLUMN_PLACE + " TEXT, " +
                COLUMN_TAG + " TEXT, " +
                COLUMN_GOAL + " TEXT, " +
                COLUMN_TIME + " INTEGER, " +
                COLUMN_DAY + " TEXT);";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addInfo(InfoModel info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, info.getName());
        values.put(COLUMN_PRICE, info.getPrice());
        values.put(COLUMN_LANGUAGE, info.getLanguage());
        values.put(COLUMN_ADDRESS, info.getAddress());
        values.put(COLUMN_SCHEDULE, info.getSchedule());
        values.put(COLUMN_PLACE, info.getPlace());
        values.put(COLUMN_TAG, info.getTag());
        values.put(COLUMN_GOAL, info.getGoal());
        values.put(COLUMN_TIME, info.getTime());
        values.put(COLUMN_DAY, info.getDay());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }
}
