package com.drl.brandis.geschichtswerkstatt.database;

import android.database.Cursor;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<String> validate() {

        List<String> errors = new ArrayList<String>();

        if (title == null || title.length() < 1) {
            errors.add("Titel der Geschichte fehlt.");
        }

        if (audioFile == null) {
            errors.add("Aufnahme fehlt.");
        } else {
            File file = new File(audioFile);
            if (!file.exists())
                errors.add("Aufnahme Datei kann nicht gefunden werden");
        }

        if (errors.isEmpty())
            return null;
        else
            return errors;
    }

    public boolean isEmpty() {
        return (title == null && text == null && imageFile == null && audioFile == null && loc_name == null);
    }

    public String getDate() {
        if (date == null)
            return null;

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date = iso8601Format.parse(this.date);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
