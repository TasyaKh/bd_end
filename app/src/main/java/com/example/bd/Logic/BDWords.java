package com.example.bd.Logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BDWords {

        private static final String DATABASE_NAME = "simple.db";            //имя БД
        private static final int DATABASE_VERSION = 1;                      //Версия БД

        private static final String TABLE_NAME = "Words";                   //Имя таблицы

        private static final String COLUMN_ID = "id";                       //столбец ид
        private static final String COLUMN_WORD = "Word";                   //столбец слово
        private static final String COLUMN_TRANSLATE= "Translate";          //столбец перевод
        private static final String COLUMN_DESCRIPTION = "Description";     //столбец описание
        private static final String COLUMN_PRIORITY = "Priority";           //столбец приоритет

        private static final int NUM_COLUMN_ID = 0;                         //столбец ид номер
        private static final int NUM_COLUMN_WORD = 1;                       //столбец слово номер
        private static final int NUM_COLUMN_TRANSLATE = 2;                  //столбец перевод номер
        private static final int NUM_COLUMN_DESCRIPTION= 3;                 //столбец описание номер
        private static final int NUM_COLUMN_PRIORITY = 4;                   //столбец приоритет номер

        private final SQLiteDatabase mDataBase;   //запросы к бд делает (бд сюда сохранить)

        public BDWords(Context context) {
            OpenHelper mOpenHelper = new OpenHelper(context); //создать базу данных
            mDataBase = mOpenHelper.getWritableDatabase();    //передаем созданную бузу данных для дальнейшего управления
        }

        public long insert(String word,String translate,String description) { //вставить данные в бд
            ContentValues cv=new ContentValues();          //хранит данные
            cv.put(COLUMN_WORD, word);                     //положить первую колонку
            cv.put(COLUMN_TRANSLATE, translate);           //во вторую
            cv.put(COLUMN_DESCRIPTION, description);       //хранит данные
            cv.put(COLUMN_PRIORITY, 0);                    //цвет слова

            return mDataBase.insert(TABLE_NAME, null, cv); //вставить эту сформированную строку в таблицу
        }


        public int update(Word wr) {   //обновить поле
            ContentValues cv = new ContentValues(); //та же ситуация, что и с Insert
            cv.put(COLUMN_WORD, wr.getEnglishWord());
            cv.put(COLUMN_TRANSLATE,wr.getRuWord());
            cv.put(COLUMN_DESCRIPTION, wr.getDescription());
            cv.put(COLUMN_PRIORITY, wr.getPriority());

            //mDataBase.query(TABLE_NAME,null,"id = " + wr.getId(),null,null,null,null);
            return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(wr.getId())});
        }

        public void deleteAll() {   //снести все к чертям
            mDataBase.delete(TABLE_NAME, null, null);
        }

        public void delete(long id) { //удалить по id
            mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        }

        public Word select(long id) { //выбрать строку по id
            //ищет строку (некий курсор на строку ставим)
            Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

            mCursor.moveToFirst();
            String ruWord = mCursor.getString(NUM_COLUMN_WORD);
            String enWord = mCursor.getString(NUM_COLUMN_TRANSLATE);
            String description  = mCursor.getString(NUM_COLUMN_DESCRIPTION);
            int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);

            return new Word(id, ruWord, enWord,description,priority);
        }

        public long countRows(){   //посчитать количество строк в таблице

           return DatabaseUtils.queryNumEntries(mDataBase,TABLE_NAME);
        }

//        public Word selectRaw(long rawTo){   //Выбрать строку
//            if(rawTo<0)
//                return null;
//
//            long count = 0;
//            Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
//
//            mCursor.moveToFirst();
//            while (count < rawTo) {
//                mCursor.moveToNext();
//                count++;
//            }
//
//            long id = mCursor.getLong(NUM_COLUMN_ID);
//            String ruWord = mCursor.getString(NUM_COLUMN_WORD);
//            String enWord  = mCursor.getString(NUM_COLUMN_TRANSLATE);
//            String description  = mCursor.getString(NUM_COLUMN_DESCRIPTION);
//            int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);
//
//            Word word = new Word(id, ruWord, enWord , description, priority);
//            return word;
//        }

        public ArrayList<Word> selectAllFromEnd() { //выбрать все данные с конца таблицы
            Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

            ArrayList<Word> arr = new ArrayList<>();
            mCursor.moveToLast();
            if (!mCursor.isBeforeFirst()) {
                do {
                    long id = mCursor.getLong(NUM_COLUMN_ID);
                    String ruWord = mCursor.getString(NUM_COLUMN_WORD);
                    String enWord  = mCursor.getString(NUM_COLUMN_TRANSLATE);
                    String description  = mCursor.getString(NUM_COLUMN_DESCRIPTION);
                    int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);

                    arr.add(new Word(id, ruWord, enWord , description, priority));
                } while (mCursor.moveToPrevious());
            }
            return arr;
        }

    public ArrayList<Word> selectPriorityWords() { //выбрать слова, которые имеют приоритет
        Cursor mCursor = mDataBase.query(TABLE_NAME, null,
                COLUMN_PRIORITY + " > 0", null,
                null, null, null);

        ArrayList<Word> arrPriority = new ArrayList<>();

        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String ruWord = mCursor.getString(NUM_COLUMN_WORD);
                String enWord = mCursor.getString(NUM_COLUMN_TRANSLATE);
                String description = mCursor.getString(NUM_COLUMN_DESCRIPTION);
                int priority = mCursor.getInt(NUM_COLUMN_PRIORITY);

                arrPriority.add(new Word(id, ruWord, enWord, description, priority));
            } while (mCursor.moveToNext());
        }

        return arrPriority;
    }

        //Создать бд
        private static class OpenHelper extends SQLiteOpenHelper {

            public OpenHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                String query = "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_WORD+ " TEXT, " +
                        COLUMN_TRANSLATE + " TEXT, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_PRIORITY + " INTEGER);";


                db.execSQL(query);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }

        }
}
