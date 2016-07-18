package com.streamnow.lindaumobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.lib.LDConnection;
import com.streamnow.lindaumobile.utils.Lindau;
import com.streamnow.lindaumobile.utils.SettingsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends Activity {

    protected final LDSessionUser sessionUser = Lindau.getInstance().getCurrentSessionUser();
    protected ArrayList<String> items;
    private ProgressDialog progressDialog;
    private static final int PICK_CONTACT_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RelativeLayout settings_menu = (RelativeLayout)findViewById(R.id.settings_menu_background);
        settings_menu.setBackgroundColor(sessionUser.userInfo.partner.backgroundColorSmartphone);

        if(getIntent().getBooleanExtra("main_menu",true)){
            this.items = new ArrayList<>();
            String [] list = {getResources().getString(R.string.profile),getResources().getString(R.string.contacts),getResources().getString(R.string.logout),getResources().getString(R.string.shopping)};
            items.addAll(Arrays.asList(list));
        }

        ListView listView = (ListView)findViewById(R.id.settings_menu_list_view);
        listView.setAdapter(new SettingsAdapter(this,items));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                menuItemClicked(position);
            }
        });
    }

    private void menuItemClicked(int position){
        if(position==0){ //profile clicked
            Intent i = new Intent(this,ProfileActivity.class);
            startActivity(i);

        }else if(position==1){//contacts

            Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent,PICK_CONTACT_REQUEST);
        }else if(position==2){//logout

            RequestParams requestParams = new RequestParams();
            LDConnection.get("logout", requestParams, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                {
                    try{
                        if(response.getString("msg").equals("Logout OK")){
                            Intent i = new Intent(SettingsActivity.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);


                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    System.out.println("login onFailure throwable: " + throwable.toString() + " status code = " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    System.out.println("login onFailure json");
                }
            });

        }else if(position==3){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);

        }//shopping
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Uri contactData = data.getData();
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(contactData, null, null, null, null);
                    int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                    int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    cursor.moveToFirst();
                    do {
                        String idContact = cursor.getString(contactIdIdx);
                        String name = cursor.getString(nameIdx);
                        String phoneNumber = cursor.getString(phoneNumberIdx);
                    } while (cursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }
}
