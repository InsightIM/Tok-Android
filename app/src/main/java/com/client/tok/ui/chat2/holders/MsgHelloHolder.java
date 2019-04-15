package com.client.tok.ui.chat2.holders;

import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.Message;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.widget.MenuPopWindow;

/**
 * viewHolder:new friend hello
 *
 * R.layout.chat_msg_text
 */
public class MsgHelloHolder extends BaseMsgHolder {
    private String TAG = "MsgHelloHolder";
    private TextView mTimeTv;
    private TextView mHelloTv;

    public MsgHelloHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView, presenter);
        initViews();
    }

    private void initViews() {
        mTimeTv = itemView.findViewById(R.id.id_msg_time_tv);
        mHelloTv = itemView.findViewById(R.id.id_msg_hello_tv);
    }

    @Override
    public void setMessage(Message curMsg, Message lastMsg) {
        super.setMessage(curMsg, lastMsg);
        setTime(curMsg, lastMsg, mTimeTv);
        mMenuType = MenuPopWindow.TYPE_MSG_TXT;
        mHelloTv.setText(curMsg.getMessage());
    }
}
