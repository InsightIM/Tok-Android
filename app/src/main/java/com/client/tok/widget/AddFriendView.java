package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.client.tok.R;
import com.client.tok.bot.BotManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.ViewUtil;

public class AddFriendView extends FrameLayout implements View.OnClickListener {
    private ItemInfoView mNewFriendView;
    private ItemInfoView mShareView;
    private ItemInfoView mFindFriendBotView;
    private ItemInfoView mOfflineBotView;

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
        mNewFriendView.setOnClickListener(this);

        mShareView = this.findViewById(R.id.id_add_friend_share_iiv);
        mShareView.setOnClickListener(this);

        mFindFriendBotView = this.findViewById(R.id.id_add_friend_find_bot_iiv);
        mFindFriendBotView.setOnClickListener(this);

        mOfflineBotView = this.findViewById(R.id.id_add_friend_offline_bot_iiv);
        mOfflineBotView.setOnClickListener(this);
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
                PageJumpIn.jumpSharePage(getContext());
                break;
            case R.id.id_add_friend_find_bot_iiv:
                PageJumpIn.jumpFriendInfoPage(getContext(), "-1",
                    BotManager.getInstance().getFindFriendBotPk());
                break;
            case R.id.id_add_friend_offline_bot_iiv:
                PageJumpIn.jumpOfflineBotInfoPage(getContext());
                break;
        }
    }

    public void setFindFriendBotVisible(boolean visible) {
        mFindFriendBotView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setOfflineBotVisible(boolean visible) {
        mOfflineBotView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void showNewContactRequestTag() {
        mNewFriendView.setFunctionIcon(R.drawable.unread_indicator_red);
    }

    public void hideNewContactRequestTag() {
        mNewFriendView.setFunctionIcon(-1);
    }
}
