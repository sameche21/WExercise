package com.example.webserviceexercise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AccountDB";
    private static final String TABLE_NAME = "AccountTable";
    private static final String PAYER = "AccountPayer";
    private static final String POINTS = "AccountPoints";
    private static final String DATE = "AccountDate";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    PAYER + " TEXT not null unique, " +
                    POINTS + " TEXT not null, " +
                    DATE + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addPayers(Payer pay) {
        ContentValues values = new ContentValues();
        values.put(PAYER, pay.getPayer());
        values.put(POINTS, String.valueOf(pay.getPoints()));
        values.put(DATE, pay.getDate());

        database.insert(TABLE_NAME, null, values);
    }

    public void updatePayers(Payer pay) {
        ContentValues values = new ContentValues();
        values.put(PAYER, pay.getPayer());
        values.put(POINTS, String.valueOf(pay.getPoints()));
        values.put(DATE, pay.getDate());

        database.update(TABLE_NAME, values,PAYER + " = ?", new String[] {pay.getPayer()});
    }

    public ArrayList<String[]> loadAccounts() {
        ArrayList<String[]> accounts = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{PAYER, POINTS, DATE},
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String pay = cursor.getString(0);
                String point = cursor.getString(1);
                String curr_date = cursor.getString(2);
                Log.d(TAG, "loadAccounts: " + pay + point + curr_date);
                accounts.add(new String[] {pay, point, curr_date});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return accounts;
    }

    void shutDown() {
        database.close();
    }
}

