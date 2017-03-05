package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/3/5.
 */

public class DiscussionIfm extends BmobObject {
    private String discussionID;
    private String discussionName;
    private String discussionUrl;

    public String getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(String discussionID) {
        this.discussionID = discussionID;
    }

    public String getDiscussionName() {
        return discussionName;
    }

    public void setDiscussionName(String discussionName) {
        this.discussionName = discussionName;
    }

    public String getDiscussionUrl() {
        return discussionUrl;
    }

    public void setDiscussionUrl(String discussionUrl) {
        this.discussionUrl = discussionUrl;
    }
}
