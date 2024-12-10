package com.example.shamobiles.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shamobiles.R;

public class SectionHeaderFragment extends Fragment {
    private static final String ARG_TITLE = "title";

    public static SectionHeaderFragment newInstance(String title) {
        SectionHeaderFragment fragment = new SectionHeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_section_header, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "";
        TextView headerText = view.findViewById(R.id.headerText);
        headerText.setText(title);
    }
} 