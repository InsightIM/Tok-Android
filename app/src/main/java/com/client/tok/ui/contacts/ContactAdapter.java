package com.client.tok.ui.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.ContactInfo;
import com.client.tok.msg.UserStatus;
import com.client.tok.ui.adapter.BaseViewHolder;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.PortraitView;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactsHolder> {
    private String TAG = "RecentMsgAdapter2";
    private Context mContext;
    private List<ContactInfo> mContactsList = new ArrayList<>();
    private BaseViewHolder.OnItemClickListener mItemClickListener;
    private BaseViewHolder.OnItemLongClickListener mItemLongClickListener;
    private View mHeaderView;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    public ContactAdapter(Context context) {
        mContext = context;
    }

    public void updateDataList(List<ContactInfo> contactsList) {
        mContactsList.clear();
        mContactsList.addAll(contactsList);
        notifyDataSetChanged();
    }

    public void addHeader(View headerView) {
        mHeaderView = headerView;
        notifyItemChanged(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String selLetter) {
        if (mContactsList != null) {
            for (int i = 0; i < mContactsList.size(); i++) {
                String sortStr = mContactsList.get(i).getFirstLetter();
                if (sortStr.equals(selLetter)) {
                    return i + getHeaderCount();
                }
            }
        }
        return -1;
    }

    public int getHeaderCount() {
        return mHeaderView == null ? 0 : 1;
    }

    @NonNull
    @Override
    public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ContactsHolder(mHeaderView);
        }
        View convertView = ViewUtil.inflateViewById(mContext, R.layout.item_contact_list);
        return new ContactsHolder(convertView);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(final ContactsHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            int realPosition = holder.getAdapterPosition() - getHeaderCount();
            ContactInfo item = mContactsList.get(realPosition);
            if (realPosition == 0 || !mContactsList.get(realPosition - 1)
                .getFirstLetter()
                .equals(item.getFirstLetter())) {
                holder.firstLetter.setVisibility(View.VISIBLE);
                holder.firstLetter.setText(item.getFirstLetter());
            } else {
                holder.firstLetter.setVisibility(View.GONE);
            }

            holder.lineStatus.setImageResource(
                UserStatus.statusDrawable(item.isOnline(), item.getFriendStatusAsToxUserStatus()));
            holder.portraitView.setFriendText(item.getKey().toString(), item.getDisplayName());
            holder.name.setText(item.getDisplayName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, holder.getAdapterPosition());
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemLongClickListener != null) {
                        mItemLongClickListener.onItemLongClick(v, holder.getAdapterPosition());
                    }
                    return true;
                }
            });
        } else if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return mContactsList.size() + getHeaderCount();
    }

    public void setItemClickListener(BaseViewHolder.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(
        BaseViewHolder.OnItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public class ContactsHolder extends RecyclerView.ViewHolder {
        TextView firstLetter;
        PortraitView portraitView;
        ImageView lineStatus;
        TextView name;

        public ContactsHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            firstLetter = itemView.findViewById(R.id.id_contact_first_letter_tv);
            lineStatus = itemView.findViewById(R.id.id_contact_line_status_iv);
            portraitView = itemView.findViewById(R.id.id_contact_portrait_iv);
            name = itemView.findViewById(R.id.id_contact_name_tv);
        }
    }
}
