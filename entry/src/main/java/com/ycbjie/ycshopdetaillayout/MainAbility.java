package com.ycbjie.ycshopdetaillayout;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;

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

        findComponentById(ResourceTable.Id_tv_2).setClickedListener(component -> {
            Intent intent1 = new Intent();
            Operation operation = new Intent.OperationBuilder().withAction("action.second").build();
            intent1.setOperation(operation);
            startAbility(intent1);
        });

        findComponentById(ResourceTable.Id_tv_3).setClickedListener(component -> {
            Intent intent1 = new Intent();
            Operation operation = new Intent.OperationBuilder().withAction("action.third").build();
            intent1.setOperation(operation);
            startAbility(intent1);
        });
    }
}
