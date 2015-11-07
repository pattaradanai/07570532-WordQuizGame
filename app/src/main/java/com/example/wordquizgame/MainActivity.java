package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public  static final String KEY_DIFFICULTY = "diff";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playgamebt = (Button) findViewById(R.id.playGameButton);
        playgamebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 new AlertDialog.Builder(MainActivity.this)
                         .setTitle("Choose Difficulty Level")
                         .setItems(new String[] {"Easy","Medium","Hard"},new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int which) {
                              Intent i = new Intent(MainActivity.this,GameActivity.class);
                              i.putExtra(KEY_DIFFICULTY,which);
                              startActivity(i);
                        }
                    }).show();
             }
        });

        Button highScoreButton = (Button) findViewById(R.id.highScoreButton);
        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity. this,HighScoreActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart" );
    }

}
