package com.example.bd.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.example.bd.Activities.AddNewElem;
import com.example.bd.Logic.BDWords;
import com.example.bd.Logic.SortWord;
import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.Word;
import com.example.bd.R;
import com.example.bd.Logic.TxtToSpeech;
import com.example.bd.databinding.FragmentHomeBinding;


public class Fragment_Home extends Fragment{

    interface IGoneL {
        void setAllData(Word word,int positionInList);
        void visible(boolean isVisible);
    }


    private BDWords bdWords;                //БД слов
    private RecycleAdapter myAdapter;        //Адаптер для ListView (Отображает слова в виде списка)
    private GoneLayout goneLayout;          //Лайоут для изменения данных слов

    private final int ADD_ACTIVITY = 0;     //Для Add  activity requestCode
    private final int UPDATE_ACTIVITY = 1;        //onActivityResult requestCode

    private FragmentHomeBinding binding;    //Привязать View к фрагменту

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout layout = view.findViewById(R.id.gone_layout);

        goneLayout = new GoneLayout(layout);
        layout.setVisibility(View.GONE);

        // getContext().deleteDatabase("simple.db");
        Toast.makeText(getContext(), Arrays.toString(Objects.requireNonNull(getContext()).databaseList()), Toast.LENGTH_SHORT).show();

        // setContentView(R.layout.fragment_home);

        bdWords = new BDWords(getContext());

        // for (int i=0;i<50000;i++)bdWords.insert("hello" + i,"привет","-");

        //Лист для просмтора слов
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        myAdapter= new RecycleAdapter(getContext(),
                bdWords.selectAllFromEnd(),(GoneLayout)goneLayout);

        //Задать адаптер
        recyclerView.setAdapter(myAdapter);

        //По нажатию на кнопку добавить вызвать активность для создания нового слова
        Button add = view.findViewById(R.id.add);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddNewElem.class);
            goneLayout.visible(false);              //Если у нас слово какое нибудь озвучивается, то прекратить его озвучку

            startActivityForResult(intent, ADD_ACTIVITY);
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
        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"}) MenuItem.OnMenuItemClickListener on = item -> {
            switch (item.getItemId()){

                case R.id.language_en:
                    myAdapter.setLanguageSortWord(LanguageWord.ENGLISH);
                    break;
                case R.id.language_ru:
                    myAdapter.setLanguageSortWord(LanguageWord.RUSSIAN);
                    break;

                case R.id.sort_default:
                    myAdapter.setSortWord(SortWord.LAST_DATA);
                    myAdapter.sortWord();
                    break;
                case R.id.sort_data:
                    myAdapter.setSortWord(SortWord.FIRST_DATA);
                    myAdapter.sortWord();
                    break;
            }

            item.setChecked(true);
            myAdapter.notifyDataSetChanged();
            //updateList(0,myAdapter.getSizeArray());
            return false;
        };

        searchView = (SearchView) sortFind.getActionView();
        searchView.setQueryHint("Search");
        //для поискового элемента (лупа)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myAdapter.setSortStartWords(query);
                myAdapter.setSortWord(SortWord.NAME);
                goneLayout.visible(false);
                updateList();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(myAdapter.getSizeArray()<10000) {
                    myAdapter.setSortStartWords(newText);
                    myAdapter.setSortWord(SortWord.NAME);
                    goneLayout.visible(false);
                    updateList();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            sortDefault.setChecked(true);
            myAdapter.setSortWord(SortWord.LAST_DATA);
            updateList();
            return false;
        });

        sortEn.setOnMenuItemClickListener(on);
        sortRu.setOnMenuItemClickListener(on);
        sortData.setOnMenuItemClickListener(on);
        sortDefault.setOnMenuItemClickListener(on);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        goneLayout.visible(false);
    }

    //обновляем данные в листе
    @SuppressLint("NotifyDataSetChanged")
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


    private class GoneLayout implements IGoneL {

        private Word editingWord;                  //текущее слово, которое редактируем
        private int positionInList;
        private final LinearLayout editLayout;  //Лайоут, на котором васе редактирвоание происходит
        private final TxtToSpeech toSpeech;           //Класс, отвечабщий за произношение слова, нужен для озвучивания англ слова

        public GoneLayout(LinearLayout goneLayout){
            View view = getView();
            positionInList = 0;

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
            close.setOnClickListener(v -> visible(false));
            //Сохранить данные с полей ввода в бд и обновить лист для просмотра слов
            save.setOnClickListener(v -> {

                Word word = getWordFromEdit();

                if(word==null)return;

                int result =  bdWords.update(word);
                String msg;

                if(result>0) {
                    msg = "Saved";
                    myAdapter.updateById(word,positionInList);
                    myAdapter.notifyItemChanged(positionInList);
                }
                else
                    msg = "word was deleted";

                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
            });
            //Удалить слово с бд и обновить лист
            del.setOnClickListener(v -> {
                bdWords.delete(editingWord.getId());
                myAdapter.delete(editingWord);
                myAdapter.notifyItemRemoved(positionInList);
                //updateList(0,myAdapter.getSizeArray());

                LinearLayout edit = view.findViewById(R.id.gone_layout);
                if(edit!=null)edit.setVisibility(View.GONE);

                Toast.makeText(getContext(),"Deleted", Toast.LENGTH_SHORT).show();
            });
            //Начать проговаривание английского слова
            speak.setOnClickListener(v -> {
                if(en_edit!=null)
                    toSpeech.setText(en_edit.getText().toString());
                toSpeech.speakOut();
            });

        }
        //Прекратить проговаривание слова и закрыть GoneLayout
        @Override
        public void visible(boolean visible){
            if(visible){
                editLayout.setVisibility(View.VISIBLE);
            }
            else {
                toSpeech.silent();
                editLayout.setVisibility(View.GONE);
                editingWord = null;
            }
        }

        //заполнить все поля,которые содержат данные
        @Override
        public void setAllData(Word word,int positionInList) {
            editingWord = word;
            this.positionInList = positionInList;

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
}
