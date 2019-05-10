package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import com.client.tok.ui.chat2.timer.MsgTimer;
import com.client.tok.utils.LogUtil;

public class AntoxOnReadReceiptCallback {
    private String TAG = "AntoxOnReadReceiptCallback";

    public void friendReadReceipt(ContactInfo friendInfo, int messageId) {
        //stop timer
        MsgTimer.stopTimer(messageId);
        InfoRepository infoRepo = State.infoRepo();
        int result = infoRepo.setMessageReceived(messageId, friendInfo.getKey().key);
        LogUtil.i(TAG, "AntoxOnReadReceiptCallback messageId:"
            + messageId
            + ",friendKey:"
            + friendInfo.getKey().getKey()
            + ",update result:"
            + result);
    }
}
