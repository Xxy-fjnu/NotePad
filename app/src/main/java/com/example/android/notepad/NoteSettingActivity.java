package com.example.android.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;


public class NoteSetting extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_setting);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.app_notes);
            supportActionBar.setTitle(R.string.note_setting);
        }

        Switch sw = (Switch) findViewById(R.id.sw);
        if (sw != null) {
            sw.setChecked(NightUtils.isNight);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("set", "b = " + b);
                    if (b) {
                        // 設置為全局
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    // 改变所有activity主题
                    NightUtils.isNight = b;
                    // 可能會閃爍，不断重启
//                    recreate();

                    // 重启
                    Intent intent = new Intent(NoteSetting.this, NoteSetting.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        // 重啟回到首頁，否則已經打開的Activity不生效
        Intent intent = new Intent(NoteSetting.this, NotesListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // 如果需要保存某些值，在这里保存
    }
}
