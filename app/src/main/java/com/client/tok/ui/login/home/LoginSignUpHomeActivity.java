package com.client.tok.ui.login.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.dialog.DialogFactory;
import com.client.tok.widget.dialog.DialogView;
import java.io.File;

public class LoginSignUpHomeActivity extends BaseTitleFullScreenActivity
    implements View.OnClickListener, LoginSignUpContract.ILoginSignUpView {
    private String TAG = "LoginSignUpHomeActivity";
    private View mCreateAccountBt;
    private View mLoginBt;
    private View mImportAccountTv;
    private DialogView mDialogView;

    private LoginSignUpContract.ILoginSignUpPresenter mLoginSignUpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_home);
        initViews();
        new LoginSignUpPresenter(this);
        mLoginSignUpPresenter.start();
    }

    private void initViews() {
        mCreateAccountBt = $(R.id.id_login_create_account_bt);
        mCreateAccountBt.setOnClickListener(this);
        mLoginBt = $(R.id.id_login_login_bt);
        mImportAccountTv = $(R.id.id_login_import_account_tv);
        mImportAccountTv.setOnClickListener(this);
        mLoginBt.setOnClickListener(this);
    }

    @Override
    public void hideLoginSignUpView() {
        mCreateAccountBt.setVisibility(View.GONE);
        mLoginBt.setVisibility(View.GONE);
    }

    @Override
    public void showImportInput(String userName, boolean encrypt) {
        mDialogView = DialogFactory.importAccountDialog(this, userName, encrypt,
            new DialogFactory.InputCallBack() {
                @Override
                public void input(String... input) {
                    String name = input[0];
                    String pwd = input[1];
                    mLoginSignUpPresenter.importInfo(name, pwd);
                }
            });
    }

    @Override
    public void setPresenter(LoginSignUpContract.ILoginSignUpPresenter iLoginSignUpPresenter) {
        mLoginSignUpPresenter = iLoginSignUpPresenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showImportFail(String reason) {
        ToastUtils.showLong(reason);
    }

    @Override
    public void showLoginSignUpView() {
        mCreateAccountBt.setVisibility(View.VISIBLE);
        mLoginBt.setVisibility(View.VISIBLE);
    }

    @Override
    public int getStatusBarStyle() {
        return STATUS_BAR_FULL_SCREEN_TRANSLATE;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_login_create_account_bt:
                PageJumpIn.jumpCreateAccountPage(this);
                break;
            case R.id.id_login_login_bt:
                PageJumpIn.jumpLoginPage(this);
                break;
            case R.id.id_login_import_account_tv:
                showImportPrompt();
                break;
        }
    }

    private void showImportPrompt() {
        DialogFactory.showNormal1ChooseDialog(this,
            StringUtils.getTextFromResId(R.string.sel_tok_file),
            StringUtils.getTextFromResId(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilePicker.openTxtDoc(LoginSignUpHomeActivity.this);
                }
            });
    }

    private void showImportFile(final String filePath) {
        String fileName = new File(filePath).getName();
        DialogFactory.showNormal1ChooseDialog(this, fileName,
            StringUtils.getTextFromResId(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoginSignUpPresenter.importAccountProfile(filePath);
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "requestCode:" + requestCode + "resultCode:" + resultCode + ",data:" + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePicker.REQ_FILE_TXT && data != null) {
                Uri fileUri = data.getData();
                String filePath = ImageUtils.getPath(this, fileUri);
                if (!StringUtils.isEmpty(filePath)) {
                    showImportFile(filePath);
                } else {
                    showImportFail(StringUtils.getTextFromResId(R.string.import_profile_invalid_file));
                }
            }
        }
    }

    @Override
    public void viewDestroy() {
        this.onFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialogView != null && mDialogView.isShowing()) {
            mDialogView.dismiss();
            mDialogView = null;
        }
        if (mLoginSignUpPresenter != null) {
            mLoginSignUpPresenter.destroy();
            mLoginSignUpPresenter = null;
        }
    }
}
