package com.drl.brandis.geschichtswerkstatt.recorder;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by lutz on 13/04/15.
 */
abstract public class SoundRecorder {

    protected Context context;

    public SoundRecorder(Context context) {
        this.context = context;
    }
    public void prepare() throws Exception { };
    abstract public void startRecording() throws IOException;
    abstract public void stopRecording() throws IOException;
    abstract public void reset() throws IOException;
    abstract public File save() throws IOException;
    abstract public void release();

}
