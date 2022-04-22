package com.example.bd.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import com.example.bd.Activities.AddNewElem;
import com.example.bd.Logic.BDWords;
import com.example.bd.Logic.SortWordBy;
import com.example.bd.Logic.Sorter;
import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.Word;
import com.example.bd.R;
import com.example.bd.Logic.TxtToSpeech;
import com.example.bd.databinding.FragmentHomeBinding;


public class Fragment_Home extends Fragment{

    private BDWords bdWords;                //БД слов
    private myListAdapter myAdapter;        //Адаптер для ListView (Отображает слова в виде списка)
    private GoneLayout goneLayout;          //Лайоут для изменения данных слов

    private final int ADD_ACTIVITY = 0;     //Для Add  activity requestCode
    private int UPDATE_ACTIVITY = 1;        //onActivityResult requestCode

    private FragmentHomeBinding binding;    //Привязать View к фрагменту

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        goneLayout.gone();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout layout = view.findViewById(R.id.gone_layout);

        goneLayout = new GoneLayout(layout);
        layout.setVisibility(View.GONE);

        if(view==null)
            throw new NoSuchElementException("ONSTART_WHEREVIEW");

        // getContext().deleteDatabase("simple.db");
        Toast.makeText(getContext(), Arrays.toString(getContext().databaseList()), Toast.LENGTH_SHORT).show();

        // setContentView(R.layout.fragment_home);

        bdWords = new BDWords(getContext());

        //for (int i=0;i<5000;i++)bdWords.insert("train","train","-");

        ListView listView = view.findViewById(R.id.listView);
        myAdapter= new myListAdapter(getContext(),
                bdWords.selectAllFromEnd());

        listView.setAdapter(myAdapter);
        //По нажатию на любой элемент для просмотра слов вызывается GoneLayout для редактирования слова
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                myListAdapter adapter = (Fragment_Home.myListAdapter) parent.getAdapter();

