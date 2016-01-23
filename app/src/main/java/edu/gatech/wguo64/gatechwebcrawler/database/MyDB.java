package edu.gatech.wguo64.gatechwebcrawler.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Comment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweidong on 1/23/16.
 */
public class MyDB {

    // Database fields
    private SQLiteDatabase database;
    private MyDBHelper dbHelper;
    private String[] allColumns = { MyDBHelper.COLUMN_ID,
            MyDBHelper.COLUMN_URL };

    public MyDB(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void clear() throws SQLException {
        String sql = "delete from Record;";
        database.execSQL(sql);
    }

    public boolean checkExists(String url) throws SQLException {

        Cursor cursor = database.query(MyDBHelper.TABLE_NAME,
                new String[] {MyDBHelper.COLUMN_ID}, MyDBHelper.COLUMN_URL + " = '" + url + "'", null,
                null, null, null);
        return cursor.moveToFirst();
    }

    public long insertURL(String url) throws SQLException {

        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_URL, url);
        long insertId = database.insert(MyDBHelper.TABLE_NAME, null,
                values);
        return insertId;
    }
//    public Comment createComment(String comment) {
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
//        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
//                values);
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
//                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        Comment newComment = cursorToComment(cursor);
//        cursor.close();
//        return newComment;
//    }
}

