package com.client.tok.ui.contactreq;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.FriendRequest;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.EmptyPromptView;
import java.util.List;

public class ContactReqActivity extends BaseCommonTitleActivity
    implements ContactReqContract.IContactReqView, AdapterView.OnItemClickListener {
    private String TAG = "ContactReqActivity";
    private ContactReqContract.IContactReqPresenter mContactReqPresenter;
    private ListView mContactReqLv;
    private EmptyPromptView mEmptyView;
    private ContactReqAdapter mContactsAdapter;
    private List<FriendRequest> mFriendRequestList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_request);
        initView();
        new ContactReqPresenter(this);
    }

    @Override
    public int getTitleId() {
        return R.string.new_friends_request;
    }

    private void initView() {
        mContactReqLv = $(R.id.id_contact_req_lv);
        mEmptyView = $(R.id.id_contact_req_empty);
    }

    @Override
    public void setPresenter(ContactReqContract.IContactReqPresenter iContactsPresenter) {
        mContactReqPresenter = iContactsPresenter;
    }


    @Override
    public void showContactReq(final List<FriendRequest> friendRequestList) {
        if (friendRequestList != null && friendRequestList.size() > 0) {
            mContactReqLv.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mFriendRequestList = friendRequestList;
            if (mContactsAdapter == null) {
                mContactsAdapter =
                    new ContactReqAdapter(ContactReqActivity.this, friendRequestList);
                mContactReqLv.setOnItemClickListener(this);
            } else {
                mContactsAdapter.setContactList(friendRequestList);
            }
            mContactReqLv.setAdapter(mContactsAdapter);
        } else {
            mContactReqLv.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        viewDestroy();
        super.onDestroy();
    }

    @Override
    public void viewDestroy() {
        if (mContactReqPresenter != null) {
            mContactReqPresenter.onDestroy();
            mContactReqPresenter = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendRequest friendRequest = mFriendRequestList.get(position);
        LogUtil.i(TAG, "position:" + position + "," + friendRequest.getRequestKey().getKey());
        PageJumpIn.jumpContactReqDetailPage(this, friendRequest.getRequestKey().key);
    }
}