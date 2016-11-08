package com.drl.brandis.geschichtswerkstatt.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.drl.brandis.geschichtswerkstatt.R;

import java.io.File;
import java.io.IOException;

import com.drl.brandis.geschichtswerkstatt.recorder.SoundRecorder;
import com.drl.brandis.geschichtswerkstatt.recorder.SoundRecorderWav;
import com.drl.brandis.geschichtswerkstatt.views.WaveformView;

public class RecorderActivity extends BaseActivity {

    public static final int RECORDING_MAX_TIME = 1000 * 60 * 10;

    RecorderState state = RecorderState.INIT;

    CountDownTimer timer = null;

    SoundRecorder recorder;
    WaveformView waveformView;

    public enum RecorderState {
        INIT, RECORDING, STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        // request permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_AUDIO_RECORD, REQUEST_AUDIO_RECORD);
        }

        // init soundrecorder
        recorder = new SoundRecorderWav(getApplicationContext());
        waveformView = new WaveformView(this);
        recorder.setAudioBufferCallback(new SoundRecorder.AudioBufferCallback() {
            @Override
            public void onNewData(byte[] buffer) {
                waveformView.updateAudioData(buffer);
            }
        });

        ((FrameLayout)findViewById(R.id.waveform_layout)).addView(waveformView);

        updateUi();
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

        // request permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

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

        return file;
    }

    private void updateUi() {

        TextView textView = (TextView) findViewById(R.id.text_view);
        View saveButton = findViewById(R.id.save_button);
        View cancelButton = findViewById(R.id.cancel_button);

        if (state == RecorderState.RECORDING) {
            textView.setText("Recording");
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
        } else if (state == RecorderState.STOPPED) {
            textView.setText("Stopped");
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            textView.setText("Ready");
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        }
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
            if (file != null) {
                Intent intent = new Intent();
                intent.putExtra("recording",file);
                setResult(Activity.RESULT_OK,intent);
                finish();
            } else
                finish();
        }
    }

    public void onCancelButtonClicked(View view) {
        finish();
    }
}
