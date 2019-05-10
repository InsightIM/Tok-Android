package com.client.tok.ui.profileedit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.TextInputDrawableEt;

public class ProfileEditActivity extends BaseCommonTitleActivity
    implements ProfileEditContract.IProfileEditView {
    private TextView mPromptTv;
    private TextInputDrawableEt mContentEt;
    private String mContentOld;

    private ProfileEditContract.IProfileEditPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mPromptTv = $(R.id.id_edit_prompt_tv);
        mContentEt = $(R.id.id_edit_content_tv);
        new ProfileEditPresenter(this);

        mContentEt.setOnDrawableRightListener(new TextInputDrawableEt.OnDrawableRightListener() {
            @Override
            public void onDrawableRightClick() {
                mContentEt.setText("");
            }
        });
    }

    @Override
    public int getTitleId() {
        return R.string.profile_edit;
    }

    @Override
    public int getMenuTxtId() {
        return R.string.save;
    }

    @Override
    public void setPresenter(ProfileEditContract.IProfileEditPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public void showSelfName(String userName) {
        mContentOld = userName;
        mPromptTv.setText(R.string.nick_name);
        mContentEt.setText(userName);
        mContentEt.setSelection(userName.length());
    }

    @Override
    public void showFriendName(String aliasName) {
        mContentOld = aliasName;
        mPromptTv.setText(R.string.alias_name);
        mContentEt.setText(aliasName);
        mContentEt.setSelection(aliasName.length());
    }

    @Override
    public void showSelfSignature(String selfSignature) {
        mContentOld = selfSignature;
        mPromptTv.setText(R.string.bio);
        mContentEt.setText(selfSignature);
        mContentEt.setSelection(selfSignature.length());
    }

    @Override
    public void success(int strId) {
        ToastUtils.show(strId);
    }

    @Override
    public void onMenuClick() {
        mPresenter.save(mContentEt.getText().toString());
    }

    @Override
    public void closeView() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter = null;
        }
    }
}
