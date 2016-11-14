package com.drl.brandis.geschichtswerkstatt.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.drl.brandis.geschichtswerkstatt.R;
import com.drl.brandis.geschichtswerkstatt.database.Story;
import com.drl.brandis.geschichtswerkstatt.database.StoryDatabase;
import com.drl.brandis.geschichtswerkstatt.utils.StoryUploader;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends BaseActivity {

    private static final String LOG_TAG = "UploadActivity";

    private Call<ResponseBody> httpRequest = null;

    private Story story;

    private StoryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // hide done layout
        findViewById(R.id.done_layout).setVisibility(View.GONE);

        if (getIntent().hasExtra("story"))
            story = (Story) getIntent().getSerializableExtra("story");
        else
            showAlert("Error","Keine Geschichte zum upload ausgewählt",true);

        database = new StoryDatabase(getApplicationContext());


        uploadStory();
    }

    public void uploadStory() {

        httpRequest = StoryUploader.upload(this.story, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                hideOverlay();

                //delete story
                database.deleteStory(database.getWritableDatabase(), story);

                try {
                    Log.e(LOG_TAG,"Upload response: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Geschichte " + story.title + " wurde erfolgreich hochgeladen.", Toast.LENGTH_LONG).show();

                findViewById(R.id.uploading_layout).setVisibility(View.GONE);
                findViewById(R.id.done_layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showAlert("Fehler","Es konnte keine Verbindung zum Server hergestellt werden.",true);
            }
        });
    }

    public void onUrlClicked(View view) {

        TextView textview = (TextView) view;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(textview.getText().toString()));
        startActivity(browserIntent);
    }

    public void onBackButtonClicked(View view) {
        Intent intent = new Intent(this, StoryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
