package com.client.tok.ui.chat2.holders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.client.tok.R;
import com.client.tok.bean.Message;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.rx.RxBus;
import com.client.tok.rx.event.ProgressEvent;
import com.client.tok.tox.State;
import com.client.tok.ui.chat2.Contract;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.SystemUtils;
import com.client.tok.utils.ToastUtils;
import com.client.tok.widget.MenuPopWindow;
import com.client.tok.widget.PortraitView;
import com.client.tok.widget.ProgressView;
import im.tox.utils.TimeUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;

public abstract class BaseMsgHolder extends RecyclerView.ViewHolder
    implements View.OnLongClickListener {
    private String TAG = "BaseMsgHolder";
    private Disposable mDisposable;
    private Disposable mDisposable2;
    private String mMsgId;
    protected Context mContext;
    protected Message mCurMsg;
    protected String mMenuType;
    protected Contract.IChatPresenter mPresenter;

    private MenuPopWindow.MenuClickListener mMenuListener = new MenuListener();

    public BaseMsgHolder(View itemView, Contract.IChatPresenter presenter) {
        super(itemView);
        mContext = itemView.getContext();
        mPresenter = presenter;
    }

    public void setMessage(Message curMsg, Message lastMsg) {
        this.mCurMsg = curMsg;
    }

    private String getSenderName() {
        String senderName = mPresenter.getSenderName(mCurMsg.getSenderKey().getKey());
        return !StringUtils.isEmpty(senderName) ? senderName : mCurMsg.getSenderName();
    }

    protected void showSenderName(TextView senderNameTv) {
        if (mPresenter.isShowSenderName()) {
            senderNameTv.setVisibility(View.VISIBLE);
            senderNameTv.setText(getSenderName());
        } else {
            senderNameTv.setVisibility(View.GONE);
        }
    }

    public void setPortrait(Message curMsg, PortraitView portraitView) {
        if (curMsg.isMine()) {
            portraitView.setText(getSenderName());
        } else {
            portraitView.setFriendText(curMsg.getKey().toString(), curMsg.getSenderKey().toString(),
                getSenderName());
        }
    }

    public void setTime(Message curMsg, Message lastMsg, TextView timeView) {
        if (isShowTime(curMsg, lastMsg)) {
            timeView.setVisibility(View.VISIBLE);
            timeView.setText(getTime(curMsg));
        } else {
            timeView.setVisibility(View.GONE);
        }
    }

    public void listen(String bindId) {
        if (mDisposable == null) {
            mDisposable = RxBus.listen(ProgressEvent.class)
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProgressEvent>() {
                    @Override
                    public void accept(ProgressEvent progressEvent) throws Exception {
                        if (progressEvent.getMsgId().equals(mMsgId)) {
                            BaseMsgHolder.this.chatLayout(progressEvent);
                        }
                    }
                });
        }
        this.mMsgId = bindId;
    }

    public void stopListen() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mMsgId = null;
        mDisposable = null;
    }

    public void chatLayout(ProgressEvent event) {

    }

    private boolean isShowTime(Message curMsg, Message lastMsg) {
        int msgInterval = 180;
        return lastMsg == null
            //|| !lastMsg.senderKey().equals(curMsg.senderKey())
            || ((curMsg.getTimestamp() - lastMsg.getTimestamp()) / 1000) > msgInterval;
    }

    private String getTime(Message msg) {
        return TimeUtil.getTimePoint(msg.getTimestamp());
    }

    void showProgressBar(Message msg, ProgressView progressView, View errView) {
        showProgressBar(msg, progressView, null, errView);
    }

    void showProgressBar(Message msg, ProgressView progressView, ImageView imgView, View errView) {
        if (msg != null && progressView != null) {
            if (msg.getSentStatus() == GlobalParams.SEND_ING) {
                if (msg.getMessageId() != -1) {
                    setProgress(msg, progressView);
                    if (imgView != null) {
                        imgView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                    }
                } else {
                    //FIXME this should be "Failed" - fix the DB bug
                    LogUtil.i(TAG, "send file failed");
                    progressView.setSuccess();
                    if (imgView != null) {
                        imgView.clearColorFilter();
                    }
                }
            } else {
                if (msg.getMessageId() != -1) {
                    if (msg.isMine()) {
                        LogUtil.i(TAG, "the file is mine");
                    } else {
                        //todo: no need confirm,receive
                        //State.transferManager()
                        //    .acceptFile((FriendKey) msg.key(), msg.messageId(),
                        //        TokApplication.getInstance());
                    }
                    if (imgView != null) {
                        imgView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                    }
                } else {
                    //fileHolder.setProgressText(R.string.file_rejected)
                    LogUtil.i(TAG, "the file is success");
                    progressView.setSuccess();
                    if (imgView != null) {
                        imgView.clearColorFilter();
                    }
                }
            }
        }
    }

    //TODO has problem
    //public void showProgressBar(Message msg, ProgressView progressView, View errView) {
    //    LogUtil.i(TAG, "showProgressBar....");
    //    if (msg != null && progressView != null) {
    //        if (msg.isMine()) {
    //            if (msg.sentStatus() == GlobalParams.SEND_ING) {
    //                setProgress(msg, progressView);
    //            } else if (msg.sentStatus() == GlobalParams.SEND_FAIL) {
    //                progressView.setVisibility(View.GONE);
    //                errView.setVisibility(View.VISIBLE);
    //            }
    //        } else {
    //            if (msg.receiveStatus() == GlobalParams.RECEIVE_ING) {
    //                setProgress(msg, progressView);
    //            } else if (msg.sentStatus() == GlobalParams.RECEIVE_FAIL) {
    //                progressView.setVisibility(View.GONE);
    //                errView.setVisibility(View.VISIBLE);
    //            }
    //        }
    //    }
    //}

    private void setProgress(final Message msg, final ProgressView progressView) {
        if (mDisposable2 == null || mDisposable2.isDisposed()) {
            mDisposable2 = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        long curPosition = State.transfers.getProgress(msg.getId());
                        LogUtil.i(TAG, "progress interval size:"
                            + msg.getSize()
                            + ",curPosition:"
                            + curPosition);

                        progressView.setProgress(msg.getSize(),
                            State.transfers.getProgress(msg.getId()));
                        if (msg.getSize() <= curPosition) {
                            mDisposable2.dispose();
                            mDisposable2 = null;
                        }
                    }
                });
        }
    }

    void addLongClickListener(View view) {
        if (view != null) {
            view.setOnLongClickListener(this);
        }
    }

    @Override
    public boolean onLongClick(View anchorView) {
        if (!StringUtils.isEmpty(mMenuType)) {
            PopupWindow popupWindow = MenuPopWindow.getMenuView(mContext, mMenuType, mMenuListener);
            int windowPos[] =
                MenuPopWindow.calculatePopWindowPos(anchorView, MenuPopWindow.mContentView);
            popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0],
                windowPos[1]);
        }
        return true;
    }

    public class MenuListener extends MenuPopWindow.MenuClickListener {
        @Override
        public void onCopy() {
            SystemUtils.copyTxt2Clipboard(mContext, mCurMsg.getMessage());
            ToastUtils.show(R.string.copy_success);
        }

        @Override
        public void onDel() {
            //InfoRepository infoRepo = State.infoRepo();
            //infoRepo.deleteMessage(mCurMsg.getId());
            if (mPresenter != null) {
                mPresenter.del(mCurMsg.getId());
            }
        }

        public void onSave() {
            if (mPresenter != null) {
                mPresenter.save(mCurMsg.getMessage());
            }
        }

        @Override
        public void onFailed() {

        }
    }
}
