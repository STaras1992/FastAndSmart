package com.game.fastandsmart;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import in.arjsna.swipecardlib.SwipeCardView;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ExpertModeGameActivity extends AppCompatActivity {

    final int TIME_OUT = 1;
    final int CORRECT_ANSWER = 3;
    final int WRONG_ANSWER = 2;
    final int HANDICAP_TIME = 100;

    private MediaPlayer mpCorrect;
    private MediaPlayer mpWrong;

    private ArrayList<Card> mCardsArray;
    private CardsAdapter mCardsAdapter;
    private CardsAdapter mCardsAdapter2;
    private SwipeCardView mSwipeCardView;

    int mLevel = 1;
    final int MAX_LEVEL = 10;
    int mTimeForAnswer = 4000; //time in milisec to answer on current level
    int mLifes = 1;
    int mMaxTime; // time in milisec to answer on current card
    int mCurrentCardPosition = 0; //position in card adapter
    int mPoints = 0;
    String mLastAnswer;
    boolean mIsGameStopped = true;
    boolean mIsTimeOut = false;
    CounterFab mFreezeButton;
    CounterFab mFiftyFiftyButton;
    TextView mLeftAnswer;
    TextView mRightAnswer;
    TextView mTopAnswer;
    TextView mBottomAnswer;
    TextView mCardsLeftView;
    TextView mLeftTimeView;
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
    Random random = new Random();

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

    Runnable continueGame = new Runnable() {
        @Override
        public void run() {
            mTextSwitcherCenter.setText("");
            mSwipeCardView.setVisibility(View.VISIBLE);
            prepareNextCardEnvironment();
            mIsGameStopped = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expert_mode_layout);
        init();
        mSwipeCardView.setVisibility(View.INVISIBLE);
        startGame();
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
        mMaxTime = mTimeForAnswer + HANDICAP_TIME;
        mCardsAdapter = new CardsAdapter(this, mCardsArray);
        mSwipeCardView.setAdapter(mCardsAdapter);
        mCardsLeft = mCardsAdapter.getCount();
        mCardsLeftView.setText(Integer.toString(mPoints));
        mLifesView.setText(Integer.toString(mLifes));

        mDialogTryAgain = new Dialog(this, R.style.DialogEndGame);
        mDialogTryAgain.setContentView(R.layout.dialog_expert_end_game);
        mDialogTryAgain.getWindow().setBackgroundDrawableResource(R.drawable.dialog_end_game);
        mDialogTryAgain.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogTryAgain.setCancelable(false);

        /*Set listeners:*/
        mSwipeCardView.setFlingListener(new CardSwipeListener());//actions on cards swipe
        mFreezeButton.setOnClickListener(new HelpButtonClickListener());
        mFiftyFiftyButton.setOnClickListener(new HelpButtonClickListener());
        mFreezeButton.setClickable(false);
        mFiftyFiftyButton.setClickable(false);

        /* SAVE */
        mDialogTryAgain.findViewById(R.id.dialog_button_save).setOnClickListener(new SaveResultListener());
        /* TRY AGAIN */
        mDialogTryAgain.findViewById(R.id.dialog_button_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogTryAgain.dismiss();
                Intent intent = new Intent();
                intent.putExtra("try again", true);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        final RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        mProgressBar.startAnimation(makeVertical);
        mProgressBar.setSecondaryProgress(mTimeForAnswer);
        mProgressBar.setProgress(0);


        /*Text switcher Animation*/
        mTextSwitcherTop.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        mTextSwitcherTop.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        mTextSwitcherCenter.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mTextSwitcherCenter.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    }

    /*Load levels from text file.*/
    public void loadLevels() {
        ArrayList<Card> cardsArray1 = new ArrayList<>();
        ArrayList<Card> cardsArray2 = new ArrayList<>();
        ArrayList<Card> cardsArray3 = new ArrayList<>();
        BufferedReader reader = null;
        String filename1 = "expert_level1.txt";
        String filename2 = "expert_level2.txt";
        String filename3 = "expert_level3.txt";
        /*
          Parse levels text file.
         */
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename1)));
            String line;
            Card card;
            String[] answers;

            while (!(line = reader.readLine()).equals("end")) {
                card = new Card();
                card.setQuestion(line);
                line = reader.readLine();
                answers = line.split(",");
                for (String option : answers) {
                    card.addAnswer(option);
                }
                card.setRightAnswer(reader.readLine());
                cardsArray1.add(card);
            }
            reader.close();

            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename2)));
            while (!(line = reader.readLine()).equals("end")) {
                card = new Card();
                card.setQuestion(line);
                line = reader.readLine();
                answers = line.split(",");
                for (String option : answers) {
                    card.addAnswer(option);
                }
                card.setRightAnswer(reader.readLine());
                cardsArray2.add(card);
            }

