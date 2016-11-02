package database;

import android.database.Cursor;

import java.io.File;

/**
 * Created by lutz on 02/11/16.
 */
public class Story {

    public int _id;
    public String title;
    public String text;
    public String date;
    public String location;

    public File audioFile;
    public File imageFile;

    public Story(Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex(StoryContract.StoryEntry._ID));
        title = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_TITLE));
    }
}
