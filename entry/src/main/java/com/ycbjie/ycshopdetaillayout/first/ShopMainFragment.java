package com.ycbjie.ycshopdetaillayout.first;

import com.ycbjie.ycshopdetaillayout.ResourceTable;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;

public class ShopMainFragment extends Fraction {

    private Text mTvBottomView;

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        return scatter.parse(getContentView(), container, false);
    }

    @Override
    protected void onActive() {
        super.onActive();
        initView();
    }

    private int getContentView() {
        return ResourceTable.Layout_include_shop_main;
    }

    private void initView() {
        Component component = getComponent();
        mTvBottomView = (Text) component.findComponentById(ResourceTable.Id_tv_bottom_view);
    }

    public void changBottomView(boolean isDetail) {
        if(isDetail){
            mTvBottomView.setText("下拉回到商品详情");
        }else {
            mTvBottomView.setText("继续上拉，查看图文详情");
        }
    }


}
