package com.yufa.smell.Activity.ChatCenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yufa.smell.Activity.ChatCenter.RongTt.Friend;
import com.yufa.smell.Activity.ChatCenter.RongTt.HomeActivity;
import com.yufa.smell.Adapter.FriendAdapter;
import com.yufa.smell.Entity.UserFriend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import static cn.bmob.v3.BmobConstants.TAG;

public class HaoyouFragment extends Fragment implements RongIM.UserInfoProvider{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    Button btOne;
    Button btTwo;
    View chatView;
    LinearLayout customerBtn;
    ListView friendLv;
    List<UserInformation> userFriendInformationList;
    private String loginUserFriendObjectID;
    private static String userID = "";
    private static String userToken = "";
    private static final String token1 = "dHGn5hbkp2uoaGNdM/ndPLU/IPKPq/4/rzu3rTMXUSRCNEJ9ciLWMJHPIuBZ/kIF0Ei/ZppjqKA82Y/G7o2WKw==";
    private static final String token2 = "Ps5gvQxrr/UEeIECd0Kw1zJ7fQsmkAfr2qQ8WWBA7zjOEK9cnoCpmZx1FAJl2gSKy6VzMTjO7eEo3nR7ttm0mA==";
    private static final String kefuToken = "vCYACJZW6N+6n/bWxTKJa7U/IPKPq/4/rzu3rTMXUSRr+45pWdMmjRNZqGa9SzdWUlX6awVGkOS9UH4AMaCELA==";
    private List<Friend> userIdList;
    private final int FRIENDINFORMATIONLISTDOWNOVER=0x05,GETLOGINUSERFRIENDLIST=0x06;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==FRIENDINFORMATIONLISTDOWNOVER){
                initFriendListViewAdapter();
            }else if (msg.what==GETLOGINUSERFRIENDLIST){
                getLoginUserFriendList();
            }

        }
    };

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
        //initUserInfo();
        initUserToken();
        friendLv = (ListView) chatView.findViewById(R.id.friendLv);
        getLoginUserFriendObjectID();
        //initFriendListViewAdapter();
        return chatView;


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        /*
        customerBtn = (LinearLayout) chatView.findViewById(R.id.kefuBtn);
        customerBtn.setOnClickListener(this);
        */
    }
    /*
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mBtOne) {
            connectRongServer(token1);
        } else if (v.getId() == R.id.mBtTwo) {
            connectRongServer(token2);
        } else if (v.getId() == R.id.kefuBtn){
            if (RongIM.getInstance()!=null){
                //RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.APP_PUBLIC_SERVICE, "KEFU", "官方客服");
                //RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.APP_PUBLIC_SERVICE, "10086", "移动");
                RongIM.getInstance().startPrivateChat(getActivity(), "10086", "移动");
            }
        }
    }
    */
    private void initUserToken(){
        UserInformation loginUser = BmobUser.getCurrentUser(UserInformation.class);
        userID = loginUser.getPhone();
        userToken = loginUser.getToken();
        connectRongServer(loginUser.getToken());
    }
    private void connectRongServer(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                if (userId.equals("10010")) {
                    btOne.setText("ONE连接服务器成功");
                    //startActivity(new Intent(getContext(), HomeActivity.class));
                    Toast.makeText(getContext(), "connect server success 10010", Toast.LENGTH_SHORT).show();
                } else if (userId.equals(userID)){
                    Toast.makeText(getContext(), userID+"成功连接", Toast.LENGTH_SHORT).show();
                }else {
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
                //Log.e(TAG, i.getPortraitUri());
                return new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
            }
        }


        Log.e("MainActivity", "UserId is : " + s);
        return null;
    }
    /**
     * 获取用户好友列表的数据库表单中ID
     */
    private void getLoginUserFriendObjectID(){
        final BmobQuery<UserFriend> userObjectIDQuery = new BmobQuery<UserFriend>();
        userObjectIDQuery.addWhereEqualTo("userID", BmobUser.getCurrentUser(UserInformation.class).getPhone());
        userObjectIDQuery.findObjects(new FindListener<UserFriend>() {
            @Override
            public void done(List<UserFriend> object, BmobException e) {
                if(e==null){
                    loginUserFriendObjectID =object.get(0).getObjectId();
                    Toast.makeText(getContext(), "loginUserFriendObjectID"+loginUserFriendObjectID, Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(GETLOGINUSERFRIENDLIST);
                }else{
                }
            }
        });
        /*
        // 查询好友列表内的所有用户，因此查询的是用户表
        BmobQuery<UserInformation> userFriendQuery = new BmobQuery<UserInformation>();
        userFriend.setObjectId(loginUserFriendObjectID);
        //userFriend是UserFriend表中的字段，用来存储所有该用户的好友关系的用户
        userFriendQuery.addWhereRelatedTo("userFriend", new BmobPointer(userFriend));
        userFriendQuery.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object,BmobException e) {
                if(e==null){
                    userFriendInformationList=object;
                    Toast.makeText(getContext(), "成功加载好友列表数据"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(FRIENDINFORMATIONLISTDOWNOVER);
                }else{
                    Toast.makeText(getContext(), e+"失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
        */

    }
    /**
     *获取用户好友列表数据
     */
    private void getLoginUserFriendList(){
        UserFriend userFriend = new UserFriend();
        // 查询好友列表内的所有用户，因此查询的是用户表
        BmobQuery<UserInformation> userFriendQuery = new BmobQuery<UserInformation>();
        userFriend.setObjectId(loginUserFriendObjectID);
        //userFriend是UserFriend表中的字段，用来存储所有该用户的好友关系的用户
        userFriendQuery.addWhereRelatedTo("userFriend", new BmobPointer(userFriend));
        userFriendQuery.findObjects(new FindListener<UserInformation>() {
            @Override
            public void done(List<UserInformation> object,BmobException e) {
                if(e==null){
                    userFriendInformationList=object;
                    Toast.makeText(getContext(), "成功加载好友列表数据"+userFriendInformationList.size(), Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(FRIENDINFORMATIONLISTDOWNOVER);
                }else{
                    Toast.makeText(getContext(), e+"失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    /**
     * 好友列表控件adapter初始化
     */
    private void initFriendListViewAdapter(){
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(), userFriendInformationList, friendLv);
        friendLv.setAdapter(friendAdapter);
    }

}
