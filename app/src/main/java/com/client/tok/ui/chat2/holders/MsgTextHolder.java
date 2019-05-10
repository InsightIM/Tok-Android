package com.client.tok.ui.chat2.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.Message;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.MsgTextView;
import com.client.tok.widget.PortraitView;

/**
 * viewHolder:text
 *
 * R.layout.chat_msg_text
 */
public class MsgTextHolder extends BaseMsgHolder {
    private String TAG = "MsgTextHolder";
    private TextView mTimeTv;
    //friend message:layout
    private View mFriendLayout;
    private PortraitView mFriendPortraitView;
    private TextView mSenderNameTv;
    private MsgTextView mFriendMsgTv;
    //my message:layout
    private View mMineLayout;
    private PortraitView mMyPortraitView;
    private MsgTextView mMyMsgTv;
    private ImageView mFailedIv;
    private ImageView mSentStatus2Iv;

    public MsgTextHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mFriendLayout = itemView.findViewById(R.id.id_msg_friend_layout);
        mSenderNameTv = itemView.findViewById(R.id.id_msg_sender_name_tv);
        mFriendPortraitView = itemView.findViewById(R.id.id_msg_friend_portrait_iv);
        mFriendMsgTv = itemView.findViewById(R.id.id_msg_friend_tv);
        mFriendMsgTv.setEnableOrderLink(mPresenter.isEnableOrderLink());

        mMineLayout = itemView.findViewById(R.id.id_msg_mine_layout);
        mMyPortraitView = itemView.findViewById(R.id.id_msg_mine_portrait_iv);
        mMyMsgTv = itemView.findViewById(R.id.id_msg_mine_tv);
        mMyMsgTv.setEnableOrderLink(mPresenter.isEnableOrderLink());
        mFailedIv = itemView.findViewById(R.id.id_msg_mine_send_status_iv);
        mFailedIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.onMsgFailDeal(mCurMsg);
                }
            }
        });
        mSentStatus2Iv = itemView.findViewById(R.id.id_msg_mine_send_status2_iv);
    }

    @Override
    public void setMessage(Message curMsg, Message lastMsg) {
        super.setMessage(curMsg, lastMsg);
        setTime(curMsg, lastMsg, mTimeTv);
        mMenuType = MenuPopWindow.TYPE_MSG_TXT;
        if (!curMsg.isMine()) {
            mFriendLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mFriendPortraitView);
            mFriendMsgTv.setMsg(curMsg.getMessage());
            showSenderName(mSenderNameTv);
            mFriendMsgTv.setOnLongClickListener(this);
            mMineLayout.setVisibility(View.GONE);
            addLongClickListener(mFriendMsgTv);
        } else {
            mMineLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mMyPortraitView);
            mMyMsgTv.setMsg(curMsg.getMessage());
            mFriendLayout.setVisibility(View.GONE);
            setMsgStatus(curMsg.getSentStatus());
            addLongClickListener(mMyMsgTv);
        }
    }

    private void setMsgStatus(int sentStatus) {
        LogUtil.i(TAG, "msgTxt sentStatus:" + sentStatus);
        switch (sentStatus) {
            case GlobalParams.SEND_SUCCESS:
                mFailedIv.setVisibility(View.GONE);
                mSentStatus2Iv.setVisibility(View.VISIBLE);
                mSentStatus2Iv.setImageResource(R.drawable.msg_success);
                break;
            case GlobalParams.SEND_ING:
                mFailedIv.setVisibility(View.GONE);
                mSentStatus2Iv.setVisibility(View.VISIBLE);
                mSentStatus2Iv.setImageResource(R.drawable.msg_sending);
                break;
            case GlobalParams.SEND_FAIL:
                mFailedIv.setVisibility(View.VISIBLE);
                mSentStatus2Iv.setVisibility(View.GONE);
                break;
        }
    }
}
