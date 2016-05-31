package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import term.Term.V


class VarView(context: Context, attrs: AttributeSet, variable: V) extends TextView(context: Context, attrs: AttributeSet) {
    this.setText(variable)

}
