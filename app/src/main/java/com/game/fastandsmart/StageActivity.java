package com.game.fastandsmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.ArrayList;

public class StageActivity extends AppCompatActivity {
    /*constants for sharedPreferences saves*/
    final String STAGES = "stages";
    final String AVAILABLE_STAGE = "available stage";

    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    private ArrayList<StageSlide> mStagesSlides;
    private SliderAdapter mSliderAdapter;
    private Button mStartButton;
    private int mCurrentPage = 1;
    private int mMaxAvailableStage;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean mIsNewActivityStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_stages);
        SoundHandler.playMusic();

        mViewPager=findViewById(R.id.pager_view);
        mDotsLayout = findViewById(R.id.dots_layout);
        mStartButton = findViewById(R.id.stage_start_button);

        mSharedPreferences = getSharedPreferences(STAGES,MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mMaxAvailableStage = mSharedPreferences.getInt(AVAILABLE_STAGE,1);

        /*
        Create stages slides
         */
        mStagesSlides = new ArrayList<>();
        loadStages(mMaxAvailableStage);
        mSliderAdapter = new SliderAdapter(this,mStagesSlides);
        mViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);

        mViewPager.addOnPageChangeListener(viewListener);

        /*
        Start Levels activity
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundHandler.playButtonClick();
                Intent intent = new Intent(StageActivity.this,LevelsActivity.class);
                intent.putExtra("stage",mCurrentPage);
                startActivityForResult(intent,1);
                mIsNewActivityStart = true;
                Animatoo.animateSlideLeft(StageActivity.this);
            }
        });
    }

    /*
    Slide object for PageViewer adapter
     */
    public class StageSlide {
        private String mName;
        private String mDescription;
        private int mImageId;

        public StageSlide(String name,String desc,int id){
            mName = name;
            mDescription = desc;
            mImageId = id;
        }

        public String getmDescription() {
            return mDescription;
        }

        public int getImageId() {
            return mImageId;
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }
    }

    private void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotsLayout.removeAllViews();
        for(int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.GRAY);
            mDotsLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(Color.WHITE);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
             if(position >= mMaxAvailableStage )
                 mStartButton.setVisibility(View.INVISIBLE);
             else
                 mStartButton.setVisibility(View.VISIBLE);
             addDotsIndicator(position);
             mCurrentPage = position + 1;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        Back from Levels Activity.
        If reached number of levels to next stage make available next stage
         */
        if(resultCode == RESULT_CANCELED){
              int stage = mSharedPreferences.getInt(AVAILABLE_STAGE,1);
              if(stage>mMaxAvailableStage){
                  openStage(stage);
                  mMaxAvailableStage = stage;
              }
        }
        /*
        Back from Level Activity,pressed nex stage
        Open next stage levels activity.
         */
        else if(resultCode == RESULT_OK){
            int stage = data.getIntExtra("stage",1);//next stage
            openStage(stage);
            mMaxAvailableStage = stage;
            Intent intent = new Intent(StageActivity.this,LevelsActivity.class);
            intent.putExtra("stage",stage);
            startActivityForResult(intent,1);
            mIsNewActivityStart = true;
            Animatoo.animateSlideLeft(StageActivity.this);
        }
    }

    /*Make next stage available*/
    private void openStage(int stage) {
        mStagesSlides=new ArrayList<>();
        loadStages(stage);
        mSliderAdapter=new SliderAdapter(this,mStagesSlides);
        mViewPager.setAdapter(mSliderAdapter);
        mSliderAdapter.notifyDataSetChanged();
        mEditor.putInt(AVAILABLE_STAGE,stage);
        mEditor.apply();
    }

    /*Update page with available stages*/
    private void loadStages(int maxAvailableStage){
        switch (maxAvailableStage){
            case 1:
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_basicStage),(String)getResources().getText(R.string.basic_stage_description),R.drawable.kid));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_middle_stage),(String)getResources().getText(R.string.middle_stage_description),R.drawable.black_lock));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_hard_stage),(String)getResources().getText(R.string.high_stage_description),R.drawable.black_lock));
                break;
            case 2:
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_basicStage),(String)getResources().getText(R.string.basic_stage_description),R.drawable.kid));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_middle_stage),(String)getResources().getText(R.string.middle_stage_description),R.drawable.middle_boy));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_hard_stage),(String)getResources().getText(R.string.high_stage_description),R.drawable.black_lock));
                break;
            case 3:
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_basicStage),(String)getResources().getText(R.string.basic_stage_description),R.drawable.kid));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_middle_stage),(String)getResources().getText(R.string.middle_stage_description),R.drawable.middle_boy));
                mStagesSlides.add(new StageSlide((String)getResources().getText(R.string.string_hard_stage),(String)getResources().getText(R.string.high_stage_description),R.drawable.smart_girl));
                break;
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
