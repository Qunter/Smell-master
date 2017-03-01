package com.yufa.smell.Activity.LoginAndRegister;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Activity.Map.MapActivity;
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
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 *
 * Created by luyufa on 2016/11/14.
 * 登录页面
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_sign)
    Button loginSign;
    @BindView(R.id.login_forget)
    Button loginForget;
    @BindView(R.id.same)
    CircleView same;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
    }
    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    /*
    @OnClick({R.id.login_sign, R.id.login_forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_sign:
                final String username = loginUsername.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
                query.addWhereEqualTo("username",username);
                query.addWhereEqualTo("password",password);
                query.findObjects(new FindListener<UserInformation>() {
                    @Override
                    public void done(List<UserInformation> list, BmobException e) {
                        Log.d("----->","" + list.size());
                        if (e==null&&list.size()!=0){
                            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(LoginActivity.this);
                            sharedPreferencesHelper.putString("username",loginUsername.getText().toString().trim());
                            sharedPreferencesHelper.putString("password",loginPassword.getText().toString().trim());
                            startActivity(MapActivity.class);
                            finish();
                        }else {
                            showDialog();
                        }
                    }
                });
                break;
            case R.id.login_forget:
                startActivity(RePasswordActivity.class);
                break;
        }
    }
    */
    @OnClick({R.id.login_sign, R.id.login_forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_sign:
                final String username = loginUsername.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                UserInformation loginUser = new UserInformation();
                loginUser.setUsername(username);
                loginUser.setPassword(password);
                //Toast.makeText(LoginActivity.this,username+"  "+password,Toast.LENGTH_SHORT).show();
                loginUser.login(new SaveListener<UserInformation>() {
                    @Override
                    public void done(UserInformation loginUser, BmobException e) {
                        if(e==null){
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            startActivity(MapActivity.class);
                            finish();
                            //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                            //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                        }else{
                            //Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                            //Log.e("失败代码",e.toString());
                            //loge(e);
                        }
                    }
                });
                /*
                loginUser.loginByAccount("username", "用户密码", new LogInListener<UserInformation>() {
                    @Override
                    public void done(UserInformation userInformation, BmobException e) {
                        if(userInformation!=null){
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            startActivity(MapActivity.class);
                            finish();
                            //Log.i("smile","用户登陆成功");
                        }else{
                            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                            Log.e("失败代码",e.toString());
                        }
                    }
                });
                */
                /*
                BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
                query.addWhereEqualTo("username",username);
                query.addWhereEqualTo("password",password);
                query.findObjects(new FindListener<UserInformation>() {
                    @Override
                    public void done(List<UserInformation> list, BmobException e) {
                        Log.d("----->","" + list.size());
                        if (e==null&&list.size()!=0){
                            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(LoginActivity.this);
                            sharedPreferencesHelper.putString("username",loginUsername.getText().toString().trim());
                            sharedPreferencesHelper.putString("password",loginPassword.getText().toString().trim());
                            startActivity(MapActivity.class);
                            finish();
                        }else {
                            showDialog();
                        }
                    }
                });
                */
                break;
            case R.id.login_forget:
                startActivity(RePasswordActivity.class);
                break;
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_simple,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.simple_image);
        TextView textView = (TextView)view.findViewById(R.id.simple_text);
        imageView.setImageResource(R.drawable.errorpassword);
        textView.setText("密码错误");
        builder.setView(view);
        builder.setNegativeButton("找回密码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"找回密码",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"取消",Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }
}
