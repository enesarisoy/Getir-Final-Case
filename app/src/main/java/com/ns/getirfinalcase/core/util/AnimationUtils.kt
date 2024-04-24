package com.ns.getirfinalcase.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator


fun animateCart(view: View, from: Float, to: Float, visibility: Int) {

    ObjectAnimator.ofFloat(view, "translationX", from, to)
        .apply {
            duration = 1000
            interpolator = OvershootInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = visibility
                }
            })
            start()
        }
}
