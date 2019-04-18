package com.example.dmberry.HereIAm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.dmberry.HereIAm.MainActivity.CODENAME;
import static com.example.dmberry.HereIAm.MainActivity.LON;
import static com.example.dmberry.HereIAm.MainActivity.LAT;
import static com.example.dmberry.HereIAm.MainActivity.USERNAME;
import static com.example.dmberry.HereIAm.MainActivity.TIMEDATE;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    final static String NAME="userDB";
    final private static String CREATE_CMD="CREATE TABLE "+NAME+" (" + MainActivity._ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CODENAME + " TEXT NOT NULL,"+ USERNAME+" TEXT NOT NULL,"
            +LAT+" TEXT NOT NULL,"+LON+" TEXT NOT NULL," + TIMEDATE + " TEXT NOT NULL"+ ")";

    final private static Integer VERSION=1;
    final private Context context;

    public DatabaseOpenHelper(Context context)
    {
        super(context,NAME,null,VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
        ContentValues values=new ContentValues();
        values.put(CODENAME,"None");
        values.put(LON,"0.0");
        values.put(LAT,"0.0");
        values.put(USERNAME,"None");
        values.put(TIMEDATE,"MM/dd/yyyy 12:00AM");

        db.insert(NAME,null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
