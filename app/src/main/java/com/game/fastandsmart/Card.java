package com.game.fastandsmart;

import java.util.ArrayList;

public class Card {
    public String mQuestion;
    private String mRightAnswer;
    private ArrayList<String> mAnswers;
    private int mTimeForAnswer;
    public int imageId;

    public Card(){
        mAnswers=new ArrayList<>();
    }

    public int getTimeForAnswer() {
        return mTimeForAnswer;
    }

    public void setTimeForAnswer(int mTimeForAnswer) {
        this.mTimeForAnswer = mTimeForAnswer;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public String getRightAnswer() {
        return mRightAnswer;
    }

    public void setRightAnswer(String mRightAnswer) {
        this.mRightAnswer = mRightAnswer;
    }

    public ArrayList<String> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(ArrayList<String> mAnswers) {
        this.mAnswers = mAnswers;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void addAnswer(String answer){
        mAnswers.add(answer);
    }
}
