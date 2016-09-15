package com.limwoon.musicwriter.SQLite;

import android.provider.BaseColumns;

/**
 * Created by ejdej on 2016-08-18.
 */
public class DefineSQL implements BaseColumns {
public static final String COMMA = ",";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String NOT_NULL = " NOT NULL";
    public static final String DEFAULT = " DEFAULT";

    public static final String TABLE_NAME = "musicsheet";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_AUTHOR = "author";
    public static final String COLUMN_NAME_NOTE = "note";
    public static final String COLUMN_NAME_BEATS = "beats";

    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE " + TABLE_NAME + " ("+
                DefineSQL._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DefineSQL.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA+
                DefineSQL.COLUMN_NAME_AUTHOR + TEXT_TYPE + DEFAULT + "anonymous" + COMMA+
                DefineSQL.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA+
                DefineSQL.COLUMN_NAME_BEATS + INT_TYPE +
                ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + DefineSQL.TABLE_NAME;
}
