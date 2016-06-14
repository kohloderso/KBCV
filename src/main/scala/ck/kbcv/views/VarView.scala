package ck.kbcv.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View.OnDragListener
import android.widget.TextView
import ck.kbcv.R
import term.Term.V
import term.{Term, Var}


class VarView(context: Context, attrs: AttributeSet, variable: V) extends TextView(context: Context, attrs: AttributeSet) {
    this.setText(variable)
    this.setTextColor(getResources.getColor(R.color.textColorVariable))

    override def setOnDragListener(onDragListener: OnDragListener): Unit = {
        super.setOnDragListener(onDragListener)
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.dotted_line))
    }

    def getTerm: Term = {
        new Var(variable)
    }

}
