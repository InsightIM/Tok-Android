package com.client.tok.ui.chat2.holders;

import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.Message;
import com.client.tok.constant.MessageType;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.widget.MenuPopWindow;

/**
 * viewHolder:new friend hello
 *
 * R.layout.chat_prompt_text
 */
public class MsgPromptHolder extends BaseMsgHolder {
    private String TAG = "MsgHelloHolder";
    private TextView mTimeTv;
    private TextView mHelloTv;

    public MsgPromptHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mHelloTv = itemView.findViewById(R.id.id_msg_prompt_tv);
    }

    @Override
    public void setMessage(final Message curMsg, Message lastMsg) {
        super.setMessage(curMsg, lastMsg);
        setTime(curMsg, lastMsg, mTimeTv);
        mMenuType = MenuPopWindow.TYPE_MSG_TXT;
        mHelloTv.setText(curMsg.getMessage());

        mHelloTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = curMsg.getMsgTypeVal();
                if (type == MessageType.PROMPT_ADD_OFFLINE_BOT.getType()) {
                    PageJumpIn.jumpOfflineBotInfoPage(mContext);
                }
            }
        });
    }
}
