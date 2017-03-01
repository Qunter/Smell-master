package com.yufa.smell.Activity.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/5.
 * 找回密码
 */

public class RePasswordActivity extends BaseActivity {
    @BindView(R.id.repassword_phone)
    EditText repasswordPhone;
    @BindView(R.id.repassword_registerCode)
    EditText repasswordRegisterCode;
    @BindView(R.id.repassword_send)
    Button repasswordSend;
    @BindView(R.id.repassword_sumit)
    Button repasswordSumit;
    @BindView(R.id.header_textview)
    TextView headerTextview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_repassword);
        ButterKnife.bind(this);
        showActionBar(toolbar);
        headerTextview.setText("身份验证");
    }

    @Override
    public void isShowToolBar() {

    }

    @Override
    public void isTransition(Boolean isShow) {
        super.isTransition(false);
    }

    @OnClick({R.id.repassword_send, R.id.repassword_sumit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repassword_send:
                String phone = repasswordPhone.getText().toString();
                doSendMessage(phone);
                break;
            case R.id.repassword_sumit:
                String str = repasswordRegisterCode.getText().toString();
                submit(str);
                break;
        }
    }

    private void doSendMessage(String phone) {

    }

    private void submit(String str) {
        if (str.equals("1234")) {
            Intent intent = new Intent();
            intent.setClass(RePasswordActivity.this, ResetActivity.class);
            startActivity(intent);
        }
    }
}
