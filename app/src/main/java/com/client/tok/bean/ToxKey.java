package com.client.tok.bean;

import com.client.tok.utils.ByteUtil;
import java.io.Serializable;

public class ToxKey implements Serializable {
    public String key;

    public ToxKey(String key) {
        this.key = key;
    }

    public byte[] getBytes() {
        return ByteUtil.hexStr2Bytes(key);
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
