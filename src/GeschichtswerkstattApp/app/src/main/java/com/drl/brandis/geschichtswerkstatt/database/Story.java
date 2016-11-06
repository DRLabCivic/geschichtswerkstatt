package com.drl.brandis.geschichtswerkstatt.database;

import android.database.Cursor;

import java.io.File;
import java.io.Serializable;

/**
 * Created by lutz on 02/11/16.
 */
public class Story implements Serializable {

    public long _id;
    public String title;
    public String text;
    public String date;

    // location
    public Double loc_latitude;
    public Double loc_longitude;
    public String loc_name;

    public String audioFile;
    public String imageFile;

    public Story() {

        _id = -1;
    }

    public Story(Cursor cursor) {
        _id = cursor.getLong(cursor.getColumnIndex(StoryContract.StoryEntry._ID));
        title = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_TITLE));
        text = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_TEXT));
        imageFile = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_IMAGE));
        audioFile = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_AUDIOFILE));
        loc_longitude = cursor.getDouble(cursor.getColumnIndex(StoryContract.StoryEntry.COL_LOCATION_LONG));
        loc_latitude = cursor.getDouble(cursor.getColumnIndex(StoryContract.StoryEntry.COL_LOCATION_LAT));
        loc_name = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_LOCATION_NAME));
        date = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_DATE));
    }

    public String validate() {
        if (title == null || title.length() < 1) {
            return "Titel der Geschichte fehlt.";
        }
        return null;
    }
}
