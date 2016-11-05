package com.drl.brandis.geschichtswerkstatt.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drl.brandis.geschichtswerkstatt.R;

/**
 * Created by lutz on 02/11/16.
 */

public class BaseActivity extends AppCompatActivity {

    private View overlay;


    // Storage Permissions
    protected static final int REQUEST_EXTERNAL_STORAGE = 1;
    protected static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // Record Permissions
    protected static final int REQUEST_AUDIO_RECORD = 2;
    protected static String[] PERMISSIONS_AUDIO_RECORD = {
            Manifest.permission.RECORD_AUDIO
    };

    public void showOverlay(String text, ViewGroup parent) {
        LayoutInflater inflater = this.getLayoutInflater();
        if (overlay == null)
            overlay = inflater.inflate(R.layout.overlay_spinner,parent,true);
        overlay.findViewById(R.id.spinner_overlay).setVisibility(View.VISIBLE);
        ((TextView) overlay.findViewById(R.id.spinner_text)).setText(text);
    }

    public void hideOverlay() {
        if (overlay != null)
            overlay.findViewById(R.id.spinner_overlay).setVisibility(View.GONE);
    }


    public void showAlert(final String title,final String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    showAlert("Error","This app requires to write audio and image file to external storage.");
                    finish();
                }
                return;
            }
            case REQUEST_AUDIO_RECORD: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    showAlert("Error","This app requires to record audio files.");
                    finish();
                }
                return;
            }
        }
    }

}
