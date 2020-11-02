package com.uniovi.foxvid.vista.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;

public class StatisticsFragment extends Fragment {

    private Button btPost;
    private TextView txtPost;
    private Coordinate coordinate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root =  inflater.inflate(R.layout.fragment_statistics, container, false);
        return root;
    }

}