                if(adapter!=null){

                    LinearLayout linearLayout = view.findViewById(R.id.gone_layout);
                    if(linearLayout!=null) {
                        goneLayout.setAllData(adapter.getItem(position));
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
                //                Toast.makeText(getApplicationContext(), ru.getText().toString() + " "  + en.getText().toString(),
//                        Toast.LENGTH_SHORT).show();
            }
        });

        //При долгом нажатии на элемент для просмотра слов он удаляется
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               // TextView id_elem = view.findViewById(R.id.id_num);

                if(view!=null){
                    myListAdapter adapter = (myListAdapter) parent.getAdapter();
                    long id_word = adapter.getItemId(position);

                    bdWords.delete(id_word);

                    updateList();

                    if(position<myAdapter.getSizeArray()){
                        goneLayout.setAllData(adapter.getItem(position));
                    }
                    else if(goneLayout.editingWord !=null){
                        goneLayout.gone();
                    }

                }
                Toast.makeText(getContext(),"element removed",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //По нажатию на кнопку добавить вызвать активность для создания нового слова
        Button add = view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewElem.class);
                goneLayout.gone();              //Если у нас слово какое нибудь озвучивается, то прекратить его озвучку
                startActivityForResult(intent, ADD_ACTIVITY);
            }
        });
    }

    //Создать меню опций
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_fragment_home,menu);

        super.onCreateOptionsMenu(menu, inflater);
        MenuCompat.setGroupDividerEnabled(menu,true);

        MenuItem sortFind = menu.findItem(R.id.find);
        SearchView searchView ;

        MenuItem sortEn = menu.findItem(R.id.language_en);
        MenuItem sortRu = menu.findItem(R.id.language_ru);
        MenuItem sortData = menu.findItem(R.id.sort_data);
        MenuItem sortDefault = menu.findItem(R.id.sort_default);

        //По нажатию на любой элемент в меню
        MenuItem.OnMenuItemClickListener on = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.language_en:
                        myAdapter.setLanguageSortWord(LanguageWord.ENGLISH);
                        break;
                    case R.id.language_ru:
                        myAdapter.setLanguageSortWord(LanguageWord.RUSSIAN);
                        break;

                    case R.id.sort_default:
                        myAdapter.setSortWordBy(SortWordBy.DEFAULT);
                        break;
                    case R.id.sort_data:
                        myAdapter.setSortWordBy(SortWordBy.DATA);
                        break;

                }

                item.setChecked(true);
                updateList();
                return false;
            }
        };

        searchView = (SearchView) sortFind.getActionView();
        searchView.setQueryHint("Search");
        //для поискового элемента (лупа)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.setSortWordBy(SortWordBy.NAME);
                myAdapter.setSortStartWords(newText);
                goneLayout.gone();
                updateList();
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sortDefault.setChecked(true);
                return false;
            }
        });

        sortEn.setOnMenuItemClickListener(on);
        sortRu.setOnMenuItemClickListener(on);
        sortData.setOnMenuItemClickListener(on);
        sortDefault.setOnMenuItemClickListener(on);
    }
    //обновляем данные в листе
    private void updateList () {

        myAdapter.setArrayMyData(bdWords.selectAllFromEnd());
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Word word = (Word) data.getExtras().getSerializable("Words");
            //если хоть что-то о написано
            if (!word.getRuWord().equals("") && !word.getEnglishWord().equals("")) {
                if (requestCode == UPDATE_ACTIVITY)
                    bdWords.update(word);
                else
                    bdWords.insert(word.getRuWord(), word.getEnglishWord(),word.getDescription());
                updateList();
            }
        }
    }


    private class GoneLayout{

        private Word editingWord;                  //текущее слово, которое редактируем
        private final LinearLayout editLayout;  //Лайоут, на котором васе редактирвоание происходит
        private TxtToSpeech toSpeech;           //Класс, отвечабщий за произношение слова, нужен для озвучивания англ слова

        public GoneLayout(LinearLayout goneLayout){
            View view = getView();

            if(view==null)
                throw new NoSuchElementException("ONSTART_WHEREVIEW");

            if(goneLayout!=null) {
                this.editLayout = goneLayout;
            }
            else throw new NoSuchElementException(" LinearLayout goneLayout not find");


            EditText en_edit =  view.findViewById(R.id.english_edit);
            toSpeech = new TxtToSpeech(getContext());
            editingWord = null;



            ImageButton close = goneLayout.findViewById(R.id.close);
            ImageButton save = goneLayout.findViewById(R.id.save);
            ImageButton del = goneLayout.findViewById(R.id.delete);
            ImageButton speak = goneLayout.findViewById(R.id.speak);

            //закрыть GoneLayout и перкратить озвучивание слова
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gone();
                }
            });
            //Сохранить данные с полей ввода в бд и обновить лист для просмотра слов
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Word word = getWordFromEdit();

                    if(word==null)return;

                    int result =  bdWords.update(word);
                    String msg = "";

                    if(result>0) {
                        msg = "Saved";
                        updateList();
                    }
                    else
                        msg = "word was deleted";

                    Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
                }
            });
            //Удалить слово с бд и обновить лист
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bdWords.delete(editingWord.getId());
                    updateList();

                    LinearLayout edit = view.findViewById(R.id.gone_layout);
                    if(edit!=null)edit.setVisibility(View.GONE);

                    Toast.makeText(getContext(),"Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            //Начать проговаривание английского слова
            speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(en_edit!=null)
                        toSpeech.setText(en_edit.getText().toString());
                    toSpeech.speakOut();
                }
            });

        }
        //Прекратить проговаривание слова и закрыть GoneLayout
        public void gone(){
            toSpeech.silent();
            editLayout.setVisibility(View.GONE);
            editingWord = null;
        }
        //Прекратить проговаривание слова
        public void close(){
            toSpeech.close();
        }
        //заполнить все поля,которые содержат данные
        public void setAllData(Word word) {
            editingWord = word;

            if(editLayout!=null){
                EditText rus = editLayout.findViewById(R.id.russia_edit);
                EditText eng = editLayout.findViewById(R.id.english_edit);
                EditText descipt = editLayout.findViewById(R.id.description_edit);

                rus.setText(word.getRuWord());
                eng.setText(word.getEnglishWord());
                descipt.setText(word.getDescription());
            }
        }

        //Сформировать объект слова из полей и вернуть его
        private Word getWordFromEdit(){

            EditText ru = editLayout.findViewById(R.id.russia_edit);
            EditText en = editLayout.findViewById(R.id.english_edit);
            EditText descript = editLayout.findViewById(R.id.description_edit);
            Word word = null;

            if(editingWord !=null){
                word = new Word(
                        editingWord.getId(),
                        en.getText().toString().trim(),
                        ru.getText().toString().trim(),
                        descript.getText().toString().trim(),
                        editingWord.getPriority());
            }
            return word;
        }

    }


    private class myListAdapter extends BaseAdapter {

        private LanguageWord languageWord;                  //язык сортировки
        private SortWordBy sortWordBy;                      //параметры сортировки сортировать (по дате, по умолчанию, по имени)
        private final LayoutInflater mLayoutInflater;       //привязывает все лайоуты (прямоугольники со словами к фрагменту)
        private ArrayList<Word> arrayMyWords;               //массив сос словами
        private String sortStartWords;                      //Если нужно отсортирвоать слово по определенным буквам

        public myListAdapter(Context ctx, ArrayList<Word> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);

            languageWord = LanguageWord.ENGLISH;
            sortWordBy = SortWordBy.DEFAULT;

            sortStartWords = "";
            setArrayMyData(arr);

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
            this.arrayMyWords = arrayMyData;
            sortWordBy();
        }

