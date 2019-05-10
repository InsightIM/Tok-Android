package com.client.tok.media.recorder.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.client.tok.TokApplication;
import com.client.tok.utils.DispatchQueue;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.StringUtils;
import java.io.File;
import java.util.UUID;

public class OpusAudioRecorder {
    static {
        System.loadLibrary("tok");
    }

    private static OpusAudioRecorder sInstance;
    private Context context;
    private int SAMPLE_RATE = 16000;
    private final int BUFFER_SIZE_FACTOR = 2;

    public static final int STATE_NOT_INIT = 0;
    public static final int STATE_IDLE = 1;
    public static final int STATE_RECORDING = 2;

    private final int MAX_RECORD_DURATION = 60000;

    public static int state = STATE_NOT_INIT;

    private AudioRecord audioRecord;
    private int recordBufferSize;
    private File recordingAudioFile;
    private short[] recordSamples = new short[1024];
    private long samplesCount = 0L;
    private long recordTimeCount = 0L;
    private boolean sendAfterDone = false;
    private boolean callStop = false;
    private DispatchQueue recordQueue;
    private DispatchQueue fileEncodingQueue;
    private RecordCallBack callBack;
    private Runnable recordStartRunnable;
    private Runnable recordRunnable;

    private OpusAudioRecorder(Context context) {
        this.context = context;
        init();
    }

    public static OpusAudioRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new OpusAudioRecorder(TokApplication.getInstance());
        }
        return sInstance;
    }

    private void init() {
        recordBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
        try {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {

                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    if (state != TelephonyManager.CALL_STATE_IDLE) {
                        stopRecording(false, false);
                        if (callBack != null) {
                            callBack.onCancel();
                        }
                    }
                }
            };
            context.getSystemService(TelephonyManager.class)
                .listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            recordQueue = new DispatchQueue("recordQueue");
            recordQueue.setPriority(Thread.MAX_PRIORITY);
            fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
            fileEncodingQueue.setPriority(Thread.MAX_PRIORITY);
            recordStartRunnable = new RecordStartRunnable();
            recordRunnable = new RecordRunnable();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void startRecording(RecordCallBack callback) {
        this.callBack = callback;
        recordQueue.postRunnable(recordStartRunnable, 0);
    }

    public void stopRecording(final boolean send, boolean vibrate) {
        recordQueue.cancelRunnable(recordStartRunnable);
        if (vibrate) {
            vibrate(new long[] { 0L, 10L });
        }
        recordQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (audioRecord != null) {
                    try {
                        sendAfterDone = send;
                        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                            audioRecord.stop();
                        }
                    } catch (Exception e) {
                        if (recordingAudioFile != null) {
                            recordingAudioFile.delete();
                        }
                    }
                    OpusAudioRecorder.this.stopRecordingInternal(send);
                }
            }
        }, 0);
    }

    public void stop() {
        callBack = null;
        stopRecording(false, false);
    }

    private void stopRecordingInternal(final boolean send) {
        callStop = true;
        fileEncodingQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                OpusAudioRecorder.this.stopRecord();
                if (send) {
                    long duration = recordTimeCount;
                    byte[] waveForm =
                        OpusAudioRecorder.this.getWaveform2(recordSamples, recordSamples.length);
                    //GlobalScope.launch(Dispatcher.) {
                    //    if (recordingAudioFile != null) {
                    //        callback ?.onSuccess(recordingAudioFile !!, duration, waveForm)
                    //    } callback = null recordingAudioFile = null
                    //}
                    //thread TODO
                    if (callBack != null) {
                        String newName = UUID.randomUUID() + "_" + duration + ".ogg";
                        String newPath =
                            FileUtilsJ.rename(recordingAudioFile.getAbsolutePath(), newName);
                        if (!StringUtils.isEmpty(newPath)) {
                            callBack.onSuccess(newPath, duration, waveForm);
                        } else {
                            callBack.onCancel();
                        }

                        callBack = null;
                        recordingAudioFile = null;
                    }
                } else {
                    recordingAudioFile.deleteOnExit();
                }
            }
        }, 0L);

        state = STATE_IDLE;
        try {
            if (audioRecord != null) {
                audioRecord.release();
            }
            audioRecord = null;
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private void vibrate(long[] pattern) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    public native int startRecord(String path);

    public native int writeFrame(short[] frame, int len);

    public native void stopRecord();

    public native byte[] getWaveform2(short[] arr, int len);

    public interface RecordCallBack {
        void onCancel();

        void onSuccess(String path, long duration, byte[] waveForm);
    }

    private class RecordStartRunnable implements Runnable {

        @Override
        public void run() {
            if (audioRecord != null) {
                return;
            }

            try {
                recordingAudioFile = FileUtilsJ.createTempAudioFile();
                if (startRecord(recordingAudioFile.getAbsolutePath()) != 0) {
                    return;
                }

                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    recordBufferSize * BUFFER_SIZE_FACTOR);

                if (audioRecord == null
                    || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    return;
                }
                callStop = false;
                samplesCount = 0;
                recordTimeCount = 0;
                audioRecord.startRecording();

                if (audioRecord != null
                    && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.release();
                    audioRecord = null;
                    return;
                }
                vibrate(new long[] { 0, 10 });
                state = STATE_RECORDING;
            } catch (Exception e) {
                if (recordingAudioFile != null) {
                    recordingAudioFile.delete();
                }

                try {
                    stopRecord();
                    state = STATE_IDLE;
                    if (audioRecord != null) {
                        audioRecord.release();
                    }
                    audioRecord = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }

            recordQueue.postRunnable(recordRunnable, 0);
        }
    }

    private class RecordRunnable implements Runnable {
        @Override
        public void run() {
            if (audioRecord != null) {
                final short[] shortArray = new short[recordBufferSize];
                final int len = audioRecord.read(shortArray, 0, shortArray.length);
                if (len > 0 && !callStop) {
                    int sum = 0;
                    try {
                        long newSamplesCount = samplesCount + len / 2;
                        int currPart =
                            (int) (samplesCount / newSamplesCount * recordSamples.length);
                        int newPart = recordSamples.length - currPart;
                        float sampleStep;
                        if (currPart != 0) {
                            sampleStep = recordSamples.length / currPart;
                            float currNum = 0f;
                            for (int i = 0; i < currPart; i++) {
                                recordSamples[i] = recordSamples[(int) currNum];
                                currNum += sampleStep;
                            }
                        }
                        ;
                        int currNum = currPart;
                        float nextNum = 0f;
                        sampleStep = len / 2f / newPart;
                        for (int i = 0; i < len; i++) {
                            short peak = shortArray[i];
                            if (peak > 2500) {
                                sum += peak * peak;
                            }
                            if (i == (int) nextNum && currNum < recordSamples.length) {
                                recordSamples[currNum] = peak;
                                nextNum += sampleStep;
                                currNum++;
                            }
                        }
                        samplesCount = newSamplesCount;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fileEncodingQueue.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            if (callStop) {
                                return;
                            }
                            OpusAudioRecorder.this.writeFrame(shortArray, len);
                            recordTimeCount += len / 16;

                            if (recordTimeCount >= MAX_RECORD_DURATION) {
                                OpusAudioRecorder.this.stopRecording(true, false);
                            }
                        }
                    }, 0);
                    recordQueue.postRunnable(recordRunnable, 0);
                } else {
                    stopRecordingInternal(sendAfterDone);
                }
            }
        }
    }
}
