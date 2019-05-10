package com.client.tok.ui.info.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.ContactInfo;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.ui.profileedit.ProfileEditPresenter;
import com.client.tok.utils.AvatarUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.HeadInfoView;

public class FriendInfoActivity extends BaseCommonTitleActivity
    implements FriendInfoContract.IFriendInfoView, View.OnClickListener {
    private String mPk;
    private HeadInfoView mHiv;
    private TextView mBioTv;
    private TextView mPkCopyTv;
    private TextView mPkTv;
    private TextView mMessageTv;
    private boolean mIsFriend;

    private FriendInfoContract.IFriendInfoPresenter mFriendInfoPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_infor);
        initView();
        new FriendInfoPresenter(this);
    }

    public void initView() {
        mHiv = $(R.id.id_contact_info_hiv);

        mBioTv = $(R.id.id_friend_bio_tv);

        mPkCopyTv = $(R.id.id_friend_info_fid_copy_tv);
        mPkCopyTv.setOnClickListener(this);

        mPkTv = $(R.id.id_friend_info_fid_tv);

        mMessageTv = $(R.id.id_friend_info_msg_tv);
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
            case R.id.id_contact_info_hiv:
                PageJumpIn.jumpProfileEditPage(this, mPk, ProfileEditPresenter.EDIT_NAME);
                break;
            case R.id.id_friend_info_fid_copy_tv:
                SystemUtils.copyTxt2Clipboard(this, mPkTv.getText().toString());
                ToastUtils.show(R.string.copy_success);
                break;
            case R.id.id_friend_info_msg_tv:
                if (mIsFriend) {
                    PageJumpIn.jumpFriendChatPage(this, mPk);
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
    public void showContactInfo(ContactInfo info, boolean isFriend) {
        if (info != null) {
            mIsFriend = isFriend;
            initHeadInfoViewListener();
            mPk = info.getKey().getKey();
            String name = info.getDisplayName();
            //init avatar
            String pk = info.getKey().getKey();
            if (!AvatarUtil.avatarExist(pk) && info.getDefaultIcon() > 0) {
                mHiv.setAvatarId(info.getDefaultIcon());
            } else {
                mHiv.setAvatar(pk, name);
            }
            mHiv.setClickEnterDetail(false);
            mHiv.setTitle(name);
            if (!StringUtils.isEmpty(info.getProvider())) {
                mHiv.setContent(info.getProvider());
            } else {
                String userName = info.getName().toString();
                if (StringUtils.isEmpty(userName)) {
                    mHiv.setContent(userName);
                } else {
                    mHiv.setContent(
                        StringUtils.formatTxFromResId(R.string.user_name_prompt, info.getName()));
                }
            }
            mBioTv.setText(info.getSignature());
            mPkTv.setText(mPk);
            //btn init
            mMessageTv.setVisibility(View.VISIBLE);
            //if is not friend,it is the bot
            mMessageTv.setText(isFriend ? R.string.message : R.string.add_bot);
        }
    }

    private void initHeadInfoViewListener() {
        if (mIsFriend) {
            mHiv.setOnClickListener(this);
            mHiv.setFunctionIcon(R.drawable.arrow_right_grey);
        } else {
            mHiv.setOnClickListener(null);
            mHiv.setFunctionIcon(0);
        }
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