package com.client.tok.ui.recentmsg;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import com.client.tok.bean.ConversationItem;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import java.util.List;

class RecentMsgPresenter implements RecentMsgContract.IRecentMsgPresenter {
    private String TAG = "RecentMsgFragment";
    private RecentMsgContract.IRecentMsgView mRecentMsgView;
    private List<ConversationItem> mCurConversationList;
    private InfoRepository mInfoRepo = State.infoRepo();

    public RecentMsgPresenter(RecentMsgContract.IRecentMsgView recentMsgView) {
        this.mRecentMsgView = recentMsgView;
        mRecentMsgView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        registerObserver();
    }

    private void registerObserver() {
        State.infoRepo()
            .conversationLive()
            .observe(mRecentMsgView, new Observer<List<ConversationItem>>() {
                @Override
                public void onChanged(@Nullable List<ConversationItem> conversationList) {
                    RecentMsgPresenter.this.updateByCondition(conversationList);
                }
            });
    }

    private void updateByCondition(List<ConversationItem> conversationList) {
        if (conversationList != null) {
            DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(new RecentMsgDiff(mCurConversationList, conversationList),
                    true);

            mCurConversationList = conversationList;
            mRecentMsgView.showRecentMsg(result, mCurConversationList);
            mRecentMsgView.setEmptyPromptVisible(
                mCurConversationList == null || mCurConversationList.size() == 0);
        }
    }

    @Override
    public void onDestroy() {
        if (mRecentMsgView != null) {
            mRecentMsgView = null;
        }
    }

    @Override
    public void delConversation(String key) {
        mInfoRepo.delConversation(key);
        NotifyManager.getInstance().setBadge(mInfoRepo.totalUnreadCount());
    }

    @Override
    public void markReaded(String key) {
        mInfoRepo.markReaded(key);
        NotifyManager.getInstance().setBadge(mInfoRepo.totalUnreadCount());
    }

    public class RecentMsgDiff extends DiffUtil.Callback {
        private List<ConversationItem> oldData;
        private List<ConversationItem> newData;

        public RecentMsgDiff(List<ConversationItem> oldData, List<ConversationItem> newData) {
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
            return oldData.get(oldItemPosition).cKey.equals(newData.get(newItemPosition).cKey);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            boolean result = oldData.get(oldItemPosition)
                .toString()
                .equals(newData.get(newItemPosition).toString());
            LogUtil.i(TAG, "oldP:" + oldItemPosition + ",newP" + newItemPosition + "," + result);
            return result;
        }
    }
}
