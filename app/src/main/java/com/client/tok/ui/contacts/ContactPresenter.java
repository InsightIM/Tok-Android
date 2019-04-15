package com.client.tok.ui.contacts;

import android.support.v7.util.DiffUtil;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.CoreManager;
import com.client.tok.tox.State;
import com.client.tok.utils.CollectionUtils;
import com.client.tok.utils.LogUtil;
import java.util.List;
import java.util.stream.Collectors;

public class ContactPresenter implements ContactContract.IContactsPresenter {
    private String TAG = "ContactPresenter";
    private ContactContract.IContactsView mContactView;
    private List<ContactsInfo> mCurFriendList;
    private InfoRepository mInfoRepo = State.infoRepo();

    public ContactPresenter(ContactContract.IContactsView contactsView) {
        this.mContactView = contactsView;
        mContactView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        registerObserver();
        observerFriendReq();
    }

    private void registerObserver() {
        State.infoRepo().friendList().observe(mContactView, (List<ContactsInfo> friendList) -> {
            updateByCondition(friendList);
        });
    }

    private synchronized void updateByCondition(List<ContactsInfo> newFriendList) {
        if (newFriendList != null) {
            LogUtil.i(TAG, "contacts presenter really update contacts list");
            newFriendList = newFriendList.stream()
                .sorted(CollectionUtils.contactLetterComparator())
                .collect(Collectors.toList());
            // TODO crash use diffresult on some device
            //DiffUtil.DiffResult result =
            //    DiffUtil.calculateDiff(new ContactsDiff(mCurFriendList, newFriendList), true);
            mCurFriendList = newFriendList;
            mContactView.showContacts(null, mCurFriendList);
            mContactView.setLetterSortVisible(mCurFriendList.size() > 0);
        }
    }

    @Override
    public void delContact(ContactsKey key) {
        try {
            mInfoRepo.deleteMessage(key.key);
            mInfoRepo.deleteConversation(key.key);
            mInfoRepo.delContactByKey(key.key);
            CoreManager.getManager().toxBase.deleteFriend(key);
            CoreManager.getManager().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * listen new friend requset
     */
    private void observerFriendReq() {
        mInfoRepo.getFriendReqUnReadCount().observe(mContactView, (Integer integer) -> {
            if (integer != null && integer > 0) {
                mContactView.showNewContactTag();
            } else {
                mContactView.hideNewContactTag();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mContactView != null) {
            mContactView = null;
        }
    }

    public class ContactsDiff extends DiffUtil.Callback {
        private List<ContactsInfo> oldData;
        private List<ContactsInfo> newData;

        public ContactsDiff(List<ContactsInfo> oldData, List<ContactsInfo> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData != null ? oldData.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newData != null ? newData.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).getKey().key.equals(
                newData.get(newItemPosition).getKey().key);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            boolean result = oldData.get(oldItemPosition)
                .contactDiffStr()
                .equals(newData.get(newItemPosition).contactDiffStr());
            LogUtil.i(TAG, "oldP:" + oldItemPosition + ",newP" + newItemPosition + "," + result);
            return result;
        }
    }
}
