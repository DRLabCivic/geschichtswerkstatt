package com.drl.brandis.geschichtswerkstatt.activities;

import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.drl.brandis.geschichtswerkstatt.R;

import java.io.File;
import java.io.IOException;

import database.Story;
import recorder.SoundRecorder;
import recorder.SoundRecorderWav;

public class RecorderActivity extends BaseActivity {

    public static final int RECORDING_MAX_TIME = 1000 * 60 * 10;

    RecorderState state = RecorderState.INIT;

    CountDownTimer timer = null;

    SoundRecorder recorder;

    public enum RecorderState {
        INIT, RECORDING, STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        // init soundrecorder
        recorder = new SoundRecorderWav(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        resetRecording();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recorder.release();
    }

    public void startRecording() {

        //start Counting Time, set maximum time
        timer = new CountDownTimer(RECORDING_MAX_TIME, 25) {

            long startTime = System.currentTimeMillis();

            public void onTick(long millisUntilFinished) {
                long elapsedTime = System.currentTimeMillis() - startTime;
            }

            public void onFinish() {
                stopRecording();
            }
        }.start();

        try {
            recorder.prepare();
            recorder.startRecording();
            state = RecorderState.RECORDING;
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            stopRecording();
        }

        updateUi();
    }

    public void stopRecording() {

        //stop timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        try {
            recorder.stopRecording();
            state = RecorderState.STOPPED;
        } catch (IOException e) {
            showAlert("Error", e.getMessage());
        }

        updateUi();
    }

    public void resetRecording() {

        try {
            recorder.reset();
            state = RecorderState.INIT;
        } catch (IOException e) {
            showAlert("Error", e.getMessage());
        }

        updateUi();
    }

    public File saveRecording() {

        File file = null;

        try {
            file = recorder.save();
            recorder.reset();
        } catch (Exception e) {
            showAlert("Error",e.getMessage());
        }

        updateUi();

        return file;
    }

    private void updateUi() {

        TextView textView = (TextView) findViewById(R.id.text_view);

        if (state == RecorderState.RECORDING)
            textView.setText("Recording");
        else
            textView.setText("Stopped");
    }

    public void onRecordButtonClicked(View view) {
        if (state == RecorderState.INIT)
            startRecording();
        else if (state == RecorderState.RECORDING)
            stopRecording();
    }

    public void onSaveButtonClicked(View view) {
        if (state == RecorderState.STOPPED) {
            File file = saveRecording();
            if (file != null)
                Snackbar.make(view, "Recording saved to "+file.getAbsolutePath().toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }
}
