package com.client.tok.bean;

import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.StringUtils;

public class ToxMeName {
    private String userName;
    private String domain;

    public ToxMeName(String userName, String domain) {
        this.userName = userName;
        this.domain = domain;
    }

    public static ToxMeName fromString(String toxMeName, boolean useToxMe) {
        String[] split = toxMeName.split("@");
        String domain = "";
        if (useToxMe) {
            domain = split.length == 1 ? GlobalParams.DEFAULT_TOX_ME_DOMAIN : split[1];
        }

        return new ToxMeName(split[0], domain);
    }

    public String fullAddress() {
        if (StringUtils.isEmpty(domain)) {
            return userName;
        } else {
            return userName + "@" + domain;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
