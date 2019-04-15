package com.client.tok.rx.event;

/**
 * RxBus
 */
public class PortraitEvent {
    private String key;
    private String fileName;

    public PortraitEvent(String key, String fileName) {
        this.key = key;
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "PortraitEvent{" + "key='" + key + '\'' + ", fileName='" + fileName + '\'' + '}';
    }
}
