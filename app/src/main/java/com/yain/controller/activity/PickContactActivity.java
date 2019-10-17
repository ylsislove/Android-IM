package com.yain.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.yain.R;
import com.yain.controller.adapter.PickContactAdapter;
import com.yain.model.Model;
import com.yain.model.bean.PickContactInfo;
import com.yain.model.bean.UserInfo;
import com.yain.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择联系人页面
 */
public class PickContactActivity extends Activity {

    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExistMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        // 获取传递过来的数据
        getData();
        initView();
        initData();
        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            // 获取群众已经存在的所有群成员
            mExistMembers = group.getMembers();
        }
        if (mExistMembers == null) {
            mExistMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        // listview条目点击事件
        lv_pick.setOnItemClickListener((parent, view, position, id) -> {
            // checkbox的切换
            CheckBox cb_pick = view.findViewById(R.id.cb_pick);
            cb_pick.setChecked(!cb_pick.isChecked());
            // 修改数据
            PickContactInfo pickContactInfo = mPicks.get(position);
            pickContactInfo.setIsChecked(cb_pick.isChecked());
            // 刷新页面
            pickContactAdapter.notifyDataSetChanged();
        });

        // 保存按钮的点击事件
        tv_pick_save.setOnClickListener(v -> {
            // 获取到已经选择的联系人
            List<String> names = pickContactAdapter.getPickContacts();
            // 给启动页面返回数据
            Intent intent = new Intent();
            intent.putExtra("members", names.toArray(new String[0]));
            // 设置返回的结果码
            setResult(RESULT_OK, intent);
            // 结束当前页面
            finish();
        });
    }

    private void initData() {
        // 从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        mPicks = new ArrayList<>();
        if (contacts != null && contacts.size() >= 0) {
            // 转换
            for (UserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPicks.add(pickContactInfo);
            }
        }
        // 初始化listview
        pickContactAdapter = new PickContactAdapter(this, mPicks, mExistMembers);
        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initView() {
        tv_pick_save = findViewById(R.id.tv_pick_save);
        lv_pick = findViewById(R.id.lv_pick);
    }

}
