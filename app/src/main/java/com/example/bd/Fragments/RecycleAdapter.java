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

    private ArrayList<Word> words;                      //массив выводимых слов
    private final Context context;                      //контекст

    private int lastPositionAppear = -1;                //нужен чтобы анимация создания нового элемента правильно отображалась

    private final BDWords bdWords;                      //база данных слов
    private final Fragment_Home.IGoneL IGoneLayout;     //при удалении слова, передать просьбу о закрытии лайоута для редактиррвания слова

    private int selectedPos;                            //выделить блок, с которым работаем

    //инициализировать поля класса
    public RecycleAdapter(Context ctx, ArrayList<Word> arr, Fragment_Home.IGoneL IGoneLayout) {
        mLayoutInflater = LayoutInflater.from(ctx);
        this.context = ctx;
        bdWords = new BDWords(ctx);
        this.IGoneLayout = IGoneLayout;

        languageWord = LanguageWord.ENGLISH;
        sortWord = SortWord.DEFAULT;

        sortStartWords = "";

        selectedPos = RecyclerView.NO_POSITION;
        setArrayMyData(arr);
    }

    //задать дизайн нашему листу
    @NonNull
    @Override
    public com.example.bd.Fragments.RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.list_design, parent, false);
        return new ViewHolder(view);
    }

    //срабатывает при создании или изменении одного блока в листе
    @Override
    public void onBindViewHolder(com.example.bd.Fragments.RecycleAdapter.ViewHolder holder, int position) {

       // Log.d("speed scroll",String.valueOf(cnt++));
        holder.itemView.setSelected(selectedPos==position);

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

                notifyItemChanged(selectedPos);
                selectedPos = holder.getLayoutPosition();
                notifyItemChanged(selectedPos);
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
     * задать анимацию создания или обновления блока в листе
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


    //получить число элементов в списке words.size()
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

    //получить размер массива слов
    public int getSizeArray(){
        return words.size();
    }

    //Сортировать слово по дате или по умолчанию
    public void sortWord(){

        switch (sortWord){

            case FIRST_DATA:
                words.sort(Sorter.SORT_BY_FIRST_DATA);
                break;
            case DEFAULT:
                words.sort(Sorter.SORT_BY_LAST_DATA);
                break;
            case NAME:
                words = Sorter.getWordsByStartSymbols(words, sortStartWords, languageWord);
                break;
        }

        //поменять слово в редактируемом лайоует, т.к. список меняется и пользоваатель видит другую последовательность
        if(selectedPos>=0 && selectedPos<words.size())
            IGoneLayout.setAllData(words.get(selectedPos),selectedPos);
        else selectedPos = -1;
    }

    //удалить слово
    public void delete(Word word){
        words.remove(word);
    }

    //обновить слово по Id в листе
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
                color =  res.getColor(R.color.pink_A100,res.newTheme());
                break;
        }

        return color;
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView en;      //английское слово
        final TextView ru;      //русское слово
        final TextView id;      //id слова в листе (порядковый номер)

        ViewHolder(View view){
            super(view);
            en = view.findViewById(R.id.en);
            ru = view.findViewById(R.id.ru);
            id = view.findViewById(R.id.id_num);
        }

        //задать позицию (индекс слова)
        public void setPos(){
            id.setText(String.valueOf(getLayoutPosition()+1));
        }
    }
}