package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactsInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import com.client.tok.ui.chat2.timer.MsgTimer;
import com.client.tok.utils.LogUtil;

public class AntoxOnReadReceiptCallback {
    private String TAG = "AntoxOnReadReceiptCallback";

    public void friendReadReceipt(ContactsInfo friendInfo, int messageId) {
        //stop timer
        MsgTimer.stopTimer(messageId);
        InfoRepository inforRepo = State.infoRepo();
        inforRepo.setMessageReceived(messageId);
        LogUtil.i(TAG,
            "AntoxOnReadReceiptCallback:" + messageId + ",friendKey:" + friendInfo.getKey()
                .getKey());
    }
}
