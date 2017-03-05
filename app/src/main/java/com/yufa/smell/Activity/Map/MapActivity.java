package com.yufa.smell.Activity.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.yufa.smell.Activity.ChatCenter.FriendListActivity;
import com.yufa.smell.Activity.ChatCenter.ViewPaperActivity;
import com.yufa.smell.Activity.LoginAndRegister.LoginActivity;
import com.yufa.smell.Activity.SettingCenter.SettingActivity;
import com.yufa.smell.Entity.MenuItem;
import com.yufa.smell.Entity.Smell;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.Util.BitmapAdd;
import com.yufa.smell.Util.MenuShow;
import com.yufa.smell.R;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static cn.bmob.v3.BmobConstants.TAG;
import static com.amap.api.maps2d.AMapOptions.LOGO_POSITION_BOTTOM_RIGHT;

/**
 * AMap地图中简单介绍显示定位小蓝点
 */
public class MapActivity extends Activity implements LocationSource,
        AMapLocationListener,View.OnClickListener {

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation aMapLocation;

    private  Boolean isDraw = true;

    private ImageButton swip;
    //新建菜单显示类对象
    private MenuShow mMenuShow;
    //定义菜单按钮控件
    private RelativeLayout menuBtn;
    //定义菜单列表中的四个Item的名称以及资源
    private MenuItem qiwei,qipao, haoyou, shezhi;
    private String[] MenuItemName = {"气味","气泡","好友","设置"};
    private int[] MenuItemIcon = {R.drawable.ic_speaker_notes_white,
            R.drawable.ic_filter_tilt_shift_white,R.drawable.ic_supervisor_account_white,R.drawable.ic_settings_white};
    private int[] MenuItemIconChange = {R.drawable.ic_speaker_notes_blue,
            R.drawable.ic_filter_tilt_shift_blue,R.drawable.ic_supervisor_account_blue,R.drawable.ic_settings_blue};
    //定义文字颜色的两种变换，未按下是白色，按下是蓝色
    private int textColor = Color.parseColor("#ffffff");
    private int textColorChange = Color.parseColor("#2196f3");
    //定义菜单MSG的信息，按下哪个发送哪条
    private final int QIWEI=0x00,QIPAO=0x01,HAOYOU=0x02,SHEZHI=0x03;
    //定义菜单按钮MSG
    private final int MENU=0x10;
    //菜单列表的布局
    private RelativeLayout menuList;
    //用于恢复被点击按钮的颜色
    //登录用户的融云userID
    private String userID;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //菜单按钮
                case MENU:
                    mMenuShow.showList();
                    break;
                //一级菜单
                case QIWEI:
                    qiwei.setImageViewImg(MenuItemIcon[0]);
                    qiwei.setTextViewColor(textColor);
                    qiwei();
                    break;
                case QIPAO:
                    qipao.setImageViewImg(MenuItemIcon[1]);
                    qipao.setTextViewColor(textColor);
                    qipao();
                    break;
                case HAOYOU:
                    haoyou.setImageViewImg(MenuItemIcon[2]);
                    haoyou.setTextViewColor(textColor);
                    haoyou();
                    break;
                case SHEZHI:
                    shezhi.setImageViewImg(MenuItemIcon[3]);
                    shezhi.setTextViewColor(textColor);
                    shezhi();
                    break;
            }
            super.handleMessage(msg);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RongIM.init(this);//初始化融云服务
        setContentView(R.layout.activity_map);
        Bmob.initialize(this,"f0fc59a153ba369c31798409902688bd");
        initUserToken();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initViews();
    }

    /**
     * 初始化AMap对象
     */
    private void initViews() {
        swip = (ImageButton)findViewById(R.id.swip);
        swip.setOnClickListener(this);
        menuBtn = (RelativeLayout) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(this);
        menuList = (RelativeLayout) findViewById(R.id.menulist);
        //传递菜单列表的对象
        mMenuShow = new MenuShow(menuList);
        menuList.setVisibility(View.INVISIBLE);//先将列表隐藏
        initMenuItem();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoPosition(LOGO_POSITION_BOTTOM_RIGHT);
        uiSettings.setMyLocationButtonEnabled(false);
    }
    //初始化菜单项
    private void initMenuItem(){
        qiwei = (MenuItem) findViewById(R.id.qiwei);
        qiwei.setImageViewImg(MenuItemIcon[0]);
        qiwei.setTextViewText(MenuItemName[0]);
        qiwei.setOnClickListener(this);

        qipao = (MenuItem) findViewById(R.id.qipao);
        qipao.setImageViewImg(MenuItemIcon[1]);
        qipao.setTextViewText(MenuItemName[1]);
        qipao.setOnClickListener(this);

        haoyou = (MenuItem) findViewById(R.id.haoyou);
        haoyou.setImageViewImg(MenuItemIcon[2]);
        haoyou.setTextViewText(MenuItemName[2]);
        haoyou.setOnClickListener(this);

        shezhi = (MenuItem) findViewById(R.id.shezhi);
        shezhi.setImageViewImg(MenuItemIcon[3]);
        shezhi.setTextViewText(MenuItemName[3]);
        shezhi.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    private void qipao(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this,AddChatActivity.class);
        startActivity(intent);
    }
    private void qiwei(){
        Intent intent = new Intent();
        intent.putExtra("Latitude",aMapLocation.getLatitude());
        intent.putExtra("Longitude",aMapLocation.getLongitude());
        intent.setClass(MapActivity.this,AddSmellActivity.class);
        startActivity(intent);
    }
    private void haoyou(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, ViewPaperActivity.class);
        startActivity(intent);
    }
    private void shezhi(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this,SettingActivity.class);
        startActivity(intent);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                if (aMapLocation == null){
                    this.aMapLocation = amapLocation;
                }else {
                    if (amapLocation.getLatitude()-aMapLocation.getLatitude()>0.00005||amapLocation.getLongitude()-aMapLocation.getLongitude()>0.00005){
                        aMapLocation = amapLocation;
                        updateLocation(amapLocation);
                    }
                }

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                updateLocation(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void addCircle(AMapLocation aMapLocation){
        aMap.addCircle(new CircleOptions()
                .center(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()))
                .radius(1000)
                .fillColor(Color.argb(40,0,0,240))
                .strokeColor(Color.argb(20,0,0,220))
                .strokeWidth(10));
        aMap.addCircle(new CircleOptions()
                .center(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()))
                .radius(1100)
                .fillColor(Color.argb(20,0,0,220))
                .strokeColor(Color.argb(10,0,0,200))
                .strokeWidth(80));
    }


    private void addMarks(AMapLocation aMapLocation,String string){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(aMapLocation.getLatitude() + 0.005,aMapLocation.getLongitude()+0.005));
        markerOption.title("气味").snippet(string);
        markerOption.draggable(true);
        BitmapAdd add = new BitmapAdd();
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(add.getBitmap(MapActivity.this)));
//        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(),R.drawable.location_marker)));
        aMap.addMarker(markerOption);
    }
    private void addMarks(double latitude,double longitude,String string){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude +  0.005,longitude+0.005));
        markerOption.title("气味").snippet(string);
        markerOption.draggable(true);
        BitmapAdd add = new BitmapAdd();
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(add.getBitmap(MapActivity.this)));
//        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(),R.drawable.location_marker)));
        aMap.addMarker(markerOption);
    }

    private void getSmell(AMapLocation aMapLocation){
        BmobGeoPoint bmobGeoPoint = new BmobGeoPoint();
        bmobGeoPoint.setLongitude(aMapLocation.getLongitude());
        bmobGeoPoint.setLatitude(aMapLocation.getLatitude());
        BmobQuery<Smell> query = new BmobQuery<Smell>();
        query.addWhereWithinKilometers("point",bmobGeoPoint,50);
        query.findObjects(new FindListener<Smell>() {
            @Override
            public void done(List<Smell> list, BmobException e) {
                if (e==null){
                    for (Smell smell:list) {
                        addMarks(smell.getPoint().getLatitude(),smell.getPoint().getLongitude(),smell.getTxt());
                    }
                }else {
                    Log.d("MapActivity",e.getErrorCode() + ":" + e.getMessage());
                }
            }
        });
    }



    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    private void updateLocation(AMapLocation aMapLocation){
        UserInformation user = new UserInformation();
        user.setLatitude(aMapLocation.getLatitude());
        user.setLongitude(aMapLocation.getLongitude());
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Log.d("MapActivity","update location success");
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuBtn:
                mHandler.sendEmptyMessage(MENU);
                Toast.makeText(getApplicationContext(),"点击获取成功",Toast.LENGTH_SHORT);
                break;
            case R.id.qiwei:
                qiwei.setImageViewImg(MenuItemIconChange[0]);
                qiwei.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(QIWEI,300);
                break;
            case R.id.qipao:
                qipao.setImageViewImg(MenuItemIconChange[1]);
                qipao.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(QIPAO,300);
                break;
            case R.id.haoyou:
                haoyou.setImageViewImg(MenuItemIconChange[2]);
                haoyou.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(HAOYOU,300);
                break;
            case R.id.shezhi:
                shezhi.setImageViewImg(MenuItemIconChange[3]);
                shezhi.setTextViewColor(textColorChange);
                mHandler.sendEmptyMessageDelayed(SHEZHI,300);
                break;
            case R.id.swip:{
                swip();
                break;
            }
        }
    }

    private void swip(){
        Log.d("MapActivity","on click success!");
        if (isDraw){
            isDraw = false;
            if (aMapLocation!=null)
            getSmell(aMapLocation);
        }else {
            isDraw = true;
            //aMap.clear();
        }
    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择您要创建的气泡");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.content_selectmenu,null);
        RadioGroup group = (RadioGroup) view.findViewById(R.id.qipao_group);
        final Intent intent = new Intent();
        intent.setClass(MapActivity.this,AddChatActivity.class);
        intent.putExtra("Latitude",aMapLocation.getLatitude());
        intent.putExtra("Longitude",aMapLocation.getLongitude());
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton button = (RadioButton)view.findViewById(id);
                switch (button.getText().toString()){
                    case "搭建商铺":{
                        intent.setClass(MapActivity.this,AddShopActivity.class);
                        break;
                    }
                    case "搭建摊位":{
                        intent.setClass(MapActivity.this,AddBoothActivity.class);
                        break;
                    }
                    case "搭建活动":{
                        intent.setClass(MapActivity.this,AddActivity.class);
                        break;
                    }
                    case "搭建群聊":{
                        intent.setClass(MapActivity.this,AddChatActivity.class);
                        break;
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
        builder.setView(view);
        builder.create().show();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 获取登录用户的融云token
     */
    private void initUserToken(){
        UserInformation loginUser = BmobUser.getCurrentUser(UserInformation.class);
        userID = loginUser.getPhone();
        connectRongServer(loginUser.getToken());
    }

    /**
     * 初始化登录用户的融云服务
     */
    private void connectRongServer(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                if (userId.equals(userID)){
                    Toast.makeText(MapActivity.this, userID+"成功连接", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MapActivity.this, userID+"连接失败", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
                Toast.makeText(MapActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "connect failure errorCode is : " + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                Toast.makeText(MapActivity.this, "TokenError", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "token is error ,please check token and appkey");
            }
        });

    }
}
