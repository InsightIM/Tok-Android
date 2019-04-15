package com.client.tok.ui.infor.friend;

import android.content.Intent;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.ui.basecontract.BaseContract;

public class FriendInfoContract {
    public interface IFriendInfoView extends BaseContract.IBaseView<IFriendInfoPresenter> {

        Intent getDataIntent();

        void showMsg(CharSequence msg);

        void showSendMsg();

        void showFriendInfo(ContactsInfo friendInfo);

        void showAddFriendInfo(ContactsInfo friendInfo);
    }

    public interface IFriendInfoPresenter extends BaseContract.IBasePresenter {
        void addFriendByTokId();

        void onDestroy();
    }
}
