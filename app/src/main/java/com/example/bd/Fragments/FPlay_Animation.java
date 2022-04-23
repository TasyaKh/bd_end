package com.example.bd.Fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.bd.R;

public class FPlay_Animation {
    private final View view;          //View откуда будут бртаться элементы для анимации

    public FPlay_Animation(View view){
      this.view = view;
    }
    //изменить цвет ээлемента from to
    public void changeBackgroundColor(int colorFrom, int colorTo){

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000); // milliseconds
        //анимировать цвет
        colorAnimation.addUpdateListener(animator -> {
            HorizontalScrollView scrollView = view.findViewById(R.id.scrollWord);
            scrollView.setBackgroundColor((int) animator.getAnimatedValue());
        });

        colorAnimation.start();
    }
}
