package com.yufa.smell.Activity.LoginAndRegister;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;
import com.yufa.smell.Util.JudgeTool;
import com.yufa.smell.Util.ShowTool;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.rong.methods.User;

/**
 * Created by luyufa on 2016/11/14.
 * 注册
 */

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.register_custom)
    EditText registerCustom;
    @BindView(R.id.register_password)
    EditText registerPassword;
    @BindView(R.id.register_phone)
    EditText registerPhone;
    @BindView(R.id.register_nickname)
    EditText registerNickname;
    @BindView(R.id.register_enroll)
    Button registerEnroll;
    @BindView(R.id.register_login)
    Button registerLogin;
    int RSUCCESS=0X00,RFAIL=0X01;
    String rongToken="";
    private Handler rongHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==RSUCCESS){
                rongToken=msg.obj.toString();
                Toast.makeText(RegisterActivity.this,rongToken,Toast.LENGTH_SHORT).show();
                register();
            }else{
                Toast.makeText(RegisterActivity.this,"获取token失败",Toast.LENGTH_SHORT).show();
                //错误，需要报错
            }

        }
    };

    @Override
    public void initVariables() {
        super.initVariables();
    }

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");

    }

    @Override
    public void isShowToolBar() {
        hideActionBar();
    }

    @OnClick({R.id.register_enroll, R.id.register_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_enroll:
                if (new JudgeTool().isPhoneNumber(registerPhone.getText().toString())){
                    isRegister();
                }else {
                    new ShowTool().showToast(RegisterActivity.this,"您输入的手机号码有误，请重新输入");
                }
                break;
            case R.id.register_login:
                startActivity(LoginActivity.class);
                break;
        }
    }
    private void isRegister(){
        String username = registerCustom.getText().toString().trim();
        BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("username",username);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> list, BmobException e) {
                if (e==null&&list.size()==0){
                    new getRongToken(registerPhone.getText().toString(),registerNickname.getText().toString()).start();
                }else {
                    new ShowTool().showToast(RegisterActivity.this,"您的账号已经被使用了");
                }
            }
        });
    }

    private void register(){
        UserInformation user = new UserInformation();
        user.setUsername(registerCustom.getText().toString());
        user.setNickName(registerNickname.getText().toString());
        user.setPhone(registerPhone.getText().toString());
        user.setPassword(registerPassword.getText().toString());
        user.setImage("http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png");//未设置头像时默认用户头像
        user.setToken(rongToken);
        /*
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    showDialog();
                    finish();
                }
            }
        });
        */
        user.signUp(new SaveListener<UserInformation>() {
            @Override
            public void done(UserInformation s, BmobException e) {
                if(e==null){
                    //showDialog();
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                    //toast("注册成功:" +s.toString());
                }else{
                    Log.e("失败代码",e.toString());
                }

            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.content_simple,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.simple_image);
        TextView textView = (TextView)view.findViewById(R.id.simple_text);
        imageView.setImageResource(R.drawable.repassword);
        textView.setText("注册成功");
        builder.setPositiveButton("现在进入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(RegisterActivity.this,"现在进入",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    /*
    private String getRongToken(String userId, String name){
        User rongAppInformation = new User("z3v5yqkbzc1i0", "rAZ2RNIWtWCNYq");//融云APP信息
        String rongToken = "";
        new Thread()
        {
            @Override
            public void run() {
                super.run();
            }
        }.start();
        try {
            //使用默认头像，但函数需要三参
            Toast.makeText(RegisterActivity.this,userId+"  "+name,Toast.LENGTH_SHORT).show();
            rongToken=rongAppInformation.getToken(userId,name,"http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png").getToken();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this,"获取token失败",Toast.LENGTH_SHORT).show();
            //错误，需要报错
        }
        return rongToken;
    }
    */
    class getRongToken extends Thread{
        String userId;//定义线程内变量
        String name;
        User rongAppInformation = new User("z3v5yqkbzc1i0", "rAZ2RNIWtWCNYq");//融云APP信息
        String rongToken = "";
        public getRongToken(String userId, String name){//定义带参数的构造函数,达到初始化线程内变量的值
            this.userId=userId;
            this.name=name;
        }
        @Override
        public void run() {
            try {
                //使用默认头像，但函数需要三参
                rongToken=rongAppInformation.getToken(userId,name,"http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png").getToken();
                Message msg = new Message();
                msg.what = RSUCCESS;//用于区分不同的消息
                msg.obj = rongToken ;//消息内容
                rongHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                rongHandler.sendEmptyMessage(RFAIL);

            }
        }
    }
}
