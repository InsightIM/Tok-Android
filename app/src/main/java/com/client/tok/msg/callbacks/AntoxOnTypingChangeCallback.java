package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;

public class AntoxOnTypingChangeCallback {
    public void friendTyping(ContactInfo friendInfo, boolean isTyping) {
        //ToxSingleton.typingMap().put(friendInfo.getKey(), isTyping);
        //State.typing().onNext(true);
    }
}
