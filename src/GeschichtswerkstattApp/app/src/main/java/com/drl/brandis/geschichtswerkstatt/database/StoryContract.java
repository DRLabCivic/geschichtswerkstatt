package com.drl.brandis.geschichtswerkstatt.database;

/**
 * Created by lutz on 01/11/16.
 */

import android.provider.BaseColumns;

public class StoryContract {

    public static final String DB_NAME = "com.drl.brandis.geschichtswerkstatt.db";
    public static final int DB_VERSION = 7;

    public class StoryEntry implements BaseColumns {

        public static final String TABLE = "stories";

        public static final String COL_TITLE = "title";
        public static final String COL_TEXT = "text";
        public static final String COL_DATE = "date";
        public static final String COL_LOCATION_LONG = "loc_long";
        public static final String COL_LOCATION_LAT = "loc_lang";
        public static final String COL_LOCATION_NAME = "loc_name";


        public static final String COL_AUDIOFILE = "recording";
        public static final String COL_IMAGE = "image";
    }
}
