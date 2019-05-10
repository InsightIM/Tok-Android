package com.client.tok.ui.info.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.bean.UserInfo;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.FilePicker;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.ItemInfoView;
import com.client.tok.widget.dialog.DialogFactory;
import java.io.File;
import top.zibin.luban.OnCompressListener;

public class MyInfoActivity extends BaseCommonTitleActivity
    implements MyInforContract.IMyInfoView, View.OnClickListener {
    private String TAG = "MyInforActivity1";
    private ItemInfoView mPortraitView;
    private ItemInfoView mNickNameView;
    private ItemInfoView mSignatureView;
    private MyInforContract.IMyInfoPresenter mMyInfoPresenter;

    private String mSelfKey = ToxManager.getManager().toxBase.getSelfKey().getKey();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_infor);
        initView();
        new MyInfoPresenter(this);
    }

    public void initView() {
        mPortraitView = $(R.id.id_my_info_portrait_iiv);
        mNickNameView = $(R.id.id_my_info_nick_name_iiv);
        mSignatureView = $(R.id.id_my_info_signature_iiv);
        mPortraitView.setOnClickListener(this);
        mNickNameView.setOnClickListener(this);
        mSignatureView.setOnClickListener(this);
    }

    @Override
    public int getTitleId() {
        return R.string.personal_info;
    }

    @Override
    public void setPresenter(MyInforContract.IMyInfoPresenter iSettingPresenter) {
        mMyInfoPresenter = iSettingPresenter;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_my_info_portrait_iiv://TODO
                DialogFactory.showSelImgMethodDialog(this,
                    StringUtils.getTextFromResId(R.string.edit_avatar), new View.OnClickListener() {
                        @Override
                        public void onClick(View cancel) {
                            mMyInfoPresenter.delAvatars();
                        }
                    });
                break;
            case R.id.id_my_info_nick_name_iiv:
                mMyInfoPresenter.editNickName();
                break;
            case R.id.id_my_info_signature_iiv:
                mMyInfoPresenter.editSignature();
                break;
        }
    }

    @Override
    public void showUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            String nickName = userInfo.getNickname().toString();
            LogUtil.i(TAG, "myInfo toxMeName:" + nickName);
            mPortraitView.setPortrait(mSelfKey, nickName);
            mPortraitView.setClickEnterDetail(false);
            mNickNameView.setContent(nickName);
            mSignatureView.setContent(new String(userInfo.getStatusMessage().value));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "activity result requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePicker.REQ_IMG_GALLERY
                || requestCode == FilePicker.REQ_IMG_CAMERA) {
                Uri fileUri = null;
                if (requestCode == FilePicker.REQ_IMG_GALLERY && data != null) {
                    fileUri = data.getData();
                } else if (requestCode == FilePicker.REQ_IMG_CAMERA) {
                    fileUri = FilePicker.getImgCameraUri();
                }
                if (fileUri != null) {
                    String inPath = ImageUtils.getPath(this, fileUri);
                    String outPath = StorageUtil.getAvatarsFolder() + mSelfKey+".png";
                    PageJumpIn.jumpClipImgPage(this, inPath, outPath);
                }
            } else if (requestCode == GlobalParams.REQ_CODE_PORTRAIT) {
                final String sourcePath = data.getStringExtra(IntentConstants.IMG_OUT_PATH);
                LogUtil.i(TAG, "sourcePath:" + sourcePath);
                ImageUtils.compressImg(sourcePath, StorageUtil.getAvatarsFolder(),
                    new OnCompressListener() {
                        @Override
                        public void onStart() {
                            LogUtil.i(TAG, "compress portrait start");
                        }

                        @Override
                        public void onSuccess(File file) {
                            LogUtil.i(TAG, "compress portrait success name:" + file.getPath());
                            String avatarFileName = mSelfKey+".png";
                            if (!sourcePath.equals(file.getPath())) {
                                FileUtilsJ.delFile(sourcePath);
                                FileUtilsJ.rename(file.getPath(), avatarFileName);
                            }
                            mMyInfoPresenter.updateAvatars(mSelfKey, avatarFileName);
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.i(TAG, "compress portrait error");
                        }
                    });
            }
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void viewDestroy() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMyInfoPresenter != null) {
            mMyInfoPresenter.onDestroy();
            mMyInfoPresenter = null;
        }
    }
}