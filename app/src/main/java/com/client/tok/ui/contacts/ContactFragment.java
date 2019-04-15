package com.client.tok.ui.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.base.BaseFragment;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.pagejump.PageJumpIn;
import com.client.tok.ui.adapter.BaseViewHolder;
import com.client.tok.utils.LogUtil;
import com.client.tok.widget.AddFriendView;
import com.client.tok.widget.LetterSortView;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.WrapContentLinearLayoutManager;
import java.util.List;

public class ContactFragment extends BaseFragment
    implements ContactContract.IContactsView, BaseViewHolder.OnItemClickListener,
    BaseViewHolder.OnItemLongClickListener {
    private String TAG = "ContactsFragment";
    private ContactContract.IContactsPresenter mContactsPresenter;
    private RecyclerView mContactsLv;
    private LetterSortView mSortView;
    private TextView mSortSelTv;//排列字母选中的
    private LinearLayoutManager mLayoutManager;
    private ContactAdapter mContactsAdapter;
    private AddFriendView mAddFriendView;
    private List<ContactsInfo> mContactList;

    public static ContactFragment getInstance() {
        ContactFragment fra = new ContactFragment();
        Bundle bundle = new Bundle();
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsLv = rootView.findViewById(R.id.id_contracts_lv);
        mSortView = rootView.findViewById(R.id.id_letter_sort_view);
        mSortSelTv = rootView.findViewById(R.id.id_letter_sort_sel_tv);
        mAddFriendView = new AddFriendView(getContext());

        mLayoutManager = new WrapContentLinearLayoutManager(this.getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactsLv.setLayoutManager(mLayoutManager);
        mContactsAdapter = new ContactAdapter(getActivity());
        mContactsAdapter.addHeader(mAddFriendView);
        mContactsLv.setAdapter(mContactsAdapter);
        mContactsAdapter.setItemClickListener(this);
        mContactsAdapter.setItemLongClickListener(this);

        initListener();
        new ContactPresenter(this);
        return rootView;
    }

    private void initListener() {
        mSortView.setTextView(mSortSelTv);
        mSortView.setOnTouchingLetterChangedListener((String letter) -> {
            if (mContactsAdapter != null) {
                int position = mContactsAdapter.getPositionForSection(letter);
                LogUtil.i(TAG, "smooth position:" + position);
                if (position != -1) {
                    mLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    @Override
    public void setPresenter(ContactContract.IContactsPresenter iContactsPresenter) {
        mContactsPresenter = iContactsPresenter;
    }

    @Override
    public Activity getCurActivity() {
        return this.getActivity();
    }

    @Override
    public void showContacts(DiffUtil.DiffResult result, final List<ContactsInfo> contactList) {
        mContactList = contactList;
        mContactsAdapter.updateDataList(contactList);
        //result.dispatchUpdatesTo(mContactsAdapter);
    }

    @Override
    public void showNewContactTag() {
        mAddFriendView.showNewContactRequestTag();
    }

    @Override
    public void hideNewContactTag() {
        mAddFriendView.hideNewContactRequestTag();
    }

    @Override
    public void setLetterSortVisible(boolean isVisible) {
        mSortView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewDestroy();
    }

    @Override
    public void viewDestroy() {
        if (mContactsPresenter != null) {
            mContactsPresenter.onDestroy();
            mContactsPresenter = null;
        }
    }

    private ContactsInfo getFriendInfoByPosition(int position) {
        //考虑到header
        int realPosition = position - mContactsAdapter.getHeaderCount();
        return mContactList.get(realPosition);
    }

    @Override
    public void onItemClick(View v, int position) {
        ContactsInfo friendInfo = getFriendInfoByPosition(position);
        PageJumpIn.jumpFriendInfoPage(getActivity(), null, friendInfo.getKey().toString());
    }

    @Override
    public void onItemLongClick(View view, int position) {
        PopupWindow popupWindow =
            MenuPopWindow.getMenuView(getActivity(), MenuPopWindow.TYPE_CONTACT,
                new MenuPopWindow.MenuClickListener() {
                    @Override
                    public void onDel() {
                        ContactsInfo friendInfo = getFriendInfoByPosition(position);
                        mContactsPresenter.delContact(friendInfo.getKey());
                    }
                });
        int windowPos[] = MenuPopWindow.calculatePopWindowPos(view, view);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }
}