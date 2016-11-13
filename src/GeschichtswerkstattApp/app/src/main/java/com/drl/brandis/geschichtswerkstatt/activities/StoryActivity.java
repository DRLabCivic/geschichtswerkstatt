package com.drl.brandis.geschichtswerkstatt.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drl.brandis.geschichtswerkstatt.R;
import com.drl.brandis.geschichtswerkstatt.utils.StoryUploader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.drl.brandis.geschichtswerkstatt.database.Story;
import com.drl.brandis.geschichtswerkstatt.database.StoryDatabase;
import com.drl.brandis.geschichtswerkstatt.utils.Utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryActivity extends BaseActivity {

    private static final String LOG_TAG = "StoryActivity";

    public static final String PICTURE_FILES_DIRECTORY = "Stories";

    private static final int RECORD_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST_CODE = 2;
    private static final int REQUEST_IMAGE_CAPTURE_CODE = 3;

    Story story;

    TextView titleEdit;
    TextView textEdit;
    TextView locationText;
    TextView recordingText;
    ImageView pictureView;

    ViewGroup mainLayout;

    private Call<ResponseBody> httpRequest = null;

    public File imageFile;

    private boolean deletedStory = false;

    private StoryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // request permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

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

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        titleEdit = (TextView) findViewById(R.id.edit_text_title);
        textEdit = (TextView) findViewById(R.id.edit_text_text);
        locationText = (TextView) findViewById(R.id.location_text);
        pictureView = (ImageView) findViewById(R.id.picture_view);
        recordingText = (TextView) findViewById(R.id.recording_text);

        database = new StoryDatabase(getApplicationContext());

        updateUi();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        if (story.loc_name != null)
            locationText.setText(story.loc_name);

        if (story.audioFile != null) {
            File file = new File(story.audioFile);
            recordingText.setText(file.getName());
        }

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCheckmarks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //update image in background task
        AsyncTask task = new AsyncTask<Object, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Object... params) {
                if (story.imageFile != null && Utils.fileExists(story.imageFile)) {
                    Bitmap bmp = BitmapFactory.decodeFile(story.imageFile);
                    Bitmap scaledBmp = Utils.scaleBitmap(bmp, 256, 256);
                    return scaledBmp;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null)
                    pictureView.setImageBitmap(result);
            }
        };
        task.execute();

        updateCheckmarks();

    }

    protected void updateCheckmarks() {

        boolean isTextSet = titleEdit.getText().length() > 3;
        ((CheckBox)findViewById(R.id.check_text)).setChecked(isTextSet);

        boolean isRecordSet = story.audioFile != null;
        ((CheckBox)findViewById(R.id.check_record)).setChecked(isRecordSet);

        boolean isPlaceSet = story.loc_name != null;
        ((CheckBox)findViewById(R.id.check_place)).setChecked(isPlaceSet);

        boolean isImgSet = story.imageFile != null;
        ((CheckBox)findViewById(R.id.check_img)).setChecked(isImgSet);
    }

    public void saveStory() {

        // get values
        if (titleEdit.getText().length() > 0)
            story.title = titleEdit.getText().toString();
        else
            story.title = null;

        if (textEdit.getText().length() > 0)
            story.text = textEdit.getText().toString();
        else
            story.text = null;

        if (story.isEmpty())
            return;

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

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            showAlert("Warning", "Cant access the device's camera.");
            return;
        }

        // Create the File where the photo should go
        imageFile = getOutputMediaFileName();

        // start the image capture activity
        if (imageFile != null) {
            Uri file = Uri.fromFile(imageFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
        } else {
            showAlert("Error", "Could not create image file.");
        }
    }

    public void onLocationButtonClicked(View view) {

        showOverlay("Loading Map", mainLayout);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST_CODE);

            //show spinner
            findViewById(R.id.spinner_overlay).setVisibility(View.VISIBLE);
        } catch (GooglePlayServicesRepairableException e) {
            showAlert("Error",e.getMessage());
            hideOverlay();
        } catch (GooglePlayServicesNotAvailableException e) {
            showAlert("Error", e.getMessage());
            hideOverlay();
        }

    }

    public void onDeleteButtonClicked(View view) {

        database.deleteStory(database.getWritableDatabase(), story);
        deletedStory = true;

        Toast.makeText(getApplicationContext(), "Story " + story.title + " deleted.", Toast.LENGTH_LONG).show();

        finish();
    }

    public void onUploadButtonClicked(View view) {

        saveStory();

        List<String> errors = story.validate();
        if (errors != null) {
            showAlert("Geschichte", TextUtils.join(" ",errors));
            return;
        }

        showOverlay("Uploading Story...", mainLayout);
        httpRequest = StoryUploader.upload(story, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                hideOverlay();

                //delete story
                database.deleteStory(database.getWritableDatabase(), story);
                deletedStory = true;

                try {
                    Log.e(LOG_TAG,"Upload response: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Geschichte " + story.title + " wurde erfolgreich hochgeladen.", Toast.LENGTH_LONG).show();

                //end activity
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideOverlay();
                showAlert("Upload",t.getMessage());
            }
        });
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
            case R.id.action_save:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_delete:
                onDeleteButtonClicked(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //hide Spinner
        hideOverlay();

        if (requestCode == PLACE_PICKER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final LatLng location = place.getLatLng();
            final CharSequence address = place.getAddress();

            story.loc_latitude = location.latitude;
            story.loc_longitude = location.longitude;
            story.loc_name = address.toString();

            saveStory();
            updateUi();

        } else if (requestCode == REQUEST_IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {

            if (imageFile.exists()) {
                story.imageFile = imageFile.getAbsolutePath();
            }

            saveStory();
            updateUi();

        } else if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {

            if (data.hasExtra("recording")) {
                File file = (File) data.getSerializableExtra("recording");
                story.audioFile = file.getAbsolutePath();
            }

            saveStory();
            updateUi();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static File getOutputMediaFileName() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),PICTURE_FILES_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
