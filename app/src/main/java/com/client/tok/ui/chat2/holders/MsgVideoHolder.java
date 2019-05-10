package com.client.tok.ui.chat2.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.Message;
import com.client.tok.constant.MessageType;
import com.client.tok.pagejump.PageJumpOut;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ProgressView;
import java.io.File;

/**
 * viewHolder:video
 *
 * R.layout.chat_video_file
 */
public class MsgVideoHolder extends BaseMsgHolder implements View.OnClickListener {
    private String TAG = "MsgVideoHolder";
    private TextView mTimeTv;
    //friend message:layout
    private View mFriendLayout;
    private PortraitView mFriendPortraitView;
    private TextView mSenderNameTv;
    private ImageView mFriendMsgIv;
    private ProgressView mFriendProView;
    private ImageView mFriendErrIv;
    //my message:layout
    private View mMineLayout;
    private PortraitView mMyPortraitView;
    private ImageView mMyMsgIv;
    private ProgressView mMineProView;
    private ImageView mMineErrIv;

    public MsgVideoHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mFriendLayout = itemView.findViewById(R.id.id_msg_friend_layout);
        mSenderNameTv = itemView.findViewById(R.id.id_msg_sender_name_tv);
        mFriendPortraitView = itemView.findViewById(R.id.id_msg_friend_portrait_iv);
        mFriendMsgIv = itemView.findViewById(R.id.id_msg_friend_iv);
        mFriendProView = itemView.findViewById(R.id.id_msg_friend_progress);
        mFriendProView.setFinishIcon(R.drawable.play);
        mFriendErrIv = itemView.findViewById(R.id.id_friend_img_err_iv);
        itemView.findViewById(R.id.id_msg_friend_img_layout).setOnClickListener(this);

        mMineLayout = itemView.findViewById(R.id.id_msg_mine_layout);
        mMyPortraitView = itemView.findViewById(R.id.id_msg_mine_portrait_iv);
        mMyMsgIv = itemView.findViewById(R.id.id_msg_mine_iv);
        mMineProView = itemView.findViewById(R.id.id_msg_mine_progress);
        mMineProView.setFinishIcon(R.drawable.play);
        mMineErrIv = itemView.findViewById(R.id.id_mine_img_err_iv);
        itemView.findViewById(R.id.id_msg_mine_img_layout).setOnClickListener(this);
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
            ImageLoadUtils.loadVideoMask(mContext, getVideoPath(curMsg), mFriendMsgIv,
                R.drawable.bubble_text_receive);
            showProgressBar(curMsg, mFriendProView, mFriendMsgIv, mFriendErrIv);
            mMineLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_friend_img_layout));
        } else {
            mMineLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mMyPortraitView);
            ImageLoadUtils.loadVideoMask(mContext, getVideoPath(curMsg), mMyMsgIv,
                R.drawable.bubble_text_send);
            showProgressBar(curMsg, mMineProView, mMyMsgIv, mMineErrIv);
            mFriendLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_mine_img_layout));
        }
    }

    private String getVideoPath(Message curMsg) {
        String fileMsg = curMsg.getMessage();
        LogUtil.i(TAG, "getVideoPath:" + fileMsg);
        if (fileMsg.contains(File.separator)) {
            return fileMsg;
        } else {
            return StorageUtil.getFilesFolder() + fileMsg;
        }
    }

    @Override
    public void onClick(View v) {
        if (mCurMsg != null && MessageType.FILE_TRANSFER.getType() == mCurMsg.getMsgTypeVal()) {
            PageJumpOut.openFile(TokApplication.getInstance(), mCurMsg.getMessage());
        }
    }
}
