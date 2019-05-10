package com.client.tok.bot;

import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.bean.ContactInfo;
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

    public String getFindFriendBotTokId() {
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

    public String getFindFriendBotPk() {
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

    public String getOfflineBotTokId() {
        String tokId = "";
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getType() == BotType.OFFLINE_MSG_BOT.getType()) {
                    tokId = botInfo.getTokId();
                    break;
                }
            }
        }
        return tokId;
    }

    public String getOfflineBotPk() {
        String key = "";
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getType() == BotType.OFFLINE_MSG_BOT.getType()) {
                    key = PkUtils.getPkFromAddress(botInfo.getTokId());
                    break;
                }
            }
        }
        return key;
    }

    public ContactInfo getBotContactInfo(int botType) {
        ContactInfo info = null;
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getType() == botType) {
                    info = convert(botInfo);
                    break;
                }
            }
        }
        return info;
    }

    public ContactInfo getBotContactInfo(String key) {
        ContactInfo info = null;
        if (botList != null) {
            for (BotInfo botInfo : botList) {
                if (botInfo.getTokId().contains(key)) {
                    info = convert(botInfo);
                    break;
                }
            }
        }
        return info;
    }

    private ContactInfo convert(BotInfo botInfo) {
        ContactInfo info = new ContactInfo();
        info.setKey(new ContactsKey(PkUtils.getPkFromAddress(botInfo.getTokId())));
        info.setName(ToxNickname.unsafeFromValue(botInfo.getName().getBytes()));
        info.setSignature(botInfo.getSignature());
        info.setProvider(botInfo.getProvider());
        info.setBot(true);
        info.setBotType(botInfo.getType());
        info.setTokId(botInfo.getTokId());
        return info;
    }
}
