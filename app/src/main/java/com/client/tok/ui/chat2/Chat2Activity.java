package com.client.tok.ui.chat2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.Message;
import com.client.tok.media.player.audio.AudioPlayer;
import com.client.tok.media.recorder.audio.OpusAudioRecorder;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.RecordingView;
import com.client.tok.widget.SafePromptView;
import com.client.tok.widget.WrapContentLinearLayoutManager;
import com.client.tok.widget.dialog.DialogFactory;
import java.util.List;

public class Chat2Activity extends BaseCommonTitleActivity implements Contract.IChatView {
    private String TAG = "Chat2UI";
    private LinearLayoutManager mLayoutManager;
    private SafePromptView mSafeView;
    private RecyclerView mMsgRv;
    private RecordingView mRecordingView;//录音中的Loading view
    private RichMsgFragment mRichMsgFragment;
    private MsgAdapter mMsgAdapter;
    private Intent mIntent;
    private String mKey;

    private Contract.IChatPresenter mChatPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        mIntent = getIntent();
        readData();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIntent = intent;
        String newKey = mIntent.getStringExtra(IntentConstants.PK);
        if (!mKey.equals(newKey)) {
            finish();
            String type = mIntent.getStringExtra(IntentConstants.CHAT_TYPE);
            if (GlobalParams.CHAT_FRIEND.equals(type)) {
                PageJumpIn.jumpFriendChatPage(TokApplication.getInstance(), newKey);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChatPresenter.onPause();
        AudioPlayer.pause();
        if (OpusAudioRecorder.state != OpusAudioRecorder.STATE_NOT_INIT) {
            OpusAudioRecorder.getInstance().stop();
        }
    }

    private void readData() {
        mKey = mIntent.getStringExtra(IntentConstants.PK);
    }

    @Override
    public int getMenuTxtId() {
        return R.string.more;
    }

    @Override
    public void onMenuClick() {
        mChatPresenter.jumpDetail();
    }

    private void cleanData() {
        mMsgAdapter = null;
        mRichMsgFragment = null;
        mLayoutManager = null;
    }

    @Override
    public boolean isAllowTouchHideSoft() {
        return false;
    }

    @Override
    public void hideOther() {
        super.hideOther();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        cleanData();
        mSafeView = $(R.id.id_chat_safe_view);
        mMsgRv = $(R.id.id_chat_list);
        mRecordingView = $(R.id.id_chat_recording_view);
        mLayoutManager = new WrapContentLinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setStackFromEnd(true);
        mMsgRv.setLayoutManager(mLayoutManager);
        mMsgRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && (view.getId()
                    == R.id.id_chat_list) && Chat2Activity.this.isBottomLayoutShowing()) {
                    LogUtil.i(TAG, "onTouch :hide softKeyboard");
                    Chat2Activity.this.hideBottomLayout();
                }
                view.performClick();
                return false;
            }
        });
        new ChatPresenter(this, mIntent);
        initMsgLayout();
    }

    private void initMsgLayout() {
        mRichMsgFragment = RichMsgFragment.newInstance(RichMsgFragment.class, null);
        mRichMsgFragment.bindToContentView(mMsgRv, mRecordingView);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.id_chat_msg_layout, mRichMsgFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commitAllowingStateLoss();
        mRichMsgFragment.setPresenter(mChatPresenter);
    }

    @Override
    public void setPresenter(Contract.IChatPresenter chatPresenter) {
        mChatPresenter = chatPresenter;
    }

    @Override
    public void setSafeViewVisible(boolean isVisible) {
        mSafeView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showOnlineStatus(boolean isOnline, String statusPrompt) {
        setUserStatus(isOnline);
        setSubTitle(statusPrompt);
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.showLong(msg);
    }

    @Override
    public void showAddFriend(String tokId) {
        DialogFactory.addFriendDialog(this, tokId, null, true, null, null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.show(R.string.add_friend_request_has_send);
                }
            });
    }

    @Override
    public void setContactName(String name) {
        setPageTitle(name);
    }

    @Override
    public void showDiffMsg(DiffUtil.DiffResult result, List<Message> msgList) {
        if (mMsgAdapter == null) {
            mMsgAdapter = new MsgAdapter(this, mChatPresenter);
            mMsgRv.setAdapter(mMsgAdapter);
        }
        mMsgAdapter.update(msgList);
        result.dispatchUpdatesTo(mMsgAdapter);
        if (mLayoutManager.findLastCompletelyVisibleItemPosition() >= mMsgAdapter.getItemCount() - 2
            || mChatPresenter.isLastMsgMine()) {
            mMsgRv.smoothScrollToPosition(mMsgAdapter.getItemCount());
        }
    }

    @Override
    public void showTxtFail(final Message msg) {
        if (msg != null) {
            DialogFactory.showNormal2ChooseDialog(this, null,
                StringUtils.getTextFromResId(R.string.resend),
                StringUtils.getTextFromResId(R.string.delete), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChatPresenter.del(msg.getId());
                        mChatPresenter.resentSendMsgText(msg.getMessage());
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChatPresenter.del(msg.getId());
                    }
                });
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onViewDestroy() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "activity result requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = null;
            if (requestCode == FilePicker.REQ_IMG_GALLERY && data != null) {
                fileUri = data.getData();
                sendFile(fileUri);
            }
            if (requestCode == FilePicker.REQ_FILE_SEL && data != null) {
                fileUri = data.getData();
                sendFile(fileUri);
            } else if (requestCode == FilePicker.REQ_IMG_CAMERA) {
                fileUri = FilePicker.getImgCameraUri();
                sendFile(fileUri);
            } else if (requestCode == GlobalParams.REQ_CODE_RECORD_VIDEO) {
                String videoPath = data.getStringExtra(IntentConstants.FILE);
                if (FileUtilsJ.exist(videoPath)) {
                    sendFile(videoPath);
                }
            }
        }
    }

    private void sendFile(String... filePathList) {
        if (filePathList != null && filePathList.length > 0) {
            for (String path : filePathList) {
                mChatPresenter.sendFile(path);
            }
        }
    }

    private void sendFile(List<String> filePathList) {
        if (filePathList != null && filePathList.size() > 0) {
            String path = filePathList.get(0);
            mChatPresenter.sendFile(path);
        }
    }

    private void sendFile(Uri fileUri) {
        if (fileUri != null) {
            String imgPath = ImageUtils.getPath(this, fileUri);
            LogUtil.i(TAG, "imgPath:" + imgPath);
            mChatPresenter.sendFile(imgPath);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatPresenter != null) {
            mChatPresenter.destroy();
            mChatPresenter = null;
        }
        AudioPlayer.release();
    }

    @Override
    public boolean isBottomLayoutShowing() {
        return super.isBottomLayoutShowing() || mRichMsgFragment.isRichMsgLayoutShowing();
    }

    @Override
    public void hideBottomLayout() {
        super.hideBottomLayout();
        if (mRichMsgFragment.isRichMsgLayoutShowing()) {
            mRichMsgFragment.hideRichMsgLayout();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mRichMsgFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }
}
