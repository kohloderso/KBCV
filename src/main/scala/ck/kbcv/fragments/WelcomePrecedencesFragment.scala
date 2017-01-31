package ck.kbcv.fragments

import android.animation.{AnimatorSet, ObjectAnimator}
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.animation.Animation
import android.view.{LayoutInflater, View, ViewGroup, ViewTreeObserver}
import ck.kbcv.R
import ck.kbcv.utils.DelayInterpolator


class WelcomePrecedencesFragment extends Fragment {
    var isPageVisible = false
    var g1: View = null
    var g2: View = null
    var g3: View = null
    var prec1: View = null
    var prec2: View = null
    var prec3: View = null
    var prec4: View = null
    val animSet = new AnimatorSet

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate(R.layout.welcome_precedences_fragment, container, false)
        g1 = view.findViewById(R.id.g1)
        g2 = view.findViewById(R.id.g2)
        g3 = view.findViewById(R.id.g3)
        prec1 = view.findViewById(R.id.prec1)
        prec2 = view.findViewById(R.id.prec2)
        prec3 = view.findViewById(R.id.prec3)
        prec4 = view.findViewById(R.id.prec4)

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            override def onGlobalLayout(): Unit =  {

                if(animSet.isRunning) {
                    if (!isPageVisible) {
                        animSet.end()
                    } else if (isPageVisible) {
                        startAnimation()
                        // animSet.resume()
                    }
                } else if(!animSet.isRunning) {
                    startAnimation
                }
            }
        })
        view
    }

    override def setUserVisibleHint(isVisibleToUser: Boolean): Unit = {
        super.setUserVisibleHint(isVisibleToUser)
        isPageVisible = isVisibleToUser
    }

    def startAnimation(): Unit = {
        val duration = 8000

        val fadeprec1 = ObjectAnimator.ofFloat(prec1, "alpha", 0f, 0.6f)
        fadeprec1.setDuration(duration)
        fadeprec1.setInterpolator(new DelayInterpolator(.0f, 0.8f))
        fadeprec1.setRepeatMode(Animation.RESTART)
        fadeprec1.setRepeatCount(Animation.INFINITE)

        val fadeg1 = ObjectAnimator.ofFloat(g1, "alpha", 0f, .6f)
        fadeg1.setDuration(duration)
        fadeg1.setInterpolator(new DelayInterpolator(.2f, 0.60f))
        fadeg1.setRepeatMode(Animation.RESTART)
        fadeg1.setRepeatCount(Animation.INFINITE)

        val fadeprec2 = ObjectAnimator.ofFloat(prec2, "alpha", 0f, .6f)
        fadeprec2.setDuration(duration)
        fadeprec2.setInterpolator(new DelayInterpolator(.25f, 0.55f))
        fadeprec2.setRepeatMode(Animation.RESTART)
        fadeprec2.setRepeatCount(Animation.INFINITE)

        val fadeg2 = ObjectAnimator.ofFloat(g2, "alpha", 0f, .6f)
        fadeg2.setDuration(duration)
        fadeg2.setInterpolator(new DelayInterpolator(.4f, 0.4f))
        fadeg2.setRepeatMode(Animation.RESTART)
        fadeg2.setRepeatCount(Animation.INFINITE)

        val fadeprec3 = ObjectAnimator.ofFloat(prec3, "alpha", 0f, .6f)
        fadeprec3.setDuration(duration)
        fadeprec3.setInterpolator(new DelayInterpolator(.45f, 0.35f))
        fadeprec3.setRepeatMode(Animation.RESTART)
        fadeprec3.setRepeatCount(Animation.INFINITE)

        val fadeg3 = ObjectAnimator.ofFloat(g3, "alpha", 0f, .6f)
        fadeg3.setDuration(duration)
        fadeg3.setInterpolator(new DelayInterpolator(.6f, 0.2f))
        fadeg3.setRepeatMode(Animation.RESTART)
        fadeg3.setRepeatCount(Animation.INFINITE)

        val fadeprec4 = ObjectAnimator.ofFloat(prec4, "alpha", 0f, .6f)
        fadeprec4.setDuration(duration)
        fadeprec4.setInterpolator(new DelayInterpolator(.65f, 0.15f))
        fadeprec4.setRepeatMode(Animation.RESTART)
        fadeprec4.setRepeatCount(Animation.INFINITE)

        animSet.playTogether(fadeprec1, fadeg1, fadeprec2, fadeg2, fadeprec3, fadeg3, fadeprec4)
        animSet.start()

    }


}
