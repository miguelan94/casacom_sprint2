package com.streamnow.lindaumobile.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.DMCategory;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.DocMenuAdapter;
import com.streamnow.lindaumobile.utils.Lindau;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private String[] arrayString = null;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        }
        else {

        setContentView(R.layout.activity_main);
        Locale locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());





        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
            final TextView textView_BP = (TextView) findViewById(R.id.textView_BP);
            final TextView textView_select = (TextView) findViewById(R.id.textView_Select);
            numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        /*numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                textView.setText(arrayString[view.getValue()]);
                System.out.println("value: " + scrollState);
            }
        });*/

            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    textView_BP.setText(arrayString[picker.getValue()]);
                    textView_select.setText(R.string.select_BP);
                    textView_select.setOnClickListener(MainActivity.this);
                    textView_select.setTag(arrayString[picker.getValue()]);
                }
            });

            // NumberPicker numberPicker = new NumberPicker(this);
            arrayString = new String[]{"Limmat", "Lindau", "Lindau2", "SBB", "SNLiving", "Mia", "CS"};
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(arrayString.length - 1);
            numberPicker.setDisplayedValues(arrayString);


            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(numberPicker, new ColorDrawable(Color.rgb(221, 220, 220)));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,LoginActivity.class);
        if(v.getTag().equals("Limmat")){
            intent.putExtra("BP","Limmat");
            startActivity(intent);
        }
        else if(v.getTag().equals("Lindau")){
            //Intent i = new Intent(this, LoginActivity.class);
            //intent.putExtra("BP","Lindau");
            startActivity(intent);
        }
        else if(v.getTag().equals("Lindau2")){
            intent.putExtra("BP","Lindau2");
            startActivity(intent);
        }
        else if(v.getTag().equals("SBB")){
            intent.putExtra("BP","SBB");
            startActivity(intent);
        }
        else if(v.getTag().equals("Mia")){
            intent.putExtra("BP","Mia");
            startActivity(intent);
        }
        else if(v.getTag().equals("SNLiving")){
            intent.putExtra("BP","SNLiving");
            startActivity(intent);
        }
        else if(v.getTag().equals("CS")){
            intent.putExtra("BP","CS");
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
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
        System.out.println("URL " + LDConnection.getAbsoluteUrl("logout"));
        String session = preferences.getString("session_user","");
        System.out.println("Session user: " + session);
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
                System.out.println("Not null");
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
            else{
                System.out.println("Null");
            }
        }
    }
}
