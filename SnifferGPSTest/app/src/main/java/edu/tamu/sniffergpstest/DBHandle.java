package edu.tamu.sniffergpstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tbranyon on 4/4/16.
 */
public class DBHandle extends SQLiteOpenHelper
{
    DBHandle(Context context, String name)
    {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS TBL1 (MAC_Address TEXT, Latitude REAL, Longitude REAL, Timestamp TEXT)";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        return; //Do nothing
    }

    public void writeDB()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        //data.put()
        db.insert("TBL1", null, data);
    }
}
