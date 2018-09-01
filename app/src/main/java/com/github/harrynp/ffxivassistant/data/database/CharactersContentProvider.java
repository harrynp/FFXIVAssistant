package com.github.harrynp.ffxivassistant.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.github.harrynp.ffxivassistant.data.database.CharactersContract.CharactersEntry.TABLE_NAME;

public class CharactersContentProvider extends ContentProvider {
    public static final int CHARACTERS = 100;
    public static final int CHARACTERSS_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CharactersDbHelper mCharactersDbHelper;


    public CharactersContentProvider() {
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CharactersContract.CONTENT_AUTHORITY, CharactersContract.PATH_CHARACTERS, CHARACTERS);
        uriMatcher.addURI(CharactersContract.CONTENT_AUTHORITY, CharactersContract.PATH_CHARACTERS + "/#", CHARACTERSS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mCharactersDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int charactersDeleted;

        if (null == selection) selection = "1";

        switch (match) {
            case CHARACTERS:
                charactersDeleted = db.delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CHARACTERSS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                charactersDeleted = db.delete(TABLE_NAME, CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (charactersDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return charactersDeleted;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CHARACTERS:
                return CharactersContract.CharactersEntry.CONTENT_TYPE;
            case CHARACTERSS_WITH_ID:
                return CharactersContract.CharactersEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mCharactersDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CHARACTERS:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CharactersContract.CharactersEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mCharactersDbHelper = new CharactersDbHelper(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mCharactersDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case CHARACTERS:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mCharactersDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case CHARACTERS:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
