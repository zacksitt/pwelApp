package com.eunovate.eunovatedev.pwelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Locale;

public class Setting_Activity extends AppCompatActivity {
    public static final String MyPREFERENCES="MyPrefs";
    private Locale  myLocale;
    private SharedPreferences sharedPreferences;
    private RadioButton mm_radio,en_radio;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent refresh = new Intent(Setting_Activity.this, MainActivity.class);
                startActivity(refresh);
            }
        });

        mm_radio = (RadioButton) findViewById(R.id.mm_radio);
        en_radio = (RadioButton) findViewById(R.id.en_radio);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String local = sharedPreferences.getString("locale",null);

        if(local!=null){
            if(local.equals("mm")) {
                mm_radio.setChecked(true);
                setLocale("mm");
            }else {
                en_radio.setChecked(true);
                setLocale("end");
            }
        }else{

            editor.putString("locale","end");
            editor.commit();
            setLocale("end");
        }
    }

    public void local_radio_checked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        // This check which radio button was clicked
        switch (view.getId()) {
            case R.id.mm_radio:
                if (checked) {
                    //Do something when radio button is clicked
                    setLocale("mm");
                    editor.putString("locale", "mm");
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Changed Language to Myanmar", Toast.LENGTH_SHORT).show();
                }
                    break;

            case R.id.en_radio:
                //Do something when radio button is clicked
                setLocale("end");
                editor.putString("locale", "end");
                editor.commit();
                Toast.makeText(getApplicationContext(), "Change Language to English", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public void setLocale(String lang) {

       myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
}
