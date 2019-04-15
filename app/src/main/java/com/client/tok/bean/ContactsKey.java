package com.client.tok.bean;

public class ContactsKey extends ToxKey {
    public ContactsKey(int groupNumber) {
        super(String.valueOf(groupNumber));
    }

    public int getGroupKey(){
        return Integer.valueOf(getKey());
    }
    public ContactsKey(String key) {
        super(key);
    }


}
