package com.ycbjie.ycshopdetaillayout;

import com.ycbjie.ycshopdetaillayout.first.FirstAbility;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        findComponentById(ResourceTable.Id_tv_1).setClickedListener(component -> {
            Intent intent1 = new Intent();
            Operation operation = new Intent.OperationBuilder().withAction("action.first").build();
            intent1.setOperation(operation);
            startAbility(intent1);
        });
    }
}
