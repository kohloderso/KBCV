package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{TextView, LinearLayout}
import ck.kbcv.R
import term.Term
import term.util._

/**
 * class to display a TermPair, which are rules or equations
 */
class TermPairView (context: Context, attrs: AttributeSet, termPair: TermPair, separator: String) extends LinearLayout(context, attrs) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null, "")

    this.setOrientation(LinearLayout.HORIZONTAL)
    this.setClickable(true)
    this.setFocusable(true)

    setTermPair(termPair)


    def setTermPair(termPair: TermPair): Unit = {
        this.removeAllViews()

        var lhs: Term = null
        var rhs: Term = null
        if (termPair != null) {
            lhs = termPair.lhs
            rhs = termPair.rhs

        }

        this.addView(new TermView(context, attrs, lhs))

        val separatorView = new TextView(context)
        separatorView.setText(separator)
        separatorView.setBackgroundColor(getResources.getColor(R.color.colorAccent))
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        separatorView.setLayoutParams(lp)
        separatorView.setGravity(Gravity.CENTER)

        this.addView(separatorView)

        this.addView(new TermView(context, attrs, rhs))
    }
}

class EquationView (context: Context, attrs: AttributeSet, equation: E) extends TermPairView(context, attrs, equation, "=") {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)
}

class RuleView (context: Context, attrs: AttributeSet, equation: E) extends TermPairView(context, attrs, equation, "->") {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)
}
