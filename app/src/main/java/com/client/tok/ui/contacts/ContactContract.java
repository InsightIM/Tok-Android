package com.client.tok.ui.contacts;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.util.DiffUtil;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.ui.basecontract.BaseContract;
import java.util.List;

public class ContactContract {
    public interface IContactsView
        extends BaseContract.IBaseView<IContactsPresenter>, LifecycleOwner {
        void showContacts(DiffUtil.DiffResult result, List<ContactsInfo> contactList);

        void showNewContactTag();

        void hideNewContactTag();

        void setLetterSortVisible(boolean isVisible);

        Activity getCurActivity();
    }

    public interface IContactsPresenter extends BaseContract.IBasePresenter {

        void delContact(ContactsKey ContactsKey);

        void onDestroy();
    }
}
