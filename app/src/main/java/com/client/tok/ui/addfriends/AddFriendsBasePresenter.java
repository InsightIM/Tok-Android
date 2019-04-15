package com.client.tok.ui.addfriends;

public class AddFriendsBasePresenter {
    private AddFriendsModel mAddFriendModel;

    public AddFriendsBasePresenter() {
        mAddFriendModel = new AddFriendsModel();
    }

    public boolean addFriendById(String tokId, String alias, String msg) {
        return mAddFriendModel.addFriendById(tokId, alias, msg);
    }

    public int checkIdValid(String tokId) {
        return mAddFriendModel.checkIdValid(tokId);
    }
}
