package com.streamnow.lindaumobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Miguel Angel on 22/07/2016.
 */
public class CheckLoginActivity extends BaseActivity {

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("keepSession",false) && !preferences.getString("AppId","").equalsIgnoreCase("")){
            Lindau.getInstance().appId = preferences.getString("AppId","");
            System.out.println("AppId----------->"+Lindau.getInstance().appId);
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            if(LDConnection.isSetCurrentUrl()){
                continueCheckLogin();
            }
            else{
                LDConnection.get("getURL",requestParams,new ResponseHandlerJson());
            }

           /* String session = preferences.getString("session_user","");
            if(!session.equalsIgnoreCase("")){
                LDSessionUser LDsessionUser;
                try {
                    JSONObject json = new JSONObject(session);
                    LDsessionUser = new LDSessionUser(json);
                } catch (JSONException e) {
                    LDsessionUser=null;
                    e.printStackTrace();
                }


                if( LDsessionUser != null && LDsessionUser.accessToken != null )
                {
                    Lindau.getInstance().setCurrentSessionUser(LDsessionUser);
                    Locale locale = new Locale(LDsessionUser.userInfo.language);
                    //Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }*/
        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    private class ResponseHandlerJson extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
        {
            try {
                String url = response.getString("url");
                System.out.println("URL: " + url);
                LDConnection.setCurrentUrlString(url);
                continueCheckLogin();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable)
        {
            System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
        {
            System.out.println("onFailure json");
        }
    }

    private void continueCheckLogin(){
        System.out.println("continue check login");

        String session = preferences.getString("session_user","");
        if(!session.equalsIgnoreCase("")){
            LDSessionUser LDsessionUser;
            try {
                JSONObject json = new JSONObject(session);
                LDsessionUser = new LDSessionUser(json);
            } catch (JSONException e) {
                LDsessionUser=null;
                e.printStackTrace();
            }


            if( LDsessionUser != null && LDsessionUser.accessToken != null )
            {
                Lindau.getInstance().setCurrentSessionUser(LDsessionUser);
                Locale locale = new Locale(LDsessionUser.userInfo.language);
                //Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }
}
