package com.jimulabs.googlemusicmock.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lintonye on 14-12-05.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Scale extends Visibility {
    public Scale(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Scale() {
        super();
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createScaleAnimator(view, 0, 1);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createScaleAnimator(view, 1, 0);
    }

    public Animator createScaleAnimator(View view, float startScale, float endScale) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", startScale, endScale);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", startScale, endScale);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY);
        return animator;
    }
}
