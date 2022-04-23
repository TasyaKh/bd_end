package com.example.bd.Logic;

import android.content.Context;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

//TxtToSpeech
public class TxtToSpeech implements TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */

    private TextToSpeech tts;   //определяет настройки для проговаривания
    private Context context;    //контекст на котором говорение будет происходить
    private String text;        //что проговорить

    public TxtToSpeech(Context context){
        this.context = context;

        tts = new TextToSpeech(context, this);
    }
    //задать текст для проговаривания
    public void setText(String text){
        this.text = text;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }else{
                tts.setPitch(1.2f);
                tts.setSpeechRate(0.8f);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }
    //закрыть поток и заткнуть проговаривание
    public void close(){

       if(tts!=null){
           silent();
           tts.shutdown();
       }
    }
    //перестать говорить
    public void silent(){
        tts.stop();
    }
    //начать говорить
    public void speakOut() {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}