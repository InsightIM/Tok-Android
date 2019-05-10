package com.client.tok.ui.contacts;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.ToxAddress;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.CollectionUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import java.util.Collections;
import java.util.List;

public class ContactPresenter implements ContactContract.IContactsPresenter {
    private String TAG = "ContactPresenter";
    private ContactContract.IContactsView mContactView;
    private List<ContactInfo> mCurFriendList;
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
        observerBot();
    }

    private void registerObserver() {
        State.infoRepo().friendList().observe(mContactView, new Observer<List<ContactInfo>>() {
            @Override
            public void onChanged(@Nullable List<ContactInfo> friendList) {
                ContactPresenter.this.updateByCondition(friendList);
            }
        });
    }

    private synchronized void updateByCondition(List<ContactInfo> newFriendList) {
        if (newFriendList != null) {
            LogUtil.i(TAG, "contacts presenter really update contacts list");
            // jdk 1.8
            //newFriendList = newFriendList.stream()
            //    .sorted(CollectionUtils.contactLetterComparator())
            //    .collect(Collectors.toList());

            Collections.sort(newFriendList, CollectionUtils.contactLetterComparator());

            // TODO crash use diffresult on some device
            //DiffUtil.DiffResult result =
            //    DiffUtil.calculateDiff(new ContactsDiff(mCurFriendList, newFriendList), true);
            mCurFriendList = newFriendList;
            mContactView.showContacts(null, mCurFriendList);
            mContactView.setLetterSortVisible(mCurFriendList.size() > 0);
        }
    }

    @Override
    public void beforeDelContact(String key) {
        String message = StringUtils.getTextFromResId(R.string.del_contact_prompt);
        if (GlobalParams.OFFLINE_BOT_TOK_ID.contains(key)) {
            message = StringUtils.getTextFromResId(R.string.del_offline_bot_prompt);
        }
        mContactView.showDelContactDialog(key, message);
    }

    @Override
    public void delContact(String key) {
        try {
            mInfoRepo.deleteMessage(key);
            mInfoRepo.deleteConversation(key);
            mInfoRepo.delContactByKey(key);
            ToxManager.getManager().toxBase.deleteFriend(new ContactsKey(key));
            ToxManager.getManager().save();
            NotifyManager.getInstance().setBadge(mInfoRepo.totalUnreadCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * listen new friend request
     */
    private void observerFriendReq() {
        mInfoRepo.getFriendReqUnReadCount().observe(mContactView, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null && integer > 0) {
                    mContactView.showNewContactTag();
                } else {
                    mContactView.hideNewContactTag();
                }
            }
        });
    }

    private void observerBot() {
        String findFriendBotPk =
            new ToxAddress(GlobalParams.FIND_FRIEND_BOT_TOK_ID).getKey().toString();
        mInfoRepo.getFriendInfoLive(findFriendBotPk)
            .observe(mContactView, new Observer<ContactInfo>() {
                @Override
                public void onChanged(@Nullable ContactInfo contactInfo) {
                    mContactView.setFindFriendBotVisible(contactInfo == null);
                }
            });

        String offlineBotPk = new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey().toString();
        mInfoRepo.getFriendInfoLive(offlineBotPk)
            .observe(mContactView, new Observer<ContactInfo>() {
                @Override
                public void onChanged(@Nullable ContactInfo contactInfo) {
                    mContactView.setOfflineBotVisible(contactInfo == null);
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
        private List<ContactInfo> oldData;
        private List<ContactInfo> newData;

        public ContactsDiff(List<ContactInfo> oldData, List<ContactInfo> newData) {
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
