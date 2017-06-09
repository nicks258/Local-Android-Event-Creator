package com.nicks.sumit.eventcreator.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nicks.sumit.eventcreator.data.FeedItem;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by sumitm on 08-Jun-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "event_creator";

    // Table Names
    private static final String DB_TABLE = "events_info";

    // column names

    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_CATEGORY = "event_category";
    private static final String EVENT_DATE = "event_date";
    private static final String EVENT_DESCRIPTION = "event_description";
    private static final String KEY_ID = "key_id";
    private static final String IMAGE_PATH = "image_data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            EVENT_NAME + " VARCHAR," + EVENT_DESCRIPTION + " VARCHAR,"
            +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_CATEGORY + " VARCHAR," +
            EVENT_DATE + " VARCHAR," +
            IMAGE_PATH + " VARCHAR);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("Path:" , context.getDatabasePath(DATABASE_NAME).getAbsolutePath());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.init();
        Logger.i("Table Created");
        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }
    public void addEvent(FeedItem feedItem) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENT_CATEGORY,       feedItem.getCategory());
        cv.put(EVENT_NAME,           feedItem.getEventName());
        cv.put(EVENT_DESCRIPTION,    feedItem.getEventDescription());
        cv.put(EVENT_DATE,           feedItem.getTimeStamp());
        cv.put(IMAGE_PATH,           feedItem.getImagePath());
        database.insert( DB_TABLE, null, cv );
    }
    public ArrayList<FeedItem> getAllRecordsAlternate() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DB_TABLE, null);

        ArrayList<FeedItem> contacts = new ArrayList<FeedItem>();
        FeedItem feedItem ;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                feedItem = new FeedItem();
                feedItem.setEventName(cursor.getString(0));
                feedItem.setEventDescription(cursor.getString(1));
                feedItem.setCategory(cursor.getString(3));
                feedItem.setTimeStamp(cursor.getString(4));
                String image = cursor.getString(5);
                feedItem.setImagePath(image);
                contacts.add(feedItem);
            }

        }
        cursor.close();
        database.close();

        return contacts;
    }
}