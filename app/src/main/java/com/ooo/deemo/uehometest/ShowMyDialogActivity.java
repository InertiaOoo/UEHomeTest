package com.ooo.deemo.uehometest;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowMyDialogActivity extends AppCompatActivity {

    private Button bt_show;

    private Button bt_show2;

    private MyDialog myDialog;

    SimpleAdapter adapter;

    private static Boolean[] bl = {true, false, false, false, false, false, false, false, false};

    String[] mlistText = {"全选", "WIFI", "USB", "SD", "蓝牙", "本地音乐", "在线音乐", "有声内容", "本地蓝牙"};

    ArrayList<Map<String, Object>> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_dialog);

        bt_show =findViewById(R.id.bt_show);
bt_show2 = findViewById(R.id.bt_show2);

        int length = mlistText.length;
        for (int i = 0; i < length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("text", mlistText[i]);
            mData.add(item);
        }

        bt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateMyDialog();

            }
        });


        bt_show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMyDialog2();
            }
        });




    }



    private void CreateMyDialog2() {

        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(ShowMyDialogActivity.this,R.style.MyDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alterDiaglog.setView(R.layout.mydialog_layout);//加载进去
        }
        AlertDialog dialog = alterDiaglog.create();

        dialog.show();


    }



    private void CreateMyDialog() {

        myDialog = new MyDialog(this);

        adapter = new SetSimpleAdapter(ShowMyDialogActivity.this, mData, R.layout.check, new String[]{"text"},
                new int[]{R.id.X_item_text});
        // 给listview加入适配器
        myDialog.setAdapter(adapter);
        myDialog.setItemsCanFocus(false);
        myDialog.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myDialog.setOnItemClickListener(new ItemOnClick());

        myDialog.setYesOnclickListener("确定",new MyDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
myDialog.dismiss();
            }
        });

        myDialog.setNoOnclickListener("", new MyDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    class ItemOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            CheckBox cBox = (CheckBox) view.findViewById(R.id.X_checkbox);
            if (cBox.isChecked()) {
                Log.i("TAG", "取消选项");
                cBox.setChecked(false);
            } else {
                Log.i("TAG", "确认选项");
                cBox.setChecked(true);
            }

            if (position == 0 && (cBox.isChecked())) {
                //如果是选中 全选  就把所有的都选上 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = true;
                }
                adapter.notifyDataSetChanged();
            } else if (position == 0 && (!cBox.isChecked())) {
                //如果是取消全选 就把所有的都取消 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = false;
                }
                adapter.notifyDataSetChanged();
            }
            if (position != 0 && (!cBox.isChecked())) {
                // 如果把其它的选项取消   把全选取消
                bl[0] = false;
                bl[position] = false;
                adapter.notifyDataSetChanged();
            } else if (position != 0 && (cBox.isChecked())) {
                //如果选择其它的选项，看是否全部选择
                //先把该选项选中 设置为true
                bl[position] = true;
                int a = 0;
                for (int i = 1; i < bl.length; i++) {
                    if (bl[i] == false) {
                        //如果有一个没选中  就不是全选 直接跳出循环
                        break;
                    } else {
                        //计算有多少个选中的
                        a++;
                        if (a == bl.length - 1) {
                            //如果选项都选中，就把全选 选中，然后更新
                            bl[0] = true;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }


    //重写simpleadapterd的getview方法
    class SetSimpleAdapter extends SimpleAdapter {

        public SetSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                                int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.check, null);
            }
            CheckBox ckBox = (CheckBox) convertView.findViewById(R.id.X_checkbox);
            //每次都根据 bl[]来更新checkbox
            if (bl[position] == true) {
                ckBox.setChecked(true);
            } else if (bl[position] == false) {
                ckBox.setChecked(false);
            }
            return super.getView(position, convertView, parent);
        }
    }
}
