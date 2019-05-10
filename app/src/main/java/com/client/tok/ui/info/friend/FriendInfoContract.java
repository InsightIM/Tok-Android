package com.client.tok.ui.info.friend;

import android.content.Intent;
import com.client.tok.bean.ContactInfo;
import com.client.tok.ui.basecontract.BaseContract;

public class FriendInfoContract {
    public interface IFriendInfoView extends BaseContract.IBaseView<IFriendInfoPresenter> {

        Intent getDataIntent();

        void showContactInfo(ContactInfo info, boolean isFriend);

        void showMsg(CharSequence msg);

    }

    public interface IFriendInfoPresenter extends BaseContract.IBasePresenter {
        void addFriendByTokId();

        void onDestroy();
    }
}
