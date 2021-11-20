package com.zhang.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class BaseActivity extends AppCompatActivity {

    public final String ACTION = "NIGHT_SWITCH";
    protected BroadcastReceiver receiver; //广播接收器
    protected IntentFilter filter; //意图过滤器

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //所有继承该类的类对象都设置初始时模式
        setNightMode();

        filter = new IntentFilter();
        filter.addAction(ACTION); //设置意图的action,用于过滤

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                needRefresh();
            }
        };
        //注册广播接收器
        registerReceiver(receiver, filter);
    }

    public boolean isNightMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return sharedPreferences.getBoolean("nightMode", false);
    }
    public void setNightMode(){
        if(isNightMode()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            this.setTheme(R.style.NightTheme);
        }
        else setTheme(R.style.DayTheme);
    }

    /*当继承该抽象类的类对象销毁时,对广播接收器进行取消注册*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /*刷新函数*/
    protected abstract void needRefresh();
}