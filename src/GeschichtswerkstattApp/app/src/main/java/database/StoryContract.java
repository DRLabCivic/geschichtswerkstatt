package database;

/**
 * Created by lutz on 01/11/16.
 */

import android.provider.BaseColumns;

public class StoryContract {

    public static final String DB_NAME = "com.drl.brandis.geschichtswerkstatt.db";
    public static final int DB_VERSION = 4;

    public class StoryEntry implements BaseColumns {

        public static final String TABLE = "stories";

        public static final String COL_TITLE = "title";
        public static final String COL_TEXT = "text";
        public static final String COL_DATE = "date";
        public static final String COL_LOCATION = "location";
        public static final String COL_AUDIOFILE = "title";
        public static final String COL_IMAGE = "image";
    }
}
