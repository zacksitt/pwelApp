package com.eunovate.eunovatedev.pwelapp.dao_class;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.Toast;

import com.eunovate.eunovatedev.pwelapp.MainActivity;
import com.eunovate.eunovatedev.pwelapp.object.EventObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EunovateDev on 5/9/2016.
 */
public class Event_DbHelper {
    protected SQLiteDatabase database;
    private Database_helper dbHelper;
    private Context mContext;
    private ContentValues contentValues;
    private HashMap<String,String> mAliasMap;

    public Event_DbHelper(Context context){
        this.mContext=context;
        dbHelper=Database_helper.getHelper(mContext);

        mAliasMap = new HashMap<String, String>();

        // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put("_ID", "_id as " + "_id" );

        // Text for Suggestions ( Mandatory )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, " name as " + SearchManager.SUGGEST_COLUMN_TEXT_1);

        // Icon for Suggestions ( Optional )
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_ICON_1, " description as " + SearchManager.SUGGEST_COLUMN_ICON_1);

        // This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,  " _id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID );
        open();
    }

    public void open(){
        if(dbHelper==null)
            dbHelper=Database_helper.getHelper(mContext);
        database=dbHelper.getWritableDatabase();
    }

    public Cursor get_events(String[] selectionArgs){
        Cursor cursor;
        String selection = "name like ? ";

        if(selectionArgs!=null){
            selectionArgs[0] = "%"+selectionArgs[0] + "%";
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(mAliasMap);

        queryBuilder.setTables("event_");

        cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                new String[] { "_ID",
                        SearchManager.SUGGEST_COLUMN_TEXT_1 ,
                        SearchManager.SUGGEST_COLUMN_ICON_1 ,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } ,
                selection,
                selectionArgs,
                null,
                null,
                " name asc ","10"
        );
        return cursor;
    }

    public Cursor getEvent(String id){
        Cursor cursor;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables("event_");

        cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                new String[] { "_id", "name", "description"} ,
                //new String[] { "_id", "name", "description", "currency" } ,
                "_id = ?", new String[] { id } , null, null, null ,"1"
        );

        return  cursor;
    }

    public ArrayList<CategoryObj> get_subcats(int catid){

        ArrayList<CategoryObj> arrayList=new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + dbHelper.SUBCAT_TABLE_NAME + " WHERE " + dbHelper.CAT_COLUMN_ID + " = " + catid,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            CategoryObj cObj=new CategoryObj();

            cObj.setSub_category_id(cursor.getInt(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_ID)));
            cObj.setSubcat_desc(cursor.getString(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_DESC)));
            arrayList.add(cObj);
            cursor.moveToNext();

        }
        return arrayList;
    }

    public ArrayList<CategoryObj> get_cat_list(){
        ArrayList<CategoryObj> arrayList=new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + dbHelper.CAT_TABLE_NAME,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            CategoryObj cObj=new CategoryObj();

            cObj.setCategory_id(cursor.getInt(cursor.getColumnIndex(dbHelper.CAT_COLUMN_ID)));
            cObj.setCat_desc(cursor.getString(cursor.getColumnIndex(dbHelper.CAT_COLUMN_DESC)));
            arrayList.add(cObj);
            cursor.moveToNext();

        }
        return arrayList;
    }

    public ArrayList<CategoryObj> get_subcat_list(int cid){

        Cursor cursor = database.rawQuery(" SELECT * FROM " + dbHelper.SUBCAT_TABLE_NAME + " WHERE " + dbHelper.CAT_COLUMN_ID + " = " + cid ,null);
        ArrayList<CategoryObj> arrayList=new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            CategoryObj cObj=new CategoryObj();

            cObj.setSub_category_id(cursor.getInt(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_ID)));
            cObj.setSubcat_desc(cursor.getString(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_DESC)));
            arrayList.add(cObj);
            cursor.moveToNext();

        }
        return arrayList;
    }

    public Cursor get_evnt_detail(int eid ){
        EventObj eobj= new EventObj();
        Cursor cursor = database.rawQuery("SELECT e." + dbHelper.EVNT_COLUMN_NAME + ",e." + dbHelper.EVNT_COLUMN_DESC + ",e." + dbHelper.EVNT_COLUMN_IMG + ",e." + dbHelper.EVNT_COLUMN_CONTACT
                                    + " ,e." + dbHelper.EVNT_COLUMN_LATITUDE + ",e." +dbHelper.EVNT_COLUMN_LONGTITUDE
                                    + " ,o." + dbHelper.ORGANIZER_COLUMN_ID + ",o." + dbHelper.ORAGNIZER_COLUMN_NAME + " as oname,v." + dbHelper.VENUE_COLUMN_ID + ",v." +dbHelper.VENUE_NAME_COLUMN
                                    + " as vname ,sc. " + dbHelper.SUBCAT_COLUMN_DESC + " as subcat_desc,e." + dbHelper.SUBCAT_COLUMN_ID
                                    + " FROM " + dbHelper.EVNT_TABLE_NAME + " as e "
                                    + " LEFT JOIN " + dbHelper.SUBCAT_TABLE_NAME + " as sc ON sc." + dbHelper.SUBCAT_COLUMN_ID + " = e." + dbHelper.SUBCAT_COLUMN_ID
                                    + " LEFT JOIN " + dbHelper.ORGANIZER_TABLE_NAME + " as o ON o." + dbHelper.ORGANIZER_COLUMN_ID + " = e." + dbHelper.ORGANIZER_COLUMN_ID
                                    + " LEFT JOIN " + dbHelper.VENUE_TABLE_NAME + " as v ON v." + dbHelper.VENUE_COLUMN_ID + " = e." + dbHelper.VENUE_COLUMN_ID
                                    + " WHERE e." + dbHelper.EVNT_COLUMN_ID + " = " + eid ,null);

        return cursor;
    }

    public void save_event(EventObj eventObj){
        contentValues = new ContentValues();
        contentValues.put(dbHelper.EVNT_COLUMN_ID,eventObj.getEvent_id());
        contentValues.put(dbHelper.ORGANIZER_COLUMN_ID,eventObj.getOrganizer_id());
        contentValues.put(dbHelper.VENUE_COLUMN_ID,eventObj.getVenue_id());
        contentValues.put(dbHelper.EVNT_COLUMN_IMG,eventObj.getImage());
        contentValues.put(dbHelper.SUBCAT_COLUMN_DESC,eventObj.getSubcat_desc());
        contentValues.put(dbHelper.SUBCAT_COLUMN_ID,eventObj.getSub_cat_id());
        contentValues.put(dbHelper.EVNT_COLUMN_NAME,eventObj.getName());
        contentValues.put(dbHelper.EVNT_COLUMN_DESC,eventObj.getDescription());
        contentValues.put(dbHelper.EVNT_COLUMN_EMAIL,eventObj.getEmail());
        contentValues.put(dbHelper.EVNT_COLUMN_WEBSITE,eventObj.getWebsite());
        contentValues.put(dbHelper.EVNT_COLUMN_CONTACT,eventObj.getContact());
        contentValues.put(dbHelper.EVNT_COLUMN_FAX,eventObj.getFax());
        contentValues.put(dbHelper.EVNT_COLUMN_FBLINK,eventObj.getFb_link());
        contentValues.put(dbHelper.EVNT_COLUMN_PRICECHK,eventObj.getPrice_chk());
        contentValues.put(dbHelper.EVNT_COLUMN_LATITUDE,eventObj.getLatitude());
        contentValues.put(dbHelper.EVNT_COLUMN_LONGTITUDE,eventObj.getLongtitue());
        database.insert(dbHelper.EVNT_TABLE_NAME,null,contentValues);
    }

    public void clear_category(){
        database.delete(dbHelper.CAT_TABLE_NAME,null,null);
    }
    public void clear_sub_category(){
        database.delete(dbHelper.SUBCAT_TABLE_NAME,null,null);
    }

    public void save_category(JSONObject job){

        try {
            contentValues = new ContentValues();
            contentValues.put(dbHelper.CAT_COLUMN_ID,job.getInt("category_id"));
            contentValues.put(dbHelper.CAT_COLUMN_DESC,job.getString("category_name"));
            database.insert(dbHelper.CAT_TABLE_NAME,null,contentValues);
        } catch (JSONException e) {
            Log.e("LOG TAG", "JSON Parsing error: " + e.getMessage());
        }
    }

    public Cursor get_event_cursor(){
          Cursor cursor = database.rawQuery("SELECT _id,name,description FROM event_",null);
//        Cursor cursor = database.rawQuery("SELECT e." + dbHelper.EVNT_COLUMN_NAME +",sc.description "+
//                " FROM " + dbHelper.EVNT_TABLE_NAME + " as e "
//                + " LEFT JOIN " + dbHelper.SUBCAT_TABLE_NAME + " as sc ON sc." + dbHelper.SUBCAT_COLUMN_ID + " = e. " + dbHelper.SUBCAT_COLUMN_ID,null);
        return cursor;
    }

    public  void save_save_category(JSONObject job){
        try{
            contentValues = new ContentValues();
            contentValues.put(dbHelper.SUBCAT_COLUMN_ID,job.getInt("sub_category_id"));
            contentValues.put(dbHelper.CAT_COLUMN_ID,job.getInt("category_id"));
            contentValues.put(dbHelper.SUBCAT_COLUMN_DESC,job.getString("sub_category_name"));
            database.insert(dbHelper.SUBCAT_TABLE_NAME,null,contentValues);

        } catch (JSONException e) {
            Log.e("LOG TAG", "JSON Parsing error: " + e.getMessage());
        }
    }

    public void save_this_event(int eid){
        contentValues = new ContentValues();
        contentValues.put(dbHelper.EVNT_COLUMN_SAVE,1);
        database.update(dbHelper.EVNT_TABLE_NAME, contentValues, "event_id=?", new String[]{Integer.toString(eid)});
    }

    public ArrayList<EventObj> get_event_by_cat(int cat_id){
        ArrayList<EventObj> arrList=new ArrayList<>();
        Cursor cursor= database.rawQuery("SELECT  e."+dbHelper.EVNT_COLUMN_ID+ ",e."+dbHelper.EVNT_COLUMN_IMG +",e."+dbHelper.EVNT_COLUMN_NAME +",e."+dbHelper.EVNT_COLUMN_DESC
                +" ,sc." + dbHelper.SUBCAT_COLUMN_DESC + " as subcat_desc,sc. "+dbHelper.SUBCAT_COLUMN_ID +" ,v." +dbHelper.VENUE_NAME_COLUMN + " as vname"
                + " FROM "+ dbHelper.EVNT_TABLE_NAME + " as e "
                + " LEFT JOIN " + dbHelper.SUBCAT_TABLE_NAME + " as sc ON sc." + dbHelper.SUBCAT_COLUMN_ID + " = e." + dbHelper.SUBCAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CAT_TABLE_NAME + " as c ON c." + dbHelper.CAT_COLUMN_ID + " = sc." +dbHelper.CAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.TOWNSHIP_TABLE_NAME + " as t ON t." + dbHelper.TOWNSHIP_COLUMN_ID + " = e." +dbHelper.TOWNSHIP_COLUMN_ID
                + " LEFT JOIN " + dbHelper.VENUE_TABLE_NAME + " as v ON v." + dbHelper.VENUE_COLUMN_ID + " = e." + dbHelper.VENUE_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CITY_TABLE_NAME + " as cty ON cty." + dbHelper.CITY_COLUMN_ID + " = t." + dbHelper.CITY_COLUMN_ID
                + " WHERE sc." + dbHelper.SUBCAT_COLUMN_ID + " = " + cat_id,null);

        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            EventObj eobj=new EventObj();
            eobj.setEvent_id(cursor.getInt(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_ID)));
            eobj.setImage(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_IMG)));
            eobj.setSub_cat_id(cursor.getInt(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_ID)));
            eobj.setName(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_NAME)));
            eobj.setSubcat_desc(cursor.getString(cursor.getColumnIndex("subcat_desc")));
            eobj.setVenue(cursor.getString(cursor.getColumnIndex("vname")));
            eobj.setDescription(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_DESC)));
            arrList.add(eobj);
            cursor.moveToNext();

        }
        return arrList;
    }

    public ArrayList<EventObj> search_event(int cat_id,int sub_cat_id,int location_id,int time_id){

        ArrayList<EventObj> arrList=new ArrayList<>();
        Cursor cursor= database.rawQuery("SELECT  e."+dbHelper.EVNT_COLUMN_ID+ ",e."+dbHelper.EVNT_COLUMN_IMG +",e."+dbHelper.EVNT_COLUMN_NAME +",e."+dbHelper.EVNT_COLUMN_DESC
                +" ,sc." + dbHelper.SUBCAT_COLUMN_DESC + " as subcat_desc,sc. "+dbHelper.SUBCAT_COLUMN_ID +" ,v." +dbHelper.VENUE_NAME_COLUMN + " as vname"
                + " FROM "+ dbHelper.EVNT_TABLE_NAME + " as e "
                + " LEFT JOIN " + dbHelper.SUBCAT_TABLE_NAME + " as sc ON sc." + dbHelper.SUBCAT_COLUMN_ID + " = e." + dbHelper.SUBCAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CAT_TABLE_NAME + " as c ON c." + dbHelper.CAT_COLUMN_ID + " = sc." +dbHelper.CAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.TOWNSHIP_TABLE_NAME + " as t ON t." + dbHelper.TOWNSHIP_COLUMN_ID + " = e." +dbHelper.TOWNSHIP_COLUMN_ID
                + " LEFT JOIN " + dbHelper.VENUE_TABLE_NAME + " as v ON v." + dbHelper.VENUE_COLUMN_ID + " = e." + dbHelper.VENUE_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CITY_TABLE_NAME + " as cty ON cty." + dbHelper.CITY_COLUMN_ID + " = t." + dbHelper.CITY_COLUMN_ID
                + " WHERE cty." + dbHelper.CITY_COLUMN_ID + " = " +location_id + " OR c." + dbHelper.CAT_COLUMN_ID + " = " + cat_id + " OR sc."
                + dbHelper.SUBCAT_COLUMN_ID + " = " + sub_cat_id,null);

        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            EventObj eobj=new EventObj();
            eobj.setEvent_id(cursor.getInt(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_ID)));
            eobj.setImage(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_IMG)));
            eobj.setSub_cat_id(cursor.getInt(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_ID)));
            eobj.setName(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_NAME)));
            eobj.setSubcat_desc(cursor.getString(cursor.getColumnIndex("subcat_desc")));
            eobj.setVenue(cursor.getString(cursor.getColumnIndex("vname")));
            eobj.setDescription(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_DESC)));
            arrList.add(eobj);
            cursor.moveToNext();

        }
        return arrList;
    }

    public int get_event_last_id(){
        int last_id=0;
        Cursor cursor = database.rawQuery("SELECT " + dbHelper.EVNT_COLUMN_ID + " FROM " + dbHelper.EVNT_TABLE_NAME + " ORDER BY " + dbHelper.EVNT_COLUMN_ID + " DESC LIMIT 1",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            last_id = cursor.getInt(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_ID));
            cursor.moveToNext();
        }
        return last_id;
    }

    public ArrayList<EventObj> get_all_events(){
        ArrayList<EventObj> arrList=new ArrayList<>();
        Cursor cursor= database.rawQuery("SELECT  e."+dbHelper.EVNT_COLUMN_ID+ ",e."+dbHelper.EVNT_COLUMN_IMG +",e."+dbHelper.EVNT_COLUMN_NAME +",e."+dbHelper.EVNT_COLUMN_DESC
                +" ,sc." + dbHelper.SUBCAT_COLUMN_DESC + " as subcat_desc,sc. "+dbHelper.SUBCAT_COLUMN_ID +" ,v." +dbHelper.VENUE_NAME_COLUMN + " as vname"
                +" FROM "+ dbHelper.EVNT_TABLE_NAME + " as e "
                + " LEFT JOIN " + dbHelper.SUBCAT_TABLE_NAME + " as sc ON sc." + dbHelper.SUBCAT_COLUMN_ID + " = e." + dbHelper.SUBCAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CAT_TABLE_NAME + " as c ON c." + dbHelper.CAT_COLUMN_ID + " = sc." +dbHelper.CAT_COLUMN_ID
                + " LEFT JOIN " + dbHelper.TOWNSHIP_TABLE_NAME + " as t ON t." + dbHelper.TOWNSHIP_COLUMN_ID + " = e." +dbHelper.TOWNSHIP_COLUMN_ID
                + " LEFT JOIN " + dbHelper.VENUE_TABLE_NAME + " as v ON v." + dbHelper.VENUE_COLUMN_ID + " = e." + dbHelper.VENUE_COLUMN_ID
                + " LEFT JOIN " + dbHelper.CITY_TABLE_NAME + " as cty ON cty." + dbHelper.CITY_COLUMN_ID + " = t." + dbHelper.CITY_COLUMN_ID,null);

        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            EventObj eobj=new EventObj();
            eobj.setEvent_id(cursor.getInt(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_ID)));
            eobj.setImage(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_IMG)));
            eobj.setSub_cat_id(cursor.getInt(cursor.getColumnIndex(dbHelper.SUBCAT_COLUMN_ID)));
            eobj.setName(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_NAME)));
            eobj.setSubcat_desc(cursor.getString(cursor.getColumnIndex("subcat_desc")));
            eobj.setVenue(cursor.getString(cursor.getColumnIndex("vname")));
            eobj.setDescription(cursor.getString(cursor.getColumnIndex(dbHelper.EVNT_COLUMN_DESC)));
            arrList.add(eobj);
            cursor.moveToNext();

        }
        return arrList;
    }
}
