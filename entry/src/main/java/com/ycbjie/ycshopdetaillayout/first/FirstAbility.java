package com.ycbjie.ycshopdetaillayout.first;

import com.ycbjie.ycshopdetaillayout.ResourceTable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class FirstAbility extends Ability {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_first);


    }


}
