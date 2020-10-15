package com.game.fastandsmart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity
{
    private RatingBar ratingBar;
    Switch switchSoundButton;
    Switch switchMusicButton;
    Button shareBtn;
    Button sendRateBtn;
    Button saveBtn;
    Button cancelBtn;
    private boolean mIsNewActivityStart = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        SoundHandler.playMusic();

        startAnimated ((RelativeLayout)findViewById(R.id.layout_settings));

        switchSoundButton = findViewById(R.id.soundSwitch);
        switchMusicButton = findViewById(R.id.musicSwitch);

        shareBtn = findViewById(R.id.shareBtn);
        sendRateBtn = findViewById(R.id.rateBtn);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        ratingBar = findViewById(R.id.rating);

        initSwitchBtn(switchSoundButton, switchMusicButton);
        switchSoundButton.setOnCheckedChangeListener(new CheckListener());
        switchMusicButton.setOnCheckedChangeListener(new CheckListener());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHandler.playButtonClick();
                finish();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHandler.playButtonClick();
                finish();
            }
        });

        sendRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHandler.playButtonClick();
                Toast.makeText(SettingsActivity.this, R.string.tnx_rating, Toast.LENGTH_SHORT).show();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHandler.playButtonClick();

//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                String shareBody = "Beat Me: https://drive.google.com/drive/folders/1dfXeNDdiUJWe05hkyvX-oWcKrTabXLI4?usp=sharing";
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Toast.makeText(SettingsActivity.this, rating+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initSwitchBtn(Switch switchSoundButton, Switch switchMusicButton) {
        if (SoundHandler.getSoundOn()) {
            switchSoundButton.setText(R.string.on);
        }
        else {
            switchSoundButton.setText(R.string.off);
        }

        if (SoundHandler.getMusicOn()) {
            switchMusicButton.setText(R.string.on);
        }
        else {
            switchMusicButton.setText(R.string.off);
        }

        switchSoundButton.setChecked(SoundHandler.getSoundOn());
        switchMusicButton.setChecked(SoundHandler.getMusicOn());
    }

    public class CheckListener implements Switch.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
            switch (compoundButton.getId()) {
                case R.id.soundSwitch:
                    SoundHandler.playButtonClick();
                    if (!flag) {
                        compoundButton.setText(R.string.off);
                        Toast.makeText(SettingsActivity.this, R.string.sound_off, Toast.LENGTH_SHORT).show();
                        SoundHandler.turnSoundsOn(false);
                    }
                    else {
                        compoundButton.setText(R.string.on);
                        Toast.makeText(SettingsActivity.this, R.string.sound_on, Toast.LENGTH_SHORT).show();
                        SoundHandler.turnSoundsOn(true);
                    }
                    break;

                case R.id.musicSwitch:
                    SoundHandler.playButtonClick();
                    if (!flag) {
                        compoundButton.setText(R.string.off);
                        Toast.makeText(SettingsActivity.this, R.string.music_off, Toast.LENGTH_SHORT).show();
                        SoundHandler.turnMusicOn(false);

                    }
                    else {
                        compoundButton.setText(R.string.on);
                        Toast.makeText(SettingsActivity.this, R.string.music_on, Toast.LENGTH_SHORT).show();
                        SoundHandler.turnMusicOn(true);
                    }
                    break;
            }
        }
    }

    public void startAnimated (RelativeLayout relativeLayout) {
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    @Override
    protected void onResume() {
        SoundHandler.playMusic();
        mIsNewActivityStart = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (!mIsNewActivityStart && !this.isFinishing())
            SoundHandler.stopMusic();
        super.onStop();
    }
}
