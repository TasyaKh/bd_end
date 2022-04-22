package com.example.bd.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.SortWordBy;
import com.example.bd.Logic.Sorter;
import com.example.bd.Logic.Word;
import com.example.bd.R;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{

    private LanguageWord languageWord;                  //язык сортировки
    private SortWordBy sortWordBy;                      //параметры сортировки сортировать (по дате, по умолчанию, по имени)
    private final LayoutInflater mLayoutInflater;       //привязывает все лайоуты (прямоугольники со словами к фрагменту)
    private String sortStartWords;                      //Если нужно отсортирвоать слово по определенным буквам

    private ArrayList<Word> words;
    private final Context context;
    private int lastPositionAppear = -1;

    public RecycleAdapter(Context ctx, ArrayList<Word> arr) {
        mLayoutInflater = LayoutInflater.from(ctx);
        this.context = ctx;
        //  languageWord = LanguageWord.ENGLISH;
        //sortWordBy = SortWordBy.DEFAULT;

        sortStartWords = "";
        setArrayMyData(arr);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.list_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Word word = words.get(position);
        String ru = word.getRuWord();
        String en = word.getEnglishWord();
        String pos = String.valueOf(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                words.remove(holder.getLayoutPosition());

                notifyItemRemoved(holder.getLayoutPosition());
                notifyItemRangeChanged(holder.getLayoutPosition(), words.size());
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    LinearLayout linearLayout = holder.itemView.findViewById(R.id.gone_layout);
                    if(linearLayout!=null) {
                       // goneLayout.setAllData(getItem(holder.getLayoutPosition()));
                        linearLayout.setVisibility(View.VISIBLE);
                    }
            }
        });

        Word wr = words.get(position);
        holder.id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wr.updatePriority();
               // bdWords.update(wr);

                holder.id.setBackgroundColor(getColorByPriority(wr.getPriority()));
            }
        });

        holder.id.setBackgroundColor(getColorByPriority(wr.getPriority()));
        holder.id.setText(String.valueOf(position + 1));

        languageSort(holder.itemView,wr);

        holder.setEn(en);
        holder.setRu(ru);
        holder.setPos(pos);

        setAnimation(holder.itemView, position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPositionAppear)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPositionAppear = position;
        }else{
            lastPositionAppear--;
        }
    }


    @Override
    public int getItemCount() {
        return words.size();
    }


    //задать слово, по которому сортируем лист
    public void setSortStartWords(String sortStartWords) {
        this.sortStartWords = sortStartWords;
    }
    //Задать сортировать по (дата, по умолчанию)
    public void setSortWordBy(SortWordBy sortWordBy) {
        this.sortWordBy = sortWordBy;
    }
    //задать язык сортировки
    public void setLanguageSortWord(LanguageWord languageWord) {
        this.languageWord = languageWord;
    }

    //задать массив с данными слов
    public void setArrayMyData(ArrayList<Word> arrayMyData) {
        this.words = arrayMyData;
        //sortWordBy();
    }

    public int getSizeArray(){
        return words.size();
    }

    //Сортирвоать слово по дате или по умолчанию
    private void sortWordBy(){
        Sorter sorter = new Sorter();

        switch (sortWordBy){

            case DATA:
                words.sort(sorter.getDataComparator());
                break;
            case NAME:
                words = sorter.getWordsByStartSymbols(words, sortStartWords, languageWord);
                break;

        }
    };

    //Получить элемент с массива со словами по индексу
    public Word getItem(int position) {
        Word wr = words.get(position);

        return wr;
    }
    //Получить id элепмента по индексу
    public long getItemId(int position) {
        Word wr = words.get(position);

        if (wr != null) {
            return wr.getId();
        }
        return 0;
    }

    //Ограничить размер слов, которые отображаются в ячейках
    private String constraintSizeWord(int maxLength, String word){
        if (word.length() > maxLength - 3)
            word = word.substring(0, maxLength - 4) + "...";
        return word;
    }
    //Задать язык сортировки
    private void languageSort(View convertView, Word wr){

        TextView ru= convertView.findViewById(R.id.ru);
        TextView en =convertView.findViewById(R.id.en);

        String eng = "";
        String rus = "";

        eng = wr.getEnglishWord();
        rus = wr.getRuWord();

        int maxLength = 50;

//        if(languageWord != LanguageWord.RUSSIAN){
//            en.setText(constraintSizeWord(maxLength,eng));
//            ru.setText(constraintSizeWord(maxLength,rus));
       // }else {
            en.setText(constraintSizeWord(maxLength,rus));
            ru.setText(constraintSizeWord(maxLength,eng));
        //}

    }
    //Задать приоритет слова (покрасить ячейку со словом сбоку, в соответствии с приоритетом)
    private int getColorByPriority(int priority) {
       // Resources res = getResources();
        int color = 0;

        switch (priority) {
            case 0:
                color = 0;
                break;
            case 1:
                color= Color.RED;
                break;
            case 2:
                color = Color.BLUE;
                break;
        }

        return color;
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView en;
        final TextView ru;
        final TextView id;

        ViewHolder(View view){
            super(view);
            en = view.findViewById(R.id.en);
            ru = view.findViewById(R.id.ru);
            id = view.findViewById(R.id.id_num);

        }

        public void setEn(String word){
            en.setText(word);
        }

        public void setRu(String word){
            ru.setText(word);
        }

        public void setPos(String word){
            id.setText(word);
        }
    }
}

