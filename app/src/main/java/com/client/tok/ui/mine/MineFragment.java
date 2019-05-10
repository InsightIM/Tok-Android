package com.client.tok.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseFragment;
import com.client.tok.bean.UserInfo;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.utils.StringUtils;
import com.client.tok.widget.ItemInfoView;
import com.client.tok.widget.PortraitView;

public class MineFragment extends BaseFragment
    implements MineContract.IMineView, View.OnClickListener {
    private View mMineInfoLayout;
    private PortraitView mAvatarIv;
    private TextView mNickNameTv;
    private TextView mUsernameTv;
    private TextView mSignatureTv;
    private ItemInfoView mTokIdTv;
    private ItemInfoView mFindFriendBotIIV;
    private ItemInfoView mOfflineBotIIV;
    private ItemInfoView mUserStatusTv;

    private View mSafeLayout;
    private View mAboutLayout;
    private View mSettingLayout;

    private MineContract.IMinePresenter mMinePresenter;

    public static MineFragment getInstance() {
        MineFragment fra = new MineFragment();
        Bundle bundle = new Bundle();
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        mMineInfoLayout = $(view, R.id.id_mine_me_layout);
        mMineInfoLayout.setOnClickListener(this);
        mAvatarIv = $(view, R.id.id_mine_avatar_civ);
        mNickNameTv = $(view, R.id.id_mine_nick_name_tv);
        mUsernameTv = $(view, R.id.id_mine_user_name_tv);
        mTokIdTv = $(view, R.id.id_mine_tok_id_iiv);
        mTokIdTv.setOnClickListener(this);
        mFindFriendBotIIV = $(view, R.id.id_mine_find_friend_iiv);
        mFindFriendBotIIV.setOnClickListener(this);
        mOfflineBotIIV = $(view, R.id.id_mine_offline_iiv);
        mOfflineBotIIV.setOnClickListener(this);

        mUserStatusTv = $(view, R.id.id_mine_status_iiv);
        mSignatureTv = $(view, R.id.id_mine_signature_tv);
        //
        mSafeLayout = $(view, R.id.id_mine_safe_layout);
        mSafeLayout.setOnClickListener(this);
        mAboutLayout = $(view, R.id.id_mine_about_layout);
        mAboutLayout.setOnClickListener(this);
        mSettingLayout = $(view, R.id.id_mine_setting_layout);
        mSettingLayout.setOnClickListener(this);
        new MinePresenter(this);
        return view;
    }

    @Override
    public void setPresenter(MineContract.IMinePresenter iMinePresenter) {
        mMinePresenter = iMinePresenter;
    }

    @Override
    public void showUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            //Option<File> fileOption = AVATAR.getAvatarFile(userInfo.avatarName(), getActivity());
            //BitmapManager.load(fileOption.get(), true);
            //BitmapManager.load(avatar, isAvatar = true).foreach(avatarView.setImageBitmap)
            mAvatarIv.setText(new String(userInfo.getNickname().value));
            mNickNameTv.setText(new String(userInfo.getNickname().value));
            mUsernameTv.setText(StringUtils.formatTxFromResId(R.string.user_name_prompt,
                userInfo.getToxMeName().getUserName()));

            String signature = new String(userInfo.getStatusMessage().value);
            if (StringUtils.isEmpty(signature)) {
                mSignatureTv.setVisibility(View.GONE);
            } else {
                mSignatureTv.setVisibility(View.VISIBLE);
                mSignatureTv.setText(signature);
            }
        }
    }

    @Override
    public void showStatus(String status) {
        int statusResId = 0;
        switch (status) {
            case GlobalParams.ON_LINE:
                statusResId = R.string.on_line;
                break;
            case GlobalParams.OFF_LINE:
                statusResId = R.string.off_line;
                break;
            case GlobalParams.AWAY:
                statusResId = R.string.away;
                break;
            case GlobalParams.BUSY:
                statusResId = R.string.busy;
                break;
        }
        mUserStatusTv.setContent(StringUtils.formatHtmlTxFromResId(statusResId));
    }

    @Override
    public void showFindFriendBotNew(String content, int style, int bg) {
        mFindFriendBotIIV.setContent(content, style, bg);
    }

    @Override
    public void showOfflineBotNew(String content, int style, int bg) {
        mOfflineBotIIV.setContent(content, style, bg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMinePresenter != null) {
            mMinePresenter.onDestroy();
        }
    }

    @Override
    public void viewDestroy() {
        if (mMinePresenter != null) {
            mMinePresenter.onDestroy();
            mMinePresenter = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_mine_me_layout:
                PageJumpIn.jumpMyInfoPage(getActivity());
                break;
            case R.id.id_mine_tok_id_iiv:
                PageJumpIn.jumpMyTokIdPage(getActivity());
                break;
            case R.id.id_mine_find_friend_iiv:
                mMinePresenter.showFindFriendBot();
                break;
            case R.id.id_mine_offline_iiv:
                mMinePresenter.showOfflineBot();
                break;
            case R.id.id_mine_safe_layout:
                PageJumpIn.jumpSafePage(getActivity());
                break;
            case R.id.id_mine_about_layout:
                PageJumpIn.jumpAboutUsPage(this.getActivity());
                break;
            case R.id.id_mine_setting_layout:
                PageJumpIn.jumpSettingPage(getActivity());
                break;
        }
    }
}