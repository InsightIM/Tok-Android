package com.client.tok.ui.login.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.client.tok.R;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.service.ServiceManager;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxDataFile;
import com.client.tok.ui.login.AccountUtil;
import com.client.tok.ui.login.login.UserModel;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LocalBroaderUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import java.io.File;

public class LoginSignUpPresenter implements LoginSignUpContract.ILoginSignUpPresenter {
    private String TAG = "LoginSignUpPresenter";
    private LoginSignUpContract.ILoginSignUpView mLoginSignUpView;
    private UserRepository mUserRepo = State.userRepo();
    private UserModel mUserModel = new UserModel();
    private BroadcastReceiver mReceiver;

    private boolean mEncrypt;
    private String mImportFile;

    public LoginSignUpPresenter(LoginSignUpContract.ILoginSignUpView iLoginSignUpView) {
        this.mLoginSignUpView = iLoginSignUpView;
        mLoginSignUpView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoginSignUpView.setPresenter(this);
        mLoginSignUpView.showLoginSignUpView();
        registerLoginResultReceiver();
    }

    @Override
    public void importInfo(String userName, String pwd) {
        if (mUserRepo.doesUserExist(userName)) {
            mLoginSignUpView.showImportFail(StringUtils.getTextFromResId(R.string.user_has_exist));
            return;
        }
        byte[] decryptData;
        if (mEncrypt) {
            decryptData = ToxDataFile.decrypt(mImportFile, pwd);
        } else {
            decryptData = FileUtilsJ.readToBytes(mImportFile);
        }
        if (decryptData != null) {
            File userFile = new File(FileUtilsJ.getAppPath() + File.separator + userName);
            LogUtil.i(TAG, "userFile:" + userFile.getPath());
            FileUtilsJ.copy(decryptData, userFile);
            boolean result = mUserModel.createUser(userName, pwd, false, false);
            if (result) {
                importSuccess();
            } else {
                importFail();
            }
        } else {
            importFail();
        }
    }

    @Override
    public void importAccountProfile(String filePath) {
        mImportFile = filePath;
        LogUtil.i(TAG, "import filePath:" + filePath);
        if (StringUtils.isEmpty(filePath) || !AccountUtil.isSupportAccountFile(filePath)) {
            mLoginSignUpView.showImportFail(
                StringUtils.getTextFromResId(R.string.import_profile_invalid_file));
        } else {
            String userName = AccountUtil.replaceSuffix(new File(filePath).getName());
            mEncrypt = ToxDataFile.isEncrypted(filePath);
            mLoginSignUpView.showImportInput(userName, mEncrypt);
        }
    }

    private void registerLoginResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case GlobalParams.ACTION_LOGIN_SUCCESS:
                        mLoginSignUpView.viewDestroy();
                        LocalBroaderUtils.unRegisterReceiver(mReceiver);
                        break;
                }
            }
        };
        LocalBroaderUtils.registerLocalReceiver(mReceiver, GlobalParams.ACTION_LOGIN_SUCCESS);
    }

    private void importSuccess() {
        ServiceManager.startToxService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PageJumpIn.jumpHomePage(mLoginSignUpView.getActivity());
                mLoginSignUpView.viewDestroy();
            }
        }, GlobalParams.DELAY_ENTER_HOME);
    }

    public void importFail() {
        LogUtil.e(TAG, "login from file Failed");
        mLoginSignUpView.showImportFail(
            StringUtils.getTextFromResId(R.string.login_from_file_failed));
    }

    @Override
    public void destroy() {
        if (mReceiver != null) {
            LocalBroaderUtils.unRegisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}
