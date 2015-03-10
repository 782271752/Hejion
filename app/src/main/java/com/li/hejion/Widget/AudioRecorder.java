package com.li.hejion.Widget;

import java.io.File;
import java.io.IOException;
import android.media.MediaRecorder;
import android.os.Environment;

/**
 * 录音工具
 */
public class AudioRecorder
{

	MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String path)
	{
		this.path = path;
	}


	public void start() throws IOException
	{
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException
	{
		recorder.stop();
        recorder.reset();
		recorder.release();
        recorder=null;
	}
}