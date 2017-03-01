package com.yufa.smell.Activity.ChatCenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yufa.smell.Activity.ChatCenter.RongTt.Friend;
import com.yufa.smell.Activity.ChatCenter.RongTt.FriendFragment;
import com.yufa.smell.Activity.ChatCenter.RongTt.HomeActivity;
import com.yufa.smell.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import static cn.bmob.v3.BmobConstants.TAG;

public class HaoyouFragment extends Fragment implements View.OnClickListener,RongIM.UserInfoProvider{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    Button btOne;
    Button btTwo;
    View chatView;
    LinearLayout customerBtn;
    private static final String token1 = "dHGn5hbkp2uoaGNdM/ndPLU/IPKPq/4/rzu3rTMXUSRCNEJ9ciLWMJHPIuBZ/kIF0Ei/ZppjqKA82Y/G7o2WKw==";
    private static final String token2 = "Ps5gvQxrr/UEeIECd0Kw1zJ7fQsmkAfr2qQ8WWBA7zjOEK9cnoCpmZx1FAJl2gSKy6VzMTjO7eEo3nR7ttm0mA==";
    private static final String kefuToken = "vCYACJZW6N+6n/bWxTKJa7U/IPKPq/4/rzu3rTMXUSRr+45pWdMmjRNZqGa9SzdWUlX6awVGkOS9UH4AMaCELA==";
    private List<Friend> userIdList;


    public static HaoyouFragment instance = null;//单例模式

    public static HaoyouFragment getInstance() {
        if (instance == null) {
            instance = new HaoyouFragment();
        }

        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        chatView = inflater.inflate(R.layout.fragment_haoyou, container,false);
        initUserInfo();
        return chatView;


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        btOne = (Button) chatView.findViewById(R.id.mBtOne);
        btTwo = (Button) chatView.findViewById(R.id.mBtTwo);
        customerBtn = (LinearLayout) chatView.findViewById(R.id.kefuBtn);
        btOne.setOnClickListener(this);
        btTwo.setOnClickListener(this);
        customerBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mBtOne) {
            connectRongServer(token1);
        } else if (v.getId() == R.id.mBtTwo) {
            connectRongServer(token2);
        } else if (v.getId() == R.id.kefuBtn){
            if (RongIM.getInstance()!=null){
                //直接连token然后打开聊天窗
                connectRongServer(token1);
                //RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.APP_PUBLIC_SERVICE, "KEFU", "官方客服");
                //RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.APP_PUBLIC_SERVICE, "10086", "移动");
                RongIM.getInstance().startPrivateChat(getActivity(), "10086", "移动");
            }
        }
    }

    private void connectRongServer(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                if (userId.equals("10010")) {
                    btOne.setText("ONE连接服务器成功");
                    //startActivity(new Intent(getContext(), HomeActivity.class));
                    Toast.makeText(getContext(), "connect server success 10010", Toast.LENGTH_SHORT).show();
                } else {
                    btTwo.setText("TWO连接服务器成功");
                    startActivity(new Intent(getContext(), HomeActivity.class));
                    Toast.makeText(getContext(), "connect server success 10086", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "connect failure errorCode is : " + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                Toast.makeText(getContext(), "TokenError", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "token is error ,please check token and appkey");
            }
        });

    }

    private void initUserInfo() {
        userIdList = new ArrayList<Friend>();
        userIdList.add(new Friend("10010", "联通", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//联通图标
        userIdList.add(new Friend("10086", "移动", "http://bmob-cdn-8854.b0.upaiyun.com/2017/01/21/910615c0405f9bd280350b57f8dc180c.png"));//移动图标
        userIdList.add(new Friend("KEFU","官方客服","http://img02.tooopen.com/Download/2010/5/22/20100522103223994012.jpg"));
        //Token:vCYACJZW6N+6n/bWxTKJa7U/IPKPq/4/rzu3rTMXUSRr+45pWdMmjRNZqGa9SzdWUlX6awVGkOS9UH4AMaCELA==
        RongIM.setUserInfoProvider(this, true);
    }

    @Override
    public UserInfo getUserInfo(String s) {

        for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                Log.e(TAG, i.getPortraitUri());
                return new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
            }
        }


        Log.e("MainActivity", "UserId is : " + s);
        return null;
    }
}
