package com.eunovate.eunovatedev.pwelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Facebook_login_Activity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FacebookCallback callback;
    private String fb_user_id;
    private JSONObject udta_jobj;
    public  static final String MyPREFERENCES="MyPrefs";
    private static final int PREFERENCE_MODE_PRIVATE=0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
//

       // AppEventsLogger.activateApp(this);
       callbackManager = CallbackManager.Factory.create();
        info = (TextView) findViewById(R.id.info);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                fb_user_id = loginResult.getAccessToken().getUserId();

                Log.i("LOG TAG","Login result :" +loginResult);

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                try {
                                    Log.i("LOG TAG","U INFO : "+object );
                                    Log.i("LOG TAG","U INFO : "+response.getRawResponse());

                                    udta_jobj = new JSONObject();
                                    udta_jobj.put("name",object.getString("name"));
                                    udta_jobj.put("gender",object.getString("gender"));
                                    udta_jobj.put("link",object.getString("link"));
                                    udta_jobj.put("locale",object.getString("locale"));
                                    udta_jobj.put("timezone",object.getString("timezone"));
                                    udta_jobj.put("fb_uid",object.getString("id"));
                                    new check_user().execute();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //getGroups();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,locale,timezone,link");
                request.setParameters(parameters);
                request.executeAsync();

                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
            }

            @Override
            public void onCancel() {
                Toast.makeText(Facebook_login_Activity.this, "Login cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Facebook_login_Activity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class check_user extends AsyncTask<String,String,String> {

        //private ProgressDialog progressDialog=ProgressDialog.show(MainActivity.this,"","Loading..");
        protected void onPreExecute(){}
        @Override
        protected String doInBackground(String... arg0){
            try {
                //int last_sync_count=atd_db.get_sync_schedule_count();
                String link = MainActivity.BASE_URL + "check_user";
                //String data= URLEncoder.encode("user_id", "UTF-8")+" = "+ URLEncoder.encode(String.valueOf(user_id),"UTF-8");
                /// TEMP CODE //////////////
                String data = udta_jobj.toString();
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
            if(!result.equals("false")) {
                try {

                    JSONObject jobj = new JSONObject(result);
                    pref=getSharedPreferences("loginPrefs",PREFERENCE_MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putInt("user_id", jobj.getInt("uid"));
                    editor.commit();

                }catch (Exception e){}

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
