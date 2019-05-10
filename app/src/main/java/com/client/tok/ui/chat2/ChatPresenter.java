package com.client.tok.ui.chat2;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.Message;
import com.client.tok.bean.ToxAddress;
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
import com.client.tok.tox.MsgHelper;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxManager;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.ui.chat2.Contract.IChatPresenter;
import com.client.tok.ui.offlinecore.OfflineSender;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.MimeTypeUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxNickname;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    //friend is online
    private boolean mFriendOnline;
    //message assistant is online
    private boolean mOfflineBotOnline = false;
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
    //curent chat friend is FindFriendBot
    private boolean mIsFindFriendBot;
    //current chat friend is bot(FindFriendBot,OfflineBot...) ?
    private boolean mIsBot = false;
    //Am I have add OfflineBot
    private boolean mIHaveOfflineBot = false;
    //Is current chat friend has add OfflineBot
    private boolean mHasQueryFriendOfflineBot = false;
    private boolean mFriendHasOfflineBot = false;
    private boolean mIsShowFriendHasAddOfflineBotPrompt = false;

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
        mKey = mIntent.getStringExtra(IntentConstants.PK);
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

        ContactInfo bot = BotManager.getInstance().getBotContactInfo(mKey);
        mIsFindFriendBot = bot != null && bot.getBotType() == BotType.FIND_FRIEND_BOT.getType();
        mIsBot = (bot != null);
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
        NotifyManager.getInstance().setBadge(mInfoRepo.totalUnreadCount());
    }

    @Override
    public void onPause() {
        State.setChatPageActive(false);
    }

    private void subNameAndState() {
        updateStatusPrompt();
        mInfoRepo.getFriendInfoLive(mKey).observe(mChatView, new Observer<ContactInfo>() {
            @Override
            public void onChanged(@Nullable ContactInfo contactInfo) {
                if (contactInfo != null) {//if delete group,it will be here，info isnull
                    mChatView.setContactName(contactInfo.getDisplayName());
                    mFriendOnline = contactInfo.isOnline();
                    ChatPresenter.this.initKeyNames(contactInfo);
                    mFriendHasOfflineBot = contactInfo.isHasOfflineBot();
                } else {
                    mFriendOnline = false;
                    mFriendHasOfflineBot = false;
                }
                dealHasAddOfflineBotPrompt();
                updateStatusPrompt();
            }
        });

        if (!mIsBot) {
            String offlineBotPk =
                new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey().toString();
            mInfoRepo.getFriendInfoLive(offlineBotPk)
                .observe(mChatView, new Observer<ContactInfo>() {
                    @Override
                    public void onChanged(@Nullable ContactInfo contactInfo) {
                        if (contactInfo != null) {
                            mOfflineBotOnline = contactInfo.isOnline();
                            mIHaveOfflineBot = true;
                        } else {
                            mOfflineBotOnline = false;
                            mIHaveOfflineBot = false;
                        }
                        queryFriendOfflineBot();
                        updateStatusPrompt();
                    }
                });
        }
    }

    private void queryFriendOfflineBot() {
        if (!mHasQueryFriendOfflineBot && mIHaveOfflineBot && mOfflineBotOnline && mChatType.equals(
            GlobalParams.CHAT_FRIEND)) {
            OfflineSender.sendQueryFriend(mKey);
            mHasQueryFriendOfflineBot = true;
        }
    }

    private void updateStatusPrompt() {
        if (mFriendOnline) {
            mChatView.showOnlineStatus(true, StringUtils.getTextFromResId(R.string.on_line));
        } else if (mOfflineBotOnline && mFriendHasOfflineBot) {
            mChatView.showOnlineStatus(true,
                StringUtils.getTextFromResId(R.string.offline_bot_online));
        } else {
            mChatView.showOnlineStatus(false, StringUtils.getTextFromResId(R.string.off_line));
        }
    }

    private void dealHasAddOfflineBotPrompt() {
        if (mFriendHasOfflineBot && mIsShowFriendHasAddOfflineBotPrompt) {
            mIsShowFriendHasAddOfflineBotPrompt = false;
            savePrompt(MessageType.PROMPT_NORMAL,
                StringUtils.getTextFromResId(R.string.friend_has_add_offline_bot));
        }
    }

    private void subMsg() {
        NotifyManager.getInstance().cleanNotify(mContactsKey.toString().hashCode());

        mInfoRepo.getMessageByKeyLive(mContactsKey.getKey())
            .observe(mChatView, new Observer<List<Message>>() {
                @Override
                public void onChanged(@Nullable final List<Message> newMsgList) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
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
                                    DiffUtil.calculateDiff(new MsgDiff(mCurMsgList, newMsgList),
                                        true);
                                mCurMsgList = newMsgList;
                                mHandler.sendEmptyMessage(MSG_UPDATE_LIST);
                            }
                        }
                    }).start();
                }
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
        mInfoRepo.addMessage2(mContactsKey, ToxManager.getManager().toxBase.getSelfKey(),
            ToxNickname.unsafeFromValue("".getBytes()), draft, System.currentTimeMillis(),
            GlobalParams.SEND_SUCCESS, true, MessageType.DRAFT, -1);
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

    private void initKeyNames(ContactInfo friendInfo) {
        if (GlobalParams.CHAT_FRIEND.equals(mChatType)) {
            mKeyNameMap.put(friendInfo.getKey().getKey(), friendInfo.getDisplayName());
        }
    }

    @Override
    public boolean canSendFile() {
        if (mIsFindFriendBot) {
            // if find friend bot,can't send file to it
            mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.can_not_send_file_to_bot));
            return false;
        }

        if (!mFriendOnline) {
            if (!mOfflineBotOnline) {
                mChatView.showErrorMsg(
                    StringUtils.getTextFromResId(R.string.send_file_need_online));
                return false;
            } else {
                //add prompt to db
                savePrompt(MessageType.PROMPT_NORMAL,
                    StringUtils.getTextFromResId(R.string.prompt_offline_text_only));
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canSendTxt() {
        if (!mFriendOnline) {
            if (mIsBot) {
                mChatView.showErrorMsg(StringUtils.getTextFromResId(R.string.send_txt_need_online));
                return false;
            }
            if (!mOfflineBotOnline) {
                if (mIHaveOfflineBot) {
                    mChatView.showErrorMsg(
                        StringUtils.getTextFromResId(R.string.send_txt_need_online));
                } else {
                    savePrompt(MessageType.PROMPT_ADD_OFFLINE_BOT,
                        StringUtils.getTextFromResId(R.string.prompt_add_offline_bot));
                }
                return false;
            } else if (!mFriendHasOfflineBot) {
                savePrompt(MessageType.PROMPT_NORMAL,
                    StringUtils.getTextFromResId(R.string.prompt_friend_not_has_offline_bot));
                //every prompt of friend not has offline bot =>check
                mIsShowFriendHasAddOfflineBotPrompt = true;
                mHasQueryFriendOfflineBot = false;
                queryFriendOfflineBot();
                return false;
            }
        }
        return true;
    }

    private void savePrompt(MessageType msgType, String content) {
        State.infoRepo()
            .addMessage2(mContactsKey, mContactsKey, ToxNickname.unsafeFromValue("".getBytes()),
                content, 0L, GlobalParams.SEND_SUCCESS, true, msgType, -1);
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
            //TODO in the old Antox code, here is a deal on '/me' ？？
            if (GlobalParams.CHAT_FRIEND.equals(mChatType)) {
                MsgHelper.sendFriendMessage(mContactsKey, mFriendOnline, msg, MessageType.MESSAGE);
            }
        }
    }

    @Override
    public void resentSendMsgText(String msg) {
        if (canSendTxt()) {
            sendMsgText(msg);
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
    public void save(String filePath) {
        String file = ImageUtils.getImgPath(filePath);
        boolean result = FileUtilsJ.save2Download(mChatView.getActivity(), file,
            StorageUtil.getDownloadFolder());
        if (result) {
            mChatView.showErrorMsg(
                StringUtils.formatTxFromResId(R.string.save_to, StorageUtil.getDownloadFolder()));
        } else {
            mChatView.showErrorMsg(StringUtils.formatTxFromResId(R.string.failed));
        }
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
                .subscribe(new Consumer<ContactEvent>() {
                    @Override
                    public void accept(ContactEvent contactEvent) throws Exception {
                        LogUtil.i(TAG, contactEvent.toString());
                        if (contactEvent.getPk().equals(mKey)) {
                            if (contactEvent.getEvent() == ContactEvent.DEL_CONTACT) {
                                mChatView.onViewDestroy();
                            }
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
        mHasQueryFriendOfflineBot = false;
        LogUtil.i(TAG, "chatPresenter ondestroy");
    }
}
