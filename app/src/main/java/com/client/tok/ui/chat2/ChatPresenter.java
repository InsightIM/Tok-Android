package com.client.tok.ui.chat2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.util.DiffUtil;
import com.client.tok.R;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.Message;
import com.client.tok.bot.BotManager;
import com.client.tok.constant.BotType;
import com.client.tok.constant.FileKind;
import com.client.tok.constant.MessageType;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.rx.RxBus;
import com.client.tok.rx.event.ContactEvent;
import com.client.tok.tox.CoreManager;
import com.client.tok.tox.MsgHelper;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.ui.chat2.Contract.IChatPresenter;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.MimeTypeUtil;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxMessageType;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import top.zibin.luban.OnCompressListener;

public class ChatPresenter implements IChatPresenter {
    private String TAG = "ChatPresenter";
    private String mMyMsgPrefix = "/me ";
    private Contract.IChatView mChatView;
    private Intent mIntent;
    private String mKey;
    private ContactsKey mContactsKey;
    private boolean mFromNotification;
    private String mChatType;
    private boolean mHasUpdateGroup = false;
    //friend is online
    private boolean mFriendOnline;
    //message assistant is online
    private boolean mMsgAssistantOnline = false;
    //friendName
    private Map<String, String> mKeyNameMap = new HashMap<String, String>();
    private List<Message> mCurMsgList;

    private InfoRepository mInfoRepo = State.infoRepo();

    private int PAGE_MSG_SIZE = 1000;
    private int mShowMsgSize = PAGE_MSG_SIZE;

    private final int MSG_UPDATE_LIST = 1;
    private final int MSG_UPDATE_SAFE_WARNING = 2;

    private boolean mSafeWarningShow = true;
    private Handler mHandler;
    private DiffUtil.DiffResult mDiffResult = null;

    private Disposable mDelContactDis;

    private boolean mIsFindFriendBot;

    protected ChatPresenter(Contract.IChatView chatView, Intent intent) {
        mChatView = chatView;
        mIntent = intent;
        destroy();
        mHandler = new ChatHandler();
        mChatView.setPresenter(this);
        readIntentData();
        start();
        listen();
    }

    private void readIntentData() {
        mKey = mIntent.getStringExtra(IntentConstants.TOK_ID);
        mFromNotification = mIntent.getBooleanExtra(IntentConstants.FROM_NOTIFICATION, false);
        mChatType = mIntent.getStringExtra(IntentConstants.CHAT_TYPE);
        LogUtil.i(TAG, "chatPresenter ContactsKey:"
            + mKey
            + ",fromNotification:"
            + mFromNotification
            + ",chatType:"
            + mChatType);
        mContactsKey = new ContactsKey(mKey);
        State.setChatKey(mContactsKey.key);

        ContactsInfo bot = BotManager.getInstance().getAddFriendBotInfo(mKey);
        mIsFindFriendBot = bot != null && bot.getBotType() == BotType.FIND_FRIEND_BOT.getType();
    }

    private void start() {
        subNameAndState();
        subMsg();
    }

