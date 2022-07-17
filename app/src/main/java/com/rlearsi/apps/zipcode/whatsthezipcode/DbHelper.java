package com.rlearsi.apps.zipcode.whatsthezipcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "zipcode.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TOPICS = "topics";
    private static final String TABLE_CAT = "category";
    private static final String TABLE_RECENTS = "recent_adress";

    private final String C_TABLE_TOPICS = "CREATE TABLE IF NOT EXISTS " + TABLE_TOPICS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "code TEXT, title TEXT, description TEXT, zipcode TEXT NOT NULL, city TEXT, neighborhood TEXT, logradouro TEXT, uf TEXT, " +
            "image TEXT, typeCode TEXT, country TEXT, countryCode TEXT, flag TEXT, fav INTEGER, color TEXT, local TEXT, identifier INTEGER, " +
            "pid INTEGER, archive INTERGER, active INTEGER, datetime TEXT, dt INTEGER, tm TEXT, " +
            "UNIQUE(zipcode) ON CONFLICT IGNORE);";

    private final String C_TABLE_CAT = "CREATE TABLE IF NOT EXISTS " + TABLE_CAT + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, description TEXT, pid INTEGER, country TEXT, countryCode TEXT, elements INTEGER, color TEXT, pin INTEGER, pos INTEGER, active INTEGER, datetime TEXT, dt INTEGER, tm TEXT, " +
            "UNIQUE(title) ON CONFLICT IGNORE);";

    private final String C_TABLE_RECENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_RECENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "search TEXT NOT NULL, description TEXT, country TEXT, countryCode TEXT, zipcode TEXT, city TEXT, neighborhood TEXT, logradouro TEXT, uf TEXT, " +
            "filter INTEGER, pid INTEGER, active INTEGER, datetime TEXT, dt INTEGER, tm TEXT, " +
            "UNIQUE(search) ON CONFLICT IGNORE);";

    private final String C_IDX_TOPICS_ACT = "CREATE INDEX IF NOT EXISTS tp_active_idx ON " + TABLE_TOPICS + "(active);";
    private final String C_IDX_TOPICS_UF = "CREATE INDEX IF NOT EXISTS tp_uf_idx ON " + TABLE_TOPICS + "(uf,active);";
    private final String C_IDX_TOPICS_PID = "CREATE INDEX IF NOT EXISTS tp_pid_idx ON " + TABLE_TOPICS + "(pid,active);";
    private final String C_IDX_TOPICS_FAV = "CREATE INDEX IF NOT EXISTS tp_fav_idx ON " + TABLE_TOPICS + "(fav,active);";
    private final String C_IDX_TOPICS_CEP = "CREATE INDEX IF NOT EXISTS tp_zipcode_idx ON " + TABLE_TOPICS + "(zipcode,active);";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

        db.execSQL(C_TABLE_CAT);
        db.execSQL(C_TABLE_TOPICS);
        db.execSQL(C_TABLE_RECENTS);

        db.execSQL(C_IDX_TOPICS_ACT);
        db.execSQL(C_IDX_TOPICS_FAV);
        db.execSQL(C_IDX_TOPICS_CEP);
        db.execSQL(C_IDX_TOPICS_UF);
        db.execSQL(C_IDX_TOPICS_PID);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // create new tables
        onCreate(db);

    }

}