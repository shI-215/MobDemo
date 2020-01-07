package com.hello.mobdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_code;
    private TextView tv_code_mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv_code = (TextView) findViewById(R.id.tv_code);
        tv_code.setOnClickListener(this);
        tv_code_mine = (TextView) findViewById(R.id.tv_code_mine);
        tv_code_mine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_code:
                RegisterPage registerPage = new RegisterPage();
                registerPage.setRegisterCallback(new EventHandler() {
                    @Override
                    public void afterEvent(int even, int result, Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String country = String.valueOf(phoneMap.get("country"));
                            String phone = String.valueOf(phoneMap.get("phone"));
                            registerUser(country, phone);
                            handler.obtainMessage(1).sendToTarget();
                        } else {
                            handler.obtainMessage(2).sendToTarget();
                        }
                    }
                });
                registerPage.show(this);
                break;
            case R.id.tv_code_mine:
                startActivity(new Intent(MainActivity.this, MineActivity.class));
                break;
        }
    }

    private void registerUser(String country, String phone) {
        Random random = new Random();
        int id = Math.abs(random.nextInt());
        String name = "user_" + id;
        String picture = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3778255959,2988549499&fm=26&gp=0.jpg";
        SMSSDK.submitUserInfo(id + "", name, picture, country, phone);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
