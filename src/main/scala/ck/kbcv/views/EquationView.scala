package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{LinearLayout, TextView}
import ck.kbcv.R
import term.Term
import term.util.E

/**
 * Created by Christina on 03.01.2016.
 */
class EquationView (context: Context, attrs: AttributeSet, equation: E) extends LinearLayout(context, attrs) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    this.setOrientation(LinearLayout.HORIZONTAL)

    var lhs: Term = null
    var rhs: Term = null
    if(equation != null) {
        lhs = equation.lhs
        rhs = equation.rhs

    }

    this.addView(new TermView(context, attrs, lhs))

    val equalsSign = new TextView(context)
    equalsSign.setText("=")
    equalsSign.setBackgroundColor(getResources.getColor(R.color.colorAccent))
    val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
    equalsSign.setLayoutParams(lp)
    equalsSign.setGravity(Gravity.CENTER)

    this.addView(equalsSign)

    this.addView(new TermView(context, attrs, rhs))



}
