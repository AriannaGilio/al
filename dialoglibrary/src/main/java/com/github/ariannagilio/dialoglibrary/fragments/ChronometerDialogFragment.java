package com.github.ariannagilio.dialoglibrary.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ariannagilio.dialoglibrary.listeners.ChronometerListener;

import java.lang.ref.WeakReference;

public abstract class ChronometerDialogFragment extends DialogFragment {

    ChronometerListener chronometerListener;

    private TextView chronometer_text;
    private TextView milliseconds_text;

    private ImageButton start_button;
    private ImageButton pause_button;
    private ImageButton stop_button;
    private ImageButton reset_button;

    private boolean pause = false;
    private boolean reset = false;

    public static final int HH_MM_SS_Format = 0;
    public static final int MM_SS_Format = 1;
    public static final int HH_MM_Format = 2;

    private int selectedFormat = 0;

    private static final String TIME_SAVING_KEY = "timeSaving";
    private static final String LAST_BUTTON_PLAY = "lastButtonPlay";

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs,msecs;

    private boolean continueChronometer  = false;
    private SharedPreferences preferences;
    private WeakReference<Long> chronometerTime;

    public ChronometerDialogFragment() {
        // Required empty public constructor
    }

    public void setOnClickButtons() {
        if (start_button != null) {
            start_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (continueChronometer) {
                        if (!preferences.getBoolean(LAST_BUTTON_PLAY, false)) {
                            elapsedTime = 0;
                        } else {
                            elapsedTime = System.currentTimeMillis() - preferences.getLong(TIME_SAVING_KEY, 0); //FIXME:?
                        }
                        preferences.edit().putBoolean(LAST_BUTTON_PLAY, true).commit();
                    }
                    startTime = System.currentTimeMillis() - elapsedTime;
                    mHandler.removeCallbacks(startTimer);
                    mHandler.postDelayed(startTimer, 0);
                    if (hasPause()) {
                        start_button.setVisibility(View.GONE);
                        pause_button.setVisibility(View.VISIBLE);
                    }
                    chronometerListener.startChronometer();
                }
            });
        }

        if (pause_button != null) {
            pause_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTime = System.currentTimeMillis();
                    mHandler.removeCallbacks(startTimer);
                    start_button.setVisibility(View.VISIBLE);
                    pause_button.setVisibility(View.GONE);
                    chronometerListener.pauseChronometer();
                }
            });
        }

        if (stop_button != null) {
            stop_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.removeCallbacks(startTimer);
                    startTime = System.currentTimeMillis();
                    elapsedTime = 0;
                    if (continueChronometer) {
                        preferences.edit().putLong(TIME_SAVING_KEY, System.currentTimeMillis()).commit();
                        preferences.edit().putBoolean(LAST_BUTTON_PLAY, false).commit();
                    }
                    if (hasPause()) {
                        start_button.setVisibility(View.VISIBLE);
                        pause_button.setVisibility(View.GONE);
                    }
                    chronometerListener.stopChronometer();
                }
            });
        }

        if (reset_button != null) {
            reset_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTime = System.currentTimeMillis();
                    mHandler.removeCallbacks(startTimer);
                    mHandler.postDelayed(startTimer, 0);
                    if (hasPause()) {
                        start_button.setVisibility(View.GONE);
                        pause_button.setVisibility(View.VISIBLE);
                    }
                    chronometerListener.restartChronometer();
                }
            });
        }
    }

    public void show_HH_MM_SS(int selectedFormat) {
        this.selectedFormat = selectedFormat;
    }

    public void continuousChronometer(boolean continueChronometer, String preferencesName) {
        this.continueChronometer = continueChronometer;
        if (preferencesName != null) {
            preferences = getActivity().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        }
        setRetainInstance(continueChronometer);
    }

    public long getChronometerTime() {
        return elapsedTime;
    }

    public ChronometerListener getChronometerListener() {
        return chronometerListener;
    }

    public void setChronometerListener(ChronometerListener chronometerListener) {
        this.chronometerListener = chronometerListener;
    }

    public TextView getChronometer_text() {
        return chronometer_text;
    }

    public void setChronometer_text(TextView chronometer_text) {
        this.chronometer_text = chronometer_text;
    }

    public TextView getMilliseconds_text() {
        return milliseconds_text;
    }

    public void setMilliseconds_text(TextView milliseconds_text) {
        this.milliseconds_text = milliseconds_text;
    }

    public ImageButton getStart_button() {
        return start_button;
    }

    public void setStart_button(ImageButton start_button) {
        this.start_button = start_button;
        start_button.setVisibility(View.VISIBLE);
    }

    public ImageButton getPause_button() {
        return pause_button;
    }

    public void setPause_button(ImageButton pause_button) {
        this.pause_button = pause_button;
        pause_button.setVisibility(View.GONE);
        setPause(true);
    }

    public ImageButton getStop_button() {
        return stop_button;
    }

    public void setStop_button(ImageButton stop_button) {
        this.stop_button = stop_button;
    }

    public ImageButton getReset_button() {
        return reset_button;
    }

    public void setReset_button(ImageButton reset_button) {
        this.reset_button = reset_button;
        setReset(true);
    }

    public boolean hasPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean hasReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    private void updateTimer (float time){
        msecs = (long) time;
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);
        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0){
            seconds = "00";
        }
        if (secs <10 && secs > 0){
            seconds = "0" + seconds; }
        mins = mins % 60;
        minutes = String.valueOf(mins);
        if (mins == 0){
            minutes = "00";
        }
        if (mins <10 && mins > 0){
            minutes = "0" + minutes;
        }
        hours = String.valueOf(hrs);
        if (hrs == 0){
            hours = "00";
        }
        if (hrs <10 && hrs > 0){
            hours = "0" + hours;
        }
        milliseconds = String.valueOf((long)time);
        if (milliseconds.length()==2){
            milliseconds = "0" + milliseconds;
        }
        if (milliseconds.length()<=1){
            milliseconds = "00";
        }
        if (milliseconds.length() >= 3) {
            milliseconds = milliseconds.substring(milliseconds.length() -  3);
        }
        //milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

        String timeToShow = "00:00:00";
        switch (selectedFormat) {
            case HH_MM_SS_Format:
                timeToShow = hours + ":" + minutes + ":" + seconds;
                break;
            case MM_SS_Format:
                timeToShow = minutes + ":" + seconds;
                break;
            case HH_MM_Format:
                timeToShow = hours + ":" + minutes;
                break;
        }
        chronometer_text.setText(timeToShow);
        if (milliseconds_text != null) {
            milliseconds_text.setText("." + milliseconds);
        }
    }

    private Runnable startTimer = new Runnable() {
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            chronometerTime = new WeakReference<Long>(startTime);
            if (continueChronometer) {
                preferences.edit().putLong(TIME_SAVING_KEY, startTime).commit();
            }
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (continueChronometer && preferences != null && preferences.getBoolean(LAST_BUTTON_PLAY, false)) {
            elapsedTime = System.currentTimeMillis() - preferences.getLong(TIME_SAVING_KEY, 0);
            startTime = System.currentTimeMillis() - elapsedTime;
            //mHandler.removeCallbacks(startTimer);
            mHandler.postDelayed(startTimer, 0);
            if (pause_button != null) {
                start_button.setVisibility(View.GONE);
                pause_button.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(startTimer); //TODO: giusto???
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && chronometerTime.get() != null) {
            outState.putLong(TIME_SAVING_KEY, chronometerTime.get());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            chronometerTime = new WeakReference<Long>(savedInstanceState.getLong(TIME_SAVING_KEY));
            elapsedTime = chronometerTime.get();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            chronometer_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 90);
            if (milliseconds_text != null) {
                milliseconds_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            }
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            chronometer_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70);
            if (milliseconds_text != null) {
                milliseconds_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            }
        }
    }
}
