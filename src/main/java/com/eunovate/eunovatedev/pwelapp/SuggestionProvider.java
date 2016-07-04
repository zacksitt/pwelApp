package com.eunovate.eunovatedev.pwelapp;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.eunovate.eunovatedev.pwelapp.dao_class.Database_helper;
import com.eunovate.eunovatedev.pwelapp.dao_class.Event_DbHelper;

import java.util.HashMap;

/**
 * Created by EunovateDev on 5/31/2016.
 */
public class SuggestionProvider extends ContentProvider {


    public static final String AUTHORITY = "com.eunovate.eunovatedev.pwelapp..CountryContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events" );

    private static final int SUGGESTIONS_COUNTRY = 1;
    private static final int SEARCH_COUNTRY = 2;
    private static final int GET_COUNTRY = 3;
    private Event_DbHelper event_dbHelper;

    UriMatcher mUriMatcher = buildUriMatcher();
    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS_COUNTRY);

        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchableActivity is provided by this uri
        // See android:searchSuggestIntentData="content://in.wptrafficanalyzer.searchdialogdemo.provider/countries" of searchable.xml
        uriMatcher.addURI(AUTHORITY, "events", SEARCH_COUNTRY);

        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        // Country details for CountryActivity is provided by this uri
        // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in CountryDB.java
        uriMatcher.addURI(AUTHORITY, "events/#", GET_COUNTRY);

        return uriMatcher;
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        // DbHelper dbHelper = new DbHelper(getContext());
        Database_helper dbHelper= new Database_helper(getContext());
        db = dbHelper.getReadableDatabase();
        event_dbHelper = new Event_DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor c = null;
        switch(mUriMatcher.match(uri)){
            case SUGGESTIONS_COUNTRY :
                c = event_dbHelper.get_events(selectionArgs);
                break;
            case SEARCH_COUNTRY :
                c = event_dbHelper.get_events(selectionArgs);
                break;
            case GET_COUNTRY :
                String id = uri.getLastPathSegment();
                c = event_dbHelper.getEvent(id);
        }

        return c;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
