package com.game.fastandsmart;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener
{
    private Intent in;
    private Intent refresh;
    private RatingBar ratingBar;
    private boolean flagMusic = false;
    private boolean flagSound = false;
//    private int langIdx;
//    private boolean haveSound;
//    private boolean haveMusic;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        return;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            conf.setLocale(new Locale(lang.toLowerCase()));
        else
            conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
        refresh = new Intent(this, MainActivity.class);
    }

    public void setInitSwitchBtn (Switch switchSoundButton, Switch switchMusicButton) {
        if (MainActivity.haveSound) { switchSoundButton.setText(R.string.on); }
        else { switchSoundButton.setText(R.string.off); }

        if (MainActivity.haveMusic) { switchMusicButton.setText(R.string.on); }
        else { switchMusicButton.setText(R.string.off); }

        switchSoundButton.setChecked(MainActivity.haveSound);
        switchMusicButton.setChecked(MainActivity.haveMusic);
    }

    public void setInitLanguage (Spinner langSpn) {
        langSpn.setSelection(1);
//        langSpn.setSelection(MainActivity.langIdx);
    }

    public void startAnimated (RelativeLayout relativeLayout) {
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        startAnimated ((RelativeLayout)findViewById(R.id.layout_settings));


        final Switch switchSoundButton = (Switch) findViewById(R.id.soundBtn);
        final Switch switchMusicButton = (Switch) findViewById(R.id.musicBtn);

        final TextView musicText = (TextView) findViewById(R.id.musicText);
        final TextView soundText = (TextView) findViewById(R.id.musicText);

        final Button shareBtn = (Button) findViewById(R.id.shareBtn);
        final Button sendRateBtn = (Button) findViewById(R.id.rateBtn);
        final Button saveBtn = (Button) findViewById(R.id.saveBtn);
        final Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        final Spinner langSpn = (Spinner) findViewById(R.id.languageButton);

        ratingBar = (RatingBar) findViewById(R.id.rating);

        in = new Intent(getApplicationContext(), MainActivity.class);

        setInitSwitchBtn(switchSoundButton, switchMusicButton);
        switchSoundButton.setOnCheckedChangeListener(new CheckListener());
        switchMusicButton.setOnCheckedChangeListener(new CheckListener());


        if (Locale.getDefault().getLanguage().equals("he")) {
            //TODO
            // Change location of btns
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language,R.layout.spinner_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpn.setAdapter(adapter);
        langSpn.setOnItemSelectedListener(this);
        setInitLanguage(langSpn);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.haveSound) { MainActivity.mpBtn.start(); }
                //else { MainActivity.mpBtn.stop(); }
                if (flagMusic) {
                    MainActivity.haveMusic = !MainActivity.haveMusic;
                }
                if (flagSound) {
                    MainActivity.haveSound = !MainActivity.haveSound;
                }
                finish();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.haveSound) { MainActivity.mpBtn.start(); }
                //else { MainActivity.mpBtn.stop(); }
//                SharedPreferences sh = getPreferences(MODE_PRIVATE);//MainPreferences
//                SharedPreferences.Editor mEditor = sh.edit();
//                mEditor.putBoolean("haveSound", haveSound);
//                mEditor.putBoolean("haveMusic", haveMusic);
//                mEditor.apply();
//                setResult(RESULT_OK);
                finishAffinity();
                startActivity(refresh);
            }
        });

        sendRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.haveSound) { MainActivity.mpBtn.start(); }
                //else { MainActivity.mpBtn.stop(); }
                Toast.makeText(SettingsActivity.this, R.string.tnx_rating, Toast.LENGTH_LONG).show();
                //TODO send rate to server
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.haveSound) { MainActivity.mpBtn.start(); }
                //else { MainActivity.mpBtn.stop(); }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Beat Me: https://drive.google.com/drive/folders/1dfXeNDdiUJWe05hkyvX-oWcKrTabXLI4?usp=sharing";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Toast.makeText(SettingsActivity.this, rating+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        adapterView.setSelection(i);
        if (i == 0) {
            MainActivity.langIdx = i;
            setLocale("iw");
        }
        else {
            MainActivity.langIdx = i;
            setLocale("en");
        }
        //String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(SettingsActivity.this,text , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    public class CheckListener implements Switch.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.soundBtn:
                    if (MainActivity.haveSound) { MainActivity.mpSwitch.start(); }
                    //else { MainActivity.mpSwitch.stop(); }
                    if (!b) {
                        compoundButton.setText(R.string.off);
                        Toast.makeText(SettingsActivity.this, R.string.sound_off, Toast.LENGTH_LONG).show();
//                        in.putExtra("sound",false);
//                        haveSound = false;
                        MainActivity.haveSound = false;
                        flagSound = true;
                    }
                    else {
                        compoundButton.setText(R.string.on);
                        Toast.makeText(SettingsActivity.this, R.string.sound_on, Toast.LENGTH_LONG).show();
//                        in.putExtra("sAound",true);
//                        haveSound = true;
                        MainActivity.haveSound = true;
                        flagSound = true;

                    }
                    break;
                case R.id.musicBtn:
                    if (MainActivity.haveSound) { MainActivity.mpSwitch.start(); }
                    //else { MainActivity.mpSwitch.stop(); }
                    if (!b) {
                        compoundButton.setText(R.string.off);
                        Toast.makeText(SettingsActivity.this, R.string.music_off, Toast.LENGTH_LONG).show();
//                        in.putExtra("music",false);
//                        haveMusic = false;
                        MainActivity.haveMusic = false;
                        MainActivity.mpBackground.pause();
                        flagMusic = true;

                    }
                    else {
                        compoundButton.setText(R.string.on);
                        Toast.makeText(SettingsActivity.this, R.string.music_on, Toast.LENGTH_LONG).show();
//                        in.putExtra("music",true);
//                        haveMusic = true;
                        MainActivity.haveMusic = true;
                        flagMusic = true;
                    }
                    break;
            }
        }
    }
}
