package com.yain.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yain.R;
import com.yain.controller.adapter.GroupListAdapter;
import com.yain.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class GroupListActivity extends Activity  {

    private ListView lv_grouplist;
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        // listview条目点击事件
        lv_grouplist.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("TAG", "positon:"+position);
            if(position == 0) {
                return;
            }
            Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
            // 传递会话类型
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
            // 群id
            EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
            intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());
            startActivity(intent);
        });
        // 跳转到新建群
        ll_grouplist.setOnClickListener(v -> {
            Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
            startActivity(intent);
        });
    }

    private void initData() {
        // 初始化listview
        groupListAdapter = new GroupListAdapter(this);
        lv_grouplist.setAdapter(groupListAdapter);
        // 从环信服务器获取所有群的信息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                // 从网络获取数据
                final List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                // 更新页面
                runOnUiThread(() -> {
                    Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                    // groupListAdapter.refresh(mGroups);
                    // 刷新
                    refresh();
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // 刷新
    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        // 获取listview对象
        lv_grouplist = findViewById(R.id.lv_grouplist);
        // 添加头布局
        View headerView = View.inflate(this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headerView);
        ll_grouplist = headerView.findViewById(R.id.ll_grouplist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新页面
        refresh();
    }

}
