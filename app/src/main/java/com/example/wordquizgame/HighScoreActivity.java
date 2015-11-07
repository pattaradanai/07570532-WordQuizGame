package com.example.wordquizgame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;

import com.example.wordquizgame.db.MyHelper;

public class HighScoreActivity extends AppCompatActivity {

    private static  final String TAG = "HighScoreActivity";

    private MyHelper mHelper;
    private SQLiteDatabase mDatabase;
    private ListView list;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        list = (ListView) findViewById(R.id.highScoreListView);

        mHelper = new MyHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.high_score_row,
                null,
                new String[] {MyHelper.COL_SCORE},
                new int[] {R.id.scoreTextView});

        list.setAdapter(mAdapter);

        RadioGroup difficultyRadioGroup = (RadioGroup) findViewById(R.id.difficultyRadioGroup);
        difficultyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkId) {
                switch (checkId){
                    case R.id.easyRadioButton:
                        showHighScoreByDifficulty(0);
                        break;

                    case R.id.mediumRadioButton:
                        showHighScoreByDifficulty(1);
                        break;

                    case R.id.hardRadioButton:
                        showHighScoreByDifficulty(2);
                        break;
                }
            }
        });


    }

    private void showHighScoreByDifficulty(int diff) {
        //ชื่อข้อมูลแต่ละคอลัมที่จะเอามา new String[] {MyHelper.COL_ID, MyHelper.COL_SCORE, MyHelper.COL_DIFFICULTY}
        Cursor c = mDatabase.query(MyHelper.TABAL_NAME, //ชื่อตาราง
                null,
                MyHelper.COL_DIFFICULTY + "=" + diff,
                null,
                null,
                null,
                MyHelper.COL_SCORE + " DESC", // เรียงคะแนนจากน้อยไปมาก
                null);

        mAdapter.changeCursor(c);
    }
}
