package database;

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
    public String location;

    public File audioFile;
    public File imageFile;

    public Story() {

    }

    public Story(Cursor cursor) {
        _id = cursor.getLong(cursor.getColumnIndex(StoryContract.StoryEntry._ID));
        title = cursor.getString(cursor.getColumnIndex(StoryContract.StoryEntry.COL_TITLE));
    }
}
