package com.zhang.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private FloatingActionButton btAdd;
    private TextView tvItem;
    private ListView listView;

    private Context mContext = this;
    private List<Note> notes = new ArrayList<>();
    private NoteAdapter adapter;
    private NoteDatabase db;

    private Toolbar toolbar;

    private LayoutInflater layoutInflater;
    private ViewGroup customView;
    private ViewGroup coverView;
    private RelativeLayout main;
    private WindowManager windowManager;
    private DisplayMetrics metrics;
    private PopupWindow popupCover;
    private PopupWindow popupWindow;
    private TextView settingText;
    private ImageView settingImage;
    private ImageView tipImage;
    private TextView tipText;
    private ImageView githubImage;
    private TextView githubText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


        adapter = new NoteAdapter(getApplicationContext(), notes);
        refreshListView();
        listView.setAdapter(adapter);

        //设置toolbar取代actionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置toolbar的图标
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        //设置listView的Item点击事件,因为MainActivity已经实现该方法,所以可以直接用this
        listView.setOnItemClickListener(this);


        initListener();
    }

    /*切换模式时进行刷新*/
    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /*引入菜单menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //渲染菜单
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //获取menu中的search项并获取它的搜索视图
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        //设置搜索视图的文本监听器
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /*当该搜索输入框文本发生改变时触发*/
            @Override
            public boolean onQueryTextChange(String newText) {
                //获取适配器中自定义的Filter并根据该文本过滤笔记项
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /*设置菜单选中项的点击事件*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this).setMessage("您确定要删除所有清单吗?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db = new NoteDatabase(mContext);
                        //获取数据库的写权限
                        SQLiteDatabase dbHelper = db.getWritableDatabase();
                        //不加过滤条件,删除所有项
                        dbHelper.delete("notes", null, null);
                        //对表的id进行清0
                        dbHelper.execSQL("update sqlite_sequence set seq=0 where name='notes'");
                        //刷新ListView
                        refreshListView();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*显示弹出菜单*/
    public void showPopUpView() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        //创建弹出菜单以及用于分割的popupCover,popupCover占满屏,popupWindow占左侧部分
        popupCover = new PopupWindow(coverView, width, height, false);
        popupWindow = new PopupWindow(customView, (int) (width*0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //弹出菜单在主页面加载完成之后才会出现
        main.post(new Runnable() {
            @Override
            public void run() {
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);

                settingImage = customView.findViewById(R.id.setting_settings_image);
                settingText = customView.findViewById(R.id.setting_settings_text);
                tipImage = customView.findViewById(R.id.privacy_tip);
                tipText = customView.findViewById(R.id.privacy_tip_text);
                githubImage = customView.findViewById(R.id.github_image);
                githubText = customView.findViewById(R.id.github_image_text);

                settingImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                });

                settingText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                });
                tipImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                    }
                });
                tipText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                    }
                });
                githubImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //构造函数中指定ACTION是Intent.ACTION_VIEW，即用于打开浏览器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //指定要打开的网址
                        intent.setData(Uri.parse("https://www.baidu.com"));
                        startActivity(intent);
                    }
                });
                githubText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //构造函数中指定ACTION是Intent.ACTION_VIEW，即用于打开浏览器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //指定要打开的网址
                        intent.setData(Uri.parse("https://github.com/albertzhcs/testForGit"));
                        startActivity(intent);
                    }
                });


                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //即当点击阴影部分时,弹出菜单就会消失
                        popupWindow.dismiss();
                        return true;
                    }
                });
                //当弹出菜单消失时,会自动触发,让阴影部分也消失
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });
            }

        });
    }

    /*获取所有控件*/
    public void initView() {
        btAdd = this.findViewById(R.id.bt_add);
        listView = this.findViewById(R.id.lv);
        toolbar = this.findViewById(R.id.toolbar);

        /*初始化弹出菜单*/
        //获取布局渲染器
        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //渲染视图
        customView = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout, null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.settings_cover_layout, null);
        main = this.findViewById(R.id.main_layout);
        //窗口管理器
        windowManager = getWindowManager();
        //展现矩阵
        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

    }



    /*设置控件的监听器*/
    public void initListener() {
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode", 4);
                startActivityForResult(intent, 0);
            }
        });
        //点击时弹出菜单
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpView();
            }
        });
    }

    /*处理回传数据*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //returnMode为传回的mode值.-1表示什么都不干,0表示创建,1表示更新
        int returnMode = -1; //默认为-1
        long id = 0; //Item的id值,默认为0
        returnMode  = data.getIntExtra("mode", -1);

        if (returnMode == 1) {  //更新
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            id = data.getLongExtra("id", 0);
            Note newNote = new Note(content, time, tag);
            newNote.setId(id);
            CRUD op = new CRUD(mContext);
            op.open();
            op.updateNote(newNote);
            op.close();
        } else if (returnMode == 0) {  // 创建新Note
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(mContext);
            op.open();
            op.addNote(newNote);
            op.close();
        } else if (returnMode == 2) { //删除
            Note note = new Note();
            note.setId(data.getLongExtra("id", 0));
            CRUD op = new CRUD(mContext);
            op.open();
            op.removeNote(note);
            op.close();
        } else { //什么也不干

        }
        refreshListView();

    }

    /*刷新ListView*/
    public void refreshListView(){

        CRUD op = new CRUD(mContext);
        op.open();
        // set adapter
        if (notes.size() > 0) notes.clear();
        notes.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    /*ListView每一项的点击事件*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView来确定是哪个ListView
        switch (adapterView.getId()) {
            case R.id.lv:
                //点击之后跳转到编辑页面,并传入旧的note各个属性
                Note curNote = (Note) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3);     // MODE of 'click to edit'
                intent.putExtra("tag", curNote.getTag());
                startActivityForResult(intent, 1);      //collect data from edit
                Log.d(TAG, "onItemClick: " + i);
                break;
        }
    }

    /*当该页面刷新时也*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        if (popupCover != null && popupCover.isShowing()) {
            popupCover.dismiss();
            popupCover = null;
        }
    }
}