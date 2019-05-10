package com.client.tok.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.view.View;
import cn.simonlee.xcodescanner.core.CameraScanner;
import cn.simonlee.xcodescanner.core.NewCameraScanner;
import cn.simonlee.xcodescanner.core.OldCameraScanner;
import cn.simonlee.xcodescanner.view.AdjustTextureView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.permission.PermissionCallBack;
import com.client.tok.permission.PermissionModel;
import com.client.tok.ui.scan.decoder.Decoder;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.dialog.DialogFactory;
import java.util.List;

public class ScanActivity extends BaseCommonTitleActivity
    implements ScanContract.IScanView, CameraScanner.CameraListener,
    TextureView.SurfaceTextureListener, Decoder.DecoderResultListener {
    private String TAG = "FScanActivity";
    private ScanContract.IScanPresenter mScanPresenter;
    private boolean mScanable = false;
    private Handler mHandler = new Handler();

    private AdjustTextureView mTextureView;
    private View mScannerFrameView;
    private CameraScanner mCameraScanner;
    private Decoder mDecoder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new);
        initView();
        new ScanPresenter(this);
        checkPermission();
    }

    public void initView() {
        mTextureView = findViewById(R.id.id_texture_view);
        mTextureView.setSurfaceTextureListener(this);
        mScannerFrameView = findViewById(R.id.id_scanner_frame);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraScanner = new NewCameraScanner(this);
        } else {
            mCameraScanner = new OldCameraScanner(this);
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
        FilePicker.openGallery(this, true);
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

    public void startScan() {
        if (mTextureView.isAvailable()) {
            mCameraScanner.setPreviewTexture(mTextureView.getSurfaceTexture());
            mCameraScanner.setPreviewSize(mTextureView.getWidth(), mTextureView.getHeight());
            mCameraScanner.openCamera(this.getApplicationContext());
        }
        mScanable = true;
        LogUtil.i(TAG, "startScan mScanable:" + mScanable);
    }

    @Override
    protected void onRestart() {
        startScan();
        super.onRestart();
    }

    @Override
    public void setScanable(final boolean scanable, int delayMills) {
        if (delayMills <= 0) {
            mScanable = scanable;
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanable = scanable;
                }
            }, delayMills);
        }
        LogUtil.i(TAG, "setScanable:" + scanable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraScanner != null) {
            mCameraScanner.closeCamera();
        }
    }

    @Override
    public void showMsgDialog(String tokId, String alias, String defaultMsg) {
        DialogFactory.addFriendDialog(this, tokId, null, false, defaultMsg,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanActivity.this.setScanable(true, -1);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanActivity.this.setScanable(false, -1);
                    ScanActivity.this.showSuccess(R.string.add_friend_request_has_send);
                    ScanActivity.this.viewDestroy();
                }
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
                LogUtil.i(TAG, "decode file uri:" + fileUri);
                if (fileUri != null) {
                    mDecoder.decoder(this, fileUri);
                } else {
                    showErr(R.string.failed);
                    setScanable(true, -1);
                }
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCameraScanner.setPreviewTexture(surface);
        mCameraScanner.setPreviewSize(width, height);
        mCameraScanner.openCamera(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtil.i(TAG, "onSurfaceTextureSizeChanged() width = " + width + " , height = " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtil.i(TAG, "onSurfaceTextureDestroyed()");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void openCameraSuccess(int surfaceWidth, int surfaceHeight, int surfaceDegree) {
        LogUtil.i(TAG, "openCameraSuccess:" + mScanable);
        mTextureView.setImageFrameMatrix(surfaceWidth, surfaceHeight, surfaceDegree);
        if (mDecoder == null) {
            mDecoder = new Decoder(this);
        }
        mCameraScanner.setFrameRect(mScannerFrameView.getLeft(), mScannerFrameView.getTop(),
            mScannerFrameView.getRight(), mScannerFrameView.getBottom());
        mCameraScanner.setGraphicDecoder(mDecoder.getDecoder());
    }

    @Override
    public void decodeComplete(String result, int type, int quality, int requestCode) {
        LogUtil.i(TAG, "decodeComplete mScanable:" + mScanable);
        if (!StringUtils.isEmpty(result) && mScanable) {
            mScanPresenter.onScanResult(result);
        }
    }

    @Override
    public void openCameraError() {

    }

    @Override
    public void noCameraPermission() {

    }

    @Override
    public void cameraDisconnected() {

    }

    @Override
    public void cameraBrightnessChanged(int brightness) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScanPresenter != null) {
            mScanPresenter.onDestroy();
            mScanPresenter = null;
        }

        if (mDecoder != null) {
            mDecoder.destroy();
            mDecoder = null;
        }
        if (mCameraScanner != null) {
            mCameraScanner.detach();
            mCameraScanner = null;
        }
    }
}