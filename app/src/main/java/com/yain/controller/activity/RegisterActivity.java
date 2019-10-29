package com.yain.controller.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yain.R;
import com.yain.model.Model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher {

    private ImageButton mIbNavigationBack;
    private EditText mEtRegisterEmail;
    private EditText mEtRegisterAuthCode;
    private LinearLayout mLlRegisterEmail;
    private ImageView mIvRegisterEmailDel;
    private Button mBtRegisterSubmit;
    private LinearLayout mLlRegisterCode;
    private TextView mTvRegisterCodeCall;
    private ImageView mIvLoginLogo;
    private LinearLayout mLayBackBar;

    private int mLogoHeight;
    private int mLogoWidth;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register_step_one);
        initView();
    }

    //初始化视图
    private void initView() {
        // 导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back);

        // logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        // email
        mLlRegisterEmail =  findViewById(R.id.ll_register_email);
        mEtRegisterEmail = findViewById(R.id.et_register_email);
        mIvRegisterEmailDel = findViewById(R.id.iv_register_email_del);

        // code
        mLlRegisterCode = findViewById(R.id.ll_register_sms_code);
        mEtRegisterAuthCode = findViewById(R.id.et_register_auth_code);
        mTvRegisterCodeCall = findViewById(R.id.tv_register_sms_call);

        // 注册
        mBtRegisterSubmit = findViewById(R.id.bt_register_submit);

        mIbNavigationBack.setOnClickListener(this);
        mEtRegisterEmail.setOnClickListener(this);
        mIvRegisterEmailDel.setOnClickListener(this);
        mBtRegisterSubmit.setOnClickListener(this);
        mEtRegisterAuthCode.setOnClickListener(this);
        mTvRegisterCodeCall.setOnClickListener(this);

        //注册其它事件
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mEtRegisterEmail.setOnFocusChangeListener(this);
        mEtRegisterEmail.addTextChangedListener(this);
        mEtRegisterAuthCode.setOnFocusChangeListener(this);
        mEtRegisterAuthCode.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.et_register_email:
                mEtRegisterAuthCode.clearFocus();
                mEtRegisterEmail.setFocusableInTouchMode(true);
                mEtRegisterEmail.requestFocus();
                break;
            case R.id.et_register_auth_code:
                mEtRegisterEmail.clearFocus();
                mEtRegisterAuthCode.setFocusableInTouchMode(true);
                mEtRegisterAuthCode.requestFocus();
                break;
            case R.id.iv_register_email_del:
                // 清空邮箱地址
                mEtRegisterEmail.setText(null);
                break;
            case R.id.tv_register_sms_call:
                // 发送验证码
                emailCodeRequest();
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

        String email = mEtRegisterEmail.getText().toString().trim();
        String userCode = mEtRegisterAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(userCode)) {
            Toast.makeText(RegisterActivity.this, "邮箱地址或验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code != null && userCode.equals(code)) {
            // 跳转到注册界面2
            Intent intent = new Intent(RegisterActivity.this, RegisterTwoActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void emailCodeRequest() {

        String email = mEtRegisterEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成激活码
        Random r = new Random();
        // 字符串的拼接
        code = "" + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10);
        Log.i("payload", code);

        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                //new一个访问的url
                URL url = new URL("http://www.yaindream.com:8080/soap/email/service");
                //创建HttpURLConnection 实例
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置超时时间
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //请求体
                String xmlString =
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sch=\"http://yaindream.com/schemas\">" +
                                "<soapenv:Header/>" +
                                "<soapenv:Body>" +
                                "<sch:EmailRequest>" +
                                "<sch:url>"+email+"</sch:url>" +
                                "<sch:payload>"+code+"</sch:payload>" +
                                "</sch:EmailRequest>" +
                                "</soapenv:Body>" +
                                "</soapenv:Envelope>";

                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type","text/xml");
                connection.setRequestProperty("Content-Length", xmlString.length()+"");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(xmlString.getBytes());

                if (connection.getResponseCode() == 200) {
                    //接收服务器输入流信息
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    //拿到信息
                    String  result = br.readLine();
                    Log.i("返回数据：", result);
                    is.close();
                    connection.disconnect();
                    if ("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:EmailResponse xmlns:ns2=\"http://yaindream.com/schemas\"><ns2:result>Y</ns2:result></ns2:EmailResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>".equals(result)) {
                        runOnUiThread(() -> {
                            // 提示发送成功
                            Toast.makeText(RegisterActivity.this, "调用基于Soap风格的WebService服务，返回值：Y", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            // 提示发送失败
                            Toast.makeText(RegisterActivity.this, "调用基于Soap风格的WebService服务，返回值：N\n" + result, Toast.LENGTH_LONG).show();
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    // 邮箱地址输入事件
    @Override
    public void afterTextChanged(Editable s) {

        String email = mEtRegisterEmail.getText().toString().trim();
        String userCode = mEtRegisterAuthCode.getText().toString().trim();

        //是否显示清除按钮
        if (email.length() > 0) {
            mIvRegisterEmailDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterEmailDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(userCode)) {
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
                mLlRegisterEmail.setActivated(true);
                mLlRegisterCode.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlRegisterCode.setActivated(true);
                mLlRegisterEmail.setActivated(false);
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
}