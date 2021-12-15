package com.ycbjie.slide;

import ohos.agp.components.AttrSet;
import ohos.agp.components.ScrollView;
import ohos.app.Context;

public class VerticalScrollView extends ScrollView {

    public VerticalScrollView(Context context) {
        this(context, null);
    }

    public VerticalScrollView(Context context, AttrSet attrSet) {
        super(context, attrSet);
    }

    private boolean isTop() {
        return !canScroll(DRAG_UP);
    }

    private boolean isBottom() {
        return !canScroll(DRAG_DOWN);
    }

    //TODO : dispatchTouch eventAPI not available in current API release

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                //如果滑动到了最底部，就允许继续向上滑动加载下一页，否者不允许
                //如果子节点不希望父进程拦截触摸事件，则为true。
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                boolean allowParentTouchEvent;
                if (Math.abs(dy) > Math.abs(dx)) {
                    if (dy > 0) {
                        //位于顶部时下拉，让父View消费事件
                        allowParentTouchEvent = isTop();
                    } else {
                        //位于底部时上拉，让父View消费事件
                        allowParentTouchEvent = isBottom();
                    }
                } else {
                    //水平方向滑动
                    allowParentTouchEvent = true;
                }
                getParent().requestDisallowInterceptTouchEvent(!allowParentTouchEvent);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }*/
}
