package com.client.tok.ui.video;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.media.recorder.video.CameraGLView;
import com.client.tok.media.recorder.video.MediaAudioEncoder;
import com.client.tok.media.recorder.video.MediaEncoder;
import com.client.tok.media.recorder.video.MediaMuxerWrapper;
import com.client.tok.media.recorder.video.MediaVideoEncoder;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.permission.PermissionCallBack;
import com.client.tok.permission.PermissionModel;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.RoundProgress;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VideoRecordActivity extends BaseTitleFullScreenActivity
    implements View.OnClickListener, RoundProgress.ProgressListener {
    private String TAG = "VideoRecordActivity_";
    // for camera preview display
    private CameraGLView mCameraGlView;
    private VideoView mVideoView;
    //muxer for audio/video recording
    private MediaMuxerWrapper mMuxer;

    private FrameLayout mCameraLayout;
    //button for start/stop recording
    private ImageView mFlashIv;
    private ImageView mRecordIv;
    private RoundProgress mProgressView;
    private ImageView mCameraIv;

    private ImageView mCloseIv;
    private ImageView mDropIv;
    private ImageView mSelectIv;

    private String[] FLASH_MODES = new String[] { CameraGLView.FLASH_OFF, CameraGLView.FLASH_ON };
    private int[] FLASH_MODE_IMGS = new int[] { R.drawable.flash_off, R.drawable.flash_on };
    private int[] CAMERA_IDS = new int[] { CameraGLView.CAMERA_BACK, CameraGLView.CAMERA_FRONT };
    private int[] CAMERA_ID_IMGS = new int[] { R.drawable.camera_back, R.drawable.camera_front };
    private int mCurFlashModeIndex = 0;
    private int mCurCameraIdIndex = 0;

    private boolean mIsRecording;
    private String mVideoPath;

    private String[] mPermission = PermissionModel.PERMISSION_CAMERA_AUDIO;
    private boolean mHasPermission;
    private boolean mHasShow;

    private Intent mIntent;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        mIntent = getIntent();

        mCameraLayout = $(R.id.id_video_gl_view_layout);
        initCameraGlView();

        mVideoView = $(R.id.id_video_view);

        mRecordIv = $(R.id.id_video_record_view);
        mRecordIv.setOnTouchListener(new RecordTouchListener());
        mProgressView = $(R.id.id_record_progress);
        mProgressView.setAutoTimeProgress(GlobalParams.MAX_VIDEO_TIME);
        mProgressView.setProgressListener(this);

        mFlashIv = $(R.id.id_video_flash_iv);
        mFlashIv.setOnClickListener(this);
        mCameraIv = $(R.id.id_video_camera_iv);
        mCameraIv.setOnClickListener(this);

        mCloseIv = $(R.id.id_video_close_iv);
        mCloseIv.setOnClickListener(this);

        mDropIv = $(R.id.id_video_drop_iv);
        mDropIv.setOnClickListener(this);
        mSelectIv = $(R.id.id_video_select_iv);
        mSelectIv.setOnClickListener(this);
        checkPermission();
    }

    private void initCameraGlView() {
        if (mHasPermission && mCameraGlView == null) {
            View view = ViewUtil.inflateViewById(this, R.layout.layout_camera_gl_view);
            mCameraLayout.addView(view);
            mCameraGlView = $(R.id.id_video_gl_view);
            int width = 1280;//ScreenUtils.getScreenWidth(this);
            int height = 720;//ScreenUtils.getScreenHeight(this);
            LogUtil.i(TAG, "width:" + width + ",height:" + height);
            mCameraGlView.setVideoSize(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume:");
        showPreview();
    }

    private void showPreview() {
        if (mHasPermission && !mHasShow) {
            mCameraLayout.setVisibility(View.VISIBLE);
            initCameraGlView();
            mCameraGlView.onResume();
            mHasShow = true;
        }
    }

    @Override
    public void onPause() {
        LogUtil.i(TAG, "onPause:");
        stopRecording();
        if (mHasPermission && mHasShow) {
            mCameraGlView.onPause();
            mHasShow = false;
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_video_flash_iv:
                mCurFlashModeIndex = (mCurFlashModeIndex + 1) % FLASH_MODES.length;
                mCameraGlView.setFlashMode(FLASH_MODES[mCurFlashModeIndex]);
                mFlashIv.setImageResource(FLASH_MODE_IMGS[mCurFlashModeIndex]);
                break;
            case R.id.id_video_camera_iv:
                mCurCameraIdIndex = (mCurCameraIdIndex + 1) % CAMERA_IDS.length;
                mCameraGlView.setCameraId(CAMERA_IDS[mCurCameraIdIndex]);
                mCameraIv.setImageResource(CAMERA_ID_IMGS[mCurCameraIdIndex]);
                break;
            case R.id.id_video_drop_iv:
                revertInitView();
                FileUtilsJ.delFile(mVideoPath);
                break;
            case R.id.id_video_select_iv:
                mIntent.putExtra(IntentConstants.FILE, mVideoPath);
                setResult(Activity.RESULT_OK, mIntent);
                onFinish();
                break;
            case R.id.id_video_close_iv:
                onFinish();
                break;
        }
    }

    @Override
    public void onFinish() {
        finish();
        overridePendingTransition(0, R.anim.slide_to_bottom);
    }

    private void checkPermission() {
        if (PermissionModel.hasPermissions(this, mPermission)) {
            mHasPermission = true;
            showPreview();
        } else {
            PermissionModel.requestPermissions(mPermission,
                PermissionModel.getRationalByPer(mPermission), new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        mHasPermission = true;
                        onResume();
                    }

                    @Override
                    public void onCancelPermissionRationale(int requestCode) {
                        finish();
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode,
                        @NonNull List<String> deniedPers) {
                        showPermissionSetting();
                    }
                });
        }
    }

    private class RecordTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            LogUtil.i(TAG, "action :" + action);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (!mIsRecording) {
                        startRecording();
                        startRecordView();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRecording();
                    stopRecordView();
                    break;
            }
            return true;
        }
    }

    private String generalOutFile() {
        return StorageUtil.getFilesFolder() + System.currentTimeMillis() + ".mp4";
    }

    private void revertInitView() {
        mFlashIv.setVisibility(View.VISIBLE);
        mCameraIv.setVisibility(View.VISIBLE);
        mRecordIv.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.clear();
        mCloseIv.setVisibility(View.VISIBLE);
        mDropIv.setVisibility(View.GONE);
        mSelectIv.setVisibility(View.GONE);
        stopPlayVideo();
        showPreview();
    }

    private void startRecordView() {
        mFlashIv.setVisibility(View.GONE);
        mCameraIv.setVisibility(View.GONE);
        mCloseIv.setVisibility(View.GONE);
        mDropIv.setVisibility(View.GONE);
        mSelectIv.setVisibility(View.GONE);
        mProgressView.startAutoUpdate();
    }

    private void stopRecordView() {
        mFlashIv.setVisibility(View.GONE);
        mCameraIv.setVisibility(View.GONE);
        mRecordIv.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.INVISIBLE);
        mCloseIv.setVisibility(View.INVISIBLE);
        mDropIv.setVisibility(View.VISIBLE);
        mSelectIv.setVisibility(View.VISIBLE);
        mProgressView.stopAutoUpdate();
        mHasShow = false;
        if (mCameraGlView != null) {
            mCameraGlView.onPause();
            mCameraLayout.setVisibility(View.INVISIBLE);
            mCameraLayout.removeAllViews();
            mCameraGlView = null;
        }
        startPlayVideo();
    }

    private void stopPlayVideo() {
        mVideoView.stopPlayback();
        mVideoView.setVideoURI(null);
        mVideoView.setVisibility(View.GONE);
    }

    private void startPlayVideo() {
        mVideoView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //delay for 800 ms
                if (FileUtilsJ.exist(mVideoPath)) {
                    Uri uri = FileProvider.getUriForFile(VideoRecordActivity.this,
                        GlobalParams.PROVIDER_AUTH, new File(mVideoPath));
                    LogUtil.i("MediaMuxerWrapper", "setVideoPath:" + mVideoPath);
                    mVideoView.setVideoURI(uri);
                    mVideoView.requestFocus();
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            LogUtil.i("MediaMuxerWrapper", "video start");
                            mVideoView.start();
                        }
                    });
                    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mVideoView.start();
                        }
                    });
                } else {
                    VideoRecordActivity.this.revertInitView();
                }
            }
        }, 300);
    }

    private void startRecording() {
        mIsRecording = true;
        try {
            mVideoPath = generalOutFile();
            // if you record audio only, ".m4a" is also OK.
            mMuxer = new MediaMuxerWrapper(mVideoPath);
            // for video capturing
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraGlView.getVideoWidth(),
                mCameraGlView.getVideoHeight());
            // for audio capturing
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        mIsRecording = false;
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener =
        new MediaEncoder.MediaEncoderListener() {
            @Override
            public void onPrepared(final MediaEncoder encoder) {
                LogUtil.i(TAG, "onPrepared:encoder=" + encoder);
                if (encoder instanceof MediaVideoEncoder) {
                    mCameraGlView.setVideoEncoder((MediaVideoEncoder) encoder);
                }
            }

            @Override
            public void onStopped(final MediaEncoder encoder) {
                LogUtil.i(TAG, "onStopped:encoder=" + encoder);
                if (encoder instanceof MediaVideoEncoder) mCameraGlView.setVideoEncoder(null);
            }
        };

    @Override
    public void onProgress(float progress) {
        //note:in thread
        LogUtil.i(TAG, "onProgress:" + progress);
    }

    @Override
    public void onProgressFinish() {
        //note:in thread
        LogUtil.i(TAG, "onProgressFinish");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressView.stopAutoUpdate();
                VideoRecordActivity.this.stopRecording();
                VideoRecordActivity.this.stopRecordView();
            }
        });
    }
}
