package com.client.tok.ui.profileedit;

import android.content.Intent;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.UserInfo;
import com.client.tok.db.DBConstants;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.db.repository.UserRepository;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxStatusMessage;

public class ProfileEditPresenter implements ProfileEditContract.IProfileEditPresenter {
    public static final int EDIT_NAME = 1;
    public static final int EDIT_SIGNATURE = 2;
    private ProfileEditContract.IProfileEditView mProfileEditView;
    private boolean mIsMine;
    private String mKey;
    private int mWhatToDo;
    private UserRepository mUserRepo = State.userRepo();
    private InfoRepository mInfoRepo = State.infoRepo();

    public ProfileEditPresenter(ProfileEditContract.IProfileEditView iProfileEditView) {
        this.mProfileEditView = iProfileEditView;
        mProfileEditView.setPresenter(this);
        start();
    }

    private void start() {
        Intent intent = mProfileEditView.getDataIntent();
        mKey = intent.getStringExtra(IntentConstants.PK);
        mWhatToDo = intent.getIntExtra(IntentConstants.WHAT_TODO, EDIT_NAME);
        mIsMine = mKey.equals(ToxManager.getManager().toxBase.getSelfKey().getKey());

        String name = "";
        String signature = null;
        if (mIsMine) {
            UserInfo userInfo = mUserRepo.getActiveUserDetails();
            name = userInfo.getNickname().toString();
            signature = new String(userInfo.getStatusMessage().value);
        } else {
            ContactInfo friendInfo = mInfoRepo.getFriendInfo(mKey);
            if (friendInfo != null) {
                name = friendInfo.getDisplayName();
            }
        }

        switch (mWhatToDo) {
            case EDIT_NAME:
                if (mIsMine) {
                    mProfileEditView.showSelfName(name);
                } else {
                    mProfileEditView.showFriendName(name);
                }
                break;
            case EDIT_SIGNATURE:
                mProfileEditView.showSelfSignature(signature);
                break;
        }
    }

    @Override
    public void save(String content) {
        switch (mWhatToDo) {
            case EDIT_NAME:
                if (mIsMine) {
                    if (StringUtils.isEmpty(content)) {
                        //mine nick name default "Tok user"
                        content = StringUtils.getTextFromResId(R.string.tok_user);
                    }
                    mUserRepo.updateActiveUserDetail(DBConstants.COLUMN_NICK_NAME, content);
                    ToxManager.getManager().toxBase.setName(
                        ToxNickname.unsafeFromValue(content.getBytes()));
                } else {
                    mInfoRepo.updateAlias(mKey, content);
                }
                break;
            case EDIT_SIGNATURE:
                mUserRepo.updateActiveUserDetail(DBConstants.COLUMN_STATUS_MESSAGE, content);
                ToxManager.getManager().toxBase.setSignature(
                    ToxStatusMessage.unsafeFromValue(content.getBytes()));
                break;
        }
        mProfileEditView.success(R.string.successful);
        mProfileEditView.closeView();
    }
}
