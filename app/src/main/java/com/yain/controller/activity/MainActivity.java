package com.yain.controller.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.yain.R;
import com.yain.controller.fragment.ChatFragment;
import com.yain.controller.fragment.ContactListFragment;
import com.yain.controller.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment fragment = null;
            switch (checkedId) {
                // 会话列表页面
                case R.id.rb_main_chat:
                    fragment = chatFragment;
                    break;
                // 联系人列表页面
                case R.id.rb_main_contact:
                    fragment = contactListFragment;
                    break;
                // 设置页面
                case R.id.rb_main_setting:
                    fragment = settingFragment;
                    break;
            }
            // 实现fragment切换的方法
            switchFragment(fragment);
        });
        // 默认选择会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }

    // 实现fragment切换的方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initData() {
        // 创建三个fragment对象
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);
    }
}
