package com.example.bd.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bd.Logic.BDWords;
import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.SortWord;
import com.example.bd.Logic.Sorter;
import com.example.bd.Logic.Word;
import com.example.bd.R;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<com.example.bd.Fragments.RecycleAdapter.ViewHolder>{

    private LanguageWord languageWord;                  //язык сортировки
    private SortWord sortWord;                          //параметры сортировки сортировать (по дате, по умолчанию, по имени)
    private final LayoutInflater mLayoutInflater;       //привязывает все лайоуты (прямоугольники со словами к фрагменту)
    private String sortStartWords;                      //Если нужно отсортирвоать слово по определенным буквам

    private ArrayList<Word> words;
    private final Context context;
    private int lastPositionAppear = -1;

    private final BDWords bdWords;
    private final Fragment_Home.IGoneL IGoneLayout;

    public RecycleAdapter(Context ctx, ArrayList<Word> arr, Fragment_Home.IGoneL IGoneLayout) {
        mLayoutInflater = LayoutInflater.from(ctx);
        this.context = ctx;
        bdWords = new BDWords(ctx);
        this.IGoneLayout = IGoneLayout;

        languageWord = LanguageWord.ENGLISH;
        sortWord = SortWord.LAST_DATA;

        sortStartWords = "";
        setArrayMyData(arr);
    }


    @NonNull
    @Override
    public com.example.bd.Fragments.RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.list_design, parent, false);
        return new ViewHolder(view);
    }
    //static int cnt = 0;
    @Override
    public void onBindViewHolder(com.example.bd.Fragments.RecycleAdapter.ViewHolder holder, int position) {

       // Log.d("speed scroll",String.valueOf(cnt++));

        holder.itemView.setOnLongClickListener(v -> {
            int posit = holder.getLayoutPosition();

            long id_word = getItemId(posit);

            bdWords.delete(id_word);
            words.remove(posit);

            if(posit<getSizeArray()){
                IGoneLayout.setAllData(getItem(posit),posit);
            }else {//if(goneLayout.editingWord!=null){
                IGoneLayout.visible(false);
            }

            notifyItemRemoved(holder.getLayoutPosition());
            notifyItemRangeChanged(holder.getLayoutPosition(), words.size());

            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            return false;
        });

        holder.itemView.setOnClickListener(v -> {

            if(holder.getLayoutPosition()>=0){
                IGoneLayout.setAllData(getItem(holder.getLayoutPosition()),holder.getLayoutPosition());
                IGoneLayout.visible(true);
            }
        });


        Word wr = words.get(position);
        holder.id.setOnClickListener(v -> {
            wr.updatePriority();
            bdWords.update(wr);

            holder.id.setBackgroundColor(getColorByPriority(wr.getPriority()));
        });

        holder.id.setBackgroundColor(getColorByPriority(wr.getPriority()));
        holder.id.setText(String.valueOf(position + 1));

        languageSort(holder,wr);

        holder.setPos();

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
            animation.setDuration(250);
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
    public void setSortWord(SortWord sortWord) {
        this.sortWord = sortWord;
    }

    //задать язык сортировки
    public void setLanguageSortWord(LanguageWord languageWord) {
        this.languageWord = languageWord;
    }

    //задать массив с данными слов
    public void setArrayMyData(ArrayList<Word> arrayMyData) {
        this.words = arrayMyData;
        sortWord();
    }

    public int getSizeArray(){
        return words.size();
    }

    //Сортирвоать слово по дате или по умолчанию
    public void sortWord(){

        switch (sortWord){

            case FIRST_DATA:
                words.sort(Sorter.SORT_BY_FIRST_DATA);
                break;
            case LAST_DATA:
                words.sort(Sorter.SORT_BY_LAST_DATA);
                break;
            case NAME:
                words = Sorter.getWordsByStartSymbols(words, sortStartWords, languageWord);
                break;

        }
    }

    public void delete(Word word){
        words.remove(word);
    }

    public void updateById(Word word, int positionInList){
        //long []ids = words.
        words.set(positionInList,word);
    }

    //Получить элемент с массива со словами по индексу
    public Word getItem(int position) {

        return words.get(position);
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
    private void languageSort(com.example.bd.Fragments.RecycleAdapter.ViewHolder holder, Word wr){

        TextView ru= holder.ru;
        TextView en = holder.en;

        String eng = wr.getEnglishWord();
        String rus = wr.getRuWord();

        int maxLength =context.getResources().getInteger(R.integer.max_length_list);

        if(languageWord != LanguageWord.RUSSIAN){
            en.setText(constraintSizeWord(maxLength,eng));
            ru.setText(constraintSizeWord(maxLength,rus));
        }else {
            en.setText(constraintSizeWord(maxLength,rus));
            ru.setText(constraintSizeWord(maxLength,eng));
        }

    }
    //Задать приоритет слова (покрасить ячейку со словом сбоку, в соответствии с приоритетом)
    private int getColorByPriority(int priority) {
        Resources res = context.getResources();
        int color = 0;

        switch (priority) {
            case 0:
                color = 0;
                break;
            case 1:
                color= res.getColor(R.color.teal_A100,res.newTheme());
                break;
            case 2:
                color =  res.getColor(R.color.green_A100,res.newTheme());
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

        public void setPos(){
            id.setText(String.valueOf(getLayoutPosition()+1));
        }
    }
}