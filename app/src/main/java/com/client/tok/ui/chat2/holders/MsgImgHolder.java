package com.client.tok.ui.chat2.holders;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.Message;
import com.client.tok.constant.MessageType;
import com.client.tok.tox.State;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.ui.imgzoom.ImgViewInfoList;
import com.client.tok.ui.imgzoom.ImgZoomManager;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ProgressView;

/**
 * viewHolder:Image
 *
 * R.layout.chat_msg_file
 */
public class MsgImgHolder extends BaseMsgHolder implements View.OnClickListener {
    private String TAG = "MsgImgHolder";
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

    public MsgImgHolder(View itemView, Contract.IChatPresenter presenter) {
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
        mFriendErrIv = itemView.findViewById(R.id.id_friend_img_err_iv);
        itemView.findViewById(R.id.id_msg_friend_img_layout).setOnClickListener(this);

        mMineLayout = itemView.findViewById(R.id.id_msg_mine_layout);
        mMyPortraitView = itemView.findViewById(R.id.id_msg_mine_portrait_iv);
        mMyMsgIv = itemView.findViewById(R.id.id_msg_mine_iv);
        mMineProView = itemView.findViewById(R.id.id_msg_mine_progress);
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
            LogUtil.i(TAG, "friend img:" + getImgPath(curMsg));
            ImageLoadUtils.loadMask(mContext, getImgPath(curMsg), mFriendMsgIv,
                R.drawable.bubble_text_receive);
            showProgressBar(curMsg, mFriendProView, mFriendMsgIv, mFriendErrIv);
            mMineLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_friend_img_layout));
        } else {
            mMineLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mMyPortraitView);
            LogUtil.i(TAG, "mine img:" + getImgPath(curMsg));
            ImageLoadUtils.loadMask(mContext, getImgPath(curMsg), mMyMsgIv,
                R.drawable.bubble_text_send);
            showProgressBar(curMsg, mMineProView, mMyMsgIv, mMineErrIv);
            mFriendLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_mine_img_layout));
        }
    }

    private String getImgPath(Message curMsg) {
        return ImageUtils.getImgPath(curMsg.getMessage());
    }

    @Override
    public void onClick(View v) {
        if (mCurMsg != null && MessageType.FILE_TRANSFER.getType() == mCurMsg.getMsgTypeVal()) {
            Rect bound = ImgZoomManager.computerBound(v);
            ImgViewInfoList list = State.infoRepo()
                .getImgMessage(mCurMsg.getKey().toString(), mCurMsg.getMessage(), bound);
            ImgZoomManager.showImgList(mContext, list.getImgViewInfoList(), list.getCurIndex());
        }
    }
}
