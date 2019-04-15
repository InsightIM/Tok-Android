package com.client.tok.ui.profileedit;

import android.content.Intent;

public class ProfileEditContract {
    public interface IProfileEditView {
        void setPresenter(IProfileEditPresenter presenter);

        Intent getDataIntent();

        void showSelfName(String userName);

        void showFriendName(String aliasName);

        void showSelfSignature(String selfSignature);

        void success(int strId);

        void closeView();
    }

    public interface IProfileEditPresenter {
        void save(String content);
    }
}
