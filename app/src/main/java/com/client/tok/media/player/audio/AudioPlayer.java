package com.client.tok.media.player.audio;

import android.text.TextUtils;
import com.client.tok.R;
import com.client.tok.rx.event.ProgressEvent;
import com.client.tok.rx.RxBus;
import com.client.tok.media.player.TokPlayer;
import com.client.tok.media.player.PlayStatus;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.FileDataSource;

public class AudioPlayer {
    private static String TAG = "audioPlayer";
    private static AudioPlayer sInstance;
    private TokPlayer mPlayer;

    private int mStatus = PlayStatus.STATUS_PAUSE;
    private String mUrl;
    private String mId;

    private AudioPlayer() {
        mPlayer = new TokPlayer(true);
        mPlayer.setCycle(false);
        mPlayer.setOnVideoPlayerListener(new TokPlayer.VideoPlayerListenerWrapper() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    mStatus = PlayStatus.STATUS_DONE;
                    if (mId != null) {
                        RxBus.publish(new ProgressEvent(mId, 0, PlayStatus.STATUS_DONE));
                        mId = null;
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                mStatus = PlayStatus.STATUS_ERROR;
                LogUtil.i(TAG, "audio play error,path:" + mUrl + ",error:" + error.getMessage());
                if (error.getCause() instanceof FileDataSource.FileDataSourceException) {
                    ToastUtils.show(R.string.file_not_exist);
                } else {
                    ToastUtils.show(R.string.play_failed);
                }
                if (mId != null) {
                    RxBus.publish(new ProgressEvent(mId, 0, PlayStatus.STATUS_ERROR));
                    mId = null;
                }
            }
        });
    }

    public static AudioPlayer getInstance() {
        if (sInstance == null) {
            sInstance = new AudioPlayer();
        }
        return sInstance;
    }

    public static String getPlayingId() {
        if (sInstance != null && sInstance.mStatus == PlayStatus.STATUS_PLAYING) {
            return sInstance.mId;
        }
        return null;
    }

    public void play(String id, String url) {
        if (!TextUtils.isEmpty(url)) {
            LogUtil.i(TAG, "play url:" + url + ",status:" + mStatus + ",currentUrl:" + mUrl);
            if (!url.equals(mUrl)) {
                if (!StringUtils.isEmpty(mId)) {
                    //stop last animation
                    pause();
                }
                start(id, true, url);
            } else if (mStatus == PlayStatus.STATUS_PAUSE) {
                start(id, false, url);
            } else if (mStatus == PlayStatus.STATUS_ERROR || mStatus == PlayStatus.STATUS_DONE) {
                start(id, true, url);
            } else {
                pause_();//is palying->stop
            }
        }
    }

    private void start(String id, boolean reload, String url) {
        mId = id;
        if (id != null) {
            RxBus.publish(new ProgressEvent(id, 0, PlayStatus.STATUS_PLAYING));
        }
        mStatus = PlayStatus.STATUS_PLAYING;
        mUrl = url;
        if (reload) {
            mPlayer.loadAudio(url);
        }
        mPlayer.start();
    }

    private void pause_() {
        mStatus = PlayStatus.STATUS_PAUSE;
        if (mId != null) {
            RxBus.publish(new ProgressEvent(mId, 0, PlayStatus.STATUS_PAUSE));
        }
        mPlayer.pause();
    }

    public static void pause() {
        if (sInstance != null) {
            sInstance.pause_();
        }
    }

    public static void release() {
        LogUtil.i(TAG, "audio player release...");
        if (sInstance != null && sInstance.mPlayer != null) {
            sInstance.mPlayer.release();
            sInstance.mPlayer = null;
            sInstance.mId = null;
        }
        sInstance = null;
    }
}
