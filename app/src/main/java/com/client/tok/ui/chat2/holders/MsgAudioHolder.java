package com.client.tok.ui.chat2.holders;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.Message;
import com.client.tok.constant.MessageType;
import com.client.tok.rx.event.ProgressEvent;
import com.client.tok.media.player.PlayStatus;
import com.client.tok.media.player.audio.AudioPlayer;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ProgressView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * viewHolder:Audio
 *
 * R.layout.chat_msg_file
 */
public class MsgAudioHolder extends BaseMsgHolder implements View.OnClickListener {
    private String TAG = "MsgAudioHolder";
    private TextView mTimeTv;
    //friend info:layout
    private RelativeLayout mFriendLayout;
    private RelativeLayout mFriendInfoLayout;
    private TextView mSenderNameTv;
    private PortraitView mFriendPortraitView;
    private TextView mFriendAudioTimeTv;
    private ProgressView mFriendAudioStatusIv;
    private ImageView mFriendUnreadIv;
    private ImageView mFriendAudioErrIv;
    //friend info:layout
    private RelativeLayout mMineLayout;
    private RelativeLayout mMineInfoLayout;
    private PortraitView mMyPortraitView;
    private TextView mMyAudioTimeTv;
    private ProgressView mMineAudioStatusIv;
    private ImageView mMineAudioErrIv;
    private AnimationDrawable mAnimationDrawable;
    private static boolean isBlinking = false;
    private int minWidth = ScreenUtils.dip(80);
    private int maxWidth = ScreenUtils.getScreenWidth(TokApplication.getInstance()) * 5 / 6;
    private int everyDurationWidth = (maxWidth - minWidth) / (GlobalParams.MAX_AUDIO / 2);

    private ImageView mAudioIconIv;

    public MsgAudioHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mFriendLayout = itemView.findViewById(R.id.id_msg_friend_layout);
        mSenderNameTv = itemView.findViewById(R.id.id_msg_sender_name_tv);
        mFriendInfoLayout = itemView.findViewById(R.id.id_msg_friend_audio_layout);
        mFriendInfoLayout.setOnClickListener(this);
        mFriendPortraitView = itemView.findViewById(R.id.id_msg_friend_portrait_iv);
        mFriendAudioTimeTv = itemView.findViewById(R.id.id_msg_friend_audio_time_tv);
        mFriendAudioStatusIv = itemView.findViewById(R.id.id_msg_friend_status_iv);
        mFriendUnreadIv = itemView.findViewById(R.id.id_friend_audio_unread_iv);
        mFriendAudioErrIv = itemView.findViewById(R.id.id_friend_audio_error_iv);

