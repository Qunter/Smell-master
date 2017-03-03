package com.yufa.smell.Activity.ChatCenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/3/2.
 */

public class UserSearch extends BaseActivity {
    @BindView(R.id.seachEt)
    EditText seachEt;
    @BindView(R.id.seachBtn)
    Button seachBtn;
    @BindView(R.id.resetBtn)
    Button resetBtn;
    LinearLayout userSearchLinLayout;
    final int ADDFRIENDASK=0x00,DELFRIENDASK=0x01;
    String txUrl,name;
    LinearLayout friendView;
    //用于标记是否已显示用户信息
    int flag=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ADDFRIENDASK:
                    if(flag==1) {
                        userSearchLinLayout.removeView(friendView);
                        flag = 0;
                    }
                    userSearchLinLayout.addView(addFriendView(txUrl,name));
                    flag=1;
                    break;
                case DELFRIENDASK:
                    userSearchLinLayout.removeView(friendView);
                    break;
            }
        }
    };
    @Override
    public void isShowToolBar() {
    }

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.acticity_usersearch);
        userSearchLinLayout = (LinearLayout) findViewById(R.id.userSearchLinLayout);
        //用hander添加一个自定义控件----未完成！
        ButterKnife.bind(this);
    }
    @OnClick({R.id.seachBtn, R.id.resetBtn})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.seachBtn:
                searchUserInformation(seachEt.getText().toString());
                break;
            case R.id.resetBtn:
                seachEt.setText("");
                break;
        }
    }

    /**
     * bmob查询某用户信息
     */
    private void searchUserInformation(String userNameOrPhone){
        final BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("phone", userNameOrPhone);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object, BmobException e) {
                if(e==null){
                    UserInformation user =object.get(0);
                    txUrl = user.getImage();
                    name = user.getNickName();
                    handler.sendEmptyMessage(ADDFRIENDASK);
                    //Toast.makeText(getApplicationContext(), user.getNickName()+"查询成功", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), "查询失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 动态生成是否添加好友（显示用户信息）的控件
     */
    private LinearLayout addFriendView(String txUrl,String name){
        friendView = new LinearLayout(this);
        friendView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        friendView.setLayoutParams(llparams);
        CircleView touxiang = new CircleView(this);
        DownImage di = (DownImage) new DownImage(touxiang,txUrl).execute(txUrl);
        LinearLayout.LayoutParams txparams = new LinearLayout.LayoutParams(100, 100);
        txparams.setMargins(30,15,15,15);
        touxiang.setLayoutParams(txparams);
        LinearLayout.LayoutParams unparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        unparams.setMargins(30,0,30,0);
        TextView userName = new TextView(this);
        userName.setText(name);
        userName.setLayoutParams(unparams);
        userName.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.setMargins(30,0,30,0);
        Button add = new Button(this);
        add.setText("添加好友");
        add.setLayoutParams(btnparams);
        add.setGravity(Gravity.CENTER_VERTICAL);
        Button noAdd = new Button(this);
        noAdd.setText("取消");
        noAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(DELFRIENDASK);
            }
        });
        noAdd.setLayoutParams(btnparams);
        noAdd.setGravity(Gravity.CENTER_VERTICAL);
        friendView.addView(touxiang);
        friendView.addView(userName);
        friendView.addView(add);
        friendView.addView(noAdd);
        return friendView;
    }
    class DownImage extends AsyncTask<String, Void, Bitmap> {

        private CircleView imageView;
        public DownImage(CircleView imageView,String url) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            try {
                //加载一个网络图片
                InputStream is = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
