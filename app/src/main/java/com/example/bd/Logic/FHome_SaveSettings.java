package com.example.bd.Logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.SortWord;

import java.util.HashSet;

public class FHome_SaveSettings {
    private final Context context;                  //контекст
    private final SharedPreferences settingsSort;   //нужен, чтобы сохранить настройки

    private final String LANGUAGE_WORD ="LanguageWord";  //язык
    private final String SORT_WORD = "SortWord";         //тип сортировки
    private final String SAVE_SETTINGS = "SaveSettings"; //дать название

    public FHome_SaveSettings(Context context){
        this.context = context;
        settingsSort = context.getSharedPreferences(SAVE_SETTINGS, Context.MODE_PRIVATE);
    }

    //сохранить тип языка
    public void saveLanguageWord(LanguageWord languageWord){

        SharedPreferences.Editor editor = settingsSort.edit();

        editor.putInt(LANGUAGE_WORD,languageWord.getNumEnum());
        editor.apply();
    }

    //сохранить тип сортировки
    public void saveSortWord(SortWord sortWord){

        SharedPreferences.Editor editor = settingsSort.edit();

        editor.putInt(SORT_WORD,sortWord.getNumEnum());
        editor.apply();
    }

    //выгрузить язык
    public LanguageWord loadSettingsLanguageWord(){

        LanguageWord languageWord = LanguageWord.ENGLISH;

        Log.d("prefs",settingsSort.getAll().toString());
        Log.d("savePref",settingsSort.getStringSet(SAVE_SETTINGS,new HashSet<>()).toString());

        int numEnum = settingsSort.getInt(LANGUAGE_WORD,-1);

        for(LanguageWord val: LanguageWord.values()){
            if(val.getNumEnum() == numEnum){
                languageWord = val;
                break;
            }
        }

        return languageWord;
    }

    //выгрузить тип сортировки
    public SortWord loadSettingsSortWord(){

        SortWord sortWord = SortWord.DEFAULT;

        Log.d("prefs",settingsSort.getAll().toString());
        Log.d("savePref",settingsSort.getStringSet(SAVE_SETTINGS,new HashSet<>()).toString());

        int numEnum = settingsSort.getInt(SORT_WORD,-1);

        for(SortWord val: SortWord.values()){
            if(val.getNumEnum() == numEnum){
                sortWord = val;
                break;
            }
        }

        return sortWord;
    }

}
