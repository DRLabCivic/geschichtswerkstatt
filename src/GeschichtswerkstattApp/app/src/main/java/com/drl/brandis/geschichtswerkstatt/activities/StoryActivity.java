package com.drl.brandis.geschichtswerkstatt.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.drl.brandis.geschichtswerkstatt.R;

import database.Story;
import database.StoryDatabase;

public class StoryActivity extends AppCompatActivity {

    public static int RECORD_REQUEST_CODE = 1;

    Story story;
    TextView titleEdit;
    TextView textEdit;

    private boolean deletedStory = false;

    private StoryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);


        // setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("story"))
            story = (Story) getIntent().getSerializableExtra("story");
        else
            story = new Story();

        if (story._id > 0)
            getSupportActionBar().setTitle("Geschichte #" + story._id);
        else
            getSupportActionBar().setTitle("Neue Geschichte");

        titleEdit = (TextView) findViewById(R.id.edit_text_title);
        textEdit = (TextView) findViewById(R.id.edit_text_text);

        database = new StoryDatabase(getApplicationContext());

        updateUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deletedStory)
            saveStory();
    }

    protected void updateUi() {
        titleEdit.setText(story.title);
        textEdit.setText(story.text);
    }

    public void saveStory() {
        // get values
        story.title = titleEdit.getText().toString();
        story.text = textEdit.getText().toString();

        // add to database
        Long id = database.upsertStory(database.getWritableDatabase(), story);
        database.close();
    }

    public void onRecordButtonClicked(View view) {

        //start recorder activity
        Intent intent = new Intent(getApplicationContext(), RecorderActivity.class);
        startActivityForResult(intent, RECORD_REQUEST_CODE);
    }

    public void onImageButtonClicked(View view) {

    }

    public void onLocationButtonClicked(View view) {

    }

    public void onDeleteButtonClicked(View view) {

        database.deleteStory(database.getWritableDatabase(), story._id);

        Toast.makeText(getApplicationContext(), "Story " + story.title + " deleted.", Toast.LENGTH_LONG).show();

        deletedStory = true;

        finish();
    }

    public void onUploadButtonClicked(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_story_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_delete:
                onDeleteButtonClicked(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }
}
