package com.yxd.mydraglayout;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 创建时间：2020/7/20
 * 编写人：czy_yangxudong
 * 功能描述：自定义view DragLayout 实现侧滑缩放效果
 */
public class DragLayout extends FrameLayout {

    /**
     * ViewDragHelper 的使用
     * 1 得到ViewDragHelper的实例
     * 2 要把触摸事件交给ViewDragHelper处理 （OnTouchEvent,onIntercepteTouchEvent）
     * 3 由ViewDragHelper处理了，但是还是需要我们自己做决定---实现 Callback
     *
     * @param context
     * @param attrs
     */

    private ViewDragHelper viewDragHelper;
    private View mainView;
    private View leftView;
    private int leftWidth;
    private FloatEvaluator floatEvaluator;

    //临界速度
    private  float criticalVel;
    //mainView滑动的百分比
    private float precent=0;

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this,callback);

        //小数估值器
        floatEvaluator = new FloatEvaluator();

        //此速度等价于1秒钟划过一英寸屏幕
        criticalVel=getResources().getDisplayMetrics().density*160;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //固定写法          是否应该拦截触摸事件
        if (viewDragHelper.shouldInterceptTouchEvent(ev)){
            return true;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件交由viewDragHelper处理
        viewDragHelper.processTouchEvent(event);
        //表示始终关注触摸事件
        return true;
        //return super.onTouchEvent(event);
    }


    //布局填充结束的时候调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() < 2) {
            throw new IllegalStateException("这个控件必须要两个布局");
        }

        mainView = getChildAt(0);
        leftView = getChildAt(1);

        //把主布局拿到前面
        //mainView.bringToFront();
        bringChildToFront(mainView);


        leftView.post(new Runnable() {
            @Override
            public void run() {
                leftWidth = leftView.getMeasuredWidth();
                Log.i("test","leftWith="+leftWidth);
            }
        });
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {

        /**
         * 尝试捕获view
         * @param child   手指对应的view
         * @param pointerId  跟多点触摸有关，手指的id
         * @return 表示child具备移动的可能性
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        /**
         * 当tryCaptureView返回true的时候调用
         *
         * @param capturedChild 被捕获的孩子
         * @param activePointerId  跟多点触摸有关，手指的id
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         *一般返回正数即可
         * @param child 哪个view
         * @return   水平方向拖拽范围有多大
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }

        /**
         *一般返回正数即可
         * @param child 哪个view
         * @return   垂直方向拖拽范围有多大
         */
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 1;
        }

        /**
         * 当tryCaptureView返回true后，手指又移动了，将会产生让view移动，通过此方法控制view的移动
         * 这个方法的重要作用：1 控制view的移动范围，2 可以添加阻力效果
         * @param child  被捕获的view
         * @param left  建议值：view的left边的建议值=child.getLeft()+dx，，，child.getLeft()获取的是此view的上一个left值
         * @param dx  手指位置的变化量
         * @return  child的left边的位置
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

            if (child==mainView){
                if (left>leftWidth){
                    left=leftWidth;
                }else if(left<0){
                    left=0;
                }
            }/*else if(child==leftView){
                //Log.i("test","leftView="+leftView.getLeft());
                if (left>0){
                    left=0;
                }else if(left<-leftWidth){
                    left=-leftWidth;
                }
            }*/

            return left;
        }

        //同上 垂直方向
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            //top=0;
            //让控件在垂直方向上不发生位移 效果等同于top返回0
            return top-dy;
        }

        /**
         * ViewDragHelper会根据clampViewPositionHorizontal和clampViewPositionVertical的返回值
         * 对view进行移动操作，如果移动了 此方法就会被调用，一般用来处理相关操作（导致其他变化发生的），比如两个view联动
         * 注意：此回调的 dx 和 dy 和上面的回调的 dx 和 dy 不一样，上面的 dx 和 dy 表示的是手指的移动的变化量
         * @param changedView  位置发生变化的view
         * @param left    changedView位置变化后的left  相当于changedView.getLeft()+dx
         * @param top     changedView位置变化后的top  相当于changedView.getTop()+dy
         * @param dx      changedView的left边的变化的值
         * @param dy      changedView的top边的变化的值
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {

            //让mainView与leftView联动
            /*if (changedView==mainView){
                //leftView.offsetLeftAndRight(dx);

                //获取mainView滑动的百分比 用于控制透明度
                 precent=1.0f*mainView.getLeft()/leftWidth;

                //Log.i("test","precent="+precent);

            }else*/ if (changedView==leftView){

                if (mainView.getLeft()+dx<=leftWidth&&mainView.getLeft()+dx>=0){
                    mainView.offsetLeftAndRight(dx);
                }
                //让leftview不跟随手指移动 而跟随mainView联动
                leftView.offsetLeftAndRight(-dx);

               // precent=1-(1.0f*Math.abs(leftView.getLeft()) /(leftWidth/2));

                //Log.i("test","leftView.getLeft()="+leftView.getLeft()+"---leftWidth="+leftWidth+"---precent="+precent);


            }

            precent =1.0f*mainView.getLeft()/leftWidth;

            //Log.i("test","precent="+ precent);

            dispatchAnimation(precent);

            upState(DragState.DRAGING);
            if (mOnDragLayoutStateChangedListening!=null){
                mOnDragLayoutStateChangedListening.OnDrag(precent);
            }
        }


        //当拖拽状态发生变化的时候回调 状态有三种：空闲、拖拽、自动化
        @Override
        public void onViewDragStateChanged(int state) {
            if (state== ViewDragHelper.STATE_IDLE){
                //当空闲下来的时候 要不是开的  要不是关的
                if (mainView.getLeft()==0){
                    //关的
                    upState(DragState.CLOSE);
                }else if (mainView.getLeft()==leftWidth){
                    //开的
                    upState(DragState.OPEN);
                }

            }
            super.onViewDragStateChanged(state);
        }




        /**
         * 当手指离开屏幕的时候回调
         *
         * @param releasedChild  被释放的view
         * @param xvel   手指离开屏幕时候的水平方向的速度  单位是 像素/秒
         * @param yvel   手指离开屏幕时候的垂直方向的速度  单位是 像素/秒
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            //判断松手时mainView的left边的位置是偏左（关闭）还是偏右（打开）
            //还需要判断x方向的速度
            //先判断速度
            if (xvel>criticalVel){
                //打开
                open();
            }else if(xvel<-criticalVel){
                //关闭
                close();
            }else{
                if (mainView.getLeft()<leftWidth/2){
                    //关闭
                    close();
                }else{
                    //打开
                    open();
                }
            }

            super.onViewReleased(releasedChild, xvel, yvel);
        }

        //以下3个回调与边缘触摸有关
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }

        //在通过触摸位置，查找x/y对应的view的角标的时候回调，不需要重写
        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

    };

    public void close() {
        /**
         * 固定写法，              是否能够平滑的移动
         * 判断一个view与指定的位置是否有差距
         * child  哪个view
         * finalLeft view的left边的参考位置
         * finalTop  view的top边的参考位置
         * return true表示不在指定位置上 false表示在指定位置上
         */
        if (viewDragHelper.smoothSlideViewTo(mainView,0,0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void open() {
        if (viewDragHelper.smoothSlideViewTo(mainView,leftWidth,0)){
            //在下一帧刷新
            //因为父控件才知道mainView的所在位置 所以由draglayout去重绘
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //固定写法  continueSettling会根据scroller移动一点点距离，如果还没到指定位置那就返回true 反之返回false
        if (viewDragHelper.continueSettling(true)){
            //继续重绘
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private static final float MAIN_MIN_SCALE=0.8f;
    private static final float LEFT_MIN_SCALE=0.8f;
    //处理动画效果  根据mainView滑动的百分比
    private void dispatchAnimation(float precent) {
        //主界面的缩放效果

        //使用属性动画， 属性动画可以中途变化
        //使用小数估值器来估计缩放比例
        float mainScale=floatEvaluator.evaluate(precent,1,MAIN_MIN_SCALE);

        //先指定缩放点 再缩放
        mainView.setPivotX(0);
        //传入的需要是像素值 而不是百分比
        mainView.setPivotY(0.5f*mainView.getHeight());
        mainView.setScaleX(mainScale);
        mainView.setScaleY(mainScale);


        //菜单界面的缩放效果 以及 透明度效果
        float leftScale=floatEvaluator.evaluate(precent,LEFT_MIN_SCALE,1);
        /*//先指定缩放点 再缩放
        leftView.setPivotX(leftView.getWidth());
        //传入的需要是像素值 而不是百分比
        leftView.setPivotY(0.5f*leftView.getHeight());*/

        leftView.setScaleX(leftScale);
        leftView.setScaleY(leftScale);
        leftView.setAlpha(precent);

        float translationX=floatEvaluator.evaluate(precent,-leftWidth/2,0);
        leftView.setTranslationX(translationX);

    }


    public static enum DragState{
        CLOSE,OPEN,DRAGING
    }

    public static interface OnDragLayoutStateChangedListening{
        void OnDragLayoutStateChanged(DragState state);
        //正在被拖拽
        void OnDrag(float precent);
    }
    private  OnDragLayoutStateChangedListening mOnDragLayoutStateChangedListening;

    public void setmOnDragLayoutStateChangedListening(OnDragLayoutStateChangedListening mOnDragLayoutStateChangedListening) {
        this.mOnDragLayoutStateChangedListening = mOnDragLayoutStateChangedListening;
    }


    //记录当前的状态
    private DragState currentState= DragState.CLOSE;
    private void upState(DragState state){
        if (currentState==state){
            return;
        }
        currentState=state;
        if (mOnDragLayoutStateChangedListening!=null){
            mOnDragLayoutStateChangedListening.OnDragLayoutStateChanged(state);
        }
    }


    public DragState getCurrentState(){
        return currentState;
    }
}
