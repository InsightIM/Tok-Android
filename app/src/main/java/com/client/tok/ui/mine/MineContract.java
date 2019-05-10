package com.client.tok.ui.mine;

import android.arch.lifecycle.LifecycleOwner;
import com.client.tok.bean.UserInfo;
import com.client.tok.ui.basecontract.BaseContract;

public class MineContract {
    public interface IMineView extends BaseContract.IBaseView<IMinePresenter>, LifecycleOwner {
        void showUserInfo(UserInfo userInfo);

        void showStatus(String status);

        void showFindFriendBotNew(String content, int style, int bg);

        void showOfflineBotNew(String content, int style, int bg);
    }

    public interface IMinePresenter extends BaseContract.IBasePresenter {
        void showFindFriendBot();

        void showOfflineBot();

        void observerFindFriendBot();

        void onDestroy();
    }
}
