package com.client.tok.ui.recentmsg;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.util.DiffUtil;
import com.client.tok.bean.ConversationItem;
import com.client.tok.ui.basecontract.BaseContract;
import java.util.List;

public class RecentMsgContract {
    public interface IRecentMsgView
        extends BaseContract.IBaseView<IRecentMsgPresenter>, LifecycleOwner {
        void showRecentMsg(DiffUtil.DiffResult result, List<ConversationItem> conversationList);

        void setEmptyPromptVisible(boolean isVisible);

        Activity getCurActivity();
    }

    public interface IRecentMsgPresenter extends BaseContract.IBasePresenter {
        void delConversation(String key);

        void markReaded(String key);

        void onDestroy();
    }
}
