package com.eunovate.eunovatedev.pwelapp;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eunovate.eunovatedev.pwelapp.dao_class.Event_DbHelper;

public class SearchableActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    ListView mLVCountries;
    SimpleCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LOG TAG","LOAD searachable activity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchable_);

        onSearchRequested();
        // Getting reference to Country List
        mLVCountries = (ListView)findViewById(R.id.lv_events);

        // Setting item click listener
        mLVCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Intent countryIntent = new Intent(getApplicationContext(), CountryActivity.class);
//
//                // Creating a uri to fetch country details corresponding to selected listview item
//                Uri data = Uri.withAppendedPath(CountryContentProvider.CONTENT_URI, String.valueOf(id));
//
//                // Setting uri to the data on the intent
//                countryIntent.setData(data);
//
//                // Open the activity
//                startActivity(countryIntent);
            }
        });

        // Defining CursorAdapter for the ListView
        mCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[] { android.R.id.text1}, 0);

        // Setting the cursor adapter for the country listview
        mLVCountries.setAdapter(mCursorAdapter);

        // Getting the intent that invoked this activity
        Intent intent = getIntent();
        // If this activity is invoked by selecting an item from Suggestion of Search dialog or
        // from listview of SearchActivity
        if(intent.getAction().equals(Intent.ACTION_VIEW)){
            Log.i("Log Tag","INTENT EVENT HANDLER");
//            Intent countryIntent = new Intent(this, CountryActivity.class);
//            countryIntent.setData(intent.getData());
//            startActivity(countryIntent);
            finish();
        }else if(intent.getAction().equals(Intent.ACTION_SEARCH)){ // If this activity is invoked, when user presses "Go" in the Keyboard of Search Dialog
            Log.i("Log Tag","INTENT EVENT HANDLER");
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);

        // Invoking onCreateLoader() in non-ui thread
        getSupportLoaderManager().initLoader(1, data, this);
    }

    /** This method is invoked by initLoader() */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
       Uri uri = SuggestionProvider.CONTENT_URI;
        return new CursorLoader(getBaseContext(), uri, null, null , new String[]{data.getString("query")}, null);
    }

    /** This method is executed in ui thread, after onCreateLoader() */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        mCursorAdapter.swapCursor(c);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    /** This method is invoked by initLoader() */



    /** This method is executed in ui thread, after onCreateLoader() */



}


