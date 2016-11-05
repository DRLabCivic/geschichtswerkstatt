package com.drl.brandis.geschichtswerkstatt.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drl.brandis.geschichtswerkstatt.R;

import com.drl.brandis.geschichtswerkstatt.adapters.StoryAdapter;
import com.drl.brandis.geschichtswerkstatt.database.Story;
import com.drl.brandis.geschichtswerkstatt.database.StoryDatabase;

public class StoryListActivity extends BaseActivity {

    private static final String LOG_TAG = "StoryListActivity";

    private StoryDatabase database;

    private StoryAdapter storyAdapter;

    private ListView storyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        // setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = new StoryDatabase(getApplicationContext());

        storyListView = (ListView) findViewById(R.id.storyList);
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClicked(parent,view,position,id);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabButtonClicked(view);
            }
        });

        updateUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
        startActivity(intent);
    }

    public void onListItemClicked(AdapterView<?> parent, View view, int position, long id) {

        Story story = (Story) view.getTag();

        Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
        intent.putExtra("story", story);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
