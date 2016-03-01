package ck.kbcv.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import term.{Fun, Var, Term}

/**
 * Created by Christina on 01.03.2016.
 */
class TermView(context: Context, attrs: AttributeSet, term: Term = null) extends LinearLayout(context: Context, attrs: AttributeSet){

    if(term == null) {
        this.addView(new EquationDropzone(context, attrs))
    } else {
        term match {
            case Var(x) => this.addView(new VarView(context, attrs, x))
            case Fun(f, ts) => this.addView(new FunView(context, attrs, f, ts))
        }

    }


}
