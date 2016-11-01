package ck.kbcv.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View.OnDragListener
import android.view.ViewGroup.LayoutParams
import android.view.{DragEvent, Gravity, View}
import android.widget.{LinearLayout, TextView}
import ck.kbcv.R
import ck.kbcv.views.TermPairView.OnDropListener
import term.Term
import term.reco.{IE, IR}
import term.util._

object TermPairView {
    trait OnDropListener {

        /**
         *
         * @param idRule
         * @param idDrop
         * @param leftRight 0 for left, 1 for right
         */
        def onRuleDropped(idRule: Int, idDrop: Int, leftRight: Int)
    }
}

/**
 * class to display a TermPair, which are rules or equations
 */
class TermPairView (context: Context, attrs: AttributeSet, termPair: TermPair, separator: String, var index: Int) extends LinearLayout(context, attrs) with OnDragListener {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null, "", -1)

    var ondDropListener: OnDropListener = null
    var leftTerm: TermView = null
    var rightTerm: TermView = null
    var separatorView: TextView = null

    this.setOrientation(LinearLayout.HORIZONTAL)

    setTermPair(termPair)

    def setTermPair(termPair: TermPair): Unit = {
        this.removeAllViews()

        var lhs: Term = null
        var rhs: Term = null
        if (termPair != null) {
            lhs = termPair.lhs
            rhs = termPair.rhs
        }
        leftTerm = new TermView(context, attrs, lhs)
        leftTerm.setPadding(16, 9, 10, 9)
        rightTerm = new TermView(context, attrs, rhs)
        rightTerm.setPadding(10, 9, 16, 9)
        leftTerm.setOnDragListener(this)
        rightTerm.setOnDragListener(this)

        separatorView = new TextView(context)
        separatorView.setText(separator)
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        separatorView.setLayoutParams(lp)
        separatorView.setGravity(Gravity.CENTER)

        this.addView(leftTerm)
        this.addView(separatorView)
        this.addView(rightTerm)
    }

    override def onDrag(v: View, event: DragEvent): Boolean = {
        if(ondDropListener == null) return false
        val action = event.getAction
        action match {
            case DragEvent.ACTION_DRAG_STARTED => true//  Do nothing
            case DragEvent.ACTION_DRAG_ENTERED => v.setBackground(ContextCompat.getDrawable(context, R.drawable.dotted_line2));true
            case DragEvent.ACTION_DRAG_EXITED => v.setBackground(null); true
            case DragEvent.ACTION_DRAG_ENDED => v.setBackground(null); true
            case DragEvent.ACTION_DROP =>
                if(event.getClipDescription.getLabel == "Rule") {
                    val idRule = event.getClipData.getItemAt(0).getText.toString.toInt
                    val leftRight = if(v == leftTerm) 0 else 1
                    ondDropListener.onRuleDropped(idRule, index, leftRight)
                }
                false
            case _ =>false
        }

    }
}

class EquationView (context: Context, attrs: AttributeSet, equation: IE) extends TermPairView(context, attrs, equation._2, " \u2248 ", equation._1) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, new IE(-1, null))
}

class RuleView (context: Context, attrs: AttributeSet, rule: IR) extends TermPairView(context, attrs, rule._2, " \u2192 ", rule._1) {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, new IR(-1, null))
}