//            reader.close();
//            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename3)));
//            while (!(line = reader.readLine()).equals("end")) {
//                card = new Card();
//                card.setQuestion(line);
//                line = reader.readLine();
//                answers = line.split(",");
//                for (String option : answers) {
//                    card.addAnswer(option);
//                }
//                card.setRightAnswer(reader.readLine());
//                cardsArray3.add(card);
//            }

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

        Collections.shuffle(cardsArray1);
        Collections.shuffle(cardsArray2);
        //Collections.shuffle(cardsArray3);

        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(cardsArray1);
        cards.addAll(cardsArray2);
        //cards.addAll(cardsArray3);

        Collections.shuffle(cards);

        mCardsArray = cards;
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
                mIsTimeOut=true;

                if (!(this).isFinishing()) {
                    if (mLifes == 1) {
                        String correct_answer = mCardsAdapter.getItem(mCurrentCardPosition).getQuestion() + "=" + mCardsAdapter.getItem(mCurrentCardPosition).getRightAnswer();
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_answer)).setText(correct_answer);
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_message_text)).setText(R.string.dialog_time_out);
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_temp_text)).setText(getResources().getText(R.string.dialog_score) + " " + Integer.toString(mPoints));
                        mDialogTryAgain.show();

                    } else {
                        mTextSwitcherTop.setText(getResources().getText(R.string.message_time_out));
                        --mLifes;
                        mLifesView.setText(Integer.toString(mLifes));
                        mActionHandler.postDelayed(returnScreenBacgroundTask, 200);
                        mActionHandler.postDelayed(clearTextTopTask, 800);
                        continueGame();
                        mIsTimeOut=false;
                    }
                }

                break;

            case WRONG_ANSWER:
                mGameLayout.setBackgroundColor(getResources().getColor(R.color.colorBadAnswer));
                mTextSwitcherTop.setText("");

                if (!(this).isFinishing()) {
                    if (mLifes == 1) {
                        String correct_answer = mCardsAdapter.getItem(mCurrentCardPosition).getQuestion() + "=" + mCardsAdapter.getItem(mCurrentCardPosition).getRightAnswer();
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_answer)).setText(correct_answer);
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_message_text)).setText(R.string.dialog_wrong_answer);
                        ((TextView) mDialogTryAgain.findViewById(R.id.dialog_temp_text)).setText(getResources().getText(R.string.dialog_score) + " " + Integer.toString(mPoints));
                        mDialogTryAgain.show();

                    } else {
                        mTextSwitcherTop.setText(getResources().getText(R.string.wrong));
                        --mLifes;
                        mLifesView.setText(Integer.toString(mLifes));
                        mActionHandler.postDelayed(returnScreenBacgroundTask, 200);
                        mActionHandler.postDelayed(clearTextTopTask, 800);
                        continueGame();
                    }
                }

                break;
            case CORRECT_ANSWER:
                mGameLayout.setBackgroundColor(getResources().getColor(R.color.colorGoodAnswer));
                mTextSwitcherTop.setText(getResources().getText(R.string.message_correct));

                ++mPoints;
                mCardsLeftView.setText(Integer.toString(mPoints));
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
        mProgressBar.setMax(mMaxTime - HANDICAP_TIME);
        mProgressBar.setSecondaryProgress(mMaxTime - HANDICAP_TIME);
        mProgressBar.setProgress(mTimeUsed);

        if (mTimeUsed >= mMaxTime) {
            actionOnQuestionFinish(TIME_OUT);
        }
    }

    /* Reset progress view and reset timer for answer */
    private void resetProgressTimer() {
        mTimeUsed = 0;
        mMaxTime = mTimeForAnswer + HANDICAP_TIME;
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
                if (mFreezeButton.getCount() != 0)
                    mFreezeButton.setClickable(true);
                else
                    mFreezeButton.setClickable(false);
                if (mFiftyFiftyButton.getCount() != 0)
                    mFiftyFiftyButton.setClickable(true);
                else
                    mFiftyFiftyButton.setClickable(false);
                prepareNextCardEnvironment();
                mIsGameStopped = false;
            }
        }, 5500);
    }

    private void continueGame() {
        if(!mIsTimeOut) {
            mCardsLeft = mCardsAdapter.getCount() - 1 - mCurrentCardPosition++;
        }
        if (mCardsLeft != 0) {
            if (mPoints % 10 == 0) {
                mSwipeCardView.setVisibility(View.INVISIBLE);
                changeGame();
            } else {
                prepareNextCardEnvironment();
                mIsGameStopped = false;
            }
        } else {
            mDialogTryAgain.show();
        }
    }

    private void changeGame() {
        int rand = random.nextInt(6)+6;
        Log.i("Random","\nrand:"+rand+"\nrand%5:"+rand%5+"\n");
        if (rand % 5 == 0) {
            mFiftyFiftyButton.increase();
            mFiftyFiftyButton.setClickable(true);
            mTextSwitcherCenter.setText(getResources().getText(R.string.plus_fifty_fifty));
            mActionHandler.postDelayed(continueGame, 1500);
        }
        else if(rand % 5 == 1){
            mFreezeButton.increase();
            mFreezeButton.setClickable(true);
            mTextSwitcherCenter.setText(getResources().getText(R.string.plus_freeze));
            mActionHandler.postDelayed(continueGame, 1500);
        }
        else if(rand % 5 == 2) {
            ++mLifes;
            mLifesView.setText(Integer.toString(mLifes));
            mTextSwitcherCenter.setText(getResources().getText(R.string.plus_life));
            mActionHandler.postDelayed(continueGame, 1500);
        }
        else if (rand % 5 == 3) {
            int time = 100;
            mTimeForAnswer -= time;
            mMaxTime=mTimeForAnswer + HANDICAP_TIME;
            mTextSwitcherCenter.setText("-" + time + "msec");
            mActionHandler.postDelayed(continueGame,1500);
        }
        else if (rand % 5 == 4) {
            int time = 150;
            mTimeForAnswer -= time;
            mMaxTime=mTimeForAnswer + HANDICAP_TIME;
            mTextSwitcherCenter.setText("-" + time + "msec");
            mActionHandler.postDelayed(continueGame,1500);
        }
        else {
            int time = 200;
            mTimeForAnswer -= time;
            mMaxTime=mTimeForAnswer + HANDICAP_TIME;
            mTextSwitcherCenter.setText("-" + time + "msec");
            mActionHandler.postDelayed(continueGame,1500);
        }
//        switch (mPoints) {
//            case 10:
//            case 20:
//            case 30:
//            case 40:
//            case 50:
//            case 60:
//            case 70:
//            case 80:
//            case 90:
//            case 100:
//            case 110:
//            case 120:
//            case 130:
//            case 140:
//            case 150:
//            case 160:
//            case 170:
//            case 180:
//            case 190:
//            case 200:
//            default:
//                break;
//        }
    }

    public class CardSwipeListener implements SwipeCardView.OnCardFlingListener { //actions on cards swipe
        @Override
        public void onCardExitLeft(Object dataObject) {
            if (mIsGameStopped) //if was timeout and swap at same time
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer = (String) mLeftAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mLeftAnswer.getText())) {
                if (MainActivity.haveSound) {
                    mpCorrect.start();
                }
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                if (MainActivity.haveSound) {
                    mpWrong.start();
                }
                actionOnQuestionFinish(WRONG_ANSWER);
            }

        }

        @Override
        public void onCardExitRight(Object dataObject) {
            if (mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer = (String) mRightAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mRightAnswer.getText())) {
                if (MainActivity.haveSound) {
                    mpCorrect.start();
                }
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                if (MainActivity.haveSound) {
                    mpWrong.start();
                }
                actionOnQuestionFinish(WRONG_ANSWER);
            }

        }

        @Override
        public void onCardExitTop(Object dataObject) {
            if (mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer = (String) mTopAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mTopAnswer.getText())) {
                if (MainActivity.haveSound) {
                    mpCorrect.start();
                }
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                if (MainActivity.haveSound) {
                    mpWrong.start();
                }
                actionOnQuestionFinish(WRONG_ANSWER);
            }
        }


        @Override
        public void onCardExitBottom(Object dataObject) {
            if (mIsGameStopped)
                return;
            Card swipedCard = (Card) dataObject;
            mLastAnswer = (String) mBottomAnswer.getText();
            if (swipedCard.getRightAnswer().equals(mBottomAnswer.getText())) {
                if (MainActivity.haveSound) {
                    mpCorrect.start();
                }
                actionOnQuestionFinish(CORRECT_ANSWER);
            } else {
                if (MainActivity.haveSound) {
                    mpWrong.start();
                }
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

    public class SaveResultListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            EditText editText = mDialogTryAgain.findViewById(R.id.edit_name_etext);
            String name = editText.getText().toString();
            if (name == null || name.equals("")) {
                editText.setHintTextColor(getResources().getColor(R.color.enter_name_hint));
                //editText.setHint(getResources().getText(R.string.enter_name));
                return;
            } else {
                final Intent intent = new Intent();
                intent.putExtra("points", mPoints);
                intent.putExtra("name", name);
                setResult(RESULT_OK, intent);
                mDialogTryAgain.dismiss();
                finish();
            }
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

    public class TextSwitcherFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            TextView textView = new TextView(ExpertModeGameActivity.this);
            textView.setTextSize(40);
            textView.setTextColor(getResources().getColor(R.color.textSwitcher_color));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            return textView;
        }
    }
}

