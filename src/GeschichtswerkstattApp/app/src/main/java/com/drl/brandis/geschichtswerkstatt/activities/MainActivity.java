package com.drl.brandis.geschichtswerkstatt.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.drl.brandis.geschichtswerkstatt.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRecorderButtonClicked(View view) {
        Intent intent = new Intent(this, StoryListActivity.class);
        startActivity(intent);
    }
}
