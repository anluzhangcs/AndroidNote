package com.zhang.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class PrivacyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
    }

    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, PrivacyActivity.class);
        startActivity(intent);
    }
}