package com.example.nasadailyimage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDbHelper extends SQLiteOpenHelper {
    protected static final String DBNAME = "mydb.db";
    protected static final int VERSION = 2;
    public static final String ID = "id";
    public static final String TABLE_NAME = "nasaimgs";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String HDURL = "hdurl";
    public static final String PHOTO = "photo";

    private static MyDbHelper dbInstance;

    public static synchronized MyDbHelper getInstance(Context context) {
        if(dbInstance == null){
            dbInstance = new MyDbHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private MyDbHelper(Context context){
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT NOT NULL, " +
                DATE + " TEXT NOT NULL, " +
                HDURL + " TEXT NOT NULL, " +
                PHOTO + " BLOB NOT NULL " +
                ")";
        db.execSQL(queryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addArchive(Archive archive){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        if(!selectArchive(archive.getDate())) {
            try {
                ContentValues values = new ContentValues();
                values.put(NAME, archive.getName());
                values.put(DATE, archive.getDate());
                values.put(HDURL, archive.getHdurl());
                values.put(PHOTO, archive.getPhoto());
                db.insertOrThrow(TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    public boolean selectArchive (String date){
        SQLiteDatabase db = getReadableDatabase();
        boolean ret = false;
        db.beginTransaction();
        String selectQuery = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, DATE);
        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(date)});
        try {
            if(c.moveToFirst() && c.getCount() > 0){
                ret = true;
                db.setTransactionSuccessful();
            }
        }finally {
            if(c != null && !c.isClosed()){
                c.close();
            }
        }
        return ret;
    }

    public List<Archive> getAllPhotos(){
        List<Archive> photos = new ArrayList<>();
        String selectAllQuery = String.format("SELECT * FROM %s", TABLE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectAllQuery, null);
        try{
            if(c.moveToFirst()){
                do {
                    Archive archive = new Archive();
                    archive.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
                    archive.setDate(c.getString(c.getColumnIndexOrThrow(DATE)));
                    archive.setHdurl(c.getString(c.getColumnIndexOrThrow(HDURL)));
                    archive.setPhoto(c.getBlob(c.getColumnIndexOrThrow(PHOTO)));
                    photos.add(archive);
                }while(c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(c != null && !c.isClosed()){
                c.close();
            }
        }
        return photos;
    }

    public long deleteArchive(String date){
        SQLiteDatabase db = getWritableDatabase();
        String where = DATE + "=" + date;
        return db.delete(TABLE_NAME, where, null);
    }
}
