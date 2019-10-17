package com.yain.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.yain.R;
import com.yain.model.Model;
import com.yain.model.bean.UserInfo;

public class LoginAcitivity extends Activity {

    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button bt_login_register;
    private Button bt_login_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);
        // 初始化控件
        initView();
        // 初始化监听
        initListener();
    }

    private void initListener() {
        // 注册按钮的点击事件处理
        bt_login_register.setOnClickListener(v -> regist());

        // 登录按钮的点击事件处理
        bt_login_login.setOnClickListener(v -> login());
    }

    // 登录按钮的页面逻辑处理
    private void login() {
        // 1 获取输入的用户名和密码
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();

        // 2 校验输入的用户名和密码
        if(TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginAcitivity.this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录逻辑处理
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 去环信服务器登录
            EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                // 登录成功后的处理
                @Override
                public void onSuccess() {
                    // 对模型层数据的处理
                    Model.getInstance().loginSuccess(new UserInfo(loginName));
                    // 保存用户账号信息到本地数据库
                    Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));
                    runOnUiThread(() -> {
                        // 提示登录成功
                        Toast.makeText(LoginAcitivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        // 跳转到主页面
                        Intent intent = new Intent(LoginAcitivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }

                // 登录失败的处理
                @Override
                public void onError(int code, String error) {
                    // 提示登录失败
                    runOnUiThread(() -> Toast.makeText(LoginAcitivity.this, "登录失败"+error, Toast.LENGTH_SHORT).show());
                }

                // 登录过程中的处理
                @Override
                public void onProgress(int progress, String status) {

                }
            });
        });
    }

    // 注册的业务逻辑处理
    private void regist() {
        // 1 获取输入的用户名和密码
        final String registName = et_login_name.getText().toString();
        final String registPwd = et_login_pwd.getText().toString();

        // 2 校验输入的用户名和密码
        if(TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginAcitivity.this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3 去服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                // 去环信服务器注册账号
                EMClient.getInstance().createAccount(registName, registPwd);
                // 更新页面显示
                runOnUiThread(() -> Toast.makeText(LoginAcitivity.this, "注册成功", Toast.LENGTH_SHORT).show());

            } catch (final HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginAcitivity.this, "注册失败"+e.toString(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pwd = findViewById(R.id.et_login_pwd);
        bt_login_register = findViewById(R.id.bt_login_register);
        bt_login_login = findViewById(R.id.bt_login_login);
    }
}
