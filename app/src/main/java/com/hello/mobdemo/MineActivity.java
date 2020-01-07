package com.hello.mobdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MineActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edt_phone;
    private EditText edt_code;
    private Button btn_get_code;
    private Button btn_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        initView();
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int even, int result, Object data) {
                if (even == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                            handler.sendEmptyMessage(1);
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (even == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        handler.sendEmptyMessage(2);
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        });

    }

    private void initView() {
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_code = (Button) findViewById(R.id.btn_code);

        btn_get_code.setOnClickListener(this);
        btn_code.setOnClickListener(this);
        btn_code.setEnabled(false);
        edt_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    btn_code.setEnabled(false);
                } else {
                    btn_code.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                String phone = edt_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.getVerificationCode("86", phone);
//                语言验证码
//                SMSSDK.getVoiceVerifyCode("86",phone);
                edt_code.setHint("请输入验证码");
                break;
            case R.id.btn_code:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String phone = edt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = edt_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        SMSSDK.submitVerificationCode("86", phone, code);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MineActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MineActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MineActivity.this, "获取发送验证码的国家列表成功", Toast.LENGTH_SHORT).show();
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
