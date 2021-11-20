package com.zhang.note;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*Note适配器*/
public class NoteAdapter extends BaseAdapter implements Filterable {

    private Context mContext; //上下文变量
    private List<Note> backList;
    private List<Note> noteList;
    private MyFilter myFilter;

    public NoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
        backList = noteList;
    }

    /*返回noteList集合数量*/
    @Override
    public int getCount() {
        return noteList.size();
    }

    /*返回指定索引的Note*/
    @Override
    public Object getItem(int i) {
        return noteList.get(i);
    }

    /*获取索引*/
    @Override
    public long getItemId(int i) {
        return i;
    }

    /*ListView中的每一个Item要显示都需要Adapter调用getView方法*/
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mContext.setTheme(R.style.DayTheme);
        //渲染生成一个View
        View v = View.inflate(mContext, R.layout.note_layout, null);
        TextView tv_content = v.findViewById(R.id.tv_content);
        TextView tv_time = v.findViewById(R.id.tv_time);

        String content = noteList.get(i).getContent();
        /*if(sharedPreferences.getBoolean("noteTitle", true))
            tv_content.setText(allText.split("\n")[0]);*/
        tv_content.setText(content);
        tv_time.setText(noteList.get(i).getTime());
        v.setTag(noteList.get(i).getId());

        return v;
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    /*MyFilter内部类,用于筛选笔记*/
    class MyFilter extends Filter {

        /*用于筛选*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            List<Note> list;
            //TextUtils为工具类
            if (TextUtils.isEmpty(constraint)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Note note : backList) {
                    if (note.getContent().contains(constraint)) { //包含该文本
                        list.add(note);
                    }

                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }

        /*将返回的results交给adapter更新界面*/
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            noteList = (List<Note>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
            } else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}
