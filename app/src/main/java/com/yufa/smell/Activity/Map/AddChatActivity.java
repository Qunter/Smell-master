package com.yufa.smell.Activity.Map;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/27.
 */

public class AddChatActivity extends BaseActivity {


    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_addchat);
        ButterKnife.bind(this);
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }
}
