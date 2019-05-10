package com.client.tok.media.recorder.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MediaMuxerWrapper {
    private static final String TAG = "MediaMuxerWrapper";

    private String mOutputPath;
    private final MediaMuxer mMediaMuxer;    // API >= 18
    private int mEncoderCount, mStatredCount;
    private boolean mIsStarted;
    private MediaEncoder mVideoEncoder, mAudioEncoder;

    /**
     * Constructor
     * @param outputPath extension of output file,include file name
     * @throws IOException
     */
    public MediaMuxerWrapper(String outputPath) throws IOException {
        mOutputPath = outputPath;
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mEncoderCount = mStatredCount = 0;
        mIsStarted = false;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public void prepare() throws IOException {
        if (mVideoEncoder != null) mVideoEncoder.prepare();
        if (mAudioEncoder != null) mAudioEncoder.prepare();
    }

    public void startRecording() {
        if (mVideoEncoder != null) mVideoEncoder.startRecording();
        if (mAudioEncoder != null) mAudioEncoder.startRecording();
    }

    public void stopRecording() {
        if (mVideoEncoder != null) mVideoEncoder.stopRecording();
        mVideoEncoder = null;
        if (mAudioEncoder != null) mAudioEncoder.stopRecording();
        mAudioEncoder = null;
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    //**********************************************************************
    //**********************************************************************

    /**
     * assign encoder to this calss. this is called from encoder.
     *
     * @param encoder instance of MediaVideoEncoder or MediaAudioEncoder
     */
    void addEncoder(final MediaEncoder encoder) {
        if (encoder instanceof MediaVideoEncoder) {
            if (mVideoEncoder != null) {
                throw new IllegalArgumentException("Video encoder already added.");
            }
            mVideoEncoder = encoder;
        } else if (encoder instanceof MediaAudioEncoder) {
            if (mAudioEncoder != null) {
                throw new IllegalArgumentException("Video encoder already added.");
            }
            mAudioEncoder = encoder;
        } else {
            throw new IllegalArgumentException("unsupported encoder");
        }
        mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
    }

    /**
     * request start recording from encoder
     *
     * @return true when muxer is ready to write
     */
    /*package*/
    synchronized boolean start() {
        LogUtil.i(TAG, "start:");
        mStatredCount++;
        if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
            mMediaMuxer.start();
            mIsStarted = true;
            notifyAll();
            LogUtil.i(TAG, "MediaMuxer started:");
        }
        return mIsStarted;
    }

    /**
     * request stop recording from encoder when encoder received EOS
     */
    /*package*/
    synchronized void stop() {
        LogUtil.i(TAG, "stop:mStatredCount=" + mStatredCount);
        mStatredCount--;
        if ((mEncoderCount > 0) && (mStatredCount <= 0)) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mIsStarted = false;
            LogUtil.i(TAG, "MediaMuxer stopped:");
        }
    }

    /**
     * assign encoder to muxer
     *
     * @return minus value indicate error
     */
    /*package*/
    synchronized int addTrack(final MediaFormat format) {
        if (mIsStarted) throw new IllegalStateException("muxer already started");
        final int trackIx = mMediaMuxer.addTrack(format);
        LogUtil.i(TAG,
            "addTrack:trackNum=" + mEncoderCount + ",trackIx=" + trackIx + ",format=" + format);
        return trackIx;
    }

    /**
     * write encoded data to muxer
     */
    /*package*/
    synchronized void writeSampleData(final int trackIndex, final ByteBuffer byteBuf,
        final MediaCodec.BufferInfo bufferInfo) {
        if (mStatredCount > 0) mMediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
    }

}
