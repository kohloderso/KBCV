package ck.kbcv.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{LinearLayout, TextView}
import ck.kbcv.fragments.EquationEditor
import term.Term
import term.reco.IE
import term.util._

/** 
 * View which allows editing (deleting, adding, changing) parts or a whole Equation.
 * Constructor either takes an existing equation (+ it's index) or null, which means new equation from scratch.
 */
class EquationEditView (context: Context, attrs: AttributeSet, equation: IE, var equationEditor: EquationEditor = null) extends LinearLayout(context, attrs){
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    var index: Int = -1
    var leftTerm: TermView = null
    var rightTerm: TermView = null

    this.setOrientation(LinearLayout.HORIZONTAL)
    this.setBackgroundColor(Color.WHITE)
   
    setEquation(equation)

    def setEquationEditor(equationEditor: EquationEditor): Unit = {
        this.equationEditor = equationEditor
    }

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

        leftTerm = new TermView(context, attrs, lhs, this)
        this.addView(leftTerm)

        val equalitySign = new TextView(context)
        equalitySign.setText("\u2248")
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        equalitySign.setLayoutParams(lp)
        equalitySign.setGravity(Gravity.CENTER)

        this.addView(equalitySign)

        rightTerm = new TermView(context, attrs, rhs, this)
        this.addView(rightTerm)
    }

    def containsDropZones(): Boolean = {
        rightTerm.containsDropZones() || leftTerm.containsDropZones()
    }

    /**
     *
     * @return the equation currently set in the edit view, null if it's not valid (containing DropZones)
     */
    def getEquation(): E = {
        if(containsDropZones()) return null
        new Equation(leftTerm.getTerm, rightTerm.getTerm)
    }

    def clear(): Unit = {
        setEquation(null)
    }

    def onSymbolDropped(): Unit = {
        equationEditor.setSaveButton()
    }

}

