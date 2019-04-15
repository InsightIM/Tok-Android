package com.client.tok.bot;

import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.constant.BotType;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import im.tox.tox4j.core.data.ToxNickname;
import java.util.ArrayList;
import java.util.List;

public class BotManager {
    private String BOT_FILE = StringUtils.getTextFromResId(R.string.bot_list_file);
    private static BotManager sInstance;
    private List<BotInfo> botList;

    private BotManager() {
        if (botList == null) {
            String json = FileUtilsJ.readFromAsset(TokApplication.getInstance(), BOT_FILE);
            if (StringUtils.isEmpty(json)) {
                botList = new ArrayList<>();
            } else {
                botList = new Gson().fromJson(json, new TypeToken<List<BotInfo>>() {
                }.getType());
            }
        }
    }

    public static BotManager getInstance() {
        if (sInstance == null) {
            synchronized (BotManager.class) {
                if (sInstance == null) {
                    sInstance = new BotManager();
                }
            }
        }
        return sInstance;
    }

    public String getAddFriendBotTokId() {
        String tokId = "";
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getType() == BotType.FIND_FRIEND_BOT.getType()) {
                    tokId = botInfo.getTokId();
                    break;
                }
            }
        }
        return tokId;
    }

    public String getAddFriendBotPk() {
        String key = "";
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getType() == BotType.FIND_FRIEND_BOT.getType()) {
                    key = PkUtils.getPkFromAddress(botInfo.getTokId());
                    break;
                }
            }
        }
        return key;
    }

    public ContactsInfo getAddFriendBotInfo(String key) {
        ContactsInfo info = null;
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getTokId().contains(key)) {
                    info = new ContactsInfo();
                    info.setKey(new ContactsKey(key));
                    info.setName(ToxNickname.unsafeFromValue(botInfo.getName().getBytes()));
                    info.setSignature(botInfo.getSignature());
                    info.setBot(true);
                    info.setBotType(botInfo.getType());
                    info.setTokId(botInfo.getTokId());
                    break;
                }
            }
        }
        return info;
    }
}
