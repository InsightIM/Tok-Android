package com.client.tok.ui.setting;

import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.pagejump.SharePKeys;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.ui.login.login.UserModel;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PreferenceUtils;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.Random;

public class SettingPresenter implements SettingContract.ISettingPresenter {
    private String TAG = "SettingPresenter";
    private SettingContract.ISettingView mSettingView;
    private UserModel mUserModel = new UserModel();
    private SettingModel mSettingModel = new SettingModel();
    private Disposable mCleanHisDis;
    private Disposable mDelProfileDis;
    private Disposable mLogoutDis;

    public SettingPresenter(SettingContract.ISettingView settingView) {
        this.mSettingView = settingView;
        mSettingView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        checkHasPwd();
    }

    private void checkHasPwd() {
        String userName = State.userRepo().getActiveUser();
        boolean hasPwd = State.userRepo().hasPwd(userName);
        mSettingView.showPwdPrompt(hasPwd, hasPwd ? R.string.change_pwd : R.string.setting_pwd);
    }

    @Override
    public void exportAccountInfo() {
        String profileFolder = StorageUtil.getProfileFolder();
        boolean result = mSettingModel.exportAccountInfo();
        if (result) {
            mSettingView.showExportSuccess(
                StringUtils.formatTxFromResId(R.string.export_profile_path,
                    ToxManager.getManager().toxData.getProfileName(), profileFolder),
                profileFolder);
        } else {
            mSettingView.showMsg(StringUtils.getTextFromResId(R.string.export_failed));
        }
    }

    @Override
    public void clearMsgLogout(boolean enable) {
        PreferenceUtils.saveBoolean(PreferenceUtils.CLEAR_MSG_LOGOUT, enable);
        if (enable) {
            mSettingView.showWarning(
                StringUtils.getTextFromResId(R.string.logout_clear_msg_warning));
        }
    }

    @Override
    public void setNospam() {
        Random random = new Random();
        int maxNospam = 1234567890;
        int nospam = random.nextInt(maxNospam);
        LogUtil.i(TAG, "nospam:" + nospam);
        ToxManager.getManager().toxBase.setNospam(nospam);
        mSettingView.showMsg(StringUtils.getTextFromResId(R.string.successful));
        PreferenceUtils.saveString(SharePKeys.CHAT_ID,
            ToxManager.getManager().toxBase.getSelfAddress().toString());
        mSettingModel.exportAccountInfo();
    }

    @Override
    public void clearMsgHistory() {
        mSettingView.showLoading();
        mCleanHisDis = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean result = mUserModel.clearChatHistory();
                emitter.onNext(result);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean o) throws Exception {
                    mSettingView.hideLoading();
                    mSettingView.showMsg(StringUtils.getTextFromResId(R.string.successful));
                }
            });
    }

    @Override
    public void delProfile() {
        mSettingView.showLoading();

        mDelProfileDis = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean result = mUserModel.destroyAccount();
                emitter.onNext(result);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    mSettingView.hideLoading();
                    mSettingView.showMsg(StringUtils.getTextFromResId(R.string.successful));
                    SettingPresenter.this.logout();
                }
            });
    }

    @Override
    public void logout() {
        mSettingView.showLoading();

        mLogoutDis = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean result = true;
                if (PreferenceUtils.getBoolean(PreferenceUtils.CLEAR_MSG_LOGOUT, false)) {
                    result = mUserModel.clearChatHistory();
                }
                if (State.isLoggedIn()) {
                    State.logout();
                }
                emitter.onNext(result);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    mSettingView.hideLoading();
                    ServiceManager.stopToxService();
                    TokApplication.getInstance().finishOpenedActivities();
                    PageJumpIn.jumpLoginPage(mSettingView.getActivity());
                    mSettingView.viewDestroy();
                }
            });
    }

    @Override
    public void onDestroy() {
        if (mSettingView != null) {
            mSettingView = null;
        }
        if (mCleanHisDis != null && !mCleanHisDis.isDisposed()) {
            mCleanHisDis.dispose();
        }
        mCleanHisDis = null;
        if (mDelProfileDis != null && !mDelProfileDis.isDisposed()) {
            mDelProfileDis.dispose();
        }
        mDelProfileDis = null;

        if (mLogoutDis != null && !mLogoutDis.isDisposed()) {
            mLogoutDis.dispose();
        }
        mLogoutDis = null;
    }
}
