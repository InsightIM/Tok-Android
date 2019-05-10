package com.client.tok.ui.chat2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.client.tok.R;
import com.client.tok.base.BaseFragment;
import com.client.tok.constant.BotOrder;
import com.client.tok.rx.event.BotOrderEvent;
import com.client.tok.rx.RxBus;
import com.client.tok.pagejump.SharePKeys;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.ExtendFileView;
import com.client.tok.widget.KeyboardRecordSwitchView;
import com.client.tok.widget.RecorderBtn;
import com.client.tok.widget.RecordingView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.client.tok.constant.BotOrder.ADD;
import static com.client.tok.constant.BotOrder.START;
import static com.client.tok.constant.BotOrder.fromVal;

public class RichMsgFragment extends BaseFragment implements View.OnClickListener {
    private String TAG = "RichMsgFragment";
    private RecordingView mRecordingView;//record Loading view
    private ImageView mAddExtendIv;
    private KeyboardRecordSwitchView mInputRecordBtnSw;
    private EditText mInputEt;
    private RecorderBtn mRecorderTv;
    private ImageView mSendIv;

    private ImageView mEmotionIv;
    private View mChatContentView;
    private ExtendFileView mExtendFileLayout;

    private BoardManager mBoardManager;

    private Contract.IChatPresenter mPresenter;

    private Disposable mBotEventDis;

    private AddFriendsModel mAddFriendModel = new AddFriendsModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_rich_msg, container, false);

        mAddExtendIv = rootView.findViewById(R.id.id_add_file_iv);
        mInputRecordBtnSw = rootView.findViewById(R.id.id_keyboard_record_switcher_btn);
        mInputEt = rootView.findViewById(R.id.id_input_msg_et);
        mInputEt.setOnClickListener(this);
        if(mPresenter!=null){
            mInputEt.setText(mPresenter.getDraft());
        }
        mRecorderTv = rootView.findViewById(R.id.id_record_tv);
        mSendIv = rootView.findViewById(R.id.id_send_msg_tv);
        mSendIv.setOnClickListener(this);
        mExtendFileLayout = rootView.findViewById(R.id.id_extend_view);

        mEmotionIv = rootView.findViewById(R.id.id_add_emotion_iv);

        mBoardManager = BoardManager.with(getActivity())
            .bindToContent(mChatContentView)//bind contentView(the recycleView of message)
            .bindToExtendBtn(mAddExtendIv)//toggle button to open file selector layout
            .setExtendFileLayout(mExtendFileLayout)//file selector layout
            .setRecordInputBtSw(mInputRecordBtnSw)//toggle button record/input
            .bindToEditText(mInputEt)//bind input EditText
            .setRecordLayout(mRecorderTv)//bind record button
            .setSendBt(mSendIv)//bind send button
            .build();
        mExtendFileLayout.setPresenter(mPresenter);
        mRecorderTv.bindViewAndCallBack(mRecordingView, mPresenter);
        listen();
        return rootView;
    }

    public void setPresenter(Contract.IChatPresenter presenter) {
        mPresenter = presenter;
    }

    private void sendMsg() {
        if (mPresenter.canSendTxt()) {
            String msg = mInputEt.getText().toString();
            if (isTypeStartOrder(msg)) {
                mPresenter.sendMsgText(
                    msg + " " + PreferenceUtils.getString(SharePKeys.CHAT_ID, ""));
            } else {
                mPresenter.sendMsgText(msg);
            }

            mInputEt.setText("");
        }
    }

    private boolean isTypeStartOrder(String msg) {
        return msg.trim().equals(START.getOrder());
    }

    public void bindToContentView(View contentView, RecordingView recordingView) {
        this.mChatContentView = contentView;
        this.mRecordingView = recordingView;
    }

    private void listen() {
        if (mBotEventDis == null) {
            mBotEventDis = RxBus.listen(BotOrderEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BotOrderEvent>() {
                    @Override
                    public void accept(BotOrderEvent botOrderEvent) throws Exception {
                        LogUtil.i(TAG, "==" + botOrderEvent.toString() + "==");
                        String order = botOrderEvent.getOrder();
                        String sendMsg = order;
                        BotOrder botOrder = fromVal(order);
                        switch (botOrder) {
                            case START:
                                sendMsg =
                                    order + " " + PreferenceUtils.getString(SharePKeys.CHAT_ID, "");
                                break;
                            case SET:
                                sendMsg = order + " " + State.userRepo()
                                    .getActiveUserDetails()
                                    .getStatusMessage()
                                    .toString();
                                break;
                            case ADD:
                                break;
                            case OTHER:
                                break;
                        }

                        if (botOrder.isDirSend()) {
                            //send msg directly
                            mInputEt.setText(sendMsg);
                            RichMsgFragment.this.sendMsg();
                        } else {
                            //show msg in edit text
                            if (ADD.getOrder().equals(order)) {
                                String tokId = botOrderEvent.getMsg();
                                mPresenter.addFriendOrder(tokId);
                            } else {
                                mInputEt.setText(sendMsg);
                                mInputEt.setSelection(sendMsg.length());
                            }
                        }
                    }
                });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_send_msg_tv:
                sendMsg();
                break;
            case R.id.id_input_msg_et:
                mInputEt.setCursorVisible(true);
                break;
        }
    }

    public boolean isInterceptBackPress() {
        return mBoardManager.interceptBackPress();
    }

    public boolean isRichMsgLayoutShowing() {
        return mBoardManager.isRichMsgLayoutShowing();
    }

    public void hideRichMsgLayout() {
        mBoardManager.hideRichMsgLayout();
    }

    @Override
    public void onDestroy() {
        saveDraft();
        if (mBotEventDis != null && !mBotEventDis.isDisposed()) {
            mBotEventDis.dispose();
        }
        mBotEventDis = null;
        super.onDestroy();
    }

    private void saveDraft() {
        String draft = mInputEt.getText().toString();
        if (!StringUtils.isEmpty(draft)) {
            mPresenter.saveDraft(draft);
        }
    }
}
