package com.client.tok.ui.chat2.holders;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.Message;
import com.client.tok.constant.MessageType;
import com.client.tok.pagejump.PageJumpOut;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.FileIconView;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ProgressView;

/**
 * viewHolder:File
 *
 * R.layout.chat_msg_file
 */
public class MsgFileHolder extends BaseMsgHolder implements View.OnClickListener {
    private TextView mTimeTv;
    //friend message:layout
    private View mFriendLayout;
    private PortraitView mFriendPortraitView;
    private FileIconView mFriendFileIconIv;
    private TextView mSenderNameTv;
    private TextView mFriendFileNameIv;
    private TextView mFriendFileSizeIv;
    private ProgressView mFriendProView;
    private ImageView mFriendErrIv;
    //mine message:layout
    private View mMineLayout;
    private PortraitView mMyPortraitView;
    private FileIconView mMineFileIconIv;
    private TextView mMyFileNameIv;
    private TextView mMyFileSizeIv;
    private ProgressView mMineProView;
    private ImageView mMineErrIv;

    public MsgFileHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mFriendLayout = itemView.findViewById(R.id.id_msg_friend_layout);
        mSenderNameTv = itemView.findViewById(R.id.id_msg_sender_name_tv);
        mFriendFileIconIv = itemView.findViewById(R.id.id_msg_friend_file_icon_iv);
        mFriendPortraitView = itemView.findViewById(R.id.id_msg_friend_portrait_iv);
        mFriendFileNameIv = itemView.findViewById(R.id.id_msg_friend_file_name_tv);
        mFriendFileSizeIv = itemView.findViewById(R.id.id_msg_friend_file_size_tv);
        mFriendProView = itemView.findViewById(R.id.id_msg_friend_progress);
        mFriendErrIv = itemView.findViewById(R.id.id_friend_img_err_iv);
        itemView.findViewById(R.id.id_msg_friend_file_layout).setOnClickListener(this);

        mMineLayout = itemView.findViewById(R.id.id_msg_mine_layout);
        mMineFileIconIv = itemView.findViewById(R.id.id_msg_my_file_icon_iv);
        mMyPortraitView = itemView.findViewById(R.id.id_msg_mine_portrait_iv);
        mMyFileNameIv = itemView.findViewById(R.id.id_msg_mine_file_name_tv);
        mMyFileSizeIv = itemView.findViewById(R.id.id_msg_mine_file_size_tv);
        mMineProView = itemView.findViewById(R.id.id_msg_mine_progress);
        mMineErrIv = itemView.findViewById(R.id.id_mine_img_err_iv);
        itemView.findViewById(R.id.id_msg_mine_file_layout).setOnClickListener(this);
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
            String fileName = getFileName(curMsg.getMessage());
            mFriendFileIconIv.setText(fileName);
            mFriendFileNameIv.setText(fileName);
            mFriendFileSizeIv.setText(getFileSize(curMsg.getSize()));
            showProgressBar(curMsg, mFriendProView, mFriendErrIv);
            mMineLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_friend_file_layout));
        } else {
            mMineLayout.setVisibility(View.VISIBLE);
            setPortrait(curMsg, mMyPortraitView);
            String fileName = getFileName(curMsg.getMessage());
            mMineFileIconIv.setText(fileName);
            mMyFileNameIv.setText(fileName);
            mMyFileSizeIv.setText(getFileSize(curMsg.getSize()));
            showProgressBar(curMsg, mMineProView, mMineErrIv);
            mFriendLayout.setVisibility(View.GONE);
            addLongClickListener(itemView.findViewById(R.id.id_msg_mine_file_layout));
        }
    }

    private String getFileName(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
            String[] split = filePath.split("/");
            return split[split.length - 1];
        } else {
            return "";
        }
    }

    private String getFileSize(long size) {
        return Formatter.formatFileSize(TokApplication.getInstance(), size);
    }

    @Override
    public void onClick(View v) {
        if (mCurMsg != null && MessageType.FILE_TRANSFER.getType() == mCurMsg.getMsgTypeVal()) {
            PageJumpOut.openFile(TokApplication.getInstance(), mCurMsg.getMessage());
        }
    }
}
