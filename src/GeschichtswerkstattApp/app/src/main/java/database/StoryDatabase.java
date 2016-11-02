package database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.Normalizer;
import java.util.ArrayList;

/**
 * Created by lutz on 01/11/16.
 */
public class StoryDatabase extends SQLiteOpenHelper {

    public StoryDatabase(Context context) {
        super(context, StoryContract.DB_NAME, null, StoryContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + StoryContract.StoryEntry.TABLE + " ( " +
                StoryContract.StoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StoryContract.StoryEntry.COL_TITLE + " TEXT NOT NULL," +
                StoryContract.StoryEntry.COL_DATE + " DEFAULT CURRENT_TIMESTAMP NOT NULL" +
                ");";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StoryContract.StoryEntry.TABLE);
        onCreate(db);
    }

    public synchronized void insertStory(SQLiteDatabase db,String title) {

        String query = "INSERT INTO "+StoryContract.StoryEntry.TABLE+" " +
                "("+ StoryContract.StoryEntry.COL_TITLE+") VALUES ("+escape(title)+")";
        //insert data
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public synchronized Cursor getAllStories(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("SELECT * FROM "+ StoryContract.StoryEntry.TABLE,null);
        return cursor;
    }

    public synchronized void deleteStory(SQLiteDatabase db,int id) {

        db.delete(StoryContract.StoryEntry.TABLE,
            StoryContract.StoryEntry._ID + " = ?", new String[]{Integer.toString(id)});
    }



    static public String escape(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        return DatabaseUtils.sqlEscapeString(str);
    }

}