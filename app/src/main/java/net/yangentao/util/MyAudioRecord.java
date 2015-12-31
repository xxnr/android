package net.yangentao.util;

import java.io.File;

import android.content.Context;
import android.media.MediaRecorder;

public class MyAudioRecord {

	private Context context;
	private int tickMillSeconds = 100;

	private MediaRecorder recorder = null; // 录音变量
	private boolean recording = false;
	private long record_start_time;// 记录录音开始时间
	private long record_end_time;// 记录录音开始时间
	private ProcessListener listener = null;

	private File recordFile;

	public interface ProcessListener {
		public void onProgress(int maxAmplitude, long millSeconds);
	}

	public MyAudioRecord(Context c) {
		recording = false;
		context = c;
		// AudioManager audio = (AudioManager)
		// c.getSystemService(Context.AUDIO_SERVICE);
		// int current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		// audio.setStreamVolume(AudioManager.STREAM_MUSIC, current,
		// AudioManager.FLAG_PLAY_SOUND);

	}

	public File getFile() {
		return recordFile;
	}

	/**
	 * @return 毫秒
	 */
	public long getStartTime() {
		return record_start_time;
	}

	/**
	 * @return 毫秒
	 */
	public long getEndTime() {
		return record_end_time;
	}

	public long getTotalTime() {
		return record_end_time - record_start_time;
	}

	private void release() {
		recording = false;
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}
	}

	public void setRecordListener(int tickMillSeconds, ProcessListener listener) {
		this.listener = listener;
		this.tickMillSeconds = tickMillSeconds;
	}

	private int getMaxAmplitude() {
		if (recorder != null) {
			int am = recorder.getMaxAmplitude();
			XLog.d("AM:" + am);
			return am;
		}
		return 0;
	}

	private Runnable tick = new Runnable() {

		@Override
		public void run() {
			if (recording) {
				if (listener != null) {
					listener.onProgress(getMaxAmplitude(),
							System.currentTimeMillis() - record_start_time);
				}
			}
			if (recording) {
				TaskUtil.getMainHandler().postDelayed(tick, tickMillSeconds);
			}
		}
	};

	/**
	 * 使用默认的文件开始录音
	 * 
	 * @return
	 */
	public boolean start() {
		File file = context.getFileStreamPath("audio_record_default.amr");
		return start(file);
	}

	public boolean start(File file) {
		if (recording) {
			XLog.e("already recoding");
			return false;
		}
		recordFile = file;
		recorder = new MediaRecorder();
		XLog.d("begin recording...");
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// RAW_AMR
																		// 设置MediaRecorder录制的音频格式
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);// 设置音频编码Encoder
		recorder.setOutputFile(recordFile.getAbsolutePath());
		try {
			recorder.prepare();// 准备录制
			recorder.start();// 开始录制

			recording = true;
			record_start_time = System.currentTimeMillis();
			TaskUtil.getMainHandler().post(tick);
			return true;
		} catch (Exception e) {
			XLog.e(e);
			release();
		}
		return false;
	}

	public boolean stop() {
		XLog.d("end recording...");
		if (!recording) {
			XLog.e("no not recording!!");
			return false;
		}
		stopInner();
		return true;
	}

	public void cancel() {
		XLog.d("cancel recording...");
		stopInner();
		if (recordFile != null && recordFile.exists()) {
			recordFile.delete();
		}
	}

	private synchronized void stopInner() {
		if (recorder != null) {
			record_end_time = System.currentTimeMillis();
			recorder.stop();
			recorder.reset();
			release();
		}
	}

	public boolean isRecording() {
		return recording;
	}
}
