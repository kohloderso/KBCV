package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.{TextView, LinearLayout}
import term.Term
import term.Term.V

/**
 * Created by Christina on 01.03.2016.
 */
class VarView(context: Context, attrs: AttributeSet, variable: V) extends TextView(context: Context, attrs: AttributeSet) {
    this.setText(variable)

}
