package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.client.tok.R;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.ViewUtil;

public class AddFriendView extends FrameLayout implements View.OnClickListener {
    private ItemInfoView mNewFriendView;
    private ItemInfoView mAddChatIdView;
    private ItemInfoView mShareView;

    public AddFriendView(Context context) {
        super(context);
        initView(context);
    }

    public AddFriendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddFriendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public AddFriendView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = ViewUtil.inflateViewById(context, R.layout.view_add_friend);
        addView(rootView);
        mNewFriendView = this.findViewById(R.id.id_new_friends_iiv);
        mAddChatIdView = this.findViewById(R.id.id_add_friend_chat_id_iiv);
        mShareView = this.findViewById(R.id.id_add_friend_share_iiv);
        mNewFriendView.setOnClickListener(this);
        mAddChatIdView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_new_friends_iiv://TODO
                PageJumpIn.jumpContactRequestPage(getContext());
                break;
            case R.id.id_add_friend_chat_id_iiv:
                PageJumpIn.jumpAddFriendsPage(getContext());
                break;
            case R.id.id_add_friend_share_iiv:
                //ShareModule.shareDownLoad(getContext());
                PageJumpIn.jumpSharePage(getContext());
                break;
        }
    }

    public void showNewContactRequestTag() {
        mNewFriendView.setFunctionIcon(R.drawable.unread_indicator_red);
    }

    public void hideNewContactRequestTag() {
        mNewFriendView.setFunctionIcon(-1);
    }
}