//        public LanguageWord getTypeSortWord(LanguageWord languageWord) {
//            return languageWord;
//        }
//
//        public LanguageWord getLanguageSortWord() {
//            return languageWord;
//        }
//
//        public ArrayList<Word> getArrayMyData() {
//            return arrayMyWords;
//        }

        public int getSizeArray(){
            return arrayMyWords.size();
        }
        //Сортирвоать слово по дате или по умолчанию
        private void sortWordBy(){
            Sorter sorter = new Sorter();

            switch (sortWordBy){

                case DATA:
                    arrayMyWords.sort(sorter.getDataComparator());
                    break;
                case NAME:
                    arrayMyWords = sorter.getWordsByStartSymbols(arrayMyWords, sortStartWords, languageWord);
                    break;

            }
        };
        //получить размер массива со словами
        public int getCount() {
            return arrayMyWords.size();
        }
        //Получить элемент с массива со словами по индексу
        public Word getItem(int position) {
            Word wr = arrayMyWords.get(position);

            return wr;
        }
        //Получить id элепмента по индексу
        public long getItemId(int position) {
            Word wr = arrayMyWords.get(position);

            if (wr != null) {
                return wr.getId();
            }
            return 0;
        }
        //Получить Layout, где прописывабтся слова
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_design, null);
            }


            TextView id = convertView.findViewById(R.id.id_num);

            Word wr = arrayMyWords.get(position);
            //Сбоку поставить цвет ячеййки в соответствии с приоритетом слова
            id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wr.updatePriority();
                    bdWords.update(wr);

                    id.setBackgroundColor(getColorByPriority(wr.getPriority()));
                }
            });

            id.setBackgroundColor(getColorByPriority(wr.getPriority()));
            id.setText(String.valueOf(position + 1));

            languageSort(convertView,wr);

            return convertView;
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

            int maxLength = getResources().getInteger(R.integer.max_length_list);

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
            Resources res = getResources();
            int color = 0;

            switch (priority) {
                case 0:
                    color = 0;
                    break;
                case 1:
                   color= res.getColor(R.color.teal_A100, res.newTheme());
                    break;
                case 2:
                    color = res.getColor(R.color.green_A100, res.newTheme());
                    break;
            }

            return color;
        }
    } // end myAdapter
}
