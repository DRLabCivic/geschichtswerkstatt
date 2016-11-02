package com.drl.brandis.geschichtswerkstatt.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.drl.brandis.geschichtswerkstatt.R;

import adapters.StoryAdapter;
import database.Story;
import database.StoryDatabase;

public class StoryListActivity extends BaseActivity {

    private static final String LOG_TAG = "StoryListActivity";

    private StoryDatabase database;

    private StoryAdapter storyAdapter;

    private ListView storyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        database = new StoryDatabase(getApplicationContext());

        storyListView = (ListView) findViewById(R.id.storyList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabButtonClicked(view);
            }
        });

        updateUi();
    }

    public void updateUi() {

        // get data
        Cursor cursor = database.getAllStories(database.getReadableDatabase());

        if (storyAdapter == null)
            storyAdapter = new StoryAdapter(this, cursor);
        else
            storyAdapter.changeCursor(cursor);

        // update listview
        storyListView.setAdapter(storyAdapter);

        // close database connection
        database.close();
    }

    public void onFabButtonClicked(final View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View contentView = inflater.inflate(R.layout.dialog_storytitle, null);
        dialog.setView(contentView);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText textEdit = (EditText) contentView.findViewById(R.id.editTextTitle);

                // add to database
                Long id = database.insertStory(database.getWritableDatabase(), textEdit.getText().toString());
                database.close();

                updateUi();

                Cursor cursor = database.getStory(database.getReadableDatabase(), id);
                cursor.moveToFirst();
                Story story = new Story(cursor);

                //start recorder activity
                Intent intent = new Intent(getApplicationContext(), RecorderActivity.class);
                intent.putExtra("story", story);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }

    public void onItemDeleteButtonClicked(View view) {
        View parent = (View) view.getParent();
        Story story = (Story) parent.getTag();

        Snackbar.make(view, "Story "+story.title+" deleted.", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

        database.deleteStory(database.getWritableDatabase(), story._id);

        updateUi();
    }
}
