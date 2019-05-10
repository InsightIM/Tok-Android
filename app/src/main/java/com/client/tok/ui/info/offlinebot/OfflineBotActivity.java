package com.client.tok.ui.info.offlinebot;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.ContactInfo;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.AvatarUtil;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.HeadInfoView;
import com.client.tok.widget.dialog.DialogFactory;

public class OfflineBotActivity extends BaseCommonTitleActivity
    implements OfflineBotContract.IOfflineBotView, View.OnClickListener {
    private OfflineBotContract.IOfflineBotViewPresenter mPresenter;
    private TextView mMore;
    private HeadInfoView mBotHiv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_bot_info);
        mMore = $(R.id.id_bot_info_more);
        mMore.setOnClickListener(this);
        mBotHiv = $(R.id.id_bot_info_hiv);
        mBotHiv.setOnClickListener(this);
        mBotHiv.setFunctionListener(this);
        new OfflineBotPresenter(this);
    }

    @Override
    public int getTitleId() {
        return R.string.off_line_bot;
    }

    @Override
    public void setPresenter(OfflineBotContract.IOfflineBotViewPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showBotInfo(ContactInfo contactInfo) {
        String pk = contactInfo.getKey().key;
        if (AvatarUtil.avatarExist(pk)) {
            mBotHiv.setAvatar(pk, contactInfo.getDisplayName());
        } else {
            mBotHiv.setAvatarId(R.drawable.avatar_offline_bot);
        }
        mBotHiv.setTitle(contactInfo.getDisplayName());
        mBotHiv.setContent(contactInfo.getProvider());
    }

    @Override
    public void showIsFriend(boolean isFriend) {
        mBotHiv.setFunctionIcon(isFriend ? R.drawable.arrow_right_grey : R.drawable.add_blue);
    }

    @Override
    public void showAddFriend(String tokId) {
        DialogFactory.addFriendDialog(this, tokId, null, false, null, null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMsg(R.string.add_friend_request_has_send);
                }
            });
    }

    private void showMsg(int resId) {
        ToastUtils.show(resId);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_bot_info_more:
                PageJumpIn.jumpOfflineBotDetailPage(this);
                break;
            case R.id.id_bot_info_hiv:
                mPresenter.showContactInfo();
                break;
            case R.id.id_head_function_iv:
                mPresenter.addOrShowContactInfo();
                break;
        }
    }

    @Override
    public void viewDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        finish();
    }
}
