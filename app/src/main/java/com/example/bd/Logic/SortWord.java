package com.example.bd.Logic;

public enum SortWord {
    DEFAULT(0),
    FIRST_DATA(1),
    NAME(2);
    private final int numEnum;

    SortWord(int numEnum){
        this.numEnum = numEnum;
    }

    public int getNumEnum() {
        return numEnum;
    }
}
