package com.client.tok.bean;

import com.client.tok.utils.ByteUtil;
import com.google.gson.annotations.SerializedName;
import im.tox.core.network.Port;
import im.tox.tox4j.core.data.ToxPublicKey;

public class DhtNode {
    private String owner;
    private String ipv4;

    @SerializedName("public_key")
    private String publicKey;
    private int port;

    public String getOwner() {
        return owner;
    }

    public String getIpv4() {
        return ipv4;
    }

    public ToxPublicKey getPublicKey() {
        return ToxPublicKey.unsafeFromValue(ByteUtil.hexStr2Bytes(publicKey));
    }

    public Port getPort() {
        return Port.unsafeFromInt(port);
    }

    @Override
    public String toString() {
        return "DhtNode{"
            + "ipv4='"
            + ipv4
            + '\''
            + ", publicKey='"
            + publicKey
            + '\''
            + ", port="
            + port
            + '}';
    }
}
