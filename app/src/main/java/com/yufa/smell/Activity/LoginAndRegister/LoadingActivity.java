package com.yufa.smell.Activity.LoginAndRegister;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.MainActivity;
import com.yufa.smell.Activity.SettingCenter.ScreenLockActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.SharedPreferencesHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by luyufa on 2016/11/14.
 * 加载页面
 */

public class LoadingActivity extends BaseActivity {

    @BindView(R.id.loading_login)
    Button loadingLogin;
    @BindView(R.id.loading_register)
    Button loadingRegister;
    @BindView(R.id.loading_group)
    LinearLayout loadingGroup;
    @BindView(R.id.loading_up)
    TextView loadingUp;
    @BindView(R.id.same)
    CircleView same;
    private GestureDetector gestureDetector;


    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        gestureDetector = new GestureDetector(LoadingActivity.this, onGestureListener);
        loadingGroup.setVisibility(View.GONE);
    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.loading_login, R.id.loading_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loading_login:
                isLogin();
                break;
            case R.id.loading_register:
                startActivity(RegisterActivity.class);
                break;
        }
    }

    private void isLogin(){
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(this);
        String username = sph.getString("username","null");
        String password = sph.getString("password","null");
        final String isLock = sph.getString("drawpasw",null);
        BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("username",username);
        query.addWhereEqualTo("password",password);
        query.findObjects(new FindListener<UserInformation>() {
            @SuppressLint("NewApi")
            @Override
            public void done(List<UserInformation> list, BmobException e) {
                if(e != null){
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoadingActivity.this, same, "same").toBundle());
                }else if(isLock!=null){
                    Intent intent = new Intent();
                    intent.putExtra("setOrValidate",1);
                    intent.setClass(LoadingActivity.this, ScreenLockActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent begin, MotionEvent end, float velocityX, float velocityY) {

            if (begin.getX() < 1900) {
                float x = end.getX() - begin.getX();
                float y = end.getY() - begin.getY();

                if (x > 0) {
                    showLogin();
                } else if (x < 0) {
                    hideLogin();
                }
                return true;
            }
            return false;
        }
    };

    private void showLogin() {
        loadingUp.setVisibility(View.GONE);
        loadingGroup.setVisibility(View.VISIBLE);
    }

    private void hideLogin() {
        loadingUp.setVisibility(View.VISIBLE);
        loadingGroup.setVisibility(View.GONE);
    }
}
