package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import ck.kbcv.R
import term.Term.V


class VarView(context: Context, attrs: AttributeSet, variable: V) extends TextView(context: Context, attrs: AttributeSet) {
    this.setText(variable)
    this.setTextColor(getResources.getColor(R.color.textColorVariable))

}
