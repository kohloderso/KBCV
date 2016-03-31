package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.{LinearLayout, TextView}
import term.Term
import term.Term._

/**
 * Created by Christina on 01.03.2016.
 */
class FunView (context: Context, attrs: AttributeSet, funName: F, funArgs: List[Term]) extends LinearLayout(context: Context, attrs: AttributeSet) {
    this.setOrientation(LinearLayout.HORIZONTAL)

    val nameView = new TextView(context, attrs)
    nameView.setText(funName)
    //nameView.setTextSize(getResources.getDimension(R.dimen.equation_text))
    this.addView(nameView)

    if(!funArgs.isEmpty) {
        val bracketL = new TextView(context, attrs)
        bracketL.setText("(")
        val bracketR = new TextView(context, attrs)
        bracketR.setText(")")

        this.addView(bracketL)
        for(arg <- funArgs) {
            val termView = new TermView(context, attrs, arg)
            this.addView(termView)
            val comma = new TextView(context, attrs)
            comma.setText(", ")
            this.addView(comma)
        }
        this.removeViewAt(this.getChildCount-1) // remove last comma
        this.addView(bracketR)
    }



}
