package com.client.tok.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.permission.PermissionCallBack;
import com.client.tok.permission.PermissionModel;
import com.client.tok.ui.scan.widget.ScanView;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.ToastUtils;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.dialog.DialogFactory;
import java.util.List;

public class ScanActivity extends BaseCommonTitleActivity
    implements ScanContract.IScanView, ScanView.ScanListener {
    private String TAG = "FScanActivity";
    private ScanView mScanView;
    private ScanContract.IScanPresenter mScanPresenter;
    private boolean mScanable = false;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        new ScanPresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScanView != null) {
            mScanView.stopScan();
        }
    }

    public void initView() {
        if (mScanView == null) {
            ViewGroup rootLayout = $(R.id.id_scan_root_layout);
            View scanLayout = ViewUtil.inflateViewById(this, R.layout.layout_scan);
            mScanView = scanLayout.findViewById(R.id.id_scan_view);
            ViewGroup.LayoutParams params = mScanView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            rootLayout.addView(scanLayout, params);
            mScanView.setScanListener(this);
        }
    }

    @Override
    public int getTitleId() {
        return R.string.scan;
    }

    @Override
    public int getMenuTxtId() {
        return R.string.album;
    }

    @Override
    public void onMenuClick() {
        FilePicker.openGallery(this);
    }

    @Override
    public void setPresenter(ScanContract.IScanPresenter iSettingPresenter) {
        mScanPresenter = iSettingPresenter;
    }

    public void checkPermission() {
        String[] permissions = PermissionModel.PERMISSION_CAMERA;
        if (PermissionModel.hasPermissions(permissions)) {
            startScan();
        } else {
            PermissionModel.requestPermissions(permissions,
                PermissionModel.getRationalByPer(permissions), new PermissionCallBack() {
                    @Override
                    public void onPermissionsAllGranted(int requestCode,
                        @NonNull List<String> grantedPers) {
                        startScan();
                    }

                    @Override
                    public void onCancelPermissionRationale(int requestCode) {
                        finish();
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode,
                        @NonNull List<String> deniedPers) {
                        showPermissionSetting();
                    }
                });
        }
    }

    @Override
    public void startScan() {
        initView();
        mScanView.startScan();
        mScanable = true;
        LogUtil.i(TAG, "startScan");
    }

    @Override
    public void reStartScan(int delayMills) {
        if (delayMills <= 0) {
            mScanView.restartScan();
        } else {
            mHandler.postDelayed(() -> {
                mScanView.restartScan();
            }, delayMills);
        }
        LogUtil.i(TAG, "reStartScan");
    }

    @Override
    public void stopScan() {
        if (mScanable && mScanView != null) {
            mScanView.stopScan();
            LogUtil.i(TAG, "stopScan");
        }
    }

    @Override
    public void showMsgDialog(String tokId, String alias, String defaultMsg) {
        DialogFactory.addFriendDialog(this, tokId, null, false, defaultMsg, v -> {
            reStartScan(-1);
        }, v -> {
            stopScan();
            showSuccess(R.string.add_friend_request_has_send);
            viewDestroy();
        });
    }

    @Override
    public void showErr(int msgId) {
        ToastUtils.showLong(msgId);
    }

    @Override
    public void showSuccess(int msgId) {
        ToastUtils.show(msgId);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        onFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "activity result requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePicker.REQ_IMG_GALLERY && data != null) {
                Uri fileUri = data.getData();
                String imgPath = ImageUtils.getImgPathFromUri(this, fileUri);
                LogUtil.i(TAG, "decode file path:" + imgPath);
                mScanView.decodeFile(imgPath);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScanPresenter != null) {
            mScanPresenter.onDestroy();
            mScanPresenter = null;
        }
        if (mScanView != null) {
            mScanView.destroy();
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtil.i(TAG, "scanResult:" + result);
        stopScan();
        mScanPresenter.onScanResult(result);
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        LogUtil.i(TAG, "onCameraAmbientBrightnessChanged:" + isDark);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogUtil.i(TAG, "onScanQRCodeOpenCameraError");
    }
}