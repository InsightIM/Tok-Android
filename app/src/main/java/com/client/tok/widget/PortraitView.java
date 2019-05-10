package com.client.tok.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.client.tok.R;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.rx.RxBus;
import com.client.tok.rx.event.PortraitEvent;
import com.client.tok.tox.ToxManager;
import com.client.tok.ui.imgzoom.ImgZoomManager;
import com.client.tok.utils.ImageLoadUtils;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.io.File;

public class PortraitView extends FrameLayout {
    private String TAG = "PortraitView";
    private Context mContext;
    private TextPortraitView mTextPortraitView;
    private ImageView mImgPortraitView;
    //if is myself，null/selfKey
    private String mGroupNumber;
    private String mFriendKey;
    private CharSequence mFriendName;
    private String mAvatarFileName;
    private boolean mClickEnterInfoDetail = true;
    private boolean mHasPortraitFile = false;

    private Disposable mDisposable;

    public PortraitView(Context context) {
        this(context, null);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View rootView = ViewUtil.inflateViewById(mContext, R.layout.view_portrait);
        this.addView(rootView);
        mTextPortraitView = findViewById(R.id.id_portrait_text_view);
        mImgPortraitView = findViewById(R.id.id_portrait_img_view);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickEnterInfoDetail) {
                    //if friend key is null,the avatar is mine(current user)
                    if (StringUtils.isEmpty(mFriendKey) || mFriendKey.equals(
                        ToxManager.getManager().toxBase.getSelfKey().toString())) {
                        PageJumpIn.jumpMyTokIdPage(mContext);
                    } else {
                        PageJumpIn.jumpFriendInfoPage(mContext, mGroupNumber, mFriendKey);
                    }
                } else {
                    if (mHasPortraitFile) {
                        ImgZoomManager.showSingleImg(mContext, v,
                            StorageUtil.getAvatarsFolder() + mAvatarFileName);
                    }
                }
            }
        });
    }

    private void listen() {
        stopListen();
        if (mDisposable == null) {
            mDisposable = RxBus.listen(PortraitEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PortraitEvent>() {
                    @Override
                    public void accept(PortraitEvent portraitEvent) throws Exception {
                        if (portraitEvent.getKey().equals(mFriendKey)) {
                            PortraitView.this.loadAvatar();
                        }
                    }
                });
        }
    }

    public void stopListen() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = null;
    }

    public void setText(CharSequence text) {
        setFriendText(null, ToxManager.getManager().toxBase.getSelfKey().getKey(), text);
    }

    public void setFriendText(String friendKey, CharSequence text) {
        setFriendText(null, friendKey, text);
    }

    public void setFriendText(String groupNumber, String friendKey, CharSequence text) {
        LogUtil.i(TAG, "friendKey:" + friendKey);
        mGroupNumber = groupNumber;
        mFriendKey = friendKey;
        mFriendName = text;
        loadAvatar();
        listen();
    }

    public void setAvatarId(int avatarId) {
        if (avatarId > 0) {
            mImgPortraitView.setVisibility(View.VISIBLE);
            ImageLoadUtils.loadRoundImg(mContext, avatarId, mImgPortraitView);
        }
    }

    private void loadAvatar() {
        mAvatarFileName = mFriendKey + ".png";
        final String portraitPath = StorageUtil.getAvatarsFolder() + mAvatarFileName;
        File file = new File(portraitPath);
        mHasPortraitFile = false;
        if (file.exists() && file.length() > 0) {
            showImg();
            ImageLoadUtils.loadRoundImg(mContext, portraitPath, mImgPortraitView,
                new ImageLoadUtils.LoadListener() {
                    @Override
                    public void onLoadFailed() {
                        LogUtil.i(TAG, "onLoadFail portraitPath:" + portraitPath);
                        showText();
                        mTextPortraitView.setFriendText(mFriendKey, mFriendName);
                    }

                    @Override
                    public void onResourceReady() {
                        LogUtil.i(TAG, "onResourceReady:" + portraitPath);
                        mHasPortraitFile = true;
                    }
                });
        } else {
            showText();
            mTextPortraitView.setFriendText(mFriendKey, mFriendName);
        }
    }

    public void setClickEnterDetail(boolean isEnterDetail) {
        mClickEnterInfoDetail = isEnterDetail;
    }

    public void setPortraitResId(int imgResId) {
        LogUtil.i(TAG, "setImgResId:");
        if (imgResId > 0) {
            showImg();
            ImageLoadUtils.loadRoundImg(mContext, imgResId, mImgPortraitView);
        }
    }

    private void showImg() {
        mTextPortraitView.setVisibility(View.GONE);
        mImgPortraitView.setVisibility(View.VISIBLE);
    }

    private void showText() {
        mTextPortraitView.setVisibility(View.VISIBLE);
        mImgPortraitView.setVisibility(View.GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.i(TAG, "onDetachedFromWindow:");
        stopListen();
    }
}
