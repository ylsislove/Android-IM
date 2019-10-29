package com.yain.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.yain.R;
import com.yain.model.Model;
import com.yain.model.bean.UserInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginAcitivity extends Activity {

    private EditText et_login_name;
    private EditText et_login_pwd;
    private EditText et_login_email;
    private EditText et_login_code;
    private Button bt_login_register;
    private Button bt_login_login;
    private Button bt_login_code;

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

        // 获取验证码按钮的点击事件处理
        bt_login_code.setOnClickListener(v -> getCodeBySoap());
    }

    // 请求soap风格的webservice服务
    private void getCodeBySoap() {
        // 检验邮箱
        String registEmail = et_login_email.getText().toString();
        if (TextUtils.isEmpty(registEmail)) {
            Toast.makeText(LoginAcitivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成激活码
        Random r = new Random();
        // 字符串的拼接
        String code = "" + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10);
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
                                "<sch:url>"+registEmail+"</sch:url>" +
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

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 请求rest风格的webservice服务 POST方式
    private void getCodeByRestPost() {
        // 检验邮箱
        String registEmail = et_login_email.getText().toString();
        if (TextUtils.isEmpty(registEmail)) {
            Toast.makeText(LoginAcitivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成激活码
        Random r = new Random();
        // 字符串的拼接
        String code = "" + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10);
        Log.i("payload", code);

        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                //new一个访问的url
                URL url = new URL("http://www.yaindream.com:8080/rest/email/post");
                //创建HttpURLConnection 实例
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置超时时间
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                // 数据准备
                Map<String, Object> map = new HashMap<>(2);
                String[] urls = { registEmail };
                map.put("urls", urls);
                map.put("payload", code);

                JSONObject jsonObject = new JSONObject(map);
                String json = jsonObject.toString();
                Log.i("json", json);

                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Content-Length", json.length()+"");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(json.getBytes());

                if (connection.getResponseCode() == 200) {
                    //接收服务器输入流信息
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    //拿到信息
                    String  result = br.readLine();
                    Log.i("返回数据：", result);
                    is.close();
                    connection.disconnect();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 请求rest风格的webservice服务 GET方式
    private void getCodeByRestGet() {
        // 检验邮箱
        String registEmail = et_login_email.getText().toString();
        if (TextUtils.isEmpty(registEmail)) {
            Toast.makeText(LoginAcitivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成激活码
        Random r = new Random();
        // 字符串的拼接
        String code = "" + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10);
        Log.i("payload", code);

        Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    //new一个访问的url
                    URL url = new URL("http://www.yaindream.com:8080/rest/email/get?url="+registEmail+"&payload="+code);
                    //创建HttpURLConnection 实例
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置超时时间
                    connection.setConnectTimeout(5000);//连接超时
                    //提交数据的方式
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == 200) {
                        //接收服务器输入流信息
                        InputStream is = connection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        //拿到信息
                        String  result = br.readLine();
                        Log.i("返回数据：", result);
                        is.close();
                        connection.disconnect();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
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
        et_login_email = findViewById(R.id.et_login_email);
        et_login_code = findViewById(R.id.et_login_code);
        bt_login_register = findViewById(R.id.bt_login_register);
        bt_login_login = findViewById(R.id.bt_login_login);
        bt_login_code = findViewById(R.id.bt_login_code);
    }
}
