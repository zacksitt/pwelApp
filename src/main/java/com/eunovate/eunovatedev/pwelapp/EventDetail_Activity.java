package com.eunovate.eunovatedev.pwelapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eunovate.eunovatedev.pwelapp.dao_class.Database_helper;
import com.eunovate.eunovatedev.pwelapp.dao_class.Event_DbHelper;
import com.eunovate.eunovatedev.pwelapp.object.EventObj;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.Locale;

public class EventDetail_Activity extends AppCompatActivity {

    private int param_evnt_id,select_subcat_id;
    private EventObj eobj;
    private Event_DbHelper edb_helper;
    private Database_helper db_helper;
    private TextView name_tv,organizer_tv,contact_tv,detail_tv,venue_tv,detail_lbl,map_lbl,organizer_lbl,add_lbl,subcat_desc_tv;
    private ImageView evnt_img;
    private Typeface typeface;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private LatLng event_point;
    static final LatLng TutorialsPoint = new LatLng(16.80528 , 96.15611);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
              onBackPressed();
            }
        });


//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        typeface= Typeface.createFromAsset(getAssets(), "fonts/ZawgyiOne2008.ttf");
        detail_lbl = (TextView) findViewById(R.id.des_label);
        map_lbl = (TextView) findViewById(R.id.map_label);
        organizer_lbl = (TextView) findViewById(R.id.organizer_lbl);
        add_lbl = (TextView) findViewById(R.id.add_lable);

        detail_lbl.setTypeface(typeface);
        map_lbl.setTypeface(typeface);
        organizer_lbl.setTypeface(typeface);
        add_lbl.setTypeface(typeface);

        name_tv = (TextView) findViewById(R.id.evnt_title);
        organizer_tv = (TextView) findViewById(R.id.evnt_orgnaziner);
        venue_tv = (TextView) findViewById(R.id.evnt_venue);
        contact_tv = (TextView) findViewById(R.id.evnt_contact);
        detail_tv = (TextView) findViewById(R.id.evnt_des);
        evnt_img = (ImageView) findViewById(R.id.evnt_img);
        subcat_desc_tv=(TextView) findViewById(R.id.subcat_desc);

        name_tv.setTypeface(typeface);

        Bundle extras=getIntent().getExtras();
        param_evnt_id=extras.getInt("param");
        db_helper = new Database_helper(this);
        edb_helper = new Event_DbHelper(this);
        Cursor ecursor= edb_helper.get_evnt_detail(param_evnt_id);
        //eobj = edb_helper.get_evnt_detail(param_evnt_id);
        ecursor.moveToFirst();

        String ename_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_NAME));
        String organizer_txt = ecursor.getString(ecursor.getColumnIndex("oname"));
        String venue_txt = ecursor.getString(ecursor.getColumnIndex("vname"));
        String contact_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_CONTACT));
        String edetail_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_DESC));
        String eimg_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_IMG));
        String subcat_desc_txt = ecursor.getString(ecursor.getColumnIndex("subcat_desc"));
        select_subcat_id = ecursor.getInt(ecursor.getColumnIndex(db_helper.SUBCAT_COLUMN_ID));
        String latitude_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_LATITUDE));
        String longtude_txt = ecursor.getString(ecursor.getColumnIndex(db_helper.EVNT_COLUMN_LONGTITUDE));

        subcat_desc_tv.setText(subcat_desc_txt.toString());
        subcat_desc_tv.setFocusable(false);
        subcat_desc_tv.setClickable(true);

        name_tv.setText(ename_txt.toString());
        name_tv.setFocusable(false);
        name_tv.setClickable(true);

        organizer_tv.setText(organizer_txt.toString());
        organizer_tv.setFocusable(false);
        organizer_tv.setClickable(true);

        contact_tv.setText(contact_txt.toString());
        contact_tv.setFocusable(false);
        contact_tv.setClickable(true);

        venue_tv.setText(venue_txt.toString());
        venue_tv.setFocusable(false);
        venue_tv.setClickable(true);

        detail_tv.setText(edetail_txt.toString());
        detail_tv.setFocusable(false);
        detail_tv.setClickable(true);

        Bitmap bitmap = BitmapFactory.decodeFile(eimg_txt);
        evnt_img.setImageBitmap(bitmap);
        evnt_img.setFocusable(false);
        evnt_img.setClickable(true);

        event_point = new LatLng(Double.parseDouble(latitude_txt),Double.parseDouble(longtude_txt));
        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Marker TP = mMap.addMarker(new MarkerOptions().
                    position(event_point).title(venue_txt.toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(event_point));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }

    public void get_event_by_cat(View v){
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("param",select_subcat_id);
        Intent intent = new Intent(EventDetail_Activity.this, MainActivity.class);
        intent.putExtras(dataBundle);
        startActivity(intent);
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        mMap = map;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
}
