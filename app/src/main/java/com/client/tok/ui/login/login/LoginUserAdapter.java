package com.client.tok.ui.login.login;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.UserInfo;
import com.client.tok.utils.ViewUtil;
import java.util.List;

public class LoginUserAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserInfo> mList;

    public LoginUserAdapter(Context context, List<UserInfo> users) {
        this.mContext = context;
        this.mList = users;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder cache;
        if (convertView == null) {
            cache = new ViewHolder();
            convertView = ViewUtil.inflateViewById(mContext, R.layout.item_login_user);
            cache.name = convertView.findViewById(R.id.id_login_user_tv);
            convertView.setTag(cache);
        } else {
            cache = (ViewHolder) convertView.getTag();
        }
        cache.name.setText(mList.get(position).getProfileName());
        return convertView;
    }

    private final class ViewHolder {
        public TextView name;
    }
}
