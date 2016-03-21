package com.qqfm.jack.qqfmcontentview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.qqfm.jack.qqfmcontentview.util.ObservableScrollView;
import com.qqfm.jack.qqfmcontentview.util.ScreenUtils;
import com.qqfm.jack.qqfmcontentview.util.ScrollViewListener;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements
        View.OnTouchListener,ScrollViewListener {
    private ObservableScrollView mHorizontalScrollView;
    private LinearLayout Contentshow;

    private RelativeLayout contentcontrolshow=null;
    private LinearLayout contentcontrolmenu=null;
    private RelativeLayout contentcontrolbu=null;
    //屏幕的宽
    private int width=0;
    //屏幕的高
    private int height=0;
    //信息view
    private View info;
    //菜单view
    private View menu;
    //控制按钮view
    private View control;
    //半个页面的距离
    private int MenuWidth=0;
    private int ScrollX;
    //显示信息百分比
    private float InfoPercent=0;
    //菜单百分比
    private float MenuPercent=0;
    //底部视图各部分的高度
    private int ShowHeight;
    private int MenuHeight;
    private int ControlHeight;
    //第一次滑动状态
    private boolean FirstScoll=true;
    private boolean InitFirstData=true;

    private Float infoheight=0.0f;
    private Float menuheight=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(InitFirstData){
            width= ScreenUtils.getScreenWidth(this);
            height=ScreenUtils.getViewHeight(this);
            initScrollViewPlace();
            initViewSize();
            InitFirstData=false;
        }
    }

    //初始时移动到详细页面
    public void initScrollViewPlace(){
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.smoothScrollTo(width,0);
                UpdateViewHeight(height/2);
            }
        });
    }

    //初始化视图
    public void initView(){
        try{
            //初始化HorizontalScrollView
            mHorizontalScrollView=(ObservableScrollView)findViewById(R.id.MenuMainScrollView);
            mHorizontalScrollView.setOnTouchListener(this);
            mHorizontalScrollView.setScrollViewListener(this);
            //初始化底部视图
            Contentshow=(LinearLayout)findViewById(R.id.contentshow);
            //初始化contentcontrol的各个部分
            contentcontrolshow=(RelativeLayout)findViewById(R.id.contentcontrol_show);
            contentcontrolmenu=(LinearLayout)findViewById(R.id.contentcontrol_menu);
            contentcontrolbu=(RelativeLayout)findViewById(R.id.controlbutton);
            //初始化滑动的三个视图
            info= LayoutInflater.from(this).inflate(R.layout.contentinfo,null);
            menu= LayoutInflater.from(this).inflate(R.layout.contentmenu, null);
            control=findViewById(R.id.contentcontrolview);
        }catch(Exception e){
            Log.e("mainException",e.getMessage());
        }
    }

    public void initViewSize(){
        //半个页面的距离
        MenuWidth=width/2;
        //为info,menu和control赋值
        LinearLayout.LayoutParams InfoLayoutParams=new LinearLayout.LayoutParams(width,height/2);
        LinearLayout.LayoutParams MenuLayoutParams=new LinearLayout.LayoutParams(width,height);
        info.setLayoutParams(InfoLayoutParams);
        menu.setLayoutParams(MenuLayoutParams);
        control.getLayoutParams().height=height/2;
        Contentshow.addView(menu);
        Contentshow.addView(info);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                Log.e("action_up",event.getRawX()+"");
                if(ScrollX>MenuWidth){
                    mHorizontalScrollView.smoothScrollTo(width,0);
                }else{
                    mHorizontalScrollView.smoothScrollTo(0,0);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void ChangeHeight(View view,int height){
        ViewGroup.LayoutParams layoutParams=view.getLayoutParams();
        layoutParams.height=height;
        view.setLayoutParams(layoutParams);
    }

    public void UpdateViewHeight(int height){
        ViewGroup.LayoutParams layoutParams=control.getLayoutParams();
        layoutParams.height=height;
        control.setLayoutParams(layoutParams);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView,int scrollX,int scrollY,int oldScrollX,int oldScrollY) {
        this.ScrollX=scrollX;
        if(FirstScoll){
            DecimalFormat df=new DecimalFormat("0.00");
            ShowHeight=contentcontrolshow.getHeight();
            MenuHeight=contentcontrolmenu.getHeight();
            ControlHeight=contentcontrolbu.getHeight();
            // info缩小对应的百分比
            InfoPercent=Float.valueOf(df.format((float)width/ShowHeight));
            // menu缩小对应的百分比
            MenuPercent=Float.valueOf(df.format((float)width/MenuHeight));
            FirstScoll=false;
            //按钮保持原来的大小
            ChangeHeight(contentcontrolbu,ControlHeight);
            //保持原样
            ChangeHeight(info,height/2);
            ChangeHeight(menu,height-ControlHeight);
        }
        infoheight=Float.valueOf(scrollX/InfoPercent);
        menuheight=Float.valueOf(scrollX/MenuPercent);
        //更新view的高度
        UpdateViewHeight(infoheight.intValue()+menuheight.intValue()+ControlHeight);
        //info缩小对应的百分比
        ChangeHeight(contentcontrolshow,infoheight.intValue());
        //menu缩小对应的百分比
        ChangeHeight(contentcontrolmenu,menuheight.intValue());
    }
}
