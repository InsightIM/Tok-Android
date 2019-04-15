package com.client.tok.ui.share;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseTitleFullScreenActivity;
import com.client.tok.share.ShareModule;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.MsgTextView;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ShareView;

public class ShareActivity extends BaseTitleFullScreenActivity
    implements ShareContract.IShareView, View.OnClickListener {
    private ShareContract.ISharePresenter mLoginSignUpPresenter;

    private PortraitView mPortraitView;
    private TextView mMyInfoTv;
    private ImageView mShareQrIv;
    private MsgTextView mShareTxtTv;
    private TextView mShareTxtBtn;
    private TextView mShareImgBtn;
    private ShareView mShareView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
        new SharePresenter(this);
        mLoginSignUpPresenter.start();
    }

    private void initView() {
        mPortraitView = $(R.id.id_share_pv);
        mMyInfoTv = $(R.id.id_share_my_info_tv);
        mShareQrIv = $(R.id.id_share_my_qr_iv);
        mShareTxtTv = $(R.id.id_share_txt_tv);
        mShareTxtBtn = $(R.id.id_share_txt_bt);
        mShareTxtBtn.setOnClickListener(this);
        mShareImgBtn = $(R.id.id_share_img_bt);
        mShareImgBtn.setOnClickListener(this);
        mShareView = $(R.id.id_share_view);
    }

    @Override
    public void setPresenter(ShareContract.ISharePresenter iLoginSignUpPresenter) {
        mLoginSignUpPresenter = iLoginSignUpPresenter;
    }

    @Override
    public void showPortraitView(String key, String name) {
        mPortraitView.setFriendText(key, name);
    }

    @Override
    public void showMyInfo(CharSequence info) {
        mMyInfoTv.setText(info);
    }

    @Override
    public void showQr(String path) {
        ImageLoadUtils.loadImg(this, path, mShareQrIv);
    }

    @Override
    public void showShareTxt(String content) {
        mShareTxtTv.setMsg(content);
    }

    @Override
    public void setShareViewInfo(String key, String name, String path) {
        mShareView.setInfo(key, name, path);
        ViewUtil.layoutView(getActivity(), mShareView);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getToolBarStyle() {
        return TOOL_BAR_TRANSLATE;
    }

    @Override
    public int getStatusBarStyle() {
        return STATUS_BAR_FULL_SCREEN_TRANSLATE;
    }

    @Override
    public boolean isShowToolBar() {
        return true;
    }

    @Override
    public boolean isShowBackIcon() {
        return true;
    }

    @Override
    public int getBackIcon() {
        return R.drawable.arrow_back_white;
    }

    @Override
    public void viewDestroy() {
        this.onFinish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_share_txt_bt:
                ShareModule.shareText(getActivity(), mShareTxtTv.getText().toString());
                break;
            case R.id.id_share_img_bt:
                String shareImgPath = StorageUtil.getShareImgFile();
                FileUtilsJ.saveViewToImg(mShareView, shareImgPath);
                ShareModule.shareImg(getActivity(), shareImgPath);
                break;
        }
    }
}
