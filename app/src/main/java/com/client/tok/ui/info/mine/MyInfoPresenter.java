package com.client.tok.ui.info.mine;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import com.client.tok.TokApplication;
import com.client.tok.bean.UserInfo;
import com.client.tok.db.DBConstants;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.rx.event.PortraitEvent;
import com.client.tok.rx.RxBus;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.ui.profileedit.ProfileEditPresenter;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;

public class MyInfoPresenter implements MyInforContract.IMyInfoPresenter {
    private String TAG = "MyInfoPresenter";
    private MyInforContract.IMyInfoView mMyInfoView;

    private UserRepository mUserRepo = State.userRepo();
    private String mUserKey = ToxManager.getManager().toxBase.getSelfKey().key;

    public MyInfoPresenter(MyInforContract.IMyInfoView myInfoView) {
        this.mMyInfoView = myInfoView;
        mMyInfoView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        getUserInfo();
    }

    private void getUserInfo() {
        mUserRepo.activeUserDetailsObservable().observe(mMyInfoView, new Observer<UserInfo>() {
            @Override
            public void onChanged(@Nullable UserInfo userInfo) {
                LogUtil.i(TAG, "getUserInfo start");
                mMyInfoView.showUserInfo(userInfo);
            }
        });
    }

    @Override
    public void editNickName() {
        PageJumpIn.jumpProfileEditPage(TokApplication.getInstance(), mUserKey,
            ProfileEditPresenter.EDIT_NAME);
    }

    @Override
    public void editSignature() {
        PageJumpIn.jumpProfileEditPage(TokApplication.getInstance(), mUserKey,
            ProfileEditPresenter.EDIT_SIGNATURE);
    }

    @Override
    public void updateAvatars(String friendKey, String avatarName) {
        State.userRepo().updateActiveUserDetail(DBConstants.COLUMN_AVATAR, avatarName);
        RxBus.publish(new PortraitEvent(friendKey, avatarName));
        State.infoRepo().setAllFriendReceivedAvatar(false);
        State.transferManager().updateSelfAvatar2All();
    }

    @Override
    public void delAvatars() {
        FileUtilsJ.delFile(StorageUtil.getAvatarsFolder() + mUserKey+".png");
        updateAvatars(mUserKey, "");
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "mine presenter onDestroy");
    }
}
