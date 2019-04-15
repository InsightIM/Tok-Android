package com.client.tok.ui.addfriends;

import com.client.tok.ui.basecontract.BaseContract;

public class AddFriendsContract {
    public interface IAddFriendsView extends BaseContract.IBaseView<IAddFriendsPresenter> {
        void showTokId(CharSequence tokId);

        void showMsgDialog(String tokId, String alias, String defaultMsg);

        void showErr(int msgId);

        void showSuccess(int msgId);
    }

    public interface IAddFriendsPresenter extends BaseContract.IBasePresenter {
        void checkId(String tokId);

        void addFriend(String tokId, String alias, String msg);

        void onDestroy();
    }
}
