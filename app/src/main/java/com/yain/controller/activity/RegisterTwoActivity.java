package com.yain.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.yain.R;
import com.yain.model.Model;

public class RegisterTwoActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher {

    private ImageButton mIbNavigationBack;
    private EditText mEtRegisterUsername;
    private LinearLayout mLlRegisterUsername;
    private ImageView mIvRegisterUsernameDel;
    private Button mBtRegisterSubmit;
    private EditText mEtRegisterPassword;
    private LinearLayout mLlRegisterPassword;
    private ImageView mIvRegisterPasswordDel;
    private LinearLayout mLayBackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register_step_two);
        initView();
    }

    //初始化视图
    private void initView() {
        // 导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_register_bar);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back);

        // username
        mLlRegisterUsername =  findViewById(R.id.ll_register_two_username);
        mEtRegisterUsername = findViewById(R.id.et_register_username);
        mIvRegisterUsernameDel = findViewById(R.id.iv_register_username_del);

        // password
        mLlRegisterPassword = findViewById(R.id.ll_register_two_pwd);
        mEtRegisterPassword = findViewById(R.id.et_register_pwd_input);
        mIvRegisterPasswordDel = findViewById(R.id.iv_register_pwd_del);

        // 注册
        mBtRegisterSubmit = findViewById(R.id.bt_register_submit);

        mIbNavigationBack.setOnClickListener(this);
        mEtRegisterUsername.setOnClickListener(this);
        mIvRegisterUsernameDel.setOnClickListener(this);
        mBtRegisterSubmit.setOnClickListener(this);
        mEtRegisterPassword.setOnClickListener(this);
        mIvRegisterPasswordDel.setOnClickListener(this);

        //注册其它事件
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mEtRegisterUsername.setOnFocusChangeListener(this);
        mEtRegisterUsername.addTextChangedListener(this);
        mEtRegisterPassword.setOnFocusChangeListener(this);
        mEtRegisterPassword.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.et_register_username:
                mEtRegisterPassword.clearFocus();
                mEtRegisterUsername.setFocusableInTouchMode(true);
                mEtRegisterUsername.requestFocus();
                break;
            case R.id.et_register_pwd_input:
                mEtRegisterUsername.clearFocus();
                mEtRegisterPassword.setFocusableInTouchMode(true);
                mEtRegisterPassword.requestFocus();
                break;
            case R.id.iv_register_username_del:
                // 清空用户名
                mEtRegisterUsername.setText(null);
                break;
            case R.id.iv_register_pwd_del:
                // 清空密码
                mEtRegisterPassword.setText(null);
                break;
            case R.id.bt_register_submit:
                // 注册
                RegisterRequest();
                break;
            default:
                break;
        }
    }

    private void RegisterRequest() {
        // 1 获取输入的用户名和密码
        String username = mEtRegisterUsername.getText().toString().trim();
        String password = mEtRegisterPassword.getText().toString().trim();

        // 2 校验输入的用户名和密码
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterTwoActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3 去服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                // 去环信服务器注册账号
                EMClient.getInstance().createAccount(username, password);
                // 更新页面显示
                runOnUiThread(() -> Toast.makeText(RegisterTwoActivity.this, "注册成功", Toast.LENGTH_SHORT).show());
                // 跳转到登录界面
                Intent intent = new Intent(RegisterTwoActivity.this, LoginActivity.class);
                startActivity(intent);

            } catch (final HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterTwoActivity.this, "注册失败"+e.toString(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    // 用户名、密码输入事件
    @Override
    public void afterTextChanged(Editable s) {

        String username = mEtRegisterUsername.getText().toString().trim();
        String password = mEtRegisterPassword.getText().toString().trim();

        //是否显示清除按钮
        if (username.length() > 0) {
            mIvRegisterUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (password.length() > 0) {
            mIvRegisterPasswordDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterPasswordDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    // 邮箱地址、验证码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_register_email) {
            if (hasFocus) {
                mLlRegisterUsername.setActivated(true);
                mLlRegisterPassword.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlRegisterPassword.setActivated(true);
                mLlRegisterUsername.setActivated(false);
            }
        }
    }

    @Override
    public void onGlobalLayout() {

    }
}