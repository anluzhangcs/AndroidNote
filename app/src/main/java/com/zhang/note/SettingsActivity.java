package com.zhang.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends BaseActivity {

    private Switch nightMode;
    private SharedPreferences sharedPreferences;
    private boolean nightChange;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();

        //设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //创建暂时存储数据的对象
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //获取传过来的intent
        Intent intent = getIntent();
        //首先设置黑夜模式为否
        nightMode.setChecked(sharedPreferences.getBoolean("nightMode", false));

        initListener();

    }

    /*返回时通过广播发送intent切换模式*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent intent = new Intent();
            intent.setAction("NIGHT_SWITCH");
            sendBroadcast(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void needRefresh() {

    }

    public void initListener() {
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //点击时修改全局变量nightMode的值
                setNightModePref(isChecked);
                setSelfNightMode();

            }
        });
        /*返回时通过广播发送intent切换模式*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("NIGHT_SWITCH");
                sendBroadcast(intent);
                finish();
            }
        });
    }

    private void setNightModePref(boolean night){
        //通过nightMode switch修改pref中的nightMode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("nightMode", night);
        editor.commit();
    }

    private void setSelfNightMode(){
        //重新赋值并重启本activity
        super.setNightMode();
        Intent intent = new Intent(this, SettingsActivity.class);
        //intent.putExtra("night_change", !night_change); //重启一次，正负颠倒。最终为正值时重启MainActivity。

        startActivity(intent);
        finish();
    }

    public void initView() {
        nightMode = this.findViewById(R.id.nightMode);
        toolbar = this.findViewById(R.id.my_toolbar);

    }
}