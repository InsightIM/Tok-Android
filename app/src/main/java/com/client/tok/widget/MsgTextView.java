package com.client.tok.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import com.client.tok.R;
import com.client.tok.constant.BotOrder;
import com.client.tok.rx.event.BotOrderEvent;
import com.client.tok.rx.RxBus;
import com.client.tok.pagejump.PageJumpOut;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PatternUtil;
import java.util.regex.Matcher;

/**
 * textview
 * 1.remove bottom line default
 * 2.long click and autoLink click conflict
 * 3.jump to my WebView
 */
public class MsgTextView extends android.support.v7.widget.AppCompatTextView {
    private String TAG = "MsgTextView";
    //is enable the bot order link
    private boolean enableOrderLink;
    private final int LINK_WEB = 1;
    private final int LINK_MAIL = 2;
    private final int LINK_TEL = 3;

    public MsgTextView(Context context) {
        super(context);
    }

    public MsgTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MsgTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CharSequence buildOrderLink(CharSequence content) {
        String c = content.toString();
        SpannableString sp = new SpannableString(c);
        if (enableOrderLink) {
            Matcher orderMatcher = PatternUtil.getOrderMatcher(c);
            while (orderMatcher.find()) {
                sp.setSpan(new OrderLinkSpan(c.substring(orderMatcher.start(), orderMatcher.end())),
                    orderMatcher.start(), orderMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        Matcher pkMatcher = PatternUtil.getPkMatcher(c);
        while (pkMatcher.find()) {
            sp.setSpan(new TokIdLinkSpan(c.substring(pkMatcher.start(), pkMatcher.end())),
                pkMatcher.start(), pkMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(sp);
        setMovementMethod(LinkMovementMethod.getInstance());
        return sp;
    }

    @Override
    public void setOnLongClickListener(final View.OnLongClickListener longClickListener) {
        super.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    MsgTextView.this.setTag(R.id.id_long_click_tag, true);
                    longClickListener.onLongClick(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void setEnableOrderLink(boolean enableOrderLink) {
        this.enableOrderLink = enableOrderLink;
    }

    public void setMsg(int txtId) {
        setText(txtId, BufferType.SPANNABLE);
        replaceAutoLinkSpan();
    }

    public void setMsg(CharSequence charSequence) {
        setText(buildOrderLink(charSequence), BufferType.SPANNABLE);
        replaceAutoLinkSpan();
    }

    private void replaceAutoLinkSpan() {
        Spannable spannable = (Spannable) this.getText();
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int linkType = -1;
            String url = span.getURL();
            int index = spannable.toString().indexOf(url);
            LogUtil.i(TAG, "url:" + url + "，index:" + index);
            if (url.startsWith("http://")) {
                if (index == -1) {
                    url = url.replaceFirst("http://", "");
                }
                linkType = LINK_WEB;
            } else if (url.startsWith("https://")) {
                if (index == -1) {
                    url = url.replaceFirst("https://", "");
                }
                linkType = LINK_WEB;
            } else if (url.startsWith("rtsp://")) {
                if (index == -1) {
                    url = url.replaceFirst("rtsp://", "");
                }
                linkType = LINK_WEB;
            } else if (url.contains("mailto")) {
                url = url.replace("mailto:", "");
                linkType = LINK_MAIL;
            } else if (url.contains("tel")) {
                url = url.replace("tel:", "");
                linkType = LINK_TEL;
            }
            index = spannable.toString().indexOf(url);
            int end = index + url.length();
            if (index != -1) {
                spannable.removeSpan(span);
                spannable.setSpan(new UrlLinkSpan(span.getURL(), linkType), index, end,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    public class TokIdLinkSpan extends BaseLinkSpan {
        private TokIdLinkSpan(String tokId) {
            super(tokId);
        }

        @Override
        public void onClick(View widget) {
            if (!hasLongClick(widget)) {
                LogUtil.i(TAG, "TokId:" + getUrl());
                RxBus.publish(new BotOrderEvent(BotOrder.ADD.getOrder(), getUrl()));
            }
        }
    }

    public class OrderLinkSpan extends BaseLinkSpan {
        private OrderLinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            if (!hasLongClick(widget)) {
                LogUtil.i(TAG, "orderLink:" + getUrl());
                RxBus.publish(new BotOrderEvent(getUrl(), MsgTextView.this.getText().toString()));
            }
        }
    }

    public class UrlLinkSpan extends BaseLinkSpan {
        private int linkType = -1;

        private UrlLinkSpan(String url, int linkType) {
            super(url);
            this.linkType = linkType;
        }

        @Override
        public void onClick(View widget) {
            if (!hasLongClick(widget)) {
                switch (linkType) {
                    case LINK_WEB:
                        PageJumpOut.jumpWebBrowser(getContext(), getUrl());
                        break;
                    case LINK_MAIL:
                        PageJumpOut.jumpEmail(getContext(), getUrl());
                        break;
                    case LINK_TEL:
                        PageJumpOut.jumpOpenDial(getContext(), getUrl());
                        break;
                }
            }
        }
    }

    public class BaseLinkSpan extends ClickableSpan {
        private String url;

        private BaseLinkSpan(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public void onClick(View widget) {

        }

        public boolean hasLongClick(View view) {
            if (view.getTag(R.id.id_long_click_tag) != null) {
                view.setTag(R.id.id_long_click_tag, null);
                return true;
            }
            return false;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.auto_link_color));
            ds.setUnderlineText(false);
        }
    }
}
