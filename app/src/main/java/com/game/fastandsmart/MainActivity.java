package com.game.fastandsmart;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity {
    public static MediaPlayer mpBtn;
    public static MediaPlayer mpSwitch;
    public static MediaPlayer mpBackground;

    final int REQUEST_CODE_CLASSIC_MODE = 1;
    final int REQUEST_CODE_EXPERT_MODE = 2;
    final int REQUEST_CODE_SETTINGS = 3;
    final int REQUEST_CODE_BEST_SCORE = 4;
    final String RECORDS_KEY = "records";

    Button m_ButtonClassicMode;
    Button m_ButtonExpertMode;
    Button m_ButtonSettings;
    Button m_ButtonBestScores;
    Button m_ButtonExit;
    private long backPressedTime;
    SharedPreferences mRecordsSharedPreferences;
    SharedPreferences.Editor mRecordsEditor;
    private Toast backToast;
    private boolean mIsNewActivityStart = false;
    public boolean flagExit = false;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start_page);

        mRecordsSharedPreferences = getSharedPreferences("best scores", MODE_PRIVATE);
        mRecordsEditor = mRecordsSharedPreferences.edit();

        m_ButtonBestScores = findViewById(R.id.button_BestScores);
        m_ButtonExit = findViewById(R.id.button_Exit);
        m_ButtonClassicMode = findViewById(R.id.button_NewGameClassic);
        m_ButtonExpertMode = findViewById(R.id.button_NewGameExpert);
        m_ButtonSettings = findViewById(R.id.button_Settings);

        m_ButtonClassicMode.setOnClickListener(new ClickListener());
        m_ButtonBestScores.setOnClickListener(new ClickListener());
        m_ButtonSettings.setOnClickListener(new ClickListener());
        m_ButtonExit.setOnClickListener(new ClickListener());
        m_ButtonExpertMode.setOnClickListener(new ClickListener());
        startAnimated();
        initSound();
    }

    public class ClickListener implements View.OnClickListener {
        Intent intent;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_NewGameClassic:
                    SoundHandler.playButtonClick();
                    intent = new Intent(MainActivity.this, StageActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_CLASSIC_MODE);
                    mIsNewActivityStart = true;
                    Animatoo.animateSlideLeft(MainActivity.this);
                    break;
                case R.id.button_Settings:
                    SoundHandler.playButtonClick();
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                    mIsNewActivityStart = true;
                    Animatoo.animateFade(MainActivity.this);
                    break;
                case R.id.button_BestScores:
                    SoundHandler.playButtonClick();
                    intent = new Intent(MainActivity.this, BestScoresActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_BEST_SCORE);
                    mIsNewActivityStart = true;
                    Animatoo.animateFade(MainActivity.this);
                    break;
                case R.id.button_NewGameExpert:
                    SoundHandler.playButtonClick();
                    intent = new Intent(MainActivity.this, ExpertModeGameActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_EXPERT_MODE);
                    mIsNewActivityStart = true;
                    Animatoo.animateSlideLeft(MainActivity.this);
                    break;
                case R.id.button_Exit:
                    SoundHandler.playButtonClick();
                    finishAffinity();
                    SoundHandler.stopMusic();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            SoundHandler.stopMusic();
            return;
        } else {
            backToast = Toast.makeText(this, R.string.string_pressAgain_to_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CLASSIC_MODE:
                break;
            case REQUEST_CODE_EXPERT_MODE:
                if (resultCode == RESULT_OK) {
                    int points = data.getIntExtra("points", 0);
                    String name = data.getStringExtra("name");
                    Log.i("\nBest score data:", "points:" + points + "\nname:" + name + "\n");
                    Date date = new Date();

                    String record = "" + name + ";" + Integer.toString(points) + ";" + formatter.format(date);

                    Set<String> recordsSet = new HashSet<String>(mRecordsSharedPreferences.getStringSet(RECORDS_KEY, new TreeSet<String>()));
                    recordsSet.add(record);

                    mRecordsEditor.putStringSet(RECORDS_KEY, recordsSet);
                    mRecordsEditor.apply();
                } else {//try again
                    if (data != null) {
                        Intent intent = new Intent(MainActivity.this, ExpertModeGameActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_EXPERT_MODE);
                        Animatoo.animateSlideLeft(MainActivity.this);
                    }
                }
                break;
            case REQUEST_CODE_SETTINGS:
                //TODO save settings
                break;
            case REQUEST_CODE_BEST_SCORE:
                break;
        }
    }

    public void startAnimated() {
        LinearLayout constraintLayout = findViewById(R.id.LinearLayoutStatPage);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }

    public void initSound() {
        SoundHandler.init(this, R.raw.background_music, R.raw.click_02, R.raw.correct3, R.raw.wrong);
        SoundHandler.turnMusicOn(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        SoundHandler.playMusic();
        mIsNewActivityStart = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (!mIsNewActivityStart && !this.isFinishing()) //app goes to bacground
            SoundHandler.stopMusic();
        super.onStop();
    }


}
