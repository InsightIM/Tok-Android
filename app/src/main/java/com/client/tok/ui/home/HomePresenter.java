package com.client.tok.ui.home;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bot.BotManager;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;

public class HomePresenter implements HomeContract.IHomePresenter {
    private String TAG = "HomePresenter";
    private HomeContract.IHomeView mHomeView;
    private InfoRepository mInfoRepo = State.infoRepo();
    private AddFriendsModel mAddFriendModel = new AddFriendsModel();

    public HomePresenter(HomeContract.IHomeView homeView) {
        StorageUtil.initFolders();
        mHomeView = homeView;
        mHomeView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        observerUnReadMsg();
        observerFriendReq();
    }

    /**
     * listen un read message
     */
    private void observerUnReadMsg() {
        if (mInfoRepo != null) {
            mInfoRepo.totalUnreadMsg().observe(mHomeView, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {
                    mHomeView.showUnReadMsg(integer != null ? integer : 0);
                }
            });
        }
    }

    /**
     * listen new friend requset
     */
    private void observerFriendReq() {
        if (mInfoRepo != null) {
            mInfoRepo.totalUnreadMsg().observe(mHomeView, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {
                    mHomeView.showUnReadMsg(integer != null ? integer : 0);
                }
            });

            mInfoRepo.getFriendReqUnReadCount().observe(mHomeView, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {
                    mHomeView.showFriendReqCount(integer != null ? integer : 0);
                }
            });
        }
    }

    @Override
    public void onResume() {
        checkClipBoard();
        checkNewFeature();
    }

    private void checkClipBoard() {
        String clipPk = PkUtils.getAddressFromClip(mHomeView.getActivity());
        if (!StringUtils.isEmpty(clipPk)) {
            try {
                if (!mAddFriendModel.isMyOwnChatId(clipPk) && !mAddFriendModel.isFriendExist(
                    clipPk)) {
                    mHomeView.showAddFriend(clipPk);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkNewFeature() {
        if (mInfoRepo != null) {
            ContactInfo info =
                mInfoRepo.getFriendInfo(BotManager.getInstance().getFindFriendBotPk());
            boolean show = PreferenceUtils.hasShowFindFriendBotFeat() || info != null;
            mHomeView.showFindFriendBotFeature(
                show ? "" : StringUtils.getTextFromResId(R.string.new_tag));
        }
    }

    @Override
    public void onDestroy() {
        if (mHomeView != null) {
            mHomeView = null;
        }
        LogUtil.i(TAG, "HomePresenter onDestroy");
    }
}
