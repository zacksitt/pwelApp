package com.eunovate.eunovatedev.pwelapp.dao_class;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by EunovateDev on 5/9/2016.
 */
public class Database_helper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public static final String DATABASE_NAME="event0010.db";
    public static final int DATABASE_VERSION=1;
    //event table field
    public static final String EVNT_TABLE_NAME="event";
    public static final String EVNT_COLUMN_ID="event_id";
    public static final String ORGANIZER_COLUMN_ID="organizer_id";
    public static final String VENUE_COLUMN_ID="venue_id";
    public static final String SUBCAT_COLUMN_ID="sub_cat_id";
    public static final String EVNT_COLUMN_IMG="image";
    public static final String EVNT_COLUMN_CONTACT="contact";
    public static final String EVNT_COLUMN_FBLINK="fb_link";
    public static final String EVNT_COLUMN_FAX="fax";
    public static final String EVNT_COLUMN_WEBSITE="website";
    public static final String EVNT_COLUMN_PRICECHK="price_chk";
    public static final String EVNT_COLUMN_NAME="name";
    public static final String EVNT_COLUMN_LATITUDE ="latitude";
    public static final String EVNT_COLUMN_EMAIL="email";
    public static final String EVNT_COLUMN_LONGTITUDE="longtitude";
    public static final String EVNT_COLUMN_SAVE="save_chk";
    public static final String EVNT_COLUMN_DESC="description";

    //venue table field
    public static final String VENUE_TABLE_NAME="venue";
    public static final String VENUE_NAME_COLUMN="name";
    public static final String VENUE_MAP_COLUMN="map";
    public static final String VENUE_CITYID_COLUMN="city_id";
    public static final String VENUE_DESCRIPTION_COLUMN="description";

    //Organizer table field
    public static final String ORGANIZER_TABLE_NAME="organizer";
    public static final String ORAGNIZER_COLUMN_NAME="name";
    public static final String ORAGNIZER_COLUMN_PHONE="phone";
    public static final String ORAGNIZER_COLUMN_EMAIL="email";
    public static final String ORAGNIZER_COLUMN_GOGLELINK="google_link";
    public static final String ORAGNIZER_COLUMN_TWITEERLINK="twitter_link";
    public static final String ORAGNIZER_COLUMN_ADDRESS="address";
    public static final String ORAGNIZER_COLUMN_COMPANYNAME="company_name";
    public static final String ORAGNIZER_COLUMN_FB_PAGE="fb_page";
    public static final String ORAGNIZER_COLUMN_CITYID="city_id";

    //Category table field
    public static final String CAT_TABLE_NAME="category";
    public static final String CAT_COLUMN_ID="category_id";
    public static final String CAT_COLUMN_DESC="description";
    //Sub Category table field
    public static final String SUBCAT_TABLE_NAME="sub_category";
    public static final String SUBCAT_COLUMN_DESC="description";

    //township table field
    public static final String TOWNSHIP_TABLE_NAME="township";
    public static final String TOWNSHIP_COLUMN_ID="township_id";
    public static final String CITY_COLUMN_ID="city_id";
    public static final String TOWNSHIP_COLUMN_NAME="township_name";

    //city table field
    public static final String CITY_TABLE_NAME="city";
    public static final String CITY_COLUMN_NAME="city_name";

    public static final String CREATE_EVENT_TABLE="CREATE TABLE "
            + EVNT_TABLE_NAME + "(" + EVNT_COLUMN_ID + " INTEGER PRIMARY KEY, " +ORGANIZER_COLUMN_ID + " INTEGER , " + VENUE_COLUMN_ID + " INTEGER, " +SUBCAT_COLUMN_ID + " INTEGER, "
            + EVNT_COLUMN_LATITUDE + " TEXT," + EVNT_COLUMN_LONGTITUDE + " TEXT, " + EVNT_COLUMN_FBLINK + " TEXT," + EVNT_COLUMN_FAX + " TEXT, " + EVNT_COLUMN_WEBSITE + " TEXT, "
            + EVNT_COLUMN_PRICECHK + " TEXT, " + EVNT_COLUMN_EMAIL + " TEXT, "
            + TOWNSHIP_COLUMN_ID +" INTEGER, "+CITY_COLUMN_ID +" INTEGER, "+ EVNT_COLUMN_NAME + " TEXT ," + EVNT_COLUMN_DESC + " TEXT," + EVNT_COLUMN_IMG + " TEXT, "
            + EVNT_COLUMN_CONTACT +" TEXT," + EVNT_COLUMN_SAVE +" INTEGER)";

    public static final String CREATE_VENUE_TABLE="CREATE TABLE " + VENUE_TABLE_NAME + "("
            + VENUE_COLUMN_ID + " INTEGER PRIMARY KEY, "+ VENUE_NAME_COLUMN + " TEXT, " + VENUE_MAP_COLUMN + " TEXT, " +
            VENUE_CITYID_COLUMN + " INTEGER, " + VENUE_DESCRIPTION_COLUMN + " TEXT)";

    public static final String CREATE_ORGANIZER_TABLE="CREATE TABLE " + ORGANIZER_TABLE_NAME + "("
            + ORGANIZER_COLUMN_ID + " INTEGER PRIMARY KEY, " + ORAGNIZER_COLUMN_NAME + " TEXT)";


    public static final String CREATE_CAT_TABLE="CREATE TABLE " + CAT_TABLE_NAME + "( "
            + CAT_COLUMN_ID + " INTEGER PRIMARY KEY, " + CAT_COLUMN_DESC + " TEXT)";

    public static final String CREATE_TOWNSHIP_TABLE="CREATE TABLE " + TOWNSHIP_TABLE_NAME+ "( "
            + TOWNSHIP_COLUMN_ID + " INTEGER PRIMARY KEY, " + CITY_COLUMN_ID+ " INTEGER, " + TOWNSHIP_COLUMN_NAME + " TEXT) ";

    public static final String CREATE_CITY_TABLE="CREATE TABLE " + CITY_TABLE_NAME + "( "
            + CITY_COLUMN_ID + " INTEGER PRIMARY KEY, " + CITY_COLUMN_NAME+ " TEXT) ";

    public static final String CREATE_SUBCAT_TABLE="CREATE TABLE " + SUBCAT_TABLE_NAME + "( "
            + SUBCAT_COLUMN_ID + " INTEGER PRIMARY KEY, " + CAT_COLUMN_ID + " INTEGER, " + SUBCAT_COLUMN_DESC  + " TEXT) ";

    private static Database_helper instance;

    public static synchronized Database_helper getHelper(Context context) {
        if (instance == null)
            instance = new Database_helper(context);
        return instance;
    }

    public Database_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(CREATE_EVENT_TABLE);
        Log.i("LOG TAG", "CREATED EVENT TABLE");
        db.execSQL(CREATE_VENUE_TABLE);
        Log.i("LOG TAG", "CREATED VENUE TABLE");
        db.execSQL(CREATE_ORGANIZER_TABLE);
        Log.i("LOG TAG", "CREATED ORGANIZER TABLE");
        db.execSQL(CREATE_CAT_TABLE);
        Log.i("LOG TAG", "CREATED CATEGORY TABLE");
        db.execSQL(CREATE_SUBCAT_TABLE);
        Log.i("LOG TAG", "CREATED SUB CATEGORY TABLE");
        db.execSQL(CREATE_CITY_TABLE);
        Log.i("LOG TAG", "CREATED SUB CATEGORY TABLE");
        db.execSQL(CREATE_TOWNSHIP_TABLE);
        Log.i("LOG TAG", "CREATED SUB CATEGORY TABLE");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        //TODO Auto-generated method stub
        onCreate(db);
    }
}
