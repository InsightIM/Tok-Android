package com.client.tok.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.PopupWindow;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.ui.contacts.ContactFragment;
import com.client.tok.ui.mine.MineFragment;
import com.client.tok.ui.recentmsg.RecentMsgFragment;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ScreenUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.BottomTabView;
import com.client.tok.widget.HomeMenuWindow;
import com.client.tok.widget.dialog.DialogFactory;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseCommonTitleActivity
    implements HomeContract.IHomeView, View.OnClickListener {
    private String TAG = "TokHome";

    private HomeContract.IHomePresenter mPresenter;

    private BottomTabView mRecentMsgView;
    private BottomTabView mContactsView;
    private BottomTabView mMineView;
    private BottomTabView[] mBottomViews;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        showFragments();
        new HomePresenter(this);
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    public void initView() {
        mRecentMsgView = $(R.id.id_home_msg_rb);
        mContactsView = $(R.id.id_home_contacts_rb);
        mMineView = $(R.id.id_home_mine_rb);
        mViewPager = $(R.id.id_home_vp);
        mRecentMsgView.setOnClickListener(this);
        mContactsView.setOnClickListener(this);
        mMineView.setOnClickListener(this);
    }

    @Override
    public int getTitleGravity() {
        return GRAVITY_LEFT;
    }

    @Override
    public boolean isShowBackIcon() {
        return false;
    }

    @Override
    public boolean isSupportTaskBack() {
        return true;
    }

    @Override
    public int getToolBarStyle() {
        return TOOL_BAR_STYLE_WHITE_BIG;
    }

    @Override
    public int getTitleId() {
        return R.string.app_name;
    }

    @Override
    public int getMenuImgId() {
        return R.drawable.add;
    }

    @Override
    public void onMenuClick() {
        PopupWindow popupWindow = HomeMenuWindow.getHomeMenu(this.getActivity());
        popupWindow.showAsDropDown(mMenuLayout, 0, ScreenUtils.dimen2px(this, R.dimen.s_20));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_home_msg_rb:
            case R.id.id_home_contacts_rb:
            case R.id.id_home_mine_rb:
                int tagPosition = Integer.valueOf((String) v.getTag());
                setViewPagerSelected(tagPosition);
                break;
        }
    }

    private void showFragments() {
        int size = 3;
        mBottomViews = new BottomTabView[size];
        mBottomViews[0] = mRecentMsgView;
        mBottomViews[1] = mContactsView;
        mBottomViews[2] = mMineView;

        List<Fragment> fragmentList = new ArrayList<>(size);
        //fragmentList.add(new RecentFragment());
        fragmentList.add(RecentMsgFragment.getInstance());
        fragmentList.add(ContactFragment.getInstance());
        fragmentList.add(MineFragment.getInstance());
        mViewPager.setOffscreenPageLimit(size);
        mViewPager.setAdapter(
            new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList, null));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setBottomMenuSelected(position);
            }
        });
    }

    private void setBottomMenuSelected(int position) {
        for (int i = 0; i < mBottomViews.length; i++) {
            if (i == position) {
                mBottomViews[i].setSelected(true);
            } else {
                mBottomViews[i].setSelected(false);
            }
        }
    }

    private void setViewPagerSelected(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    @Override
    public void showFriendReqCount(int countReq) {
        mContactsView.setNewMsg(countReq);
    }

    @Override
    public void showUnReadMsg(int unReadMsgNum) {
        mRecentMsgView.setNewMsg(unReadMsgNum);
    }

    @Override
    public void showFindFriendBotFeature(String newContent) {
        mMineView.setNewFeature(newContent);
    }

    @Override
    public void showAddFriend(String friendPk) {
        DialogFactory.addFriendDialog(this, friendPk, null, true, null, null, (View v) -> {
            ToastUtils.show(R.string.add_friend_request_has_send);
            viewDestroy();
        });
    }

    @Override
    public void setPresenter(HomeContract.IHomePresenter iHomePresenter) {
        mPresenter = iHomePresenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewDestroy();
        LogUtil.i(TAG, "home destroy");
    }

    @Override
    public void viewDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
    }
}
