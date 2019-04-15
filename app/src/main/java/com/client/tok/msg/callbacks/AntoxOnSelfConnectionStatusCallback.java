package com.client.tok.msg.callbacks;

import com.client.tok.rx.RxBus;
import im.tox.tox4j.core.enums.ToxConnection;

public class AntoxOnSelfConnectionStatusCallback {
    public void selfConnectionStatus(ToxConnection toxConnection) {
        RxBus.publish(toxConnection);
    }
}
