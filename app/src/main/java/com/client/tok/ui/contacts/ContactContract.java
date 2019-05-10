package com.client.tok.ui.contacts;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.util.DiffUtil;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.ui.basecontract.BaseContract;
import java.util.List;

public class ContactContract {
    public interface IContactsView
        extends BaseContract.IBaseView<IContactsPresenter>, LifecycleOwner {
        void showContacts(DiffUtil.DiffResult result, List<ContactInfo> contactList);

        void showNewContactTag();

        void hideNewContactTag();

        void showDelContactDialog(String key, String msg);

        void setFindFriendBotVisible(boolean visible);

        void setOfflineBotVisible(boolean visible);

        void setLetterSortVisible(boolean isVisible);

        Activity getCurActivity();
    }

    public interface IContactsPresenter extends BaseContract.IBasePresenter {

        void beforeDelContact(String key);

        void delContact(String key);

        void onDestroy();
    }
}
