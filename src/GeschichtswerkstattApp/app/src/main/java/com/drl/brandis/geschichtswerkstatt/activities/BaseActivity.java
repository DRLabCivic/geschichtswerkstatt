package com.drl.brandis.geschichtswerkstatt.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lutz on 02/11/16.
 */

public class BaseActivity extends AppCompatActivity {

    public void showAlert(final String title,final String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Damn!",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (title == "Error")
                        finish();
                }
            });
        alertDialog.show();
    }

}
