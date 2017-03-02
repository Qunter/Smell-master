package com.yufa.smell.Activity.ChatCenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/21.
 */

public class FriendListActivity extends BaseActivity {

    @Override
    public void isShowToolBar() {
    }

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_friendlist);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.friend_add, R.id.friendList_persion})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_add:
                startActivity(UserSearch.class);
                break;
            case R.id.friendList_persion:
                break;
        }
    }
}
