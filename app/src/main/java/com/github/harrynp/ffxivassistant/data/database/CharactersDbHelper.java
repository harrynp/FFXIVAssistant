package com.github.harrynp.ffxivassistant.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CharactersDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "characters.db";
    private static final int DATABASE_VERSION = 1;

    public CharactersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHARACTERS_TABLE =
                "CREATE TABLE " +
                        CharactersContract.CharactersEntry.TABLE_NAME           + "(" +
                        CharactersContract.CharactersEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID  + " INTEGER NOT NULL, " +
                        CharactersContract.CharactersEntry.COLUMN_NAME          + " TEXT NOT NULL, " +
                        CharactersContract.CharactersEntry.COLUMN_SERVER        + " TEXT NOT NULL, " +
                        CharactersContract.CharactersEntry.COLUMN_ICON_PATH     + " TEXT NOT NULL, " +
                        CharactersContract.CharactersEntry.COLUMN_URL_API       + " TEXT NOT NULL, " +
                        CharactersContract.CharactersEntry.COLUMN_URL_XIVDB     + " TEXT NOT NULL, " +
                        " UNIQUE (" + CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_CHARACTERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CharactersContract.CharactersEntry.TABLE_NAME);
        onCreate(db);
    }
}
