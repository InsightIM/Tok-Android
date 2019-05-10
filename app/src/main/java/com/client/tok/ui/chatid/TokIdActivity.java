package com.client.tok.ui.chatid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseCommonTitleActivity;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;
import java.io.File;

public class TokIdActivity extends BaseCommonTitleActivity
    implements TokIdContract.IChatIdView, View.OnClickListener {
    private View mCopyTv;
    private TextView mTokIdTv;
    private ImageView mTokIdIv;

    private TokIdContract.IChatIdPresenter mChatIdPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tok_id);
        initView();
        new TokIdPresenter(this);
        mChatIdPresenter.start();
    }

    public void initView() {
        mCopyTv = $(R.id.id_chat_id_copy_tv);
        mCopyTv.setOnClickListener(this);
        mTokIdTv = $(R.id.id_chat_id_tv);
        mTokIdTv.setOnClickListener(this);
        mTokIdIv = $(R.id.id_chat_id_qrcode_iv);
        mTokIdIv.setOnClickListener(this);
    }

    @Override
    public int getMenuImgId() {
        return R.drawable.share;
    }

    @Override
    public void onMenuClick() {
        mChatIdPresenter.share();
    }

    @Override
    public void setPresenter(TokIdContract.IChatIdPresenter chatIdPresenter) {
        mChatIdPresenter = chatIdPresenter;
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
        if (mChatIdPresenter != null) {
            mChatIdPresenter.onDestroy();
            mChatIdPresenter = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_chat_id_copy_tv:
                SystemUtils.copyTxt2Clipboard(this, mTokIdTv.getText().toString());
                ToastUtils.show(R.string.copy_success);
                break;
        }
    }

    @Override
    public void showTokId(CharSequence tokId) {
        mTokIdTv.setText(tokId);
    }

    @Override
    public void showTokIdImg(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                mTokIdIv.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showTitle(String title) {
        setPageTitle(title);
    }

    @Override
    public void showPrompt(String prompt) {

    }
}