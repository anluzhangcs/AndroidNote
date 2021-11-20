package com.zhang.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EditActivity extends BaseActivity {

    private EditText etItem;
    private Toolbar toolbar;

    //note的各个属性
    private String oldContent = "";
    private long id = 0;
    private String oldTime = "";
    private int oldTag = 1;
    //openMode为打开是传入的mode值,3表示更新,4表示创建
    private int openMode = 0;
    private int tag = 1; //tag是当前笔的默认tag
    private boolean tagChange = false; //判断tag是否改变的信号量

    Intent intent = new Intent(); //要传回去的intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();

        //设置toolbar取代actionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //为toolbar图标设置点击事件,返回MainActivity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoSetMessage();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //获取传入的intent,并将数据回显到编辑框
        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode", 0);
        if (openMode == 3) { //表示这是从已有的Item打开
            id = getIntent.getLongExtra("id", 0);
            oldContent = getIntent.getStringExtra("content");
            oldTime = getIntent.getStringExtra("time");
            oldTag = getIntent.getIntExtra("tag",1);
            etItem.setText(oldContent);
            etItem.setSelection(oldContent.length());
        }
    }

    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    /*引入菜单项*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*为菜单栏的选中项设置点击事件*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                //提示框模板
                new AlertDialog.Builder(EditActivity.this).setMessage("您确定要删除吗")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (openMode == 4) { //如果删除创建的,则什么都不做
                                    intent.putExtra("mode", -1);
                                    setResult(RESULT_OK, intent);
                                } else if (openMode == 3) { //如果要修改,则传入id值回去,通过id删除数据库
                                    intent.putExtra("mode", 2); //2代表删除
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //如果为否,则忽略掉
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        etItem = this.findViewById(R.id.et_item);
        toolbar = this.findViewById(R.id.toolbar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            autoSetMessage();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void autoSetMessage(){
        if(openMode == 4){ //创建Item
            if(etItem.getText().toString().length() == 0){ //如果无内容,返回mode-1
                intent.putExtra("mode", -1);
            }
            else{
                intent.putExtra("mode", 0); // 创建新Item,返回mode 0
                intent.putExtra("content", etItem.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        }
        else if (openMode == 3){ //更新Item
            if (etItem.getText().toString().equals(oldContent) && !tagChange)
                intent.putExtra("mode", -1); //没有更新,返回mode -1
            else {
                intent.putExtra("mode", 1); //更新,返回mode 1
                intent.putExtra("content", etItem.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }


    /*将当前时间转换为字符串*/
    public String dateToStr(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}