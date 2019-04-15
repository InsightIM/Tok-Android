package com.client.tok.ui.home;

import android.arch.lifecycle.LifecycleOwner;
import com.client.tok.ui.basecontract.BaseContract;

public class HomeContract {
    public interface IHomeView extends BaseContract.IBaseView<IHomePresenter>, LifecycleOwner {
        void showFriendReqCount(int reqCount);

        void showUnReadMsg(int unReadMsgNum);

        void showFindFriendBotFeature(String content);

        void showAddFriend(String friendPk);
    }

    public interface IHomePresenter extends BaseContract.IBasePresenter {

        void onResume();

        void onDestroy();
    }
}
