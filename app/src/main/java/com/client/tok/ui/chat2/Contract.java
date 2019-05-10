package com.client.tok.ui.chat2;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.util.DiffUtil;
import com.client.tok.bean.Message;
import java.util.List;

public class Contract {
    public interface IChatView extends LifecycleOwner {
        void setPresenter(IChatPresenter chatPresenter);

        void setSafeViewVisible(boolean isVisible);

        void showOnlineStatus(boolean isOnline, String statusPrompt);

        void showErrorMsg(String msg);

        void showAddFriend(String tokId);

        void setContactName(String name);

        void showDiffMsg(DiffUtil.DiffResult result, List<Message> msgList);

        void showTxtFail(Message msg);

        Activity getActivity();

        void onViewDestroy();
    }

    public interface IChatPresenter {
        void getMsgList();

        String getDraft();

        void saveDraft(String draft);

        boolean isLastMsgMine();

        void jumpDetail();

        void onResume();

        void onPause();

        boolean canSendTxt();

        boolean canSendFile();

        void sendMsgText(String msg);

        void resentSendMsgText(String msg);

        void addFriendOrder(String tokId);

        void sendFile(String path);

        void setPlayed(int msgId);//audio has played

        boolean isShowSenderName();

        boolean isEnableOrderLink();

        String getSenderName(String key);

        void del(int msgId);

        void save(String filePath);

        void onMsgFailDeal(Message msg);

        void resent(Message msg);

        void destroy();
    }
}
