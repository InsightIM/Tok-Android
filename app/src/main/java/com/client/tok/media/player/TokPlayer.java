package com.client.tok.media.player;

import android.content.Context;
import android.net.Uri;
import com.client.tok.BuildConfig;
import com.client.tok.TokApplication;
import com.client.tok.utils.LogUtil;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.video.VideoListener;

public class TokPlayer implements EventListener, VideoListener {
    private String TAG = "TokPlayer";
    private SimpleExoPlayer mPlayer;
    private boolean mCycle = false;
    private Context mContext = TokApplication.getInstance();
    private OnVideoPlayerListener mOnVideoPlayerListener = null;

    public TokPlayer(boolean isAudio) {
        if (isAudio) {
            //audio
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector);
            mPlayer.addListener(this);
            mPlayer.addVideoListener(this);
        } else {
            //video TODO
        }
    }

    public void setCycle(boolean cycle) {
        mCycle = cycle;
    }

    public boolean isPlaying() {
        return mPlayer.getPlayWhenReady();
    }

    public int duration() {
        if (mPlayer.getDuration() == C.TIME_UNSET) {
            return 0;
        } else {
            return (int) mPlayer.getDuration();
        }
    }

    public void start() {
        mPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    public void loadAudio(String url) {
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(
            new DefaultDataSourceFactory(mContext, BuildConfig.APPLICATION_ID)).createMediaSource(
            Uri.parse(url));
        mPlayer.prepare(mediaSource);
    }

    public void seekTo(Long pos) {
        mPlayer.seekTo(pos);
    }

    public void seekTo(int timeMillis) {
        long seekPos = 0;
        if (mPlayer.getDuration() == C.TIME_UNSET) {
            seekPos = 0;
        } else {
            seekPos = Math.min(Math.max(0, timeMillis), duration());
        }
        seekTo(seekPos);
    }

    public void setOnVideoPlayerListener(OnVideoPlayerListener onVideoPlayerListener) {
        this.mOnVideoPlayerListener = onVideoPlayerListener;
    }

    public void setSpeed(float speed) {
        PlaybackParameters pp =
            new PlaybackParameters(speed, mPlayer.getPlaybackParameters().pitch);
        mPlayer.setPlaybackParameters(pp);
    }

    public void release() {
        mPlayer.release();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        LogUtil.i(TAG, "onTimelineChanged");
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onTimelineChanged(timeline, manifest);
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onTracksChanged(trackGroups, trackSelections);
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onLoadingChanged(isLoading);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onPlayerStateChanged(playWhenReady, playbackState);
        }
        if (mCycle && playbackState == Player.STATE_ENDED) {
            mPlayer.seekTo(0);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onPlayerError(error);
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onPositionDiscontinuity();
        }
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onPlaybackParametersChanged(playbackParameters);
        }
    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
        float pixelWidthHeightRatio) {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onVideoSizeChanged(width, height, unappliedRotationDegrees,
                pixelWidthHeightRatio);
        }
    }

    public void onRenderedFirstFrame() {
        if (mOnVideoPlayerListener != null) {
            mOnVideoPlayerListener.onRenderedFirstFrame();
        }
    }

    interface OnVideoPlayerListener {
        void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
            Float pixelWidthHeightRatio);

        void onRenderedFirstFrame();

        void onPlayerStateChanged(boolean playWhenReady, int playbackState);

        void onPlaybackParametersChanged(PlaybackParameters playbackParameters);

        void onPositionDiscontinuity();

        void onLoadingChanged(boolean isLoading);

        void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections);

        void onTimelineChanged(Timeline timeline, Object manifest);

        void onPlayerError(ExoPlaybackException error);
    }

    public static class VideoPlayerListenerWrapper implements OnVideoPlayerListener {

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
            Float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame() {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups,
            TrackSelectionArray trackSelections) {

        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }
    }
}
