package com.game.fastandsmart;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import in.arjsna.swipecardlib.SwipeCardView;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class GameActivity extends AppCompatActivity {

    final int TIME_OUT = 1;
    final int CORRECT_ANSWER = 3;
    final int WRONG_ANSWER = 2;
    final int HADICAP_TIME = 100 ;
    final int RESULT_TRY_AGAIN = 4;

    private MediaPlayer mpCorrect;
    private MediaPlayer mpWrong;


    private ArrayList<Card> mCardsArray;
    private CardsAdapter mCardsAdapter;
    private SwipeCardView mSwipeCardView;

    int mStage;
    int mLevel;
    int mTimeForAnswer =4000;
    int mLifes = 2;
    int mMaxTime; // time in milisec to answer on current card
    int mCurrentCardPosition = 0; //position in card adapter
    String mLastAnswer;
    boolean mIsTutorialDone = false;
    boolean mIsGameStopped = true;
    boolean mIsTimeout = false;
    CounterFab mFreezeButton;
    CounterFab mFiftyFiftyButton;
    TextView mLeftAnswer;
    TextView mRightAnswer;
    TextView mTopAnswer;
    TextView mBottomAnswer;
    TextView mCardsLeftView;
    TextView mLifesView;
    TextSwitcher mTextSwitcherTop;
    TextSwitcher mTextSwitcherCenter;
    ProgressBar mProgressBar;
    RelativeLayout mGameLayout;
    int mCardsLeft;
    int mTimeUsed = 0;
    Handler mTimerHandler = new Handler(Looper.getMainLooper());
    Handler mActionHandler = new Handler(Looper.getMainLooper());
    Dialog mDialogTryAgain;
    Dialog mDialogContinue;
    Dialog mDialogLevelComplete;
    Dialog mCurrentDialog;
    ShowcaseView mCurrentShowcase;

    /*Task for every X milisec update progress view */
    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsGameStopped) {
                mTimeUsed += 50;
                updateProgressBar();
                if (mTimeUsed < mMaxTime) {
                    startTimer();
                }
            }
        }
    };

    /*Tssk for return screen background to original after was changed*/
    Runnable returnScreenBacgroundTask = new Runnable() {
        @Override
        public void run() {
            mGameLayout.setBackgroundResource(R.drawable.game_background);
        }
    };

    Runnable clearTextTopTask = new Runnable() {
        @Override
        public void run() {
            mTextSwitcherTop.setText("");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classic_mode_game_layout);

        SoundHandler.stopMusic();

        mStage = getIntent().getIntExtra("stage", 0);
        mLevel = getIntent().getIntExtra("level", 0);
        mIsTutorialDone = getIntent().getBooleanExtra("tutorial passed",true);

        if (mLevel == 0){
            setResult(RESULT_CANCELED);
            finish();
        }

        //mIsTutorialDone == getIntent().getBooleanExtra("tutorial passed", false)
        else if (mLevel == 1 && !mIsTutorialDone) {
            init();
            mSwipeCardView.setVisibility(View.VISIBLE);
            mBottomAnswer.setVisibility(View.INVISIBLE);
            mTopAnswer.setVisibility(View.INVISIBLE);
            mLeftAnswer.setVisibility(View.INVISIBLE);
            mRightAnswer.setVisibility(View.INVISIBLE);
            mLifesView.setVisibility(View.INVISIBLE);
            mCardsLeftView.setVisibility(View.INVISIBLE);
            mFreezeButton.setVisibility(View.INVISIBLE);
            mFiftyFiftyButton.setVisibility(View.INVISIBLE);
            mActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startBasicTutorial();
                }
            },200);
        }
        else if (mLevel == 2 && !mIsTutorialDone){
            init();
            mSwipeCardView.setVisibility(View.INVISIBLE);
            mFiftyFiftyButton.setVisibility(View.INVISIBLE);
            mActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startFreezeTutorial();
                }
            },200);
        }
        else if (mLevel == 3 && !mIsTutorialDone){
            init();
            mSwipeCardView.setVisibility(View.INVISIBLE);
            mActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startFiftyFiftyTutorial();
                }
            },200);
        }
        else {
            init();
            if(mStage==1 && mLevel == 1){
                mFreezeButton.setVisibility(View.INVISIBLE);
                mFiftyFiftyButton.setVisibility(View.INVISIBLE);
            }
            else if(mStage==1 && mLevel == 2){
                mFreezeButton.setVisibility(View.INVISIBLE);
            }
            mSwipeCardView.setVisibility(View.INVISIBLE);
            startGame();
        }
    }

    private void init() {

        /*Get references */
        mGameLayout = findViewById(R.id.game_view);
        mLeftAnswer = findViewById(R.id.answer_left);
        mRightAnswer = findViewById(R.id.answer_right);
        mTopAnswer = findViewById(R.id.answer_up);
        mBottomAnswer = findViewById(R.id.answer_down);
        mCardsLeftView = findViewById(R.id.view_CardsLeft);
        mSwipeCardView = findViewById(R.id.card_Swipe_View); //swipe cards view get from layout
        mProgressBar = findViewById(R.id.view_progress_bar);
        mFreezeButton = findViewById(R.id.fab_freeze);
        mFiftyFiftyButton = findViewById(R.id.fab_fifty_fifty);
        mTextSwitcherTop = findViewById(R.id.textSwitcher_top);
        mTextSwitcherCenter = findViewById(R.id.textSwitcher_center);
        mLifesView = findViewById(R.id.lifes_text);

        mpCorrect =MediaPlayer.create(this, R.raw.correct3);
        mpWrong = MediaPlayer.create(this, R.raw.wrong);

        mTextSwitcherTop.setFactory(new TextSwitcherFactory());
        mTextSwitcherCenter.setFactory(new TextSwitcherFactory());
        mCardsArray = new ArrayList<>();
        loadLevels();
        mMaxTime = mTimeForAnswer + HADICAP_TIME;
        mCardsAdapter = new CardsAdapter(this, mCardsArray);
        mSwipeCardView.setAdapter(mCardsAdapter);
        mCardsLeft = mCardsAdapter.getCount();
        mCardsLeftView.setText(Integer.toString(mCardsLeft));
        mLifesView.setText(Integer.toString(mLifes));

        mDialogTryAgain = new Dialog(this, R.style.DialogEndGame);
        mDialogTryAgain.setContentView(R.layout.dialog_try_again);
        mDialogTryAgain.getWindow().setBackgroundDrawableResource(R.drawable.dialog_end_game);
        mDialogTryAgain.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogTryAgain.setCancelable(false);
        mDialogContinue = new Dialog(this, R.style.DialogEndGame);
        mDialogContinue.setContentView(R.layout.dialog_to_continue);
        mDialogContinue.getWindow().setBackgroundDrawableResource(R.drawable.dialog_end_game);
        mDialogContinue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogContinue.setCancelable(false);
        mDialogLevelComplete = new Dialog(this, R.style.DialogEndGame);
        mDialogLevelComplete.setContentView(R.layout.dialog_level_complete);
        mDialogLevelComplete.getWindow().setBackgroundDrawableResource(R.drawable.dialog_end_game);
        mDialogLevelComplete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogLevelComplete.setCancelable(false);


        /*Set listeners:*/
        mSwipeCardView.setFlingListener(new CardSwipeListener());//actions on cards swipe
        mFreezeButton.setOnClickListener(new HelpButtonClickListener());
        mFiftyFiftyButton.setOnClickListener(new HelpButtonClickListener());
        mFreezeButton.setClickable(false);
        mFiftyFiftyButton.setClickable(false);
        mDialogTryAgain.findViewById(R.id.dialog_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDialogTryAgain.dismiss();
                setResult(RESULT_CANCELED);
                mDialogTryAgain.dismiss();
                finish();
            }
        });
        mDialogTryAgain.findViewById(R.id.dialog_button_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDialogTryAgain.dismiss();
                Intent intent = new Intent();
                intent.putExtra("level",mLevel);
                setResult(RESULT_TRY_AGAIN, intent);
                mDialogTryAgain.dismiss();
                finish();
            }
        });
        mDialogContinue.findViewById(R.id.dialog_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDialogContinue.dismiss();
                setResult(RESULT_CANCELED);
                mDialogContinue.dismiss();
                finish();
            }
        });
        mDialogContinue.findViewById(R.id.dialog_button_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLifes -= 1;
                mLifesView.setText(Integer.toString(mLifes));
                mActionHandler.postDelayed(returnScreenBacgroundTask, 100);
                //mDialogContinue.dismiss();
                continueGame();
                mDialogContinue.dismiss();
            }
        });

        mDialogLevelComplete.findViewById(R.id.dialog_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDialogLevelComplete.dismiss();
                Intent intent = new Intent();
                intent.putExtra("level",mLevel);
                setResult(RESULT_CANCELED,intent);
                mDialogLevelComplete.dismiss();
                finish();
            }
        });

        mDialogLevelComplete.findViewById(R.id.dialog_button_next_level).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDialogContinue.dismiss();
                Intent intent = new Intent();
                intent.putExtra("level",mLevel);
                setResult(RESULT_OK,intent);
                mDialogLevelComplete.dismiss();
                finish();
            }
        });

        final RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        mProgressBar.startAnimation(makeVertical);
        mProgressBar.setSecondaryProgress(mTimeForAnswer);
        mProgressBar.setProgress(0);


        /*Text switcher Animation*/
        Animation mTextSwitcherTopAnimationIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation mTextSwitcherTopAnimationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        Animation mTextSwitcherCenterAnimationIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation mTextSwitcherCenterAnimationOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mTextSwitcherTop.setInAnimation(mTextSwitcherTopAnimationIn);
        mTextSwitcherTop.setOutAnimation(mTextSwitcherTopAnimationOut);
        mTextSwitcherCenter.setInAnimation(mTextSwitcherCenterAnimationIn);
        mTextSwitcherCenter.setOutAnimation(mTextSwitcherCenterAnimationOut);
    }

    /*Load levels from text file.For each card: Level name -> time for answer -> question -> answers -> right answer*/
    public void loadLevels() {
        BufferedReader reader = null;
        String filename = null;

        switch (mStage) {
            case 1:
                //filename = "stage1.txt";
                filename = "stage1.txt";
                break;
            case 2:
                //filename = "stage2.txt";
                filename = "stage2.txt";
                break;
            case 3:
                //filename = "stage3.txt";
                filename = "stage3.txt";
                break;
        }

        /*
          Parse levels text file.
         */
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            String line;
            Card card;
            String[] answers;

            while (!(line = reader.readLine()).equals("level" + mLevel)) {
                if (line.equals("end stage")) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

            mTimeForAnswer = Integer.parseInt(reader.readLine());
            //mTimeForAnswer = 10000; //TODO remove
            mLifes = Integer.parseInt(reader.readLine());
            mFreezeButton.setCount(Integer.parseInt(reader.readLine()));
            mFiftyFiftyButton.setCount(Integer.parseInt(reader.readLine()));

            while (!(line = reader.readLine()).equals("end level")) {
                card = new Card();
                card.setQuestion(line);
                line = reader.readLine();
                answers = line.split(",");
                for (String option : answers) {
                    card.addAnswer(option);
                }
                card.setRightAnswer(reader.readLine());
                mCardsArray.add(card);
            }

        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    /*Action to stop game and show correct message on end of single card turn*/
    private void actionOnQuestionFinish(int option) {
        /*
         * 1 - timeout
         * 2 - wrong Answer
         * 3 - correct answer
         */
        mIsGameStopped = true;
        cancelTimer();
        switch (option) {
            case TIME_OUT:
                mGameLayout.setBackgroundColor(getResources().getColor(R.color.colorTimeOut));
                mTextSwitcherTop.setText("");
                mIsTimeout = true;
                if (!(this).isFinishing()) {
                    if (mLifes == 1)
                        mCurrentDialog = mDialogTryAgain;
                    else
                        mCurrentDialog = mDialogContinue;
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_message_text)).setText(R.string.dialog_time_out);
                    String correct_answer = mCardsAdapter.getItem(mCurrentCardPosition).getQuestion() + "=" + mCardsAdapter.getItem(mCurrentCardPosition).getRightAnswer();
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_wrong_answer)).setVisibility(View.GONE);
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_correct_answer)).setVisibility(View.GONE);
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_temp_text)).setText(correct_answer);
                    mCurrentDialog.show();
                }

                //mActionHandler.postDelayed(returnScreenBacgroundTask, 200);
                break;
            case WRONG_ANSWER:

                mGameLayout.setBackgroundColor(getResources().getColor(R.color.colorBadAnswer));
                mTextSwitcherTop.setText("");

                if (!(this).isFinishing()) {
                    if (mLifes == 1)
                        mCurrentDialog = mDialogTryAgain;
                    else
                        mCurrentDialog = mDialogContinue;
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_message_text)).setText(R.string.dialog_wrong_answer);
                    String correct_answer = mCardsAdapter.getItem(mCurrentCardPosition).getQuestion() + "=" + mCardsAdapter.getItem(mCurrentCardPosition).getRightAnswer();
                    String wrong_answer = mCardsAdapter.getItem(mCurrentCardPosition).getQuestion() + "=" + mLastAnswer;
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_wrong_answer)).setText(wrong_answer);
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_correct_answer)).setText(correct_answer);
                    ((TextView) mCurrentDialog.findViewById(R.id.dialog_temp_text)).setVisibility(View.GONE);
                    mCurrentDialog.show();
                }

                //mActionHandler.postDelayed(returnScreenBacgroundTask, 200);
                break;
            case CORRECT_ANSWER:
                mGameLayout.setBackgroundColor(getResources().getColor(R.color.colorGoodAnswer));
                mTextSwitcherTop.setText(getResources().getText(R.string.message_correct));
                continueGame();
                mActionHandler.postDelayed(returnScreenBacgroundTask, 200);
                mActionHandler.postDelayed(clearTextTopTask, 800);
                break;
        }
    }

    /*After card was swiped and game continue,set availabale answers and reset timer*/
    private void prepareNextCardEnvironment() {
        ArrayList<String> answers = mCardsAdapter.getItem(mCurrentCardPosition).getAnswers();
        mTopAnswer.setText(answers.get(0));
        mRightAnswer.setText(answers.get(1));
        mBottomAnswer.setText(answers.get(2));
        mLeftAnswer.setText(answers.get(3));
        resetProgressTimer();
    }

    /*Update progress view ,if maximum time for answer reached set action on time out*/
    private void updateProgressBar() {
        mProgressBar.setMax(mMaxTime - HADICAP_TIME);
        mProgressBar.setSecondaryProgress(mMaxTime - HADICAP_TIME);
        mProgressBar.setProgress(mTimeUsed);

        if (mTimeUsed >= mMaxTime) {
            actionOnQuestionFinish(1);
        }
    }

    /* Reset progress view and reset timer for answer */
    private void resetProgressTimer() {
        mTimeUsed = 0;
        mMaxTime = mTimeForAnswer + HADICAP_TIME;
        mProgressBar.setProgress(0);
        cancelTimer();
        startTimer();
    }

    /*call to timer task after X milisec delay*/
    public void startTimer() {
        mTimerHandler.postDelayed(timerTask, 50);
    }

    /*Remove task from list*/
    public void cancelTimer() {
        mTimerHandler.removeCallbacks(timerTask);
    }

    private void startGame() {
        mTextSwitcherCenter.setText(getResources().getText(R.string.message_ready));
        mActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextSwitcherCenter.setText("3");
            }
        }, 1500);

        mActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextSwitcherCenter.setText("2");
            }
        }, 2500);

        mActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextSwitcherCenter.setText("1");
            }
        }, 3500);

        mActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextSwitcherCenter.setText(getResources().getText(R.string.message_go));
            }
        }, 4500);

        mActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextSwitcherCenter.setText("");
                mSwipeCardView.setVisibility(View.VISIBLE); //MUST
                ArrayList<String> answers = mCardsAdapter.getItem(mCurrentCardPosition).getAnswers();
                mTopAnswer.setText(answers.get(0));
                mRightAnswer.setText(answers.get(1));
                mBottomAnswer.setText(answers.get(2));
                mLeftAnswer.setText(answers.get(3));
                if(mFreezeButton.getCount() != 0)
                    mFreezeButton.setClickable(true);
                else
                    mFreezeButton.setClickable(false);

                if(mFiftyFiftyButton.getCount() != 0)
                    mFiftyFiftyButton.setClickable(true);
                else
                    mFiftyFiftyButton.setClickable(false);

                startTimer();
                mIsGameStopped = false;
            }
        }, 5500);
    }

    private void restartLevel() {
        finish();
        startActivityForResult(getIntent(),1);
    }

    private void startNextLevel() {
        finish();
        Intent intent = new Intent(GameActivity.this, GameActivity.class);
        intent.putExtra("stage", mStage);
        intent.putExtra("level", mLevel + 1);
        startActivity(intent);
    }

    private void continueGame() {
        if(!mIsTimeout) {
            mCardsLeft = mCardsAdapter.getCount() - 1 - mCurrentCardPosition++;
            mCardsLeftView.setText(Integer.toString(mCardsLeft));
        }
        else{
            mIsTimeout=false;
        }
        if (mCardsLeft != 0) {
            prepareNextCardEnvironment();
            mIsGameStopped = false;
        }
        else{
            mDialogLevelComplete.show();
        }
    }

    private void startBasicTutorial() {
        mCurrentShowcase = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(mSwipeCardView))
                .blockAllTouches()
                .setContentTitle(getResources().getText(R.string.questions_title))
                .setContentText(getResources().getText(R.string.pop_up_question))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentShowcase.hide();
                        mCurrentShowcase = new ShowcaseView.Builder(GameActivity.this)
                                .setStyle(R.style.CustomShowcaseTheme2)
                                .setTarget(new ViewTarget(mLeftAnswer))
                                .blockAllTouches()
                                .setContentTitle(getResources().getText(R.string.timer_title))
                                .setContentText(getResources().getText(R.string.pop_up_timer))
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mCurrentShowcase.hide();
                                        mBottomAnswer.setVisibility(View.VISIBLE);
                                        mTopAnswer.setVisibility(View.VISIBLE);
                                        mLeftAnswer.setVisibility(View.VISIBLE);
                                        mRightAnswer.setVisibility(View.VISIBLE);
                                        mCurrentShowcase =  new ShowcaseView.Builder(GameActivity.this)
                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                .setTarget(new ViewTarget(mTopAnswer))
                                                .blockAllTouches()
                                                .setContentTitle(getResources().getText(R.string.answers_title))
                                                .setContentText(getResources().getText(R.string.pop_up_answers))
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mCurrentShowcase.hide();
                                                        mLifesView.setVisibility(View.VISIBLE);
                                                        mCurrentShowcase = new ShowcaseView.Builder(GameActivity.this)
                                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                                .setTarget(new ViewTarget(mLifesView))
                                                                .blockAllTouches()
                                                                .setContentTitle(getResources().getText(R.string.lifes_title))
                                                                .setContentText(getResources().getText(R.string.pop_up_lifes))
                                                                .setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        mCurrentShowcase.hide();
                                                                        mCardsLeftView.setVisibility(View.VISIBLE);
                                                                        mCurrentShowcase = new ShowcaseView.Builder(GameActivity.this)
                                                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                                                .setTarget(new ViewTarget(mCardsLeftView))
                                                                                .blockAllTouches()
                                                                                .setContentTitle(getResources().getText(R.string.cards_left_title))
                                                                                .setContentText(getResources().getText(R.string.pop_up_cards_left))
                                                                                .setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        mCurrentShowcase.hide();
                                                                                        mCurrentShowcase = new ShowcaseView.Builder(GameActivity.this)
                                                                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                                                                .setTarget(new ViewTarget(mRightAnswer))
                                                                                                .blockAllTouches()
                                                                                                .setContentTitle(getResources().getText(R.string.start_playing_title))
                                                                                                .setContentText(getResources().getText(R.string.pop_up_how_to_play))
                                                                                                .setOnClickListener(new View.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(View v) {
                                                                                                        mSwipeCardView.setVisibility(View.INVISIBLE);
                                                                                                        startGame();
                                                                                                        mCurrentShowcase.hide();
                                                                                                    }
                                                                                                })
                                                                                                .build();
                                                                                    }
                                                                                })
                                                                                .build();
                                                                    }
                                                                })
                                                                .build();
                                                    }
                                                })
                                                .build();
                                    }
                                })
                                .build();
                    }
                })
                .build();


    }

    private void startFreezeTutorial(){
        mCurrentShowcase = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(mFreezeButton))
                .blockAllTouches()
                .setContentTitle("Freeze")
                .setContentText(getResources().getText(R.string.pop_up_freeze_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSwipeCardView.setVisibility(View.INVISIBLE);
                        startGame();
                        mCurrentShowcase.hide();
                    }
                })
                .build();
    }

    private void startFiftyFiftyTutorial(){
        mCurrentShowcase = new ShowcaseView.Builder(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(mFiftyFiftyButton))
                .blockAllTouches()
                .setContentTitle("50/50")
                .setContentText(getResources().getText(R.string.pop_up_fifty_fifty))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSwipeCardView.setVisibility(View.INVISIBLE);
                        startGame();
                        mCurrentShowcase.hide();
                    }
                })
                .build();
    }

    public class CardSwipeListener implements SwipeCardView.OnCardFlingListener { //actions on cards swipe
        @Override
        public void onCardExitLeft(Object dataObject) {
            if(mIsGameStopped) //if was timeout and swap at same time
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer =(String)mLeftAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mLeftAnswer.getText())) {
                SoundHandler.playCorrectAnswer();
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                SoundHandler.playWrongAnswer();
                actionOnQuestionFinish(WRONG_ANSWER);
            }

        }

        @Override
        public void onCardExitRight(Object dataObject) {
            if(mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer =(String)mRightAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mRightAnswer.getText())) {
                SoundHandler.playCorrectAnswer();
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                SoundHandler.playWrongAnswer();
                actionOnQuestionFinish(WRONG_ANSWER);
            }

        }

        @Override
        public void onCardExitTop(Object dataObject) {
            if(mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer =(String)mTopAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mTopAnswer.getText())) {
                SoundHandler.playCorrectAnswer();
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                SoundHandler.playWrongAnswer();
                actionOnQuestionFinish(WRONG_ANSWER);
            }
        }


        @Override
        public void onCardExitBottom(Object dataObject) {
            if(mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer =(String)mBottomAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mBottomAnswer.getText())) {
                SoundHandler.playCorrectAnswer();
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                SoundHandler.playWrongAnswer();
                actionOnQuestionFinish(WRONG_ANSWER);
            }
        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {

        }

        @Override
        public void onScroll(float scrollProgressPercent) {

        }
    }

    public class HelpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fab_freeze) {
                mMaxTime = 10000 + (mMaxTime - mTimeUsed);
                mGameLayout.setBackgroundResource(R.drawable.freeze_bacground);
                mFreezeButton.decrease();
                if (mFreezeButton.getCount() == 0)
                    mFreezeButton.setClickable(false);

            } else if (v.getId() == R.id.fab_fifty_fifty) {
                int removed = 0;
                Random rand = new Random();
                Card card = mCardsAdapter.getItem(mCurrentCardPosition);
                ArrayList<String> answers = card.getAnswers();
                int dontRemoveIndex1 = answers.indexOf(card.getRightAnswer());
                int dontRemoveIndex2 = -1;
                int i;

                while (removed < 2) {
                    i = rand.nextInt(4);

                    if (i == dontRemoveIndex1 || i == dontRemoveIndex2)
                        continue;

                    switch (i) {
                        case 0:
                            mTopAnswer.setText("");
                            dontRemoveIndex2 = i;
                            ++removed;
                            break;
                        case 1:
                            mRightAnswer.setText("");
                            dontRemoveIndex2 = i;
                            ++removed;
                            break;
                        case 2:
                            mBottomAnswer.setText("");
                            dontRemoveIndex2 = i;
                            ++removed;
                            break;
                        case 3:
                            mLeftAnswer.setText("");
                            dontRemoveIndex2 = i;
                            ++removed;
                            break;
                    }
                }
                mFiftyFiftyButton.decrease();
                if (mFiftyFiftyButton.getCount() == 0)
                    mFiftyFiftyButton.setClickable(false);
            }
        }
    }

