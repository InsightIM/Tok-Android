package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.constant.MessageType;
import com.client.tok.tox.MsgHelper;
import com.client.tok.utils.LogUtil;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.enums.ToxMessageType;

public class AntoxOnMessageCallback {
    private String TAG = "AntoxOnMessageCallback";

    public AntoxOnMessageCallback() {
    }

    public void friendMessage(ContactInfo friendInfo, ToxMessageType messageType, int timeDelta,
        ToxFriendMessage message) {
        //timeDelta is the send message timeStamp,but from tox is second,we should translate to millsecond
        long timeMills = timeDelta * 1000L;
        LogUtil.i(TAG, "timeMills:" + timeMills);
        MsgHelper.receiveMessage(friendInfo, new String(message.value),
            MessageType.fromToxMessageType(messageType), -1, timeMills);
    }
}
