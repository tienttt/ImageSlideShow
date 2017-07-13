package com.ygaps.mybackground;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Luu du lieu: SharedPreferences (json), File, SQLLite
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lang = sharedPref.getString(getString(R.string.Saved_MyBackground_Lang), null);
        if(lang == null){
            //doc ngon ngu tren may nguoi dung
            lang = MainActivity.CONST_LANG.EN;
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        getApplicationContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());



        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ddeee ho tro nhieu phien ban he dieu hanh (getActionBar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
//        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
