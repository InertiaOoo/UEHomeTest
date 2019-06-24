package com.ooo.deemo.uehometest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Author by Deemo, Date on 2019/5/7.
 * Have a good day
 */
public class MyDialog extends AlertDialog {

    private Button ok_bt;

    private ImageView iv_back;

    public ListView lv_show;

    private TextView tv_title;

    private Context mContext;

    private View mView;

    private SendData mData;

    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    private AdapterView.OnItemClickListener mOnItemClickListener;


    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });


        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });
    }


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

    public interface SendData {
        void sendviewdata(String m);
    }


    public interface OnItemClickListener {


        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }


    public MyDialog(Context context) {
        super(context);
        this.mContext = context;

        initView();
        initEvent();
    }

    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.mydialog_layout, null);
        lv_show = (ListView) mView.findViewById(R.id.dialog_lv);

        iv_back = mView.findViewById(R.id.dialog_iv_cancel);

        ok_bt = mView.findViewById(R.id.dialog_ok);

        tv_title = mView.findViewById(R.id.dialog_title);


    }


    public void setTitle(String s) {

        tv_title.setText(s);
    }


    public void setItemsCanFocus(boolean b) {
        lv_show.setItemsCanFocus(b);
    }

    public void setChoiceMode(int choiceMode) {
        lv_show.setChoiceMode(choiceMode);
    }


    public void setAdapter(Adapter adapter) {

        lv_show.setAdapter((ListAdapter) adapter);

    }

    public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mView);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setCanceledOnTouchOutside(false);
        wl.x = 0;
        wl.y = 0;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);


    }
}

