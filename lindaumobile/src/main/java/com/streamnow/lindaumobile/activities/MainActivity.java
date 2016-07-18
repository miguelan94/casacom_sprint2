package com.streamnow.lindaumobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.streamnow.lindaumobile.R;

import java.lang.reflect.Field;

public class MainActivity extends Activity implements View.OnClickListener{

    private String[] arrayString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NumberPicker numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
        final TextView textView_BP = (TextView)findViewById(R.id.textView_BP);
        final TextView textView_select = (TextView)findViewById(R.id.textView_Select);
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
        arrayString = new String[]{"Limmat","Lindau","SBB","SNLiving","Mia","CS"};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(arrayString.length-1);
        numberPicker.setDisplayedValues(arrayString);



        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(numberPicker, new ColorDrawable(Color.rgb(221,220,220)));
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
}
