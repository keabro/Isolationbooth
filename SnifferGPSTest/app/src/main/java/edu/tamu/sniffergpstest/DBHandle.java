package edu.tamu.sniffergpstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tbranyon on 4/4/16.
 */
public class DBHandle extends SQLiteOpenHelper
{
    public String curTblName = "TBL_DEFAULT";

    public DBHandle(Context context, String name)
    {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String cmd = "CREATE TABLE IF NOT EXISTS " + curTblName + " (MAC_Address TEXT, Latitude REAL, Longitude REAL, Timestamp TEXT)";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        return; //Do nothing
    }

    public void writeDB(String MAC, double lat, double longitude, String timestamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("MAC_Address", MAC);
        data.put("Latitude", lat);
        data.put("Longitude", longitude);
        data.put("Timestamp", timestamp);
        db.insert(curTblName, null, data);
    }

    public void openNextTable(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String cmd = "CREATE TABLE IF NOT EXISTS " + name + " (MAC_Address TEXT, Latitude REAL, Longitude REAL, Timestamp TEXT)";
        db.execSQL(cmd);
        curTblName = name;
    }

    public void clearCurrentTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String cmd = "DELETE FROM " + curTblName;
        db.execSQL(cmd);
    }

    public String getLatestEntry()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String cmd = "SELECT * FROM " + curTblName + " WHERE Timestamp = (SELECT MAX(Timestamp) FROM " + curTblName + ")";
        Cursor cursor = db.rawQuery(cmd, null);
        if(cursor.getCount() == 0)
            return "EMPTY";
        cursor.moveToFirst();
        int lat = cursor.getColumnIndex("Latitude");
        int longi = cursor.getColumnIndex("Longitude");
        int timest = cursor.getColumnIndex("Timestamp");
        return "(" + cursor.getFloat(lat) + "," + cursor.getFloat(longi) + "," + cursor.getString(timest) + ")";
    }

    public String getNumRows()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + curTblName, null);
        if(cursor == null)
            return "NULL";
        cursor.moveToFirst();
        return "" + cursor.getInt(0);
    }
}
