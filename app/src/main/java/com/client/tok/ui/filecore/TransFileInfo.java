package com.client.tok.ui.filecore;

import com.client.tok.pagejump.GlobalParams;

public class TransFileInfo {
    private int chatType;
    //when sending message,this is receiver pk; if receive message ,this is sender pk
    private String toOrFromPk;
    private int toGroupNumber;
    private String fileName;
    private long createTime;

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getToOrFromPk() {
        return toOrFromPk;
    }

    public void setToOrFromPk(String toOrFromPk) {
        this.toOrFromPk = toOrFromPk;
    }

    public int getToGroupNumber() {
        return toGroupNumber;
    }

    public void setToGroupNumber(int toGroupNumber) {
        this.toGroupNumber = toGroupNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCreateTime() {
        return createTime > 0 ? createTime * 1000 : System.currentTimeMillis();
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isDataValid() {
        return String.valueOf(chatType).equals(GlobalParams.CHAT_FRIEND);
    }

    @Override
    public String toString() {
        return "TransFileInfo{"
            + "chatType="
            + chatType
            + ", toOrFromPk='"
            + toOrFromPk
            + '\''
            + ", toGroupNumber="
            + toGroupNumber
            + ", fileName='"
            + fileName
            + '\''
            + ", createTime="
            + createTime
            + '}';
    }
}
