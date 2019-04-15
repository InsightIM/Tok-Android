package com.client.tok.bean;


public class ToxData {
    private byte[] fileBytes;
    private ToxAddress address;

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public ToxAddress getAddress() {
        return address;
    }

    public void setAddress(ToxAddress address) {
        this.address = address;
    }
}
