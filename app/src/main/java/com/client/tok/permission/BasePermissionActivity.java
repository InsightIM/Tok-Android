package com.client.tok.permission;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.dialog.DialogFactory;
import com.client.tok.widget.dialog.DialogView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class BasePermissionActivity extends AppCompatActivity {
    private String TAG = "chatPermission";
    private DialogView mPermissionDialog;
    private int mRequestPerCode;
    private BasePermissionActivity mActivity = this;
    //the message prompt to user when permission refused
    private CharSequence mRationale;
    String[] mPermissions = null;
    private PermissionCallBack mCallBack;
    public static Stack<BasePermissionActivity> permissionActivityList = new Stack<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionActivityList.add(this);
    }

    protected void setPermissionsInfo(int requestPerCode, String[] permissions,
        CharSequence rationale, PermissionCallBack callBack) {
        if (requestPerCode > 0) {
            mRequestPerCode = requestPerCode;
        }
        mRationale = rationale;
        if (permissions != null && permissions.length > 0) {
            mPermissions = permissions;
        }
        mCallBack = callBack;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //permission call back here，dispatch by our app
        permissionResultDeal(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionSetting.REQ_CODE_SETTING) {
            if (mPermissions != null) {
                //jump setting's permission
                if (PermissionModel.hasPermissions(mActivity, mPermissions)) {
                    onPermissionsAllGranted(mRequestPerCode, Arrays.asList(mPermissions));
                } else {
                    if (isShowRationale()) {
                        showPermissionSetting();
                    }
                }
            }
        }
    }

    private boolean isShowRationale() {
        return !TextUtils.isEmpty(mRationale);
    }

    public void showPermissionSetting() {
        if (isShowRationale()) {
            if (mPermissionDialog == null) {
                mPermissionDialog = DialogFactory.showTwoBtErrorDialog(this, mRationale,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPermissionDialog.dismiss();
                            BasePermissionActivity.this.onCancelPermissionRationale();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPermissionDialog.dismiss();
                            if (!PermissionModel.somePermissionNeverPrompt(mActivity,
                                mPermissions)) {
                                PermissionModel.requestPermissions(mRequestPerCode, mPermissions,
                                    mRationale, mCallBack);
                            } else {
                                PermissionSetting.startSettingForResult(mActivity,
                                    PermissionSetting.REQ_CODE_SETTING);
                            }
                        }
                    });
            } else if (!mPermissionDialog.isShowing()) {
                mPermissionDialog.show();
            }
        }
    }

    /**
     * all permission granted
     */
    private void onPermissionsAllGranted(int requestCode, @NonNull List<String> grantedPers) {
        LogUtil.i(TAG, "onPermissionsAllGranted");
        if (mCallBack != null) {
            mCallBack.onPermissionsAllGranted(requestCode, grantedPers);
        }
    }

    /**
     * some permission granted
     */
    private void onPermissionsGranted(int requestCode, @NonNull List<String> grantedPers) {
        LogUtil.i(TAG, "onPermissionsGranted");
        if (mCallBack != null) {
            mCallBack.onPermissionsGranted(requestCode, grantedPers);
        }
    }

    /**
     * permission denied
     */
    private void onPermissionsDenied(int requestCode, @NonNull List<String> deniedPers) {
        LogUtil.i(TAG, "onPermissionsDenied");
        if (isShowRationale()) {
            if (PermissionModel.somePermissionsDenied(mActivity, mPermissions)
                || mPermissions.length == deniedPers.size()) {
                showPermissionSetting();
            }
        }
        if (mCallBack != null) {
            mCallBack.onPermissionsDenied(requestCode, deniedPers);
        }
    }

    /**
     * when user cancel jump setting dialog
     */
    private void onCancelPermissionRationale() {
        LogUtil.i(TAG, "onCancelPermissionRationale");
        if (mCallBack != null) {
            mCallBack.onCancelPermissionRationale(mRequestPerCode);
        }
    }

    /**
     * deal permission result when system permission call back
     */
    protected void permissionResultDeal(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        // If 100% successful, call annotated methods
        if (!granted.isEmpty() && denied.isEmpty()) {
            onPermissionsAllGranted(requestCode, denied);
            return;
        }
        // Report granted permissions, if any.
        if (!granted.isEmpty()) {
            onPermissionsGranted(requestCode, granted);
        }

        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            onPermissionsDenied(requestCode, denied);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionActivityList.remove(this);
    }
}
