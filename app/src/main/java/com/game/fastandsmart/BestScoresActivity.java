package com.game.fastandsmart;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Set;

public class BestScoresActivity extends Activity {
    final String RECORDS_PREFERENCES = "best scores";
    final String RECORDS_KEY = "records";
    ListView mListView;
    Button bckBtn;
    ArrayList<DataList> mListOfRecords;
    private boolean mIsNewActivityStart = false;

    public void startAnimated(RelativeLayout relativeLayout) {
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scores);
        SoundHandler.playMusic();

        startAnimated((RelativeLayout) findViewById(R.id.layout_scores));

        bckBtn = findViewById(R.id.button_levels_back);
        mListView = findViewById(R.id.list_view);
        mListOfRecords = loadRecords();
        //TODO sort
        ScoresAdapter adapter = new ScoresAdapter(this, mListOfRecords);
        mListView.setAdapter(adapter);

        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundHandler.playButtonClick();
                finish();
            }
        });
    }

    private ArrayList<DataList> loadRecords() {
        ArrayList<DataList> dataArray = new ArrayList<>();
        SharedPreferences recordsInMemory = getSharedPreferences(RECORDS_PREFERENCES, MODE_PRIVATE);

        Set<String> recordSet = recordsInMemory.getStringSet(RECORDS_KEY, null);
        if (recordSet == null) {
            System.out.println("There is no records");
            //
        } else {
            String[] temp;
            String name;
            String points;
            String date;
            for (String singleRecord : recordSet) {
                temp = singleRecord.split(";");
                name = temp[0];
                points = temp[1];
                date = temp[2];
                dataArray.add(new DataList(name, Integer.parseInt(points), date));
            }
        }

        return dataArray;
    }

    public class DataList {
        public String name;
        public int score;
        public String date;

        public DataList(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }

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
