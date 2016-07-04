package com.eunovate.eunovatedev.pwelapp;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by EunovateDev on 5/25/2016.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    public static final String SENT_TOKEN_TO_SERVER = "922502154229";
    public static final String GCM_TOKEN = "gcmToken";
    private static final String TAG = "RegIntentService";
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        Log.i("LOG TAG","Start Regristration Intent Service!");
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Fetch token here
        try {
            // save token
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.i("LOG TAG","GOOGLE TOKEN : " +token);
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();
            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        // send network request

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        String link = "http://10.0.2.2/eunovate/ams/service/index.php/GCM_ctrl/register";
        URLConnection conn = null;
        try {
            JSONObject jobj = new JSONObject();
            JSONArray jarr = new JSONArray();
            JSONObject msg= new JSONObject();
            msg.put("title","GCM");
            msg.put("body","GCM Notification စမ္းသပ္ျခင္း");
            jarr.put(token);
            jobj.put("reg_id",jarr);
            jobj.put("noti_msg",msg);
            String data =  jobj.toString();
            URL url=new URL(link);
            conn=url.openConnection();
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

            Log.e("LOG TAG","RESULT"+sb.toString());
        }
        catch (Exception e){
           Log.e("LOG TAG","ERROR "+e);
        }
        finally {
            if (conn != null) {

            }
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
    }


}
