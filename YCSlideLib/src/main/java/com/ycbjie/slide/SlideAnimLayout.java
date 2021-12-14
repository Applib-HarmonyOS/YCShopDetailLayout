package com.ycbjie.slide;

import ohos.agp.animation.Animator;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class SlideAnimLayout extends ComponentContainer implements Component.BindStateChangedListener,
        Component.EstimateSizeListener, ComponentContainer.ArrangeListener, Component.TouchEventListener,
        Component.LayoutRefreshedListener {

    private ValueAnimator animator;

    @Override
    public void onRefreshed(Component component) {
        onFinishInflate();
    }


   /* public void onRefreshed(Component component) {
        onFinishInflate();
    }*/

    public enum Status {
        /**
         * 关闭
         */
        CLOSE,
        /**
         * 打开
         */
        OPEN;
        public static Status valueOf(int stats) {
            if (0 == stats) {
                return CLOSE;
            } else if (1 == stats) {
                return OPEN;
            } else {
                return CLOSE;
            }
        }
    }

    private static final int DEFAULT_DURATION = 300;
    private static final String DURATION = "duration";
    private static final String DEFAULT_PANEL = "default_panel";
    private Component mFrontView;
    private Component mAnimView;
    private Component mBehindView;

    private float mTouchSlop;
    private float mInitMotionY;
    private float mInitMotionX;


    private Component mTarget;
    private float mSlideOffset;
    private Status mStatus = Status.CLOSE;
    private boolean isFirstShowBehindView = true;
    private long mDuration = DEFAULT_DURATION;
    private int mDefaultPanel = 0;
    private int animHeight;
    private EventHandler mEventHandler = new EventHandler(EventRunner.getMainEventRunner());

    public SlideAnimLayout(Context context) {
        this(context, null);
    }

    public SlideAnimLayout(Context context, AttrSet attrSet) {
        super(context, attrSet);

        LogUtil.debug("Gowtham", "Inside Constructor : ");

        if (attrSet != null) {
            mDuration = attrSet.getAttr(DURATION).isPresent() ? attrSet.getAttr(
                    DURATION).get().getIntegerValue() : DEFAULT_DURATION;
            mDefaultPanel = attrSet.getAttr(DEFAULT_PANEL).isPresent() ? attrSet.getAttr(
                    DEFAULT_PANEL).get().getIntegerValue() : 0;
        }

        //TODO : API unavailable
        //mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mTouchSlop = 24;

        setBindStateChangedListener(this);
        setEstimateSizeListener(this);
        setArrangeListener(this);
        setTouchEventListener(this);
        setLayoutRefreshedListener(this);
    }

    /**
     * 打开商详页
     * @param smooth
     */
    public void smoothOpen(boolean smooth) {
        if (mStatus != Status.OPEN) {
            mStatus = Status.OPEN;
            //控件的高度+动画布局
            final float height = -getEstimatedHeight() - animHeight;
            LoggerUtils.i("SlideLayout---smoothOpen---", ""+height);
            animatorSwitch(0, height, true, smooth ? mDuration : 0);
        }
    }

    /**
     * 关闭商详页
     * @param smooth
     */
    public void smoothClose(boolean smooth) {
        if (mStatus != Status.CLOSE) {
            mStatus = Status.CLOSE;
            final float height = -getEstimatedHeight();
            LoggerUtils.i("SlideLayout---smoothClose---",""+height);
            animatorSwitch(height, 0, true, smooth ? mDuration : 0);
        }
    }

    private void onFinishInflate() {
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept child more than 1!!");
        }

        LoggerUtils.i("获取子节点的个数",""+childCount);
        mFrontView = getComponentAt(0);
        mAnimView = getComponentAt(1);
        mBehindView = getComponentAt(2);

        mEventHandler.postTask(() -> {
            animHeight = mAnimView.getHeight();
            LoggerUtils.i("获取控件高度",""+animHeight);
        });


        if(mDefaultPanel == 1){
            mEventHandler.postTask(() -> smoothOpen(false));
        }
    }

    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {
        setScrollStatusListener(null);
        setOnSlideStatusListener(null);
        if (animator!=null){
            animator.cancel();
            animator = null;
        }
    }

    @Override
    public boolean onEstimateSize(int widthEstimateConfig, int heightEstimateConfig) {
        final int pWidth = EstimateSpec.getSize(widthEstimateConfig);
        final int pHeight = EstimateSpec.getSize(heightEstimateConfig);
        int childWidthMeasureSpec = EstimateSpec.getSizeWithMode(pWidth, EstimateSpec.PRECISE);
        int childHeightMeasureSpec = EstimateSpec.getSizeWithMode(pHeight, EstimateSpec.PRECISE);
        Component child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getComponentAt(i);
            //当控件是Gone的时候，不进行测量
            if (child.getVisibility() == Component.INVISIBLE) {
                continue;
            }
            //当孩子控件是动画控件时，则特殊处理
            if(getComponentAt(i) == mAnimView){
                child.estimateSize(0,0);
                int measuredHeight = child.getEstimatedHeight();
                int makeMeasureSpec = EstimateSpec.getSizeWithMode(measuredHeight, EstimateSpec.PRECISE);
                LogUtil.info("onMeasure获取控件高度",""+measuredHeight);
                child.estimateSize(childWidthMeasureSpec, makeMeasureSpec);
            } else{
                child.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        setEstimatedSize(childWidthMeasureSpec, childHeightMeasureSpec);
        return true;
    }

    @Override
    public boolean onArrange(int l, int t, int r, int b) {
        int top;
        int bottom;
        final int offset = (int) mSlideOffset;
        Component child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getComponentAt(i);
            if (child.getVisibility() == INVISIBLE) {
                continue;
            }
            LoggerUtils.i("onLayout，offset---",""+offset);
            int measuredHeight = getComponentAt(1).getEstimatedHeight();
            if (child == mBehindView) {
                top = b + offset + measuredHeight ;
                bottom = top + b - t + measuredHeight;
                LoggerUtils.i("onLayout，mBehindView---",""+top+"-----"+bottom);
            }else if(child == mAnimView){
                top = b + offset;
                bottom = top - t + child.getEstimatedHeight();
                LoggerUtils.i("onLayout，mAnimView---",""+top+"-----"+bottom);
            } else {
                top = t + offset;
                bottom = b + offset;
                LoggerUtils.i("onLayout，other---",""+top+"-----"+bottom);
            }
            child.arrange(l, top, r, bottom);
        }
        return true;
    }

    //TODO : API unavailable
    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (null == mTarget) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        final int action = ev.getAction();
        boolean shouldIntercept = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitMotionX = ev.getX();
                mInitMotionY = ev.getY();
                shouldIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();
                final float xDiff = x - mInitMotionX;
                final float yDiff = y - mInitMotionY;
                boolean close = mStatus == Status.CLOSE && yDiff > 0;
                boolean open = mStatus == Status.OPEN && yDiff < 0;
                if (!canChildScrollVertically((int) yDiff)) {
                    final float xDiffers = Math.abs(xDiff);
                    final float yDiffers = Math.abs(yDiff);
                    if (yDiffers > mTouchSlop && yDiffers >= xDiffers && !(close || open)) {
                        shouldIntercept = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return shouldIntercept;
    }*/

    @Override
    public boolean onTouchEvent(Component component, TouchEvent event) {
        if (null == mTarget) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        boolean wantTouch = true;
        final int action = event.getAction();
        int actionIndex = event.getIndex();
        int index = event.getPointerId(actionIndex);
        MmiPoint point1 = event.getPointerPosition(index);
        switch (action) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                break;
            case TouchEvent.POINT_MOVE:
                final float y = point1.getY();
                final float yDiff = y - mInitMotionY;
                boolean childScrollVertically = canChildScrollVertically(((int) yDiff));
                //在关闭状态并且滑动位移小于等于0时
                boolean isDiffZero = yDiff<=0 && Status.OPEN == mStatus;
                boolean isAnimOpen = Status.OPEN == mStatus && yDiff>=animHeight;
                boolean isAnimClose = Status.CLOSE == mStatus && Math.abs(yDiff)>=animHeight;
                if (childScrollVertically  || isDiffZero) {
                    wantTouch = false;
                }else if(isAnimOpen|| isAnimClose){
                    wantTouch = true;
                } else {
                    processTouchEvent(yDiff);
                    wantTouch = true;
                }
                break;
            case TouchEvent.PRIMARY_POINT_UP:
            case TouchEvent.CANCEL:
                finishTouchEvent();
                wantTouch = false;
                break;
            default:
                break;
        }
        return wantTouch;
    }

    /**
     * 设置方法是触摸滑动的时候
     * @param offset                        offset
     */
    private void processTouchEvent(final float offset) {
        if (Math.abs(offset) < mTouchSlop) {
            return;
        }
        final float oldOffset = mSlideOffset;
        if (mStatus == Status.CLOSE) {
            if (offset >= 0) {
                mSlideOffset = 0;
            } else {
                mSlideOffset = offset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        } else if (mStatus == Status.OPEN) {
            final float pHeight = -getEstimatedHeight();
            if (offset <= 0) {
                mSlideOffset = pHeight;
            } else {
                mSlideOffset = pHeight- animHeight + offset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        }

        if (Status.CLOSE == mStatus) {
            if (offset <= -animHeight/2) {
                LoggerUtils.i("准备翻下页，已超过一半", "");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
            } else {
                LoggerUtils.i("准备翻下页，不超过一半","");
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset ) >= animHeight/2) {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, false);
                }
                LoggerUtils.i("准备翻上页，已超过一半:offset:",""+offset+"--->pHeight:"+"--->:"+animHeight);
            } else {
                if(listener!=null){
                    listener.onStatusChanged(mStatus, true);
                }
                LoggerUtils.i("准备翻上页，不超过一半",""+offset+"--->pHeight:"+"--->:"+animHeight);
            }
        }
        postLayout();
    }

    /**
     * 结束触摸
     */
    private void finishTouchEvent() {
        final int pHeight = getEstimatedHeight();
        LoggerUtils.i("finishTouchEvent------pHeight---",""+pHeight);
        final float offset = mSlideOffset;
        boolean changed = false;
        if (Status.CLOSE == mStatus) {
            if (offset <= -animHeight /2) {
                mSlideOffset = -pHeight - animHeight;
                mStatus = Status.OPEN;
                changed = true;
            } else {
                mSlideOffset = 0;
            }
            LoggerUtils.i("finishTouchEvent----CLOSE--mSlideOffset---",""+mSlideOffset);
        } else if (Status.OPEN == mStatus) {
            if ((offset + pHeight) >= -animHeight/2) {
                mSlideOffset = 0;
                mStatus = Status.CLOSE;
                changed = true;
            } else {
                mSlideOffset = -pHeight - animHeight;
            }
            LoggerUtils.i("finishTouchEvent----OPEN-----",""+mSlideOffset);
        }
        animatorSwitch(offset, mSlideOffset, changed);
    }

    /**
     * 共同调用的方法
     */
    private void animatorSwitch(final float start, final float end, final boolean changed) {
        animatorSwitch(start, end, changed, mDuration);
    }

    private void animatorSwitch(final float start, final float end,
                                final boolean changed, final long duration) {
        animator = ValueAnimator.ofFloat(start, end);

        animator.setValueUpdateListener((animatorValue, v) -> {
            mSlideOffset = v;
            postLayout();
        });

        animator.setStateChangedListener(new Animator.StateChangedListener() {
            @Override
            public void onStart(Animator animator) {

            }

            @Override
            public void onStop(Animator animator) {

            }

            @Override
            public void onCancel(Animator animator) {

            }

            @Override
            public void onEnd(Animator animator) {
                if (changed) {
                    if (mStatus == Status.OPEN && isFirstShowBehindView) {
                        isFirstShowBehindView = false;
                        mBehindView.setVisibility(VISIBLE);
                    }
                    if (onSlideStatusListener!=null){
                        onSlideStatusListener.onStatusChanged(mStatus);
                    }
                }
            }

            @Override
            public void onPause(Animator animator) {

            }

            @Override
            public void onResume(Animator animator) {

            }
        });

        animator.setDuration(duration);
        animator.start();
    }


    private void ensureTarget() {
        if (mStatus == Status.CLOSE) {
            mTarget = mFrontView;
        } else {
            mTarget = mBehindView;
        }
    }

    /**
     * 是否可以滑动，direction为负数时表示向下滑动，反之表示向上滑动。
     * @param direction                         direction
     * @return
     */
       protected boolean canChildScrollVertically(int direction) {
        if (mTarget instanceof ListContainer) {
            return canListViewScroll((ListContainer) mTarget);
        } else if (mTarget instanceof StackLayout || mTarget instanceof DependentLayout ||
                mTarget instanceof DirectionalLayout) {
            Component child;
            for (int i = 0; i < ((ComponentContainer) mTarget).getChildCount(); i++) {
                child = ((ComponentContainer) mTarget).getComponentAt(i);
                if (child instanceof ListContainer) {
                    return canListViewScroll((ListContainer) child);
                }
            }
        }
        return mTarget.canScroll(-direction);
    }

    protected boolean canListViewScroll(ListContainer absListView) {
        if (mStatus == Status.OPEN) {
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisibleItemPosition() > 0
                    || absListView.getComponentAt(0).getTop() < absListView.getPaddingTop());
        } else {
            final int count = absListView.getChildCount();
            return count > 0
                    && (absListView.getLastVisibleItemPosition() < count - 1
                    || absListView.getComponentAt(count - 1).getBottom() > absListView.getEstimatedHeight());
        }
    }

    public interface onScrollStatusListener{
        /**
         * 监听方法
         * @param status            状态
         * @param isHalf            是否是一半距离
         */
        void onStatusChanged(Status status, boolean isHalf);
    }

    private onScrollStatusListener listener;

    public void setScrollStatusListener(onScrollStatusListener listener){
        this.listener = listener;
    }

    private OnSlideStatusListener onSlideStatusListener;
    public interface OnSlideStatusListener {
        void onStatusChanged(Status status);
    }
    public void setOnSlideStatusListener(OnSlideStatusListener listener){
        this.onSlideStatusListener = listener;
    }
}
