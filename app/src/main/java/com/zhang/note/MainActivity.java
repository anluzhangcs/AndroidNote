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

        //??????toolbar??????actionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //??????toolbar?????????
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);

        //??????listView???Item????????????,??????MainActivity?????????????????????,?????????????????????this
        listView.setOnItemClickListener(this);


        initListener();
    }

    /*???????????????????????????*/
    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /*????????????menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //????????????
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //??????menu??????search??????????????????????????????
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        //????????????????????????????????????
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /*????????????????????????????????????????????????*/
            @Override
            public boolean onQueryTextChange(String newText) {
                //??????????????????????????????Filter?????????????????????????????????
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /*????????????????????????????????????*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this).setMessage("??????????????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db = new NoteDatabase(mContext);
                        //???????????????????????????
                        SQLiteDatabase dbHelper = db.getWritableDatabase();
                        //??????????????????,???????????????
                        dbHelper.delete("notes", null, null);
                        //?????????id?????????0
                        dbHelper.execSQL("update sqlite_sequence set seq=0 where name='notes'");
                        //??????ListView
                        refreshListView();
                    }
                }).setNegativeButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*??????????????????*/
    public void showPopUpView() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        //???????????????????????????????????????popupCover,popupCover?????????,popupWindow???????????????
        popupCover = new PopupWindow(coverView, width, height, false);
        popupWindow = new PopupWindow(customView, (int) (width*0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //??????????????????????????????????????????????????????
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
                        //?????????????????????ACTION???Intent.ACTION_VIEW???????????????????????????
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //????????????????????????
                        intent.setData(Uri.parse("https://www.baidu.com"));
                        startActivity(intent);
                    }
                });
                githubText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //?????????????????????ACTION???Intent.ACTION_VIEW???????????????????????????
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //????????????????????????
                        intent.setData(Uri.parse("https://github.com/albertzhcs/testForGit"));
                        startActivity(intent);
                    }
                });


                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //???????????????????????????,????????????????????????
                        popupWindow.dismiss();
                        return true;
                    }
                });
                //????????????????????????,???????????????,????????????????????????
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });
            }

        });
    }

    /*??????????????????*/
    public void initView() {
        btAdd = this.findViewById(R.id.bt_add);
        listView = this.findViewById(R.id.lv);
        toolbar = this.findViewById(R.id.toolbar);

        /*?????????????????????*/
        //?????????????????????
        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //????????????
        customView = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout, null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.settings_cover_layout, null);
        main = this.findViewById(R.id.main_layout);
        //???????????????
        windowManager = getWindowManager();
        //????????????
        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

    }



    /*????????????????????????*/
    public void initListener() {
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode", 4);
                startActivityForResult(intent, 0);
            }
        });
        //?????????????????????
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpView();
            }
        });
    }

    /*??????????????????*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //returnMode????????????mode???.-1?????????????????????,0????????????,1????????????
        int returnMode = -1; //?????????-1
        long id = 0; //Item???id???,?????????0
        returnMode  = data.getIntExtra("mode", -1);

        if (returnMode == 1) {  //??????
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
        } else if (returnMode == 0) {  // ?????????Note
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(mContext);
            op.open();
            op.addNote(newNote);
            op.close();
        } else if (returnMode == 2) { //??????
            Note note = new Note();
            note.setId(data.getLongExtra("id", 0));
            CRUD op = new CRUD(mContext);
            op.open();
            op.removeNote(note);
            op.close();
        } else { //???????????????

        }
        refreshListView();

    }

    /*??????ListView*/
    public void refreshListView(){

        CRUD op = new CRUD(mContext);
        op.open();
        // set adapter
        if (notes.size() > 0) notes.clear();
        notes.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    /*ListView????????????????????????*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView??????????????????ListView
        switch (adapterView.getId()) {
            case R.id.lv:
                //?????????????????????????????????,???????????????note????????????
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

    /*????????????????????????*/
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