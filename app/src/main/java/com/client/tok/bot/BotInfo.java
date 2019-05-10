package com.client.tok.bot;

public class BotInfo {
    private String tokId;
    private String name;
    private String provider;
    private int type;
    private String signature;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokId() {
        return tokId;
    }

    public void setTokId(String tokId) {
        this.tokId = tokId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
