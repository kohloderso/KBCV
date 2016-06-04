package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.{LinearLayout, TextView}
import ck.kbcv.R
import term.Term
import term.Term._


class FunView (context: Context, attrs: AttributeSet, funName: F, funArgs: List[Term], equationEditView: EquationEditView = null) extends LinearLayout(context: Context, attrs: AttributeSet) {
    this.setOrientation(LinearLayout.HORIZONTAL)


    val nameView = new TextView(context, attrs)
    nameView.setText(funName)
    nameView.setTextColor(getResources.getColor(R.color.textColorFunction))
    //nameView.setTextSize(getResources.getDimension(R.dimen.equation_text))
    this.addView(nameView)

    if(!funArgs.isEmpty) {
        val bracketL = new TextView(context, attrs)
        bracketL.setText("(")
        val bracketR = new TextView(context, attrs)
        bracketR.setText(")")

        this.addView(bracketL)
        for(arg <- funArgs) {
            val termView = new TermView(context, attrs, arg, equationEditView)
            this.addView(termView)
            val comma = new TextView(context, attrs)
            comma.setText(", ")
            this.addView(comma)
        }
        this.removeViewAt(this.getChildCount-1) // remove last comma
        this.addView(bracketR)
    }



}
