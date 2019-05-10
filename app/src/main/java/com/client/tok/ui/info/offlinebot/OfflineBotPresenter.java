package com.client.tok.ui.info.offlinebot;

import android.arch.lifecycle.Observer;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bot.BotManager;
import com.client.tok.constant.BotType;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;

public class OfflineBotPresenter implements OfflineBotContract.IOfflineBotViewPresenter {
    private String TAG = "OfflineBotPresenter";
    private OfflineBotContract.IOfflineBotView mOfflineBotView;
    private ContactInfo mContactInfo;
    private String mOfflineBotPk;
    private String mOfflineBotTokId;
    private boolean mIsFriend;

    public OfflineBotPresenter(OfflineBotContract.IOfflineBotView offlineBotView) {
        this.mOfflineBotView = offlineBotView;
        offlineBotView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        mContactInfo =
            BotManager.getInstance().getBotContactInfo(BotType.OFFLINE_MSG_BOT.getType());
        mOfflineBotView.showBotInfo(mContactInfo);
        mOfflineBotPk = mContactInfo.getKey().getKey();
        mOfflineBotTokId = mContactInfo.getTokId();
        State.infoRepo()
            .getFriendInfoLive(mOfflineBotPk)
            .observe(mOfflineBotView, new Observer<ContactInfo>() {
                @Override
                public void onChanged(ContactInfo contactInfo) {
                    mIsFriend = (contactInfo != null);
                    LogUtil.i(TAG, "observer offline  bot is friend:" + mIsFriend);
                    mOfflineBotView.showIsFriend(mIsFriend);
                }
            });
    }

    @Override
    public void addOrShowContactInfo() {
        if (mIsFriend) {
            PageJumpIn.jumpFriendInfoPage(mOfflineBotView.getActivity(), "-1", mOfflineBotPk);
        } else {
            mOfflineBotView.showAddFriend(mOfflineBotTokId);
        }
    }

    @Override
    public void showContactInfo() {
        PageJumpIn.jumpFriendInfoPage(mOfflineBotView.getActivity(), "-1", mOfflineBotPk);
    }

    @Override
    public void onDestroy() {
        if (mOfflineBotView != null) {
            mOfflineBotView = null;
        }
    }
}
