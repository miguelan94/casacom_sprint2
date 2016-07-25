package com.streamnow.lindaumobile.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDService;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.interfaces.IMenuPrintable;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.utils.MenuAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.Preferences;

import cz.msebera.android.httpclient.Header;

public class MenuActivity extends BaseActivity
{
    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        categoryId = this.getIntent().getStringExtra("category_id");
        ArrayList<? extends IMenuPrintable> adapterArray;

        if( categoryId == null )
        {
            adapterArray = sessionUser.getAvailableServicesForSession();
        }
        else
        {
            adapterArray = sessionUser.getAvailableServicesForCategoryId(categoryId);
        }


        /*Locale locale = new Locale(sessionUser.userInfo.language);
        //Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
*/





        RelativeLayout mainBackground = (RelativeLayout) findViewById(R.id.main_menu_background);
        //mainBackground.setBackgroundColor(sessionUser.userInfo.partner.colorTop);
        mainBackground.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);


        TextView textView = (TextView)findViewById(R.id.text_app_name);
        if(sessionUser.userInfo.partner.smartphoneAppName!=null && sessionUser.userInfo.partner.smartphoneAppName.isEmpty()){
            textView.setText(sessionUser.userInfo.partner.name);
        }
        else{
            textView.setText(sessionUser.userInfo.partner.smartphoneAppName);
        }




        RelativeLayout bgnd_image = (RelativeLayout)findViewById(R.id.bgnd_image);
        ImageView smart_image = (ImageView)findViewById(R.id.smartphone_image);
        ImageView left_arrow = (ImageView)findViewById(R.id.left_arrow);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View dividerTop = findViewById(R.id.divider);
        ImageView imageView = (ImageView)findViewById(R.id.settings_ico); //settings
        if(!getIntent().getBooleanExtra("sub_menu",false)){
            //smart_image.setImageResource(sessionUser.userInfo.partner.backgroundSmartphoneImage);

            System.out.println("Image: " + sessionUser.userInfo.partner.backgroundSmartphoneImage);
            dividerTop.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(sessionUser.userInfo.partner.backgroundSmartphoneImage)
                    .into(smart_image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MenuActivity.this,SettingsActivity.class);
                    i.putExtra("main_menu",true);
                    startActivity(i);
                }
            });
        }
        else{
            dividerTop.setBackgroundColor(sessionUser.userInfo.partner.lineColorSmartphone);
            dividerTop.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            smart_image.setVisibility(View.GONE);
            //bgnd_image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            left_arrow.setVisibility(View.VISIBLE);
            //bgnd_image.setVisibility(View.GONE);
            mainBackground.setPadding(0,(int)getResources().getDimension(R.dimen.activity_vertical_margin),0,0);
        }

        final ListView listView = (ListView) findViewById(R.id.main_menu_list_view);
        listView.setDivider(new ColorDrawable(sessionUser.userInfo.partner.lineColorSmartphone));
        listView.setDividerHeight(1);
        listView.setAdapter(new MenuAdapter(this, adapterArray));



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });
    }

    private void menuItemClicked(int position)
    {
        ArrayList<? extends IMenuPrintable> services;

        if( getIntent().getBooleanExtra("sub_menu", false) ) //si true
        {

            services = sessionUser.getAvailableServicesForCategoryId(categoryId);
            final LDService service = (LDService) services.get(position);
            System.out.println("service clicked-----" + service.name + "id " + service.id + " category " + service.categoryId);
            if (service.type.equals("2"))
            {
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", service.apiUrl);
                intent.putExtra("service_id", service.id);
                if(categoryId.equals("5")){

                    final String pass = getIntent().getStringExtra("user_vodka");
                    final String user = getIntent().getStringExtra("pass_vodka");
                    //call API vodka
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("appId","5033d287e70e42f0a5a9f44001cb2d");
                    requestParams.add("userId",getIntent().getStringExtra("user_vodka"));
                    requestParams.add("password",getIntent().getStringExtra("pass_vodka"));
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    httpClient.setUserAgent("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
                    httpClient.setEnableRedirects(true);
                   /* KeyStore trustStore = null;
                    MySSLSocketFactory socketFactory = null;
                    try {
                        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);
                        socketFactory = new MySSLSocketFactory(trustStore);
                        socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    httpClient.setSSLSocketFactory(socketFactory);
                    */

                    httpClient.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    httpClient.post("https://project-test.streamnow.ch/external/client/core/Login.do?",requestParams,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            try{
                                intent.putExtra("token",response.getString("token") );
                                startActivity(intent);
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            System.out.println("onFailure json" + errorResponse.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            System.out.println("onFailure array");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                            System.out.println("get token KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                        }
                    });
                }else{
                    startActivity(intent);
                }

            }
            else if (service.type.equals("3"))
            {
                // TODO Open youtube video here
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                startActivity(intent);
            }
            else if(service.type.equals("1")){
                if(service.id.equals("22")){//events
                    Intent i = new Intent(this,EventActivity.class);
                    startActivity(i);

                }
                else if( service.id.equals("53") || service.id.equals("20") )
                {
                    Intent intent = new Intent(this, ContactActivity.class);
                    startActivity(intent);
                }
                else if(service.id.equals("3"))
                {
                    Intent intent = new Intent(this, DocmanMenuActivity.class);
                    intent.putExtra("root_menu", true);
                    intent.putExtras( new Bundle());
                    startActivity(intent);
                }
            }
        }
        else
        {
            services = sessionUser.getAvailableServicesForCategoryId(sessionUser.categories.get(position).id);

             System.out.println("clicked on item with title " + sessionUser.categories.get(position).name + " it has " + services.size() + " services available");

            if (services.size() == 1)
            {

                LDService service = (LDService) services.get(0);
                System.out.println("size 1, id: " + service.id + "type: " + service.type + "name: " + service.name + "url api: " + service.apiUrl);
                    //check service type
                if (service.type.equals("1"))
                {
                    if( service.id.equals("53") )
                    {
                        Intent intent = new Intent(this, ContactActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(this, DocmanMenuActivity.class);
                        intent.putExtra("root_menu", true);
                        intent.putExtras( new Bundle());
                        startActivity(intent);
                    }
                }
                else if (service.type.equals("2"))
                {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", service.apiUrl);
                    startActivity(intent);
                }
                else if (service.type.equals("3"))
                {
                    // TODO Open youtube video here
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("api_url", "https://m.youtube.com/watch?v=" + service.apiUrl);
                    startActivity(intent);
                }
            }
            else if (services.size() > 1)
            {
                final Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra("category_id", sessionUser.categories.get(position).id);
                intent.putExtra("sub_menu", true);
                if(sessionUser.categories.get(position).id.equals("5")){//entertainment
                    System.out.println("access token: " + sessionUser.accessToken);
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1){ //API 21-22
                        showAlertDialog(getResources().getString(R.string.no_entertainment_avaliable));
                    }
                    else{
                        final RequestParams requestParams = new RequestParams("access_token",sessionUser.accessToken);
                        LDConnection.get("myentertainment/getCredentials",requestParams,new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                            {
                                System.out.println("response:----" + response.toString());
                                try{
                                    if(response.getJSONObject("status").getString("status").equals("ok")){
                                        intent.putExtra("user_vodka",response.getJSONObject("status").getJSONObject("credentials").getString("username"));
                                        intent.putExtra("pass_vodka",response.getJSONObject("status").getJSONObject("credentials").getString("password"));
                                    }
                                    else{
                                        System.out.println("response is not ok");
                                    }
                                    startActivity(intent);

                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                System.out.println("onFailure json" +errorResponse.toString());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                System.out.println("onFailure array");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                System.out.println("getCredentials KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                            }
                        });
                    }


                }
                else{
                    startActivity(intent);
                }

            }
        }
    }
    private void showAlertDialog(String msg){
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        finish();

    }
}
