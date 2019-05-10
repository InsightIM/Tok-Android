package com.client.tok.ui.chatid;

import android.content.Intent;
import com.client.QR.Contents;
import com.client.QR.QRCodeEncode;
import com.client.tok.R;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class TokIdPresenter implements TokIdContract.IChatIdPresenter {
    public TokIdContract.IChatIdView mChatIdView;
    private String mIdType;//tok id type  current user/friend(not support now)/group
    private String mChatId;
    private String mQrCodePath;
    private Intent mIntent;

    public TokIdPresenter(TokIdContract.IChatIdView chatIdView) {
        this.mChatIdView = chatIdView;
        mChatIdView.setPresenter(this);
    }

    @Override
    public void start() {
        readData();
        mChatIdView.showTitle(getTitle());
        mChatId = getTokId();
        mChatIdView.showTokId(mChatId);
        mQrCodePath = generateQR(mChatId);
        mChatIdView.showTokIdImg(mQrCodePath);
    }

    private void readData() {
        mIntent = mChatIdView.getActivity().getIntent();
        mIdType = mIntent.getStringExtra(IntentConstants.ID_TYPE);
    }

    private String getTokId() {
        //return PreferenceUtils.getString(SharePKeys.CHAT_ID, "");
        return ToxManager.getManager().toxBase.getSelfAddress().getAddress();
    }

    private String getTitle() {
        return StringUtils.getTextFromResId(R.string.my_tok_id);
    }

    private String generateQR(String tokId) {
        String outPath = StorageUtil.getQrCodeFile();
        int qrCodeSize = 800;
        QRCodeEncode qrCodeEncoder =
            new QRCodeEncode(tokId, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                qrCodeSize);
        try {
            ImageUtils.compressBitmap(qrCodeEncoder.encodeAsBitmap(), outPath);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return outPath;
    }

    @Override
    public void share() {
        PageJumpIn.jumpSharePage(mChatIdView.getActivity());
    }

    @Override
    public void onDestroy() {
        if (mChatIdView != null) {
            mChatIdView = null;
        }
    }
}
