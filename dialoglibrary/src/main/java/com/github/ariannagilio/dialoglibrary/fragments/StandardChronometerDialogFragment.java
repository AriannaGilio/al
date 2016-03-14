package com.github.ariannagilio.dialoglibrary.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ariannagilio.dialoglibrary.R;
import com.github.ariannagilio.dialoglibrary.listeners.ChronometerListener;

public class StandardChronometerDialogFragment extends ChronometerDialogFragment {

    private TextView chronometer_text;
    private TextView milliseconds_text;
    private ImageButton start_button;
    private ImageButton pause_button;
    private ImageButton stop_button;
    private ImageButton reset_button;

    private ChronometerListener listener;

    public StandardChronometerDialogFragment() {
        // Required empty public constructor
    }

    public static StandardChronometerDialogFragment newInstance(ChronometerListener listener) {
        StandardChronometerDialogFragment fragment = new StandardChronometerDialogFragment();
        fragment.listener = listener;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_standard_chronometer_dialog, container, false);


        chronometer_text = (TextView) view.findViewById(R.id.chronometer_text);
        milliseconds_text = (TextView) view.findViewById(R.id.milliseconds_text);
        start_button = (ImageButton) view.findViewById(R.id.start_button);
        pause_button = (ImageButton) view.findViewById(R.id.pause_button);
        stop_button = (ImageButton) view.findViewById(R.id.stop_button);
        reset_button = (ImageButton) view.findViewById(R.id.reset_button);

        setChronometer_text(chronometer_text);
        setMilliseconds_text(milliseconds_text);
        setStart_button(start_button);
        setPause_button(pause_button);
        setStop_button(stop_button);
        setReset_button(reset_button);
        setChronometerListener(listener);

        setOnClickButtons();

        return view;
    }

}

