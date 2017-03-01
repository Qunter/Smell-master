package com.yufa.smell.Activity.ChatCenter;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yufa.smell.Activity.BaseFragment;
import com.yufa.smell.Adapter.FragmentAdapter;
import com.yufa.smell.CustomView.CircleView;
import com.yufa.smell.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * MainActivity需要继承FragmentActivity
 */
public class ViewPaperActivity extends BaseFragment {

    @BindView(R.id.friend_image)
    CircleView friendImage;
    @BindView(R.id.friend_name)
    TextView friendName;
    @BindView(R.id.friend_per)
    TextView friendPer;
    @BindView(R.id.friend_add)
    ImageButton friendAdd;
    @BindView(R.id.friendList_persion)
    LinearLayout friendListPersion;
    //Tab显示内容的三个TextView
    @BindView(R.id.xiaoxiTv)
    TextView xiaoxiTv;
    @BindView(R.id.haoyouTv)
    TextView haoyouTv;
    @BindView(R.id.qipaoTv)
    TextView qipaoTv;
    //Tab的那个引导线
    @BindView(R.id.iv_tabline)
    ImageView tablineIv;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    //三个Fragment页面
    private XiaoxiFragment xiaoxiFg;
    private HaoyouFragment haoyouFg;
    private QipaoFragment qiopaoFg;
    //测试!!
    private Fragment XiaoxiList;
    private Fragment XiaoxiFragment = null;
    //反

    //ViewPager的当前选中页
    private int currentIndex;

    //屏幕的宽度
    private int screenWidth;

    @Override
    public void initViews() {
        super.initViews();
        setContentView(R.layout.activity_friendlist);
        ButterKnife.bind(this);
        initXiaoxiFg();
        initViewPaper();
        initTabLineWidth();
    }
    /**
     * 初始化会话列表
     */
    private void initXiaoxiFg(){
        XiaoxiList = initXiaoxiList();
    }

    /**
     * 初始化ViewPager和设置监听器
     */
    private void initViewPaper() {
        //xiaoxiFg = new XiaoxiFragment();
        haoyouFg = new HaoyouFragment();
        qiopaoFg = new QipaoFragment();
        //将三个页面添加到容器里面
        //mFragmentList.add(xiaoxiFg);
        mFragmentList.add(XiaoxiList);
        mFragmentList.add(haoyouFg);
        mFragmentList.add(qiopaoFg);

        //重写一个FragmentAdapter继承FragmentPagerAdapter，需要传FragmentManager和存放页面的容器过去
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        //ViewPager绑定监听器
        viewPager.setAdapter(mFragmentAdapter);
        //ViewPager设置默认当前的项
        viewPager.setCurrentItem(0);
        //ViewPager设置监听器，需要重写onPageScrollStateChanged，onPageScrolled，onPageSelected三个方法
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             * 三个方法的执行顺序为：用手指拖动翻页时，最先执行一遍onPageScrollStateChanged（1），
             * 然后不断执行onPageScrolled，放手指的时候，直接立即执行一次onPageScrollStateChanged（2），
             * 然后立即执行一次onPageSelected，然后再不断执行onPageScrolled，
             * 最后执行一次onPageScrollStateChanged（0）。
             */

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("PageScroll：", "onPageScrollStateChanged" + ":" + state);
            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tablineIv.getLayoutParams();
                Log.i("mOffset", "offset:" + offset + ",position:" + position);
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                }
                tablineIv.setLayoutParams(lp);
            }

            /**
             * 将当前选择的页面的标题设置字体加粗
             */
            @Override
            public void onPageSelected(int position) {
                Log.i("PageScroll：", "onPageSelected" + ":" + position);
                resetTextView();
                switch (position) {
                    case 0:
                        xiaoxiTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        break;
                    case 1:
                        haoyouTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        break;
                    case 2:
                        qipaoTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        break;
                }
                currentIndex = position;
            }
        });

    }

    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tablineIv.getLayoutParams();
        lp.width = screenWidth / 3;
        tablineIv.setLayoutParams(lp);
    }

    /**
     * 重置字体
     */
    private void resetTextView() {
        xiaoxiTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        haoyouTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        qipaoTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    @OnClick({R.id.friend_add, R.id.friendList_persion})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_add:
                break;
            case R.id.friendList_persion:
                break;
        }
    }

    private Fragment initXiaoxiList() {

        /**
         * appendQueryParameter对具体的会话列表做展示
         */
        if (XiaoxiFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationList")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    // .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                    //.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务号
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置私聊会是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return XiaoxiFragment;
        }
    }

}
