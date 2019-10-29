package com.yain.controller.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.yain.R;
import com.yain.model.Model;
import com.yain.model.bean.UserInfo;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher {

    private ImageButton mIbNavigationBack;
    private EditText mEtLoginUsername;
    private EditText mEtLoginPwd;
    private LinearLayout mLlLoginUsername;
    private ImageView mIvLoginUsernameDel;
    private Button mBtLoginSubmit;
    private LinearLayout mLlLoginPwd;
    private ImageView mIvLoginPwdDel;
    private ImageView mIvLoginLogo;
    private LinearLayout mLayBackBar;
    private Button mBtLoginRegister;

    //全局Toast
    private Toast mToast;

    private int mLogoHeight;
    private int mLogoWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        initView();
    }

    //初始化视图
    private void initView() {
        // 导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back);

        // logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        // username
        mLlLoginUsername =  findViewById(R.id.ll_login_username);
        mEtLoginUsername = findViewById(R.id.et_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);

        // password
        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);

        // 提交、注册
        mBtLoginSubmit = findViewById(R.id.bt_login_submit);
        mBtLoginRegister = findViewById(R.id.bt_login_register);

        mIbNavigationBack.setOnClickListener(this);
        mEtLoginUsername.setOnClickListener(this);
        mIvLoginUsernameDel.setOnClickListener(this);
        mBtLoginSubmit.setOnClickListener(this);
        mBtLoginRegister.setOnClickListener(this);
        mEtLoginPwd.setOnClickListener(this);
        mIvLoginPwdDel.setOnClickListener(this);

        //注册其它事件
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(this);
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                //返回
                finish();
                break;
            case R.id.et_login_username:
                mEtLoginPwd.clearFocus();
                mEtLoginUsername.setFocusableInTouchMode(true);
                mEtLoginUsername.requestFocus();
                break;
            case R.id.et_login_pwd:
                mEtLoginUsername.clearFocus();
                mEtLoginPwd.setFocusableInTouchMode(true);
                mEtLoginPwd.requestFocus();
                break;
            case R.id.iv_login_username_del:
                //清空用户名
                mEtLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                //清空密码
                mEtLoginPwd.setText(null);
                break;
            case R.id.bt_login_submit:
                //登录
                loginRequest();
                break;
            case R.id.bt_login_register:
                //注册
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    // 用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }

    // 显示或隐藏logo
    @Override
    public void onGlobalLayout() {
        final ImageView ivLogo = this.mIvLoginLogo;
        Rect KeypadRect = new Rect();

        mLayBackBar.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayBackBar.getRootView().getHeight();
        int keypadHeight = screenHeight - KeypadRect.bottom;

        //隐藏logo
        if (keypadHeight > 300 && ivLogo.getTag() == null) {
            final int height = ivLogo.getHeight();
            final int width = ivLogo.getWidth();
            this.mLogoHeight = height;
            this.mLogoWidth = width;

            ivLogo.setTag(true);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                layoutParams.height = (int) (height * animatedValue);
                layoutParams.width = (int) (width * animatedValue);
                ivLogo.requestLayout();
                ivLogo.setAlpha(animatedValue);
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
        //显示logo
        else if (keypadHeight < 300 && ivLogo.getTag() != null) {
            final int height = mLogoHeight;
            final int width = mLogoWidth;

            ivLogo.setTag(null);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                layoutParams.height = (int) (height * animatedValue);
                layoutParams.width = (int) (width * animatedValue);
                ivLogo.requestLayout();
                ivLogo.setAlpha(animatedValue);
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //用户名密码输入事件
    @Override
    public void afterTextChanged(Editable s) {
        String username = mEtLoginUsername.getText().toString().trim();
        String pwd = mEtLoginPwd.getText().toString().trim();

        //是否显示清除按钮
        if (username.length() > 0) {
            mIvLoginUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (pwd.length() > 0) {
            mIvLoginPwdDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginPwdDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    //登录
    private void loginRequest() {
// 1 获取输入的用户名和密码

        String username = mEtLoginUsername.getText().toString().trim();
        String pwd = mEtLoginPwd.getText().toString().trim();

        // 校验输入的用户名和密码
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录逻辑处理
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 去环信服务器登录
            EMClient.getInstance().login(username, pwd, new EMCallBack() {
                // 登录成功后的处理
                @Override
                public void onSuccess() {
                    // 对模型层数据的处理
                    Model.getInstance().loginSuccess(new UserInfo(username));
                    // 保存用户账号信息到本地数据库
                    Model.getInstance().getUserAccountDao().addAccount(new UserInfo(username));
                    runOnUiThread(() -> {
                        // 提示登录成功
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }

                // 登录失败的处理
                @Override
                public void onError(int code, String error) {
                    // 提示登录失败
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败"+error, Toast.LENGTH_SHORT).show());
                }

                // 登录过程中的处理
                @Override
                public void onProgress(int progress, String status) {

                }
            });
        });
    }

    /**
     * 显示Toast
     *
     * @param msg 提示信息内容
     */
    @SuppressLint("ShowToast")
    private void showToast(int msg) {
        if (null != mToast) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
