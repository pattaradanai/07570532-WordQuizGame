package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wordquizgame.db.MyHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private int mNumChoices;

    private ArrayList<String> mFileNameList;
    private ArrayList<String> mQuizWordList;
    private ArrayList<String> mChoiceWordList;

    private int mScore;
    private int mTotalGuesses;
    private String mAnswerFileName;

    private Random mRandom;
    private Handler mHandler;

    private TextView mQuestionNumberTextView;
    private ImageView mQuestionImageView;
    private TableLayout mButtonTableLayout;
    private TextView mAnswerTextView;

    private MyHelper mHelper;
    private SQLiteDatabase mDatabase;
    private int mDifficulty;

    private Bundle save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        save = savedInstanceState;
// <---------------------------------------------------->
        if(savedInstanceState == null){
            Log.i(TAG, "state == null");
        }
        else{
            Log.i(TAG, "state != null");
        }
//<---------------------------------------------------->
        Intent i = getIntent();
        //Shift+F6 จะแก้ชื่อตัวแปลที่ชื่อเหมือนกันทั้งหมดให้ทีเดียว
        mDifficulty = i.getIntExtra(MainActivity.KEY_DIFFICULTY,0);


        Log.i(TAG,"Difficulty " + mDifficulty);

        switch (mDifficulty){
            case 0:
                mNumChoices = 2;
                break;
            case 1:
                mNumChoices = 4;
                break;
            case 2:
                mNumChoices = 6;
                break;
        }
        Log.i(TAG, "Number of choices " + mNumChoices);


        mFileNameList = new ArrayList<>();
        mQuizWordList = new ArrayList<>();
        mChoiceWordList = new ArrayList<>();

        mRandom = new Random();
        mHandler = new Handler();

        mHelper = new MyHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        setupView();
        getImageFileName();
    }

    private void setupView() {
        mQuestionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        mQuestionImageView = (ImageView) findViewById(R.id.questionImageView);
        mButtonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
    }

    private void getImageFileName() {
        String[] categories = {"animals","body","colors","numbers","objects"};

        AssetManager assets = getAssets();

        for(String category : categories){
            try {
                String[] fileNames = assets.list(category);

                for(String f : fileNames){
                    mFileNameList.add(f.replace(".png",""));
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"Error listing file in " + category);
            }
        }
        Log.i(TAG, "***** รายชื่อไฟล์ภาพทั้งหมด *****");
        for(String f : mFileNameList){
            Log.i(TAG, f);
        }

        startQuiz();
    }

    private void startQuiz() {
        mTotalGuesses = 0;
        mScore = 0;
        mQuizWordList.clear();

        while(mQuizWordList.size() < 3){
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            String fileName = mFileNameList.get(randomIndex);

            //contains เอาไว้ใช้เช็คว่าตัวที่เราจะเอาไปใส่ในอาเรย์มีซ้ำไหม
            if(mQuizWordList.contains(fileName) == false) {
                mQuizWordList.add(fileName);
            }
        }

        Log.i(TAG, "***** ชื่อไฟล์ที่สุ่มได้สำหรับตั้งโจทย์ *****");
        for(String f : mQuizWordList){
            Log.i(TAG, f);
        }

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        mAnswerTextView.setText(null);
        mAnswerFileName = mQuizWordList.remove(0);

        if(save!=null) {
            mScore = save.getInt("mScore");
            mTotalGuesses += save.getInt("mTotleGuesses");
        }

        String msg = String.format("คำถามข้อ %d จาก %d ข้อ", mScore + 1, 3);
        mQuestionNumberTextView.setText(msg);

        loadQuestImage();
        prepareChoiceWords();
    }

    private void loadQuestImage() {
        String category;
        String filePath;

        if(save == null) {
            //substing เอาไว้ใช้ตัดเอาคำตั้งแต่ตัวไหนถึงตัวไหน
            category = mAnswerFileName.substring(0, mAnswerFileName.indexOf('-'));
            //ต้องส่งอยู่ในรูป animals/animals-mAswerfileName.png;
            filePath = category + "/" + mAnswerFileName + ".png";
        }else{
            mAnswerFileName = save.getString("mAnswerFileName");
            category = mAnswerFileName.substring(0, mAnswerFileName.indexOf('-'));
            filePath = category + "/" + mAnswerFileName + ".png";
        }

        AssetManager assets = getAssets();
        InputStream stream;
        try {
            //Ctrl+Alt+T เรียกใช้ try catch
            stream = assets.open(filePath);
            Drawable image = Drawable.createFromStream(stream, filePath);
            mQuestionImageView.setImageDrawable(image);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error opening file: " + filePath);
        }
    }

    private void prepareChoiceWords() {

        mChoiceWordList.clear();

        while(mChoiceWordList.size() < mNumChoices){
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            //getWord เอาไว้ใช้ตัดคำข้างหน้าให้เหลือแต่ชื่อสัตว์ที่อยู่หลัง -
            String randomWord = getWord(mFileNameList.get(randomIndex));
            String answerWord = getWord(mAnswerFileName);

            if(mChoiceWordList.contains(randomWord) == false && randomWord.equals(answerWord) == false) {
                mChoiceWordList.add(randomWord);
            }
        }

        int randomIndex = mRandom.nextInt(mChoiceWordList.size());
        mChoiceWordList.set(randomIndex, getWord(mAnswerFileName));

        Log.i(TAG,"***** คำศัพท์ตัวเลือกที่สุ่มได้ *****");
        for(String w : mChoiceWordList){
            Log.i(TAG, w);
        }

        createChoiceButton();
    }

    private void createChoiceButton() {
        for(int row = 0; row < mButtonTableLayout.getChildCount(); row++){
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);
            tr.removeAllViews();
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int row = 0 ; row < mNumChoices / 2 ; row++){
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);

            for(int col = 0 ; col < 2; col++){
                Button guessButton = (Button) inflater.inflate(R.layout.guess_button, tr, false);
                guessButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitGuess((Button) view);
                    }
                });
                guessButton.setText(mChoiceWordList.remove(0));

                tr.addView(guessButton);
            }
        }

    }

    private void submitGuess(Button guessButton) {
        String guessWord = guessButton.getText().toString();
        String answerWord = getWord(mAnswerFileName);

        mTotalGuesses++;

        //ตอบถูก
        if(guessWord.equals(answerWord)){

            MediaPlayer mp = MediaPlayer.create(this, R.raw.applause);
            mp.start();

            mScore++;

            String msg = guessWord + " ถูกต้องนะครับ";
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            disableAllButton();

            //ตอบถูกครบ 3 ข้อจบเกม
            if(mScore == 3){
                saveScore();

                String msgResult = String.format(
                        "จำนวนครั้งที่ทาย: %d\nเปอร์เซ็นต์ความถูกต้อง: %.1f",
                        mTotalGuesses,
                        (100*3)/(double) mTotalGuesses);

                new AlertDialog.Builder(this)
                        .setTitle("สรุปผล")
                        .setMessage(msgResult)
                        .setCancelable(false)
                        .setPositiveButton("เริ่มเกมใหม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startQuiz();
                            }
                        })
                        .setNegativeButton("กลับหน้าหลัก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
            //ตอบถูกยังไม่ครบ 3 ข้อ
            else{
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextQuestion();
                    }
                }, 2000);
                if(save!=null){
                    save = null;
                }
            }
        }
        //ตอบผิด
        else{
            MediaPlayer mp = MediaPlayer.create(this, R.raw.fail3);
            mp.start();

            String msg = "ผิดครับ ลองใหม่นะครับ";
            mAnswerTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            guessButton.setEnabled(false);
        }
    }

    private void saveScore() {

        ContentValues cv = new ContentValues();
        cv.put(MyHelper.COL_SCORE, 100*3/(double) mTotalGuesses);
        cv.put(MyHelper.COL_DIFFICULTY, mDifficulty);

        mDatabase.insert(MyHelper.TABAL_NAME, null, cv);
    }

    private void disableAllButton() {
        for(int row = 0 ; row < mButtonTableLayout.getChildCount() ; row++){
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);

            for(int col = 0 ; col < tr.getChildCount() ; col++){
                tr.getChildAt(col).setEnabled(false);
            }
        }
    }

    private String getWord(String fileName) {
        String word = fileName.substring(fileName.indexOf('-')+1);
        return word;
    }
//<---------------------------------------------------->
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putString("mAnswerFileName", mAnswerFileName);
        outState.putStringArrayList("mChoiceWordList", mChoiceWordList);
        outState.putInt("mScore", mScore);
        outState.putInt("mTotleGuesses", mTotalGuesses);

    }
}//<---------------------------------------------------->
