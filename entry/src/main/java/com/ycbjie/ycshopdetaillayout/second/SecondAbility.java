package com.ycbjie.ycshopdetaillayout.second;

import com.ycbjie.slide.LogUtil;
import com.ycbjie.slide.SlideAnimLayout;
import com.ycbjie.slide.SlideLayout;
import com.ycbjie.ycshopdetaillayout.ResourceTable;
import com.ycbjie.ycshopdetaillayout.first.ShopMainFragment;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.ability.fraction.FractionManager;
import ohos.aafwk.ability.fraction.FractionScheduler;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.webengine.ResourceRequest;
import ohos.agp.components.webengine.WebAgent;
import ohos.agp.components.webengine.WebConfig;
import ohos.agp.components.webengine.WebView;

public class SecondAbility extends FractionAbility {

    private SlideAnimLayout mSlideDetailsLayout;
    private ShopMainFragment shopMainFragment;
    private WebView webView;
    private Image mIvMoreImg;
    private Text mTvMoreText;
    private boolean isBtn = true;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_second);
        initView();
        initShopMainFragment();
        initSlideDetailsLayout();
        initWebView();

        findComponentById(ResourceTable.Id_btn).setClickedListener(component -> {
            if (isBtn){
                isBtn = false;
                mSlideDetailsLayout.smoothOpen(true);
            }else {
                isBtn = true;
                mSlideDetailsLayout.smoothClose(true);
            }
        });

    }

    private void initView() {
        mSlideDetailsLayout = (SlideAnimLayout) findComponentById(ResourceTable.Id_slideDetailsLayout);
        webView = (WebView) findComponentById(ResourceTable.Id_wb_view);
        mIvMoreImg = (Image) findComponentById(ResourceTable.Id_iv_more_img);
        mTvMoreText = (Text) findComponentById(ResourceTable.Id_tv_more_text);
    }

    private void initShopMainFragment() {
        FractionManager fm = getFractionManager();
        FractionScheduler fragmentTransaction = fm.startFractionScheduler();
        if(shopMainFragment==null){
            shopMainFragment = new ShopMainFragment();
            fragmentTransaction
                    .replace(ResourceTable.Id_fl_shop_main2, shopMainFragment)
                    .submit();
        }else {
            fragmentTransaction.show(shopMainFragment);
        }
    }

    private void initSlideDetailsLayout() {
        mSlideDetailsLayout.setScrollStatusListener(new SlideAnimLayout.onScrollStatusListener() {
            @Override
            public void onStatusChanged(SlideAnimLayout.Status mNowStatus,boolean isHalf) {
                if(mNowStatus==SlideAnimLayout.Status.CLOSE){
                    if(isHalf){//打开
                        mTvMoreText.setText("释放，查看图文详情");
                        mIvMoreImg.createAnimatorProperty().rotate(0).start();
                    }else{//关闭
                        mTvMoreText.setText("继续上拉，查看图文详情");
                        mIvMoreImg.createAnimatorProperty().rotate(180).start();
                    }
                }else{
                    if(isHalf){//打开
                        mTvMoreText.setText("下拉回到商品详情");
                        mIvMoreImg.createAnimatorProperty().rotate(0).start();
                    }else{//关闭
                        mTvMoreText.setText("释放回到商品详情");
                        mIvMoreImg.createAnimatorProperty().rotate(180).start();
                    }
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
