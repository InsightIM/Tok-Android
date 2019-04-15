package com.client.tok.rx.event;

public class RichMsgEvent {
    public static final String ID = "RICH_MSG_1000000";
    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    private int status;

    public RichMsgEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
