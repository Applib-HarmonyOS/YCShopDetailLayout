package com.ycbjie.slide;

import ohos.agp.animation.Animator;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class SlideLayout extends ComponentContainer implements Component.EstimateSizeListener,
        ComponentContainer.ArrangeListener, Component.TouchEventListener {

    public static final String TAG = SlideLayout.class.getCanonicalName();
    private static final String PERCENT = "percent";
    private static final String DURATION = "duration";
    private static final String DEFAULT_PANEL = "default_panel";
    private static final float DEFAULT_PERCENT = 0.2f;
    private static final int DEFAULT_DURATION = 300;

    private Component mFrontView;
    private Component mBehindView;
    private Component mTarget;
    private float mTouchSlop;
    private float mInitMotionY;
    private float mInitMotionX;
    private float mSlideOffset;
    private Status mStatus = Status.CLOSE;
    private boolean isFirstShowBehindView = true;
    private float mPercent = DEFAULT_PERCENT;
    private long mDuration = DEFAULT_DURATION;
    private int mDefaultPanel = 0;
    private EventHandler mEventHandler = new EventHandler(EventRunner.getMainEventRunner());


    /**
     * 状态，使用枚举
     */
    public enum Status {
        CLOSE,
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

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttrSet attrSet) {
        super(context, attrSet);


        if (attrSet != null) {
            mPercent = attrSet.getAttr(PERCENT).isPresent() ? attrSet.getAttr(
                    PERCENT).get().getDimensionValue() : DEFAULT_PERCENT;
            mDuration = attrSet.getAttr(DURATION).isPresent() ? attrSet.getAttr(
                    DURATION).get().getIntegerValue() : DEFAULT_DURATION;
            mDefaultPanel = attrSet.getAttr(DEFAULT_PANEL).isPresent() ? attrSet.getAttr(
                    DEFAULT_PANEL).get().getIntegerValue() : 0;
        }

        //TODO : API unavailable
        //mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mTouchSlop = 24;

        setEstimateSizeListener(this);
        setArrangeListener(this);
        setTouchEventListener(this);
        setLayoutRefreshedListener(component -> onFinishInflate());
    }

    private void onFinishInflate() {
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept child more than 1!!");
        }
        mFrontView = getComponentAt(0);
        mBehindView = getComponentAt(1);
        if (mDefaultPanel == 1) {
            mEventHandler.postTask(() -> smoothOpen(false));
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
            if (child.getVisibility() == INVISIBLE) {
                continue;
            }
            child.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
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
            if (child == mBehindView) {
                top = b + offset;
                bottom = top + b - t;
            } else {
                top = t + offset;
                bottom = b + offset;
            }
            child.arrange(l, top, r, bottom);
        }
        return true;
    }

    //TODO : API unavailable
   /* public boolean onInterceptTouchEvent(MotionEvent ev) {
   LogUtil.debug("Gowtham", "onInterceptTouchEvent : ");
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
            case MotionEvent.ACTION_DOWN: {
                mInitMotionX = ev.getX();
                mInitMotionY = ev.getY();
                shouldIntercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float xDiff = x - mInitMotionX;
                final float yDiff = y - mInitMotionY;
                boolean close = mStatus == SlideLayout.Status.CLOSE && yDiff > 0;
                boolean open = mStatus == SlideLayout.Status.OPEN && yDiff < 0;
                if (!canChildScrollVertically((int) yDiff)) {
                    final float xDiffers = Math.abs(xDiff);
                    final float yDiffers = Math.abs(yDiff);
                    if (yDiffers > mTouchSlop && yDiffers >= xDiffers && !(close || open)) {
                        shouldIntercept = true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                shouldIntercept = false;
                break;
            }
            default:
                break;
        }
        return shouldIntercept;
    }*/

    @Override
    public boolean onTouchEvent(Component component, TouchEvent event) {
        ensureTarget();
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
                if (mTarget instanceof Component) {
                    wantTouch = true;
                }
                break;
            case TouchEvent.POINT_MOVE:
                final float y = point1.getY();
                final float yDiff = y - mInitMotionY;
                if (canChildScrollVertically(((int) yDiff))) {
                    wantTouch = false;
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
                final float newOffset = pHeight + offset;
                mSlideOffset = newOffset;
            }
            if (mSlideOffset == oldOffset) {
                return;
            }
        }
        postLayout();
    }


    private void finishTouchEvent() {
        final int pHeight = getEstimatedHeight();
        final int percent = (int) (pHeight * mPercent);
        final float offset = mSlideOffset;
        boolean changed = false;
        if (Status.CLOSE == mStatus) {
            if (offset <= -percent) {
                mSlideOffset = -pHeight;
                mStatus = Status.OPEN;
                changed = true;
            } else {
                mSlideOffset = 0;
            }
        } else if (Status.OPEN == mStatus) {
            if ((offset + pHeight) >= percent) {
                mSlideOffset = 0;
                mStatus = Status.CLOSE;
                changed = true;
            } else {
                mSlideOffset = -pHeight;
            }
        }
        animatorSwitch(offset, mSlideOffset, changed);
    }

    private void animatorSwitch(final float start, final float end, final boolean changed) {
        animatorSwitch(start, end, changed, mDuration);
    }

    private void animatorSwitch(final float start, final float end, final boolean changed, final long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.setValueUpdateListener((animatorValue, v) -> {
            mSlideOffset = v;
            postLayout();
        });

        animator.setStateChangedListener(new Animator.StateChangedListener() {
            @Override
            public void onStart(Animator animator) {
                //Do Nothing
            }

            @Override
            public void onStop(Animator animator) {
                //Do Nothing
            }

            @Override
            public void onCancel(Animator animator) {
                //Do Nothing
            }

            @Override
            public void onEnd(Animator animator) {
                if (changed) {
                    if (mStatus == Status.OPEN) {
                        checkAndFirstOpenPanel();
                    }

                    if (null != mOnSlideDetailsListener) {
                        mOnSlideDetailsListener.onStatusChanged(mStatus);
                    }
                }
            }

            @Override
            public void onPause(Animator animator) {
                //Do Nothing
            }

            @Override
            public void onResume(Animator animator) {
                //Do Nothing
            }
        });


        animator.setDuration(duration);
        animator.start();
    }

    private void checkAndFirstOpenPanel() {
        if (isFirstShowBehindView) {
            isFirstShowBehindView = false;
            mBehindView.setVisibility(VISIBLE);
        }
    }

    private void ensureTarget() {
        if (mStatus == Status.CLOSE) {
            mTarget = mFrontView;
        } else {
            mTarget = mBehindView;
        }
    }

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

    /*------------------------------------回调接口------------------------------------------------*/
    private OnSlideDetailsListener mOnSlideDetailsListener;

    public interface OnSlideDetailsListener {
        void onStatusChanged(Status status);
    }

    public void setOnSlideDetailsListener(OnSlideDetailsListener listener) {
        this.mOnSlideDetailsListener = listener;
    }


    /*------------------------------------相关方法------------------------------------------------*/
    public void smoothOpen(boolean smooth) {
        if (mStatus != Status.OPEN) {
            mStatus = Status.OPEN;
            final float height = -getEstimatedWidth();
            animatorSwitch(0, height, true, smooth ? mDuration : 0);
        }
    }


    public void smoothClose(boolean smooth) {
        if (mStatus != Status.CLOSE) {
            mStatus = Status.CLOSE;
            final float height = -getEstimatedHeight();
            animatorSwitch(height, 0, true, smooth ? mDuration : 0);
        }
    }

    public void setPercent(float percent) {
        this.mPercent = percent;
    }
}
