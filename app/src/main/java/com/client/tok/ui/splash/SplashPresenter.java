package com.client.tok.ui.splash;

import android.os.Handler;
import android.support.annotation.NonNull;
import com.client.tok.TokApplication;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.permission.BasePermissionActivity;
import com.client.tok.permission.PermissionCallBack;
import com.client.tok.permission.PermissionModel;
import com.client.tok.service.ChatMainService;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.utils.PreferenceUtils;
import java.util.List;

public class SplashPresenter implements SplashContract.ISplashPresenter {
    private SplashContract.ISplashView mSplashView;
    private String[] mPermission = PermissionModel.PERMISSION_STORAGE;

    public SplashPresenter(SplashContract.ISplashView iLoginSignUpView) {
        this.mSplashView = iLoginSignUpView;
        mSplashView.setPresenter(this);
    }

    @Override
    public void start() {
        mSplashView.setPresenter(this);
        checkPermission();
    }

    private void checkPermission() {
        if (PermissionModel.hasPermissions(mSplashView.getActivity(), mPermission)) {
            enter();
        } else {
            PermissionModel.requestPermissions(mPermission,
                PermissionModel.getRationalByPer(mPermission), new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        enter();
                    }

                    @Override
                    public void onCancelPermissionRationale(int requestCode) {
                        TokApplication.getInstance().exit();
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode,
                        @NonNull List<String> deniedPers) {
                        ((BasePermissionActivity) mSplashView.getActivity()).showPermissionSetting();
                    }
                });
        }
    }

    @Override
    public void enter() {
        if (PreferenceUtils.hasShowGuide()) {
            if (State.userRepo().loggedIn()) {
                if (!ChatMainService.isRunning) {
                    ServiceManager.startToxService();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PageJumpIn.jumpHomePage(mSplashView.getActivity());
                            mSplashView.viewDestroy();
                        }
                    }, GlobalParams.DELAY_ENTER_HOME);
                } else {
                    PageJumpIn.jumpHomePage(mSplashView.getActivity());
                }
            } else {
                PageJumpIn.jumpLoginHomePage(mSplashView.getActivity());
                mSplashView.viewDestroy();
            }
        } else {
            mSplashView.showGuideView();
        }
        //PageJumpIn.jumpVideoRecordPage(mSplashView.getActivity());
        //mSplashView.viewDestroy();
    }
}
