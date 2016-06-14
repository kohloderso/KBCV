package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{LinearLayout, TextView}
import term.Term
import term.reco.{IE, IR}
import term.util._

/**
 * class to display a TermPair, which are rules or equations
 */
class TermPairView (context: Context, attrs: AttributeSet, termPair: TermPair, separator: String, index: Int) extends LinearLayout(context, attrs) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null, "", -1)

    this.setOrientation(LinearLayout.HORIZONTAL)

    setTermPair(termPair)


    // TODO throw error or something when it's no valid termpair (when it still contains Dropzones)
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
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        separatorView.setLayoutParams(lp)
        separatorView.setGravity(Gravity.CENTER)

        this.addView(separatorView)

        this.addView(new TermView(context, attrs, rhs))
    }
}

class EquationView (context: Context, attrs: AttributeSet, equation: IE) extends TermPairView(context, attrs, equation._2, " \u2248 ", equation._1) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, new IE(-1, null))
}

class RuleView (context: Context, attrs: AttributeSet, rule: IR) extends TermPairView(context, attrs, rule._2, " \u2192 ", rule._1) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, new IR(-1, null))
}