        mMineLayout = itemView.findViewById(R.id.id_msg_mine_layout);
        mMineInfoLayout = itemView.findViewById(R.id.id_msg_mine_audio_layout);
        mMineInfoLayout.setOnClickListener(this);
        mMyPortraitView = itemView.findViewById(R.id.id_msg_mine_portrait_iv);
        mMyAudioTimeTv = itemView.findViewById(R.id.id_msg_mine_audio_time_tv);
        mMineAudioStatusIv = itemView.findViewById(R.id.id_msg_mine_status_iv);
        mMineAudioErrIv = itemView.findViewById(R.id.id_mine_audio_error_iv);
    }

    @Override
    public void setMessage(Message curMsg, Message lastMsg) {
        super.setMessage(curMsg, lastMsg);
        setTime(curMsg, lastMsg, mTimeTv);
        mMenuType = MenuPopWindow.TYPE_MSG_FILE;
        if (!curMsg.isMine()) {
            mFriendLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mFriendPortraitView);
            showSenderName(mSenderNameTv);
            int duration = getAudioTime(curMsg.getMessage());
            mFriendAudioTimeTv.setVisibility(duration > 0 ? View.VISIBLE : View.GONE);
            mFriendAudioTimeTv.setText(
                StringUtils.formatTxFromResId(R.string.time_second, duration));
            mFriendAudioStatusIv.setProType(ProgressView.TYPE_IMG_RECEIVER);
            mFriendUnreadIv.setVisibility(curMsg.isHasPlayed() ? View.GONE : View.VISIBLE);
            showProgressBar(curMsg, mFriendAudioStatusIv, mFriendAudioErrIv);
            mMineLayout.setVisibility(View.GONE);
            mAudioIconIv = itemView.findViewById(R.id.id_msg_friend_audio_icon_iv);
            resetWidth(mFriendInfoLayout, duration);

            addLongClickListener(itemView.findViewById(R.id.id_msg_friend_audio_layout));
        } else {
            mMineLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mMyPortraitView);
            int duration = getAudioTime(curMsg.getMessage());
            mMyAudioTimeTv.setVisibility(duration > 0 ? View.VISIBLE : View.GONE);
            mMyAudioTimeTv.setText(StringUtils.formatTxFromResId(R.string.time_second, duration));
            mMineAudioStatusIv.setProType(ProgressView.TYPE_IMG_SENDER);
            showProgressBar(curMsg, mMineAudioStatusIv, mMineAudioErrIv);
            mAudioIconIv = itemView.findViewById(R.id.id_msg_my_audio_icon_iv);
            mFriendLayout.setVisibility(View.GONE);
            resetWidth(mMineInfoLayout, duration);

            addLongClickListener(itemView.findViewById(R.id.id_msg_mine_audio_layout));
        }

        //恢复或停止原来的blink动画
        LogUtil.i(TAG, "msgId:" + mCurMsg.getId() + ",blinkId:" + AudioPlayer.getPlayingId());
        if (String.valueOf(mCurMsg.getId()).equals(AudioPlayer.getPlayingId())) {
            listen(String.valueOf(mCurMsg.getId()));
            startBlink();
        } else {
            stopBlink();
        }
    }

    /**
     * reset audio width
     *
     * @param layout view
     * @param duration length(second)
     */
    private void resetWidth(ViewGroup layout, int duration) {
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        int width = minWidth + everyDurationWidth * duration;
        if (width < minWidth) {
            width = minWidth;
        }
        if (width > maxWidth) {
            width = maxWidth;
        }
        params.width = width;
        layout.setLayoutParams(params);
    }

    private String getFileName(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
            String[] split = filePath.split("/");
            return split[split.length - 1];
        } else {
            return "";
        }
    }

    private int getAudioTime(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
            try {
                String pattern = "_\\d+\\.";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(filePath);
                String result = "";
                if (m.find()) {
                    result = m.group();
                    result = result.substring(1, result.length() - 1);
                }
                if (result.length() > 6) {
                    return 0;
                } else {
                    return (Integer.valueOf(result) + 999) / 1000;
                }
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (mCurMsg != null && MessageType.FILE_TRANSFER.getType() == mCurMsg.getMsgTypeVal()) {
            if (!mCurMsg.isHasPlayed()) {
                mPresenter.setPlayed(mCurMsg.getId());
            }
            listen(String.valueOf(mCurMsg.getId()));
            AudioPlayer.getInstance()
                .play(String.valueOf(mCurMsg.getId()),
                    StorageUtil.getFilesFolder() + getFileName(mCurMsg.getMessage()));
        }
    }

    @Override
    public void chatLayout(ProgressEvent event) {
        super.chatLayout(event);
        if (event != null) {
            int status = event.getStatus();
            LogUtil.i(TAG, "chatLayout:" + event.toString());
            switch (status) {
                case PlayStatus.STATUS_PLAYING:
                    startBlink();
                    break;
                case PlayStatus.STATUS_ERROR:
                case PlayStatus.STATUS_PAUSE:
                case PlayStatus.STATUS_DONE:
                    stopBlink();
                    stopListen();
                    break;
            }
        }
    }

    private void startBlink() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
        if (!mCurMsg.isMine()) {
            mAudioIconIv.setImageResource(R.drawable.audio_frame_left);
        } else {
            mAudioIconIv.setImageResource(R.drawable.audio_frame_right);
        }

        mAnimationDrawable = (AnimationDrawable) mAudioIconIv.getDrawable();
        mAnimationDrawable.start();
        isBlinking = true;
    }

    private void stopBlink() {
        if (isBlinking) {
            if (mAnimationDrawable != null) {
                mAnimationDrawable.stop();
            }
            int stopDrawable =
                mCurMsg.isMine() ? R.drawable.audio_level3_right : R.drawable.audio_level3_left;
            mAudioIconIv.setImageResource(stopDrawable);
        }
    }
}
