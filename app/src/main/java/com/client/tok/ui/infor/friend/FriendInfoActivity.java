package com.client.tok.ui.infor.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.ui.profileedit.ProfileEditPresenter;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.PortraitView;

public class FriendInfoActivity extends BaseCommonTitleActivity
    implements FriendInfoContract.IFriendInfoView, View.OnClickListener {
    private String mTokId;
    private PortraitView mPortraitView;
    private View mNameLayout;
    private TextView mNickNameTv;
    private TextView mFNameTv;
    private TextView mBioTv;
    private TextView mTokIdCopyTv;
    private TextView mTokIdTv;
    private TextView mMessageTv;
    private boolean mIsFriend;

    private FriendInfoContract.IFriendInfoPresenter mFriendInfoPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_infor);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFriendInfoPresenter == null) {
            new FriendInfoPresenter(this);
        } else {
            mFriendInfoPresenter.start();
        }
    }

    public void initView() {
        mPortraitView = $(R.id.id_friend_info_portrait_civ);
        mNameLayout = $(R.id.id_friend_info_layout);
        mNickNameTv = $(R.id.id_friend_info_nick_name_tv);
        mFNameTv = $(R.id.id_friend_info_name_tv);
        mBioTv = $(R.id.id_friend_bio_tv);
        mTokIdCopyTv = $(R.id.id_friend_info_fid_copy_tv);
        mTokIdTv = $(R.id.id_friend_info_fid_tv);
        mMessageTv = $(R.id.id_friend_info_msg_tv);

        mNameLayout.setOnClickListener(this);
        mTokIdCopyTv.setOnClickListener(this);
        mMessageTv.setOnClickListener(this);
    }

    @Override
    public int getTitleId() {
        return R.string.friend_info;
    }

    @Override
    public void setPresenter(FriendInfoContract.IFriendInfoPresenter iSettingPresenter) {
        mFriendInfoPresenter = iSettingPresenter;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_friend_info_layout:
                PageJumpIn.jumpProfileEditPage(this, mTokId, ProfileEditPresenter.EDIT_NAME);
                break;
            case R.id.id_friend_info_fid_copy_tv:
                SystemUtils.copyTxt2Clipboard(this, mTokIdTv.getText().toString());
                ToastUtils.show(R.string.copy_success);
                break;
            case R.id.id_friend_info_msg_tv:
                if (mIsFriend) {
                    PageJumpIn.jumpFriendChatPage(this, mTokId);
                } else {
                    mFriendInfoPresenter.addFriendByTokId();
                }
                break;
        }
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public void showMsg(CharSequence msg) {
        ToastUtils.show(msg);
    }

    @Override
    public void showSendMsg() {
        mIsFriend = true;
        mMessageTv.setVisibility(View.VISIBLE);
        mMessageTv.setText(R.string.message);
    }

    @Override
    public void showFriendInfo(ContactsInfo friendInfo) {
        if (friendInfo != null) {
            mIsFriend = true;
            friendMsg(friendInfo);
            mMessageTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showAddFriendInfo(ContactsInfo friendInfo) {
        if (friendInfo != null) {
            mIsFriend = false;
            friendMsg(friendInfo);
            mMessageTv.setVisibility(View.VISIBLE);
            mMessageTv.setText(R.string.add_bot);
        }
    }

    private void friendMsg(ContactsInfo friendInfo) {
        mTokId = friendInfo.getKey().toString();
        String tokName = friendInfo.getName().toString();
        String nickName = friendInfo.getDisplayName();
        //设置头像
        mPortraitView.setFriendText(mTokId, nickName);
        mPortraitView.setClickEnterDetail(false);
        //设置昵称
        mNickNameTv.setText(nickName);
        //签名
        mBioTv.setText(friendInfo.getSignature());
        //tokName
        mFNameTv.setText(StringUtils.formatTxFromResId(R.string.nick_name_prompt, tokName));
        //tokId
        mTokIdTv.setText(mTokId);
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
        if (mFriendInfoPresenter != null) {
            mFriendInfoPresenter.onDestroy();
            mFriendInfoPresenter = null;
        }
    }
}