package com.example.bd.Logic;

import androidx.annotation.NonNull;

public class WordStatistic extends Word {
    private byte countCorrect; //правильно попали
    private int allAttempts;   //все попытки

    public WordStatistic(Word word) {
        super(word.getId(), word.getEnglishWord(), word.getRuWord(), word.getDescription(), word.getPriority());
        countCorrect = 0;
        allAttempts = 0;
    }

    public void updateStatistic(boolean isCorrectHit) {

        if (isCorrectHit) {
            countCorrect++;
        }

        allAttempts++;
    }

    public byte getCountCorrect() {
        return countCorrect;
    }

    public int getAllAttempts() {
        return allAttempts;
    }

    @NonNull
    @Override
    public String toString() {

        return getEnglishWord() + "/" + getRuWord() + " correct: "
                + countCorrect + " attempts: " + allAttempts;
    }
}
