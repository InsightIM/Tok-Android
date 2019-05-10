package com.client.tok.ui.share;

import com.client.QR.Contents;
import com.client.QR.QRCodeEncode;
import com.client.tok.R;
import com.client.tok.bean.UserInfo;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class SharePresenter implements ShareContract.ISharePresenter {
    private String TAG = "SharePresenter";
    private ShareContract.IShareView mShareView;
    private String mSelfAddress;
    private String mKey;
    private String mNickName;
    private String mQrPath;

    public SharePresenter(ShareContract.IShareView iLoginSignUpView) {
        this.mShareView = iLoginSignUpView;
        mShareView.setPresenter(this);
    }

    @Override
    public void start() {
        mShareView.setPresenter(this);
        getInfo();
        generateQR();
        setShareViewInfo();
    }

    private void getInfo() {
        UserInfo userInfo = State.userRepo().getActiveUserDetails();
        mKey = ToxManager.getManager().toxBase.getSelfKey().getKey();
        mSelfAddress = ToxManager.getManager().toxBase.getSelfAddress().getAddress().toUpperCase();
        mNickName = new String(userInfo.getNickname().value);
        mShareView.showPortraitView(mKey, mNickName);
        mShareView.showMyInfo(
            StringUtils.formatHtmlTxFromResId(R.string.share_info_add_me, mNickName));
        mShareView.showShareTxt(
            StringUtils.formatTxFromResId(R.string.share_txt_prompt, mSelfAddress));
    }

    private void generateQR() {
        mQrPath = StorageUtil.getShareQrCodeFile();
        String qrContent = StringUtils.formatTxFromResId(R.string.share_qr_content, mSelfAddress);
        LogUtil.i(TAG, "share qr content:" + qrContent);
        int qrCodeSize = 800;
        QRCodeEncode qrCodeEncoder =
            new QRCodeEncode(qrContent, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                qrCodeSize);
        try {
            ImageUtils.compressBitmap(qrCodeEncoder.encodeAsBitmap(), mQrPath);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mShareView.showQr(mQrPath);
    }

    private void setShareViewInfo() {
        mShareView.setShareViewInfo(mKey, mNickName, mQrPath);
    }
}
