package com.ycbjie.ycshopdetaillayout.five;

import com.ycbjie.ycshopdetaillayout.ResourceTable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.NestedScrollView;

public class FiveAbility extends Ability {

    private NestedScrollView nestedScrollView;

    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_shop_main);

        nestedScrollView = (NestedScrollView) findComponentById(ResourceTable.Id_scrollView);
        findComponentById(ResourceTable.Id_btn).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        //TODO : API unavailable
                        //nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
                        nestedScrollView.fluentScrollByY(getWindow().getLayoutConfig().get().height);

                    }
                });

            }
        });
    }
}
