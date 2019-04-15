package com.client.tok.ui.infor.friend;

import com.client.tok.R;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bot.BotManager;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;

public class FriendInfoPresenter implements FriendInfoContract.IFriendInfoPresenter {
    private String TAG = "FriendInforPresenter";
    private String mFriendKey;
    private ContactsInfo mFriendInfo;
    private FriendInfoContract.IFriendInfoView mFriendInfoView;
    private AddFriendsModel mAddFriendModel = new AddFriendsModel();

    public FriendInfoPresenter(FriendInfoContract.IFriendInfoView friendInfoView) {
        mFriendInfoView = friendInfoView;
        mFriendInfoView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        mFriendKey = mFriendInfoView.getDataIntent().getStringExtra(IntentConstants.TOK_ID);
        if (mFriendKey != null && PkUtils.isAddressValid(mFriendKey)) {
            //get pk by address
            mFriendKey = PkUtils.getPkFromAddress(mFriendKey);
        }
        getFriendInfo();
    }

    private void getFriendInfo() {
        try {
            //find friend from db
            mFriendInfo = State.infoRepo().getFriendInfo(mFriendKey);
            if (mFriendInfo != null) {
                mFriendInfoView.showFriendInfo(mFriendInfo);
            } else {
                //find friend from bot list
                mFriendInfo = BotManager.getInstance().getAddFriendBotInfo(mFriendKey);
                if (mFriendInfo != null) {
                    mFriendInfoView.showAddFriendInfo(mFriendInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFriendByTokId() {
        int checkResult = mAddFriendModel.checkIdValid(mFriendInfo.getTokId());
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            mAddFriendModel.addFriendById(mFriendInfo.getTokId(), mFriendInfo.getName().toString(),
                "");
            mFriendInfoView.showMsg(
                StringUtils.getTextFromResId(R.string.add_friend_request_has_send));
            mFriendInfoView.showSendMsg();
        } else {
            mFriendInfoView.showMsg(StringUtils.getTextFromResId(checkResult));
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "friendInfo presenter onDestroy");
    }
}
