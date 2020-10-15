package com.game.fastandsmart;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class LevelsActivity extends AppCompatActivity {
    /*constants for sharedPreferences saves*/
    final String STAGES = "stages";
    final String AVAILABLE_STAGE = "available stage";
    final String CURRENT_LEVEL = "current level";

    final int FIRST_GAME = 1;
    final int RESTART = 2;
    final int LEVEL_COMPLETED = 0;
    final int LEVELS_TO_NEXT_STAGE = 6;
    final int RESULT_TRY_AGAIN = 4;
    final int MAX_LEVEL = 12;

    int mStage;
    int mLevelsCompleted = 0;
    boolean mIsNextStageOpenned = false;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Dialog mDialogStageComlete;
    TextView toNextStagetextView;
    TextView mStageTitle;
    Button buttonLevel_1;
    Button buttonLevel_2;
    Button buttonLevel_3;
    Button buttonLevel_4;
    Button buttonLevel_5;
    Button buttonLevel_6;
    Button buttonLevel_7;
    Button buttonLevel_8;
    Button buttonLevel_9;
    Button buttonLevel_10;
    Button buttonLevel_11;
    Button buttonLevel_12;
    Button mBackButton;
    private boolean mIsNewActivityStart = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels_layout);
        SoundHandler.playMusic();

        mStage = (int) getIntent().getIntExtra("stage", 0);

        if (mStage == 0) {
            finish();
        }

        mDialogStageComlete = new Dialog(this, R.style.DialogStageComlete);
        mDialogStageComlete.setContentView(R.layout.dialog_stage_completed);
        mDialogStageComlete.getWindow().setBackgroundDrawableResource(R.drawable.dialog_end_game);
        mDialogStageComlete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogStageComlete.setCancelable(false);
        mDialogStageComlete.findViewById(R.id.dialog_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogStageComlete.dismiss();
            }
        });
        mDialogStageComlete.findViewById(R.id.dialog_button_next_stage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogStageComlete.dismiss();
                Intent intent = new Intent(LevelsActivity.this, StageActivity.class);
                intent.putExtra("stage", mStage + 1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mSharedPreferences = getSharedPreferences("Stage" + mStage + " Preferences", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        buttonLevel_1 = findViewById(R.id.button_level_1);
        buttonLevel_2 = findViewById(R.id.button_level_2);
        buttonLevel_3 = findViewById(R.id.button_level_3);
        buttonLevel_4 = findViewById(R.id.button_level_4);
        buttonLevel_5 = findViewById(R.id.button_level_5);
        buttonLevel_6 = findViewById(R.id.button_level_6);
        buttonLevel_7 = findViewById(R.id.button_level_7);
        buttonLevel_8 = findViewById(R.id.button_level_8);
        buttonLevel_9 = findViewById(R.id.button_level_9);
        buttonLevel_10 = findViewById(R.id.button_level_10);
        buttonLevel_11 = findViewById(R.id.button_level_11);
        buttonLevel_12 = findViewById(R.id.button_level_12);
        buttonLevel_1.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_2.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_3.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_4.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_5.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_6.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_7.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_8.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_9.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_10.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_11.setOnClickListener(new LevelButtonCliclListener());
        buttonLevel_12.setOnClickListener(new LevelButtonCliclListener());

        mBackButton = findViewById(R.id.button_levels_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundHandler.playButtonClick();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        toNextStagetextView = findViewById(R.id.to_next_stage_textView);
        mStageTitle = findViewById(R.id.stage_title);

        loadStageData();
        /*
        Label for show amount of levels for open next stage initial.If reached set flag true
         */
        if(mStage == 1)
            mStageTitle.setText(getResources().getText(R.string.string_basicStage));
        else if(mStage == 2)
            mStageTitle.setText(getResources().getText(R.string.string_middle_stage));
        else if(mStage == 3)
            mStageTitle.setText(getResources().getText(R.string.string_hard_stage));

        if (mLevelsCompleted >= LEVELS_TO_NEXT_STAGE) {
            toNextStagetextView.setText(getResources().getText(R.string.next_stage_available));
            mIsNextStageOpenned = true;
        } else {
            toNextStagetextView.setText(getResources().getText(R.string.left_to_next_stage) + " " + Integer.toString(LEVELS_TO_NEXT_STAGE - mLevelsCompleted));
        }
    }

    /*
    Start Game activity for selected level with flag "First time" for show tutorial if its first stage.Else if stage not first pass "tutorial passed".
    Only first 3 levels in stage 1 have tutorial.
     */
    public class LevelButtonCliclListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SoundHandler.playButtonClick();
            Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
            intent.putExtra("stage", mStage);

            switch (v.getId()) {
                case R.id.button_level_1:
                    intent.putExtra("level", 1);
                    if (mStage == 1)
                        intent.putExtra("tutorial passed", false);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_2:
                    intent.putExtra("level", 2);
                    if (mStage == 1)
                        intent.putExtra("tutorial passed", false);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_3:
                    intent.putExtra("level", 3);
                    if (mStage == 1)
                        intent.putExtra("tutorial passed", false);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_4:
                    intent.putExtra("level", 4);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_5:
                    intent.putExtra("level", 5);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_6:
                    intent.putExtra("level", 6);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_7:
                    intent.putExtra("level", 7);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_8:
                    intent.putExtra("level", 8);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_9:
                    intent.putExtra("level", 9);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_10:
                    intent.putExtra("level", 10);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_11:
                    intent.putExtra("level", 11);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                case R.id.button_level_12:
                    intent.putExtra("level", 12);
                    startActivityForResult(intent, FIRST_GAME);
                    break;
                default:
                    break;
            }
            mIsNewActivityStart=true;
            Animatoo.animateCard(LevelsActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        Level completed and pressed next level.
        If MAX LEVEL (12) not reached start next level activity else show dialog stage completed.
         */
        if (resultCode == RESULT_OK) {
            int level = data.getIntExtra("level", 0);
            if (level != 0) {
                saveLevelCompleted(level);
                if (level < MAX_LEVEL) {
                    openNextLevel(level + 1);
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("level", level + 1);
                    intent.putExtra("stage", mStage);
                    startActivityForResult(intent, FIRST_GAME);
                    mIsNewActivityStart=true;
                } else
                    stageCompleted();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
         /*
         Try again was pressed.
         Start same level activity without tutorial.
        */
        } else if (resultCode == RESULT_TRY_AGAIN) {
            if (data != null) {
                int level = data.getIntExtra("level", 0);
                if (level != 0) {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("level", level);
                    intent.putExtra("stage", mStage);
                    intent.putExtra("tutorial passed", true);
                    startActivityForResult(intent, RESTART);
                    mIsNewActivityStart=true;
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else {
//                setResult(RESULT_CANCELED);
//                finish();
            }
        }
        /*
        Cancell or back button was pressed.
        If have level extra so level was completed - update levels data and open next level.
         */
        else if (resultCode == RESULT_CANCELED) { //Canceled
            if (data != null) { //level passed but press cancel
                int level = data.getIntExtra("level", 0);
                if (level != 0) {
                    saveLevelCompleted(level);
                    if (level < 12)
                        openNextLevel(level + 1);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            } else {
//                setResult(RESULT_CANCELED);
//                finish();
            }

        }
    }

    /*
    Save data about completed level,and update view.
     */
    private void saveLevelCompleted(int level) {
        switch (level) {
            case 1:
                if (Integer.parseInt(buttonLevel_1.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_1.setTag(LEVEL_COMPLETED);
                    buttonLevel_1.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level1 completed", true);
                }
                break;
            case 2:
                if (Integer.parseInt(buttonLevel_2.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_2.setTag(LEVEL_COMPLETED);
                    buttonLevel_2.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level2 completed", true);
                }
                break;
            case 3:
                if (Integer.parseInt(buttonLevel_3.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_3.setTag(LEVEL_COMPLETED);
                    buttonLevel_3.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level3 completed", true);
                }
                break;
            case 4:
                if (Integer.parseInt(buttonLevel_4.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_4.setTag(LEVEL_COMPLETED);
                    buttonLevel_4.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level4 completed", true);
                }
                break;
            case 5:
                if (Integer.parseInt(buttonLevel_5.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_5.setTag(LEVEL_COMPLETED);
                    buttonLevel_5.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level5 completed", true);
                }
                break;
            case 6:
                if (Integer.parseInt(buttonLevel_6.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_6.setTag(LEVEL_COMPLETED);
                    buttonLevel_6.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level6 completed", true);
                }
                break;
            case 7:
                if (Integer.parseInt(buttonLevel_7.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_7.setTag(LEVEL_COMPLETED);
                    buttonLevel_7.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level7 completed", true);
                }
                break;
            case 8:
                if (Integer.parseInt(buttonLevel_8.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_8.setTag(LEVEL_COMPLETED);
                    buttonLevel_8.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level8 completed", true);
                }
                break;
            case 9:
                if (Integer.parseInt(buttonLevel_9.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_9.setTag(LEVEL_COMPLETED);
                    buttonLevel_9.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level9 completed", true);
                }
                break;
            case 10:
                if (Integer.parseInt(buttonLevel_10.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_10.setTag(LEVEL_COMPLETED);
                    buttonLevel_10.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level10 completed", true);
                }
                break;
            case 11:
                if (Integer.parseInt(buttonLevel_11.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_11.setTag(LEVEL_COMPLETED);
                    buttonLevel_11.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level11 completed", true);
                }
                break;
            case 12:
                if (Integer.parseInt(buttonLevel_12.getTag().toString()) != LEVEL_COMPLETED) {
                    buttonLevel_12.setTag(LEVEL_COMPLETED);
                    buttonLevel_12.setBackgroundResource(R.drawable.level_button_completed);
                    ++mLevelsCompleted;
                    mEditor.putBoolean("level12 completed", true);
                }
                break;
        }

        mEditor.apply();

        /*
        if reached number of levels to next stage,make next stage available and save data.
        If reached maximal level show dialog stage completed
         */
        if (!mIsNextStageOpenned) {
            if (mLevelsCompleted >= LEVELS_TO_NEXT_STAGE) {
                toNextStagetextView.setText(getResources().getText(R.string.next_stage_available));
                nextStageAvailable();
                mIsNextStageOpenned = true;
            } else {
                toNextStagetextView.setText(getResources().getText(R.string.left_to_next_stage) + " " + Integer.toString(LEVELS_TO_NEXT_STAGE - mLevelsCompleted));
            }
        }

        if (mLevelsCompleted == MAX_LEVEL) {
            stageCompleted();
        }

    }

    /*
    Load levels data nad update View.
     */
    private void loadStageData() {
        int current_level = mSharedPreferences.getInt("current level", 1);
        if (mSharedPreferences.getBoolean("level1 completed", false)) {
            buttonLevel_1.setTag(LEVEL_COMPLETED);
            buttonLevel_1.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_1.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_1.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 1) {
            buttonLevel_1.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_1.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_1.setClickable(true);
        } else {
            buttonLevel_1.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_1.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_1.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level2 completed", false)) {
            buttonLevel_2.setTag(LEVEL_COMPLETED);
            buttonLevel_2.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_2.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_2.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 2) {
            buttonLevel_2.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_2.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_2.setClickable(true);
        } else {
            buttonLevel_2.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_2.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_2.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level3 completed", false)) {
            buttonLevel_3.setTag(LEVEL_COMPLETED);
            buttonLevel_3.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_3.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_3.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 3) {
            buttonLevel_3.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_3.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_3.setClickable(true);
        } else {
            buttonLevel_3.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_3.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_3.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level4 completed", false)) {
            buttonLevel_4.setTag(LEVEL_COMPLETED);
            buttonLevel_4.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_4.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_4.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 4) {
            buttonLevel_4.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_4.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_4.setClickable(true);
        } else {
            buttonLevel_4.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_4.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_4.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level5 completed", false)) {
            buttonLevel_5.setTag(LEVEL_COMPLETED);
            buttonLevel_5.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_5.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_5.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 5) {
            buttonLevel_5.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_5.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_5.setClickable(true);
        } else {
            buttonLevel_5.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_5.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_5.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level6 completed", false)) {
            buttonLevel_6.setTag(LEVEL_COMPLETED);
            buttonLevel_6.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_6.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_6.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 6) {
            buttonLevel_6.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_6.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_6.setClickable(true);
        } else {
            buttonLevel_6.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_6.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_6.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level7 completed", false)) {
            buttonLevel_7.setTag(LEVEL_COMPLETED);
            buttonLevel_7.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_7.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_7.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 7) {
            buttonLevel_7.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_7.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_7.setClickable(true);
        } else {
            buttonLevel_7.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_7.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_7.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level8 completed", false)) {
            buttonLevel_8.setTag(LEVEL_COMPLETED);
            buttonLevel_8.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_8.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_8.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 8) {
            buttonLevel_8.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_8.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_8.setClickable(true);
        } else {
            buttonLevel_8.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_8.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_8.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level9 completed", false)) {
            buttonLevel_9.setTag(LEVEL_COMPLETED);
            buttonLevel_9.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_9.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_9.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 9) {
            buttonLevel_9.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_9.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_9.setClickable(true);
        } else {
            buttonLevel_9.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_9.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_9.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level10 completed", false)) {
            buttonLevel_10.setTag(LEVEL_COMPLETED);
            buttonLevel_10.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_10.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_10.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 10) {
            buttonLevel_10.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_10.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_10.setClickable(true);
        } else {
            buttonLevel_10.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_10.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_10.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level11 completed", false)) {
            buttonLevel_11.setTag(LEVEL_COMPLETED);
            buttonLevel_11.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_11.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_11.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 11) {
            buttonLevel_11.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_11.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_11.setClickable(true);
        } else {
            buttonLevel_11.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_11.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_11.setClickable(false);
        }

        if (mSharedPreferences.getBoolean("level112 completed", false)) {
            buttonLevel_12.setTag(LEVEL_COMPLETED);
            buttonLevel_12.setBackgroundResource(R.drawable.level_button_completed);
            buttonLevel_12.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_12.setClickable(true);
            ++mLevelsCompleted;
        } else if (current_level == 12) {
            buttonLevel_12.setBackgroundResource(R.drawable.level_button_unlocked);
            buttonLevel_12.setTextColor(getResources().getColor(R.color.text_black));
            buttonLevel_12.setClickable(true);
        } else {
            buttonLevel_12.setBackgroundResource(R.drawable.level_button_locked);
            buttonLevel_12.setTextColor(getResources().getColor(R.color.text_white));
            buttonLevel_12.setClickable(false);
        }

    }

    /*
    If stage completed save data and show dialog
     */
    private void stageCompleted() {
        SharedPreferences sh = getSharedPreferences(STAGES, MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sh.edit();

        if (sh.getInt(AVAILABLE_STAGE, 1) == mStage && mStage < 3) {
            SharedPreferences.Editor editor = sh.edit();
            editor.putInt(AVAILABLE_STAGE, mStage + 1);
            editor.apply();
        }

        ((TextView) mDialogStageComlete.findViewById(R.id.dialog_message_text)).setText(getResources().getText(R.string.stage_completed));
        mDialogStageComlete.show();
    }

    /*
    make next stage availabale,save the data.
     */
    private void nextStageAvailable() {
        SharedPreferences sh = getSharedPreferences(STAGES, MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sh.edit();

        if (sh.getInt(AVAILABLE_STAGE, 1) == mStage && mStage < 3) {
            SharedPreferences.Editor editor = sh.edit();
            editor.putInt(AVAILABLE_STAGE, mStage + 1);
            editor.apply();
        }

//        ((TextView)mDialogStageComlete.findViewById(R.id.dialog_message_text)).setText(getResources().getText(R.string.next_stage_openned));
//        mDialogStageComlete.show();
    }

    /*
    Make next level available to play.
     */
    private void openNextLevel(int level) {
        switch (level) {
            case 2:
                buttonLevel_2.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_2.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_2.setClickable(true);
                break;
            case 3:
                buttonLevel_3.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_3.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_3.setClickable(true);
                break;
            case 4:
                buttonLevel_4.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_4.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_4.setClickable(true);
                break;
            case 5:
                buttonLevel_5.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_5.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_5.setClickable(true);
                break;
            case 6:
                buttonLevel_6.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_6.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_6.setClickable(true);
                break;
            case 7:
                buttonLevel_7.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_7.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_7.setClickable(true);
                break;
            case 8:
                buttonLevel_8.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_8.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_8.setClickable(true);
                break;
            case 9:
                buttonLevel_9.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_9.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_9.setClickable(true);
                break;
            case 10:
                buttonLevel_10.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_10.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_10.setClickable(true);
                break;
            case 11:
                buttonLevel_11.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_11.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_11.setClickable(true);
                break;
            case 12:
                buttonLevel_12.setBackgroundResource(R.drawable.level_button_unlocked);
                buttonLevel_12.setTextColor(getResources().getColor(R.color.text_black));
                buttonLevel_12.setClickable(true);
                break;
        }

        mEditor.putInt(CURRENT_LEVEL, level);
        mEditor.apply();
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

