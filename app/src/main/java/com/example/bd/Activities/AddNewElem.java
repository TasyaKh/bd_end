package com.example.bd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bd.Logic.Word;
import com.example.bd.R;

public class AddNewElem extends Activity {
    private EditText ruWord,enWord; //поля для редактирования слов
    private long MyWordID;          //присвоить id слову

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ruWord = findViewById(R.id.russia_edit);
        enWord = findViewById(R.id.english_edit);
        //если что то в эту активность отправили
        if(getIntent().hasExtra("Word")){
            Word words=(Word)getIntent().getSerializableExtra("Word");
            ruWord.setText(words.getRuWord());
            ruWord.setText(words.getEnglishWord());

            MyWordID = words.getId();
        } //если не отправили ничего
        else
        {
            MyWordID = -1;
        }

        Button save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word(MyWordID,
                        ruWord.getText().toString().trim(),
                        enWord.getText().toString().trim(),"", Color.WHITE);

                Intent intent=getIntent();
                intent.putExtra("Words",word);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        enWord.addTextChangedListener(new TextChanged(enWord));
        ruWord.addTextChangedListener(new TextChanged(ruWord));
    }


    class TextChanged implements TextWatcher{
        private EditText editText;  //Какое поле проверяем

        public TextChanged(EditText editText){
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //При изменении текста проверить в соответствии с элементом на правильном ли языке введен символ
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (editText.getId()){
                case R.id.english_edit:
                    check(editText.getText().toString(),'A','z');
                    break;
                case R.id.russia_edit:
                    check(editText.getText().toString(),'А','я');
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        //Проверить в соответствии с ограничениями символ
        public void check(String txt,char min,char max){
            editText.setTextColor(getColor(R.color.light_green200));

            for(char c:txt.toCharArray()){
               // Log.d("Tag","c " + (int)c +"   min" + (int)min + "   max" + (int)max);
                Log.d("Tag","c " + (int)' '+"   min" + (int)'?' + "   max" + (int)'!');
                if((c>max || c< min) && (c < ' '  ||  c>'?')){
                    editText.setTextColor(getColor(R.color.deep_orange500));
                }
            }
        }
    }

}
