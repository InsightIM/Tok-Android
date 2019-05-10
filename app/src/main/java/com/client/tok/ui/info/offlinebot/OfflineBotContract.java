package com.client.tok.ui.info.offlinebot;

import android.arch.lifecycle.LifecycleOwner;
import com.client.tok.bean.ContactInfo;
import com.client.tok.ui.basecontract.BaseContract;

public class OfflineBotContract {
    public interface IOfflineBotView
        extends BaseContract.IBaseView<IOfflineBotViewPresenter>, LifecycleOwner {

        void showBotInfo(ContactInfo contactInfo);

        void showIsFriend(boolean isFriend);

        void showAddFriend(String tokId);
    }

    public interface IOfflineBotViewPresenter extends BaseContract.IBasePresenter {

        void addOrShowContactInfo();

        void showContactInfo();

        void onDestroy();
    }
}