//    private class PopUpDismissListener implements EasyDialog.OnEasyDialogDismissed {
//        @Override
//        public void onDismissed() {
//            mIsTutorialDone = true;
//        }
//    }

    public class TextSwitcherFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView textView = new TextView(GameActivity.this);
            textView.setTextSize(40);
            textView.setTextColor(getResources().getColor(R.color.textSwitcher_color));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            return textView;
        }
    }

}


//    public static View makeMeBlink(View view, int duration, int offset) {
//
//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(duration);
//        anim.setStartOffset(offset);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        view.startAnimation(anim);
//        return view;
//    }

//
//    @OnClick(R.id.top) public void top() {
//        /**
//         * Trigger the right event manually.
//         */
//        swipeCardView.throwTop();
//    }
//
//    @OnClick(R.id.bottom) public void bottom() {
//        swipeCardView.throwBottom();
//    }
//
//    @OnClick(R.id.left) public void left() {
//        swipeCardView.throwLeft();
//    }
//
//    @OnClick(R.id.right) public void right() {
//        swipeCardView.throwRight();
//    }
//    @OnClick(R.id.restart) public void restart() {
//        swipeCardView.restart();
//    }
//
//    @OnClick(R.id.position)
//    public void toastCurrentPosition(){
//        makeToast(this, String.valueOf(swipeCardView.getCurrentPosition()));
//    }
