package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactsInfo;

public class AntoxOnTypingChangeCallback {
    public void friendTyping(ContactsInfo friendInfo, boolean isTyping) {
        //ToxSingleton.typingMap().put(friendInfo.getKey(), isTyping);
        //State.typing().onNext(true);
    }
}
