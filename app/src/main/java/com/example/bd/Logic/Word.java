package com.example.bd.Logic;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Word implements Serializable {

    private final long id;            //Id слова
    private final String englishWord; //Английское слово
    private final String ruWord;      //Русское слово
    private final String description; //Описание
    private int priority;             //Приоритет

    public Word (long id, String englishWord, String ruWord, String description,int priority) {

        this.id = id;
        this.englishWord=englishWord;
        this.ruWord=ruWord;
        this.description=description;
        this.priority = priority;
    }

    @NonNull
    @Override
    public String toString() {
        return englishWord + " - " + ruWord;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getRuWord() {
        return ruWord;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public int getPriority() {
        return priority;
    }

    public void updatePriority() {

       if(priority <2) priority++;
       else priority = 0;
    }
}
