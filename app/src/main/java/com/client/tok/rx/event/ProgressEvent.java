package com.client.tok.rx.event;

public class ProgressEvent {
    private String msgId;
    private float progress;
    private int status;

    public ProgressEvent(String msgId, float progress, int status) {
        this.msgId = msgId;
        this.progress = progress;
        this.status = status;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProgressEvent{"
            + "msgId='"
            + msgId
            + '\''
            + ", progress="
            + progress
            + ", status="
            + status
            + '}';
    }
}
