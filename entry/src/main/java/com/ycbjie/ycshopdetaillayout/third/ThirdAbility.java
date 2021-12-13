package com.ycbjie.ycshopdetaillayout.third;

import com.ycbjie.slide.LogUtil;
import com.ycbjie.slide.SlideLayout;
import com.ycbjie.ycshopdetaillayout.ResourceTable;
import com.ycbjie.ycshopdetaillayout.first.ShopMainFragment;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.ability.fraction.FractionManager;
import ohos.aafwk.ability.fraction.FractionScheduler;
import ohos.aafwk.content.Intent;
import ohos.agp.components.webengine.ResourceRequest;
import ohos.agp.components.webengine.WebAgent;
import ohos.agp.components.webengine.WebConfig;
import ohos.agp.components.webengine.WebView;

public class ThirdAbility extends FractionAbility {

    private SlideLayout mSlideDetailsLayout;
    private ShopMainFragment shopMainFragment;
    private WebView webView;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_third);
        initView();
        initShopMainFragment();
        initSlideDetailsLayout();
        initWebView();

    }

    private void initView() {
        mSlideDetailsLayout = (SlideLayout) findComponentById(ResourceTable.Id_slideDetailsLayout);
        webView = (WebView) findComponentById(ResourceTable.Id_wb_view);
    }

    private void initShopMainFragment() {
        FractionManager fm = getFractionManager();
        FractionScheduler fragmentTransaction = fm.startFractionScheduler();
        if(shopMainFragment==null){
            shopMainFragment = new ShopMainFragment();
            fragmentTransaction
                    .replace(ResourceTable.Id_fl_shop_main, shopMainFragment)
                    .submit();
        }else {
            fragmentTransaction.show(shopMainFragment);
        }
    }

    private void initSlideDetailsLayout() {
        mSlideDetailsLayout.setOnSlideDetailsListener(new SlideLayout.OnSlideDetailsListener() {
            @Override
            public void onStatusChanged(SlideLayout.Status status) {
                if (status == SlideLayout.Status.OPEN) {
                    //当前为图文详情页
                    LogUtil.error("ThirdAbility","下拉回到商品详情");
                    shopMainFragment.changBottomView(true);
                } else {
                    //当前为商品详情页
                    LogUtil.error("ThirdAbility","继续上拉，查看图文详情");
                    shopMainFragment.changBottomView(false);
                }
            }
        });
    }

    private void initWebView() {
        if(webView != null){
            final WebConfig settings = webView.getWebConfig();
            settings.setJavaScriptPermit(true);
            settings.setViewPortFitScreen(true);
            settings.setWebStoragePermit(true);

            webView.setWebAgent(new WebAgent(){
                @Override
                public boolean isNeedLoadUrl(WebView webView, ResourceRequest request) {
                    webView.load(request.getRequestUrl().toString());
                    return true;
                }
            });

            getUITaskDispatcher().asyncDispatch(new Runnable() {
                @Override
                public void run() {
                    webView.load("https://developer.harmonyos.com");
                }
            });
        }
    }

}
