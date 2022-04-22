package com.example.bd.Logic;

import java.util.ArrayList;
import java.util.Random;

public class PlayWithWords {

    public enum WordSPriority{
        ALL, PRIORITY,
    }
    private WordStatistic currentWord;              //Текущее выпавшее слово

    private BDWords bdWords;                         //БД слов
    private ArrayList<WordStatistic> priorityWords;  //Приоритетные слова
    private int indexCurrentWord;                    //индекс текущего слова
    private WordSPriority wordSPriority;              //Тип приоритетности слов
    //private int uncorrectMax;
    private int correctMin;                          //минимальное количество правильных слов

    private ArrayList<WordStatistic> deletedCorrectWords; //удаленные правильные слова

    public PlayWithWords(BDWords bdWords){
        this.bdWords = bdWords;
        refillWords();
        indexCurrentWord = 0;
        wordSPriority = WordSPriority.PRIORITY;
       // uncorrectMax = 4;
        currentWord = null;
        correctMin =3;

        deletedCorrectWords = new ArrayList<>();
    }
    //Получить текущее слово
    public WordStatistic getCurrentWord() {
        return currentWord;
    }
    //Получить приоритет
    public WordSPriority getWordSPriority() {
        return wordSPriority;
    }
    //Задать приоритет
    public void setWordSPriority(WordSPriority wordSPriority) {
        this.wordSPriority = wordSPriority;
    }
    //Преобразовать слово в WordStatistic
    private ArrayList<WordStatistic> fromWordIntoWordStatistic(ArrayList<Word> words){
        ArrayList<WordStatistic> statistic = new ArrayList<>();

        for(Word word:words){
            WordStatistic wordStatistic = new WordStatistic(word);
            statistic.add(wordStatistic);
        }

        return statistic;
    }
    //Получить случайный int
    public int getRandomInt(int max){
        Random random = new Random();

        return random.nextInt(max);
    }
    //Перезаполнить слова
    public void refillWords(){
        ArrayList<Word> words = new ArrayList<>();

        if(wordSPriority==WordSPriority.PRIORITY){
            words = bdWords.selectPriorityWords(); //SLOWLY
        }else if(wordSPriority==WordSPriority.ALL){
            words = bdWords.selectAllFromEnd();    //SLOWLY
        }

        priorityWords = fromWordIntoWordStatistic(words); //SLOWLY
        deletedCorrectWords = new ArrayList<>();

    }
    //Получить случайное слово
    public void setRandomWord(){

        if(priorityWords!=null && priorityWords.size()>0) {
            int randRaw = getRandomInt(priorityWords.size());
            this.currentWord = priorityWords.get(randRaw);

            indexCurrentWord =randRaw;
        }else currentWord = null;
    }

    //Обновить статистику слова и состояние массивов, которые содержат слова
    public boolean upgradeWordsStatistic(boolean isCorrect){

        if(indexCurrentWord>=priorityWords.size()) {
            return false;
        }

        WordStatistic statistic = priorityWords.get(indexCurrentWord);
        statistic.updateStatistic(isCorrect);

        if(statistic.getCountCorrect()>=correctMin &&
                statistic.getAllAttempts()-statistic.getCountCorrect()<=statistic.getCountCorrect() - correctMin){
           // correctStatistic.append(statistic.toString()).append("/n");

            deletedCorrectWords.add(priorityWords.get(indexCurrentWord));
            priorityWords.remove(indexCurrentWord);
        }
        return true;
    }

    public ArrayList<WordStatistic> getCorrectWords() {
        return deletedCorrectWords;
    }

    public ArrayList<WordStatistic> getPriorityWords() {
        return priorityWords;
    }

    public int getSizePriorityWords(){
        return priorityWords.size();
    }

}
