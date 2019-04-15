package com.client.tok.ui.contactreq;

import android.arch.lifecycle.LifecycleOwner;
import com.client.tok.bean.FriendRequest;
import com.client.tok.ui.basecontract.BaseContract;
import java.util.List;

public class ContactReqContract {
    public interface IContactReqView
        extends BaseContract.IBaseView<IContactReqPresenter>, LifecycleOwner {
        void showContactReq(List<FriendRequest> friendReqList);
    }

    public interface IContactReqPresenter extends BaseContract.IBasePresenter {

        void onDestroy();
    }
}
