package com.example.bd.Logic;

import java.util.ArrayList;
import java.util.Comparator;

public class Sorter {

    //Сортировать по ранней дате
    public static Comparator<Word> SORT_BY_FIRST_DATA = (o1, o2) -> {
        long one = o1.getId();
        long two = o2.getId();
        return Long.compare(one, two);
    };

    public static  Comparator<Word>  SORT_BY_LAST_DATA= (o1, o2) -> {
        long one = o1.getId();
        long two = o2.getId();
        return Long.compare(two, one);
    };

    //Получить слово по его нучалу
    public static ArrayList<Word> getWordsByStartSymbols(ArrayList<Word> words, String startWords, LanguageWord languageWord){
        ArrayList<Word> sortWords = new ArrayList<>();

        if(languageWord == LanguageWord.ENGLISH){
            sortWords = getWordsByEn(words,startWords);
        }
        else if(languageWord == LanguageWord.RUSSIAN){
            sortWords = getWordsByRus(words,startWords);
        }

        return sortWords;
    }
    //Получить все слова по русскому началу слова
    private static ArrayList<Word> getWordsByRus(ArrayList<Word> words, String startWord){
        ArrayList<Word> sortWords = new ArrayList<>();

        for(Word word:words){
            String enWord = word.getRuWord();

            if(enWord.startsWith(startWord)){
                sortWords.add(word);
            }
        }
        return sortWords;
    }
    //Получить все слова по английскому началу слова
    private static ArrayList<Word> getWordsByEn(ArrayList<Word> words, String startWord){
        ArrayList<Word> sortWords = new ArrayList<>();

        for(Word word:words){
            String enWord = word.getEnglishWord();

            if(enWord.startsWith(startWord)){
                sortWords.add(word);
            }
        }
        return sortWords;
    }
    //получить компаратор, который сортирует WordStatistic по Количеству правильно угаданных слов
    public static  Comparator<WordStatistic>  SORT_BY_CORRECT= (o1, o2) -> {
            long one = o1.getCountCorrect() - (o1.getAllAttempts() - o1.getCountCorrect());
            long two = o2.getCountCorrect() - (o2.getAllAttempts() - o2.getCountCorrect());
            return Long.compare(two,one);
        };

}
