package com.client.tok.ui.info.friend;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bot.BotManager;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.DigitUtil;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;

public class FriendInfoPresenter implements FriendInfoContract.IFriendInfoPresenter {
    private String TAG = "FriendInforPresenter";
    private String mPk;
    private int mGroupNumber;
    private ContactInfo mContactInfo;
    private FriendInfoContract.IFriendInfoView mFriendInfoView;
    private AddFriendsModel mAddFriendModel = new AddFriendsModel();

    public FriendInfoPresenter(FriendInfoContract.IFriendInfoView friendInfoView) {
        mFriendInfoView = friendInfoView;
        mFriendInfoView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        mPk = mFriendInfoView.getDataIntent().getStringExtra(IntentConstants.PK);
        if (mPk != null && PkUtils.isAddressValid(mPk)) {
            //get pk by address
            mPk = PkUtils.getPkFromAddress(mPk);
        }
        String groupNumberStr =
            mFriendInfoView.getDataIntent().getStringExtra(IntentConstants.GROUP_ID);
        mGroupNumber = DigitUtil.str2Int(groupNumberStr);
        getFriendInfo();
    }

    private void getFriendInfo() {
        State.infoRepo()
            .getFriendInfoLive(mPk)
            .observe(mFriendInfoView, new Observer<ContactInfo>() {
                @Override
                public void onChanged(@Nullable ContactInfo contactInfo) {
                    //find friend from db
                    mContactInfo = State.infoRepo().getFriendInfo(mPk);
                    ContactInfo bot = BotManager.getInstance().getBotContactInfo(mPk);
                    if (mContactInfo != null) {
                        if (bot != null) {
                            if (StringUtils.isEmpty(mContactInfo.getName().toString())) {
                                //friend is bot and bot info not get,init by default bot info
                                mContactInfo = bot;
                            } else {
                                //friend is bot,and set provide
                                mContactInfo.setProvider(bot.getProvider());
                            }
                        }
                        mFriendInfoView.showContactInfo(mContactInfo, true);
                    } else {
                        if (bot != null) {
                            //pk is bot info ,but not add bot as friend
                            mContactInfo = bot;
                            mFriendInfoView.showContactInfo(mContactInfo, false);
                        }
                    }
                }
            });
    }

    @Override
    public void addFriendByTokId() {
        int checkResult = mAddFriendModel.checkIdValid(mContactInfo.getTokId());
        if (checkResult == AddFriendsModel.TOK_ID_VALID) {
            mAddFriendModel.addFriendById(mContactInfo.getTokId(),
                mContactInfo.getName().toString(), "");
            mFriendInfoView.showMsg(
                StringUtils.getTextFromResId(R.string.add_friend_request_has_send));
        } else {
            mFriendInfoView.showMsg(StringUtils.getTextFromResId(checkResult));
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "friendInfo presenter onDestroy");
    }
}
