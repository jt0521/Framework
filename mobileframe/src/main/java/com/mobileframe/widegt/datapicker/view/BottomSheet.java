package com.mobileframe.widegt.datapicker.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.mobileframe.R;

public class BottomSheet extends Dialog implements DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener, View.OnClickListener {

    private View mContentView;// dialog content view
    private String mTitleText;
    private Button mLeftBtn;
    private Button mRightBtn;
    private TextView mTitleTv;
    private View.OnClickListener mLeftBtnClickListener;
    private View.OnClickListener mRightBtnClickListener;

    private OnDialogCloseListener mOnDismissListener;

    private View mTitleLayout;
    private View mDivideLineLayout;
    private TextView mContentTv;

    public BottomSheet(Context context) {
        super(context, R.style.style_data_picker_dialog);
        init();
    }

    public BottomSheet(Context context, int themeResId) {
        super(context, R.style.style_data_picker_dialog);
        init();
    }

    protected BottomSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.bottom_sheet_dialog);
        dialogWindow.setWindowAnimations(R.style.style_dialog_window_anim);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);

        mTitleLayout = dialogWindow.findViewById(R.id.title);
        mDivideLineLayout = dialogWindow.findViewById(R.id.divide_line);
        mLeftBtn = (Button) dialogWindow.findViewById(R.id.left_btn);
        mRightBtn = (Button) dialogWindow.findViewById(R.id.right_btn);
        mContentTv = dialogWindow.findViewById(R.id.content_tv);
        mTitleTv = dialogWindow.findViewById(R.id.middle_txt);

        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

        setOnCancelListener(this);
        setOnDismissListener(this);
    }

    public void setTitleVisibility(int visibility) {
        if (mTitleLayout != null) {
            mTitleLayout.setVisibility(visibility);
        }
        if (mDivideLineLayout != null) {
            mDivideLineLayout.setVisibility(visibility);
        }
    }

    public void setContent(View content) {
        ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content);
    }

    public void setContent(int contentId) {
        View content = getLayoutInflater().inflate(contentId, null);
        ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content);
    }

    public void setContent(View content, FrameLayout.LayoutParams params) {
        if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content, params);
        } else {
            ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content);
        }
    }

    public void setTitleBackground(@ColorRes int color) {
        if (mTitleLayout != null) {
            mTitleLayout.setBackgroundColor(getContext().getResources().getColor(color));
        }
    }

    public void setMiddleText(String text) {
        mTitleTv.setText(text);
    }

    public void setContentText(String content) {
        mContentTv.setText(content);
        int paddingTop = getContext().getResources().getDimensionPixelOffset(R.dimen.px28);
        mLeftBtn.setPadding(mLeftBtn.getPaddingLeft(), paddingTop, mLeftBtn.getPaddingRight(), 0);
        mRightBtn.setPadding(mRightBtn.getPaddingLeft(), paddingTop, mRightBtn.getPaddingRight(), 0);
        mTitleTv.setPadding(0, paddingTop, 0, 0);
        mContentTv.setVisibility(View.VISIBLE);
    }

    public void setLeftBtnVisibility(int visibility) {
        mLeftBtn.setVisibility(visibility);
    }

    public void setRightBtnVisibility(int visibility) {
        mRightBtn.setVisibility(visibility);
    }

    public void setLeftBtnText(String text) {
        mLeftBtn.setText(text);
    }

    public void setRightBtnText(String text) {
        mRightBtn.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_btn) {
            dismiss();
            if (mLeftBtnClickListener != null) {
                mLeftBtnClickListener.onClick(v);
            }
        } else if (v.getId() == R.id.right_btn) {
            dismiss();
            if (mRightBtnClickListener != null) {
                mRightBtnClickListener.onClick(v);
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onCancel();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    public void setLeftBtnClickListener(View.OnClickListener listener) {
        mLeftBtnClickListener = listener;
    }

    public void setRightBtnClickListener(View.OnClickListener listener) {
        mRightBtnClickListener = listener;
    }

    public void setOnDismissListener(OnDialogCloseListener listener) {
        mOnDismissListener = listener;
    }

    public interface OnDialogCloseListener {
        public void onCancel();

        public void onDismiss();
    }

    public void setLeftBtnTextColor(@ColorRes int textColor) {
        mLeftBtn.setTextColor(ContextCompat.getColor(getContext(),textColor));
    }

    public void setRightBtnTextColor(@ColorRes int textColor) {
        mRightBtn.setTextColor(ContextCompat.getColor(getContext(),textColor));
    }

    public Button getLeftBtn() {
        return mLeftBtn;
    }
}
