package com.streamnow.lindaumobile.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends BaseActivity implements View.OnClickListener
{
    private final int LOGIN_BUTTON_TAG = 21;
    private final int RESET_BUTTON_TAG = 22;
    private ProgressDialog progressDialog;

    private Button loginButton;
    private Button resetButton;
    private EditText userEditText;
    private EditText passwdEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
         //Locale defaultLocale  = new Locale(Locale.getDefault().getDisplayLanguage());
       // Configuration config = new Configuration();
       // config.
        //config.locale = defaultLocale;
       // getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());



            //Locale defaultLocale = new Locale( getResources().getConfiguration().locale.getDisplayLanguage());
            //Configuration config = new Configuration();
            //config.locale = defaultLocale;
            //getBaseContext().getApplicationContext().getResources().updateConfiguration(config,null);


        Locale locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);
        LinearLayout background = (LinearLayout)findViewById(R.id.login_background);
        ImageView main_logo = (ImageView)findViewById(R.id.main_logo);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        resetButton = (Button)findViewById(R.id.resetButton);

        if(getIntent().getStringExtra("BP")!=null){
            if(getIntent().getStringExtra("BP").equals("Limmat")){
                Lindau.getInstance().appId = "com.streamnow.limmatmobile";
                System.out.println("Limmat");
                main_logo.setImageResource(R.drawable.limmat_logo);
                loginButton.setBackgroundColor(Color.rgb(215,10,23));
                resetButton.setBackgroundColor(Color.rgb(215,10,23));
                Lindau.getInstance().appDemoAccount = "demo.limmat";
            }
            else if(getIntent().getStringExtra("BP").equals("SBB")){
                Lindau.getInstance().appId = "com.streamnow.sbbmobile";
                //background.setBackgroundColor(Color.rgb(255,255,255));
                main_logo.setImageResource(R.drawable.sbb_logo);
                main_logo.setAdjustViewBounds(true);
                loginButton.setBackgroundColor(Color.rgb(197,1,44));
                resetButton.setBackgroundColor(Color.rgb(197,1,44));
                Lindau.getInstance().appDemoAccount = "demo.sbb";
            }
            else if(getIntent().getStringExtra("BP").equals("SNLiving")){
                Lindau.getInstance().appId = "com.streamnow.lsmobile";
                main_logo.setImageResource(R.drawable.snliving_logo);
                loginButton.setBackgroundColor(Color.rgb(0,0,0));
                resetButton.setBackgroundColor(Color.rgb(0,0,0));
                Lindau.getInstance().appDemoAccount = "demo.snliving";
            }
            else if(getIntent().getStringExtra("BP").equals("Mia")){
                Lindau.getInstance().appId = "com.streamnow.miamobile";
                background.setBackgroundColor(Color.rgb(255,255,255));
                main_logo.setImageResource(R.drawable.mia_logo);
                loginButton.setBackgroundColor(Color.rgb(197,1,44));
                resetButton.setBackgroundColor(Color.rgb(197,1,44));
                Lindau.getInstance().appDemoAccount = "demo.mia";
            }
            else if(getIntent().getStringExtra("BP").equals("CS")){
                Lindau.getInstance().appId = "com.streamnow.csmobile";
                System.out.println("CS");
                background.setBackgroundColor(Color.rgb(255,255,255));
                main_logo.setImageResource(R.drawable.credit_suisse_logo);
                loginButton.setBackgroundColor(Color.rgb(0,50,83));
                resetButton.setBackgroundColor(Color.rgb(0,50,83));
                Lindau.getInstance().appDemoAccount = "democs";
            }
        }else{
            Lindau.getInstance().appId ="com.streamnow.lindaumobile";
            Lindau.getInstance().appDemoAccount = "demo.lindau";
        }



        userEditText = (EditText) this.findViewById(R.id.userEditText);
        passwdEditText = (EditText) this.findViewById(R.id.passwdEditText);

        loginButton.setOnClickListener(this);
        loginButton.setTag(LOGIN_BUTTON_TAG);
        resetButton.setOnClickListener(this);
        resetButton.setTag(RESET_BUTTON_TAG);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if( userEditText.getText().toString().isEmpty()  && passwdEditText.getText().toString().isEmpty() )
                {
                    loginButton.setText(R.string.login_button_title1);
                }
                else
                {
                    loginButton.setText(R.string.login_button_title2);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };

        userEditText.addTextChangedListener(textWatcher);
        passwdEditText.addTextChangedListener(textWatcher);

        userEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    passwdEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        passwdEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if( (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) )
                {
                    loginButtonClicked(null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        if( (int)v.getTag() == LOGIN_BUTTON_TAG )
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo!=null && activeNetworkInfo.isConnected()){
                this.loginButtonClicked(v);
            }
            else{
                showAlertDialog(getResources().getString(R.string.network_error));
            }
        }
        if((int)v.getTag() == RESET_BUTTON_TAG){
            resetButtonClicked(v);
        }
    }

    public void loginButtonClicked(View sender)
    {
        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait), true);

        //if( LDConnection.isSetCurrentUrl() )
        //{
            //continueLogin();
        //}
        //else
        //{
            LDConnection.setCurrentUrlString(null);
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            System.out.println("App ID: " + Lindau.getInstance().appId);
            LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    try
                    {
                        String url = response.getString("url");
                        LDConnection.setCurrentUrlString(url);

                        System.out.println("Response.url = '" + url + "'");
                        continueLogin();
                    }
                    catch (JSONException e)
                    {
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
                    System.out.println("getURL KO: " + throwable.toString() + " status code = " + statusCode + " responseString = " + response);
                }
            });
        //}
    }

    public void resetButtonClicked(View v){
        if(LDConnection.isSetCurrentUrl()){
            showAlertDialogReset(R.string.no_mail_reset);
        }
        else{
            RequestParams requestParams = new RequestParams("app", Lindau.getInstance().appId);
            LDConnection.get("getURL", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    try
                    {
                        String url = response.getString("url");
                        LDConnection.setCurrentUrlString(url);
                        showAlertDialogReset(R.string.no_mail_reset);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("onFailure json");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    System.out.println("onFailure array");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                }
            });
        }
    }



    private void continueLogin()
    {
        String username, password;

        if( userEditText.getText().toString().isEmpty()  && passwdEditText.getText().toString().isEmpty() )
        {
            username = Lindau.getInstance().appDemoAccount;
            password = Lindau.getInstance().appDemoAccount;
        }
        else
        {
            username = userEditText.getText().toString();
            password = passwdEditText.getText().toString();
        }

        RequestParams requestParams = new RequestParams();
        requestParams.add("email", username);
        requestParams.add("password", password);
        requestParams.add("source", "Mobile");

        LDConnection.post("auth/login", requestParams, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {

                LDSessionUser sessionUser;

                try
                {
                    sessionUser = new LDSessionUser(response);
                }
                catch(Exception e)
                {
                    sessionUser = null;
                    e.printStackTrace();
                }

                progressDialog.dismiss();

                if( sessionUser != null && sessionUser.accessToken != null )
                {
                    Lindau.getInstance().setCurrentSessionUser(sessionUser);
                    Locale locale = new Locale(sessionUser.userInfo.language);
                    //Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getApplicationContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    showAlertDialog(getString(R.string.login_error));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("login onFailure json");
                progressDialog.dismiss();
            }
        });
    }

    private void showAlertDialog(String msg){
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    private void showAlertDialogReset(int msg)
    {
        final EditText inputEmail = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setView(inputEmail)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {


                        if(inputEmail!=null && !inputEmail.getText().toString().equals("")){
                            String email = inputEmail.getText().toString();
                            RequestParams requestParams = new RequestParams("email", email);
                            LDConnection.post("auth/reset", requestParams, new JsonHttpResponseHandler()
                            {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                                {
                                    System.out.println("Response: " + response.toString());
                                    try{
                                        if(response.get("msg").equals("Ok")){
                                            Toast.makeText(LoginActivity.this,R.string.password_reseted,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (JSONException e){

                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                    System.out.println("onFailure throwable: " + throwable.toString() + " status code = " + statusCode);

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    System.out.println(" onFailure json" + errorResponse.toString());

                                }
                            });
                        }
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }
}

/*
Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
setSupportActionBar(toolbar);

FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
fab.setOnClickListener(new View.OnClickListener()
{
    @Override
    public void onClick(View view)
    {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
});
*/