package com.client.tok.bean;

import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.ByteUtil;

public class ToxAddress {
    private String address;

    public ToxAddress(String address) {
        this.address =
            address.toLowerCase().replace(GlobalParams.CHAT_ID_PRE_SUFFIX, "").toUpperCase();
    }

    public ToxAddress(byte[] bytes) {
        this(ByteUtil.bytes2HexStr(bytes));
    }

    public byte[] getBytes() {
        return ByteUtil.hexStr2Bytes(address);
    }

    public ContactsKey getKey() {
        return new ContactsKey(address.substring(0, GlobalParams.PK_LENGTH));
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address;
    }
}
