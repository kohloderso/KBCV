package ck.kbcv.fragments

import android.animation.Animator.AnimatorListener
import android.animation.{Animator, AnimatorSet, ObjectAnimator}
import android.graphics.Color
import android.graphics.drawable.{ColorDrawable, Drawable, TransitionDrawable}
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.{LayoutInflater, View, ViewGroup, ViewTreeObserver}
import ck.kbcv.views.RuleView
import ck.kbcv.{R, TR, TypedFindView}
import term.parser.{Parser, ParserOldTRS}
import term.{Fun, Var}
import term.util.TermPair

class WelcomeCompletionFragment extends Fragment {
  var card1: View = null
  var card2: View = null
  val animSet = new AnimatorSet

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate(R.layout.welcome_completion_fragment, container, false)

    card1 = view.findViewById(R.id.r_item1)
    card2 = view.findViewById(R.id.r_item2)
    card2.bringToFront()
    val ruleView1 = view.findViewById(R.id.rule_view1).asInstanceOf[RuleView]
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
    });

    view
  }


  def startAnimation(): Unit = {
    val animX1 = ObjectAnimator.ofFloat(card2, "x", card1.getLeft-20);
    val animY1 = ObjectAnimator.ofFloat(card2, "y", card1.getTop-10);
    animY1.setDuration(2000)
    animX1.setDuration(2000)
    animX1.setRepeatMode(Animation.REVERSE)
    animX1.setRepeatCount(Animation.INFINITE)
    animY1.setRepeatMode(Animation.REVERSE)
    animY1.setRepeatCount(Animation.INFINITE)

    val alphaAnim = ObjectAnimator.ofFloat(card2, "alpha", 1f, 0.7f);
    alphaAnim.setDuration(1000);

    animSet.playTogether(animX1, animY1)

    animSet.start();
    alphaAnim.start()

  }

}
