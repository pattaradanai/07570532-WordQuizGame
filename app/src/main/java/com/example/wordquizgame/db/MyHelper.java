package com.example.wordquizgame.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 25/10/2558.
 */
public class MyHelper extends SQLiteOpenHelper {

    private static  final String DATABASE_NAME = "wordquizgame.db";
    private static  final int DATABASE_VERSION = 1;

    public MyHelper(Context context) {
        //super เรียกไปคลาสแม่
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Ctrl+i เรียกเมดธอด oncreate onupgrade
    public static final String TABAL_NAME = "scores";
    public static final String COL_ID = "_id";
    public static final String COL_SCORE = "score";
    public static final String COL_DIFFICULTY = "difficulty";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateTable = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "%s REAL,"
                + "%s INTEGER)";

        sqlCreateTable = String.format(sqlCreateTable, TABAL_NAME, COL_ID,COL_SCORE, COL_DIFFICULTY);

        db.execSQL(sqlCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oleVersion, int newVersion) {
        return;
    }
}
