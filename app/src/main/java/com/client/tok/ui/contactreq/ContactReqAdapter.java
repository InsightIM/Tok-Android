package com.client.tok.ui.contactreq;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.FriendRequest;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.PortraitView;
import java.util.List;

public class ContactReqAdapter extends BaseAdapter {
    private Context mContext;
    private List<FriendRequest> mContactReqList;

    public ContactReqAdapter(Context context, List<FriendRequest> contactReqList) {
        mContext = context;
        mContactReqList = contactReqList;
    }

    public void setContactList(List<FriendRequest> contactReqList) {
        mContactReqList = contactReqList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mContactReqList != null ? mContactReqList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mContactReqList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = ViewUtil.inflateViewById(mContext, R.layout.item_contact_request_list);

            viewHolder.portraitView = convertView.findViewById(R.id.id_contact_portrait_iv);
            viewHolder.name = convertView.findViewById(R.id.id_contact_name_tv);
            viewHolder.msg = convertView.findViewById(R.id.id_contact_msg_tv);
            viewHolder.acceptTv = convertView.findViewById(R.id.id_contact_accept_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FriendRequest item = mContactReqList.get(position);
        // viewHolder.portraitView.setText(item.getDisplayName());
        viewHolder.portraitView.setFriendText(item.getRequestKey().getKey(), item.getRequestMessage());
        viewHolder.name.setText(item.getRequestKey().getKey());
        viewHolder.msg.setText(item.getRequestMessage());
        return convertView;
    }

    private static class ViewHolder {
        PortraitView portraitView;
        TextView name;
        TextView msg;
        TextView acceptTv;
    }
}
