package com.client.tok.ui.recentmsg;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.client.tok.R;
import com.client.tok.base.BaseFragment;
import com.client.tok.bean.ConversationItem;
import com.client.tok.constant.ContactType;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.EmptyPromptView;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.WrapContentLinearLayoutManager;
import java.util.List;

public class RecentMsgFragment extends BaseFragment
    implements RecentMsgContract.IRecentMsgView, RecentMsgAdapter.OnItemClickListener,
    RecentMsgAdapter.OnItemLongClickListener {
    private String TAG = "RecentMsgFragment";
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecentMsgRv;
    private EmptyPromptView mEmptyPromptView;
    private RecentMsgContract.IRecentMsgPresenter mRecentMsgPresenter;
    private List<ConversationItem> mConversationList;
    private RecentMsgAdapter mRecentMsgAdapter;

    public static RecentMsgFragment getInstance() {
        RecentMsgFragment fra = new RecentMsgFragment();
        Bundle bundle = new Bundle();
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_msg, container, false);
        mRecentMsgRv = view.findViewById(R.id.id_recent_msg_lv);
        mLayoutManager = new WrapContentLinearLayoutManager(this.getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecentMsgRv.setLayoutManager(mLayoutManager);
        mEmptyPromptView = view.findViewById(R.id.id_recent_msg_empty_layout);
        mEmptyPromptView.setMainContentListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpIn.jumpSafePage(RecentMsgFragment.this.getActivity());
            }
        });
        mEmptyPromptView.setBtn1Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpIn.jumpSharePage(RecentMsgFragment.this.getActivity());
            }
        });
        mEmptyPromptView.setBtn2Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpIn.jumpAddFriendsPage(RecentMsgFragment.this.getActivity());
            }
        });
        new RecentMsgPresenter(this);
        return view;
    }

    @Override
    public void setPresenter(RecentMsgContract.IRecentMsgPresenter iRecentMsgPresenter) {
        mRecentMsgPresenter = iRecentMsgPresenter;
    }

    @Override
    public void showRecentMsg(DiffUtil.DiffResult result, List<ConversationItem> conversationList) {
        mConversationList = conversationList;
        LogUtil.i(TAG, "showRecentMsg");
        if (mRecentMsgAdapter == null) {
            mRecentMsgAdapter = new RecentMsgAdapter(getActivity());
            mRecentMsgRv.setAdapter(mRecentMsgAdapter);
            mRecentMsgAdapter.setItemClickListener(this);
            mRecentMsgAdapter.setItemLongClickListener(this);
        }
        mRecentMsgAdapter.updateDataList(mConversationList);
        result.dispatchUpdatesTo(mRecentMsgAdapter);
    }

    @Override
    public void setEmptyPromptVisible(boolean isVisible) {
        mEmptyPromptView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mRecentMsgRv.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public Activity getCurActivity() {
        return getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewDestroy();
    }

    @Override
    public void viewDestroy() {
        if (mRecentMsgPresenter != null) {
            mRecentMsgPresenter.onDestroy();
            mRecentMsgPresenter = null;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        ConversationItem conversation = mConversationList.get(position);
        LogUtil.i(TAG, "position:" + position + ",key:" + conversation.cKey);
        int contactType = conversation.contactType;
        if (contactType == ContactType.FRIEND.getType()) {
            //进入个人会话
            PageJumpIn.jumpFriendChatPage(getActivity(), conversation.cKey);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        LogUtil.i(TAG, "onItemLongClick position:" + position);
        PopupWindow popupWindow =
            MenuPopWindow.getMenuView(this.getActivity(), MenuPopWindow.TYPE_RECENT,
                new MenuListener(mConversationList.get(position)));
        int windowPos[] = MenuPopWindow.calculatePopWindowPos(view, view);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    private class MenuListener extends MenuPopWindow.MenuClickListener {
        private ConversationItem mConversation;

        public MenuListener(ConversationItem conversation) {
            mConversation = conversation;
        }

        @Override
        public void onDel() {
            mRecentMsgPresenter.delConversation(mConversation.cKey);
        }

        @Override
        public void onMarkRead() {
            mRecentMsgPresenter.markReaded(mConversation.cKey);
        }
    }
}