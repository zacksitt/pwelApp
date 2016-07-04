package com.eunovate.eunovatedev.pwelapp;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.eunovate.eunovatedev.pwelapp.dao_class.CategoryObj;
import com.eunovate.eunovatedev.pwelapp.dao_class.Database_helper;
import com.eunovate.eunovatedev.pwelapp.dao_class.Event_DbHelper;
import com.eunovate.eunovatedev.pwelapp.object.EventObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,SwipeRefreshLayout.OnRefreshListener {

    private String TAG = MainActivity.class.getSimpleName();
    private String URL_TOP_250 = "http://api.androidhive.info/json/imdb_top_250.php?offset=";
    private ListView listView;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    // Online Server API Link
    //public static final String BASE_URL="http://128.199.208.42/myme/service/index.php/Schedule_output_ctrl/";
    // Geny Emulator API Link
     public static final String BASE_IMGURL="http://10.0.3.2/mmevents_admin/static/img/pmt_thumbnail/";
     public static final String BASE_URL="http://10.0.3.2/mmevents_admin/service/index.php/Event_output_ctrl/";

    // Android Emulator API Link
//    public static final String BASE_URL="http://10.0.2.2/mmevents_admin/service/index.php/Event_output_ctrl/";
//    public static final String BASE_IMGURL="http://10.0.2.2/mmevents_admin/static/img/pmt_thumbnail/";

    private Spinner cat_spinner,time_spinner,location_spinner,subcat_spinner;
    private List<String> category,time_option,location,sub_category;
    private ArrayAdapter cat_adapter,time_adapter,location_adapter,subcat_adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PopupWindow popupWindow;
    private CardView ecard_view;
    private ListView evnt_listview;
    private FancyAdapter Evt_Adapter;
    private EventObj eobj;
    private ArrayList<CategoryObj> catList;
    private ArrayList<CategoryObj> subcatList;
    private Database_helper dbHelper;
    private ArrayList<EventObj> evnt_list;
    private Event_DbHelper evdb;
    private MySingleton mySingleton;
    private ArrayList<String> menuList;
    private CategoryObj catObj;
    private CategoryObj subcatObj;
    private Typeface typeface;
    private Button cat1_btn;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private String mActivityTitle;
    private SearchView mSearchView;
    private ActionBarDrawerToggle mDrawerToggle;
    private int cat_select_id,subcat_select_id,time_select_id,location_select_id,param_id=0,cur_event_id;
    private Button hot_btn,party_btn,music_btn,exhibitions_btn;
    private ImageView msg_pic;
    private SwipeRefreshLayout evnt_layout;
    private TextView msg_txt;
    private Toolbar toolbar;
    private MenuItem refresh,expend_btn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public  static final String MyPREFERENCES="MyPrefs";
    private static final int PREFERENCE_MODE_PRIVATE=0;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String local = sharedPreferences.getString("locale",null);
        Log.i("LOG TAG","LOCALE : " +local);
        if(local!=null){
            if(local.equals("mm"))
                setLocale("mm");
            else
                setLocale("en");
        }else{
            setLocale("en");
            editor.putString("locale","en");
            editor.commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        getWindow().setWindowAnimations(0);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.event_refresh_layout);
        evnt_listview=(ListView) findViewById(R.id.evnt_list);

        msg_txt = (TextView) findViewById(R.id.msg_txt);
        msg_pic = (ImageView) findViewById(R.id.msg_pic);
        hot_btn= (Button) findViewById(R.id.hot_event);
        party_btn = (Button) findViewById(R.id.cat1_btn);
        music_btn = (Button) findViewById(R.id.cat3_btn);
        exhibitions_btn = (Button) findViewById(R.id.cat2_btn);
        evnt_layout = (SwipeRefreshLayout) findViewById(R.id.event_refresh_layout);

        cat1_btn = (Button) findViewById(R.id.cat1_btn);
        typeface= Typeface.createFromAsset(getAssets(), "fonts/ZawgyiOne2008.ttf");
        cat1_btn.setTypeface(typeface);


        ecard_view=(CardView) findViewById(R.id.event_cardview);

        dbHelper = new Database_helper(this);
        evdb = new Event_DbHelper(this);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            param_id=extras.getInt("param");
            Log.i("LOG TAG","WITH PARAM : " +param_id);
            load_event_by_cat(param_id);
        }else{
            load_event_list(0);
        }
        mDrawerList = (ListView)findViewById(R.id.navList);
        //Get Drawer Layout
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                        //   load_more_event();
                                    }
                                }
        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog ftr_dialog = new Dialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                ftr_dialog.setContentView(R.layout.filter_box_dialog);

                cat_spinner = (Spinner) ftr_dialog.findViewById(R.id.spinner_category);
                subcat_spinner = (Spinner) ftr_dialog.findViewById(R.id.spinner_sub_category);
                time_spinner = (Spinner) ftr_dialog.findViewById(R.id.spinner_time);
                location_spinner = (Spinner) ftr_dialog.findViewById(R.id.spinner_location);

                cat_spinner.setOnItemSelectedListener(MainActivity.this);
                subcat_spinner.setOnItemSelectedListener(MainActivity.this);
                time_spinner.setOnItemSelectedListener(MainActivity.this);
                location_spinner.setOnItemSelectedListener(MainActivity.this);

                category = new ArrayList<>();
                time_option = new ArrayList<>();
                location = new ArrayList<>();
                catList = evdb.get_cat_list();
                //category.add("Choose Category");
                for (int i = 0; i < catList.size(); i++) {
                    catObj = new CategoryObj();
                    catObj = catList.get(i);
                    category.add(catObj.getCat_desc());
                }

                time_option.add("This Week");
                time_option.add("This Month");
                time_option.add("This Year");
                location.add("All Location");

                cat_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, category);
                // Drop down layout style - list view with radio button
                cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                cat_spinner.setAdapter(cat_adapter);
                time_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, time_option);
                // Drop down layout style - list view with radio button
                time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                time_spinner.setAdapter(time_adapter);
                location_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, location);
                // Drop down layout style - list view with radio button
                location_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                location_spinner.setAdapter(location_adapter);
                Button cancelBtn = (Button) ftr_dialog.findViewById(R.id.cancel);
                // if button is clicked, close the custom dialog
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ftr_dialog.dismiss();
                    }
                });
                Button searchBtn = (Button) ftr_dialog.findViewById(R.id.search);
                // if button is clicked, close the custom dialog

                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_header("Search Events");
                        load_event_list(1);
                        ftr_dialog.dismiss();
                    }
                });

                ftr_dialog.show();
                handleIntent(getIntent());
            }
        });


       //setLocale("mm");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.i("LOG TAG","SEARCH QUERY :"+query);
        }
    }

    public void change_header(String title){

        refresh.setVisible(false);
        expend_btn.setVisible(false);
        getSupportActionBar().setTitle(title);
        //setTitle(title);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                refresh.setVisible(true);
                expend_btn.setVisible(true);

//                getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
//                getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
//                getSupportActionBar().setTitle("Event");

                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    public void get_hot_event(View v){
        change_header("Hot Events");
        party_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        exhibitions_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        music_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        hot_btn.setTextColor(getResources().getColor(R.color.icons));
        load_event_list(0);
    }
    public void get_party(View v){
        change_header("Parties");
        party_btn.setTextColor(getResources().getColor(R.color.icons));
        exhibitions_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        music_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        hot_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        load_event_by_cat(2);
    }

    public void get_exhibitions(View view){
        change_header("Exhibitions");
        party_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        exhibitions_btn.setTextColor(getResources().getColor(R.color.icons));
        music_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        hot_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        load_event_by_cat(1);
    }

    public void get_music_event(View view){
        change_header("Music Concert");
        party_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        exhibitions_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        music_btn.setTextColor(getResources().getColor(R.color.icons));
        hot_btn.setTextColor(getResources().getColor(R.color.txt_normal));
        load_event_by_cat(3);
    }

    public void load_event_by_cat(int cat_id){

        evnt_list = evdb.get_event_by_cat(cat_id);
        if(evnt_list.size()>0){

            evnt_layout.setVisibility(View.VISIBLE);
            msg_pic.setVisibility(View.GONE);
            msg_txt.setVisibility(View.GONE);

            Evt_Adapter=new FancyAdapter();
            evnt_listview.setAdapter(Evt_Adapter);

            swipeRefreshLayout.setRefreshing( false );
            swipeRefreshLayout.setEnabled( false );

            evnt_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
                    String selected=((TextView)arg1.findViewById(R.id.evnt_id)).getText().toString();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putInt("param", Integer.parseInt(selected));
                    Intent intent = new Intent(MainActivity.this, EventDetail_Activity.class);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
            });
        }else{
            evnt_layout.setVisibility(View.GONE);
            msg_txt.setVisibility(View.VISIBLE);
            msg_pic.setVisibility(View.VISIBLE);
        }
    }

    public void load_event_list(int method_check){
        // Doesn't Show error Msg on Main Page

        Boolean is_mainpage = true;
        if(method_check>0) {
            is_mainpage = false;
            swipeRefreshLayout.setRefreshing( false );
            swipeRefreshLayout.setEnabled( false );
            evnt_list = evdb.search_event(cat_select_id, subcat_select_id, location_select_id, time_select_id);
        }else {
            evnt_list = evdb.get_all_events();
        }

       if(evnt_list.size()>0 || is_mainpage ){

            evnt_layout.setVisibility(View.VISIBLE);
            msg_pic.setVisibility(View.GONE);
            msg_txt.setVisibility(View.GONE);

            Evt_Adapter = new FancyAdapter();
            evnt_listview.setAdapter(Evt_Adapter);

            evnt_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
                   String selected = ((TextView) arg1.findViewById(R.id.evnt_id)).getText().toString();
                   Bundle dataBundle = new Bundle();
                   dataBundle.putInt("param", Integer.parseInt(selected));
                   Intent intent = new Intent(MainActivity.this, EventDetail_Activity.class);
                   intent.putExtras(dataBundle);
                    startActivity(intent);
                }
            });
        }else{

            evnt_layout.setVisibility(View.GONE);
            msg_txt.setVisibility(View.VISIBLE);
            msg_pic.setVisibility(View.VISIBLE);
        }

    }

    public void save_event(View view){
        Intent intent = new Intent(MainActivity.this, Facebook_login_Activity.class);
        startActivity(intent);
    }

    private class get_event_from_api extends AsyncTask<String,String,String> {

        //private ProgressDialog progressDialog=ProgressDialog.show(MainActivity.this,"","Loading..");
        protected void onPreExecute(){}
        @Override
        protected String doInBackground(String... arg0){
            try {
                //int last_sync_count=atd_db.get_sync_schedule_count();
                String link=BASE_URL+"get_all_event";
                //String data= URLEncoder.encode("user_id", "UTF-8")+" = "+ URLEncoder.encode(String.valueOf(user_id),"UTF-8");
                /// TEMP CODE //////////////
                int even_last_id = evdb.get_event_last_id();
                JSONObject jobj= new JSONObject();
                jobj.put("last_event_id",even_last_id);
                Log.i("LOG TAG","LAST EVENT ID : "+even_last_id);
                String data = jobj.toString();
                //// END TEMP CODE ///////////////
                URL url=new URL(link);
                URLConnection conn=url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr=new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb=new StringBuffer();
                String line=null;
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                }
                return sb.toString();
            }
            catch (Exception e){
                return new String("Exception: "+e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result){
            Log.i("LOG TAG","GET DATA RESULT : "+result);
            if(!result.equals("false")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    save_category(jsonObject.getJSONArray("cat_data"));
                    save_sub_category(jsonObject.getJSONArray("sub_cat_data"));
                    save_event_jarray(jsonObject.getJSONArray("edata"));
                }catch (Exception e){}

            }
        }
    }

    public void save_category(JSONArray jsonArray){
        if(jsonArray.length()>0){
                    evdb.clear_category();
          try{
                for (int i=0; i<jsonArray.length();i++){
                    evdb.save_category(jsonArray.getJSONObject(i));
                }
              Toast.makeText(MainActivity.this, "Saved Category.", Toast.LENGTH_SHORT).show();
              } catch (JSONException e) {
                  //Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                  Toast.makeText(MainActivity.this, "Can't add event.", Toast.LENGTH_SHORT).show();
              }
        }
    }

    public void save_sub_category(JSONArray jsonArray){
        if(jsonArray.length()>0){
            evdb.clear_sub_category();
            try{
                for (int i=0; i<jsonArray.length();i++){
                    evdb.save_save_category(jsonArray.getJSONObject(i));
                }
                Toast.makeText(MainActivity.this, "Saved Sub Category.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                //Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Can't add event.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void save_event_jarray(JSONArray jsonArray){

        if(jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject eventObj = jsonArray.getJSONObject(i);
                    eobj = new EventObj();
                    eobj.setEvent_id(eventObj.getInt("event_id"));
                    Log.i("LOG TAG", "GET EVENT ID " + eobj.getEvent_id());
                    eobj.setName(eventObj.getString("title"));
                    eobj.setDescription(eventObj.getString("ec_description"));
                    eobj.setContact(eventObj.getString("contact"));
                    eobj.setLatitude(eventObj.getString("map_lat"));
                    eobj.setLongtitue(eventObj.getString("map_lng"));
                    eobj.setWebsite(eventObj.getString("website"));
                    eobj.setFax(eventObj.getString("fax"));
                    eobj.setEmail(eventObj.getString("email"));
                    eobj.setPrice_chk(eventObj.getInt("price_chk"));
                    eobj.setOrganizer_id(eventObj.getInt("organizer_id"));
                    eobj.setVenue_id(eventObj.getInt("venue_id"));
                    eobj.setContact(eventObj.getString("contact"));
                    eobj.setSub_cat_id(eventObj.getInt("sub_category_id"));
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String img_path = getFilesDir() + "/" + eventObj.getString("imgfile");
                    String url = BASE_IMGURL + "" + eventObj.getString("imgfile");
                    eobj.setImage("sample image");

//                try {
//                    //DownloadFile(url,img_path);
//                    //eobj.setImage(img_path);
//
//                } catch (IOException e) {
//                    // Something went wrong here
//                }

                    evdb.save_event(eobj);

                } catch (JSONException e) {
                    //Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Can't add event.", Toast.LENGTH_SHORT).show();
                }
            }

            Toast.makeText(MainActivity.this, "Added event.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "No new event.", Toast.LENGTH_SHORT).show();
        }
        load_event_list(0);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void load_more_event(){
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        final String TAG = "0";
        int even_last_id = evdb.get_event_last_id();

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(BASE_URL + "get_all_event",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject eventObj = response.getJSONObject(i);
                                    eobj = new EventObj();
                                    eobj.setEvent_id(eventObj.getInt("event_id"));
                                    eobj.setName(eventObj.getString("title"));
                                    eobj.setDescription(eventObj.getString("ec_description"));
                                    eobj.setContact(eventObj.getString("contact"));
                                    eobj.setLatitude(eventObj.getString("map_lat"));
                                    eobj.setLongtitue(eventObj.getString("map_lng"));
                                    eobj.setWebsite(eventObj.getString("website"));
                                    eobj.setFax(eventObj.getString("fax"));
                                    eobj.setEmail(eventObj.getString("email"));
                                    eobj.setPrice_chk(eventObj.getInt("price_chk"));
                                    eobj.setOrganizer_id(eventObj.getInt("organizer_id"));
                                    eobj.setVenue_id(eventObj.getInt("venue_id"));
                                    eobj.setContact(eventObj.getString("contact"));
                                    eobj.setSub_cat_id(eventObj.getInt("sub_category_id"));
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String img_path= getFilesDir() + "/" + eventObj.getString("imgfile");
                                    String url = BASE_IMGURL + "" + eventObj.getString("imgfile");

                                    try {
                                        DownloadFile(url,img_path);
                                        eobj.setImage(img_path);
                                      } catch (IOException e) {
                                            // Something went wrong here
                                        }

                                    evdb.save_event(eobj);
//                                    int rank = movieObj.getInt("rank");
//                                    String title = movieObj.getString("title");
//
//
//                                    // updating offset value to highest value
//                                    if (rank >= offSet)
//                                        offSet = rank;

                                } catch (JSONException e) {
                                    //Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                    Toast.makeText(MainActivity.this, "Can't add event.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Toast.makeText(MainActivity.this, "Added event.", Toast.LENGTH_SHORT).show();
                            load_event_list(0);

                           // adapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG TAG", "Server Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        MySingleton.getInstance().addToRequestQueue(req);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {
        new get_event_from_api().execute();
        //_event();
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void openDrawer(){ mDrawerLayout.openDrawer(mDrawerList); }

    private void addDrawerItems() {

        menuList=new ArrayList<String>();
        menuList.add("About");
        menuList.add("Setting");
        menuList.add("Save");
        menuList.add("Logout");

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeDrawer();
                if(position==0){
                    onSearchRequested();
                }
                else if(position==1){
                    Intent intent = new Intent(MainActivity.this, Setting_Activity.class);
                    startActivity(intent);
                }else{
                    //Do something
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // setTitle("Navigation");
                //getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu();
                // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerClosed(View view) {

                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
                // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public static void DownloadFile(String imageURL, String fileName) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(imageURL);
        File file = new File(fileName);

        long startTime = System.currentTimeMillis();
        URLConnection ucon = url.openConnection();
        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        FileOutputStream fos = new FileOutputStream(file);

        int current = 0;
        while ((current = bis.read()) != -1) {
            fos.write(current);
        }

        fos.close();
    }



    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

    class FancyAdapter extends ArrayAdapter<EventObj>{

        FancyAdapter(){
            super(MainActivity.this, android.R.layout.activity_list_item, evnt_list);
        }

        public View getView(final int position, View convertView,ViewGroup parent){
            ViewHolder holder;

            if(convertView==null){
                LayoutInflater inflater=getLayoutInflater();
                convertView=inflater.inflate(R.layout.event_row,null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else
            {
                holder=(ViewHolder)convertView.getTag();
            }

            // Catch on click event from present btn inside the student list view
            holder.subcat_desc.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                   EventObj obj = evnt_list.get(position);
                   load_event_by_cat(obj.getSub_cat_id());

                }
            });

            holder.save_txt.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    pref=getSharedPreferences("loginPrefs", PREFERENCE_MODE_PRIVATE);
                    int uid = pref.getInt("user_id",0);
                    EventObj obj = evnt_list.get(position);

                    if(uid!=0){
                        evdb.save_this_event(obj.getEvent_id());
                        Toast.makeText(MainActivity.this, "Saved event.", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(MainActivity.this, Facebook_login_Activity.class);
                        startActivity(intent);
                    }

                }
            });

            holder.populateFrom(evnt_list.get(position));
            return (convertView);
        }
    }

    class ViewHolder{
        public TextView save_txt=null;
        public TextView name=null;
        public TextView evnt_id=null;
        public ImageView eimg_view=null;
        public TextView venue=null;
        public TextView subcat_desc;

        ViewHolder(View row){
            save_txt = (TextView)row.findViewById(R.id.save_txt);
            venue = (TextView)row.findViewById(R.id.venue);
            subcat_desc = (TextView)row.findViewById(R.id.subcat_desc);
            name=(TextView)row.findViewById(R.id.ename);
            evnt_id= (TextView)row.findViewById(R.id.evnt_id);
            eimg_view = (ImageView)row.findViewById(R.id.eimage);

            name.setTypeface(typeface);
        }

        void populateFrom(EventObj eobj){
            name.setText(eobj.getName());
            evnt_id.setText(String.valueOf(eobj.getEvent_id()));
            venue.setText(eobj.getVenue());
            subcat_desc.setText(eobj.getSubcat_desc());
            Bitmap bitmap = BitmapFactory.decodeFile(eobj.getImage());
            eimg_view.setImageBitmap(bitmap);

//            try {
//                img_path = getFilesDir() + "/" + "logo1w.png";
//                DownloadFile("http://www.google.de/intl/en_com/images/srpr/logo1w.png", img_path);
//            } catch (IOException e) {
//                // Something went wrong here
//            }
//            //eimg_view.setImageResource(img_path);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner_category)
        {
            catObj = catList.get(position);
            cat_select_id = catObj.getCategory_id();
            sub_category = new ArrayList<>();
            subcatList = evdb.get_subcats(catObj.getCategory_id());
            for (int i=0; i<subcatList.size(); i++){
                catObj = new CategoryObj();
                catObj=subcatList.get(i);
                sub_category.add(catObj.getSubcat_desc());
            }

            subcat_adapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, sub_category);
            // Drop down layout style - list view with radio button
            subcat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            subcat_spinner.setAdapter(subcat_adapter);
        }
        else if(spinner.getId() == R.id.spinner_sub_category)
        {
            catObj = subcatList.get(position);
            subcat_select_id = catObj.getSub_category_id();
            Log.i("LOG TAG","Location Spinner");
        }
        else if(spinner.getId() == R.id.spinner_location)
        {
            Log.i("LOG TAG","Location Spinner");
            location_select_id = position;
        }

        else if(spinner.getId() == R.id.spinner_time)
        {
            time_select_id = position;
            Log.i("LOG TAG","Time Spinner");
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refresh = menu.findItem(R.id.search_menu);
        expend_btn = menu.findItem(R.id.side_menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu).getActionView();

//
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                //load_search_event(query);
                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.side_menu:
                openDrawer();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
