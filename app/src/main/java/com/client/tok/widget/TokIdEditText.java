package com.client.tok.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;

public class TokIdEditText extends AppCompatEditText {
    private String TAG = "TokIdEditText";

    public TokIdEditText(Context context) {
        super(context);//use this(context,null) has problem, can't show soft keyboard
        this.addTextChangedListener(new PkTextWatcher());
    }

    public TokIdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(new PkTextWatcher());
    }

    public TokIdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addTextChangedListener(new PkTextWatcher());
    }

    //@Override
    //public boolean onTextContextMenuItem(int id) {
    //    if (id == android.R.id.paste) {
    //        onPast();
    //        return true;
    //    } else {
    //        return super.onTextContextMenuItem(id);
    //    }
    //}

    //private void onPast() {
    //    if (this.getText().toString().length() == 0) {
    //        CharSequence content = SystemUtils.getLastClipContent(this.getContext());
    //        if (content != null) {
    //            String result = PkUtils.getAddressFromContent(String.valueOf(content));
    //            this.setText(result);
    //            this.setSelection(result.length());
    //        }
    //    } else {
    //        super.onTextContextMenuItem(android.R.id.paste);
    //    }
    //}

    private class PkTextWatcher implements TextWatcher {
        private boolean empty = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            empty = StringUtils.isEmpty(s);
            LogUtil.i(TAG, "beforeTextChanged  empty:" + empty);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtil.i(TAG, "onTextChanged  empty:" + empty);
            if (empty && count > GlobalParams.ADDRESS_LENGTH) {
                String content = getText() == null ? null : getText().toString();
                if (content != null) {
                    String result = PkUtils.getAddressFromContent(String.valueOf(content));
                    setText(result);
                    setSelection(result.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
