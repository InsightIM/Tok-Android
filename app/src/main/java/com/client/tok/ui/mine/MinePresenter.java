package com.client.tok.ui.mine;

import com.client.tok.R;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ToxAddress;
import com.client.tok.bean.UserInfo;
import com.client.tok.bot.BotManager;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.msg.UserStatus;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.rx.RxBus;
import com.client.tok.tox.State;
import com.client.tok.ui.addfriends.AddFriendsModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxUserStatus;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MinePresenter implements MineContract.IMinePresenter {
    private String TAG = "MinePresenter";
    private MineContract.IMineView mMineView;
    private Disposable mConnectionDis;
    private UserRepository mUserRepo = State.userRepo();
    private String mFindFriendBotPk;

    public MinePresenter(MineContract.IMineView iMineView) {
        this.mMineView = iMineView;
        mMineView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        mFindFriendBotPk = BotManager.getInstance().getAddFriendBotPk();
        getUserInfo();
        getUserStatus();
        observerFindFriendBot();
    }

    private void getUserInfo() {
        LogUtil.i(TAG, "getUserInfo start");
        mUserRepo.activeUserDetailsObservable().observe(mMineView, (UserInfo userInfo) -> {
            mMineView.showUserInfo(userInfo);
        });
    }

    private void getUserStatus() {
        mMineView.showStatus(GlobalParams.OFF_LINE);
        mConnectionDis = RxBus.listen(ToxConnection.class)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((ToxConnection toxConnection) -> {
                LogUtil.i(TAG, toxConnection.toString());
                String userStatus;
                ToxUserStatus status;
                try {
                    status = UserStatus.getUserStatusFromString(
                        mUserRepo.getActiveUserDetails().getStatus());
                    LogUtil.i(TAG, "toxConnection:"
                        + toxConnection
                        + ",status:"
                        + status); //先判断toxConnection状态
                    if (toxConnection == ToxConnection.NONE) {
                        userStatus = GlobalParams.OFF_LINE;
                    } else {
                        switch (status) {
                            case NONE:
                                userStatus = GlobalParams.ON_LINE;
                                break;
                            case AWAY:
                                userStatus = GlobalParams.AWAY;
                                break;
                            case BUSY:
                                userStatus = GlobalParams.BUSY;
                                break;
                            default:
                                userStatus = GlobalParams.OFF_LINE;
                        }
                    }
                } catch (Exception e) {
                    userStatus = GlobalParams.OFF_LINE;
                }
                mMineView.showStatus(userStatus);
            });
    }


    @Override
    public void showFindFriendBot() {
        PageJumpIn.jumpFriendInfoPage(mMineView.getActivity(), "-1", mFindFriendBotPk);
        PreferenceUtils.setHasShowFindFriendBotFeat();
    }

    @Override
    public void observerFindFriendBot() {
        //boolean newFeat = PreferenceUtils.hasShowFindFriendBotFeat();
        //if (!newFeat) {
        //    mMineView.showFindFriendBotNew(StringUtils.getTextFromResId(R.string.new_tag),
        //        R.style.UnReadMsgRed, R.drawable.unread_feature_indicator_white);
        //} else {
        //    mMineView.showFindFriendBotNew(null, -1, -1);
        //}
        State.infoRepo()
            .getFriendInfoLive(mFindFriendBotPk)
            .observe(mMineView, (ContactsInfo contactsInfo) -> {
                LogUtil.i(TAG, "observer find friend bot:" + (contactsInfo == null));
                if (contactsInfo == null) {
                    mMineView.showFindFriendBotNew(StringUtils.getTextFromResId(R.string.new_tag),
                        R.style.UnReadMsgRed, R.drawable.unread_num_indicator_red);
                } else {
                    mMineView.showFindFriendBotNew(null, -1, -1);
                }
            });
    }

    @Override
    public void onDestroy() {
        if (mConnectionDis != null && !mConnectionDis.isDisposed()) {
            mConnectionDis.dispose();
            mConnectionDis = null;
        }
    }
}
