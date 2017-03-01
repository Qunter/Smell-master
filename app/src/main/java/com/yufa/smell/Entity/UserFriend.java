package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/2/23.
 */

public class UserFriend extends BmobObject {
    private UserInformation userIF;
    private BmobRelation userFriend;


    public BmobRelation getUserFriend() {
        return userFriend;
    }

    public void setUserFriend(BmobRelation userFriend) {
        this.userFriend = userFriend;
    }

    public UserInformation getUserIF() {
        return userIF;
    }

    public void setUserIF(UserInformation userIF) {
        this.userIF = userIF;
    }
}
