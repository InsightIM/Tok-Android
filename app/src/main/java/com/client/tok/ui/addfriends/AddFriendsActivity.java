package com.client.tok.ui.addfriends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.dialog.DialogFactory;

public class AddFriendsActivity extends BaseCommonTitleActivity
    implements AddFriendsContract.IAddFriendsView, View.OnClickListener {
    private EditText mFriendIdEt;
    private View mCreateGroupView;
    private View mMyTokIdView;
    private View mScanView;
    private Button mAddFriendBtn;

    private AddFriendsContract.IAddFriendsPresenter mAddFriendsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initView();
        new AddFriendsPresenter(this);
        mAddFriendsPresenter.start();
    }

    public void initView() {
        mCreateGroupView = $(R.id.id_add_group_iiv);
        mCreateGroupView.setOnClickListener(this);
        mMyTokIdView = $(R.id.id_add_friend_my_id_iiv);
        mMyTokIdView.setOnClickListener(this);
        mScanView = $(R.id.id_add_friend_scan_iiv);
        mScanView.setOnClickListener(this);
        mFriendIdEt = $(R.id.id_add_friends_id_et);
        mAddFriendBtn = $(R.id.id_add_friends_add_tv);
        mAddFriendBtn.setOnClickListener(this);

        mFriendIdEt.setText(getIntent().getStringExtra(IntentConstants.TOK_ID));
    }

    @Override
    public int getTitleId() {
        return R.string.add_friends;
    }

    @Override
    public void setPresenter(AddFriendsContract.IAddFriendsPresenter iSettingPresenter) {
        mAddFriendsPresenter = iSettingPresenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAddFriendsPresenter != null) {
            mAddFriendsPresenter.onDestroy();
            mAddFriendsPresenter = null;
        }
    }

    @Override
    public int getMenuTxtId() {
        return R.string.scan;
    }

    @Override
    public void onMenuClick() {
        PageJumpIn.jumpScanPage(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_add_friends_add_tv:
                String tokId = mFriendIdEt.getText().toString().trim();
                mAddFriendsPresenter.checkId(tokId.toUpperCase());
                break;
            case R.id.id_add_friend_my_id_iiv:
                PageJumpIn.jumpMyTokIdPage(this);
                break;
            case R.id.id_add_friend_scan_iiv:
                PageJumpIn.jumpScanPage(this);
                break;
        }
    }

    @Override
    public void showTokId(CharSequence tokId) {
        mFriendIdEt.setText(tokId);
        mFriendIdEt.setSelection(tokId.length());
    }

    @Override
    public void showMsgDialog(String tokId, String alias, String defaultMsg) {
        DialogFactory.addFriendDialog(this, tokId, null, false, defaultMsg, null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddFriendsActivity.this.showSuccess(R.string.add_friend_request_has_send);
                    AddFriendsActivity.this.viewDestroy();
                }
            });
    }

    @Override
    public void showErr(int msgId) {
        ToastUtils.show(msgId);
    }

    @Override
    public void showSuccess(int msgId) {
        ToastUtils.show(msgId);
    }
}