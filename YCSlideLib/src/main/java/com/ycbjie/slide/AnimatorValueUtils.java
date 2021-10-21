package com.ycbjie.slide;

public class AnimatorValueUtils {

    public static float getAnimatedValue(float fraction, float... values) {
        if (values == null || values.length == 0) {
            return 0;
        }
        if (values.length == 1) {
            return values[0] * fraction;
        } else {
            if (fraction == 1) {
                return values[values.length - 1];
            }
            float oneFraction = 1f / (values.length - 1);
            float offFraction = 0;
            for (int i = 0; i < values.length - 1; i++) {
                if (offFraction + oneFraction >= fraction) {
                    return values[i] + (fraction - offFraction) * (values.length - 1) * (values[i + 1] - values[i]);
                }
                offFraction += oneFraction;
            }
        }
        return 0;
    }

}
