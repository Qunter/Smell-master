package com.yufa.smell.Activity.ChatCenter;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yufa.smell.Activity.BaseActivity;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

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
    private void searchUserInformation(String userNameOrPhone){
        final BmobQuery<UserInformation> query = new BmobQuery<UserInformation>();
        query.addWhereEqualTo("phone", userNameOrPhone);
        query.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object, BmobException e) {
                if(e==null){
                    UserInformation user =object.get(0);
                    Toast.makeText(getApplicationContext(), user.getNickName()+"查询成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "查询失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
