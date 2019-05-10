package com.client.tok.ui.addfriends;

import com.client.tok.R;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.ToxAddress;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;

public class AddFriendsModel {
    public static int TOK_ID_VALID = -1;

    public boolean addFriendById(String tokId, String alias, String msg) {
        InfoRepository infoRepo = State.infoRepo();
        ToxAddress address = new ToxAddress(tokId);
        ContactsKey key = address.getKey();
        if (StringUtils.isEmpty(alias)) {
            alias = "";
        }
        if (StringUtils.isEmpty(msg)) {
            msg = StringUtils.getTextFromResId(R.string.default_signature);
        }
        try {
            ToxFriendNumber number = ToxManager.getManager().toxBase.sendAddFriendRequest(address,
                ToxFriendRequestMessage.unsafeFromValue(msg.getBytes()));
            if (number.value >= 0) {
                ToxManager.getManager().save();
                infoRepo.addFriend(key, "", alias,
                    StringUtils.getTextFromResId(R.string.add_friend_request_has_send));

                //prevent already-added friend from having an existing friend request
                infoRepo.delFriendRequest(key.key);
                infoRepo.addConversation(key.key);
                NotifyManager.getInstance().cleanNotify(key.hashCode());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int checkIdValid(String tokId) {
        int resultId = TOK_ID_VALID;
        if (!StringUtils.isEmpty(tokId)) {
            if (PkUtils.isAddressValid(tokId)) {
                if (!isMyOwnChatId(tokId)) {
                    if (!isFriendExist(tokId)) {
                        //ok
                    } else {
                        resultId = R.string.add_friend_friend_exists;//friend exist
                    }
                } else {
                    resultId = R.string.can_not_add_yourself;//can't add your self
                }
            } else {
                resultId = R.string.tok_id_invalid;//chatId format not right
            }
        } else {
            resultId = R.string.input_tok_id;//can't empty
        }
        return resultId;
    }

    public boolean isMyOwnChatId(String chatId) {
        String ownChatId =
            ToxManager.getManager().toxBase.getSelfAddress().getAddress().toUpperCase();
        return ownChatId.equals(chatId);
    }

    public boolean isFriendExist(String chatId) {
        ToxAddress address = new ToxAddress(chatId);
        return State.infoRepo().doesContactExist(address.getKey().key);
    }
}