    private class ChatHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_SAFE_WARNING:
                    mChatView.setSafeViewVisible(mSafeWarningShow);
                    break;
                case MSG_UPDATE_LIST:
                    mChatView.showDiffMsg(mDiffResult, mCurMsgList);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        State.setChatPageActive(true);
        mInfoRepo.markReaded(mContactsKey.key);
    }

    @Override
    public void onPause() {
        State.setChatPageActive(false);
    }

    private void subNameAndState() {
        mChatView.showOnlineStatus(mFriendOnline);

        mInfoRepo.getFriendInfoLive(mKey).observe(mChatView, (ContactsInfo contactsInfo) -> {
            if (contactsInfo != null) {//if delete group,it will be here，info isnull
                mChatView.setContactName(contactsInfo.getDisplayName());
                if (GlobalParams.CHAT_FRIEND.equals(mChatType)) {
                    updateFriend(contactsInfo.isOnline());
                }
                initKeyNames(contactsInfo);
            }
        });
    }

    private void updateFriend(boolean newStatus) {
        if (mFriendOnline != newStatus) {
            mFriendOnline = newStatus;
            mChatView.showOnlineStatus(mFriendOnline);
        }
    }


    private void subMsg() {
        NotifyManager.getInstance().cleanNotify(mContactsKey.toString().hashCode());

        mInfoRepo.getMessageByKeyLive(mContactsKey.getKey())
            .observe(mChatView, (List<Message> newMsgList) -> {
                new Thread(() -> {
                    LogUtil.i(TAG, "messageSeq size:" + newMsgList.size());
                    if (mHandler != null) {
                        if (newMsgList.size() < 10) {
                            mSafeWarningShow = true;
                            mHandler.sendEmptyMessage(MSG_UPDATE_SAFE_WARNING);
                        } else if (mSafeWarningShow) {
                            mSafeWarningShow = false;
                            mHandler.sendEmptyMessage(MSG_UPDATE_SAFE_WARNING);
                        }
                        mDiffResult =
                            DiffUtil.calculateDiff(new MsgDiff(mCurMsgList, newMsgList), true);
                        mCurMsgList = newMsgList;
                        mHandler.sendEmptyMessage(MSG_UPDATE_LIST);
                    }
                }).start();
            });
    }

    public class MsgDiff extends DiffUtil.Callback {
        private List<Message> oldData;
        private List<Message> newData;

        public MsgDiff(List<Message> oldData, List<Message> newData) {
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
            return oldData.get(oldItemPosition).getId() == newData.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition)
                .toString()
                .equals(newData.get(newItemPosition).toString());
        }
    }

    @Override
    public void getMsgList() {

    }

    @Override
    public String getDraft() {
        Message message = mInfoRepo.getDraftMessage(mKey);
        if (message != null) {
            mInfoRepo.deleteMessage(message.getId());
            return message.getMessage();
        } else {
            return "";
        }
    }

    @Override
    public void saveDraft(String draft) {
        mInfoRepo.addMessage2(mContactsKey, CoreManager.getManager().toxBase.getSelfKey(),
            ToxNickname.unsafeFromValue("".getBytes()), draft, System.currentTimeMillis(),
            GlobalParams.SEND_SUCCESS, true, ToxMessageType.DRAFT, -1);
    }

    @Override
    public boolean isLastMsgMine() {
        return mCurMsgList != null && mCurMsgList.size() > 0 && mCurMsgList.get(
            mCurMsgList.size() - 1).isMine();
    }

    @Override
    public void jumpDetail() {
        if (mChatType.equals(GlobalParams.CHAT_FRIEND)) {
            PageJumpIn.jumpFriendInfoPage(mChatView.getActivity(), null, mKey);
        }
    }

    private void initKeyNames(ContactsInfo friendInfo) {
        if (GlobalParams.CHAT_FRIEND.equals(mChatType)) {
            mKeyNameMap.put(friendInfo.getKey().getKey(), friendInfo.getDisplayName());
        }
    }

    @Override
    public boolean canSendFile() {
        if (!mMsgAssistantOnline && !mFriendOnline) {
            mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.send_file_need_online));
        }
        return mMsgAssistantOnline || mFriendOnline;
    }

    @Override
    public boolean canSendTxt() {
        if (!mMsgAssistantOnline && !mFriendOnline) {
            mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.send_txt_need_online));
        }
        return mMsgAssistantOnline || mFriendOnline;
    }

    /**
     * send txt message
     *
     * @param msg content
     */
    @Override
    public void sendMsgText(String msg) {
        if (StringUtils.isEmpty(msg)) {
            mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.input_msg));
        } else {
            if (GlobalParams.CHAT_FRIEND.equals(mChatType)) {
                MsgHelper.sendFriendMessage(mContactsKey, mFriendOnline, msg,
                    ToxMessageType.NORMAL);
            }
        }
    }

    @Override
    public void addFriendOrder(String tokId) {
        int promptId = new AddFriendsModel().checkIdValid(tokId);
        if (promptId == AddFriendsModel.TOK_ID_VALID) {
            mChatView.showAddFriend(tokId);
        } else {
            mChatView.showErrorMsg(StringUtils.getTextFromResId(promptId));
        }
    }

    @Override
    public void sendFile(final String path) {
        if (!StringUtils.isEmpty(path)) {
            if (MimeTypeUtil.IMG_TYPE.equals(MimeTypeUtil.getFileType(path))) {
                ImageUtils.compressImg(path, new OnCompressListener() {
                    @Override
                    public void onStart() {
                        LogUtil.i(TAG, "compress img start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        LogUtil.i(TAG, "compress img success and path:" + file.getAbsolutePath());
                        sendFileRequest(file.getAbsolutePath(), mContactsKey, mChatType,
                            mFriendOnline, FileKind.DATA, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "compress img error");
                        mChatView.showErrorMsg(
                            StringUtils.getTextFromResId(R.string.file_sel_wrong));
                    }
                });
            } else {
                sendFileRequest(path, mContactsKey, mChatType, mFriendOnline, FileKind.DATA, null);
            }
        } else {
            mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.file_sel_wrong));
        }
    }

    @Override
    public void setPlayed(int msgId) {
        mInfoRepo.setHasPlayed(msgId);
    }

    @Override
    public boolean isShowSenderName() {
        return !GlobalParams.CHAT_FRIEND.equals(mChatType);
    }

    @Override
    public boolean isEnableOrderLink() {
        return mIsFindFriendBot;
    }

    @Override
    public String getSenderName(String key) {
        return mKeyNameMap.get(key);
    }

    @Override
    public void del(int msgId) {
        mInfoRepo.deleteMessage(msgId);
    }

    @Override
    public void onMsgFailDeal(Message msg) {
        if (msg != null) {
            int typeVal = msg.getMsgTypeVal();
            if (typeVal == MessageType.MESSAGE.getType()
                || typeVal == MessageType.GROUP_MESSAGE.getType()) {
                mChatView.showTxtFail(msg);
            }
        }
    }

    @Override
    public void resent(Message msg) {

    }

    private void sendFileRequest(String filePath, ContactsKey ContactsKey, String chatType,
        boolean isOnline, FileKind fileKind, ToxFileId toxFileId) {
        State.transfers.sendFileSendRequestNew(filePath, ContactsKey, chatType, isOnline, fileKind,
            toxFileId);
    }

    private void listen() {
        if (mDelContactDis == null) {
            mDelContactDis = RxBus.listen(ContactEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ContactEvent contactEvent) -> {
                    LogUtil.i(TAG, contactEvent.toString());
                    if (contactEvent.getPk().equals(mKey)) {
                        if (contactEvent.getEvent() == ContactEvent.DEL_CONTACT) {
                            mChatView.onViewDestroy();
                        }
                    }
                });
        }
    }

    @Override
    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mDelContactDis != null && !mDelContactDis.isDisposed()) {
            mDelContactDis.dispose();
        }
        mDelContactDis = null;
        LogUtil.i(TAG, "chatPresenter ondestroy");
    }
}
