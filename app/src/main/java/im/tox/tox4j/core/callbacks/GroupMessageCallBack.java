package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.enums.ToxMessageType;

public interface GroupMessageCallBack {
    void groupMessage(ToxFriendNumber friendNumber, ToxMessageType messageType, int timeDelta,
        ToxFriendMessage message);
}
