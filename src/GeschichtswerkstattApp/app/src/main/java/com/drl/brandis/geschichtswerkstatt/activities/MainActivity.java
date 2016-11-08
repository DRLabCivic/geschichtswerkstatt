package com.drl.brandis.geschichtswerkstatt.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.drl.brandis.geschichtswerkstatt.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRecorderButtonClicked(View view) {
        Intent intent = new Intent(this, StoryListActivity.class);
        startActivity(intent);
    }

    public void onScannerButtonClicked(View view) {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivity(intent);
    }
}
