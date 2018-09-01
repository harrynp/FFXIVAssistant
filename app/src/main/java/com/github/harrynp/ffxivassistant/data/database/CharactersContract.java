package com.github.harrynp.ffxivassistant.data.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.github.harrynp.ffxivassistant.BuildConfig;

public class CharactersContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

    public static final Uri BASE_CONTENT_URI =  Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CHARACTERS = "characters";

    public static final class CharactersEntry implements BaseColumns {
        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CHARACTERS)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTERS;

        public static final String TABLE_NAME = "characters";

        public static final String COLUMN_CHARACTER_ID = "character_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVER = "server";
        public static final String COLUMN_ICON_PATH = "icon_path";
        public static final String COLUMN_URL_API = "url_api";
        public static final String COLUMN_URL_XIVDB = "url_xivdb";


        public static Uri buildUriCharacterId(int characterId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(characterId))
                    .build();
        }
    }
}
