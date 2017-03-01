package com.yufa.smell.Entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2017/2/3.
 * 气味
 */

public class Smell extends BmobObject {
    private String txt;
    private String url;
    private float time;
    private BmobFile image;

    private BmobGeoPoint point;

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }
}
