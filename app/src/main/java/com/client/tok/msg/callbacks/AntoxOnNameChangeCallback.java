package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxNickname;

public class AntoxOnNameChangeCallback {
    private String TAG = "OnNameChangeCallback";

    public void friendName(ContactInfo friendInfo, ToxNickname nameBytes) {
        String name = StringUtils.removeNewLines(new String(nameBytes.value));
        LogUtil.i(TAG, "key:" + friendInfo.getKey().key + ":name:" + name);
        InfoRepository infoRepo = State.infoRepo();
        infoRepo.updateContactName(friendInfo.getKey(), name);
    }
}
