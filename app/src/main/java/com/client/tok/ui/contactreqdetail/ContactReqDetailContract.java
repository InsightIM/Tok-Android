package com.client.tok.ui.contactreqdetail;

import com.client.tok.bean.FriendRequest;
import com.client.tok.ui.basecontract.BaseContract;

public class ContactReqDetailContract {
    public interface IContactReqDetailView
        extends BaseContract.IBaseView<IContactReqDetailPresenter> {
        void showContactDetail(FriendRequest friendRequest);
    }

    public interface IContactReqDetailPresenter extends BaseContract.IBasePresenter {
        void acceptFriend();

        void refuseFriend();

        void onDestroy();
    }
}
