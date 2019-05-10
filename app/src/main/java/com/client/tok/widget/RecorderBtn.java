package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.client.tok.R;
import com.client.tok.media.player.audio.AudioPlayer;
import com.client.tok.media.recorder.audio.OpusAudioRecorder;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.permission.PermissionModel;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ScreenUtils;
import java.io.File;

public class RecorderBtn extends android.support.v7.widget.AppCompatTextView
    implements OpusAudioRecorder.RecordCallBack {
    private String TAG = "RecorderBtn";
    private RecordingView mRecordingView;
    private UpdateTimeRunnable mUpdateTimeRunnable;

    private int mMaxScrollY = -ScreenUtils.dip(10);
    private boolean mTriggeredCancel = false;
    private int mTimeValue;
    //audito has send or cancel
    private boolean mHasDealAudio = true;

    private Contract.IChatPresenter mPresenter;

    public RecorderBtn(Context context) {
        super(context);
    }

    public RecorderBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecorderBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindViewAndCallBack(RecordingView recordingView,
        Contract.IChatPresenter presenter) {
        mRecordingView = recordingView;
        mPresenter = presenter;
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onSuccess(String path, long duration, byte[] waveForm) {
        if (path != null && new File(path).exists()) {
            if (mPresenter != null) {
                mPresenter.sendFile(path);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.i(TAG, "action down:" + action);
                setPressed(true);
                AudioPlayer.pause();
                if (!PermissionModel.hasPermissions(PermissionModel.PERMISSION_RECORD_STORAGE)) {
                    PermissionModel.requestPermissions(PermissionModel.PERMISSION_RECORD_STORAGE,
                        null);
                    return true;
                }
                if (mPresenter != null && !mPresenter.canSendFile()) {
                    return true;
                } else {
                    if (mRecordingView != null) {
                        mRecordingView.show();
                    }
                }
                if (mRecordingView != null) {
                    mRecordingView.showSlideCancel();
                }
                this.setText(R.string.release_to_send);
                OpusAudioRecorder.getInstance().startRecording(this);
                mHasDealAudio = false;
                if (mUpdateTimeRunnable == null) {
                    mUpdateTimeRunnable = new UpdateTimeRunnable();
                }
                postDelayed(mUpdateTimeRunnable, 1000);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mRecordingView != null) {
                    float y = event.getY();
                    if (y > mMaxScrollY) {
                        mRecordingView.showSlideCancel();
                        this.setText(R.string.release_to_send);
                        mTriggeredCancel = false;
                    } else {
                        mRecordingView.showReleaseCancel();
                        this.setText(R.string.release_to_cancel);
                        mTriggeredCancel = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://xiaomi always return action_cancel when action_up??
                LogUtil.i(TAG,
                    "action up or cancel:" + action + ",triggeredCancel:" + mTriggeredCancel);
                handleCancelOrEnd(mTriggeredCancel);

                break;
        }
        return true;
    }

    private void handleCancelOrEnd(boolean cancel) {
        if (!mHasDealAudio) {
            if (cancel) {
                OpusAudioRecorder.getInstance().stopRecording(false, true);
            } else {
                OpusAudioRecorder.getInstance().stopRecording(true, true);
            }
            mHasDealAudio = true;
        }
        setPressed(false);
        if (mRecordingView != null) {
            mRecordingView.setVisibility(View.GONE);
        }
        this.setText(R.string.hold_to_talk);
        removeCallbacks(mUpdateTimeRunnable);
        cleanUp();
    }

    private void cleanUp() {
        mTriggeredCancel = false;
        mTimeValue = 0;
    }

    public void onSlideTimeout() {
        handleCancelOrEnd(false);
    }

    public class UpdateTimeRunnable implements Runnable {

        @Override
        public void run() {
            mTimeValue++;
            LogUtil.i(TAG, "record time:" + mTimeValue);
            if (mTimeValue >= GlobalParams.MAX_AUDIO) {
                onSlideTimeout();
                return;
            }
            postDelayed(this, 1000);
        }
    }
}
