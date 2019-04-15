package com.client.tok.ui.contactreqdetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.FriendRequest;
import com.client.tok.utils.PkUtils;
import com.client.tok.widget.PortraitView;

public class ContactReqDetailActivity extends BaseCommonTitleActivity
    implements ContactReqDetailContract.IContactReqDetailView, View.OnClickListener {
    private String TAG = "ContactReqDetailActivity";
    private ContactReqDetailContract.IContactReqDetailPresenter mContactReqDetailPresenter;
    private PortraitView mPortraitView;
    private TextView mNameTv;
    private TextView mTokIdTv;
    private TextView mReqMsgTv;
    private TextView mRefuseTv;
    private TextView mAcceptTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_request_detail);
        initView();
        new ContactReqDetailPresenter(this, getIntent());
    }

    @Override
    public int getTitleId() {
        return R.string.new_friends_request;
    }

    private void initView() {
        mPortraitView = $(R.id.id_new_friends_portrait_iv);
        mNameTv = $(R.id.id_new_friends_name_tv);
        mTokIdTv = $(R.id.id_new_friends_tok_id_tv);
        mReqMsgTv = $(R.id.id_new_friends_msg_tv);
        mRefuseTv = $(R.id.id_new_friends_refuse_tv);
        mRefuseTv.setOnClickListener(this);
        mAcceptTv = $(R.id.id_new_friends_accept_tv);
        mAcceptTv.setOnClickListener(this);
    }

    @Override
    public void setPresenter(
        ContactReqDetailContract.IContactReqDetailPresenter contactReqDetailPresenter) {
        mContactReqDetailPresenter = contactReqDetailPresenter;
    }

    @Override
    public void showContactDetail(FriendRequest friendRequest) {
        if (friendRequest != null) {
            String key = friendRequest.getRequestKey().getKey();
            mPortraitView.setFriendText(key,
                friendRequest.getRequestMessage());
            mNameTv.setText(PkUtils.simplePk(key));
            mTokIdTv.setText(key);
            mReqMsgTv.setText(friendRequest.getRequestMessage());
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        if (mContactReqDetailPresenter != null) {
            mContactReqDetailPresenter.onDestroy();
            mContactReqDetailPresenter = null;
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_new_friends_refuse_tv:
                mContactReqDetailPresenter.refuseFriend();
                break;
            case R.id.id_new_friends_accept_tv:
                mContactReqDetailPresenter.acceptFriend();
                break;
        }
    }
}