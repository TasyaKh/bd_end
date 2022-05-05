package com.example.bd.Logic;

public enum LanguageWord {
    ENGLISH(0),
    RUSSIAN(1);
    private final int numEnum;

    LanguageWord(int numEnum){
        this.numEnum = numEnum;
    }

    public int getNumEnum() {
        return numEnum;
    }
}

