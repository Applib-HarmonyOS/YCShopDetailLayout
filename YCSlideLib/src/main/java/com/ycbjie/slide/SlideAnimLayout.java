package com.ycbjie.slide;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

public class SlideAnimLayout extends ComponentContainer {

    private ValueAnimator animator;

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

    public SlideAnimLayout(Context context) {
        this(context, null);
    }

    public SlideAnimLayout(Context context, AttrSet attrSet) {
        super(context, attrSet);

        if (attrSet != null) {
            mDuration = attrSet.getAttr(DURATION).isPresent() ? attrSet.getAttr(
                    DURATION).get().getIntegerValue() : DEFAULT_DURATION;
            mDefaultPanel = attrSet.getAttr(DEFAULT_PANEL).isPresent() ? attrSet.getAttr(
                    DEFAULT_PANEL).get().getIntegerValue() : 0;
        }

        //TODO : API unavailable
        //mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mTouchSlop = 24;
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
}
