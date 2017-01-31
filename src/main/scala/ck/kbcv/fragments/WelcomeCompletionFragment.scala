package ck.kbcv.fragments

import android.animation.{AnimatorSet, ObjectAnimator}
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.animation.Animation
import android.view.{LayoutInflater, View, ViewGroup, ViewTreeObserver}
import ck.kbcv.R
import ck.kbcv.utils.DelayInterpolator
import ck.kbcv.views.RuleView
import term.parser.{Parser, ParserOldTRS}

class WelcomeCompletionFragment extends Fragment {
  var finger: View = null
  var card1: View = null
  var ruleView1: RuleView = null
  var originalHeight: Int = 0
  var card2: View = null
  val animSet = new AnimatorSet

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate(R.layout.welcome_completion_fragment, container, false)

    finger = view.findViewById(R.id.finger)
    card1 = view.findViewById(R.id.r_item1)
    card2 = view.findViewById(R.id.r_item2)
    finger.bringToFront()
    ruleView1 = view.findViewById(R.id.rule_view1).asInstanceOf[RuleView]
    val ruleView2 = view.findViewById(R.id.rule_view2).asInstanceOf[RuleView]

    // load intro example rules
    val stream = getResources.openRawResource(R.raw.intro)
    val parser: Parser = ParserOldTRS
    val es = parser.parse(stream)
    val rule1 = es(0)
    val rule2 = es(1)
    ruleView1.setTermPair(rule1)
    ruleView2.setTermPair(rule2)


    card1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      override def onGlobalLayout(): Unit =  {
        if(!animSet.isRunning) startAnimation
      }
    })

    view
  }



  def startAnimation(): Unit = {
    val distX = -45
    val distY = card1.getTop - card2.getTop+5
    val duration = 4500
    val animX1 = ObjectAnimator.ofFloat(card2, "x", card1.getLeft-45);
    val animY1 = ObjectAnimator.ofFloat(card2, "y", card1.getTop+5);
    animY1.setDuration(duration)
    animX1.setDuration(duration)
    animX1.setInterpolator(new DelayInterpolator(0.3f, 0.0f))
    animY1.setInterpolator(new DelayInterpolator(0.3f, 0.0f))
    animX1.setRepeatMode(Animation.REVERSE)
    animX1.setRepeatCount(Animation.INFINITE)
    animY1.setRepeatMode(Animation.REVERSE)
    animY1.setRepeatCount(Animation.INFINITE)

    val fadeFinger = ObjectAnimator.ofFloat(finger, "alpha", 0f, 1f)
    fadeFinger.setDuration(duration)
    //alphaAnim.setStartDelay(500)
    fadeFinger.setInterpolator(new DelayInterpolator(.0f, 0.7f));
    fadeFinger.setRepeatMode(Animation.REVERSE)
    fadeFinger.setRepeatCount(Animation.INFINITE)


    val fingerAnimX = ObjectAnimator.ofFloat(finger, "x", finger.getLeft+distX)
    val fingerAnimY = ObjectAnimator.ofFloat(finger, "y", finger.getTop+distY)
    fingerAnimX.setDuration(duration)
    fingerAnimY.setDuration(duration)
    fingerAnimX.setInterpolator(new DelayInterpolator(0.3f, 0.0f))
    fingerAnimY.setInterpolator(new DelayInterpolator(0.3f, 0.0f))
    fingerAnimX.setRepeatMode(Animation.REVERSE)
    fingerAnimX.setRepeatCount(Animation.INFINITE)
    fingerAnimY.setRepeatMode(Animation.REVERSE)
    fingerAnimY.setRepeatCount(Animation.INFINITE)


    val alphaAnim = ObjectAnimator.ofFloat(card2, "alpha", 1f, 0.8f)
    alphaAnim.setDuration(duration)
    //alphaAnim.setStartDelay(500)
    alphaAnim.setInterpolator(new DelayInterpolator(.3f, .3f));
    alphaAnim.setRepeatMode(Animation.REVERSE)
    alphaAnim.setRepeatCount(Animation.INFINITE)

    val scaleUpX = ObjectAnimator.ofFloat(card1, "scaleX", 1.2f);
    val scaleUpY = ObjectAnimator.ofFloat(card1, "scaleY", 1.2f);
    scaleUpX.setDuration(duration)
    scaleUpY.setDuration(duration)
    scaleUpX.setInterpolator(new DelayInterpolator(.45f, .3f))
    scaleUpY.setInterpolator(new DelayInterpolator(.45f, .3f))
    //scaleUpX.setStartDelay(500)
    //scaleUpY.setStartDelay(500)
    scaleUpX.setRepeatMode(Animation.REVERSE)
    scaleUpY.setRepeatMode(Animation.REVERSE)
    scaleUpX.setRepeatCount(Animation.INFINITE)
    scaleUpY.setRepeatCount(Animation.INFINITE)

    animSet.playTogether(animX1, animY1, alphaAnim, scaleUpX, scaleUpY, fadeFinger, fingerAnimX, fingerAnimY)
    animSet.start()

  }

  override def onPause(): Unit = {
    super.onPause()
    animSet.pause()
  }

  override def onResume(): Unit = {
    super.onResume()
    animSet.resume()
  }

}
