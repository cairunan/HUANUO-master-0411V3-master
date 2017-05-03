package com.example.logintest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);
        responseText = (TextView) findViewById(R.id.response_text);
        login.setOnClickListener(this);
        checkBox1=(CheckBox) findViewById(R.id.checkBox1);
        checkBox2=(CheckBox) findViewById(R.id.checkBox2);
        login.setOnClickListener(this);
        checkBox1=(CheckBox) findViewById(R.id.checkBox1);
        checkBox2=(CheckBox) findViewById(R.id.checkBox2);
        boolean isRemember=pref.getBoolean("checkBox2",false);
        if (isRemember){
            String account =pref.getString("account","");
            String password =pref.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            checkBox2.setChecked(true);

        }
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void  onClick(View v) {
        if (v.getId() == R.id.login){
            sendRequestWithHttpURLConnection();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                String use = accountEdit.getText().toString();
                String pas = passwordEdit.getText().toString();
                String code;
                try {
                    Map dataMap = new HashMap();
                    dataMap.put("username", use);
                    dataMap.put("Password", pas);
                    code = new HttpRequestor().doPost("http://172.16.201.17:8080/HuanuoServer/login", dataMap);
                    if (code.equals("1")) {
                        editor=pref.edit();
                        if(checkBox2.isChecked()){
                            editor.putBoolean("checkBox2",true);
                            editor.putString("account",use);
                            editor.putString("password",pas);
                        }else {
                            editor.clear();
                        }editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (code.equals("-1")) {
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "帐号不存在", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.getMessage();
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "请检查网络设置", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    // 点击手机HOME键，使应用程序退到后台；当再次打开App时，当前显示页面还是刚才退出时的页面
    // 点击返回键，弹出提示窗口
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 监控返回键
            new AlertDialog.Builder(LoginActivity.this).setTitle("提示")
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setMessage("确定要退出吗?")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.finish();
                        }})
                    .setNegativeButton("取消", null)
                    .create().show();
            return false;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {
            // 监控菜单键
            Toast.makeText(LoginActivity.this, "Menu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}