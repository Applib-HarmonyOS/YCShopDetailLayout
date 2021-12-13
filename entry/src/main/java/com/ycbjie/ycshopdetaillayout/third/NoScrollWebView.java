package com.ycbjie.ycshopdetaillayout.third;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.webengine.WebView;
import ohos.app.Context;

public class NoScrollWebView extends WebView implements Component.EstimateSizeListener {

    public NoScrollWebView(Context context) {
        super(context);
        initView();
    }

    public NoScrollWebView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        initView();
    }

    private void initView() {

    }

    @Override
    public boolean onEstimateSize(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = EstimateSpec.getSizeWithMode(Integer.MAX_VALUE >> 2, EstimateSpec.NOT_EXCEED);
        setEstimatedSize(widthMeasureSpec, mExpandSpec);
        return true;
    }
}
