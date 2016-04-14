package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.{TextView, LinearLayout}
import ck.kbcv.R
import term.Term
import term.reco.IE
import term.util.TermPair
import android.view.ViewGroup.LayoutParams

/** 
 * View which allows editing (deleting, adding, changing) parts or a whole Equation.
 * Constructor either takes an existing equation (+ it's index) or null, which means new equation from scratch.
 */
class EquationEditView (context: Context, attrs: AttributeSet, equation: IE) extends LinearLayout(context, attrs) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    var index: Int = -1

    this.setOrientation(LinearLayout.HORIZONTAL)
   
    setEquation(equation)


    def setEquation(ie: IE): Unit = {
        this.removeAllViews()

        var lhs: Term = null
        var rhs: Term = null
        if (ie != null) {
            val equation = ie._2
            index = ie._1
            lhs = equation.lhs
            rhs = equation.rhs

        }

        this.addView(new TermView(context, attrs, lhs))

        val separatorView = new TextView(context)
        separatorView.setText("=")
        separatorView.setBackgroundColor(getResources.getColor(R.color.colorAccent))
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        separatorView.setLayoutParams(lp)
        separatorView.setGravity(Gravity.CENTER)

        this.addView(separatorView)

        this.addView(new TermView(context, attrs, rhs))
    }
}

