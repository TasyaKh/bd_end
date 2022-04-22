package com.example.bd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bd.R;
import com.example.bd.databinding.FragmentInfoBinding;

public class Fragment_Info extends Fragment {

    private FragmentInfoBinding binding;    //Связать с View
    //Установить View
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
    //Отобразить содержимое (текст)
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        if(view==null) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        WebView info = view.findViewById(R.id.inform);

        if(info!=null) {
            String infString = getString(R.string.info);
            info.loadDataWithBaseURL(null, infString,"text/html","utf-8",null);
        }
    }

    //Уничтожить View
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
