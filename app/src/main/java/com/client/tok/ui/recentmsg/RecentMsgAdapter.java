package com.client.tok.ui.recentmsg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.ConversationItem;
import com.client.tok.constant.ContactType;
import com.client.tok.constant.MessageType;
import com.client.tok.media.MediaUtil;
import com.client.tok.msg.UserStatus;
import com.client.tok.tox.State;
import com.client.tok.utils.ImageUtils;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ViewUtil;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.UnreadMsgView;
import im.tox.utils.TimeUtil;
import java.util.ArrayList;
import java.util.List;

public class RecentMsgAdapter extends RecyclerView.Adapter<RecentMsgAdapter.RecentMsgHolder> {
    private String TAG = "RecentMsgAdapter";
    private Context mContext;
    private List<ConversationItem> mConversationList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public RecentMsgAdapter(Context context) {
        mContext = context;
    }

    public void updateDataList(List<ConversationItem> contactMsgList) {
        mConversationList.clear();
        mConversationList.addAll(contactMsgList);
    }

    @NonNull
    @Override
    public RecentMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = ViewUtil.inflateViewById(mContext, R.layout.item_recent_msg_list);
        return new RecentMsgHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentMsgHolder holder, int position) {
        ConversationItem item = mConversationList.get(position);
        int contactType = item.getContactType();
        if (contactType == ContactType.GROUP.getType()) {
            holder.portraitView.setPortraitResId(R.drawable.avatar_group_defulat);
            holder.lineStatus.setVisibility(View.GONE);
        } else if (contactType == ContactType.FRIEND.getType()) {
            holder.portraitView.setFriendText(item.cKey, item.getDisplayName());
            holder.lineStatus.setVisibility(View.VISIBLE);
            holder.lineStatus.setImageResource(UserStatus.statusDrawable(item.isOnline(),
                UserStatus.getUserStatusFromString(item.status)));
        }
        holder.mute.setVisibility(item.isMute() ? View.VISIBLE : View.GONE);
        holder.name.setText(item.getDisplayName());
        holder.unReadMsgIv.setUnreadNum(State.infoRepo().totalUnreadCount(item.cKey));

        holder.lastMsg.setText(formatLastMsg(item.lastMsg, item.msgType));
        holder.lastMsgTime.setText(TimeUtil.getTimePoint(item.lastMsgTime));
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
    }

    private CharSequence formatLastMsg(String msg, int msgType) {
        if (msg != null) {
            if (MessageType.isFileTransfer(MessageType.fromValue(msgType))) {
                int resId = 0;
                if (ImageUtils.isImgFile(msg)) {
                    resId = R.string.type_photo;
                } else if (MediaUtil.isAudio(msg)) {
                    resId = R.string.type_audio;
                } else if (MediaUtil.isVideo(msg)) {
                    resId = R.string.type_video;
                } else {
                    resId = R.string.type_file;
                }
                return StringUtils.getTextFromResId(resId);
            } else {
                if (msgType == MessageType.DRAFT.getType()) {
                    return StringUtils.formatHtmlTxFromResId(R.string.type_draft, msg);
                }
            }
        }
        return msg;
    }

    @Override
    public int getItemCount() {
        return mConversationList == null ? 0 : mConversationList.size();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public class RecentMsgHolder extends RecyclerView.ViewHolder {
        PortraitView portraitView;
        ImageView lineStatus;
        TextView name;
        ImageView mute;
        TextView lastMsg;
        TextView lastMsgTime;
        UnreadMsgView unReadMsgIv;

        public RecentMsgHolder(View itemView) {
            super(itemView);
            portraitView = itemView.findViewById(R.id.id_contact_portrait_iv);
            portraitView.setClickable(false);
            lineStatus = itemView.findViewById(R.id.id_contact_line_status_iv);
            name = itemView.findViewById(R.id.id_contact_name_tv);
            lastMsg = itemView.findViewById(R.id.id_contact_last_msg_tv);
            mute = itemView.findViewById(R.id.id_contact_mute_iv);
            lastMsgTime = itemView.findViewById(R.id.id_contact_last_msg_time_tv);
            unReadMsgIv = itemView.findViewById(R.id.id_contact_unread_msg_iv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }
}
