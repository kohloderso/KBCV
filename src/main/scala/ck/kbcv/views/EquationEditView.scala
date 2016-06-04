package ck.kbcv.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.{View, Gravity}
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.widget.{LinearLayout, TextView}
import term.Term
import term.reco.IE

/** 
 * View which allows editing (deleting, adding, changing) parts or a whole Equation.
 * Constructor either takes an existing equation (+ it's index) or null, which means new equation from scratch.
 */
class EquationEditView (context: Context, attrs: AttributeSet, equation: IE) extends LinearLayout(context, attrs){
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    var index: Int = -1
    var selectedTerm: TermView = null

    this.setOrientation(LinearLayout.HORIZONTAL)
    this.setBackgroundColor(Color.WHITE)
   
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

        this.addView(new TermView(context, attrs, lhs, this))

        val equalitySign = new TextView(context)
        equalitySign.setText("\u2248")
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        equalitySign.setLayoutParams(lp)
        equalitySign.setGravity(Gravity.CENTER)

        this.addView(equalitySign)

        this.addView(new TermView(context, attrs, rhs, this))
    }

}

