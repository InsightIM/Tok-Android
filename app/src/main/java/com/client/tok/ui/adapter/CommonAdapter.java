package com.client.tok.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public abstract class CommonAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<Object> mDataList;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private int mLayoutId;

    public CommonAdapter(Context context, int layoutId, List dataList) {
        mContext = context;
        mLayoutId = layoutId;
        mDataList = dataList;
    }

    public void removeItem(int i) {
        if (i > 0) {
            mDataList.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void notify(List dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = BaseViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
        convert(holder, mDataList.get(position));
    }

    public void convert(BaseViewHolder holder, Object data) {
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
