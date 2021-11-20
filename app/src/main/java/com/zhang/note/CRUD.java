package com.zhang.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/*数据库的增删改查操作*/
public class CRUD {

    private SQLiteDatabase db; //数据库
    private SQLiteOpenHelper dbHandler; //数据库连接

    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
            NoteDatabase.TAG
    };

    public CRUD(Context context) {
        //创建数据库并让dbHandler指向该数据库
        dbHandler = new NoteDatabase(context);
    }

    /*获取写权限*/
    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    /*关闭连接*/
    public void close(){
        dbHandler.close();
    }

    //添加Note到数据库中并返回该Note
    public Note addNote(Note note){
        //创建ContentValues暂时储存数据
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.TAG, note.getTag());
        //插入数据库并返回该id
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    /*获取指定Note*/
    public Note getNote(long id){
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note e = new Note(cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        return e;
    }

    /*获取所有Note*/
    public List<Note> getAllNotes(){
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, null, null, null, null, null);

        List<Note> notes = new ArrayList<>();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getLong(0));
                note.setContent(cursor.getString(1));
                note.setTime(cursor.getString(2));
                note.setTag(cursor.getInt(3));
                notes.add(note);
            }
        }
        return notes;
    }

    /*更新Note*/
    public int updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.CONTENT, note.getContent());
        values.put(NoteDatabase.TIME, note.getTime());
        values.put(NoteDatabase.TAG, note.getTag());
        return db.update(NoteDatabase.TABLE_NAME, values,
                NoteDatabase.ID + "=?", new String[] { String.valueOf(note.getId())});
    }

    /*删除Note*/
    public void removeNote(Note note){
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }
}
