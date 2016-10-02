package ck.kbcv.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{LinearLayout, TextView}
import ck.kbcv.activities.PrecedenceActivity
import ck.kbcv.fragments.EquationEditor
import term.Term.F
import term.reco._
import term.util.{E, Equation}
import term.{Fun, Term}

trait DropSymbolsEditor {
    var left: TermView
    var right: TermView

    def onSymbolDropped(): Unit

    def clear(): Unit = {
        left.clear()
        right.clear()
    }

    def containsDropZones(): Boolean = {
        right.containsDropZones() || left.containsDropZones()
    }
}

class PrecedenceEditView(context: Context, attrs: AttributeSet) extends LinearLayout(context, attrs) with DropSymbolsEditor {
    var precedenceActivity: PrecedenceActivity = null

    this.setOrientation(LinearLayout.HORIZONTAL)
    this.setBackgroundColor(Color.WHITE)

    override var left: TermView = new TermView(context, attrs, null, this)
    override var right: TermView = new TermView(context, attrs, null, this)

    val greaterSign = new TextView(context)
    greaterSign.setText(">")
    val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
    greaterSign.setLayoutParams(lp)
    greaterSign.setGravity(Gravity.CENTER)

    this.addView(left)
    this.addView(greaterSign)
    this.addView(right)

    def setPrecedenceActivity(activity: PrecedenceActivity): Unit = {
        precedenceActivity = activity
    }

    override def onSymbolDropped(): Unit = {
        precedenceActivity.setAddButton()
    }

    def getPrecedence: (F, F) = {
        val f1 = if (left.getTerm == null || left.getTerm.funs.isEmpty) null else left.getTerm.funs.head
        val f2 = if (right.getTerm == null || right.getTerm.funs.isEmpty) null else right.getTerm.funs.head

        (f1, f2)
    }

    def setPrecedence(f1: F, f2: F): Unit = {
        this.removeAllViews()

        if (f1 != null) left = new TermView(context, attrs, new Fun(f1, List.empty), this)
        else left = new TermView(context, attrs, null, this)

        if (f2 != null) right = new TermView(context, attrs, new Fun(f2, List.empty), this)
        else right = new TermView(context, attrs, null, this)

        this.addView(left)
        this.addView(greaterSign)
        this.addView(right)
    }
}


/**
 * View which allows editing (deleting, adding, changing) parts or a whole Equation.
 * Constructor either takes an existing equation (+ it's index) or null, which means new equation from scratch.
 */
class EquationEditView(context: Context, attrs: AttributeSet, equation: IE, var equationEditor: EquationEditor = null) extends LinearLayout(context, attrs) with DropSymbolsEditor {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    var index: Int = -1
    override var left: TermView = null
    override var right: TermView = null

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

        left = new TermView(context, attrs, lhs, this)
        right = new TermView(context, attrs, rhs, this)

        val equalitySign = new TextView(context)
        equalitySign.setText("\u2248")
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        equalitySign.setLayoutParams(lp)
        equalitySign.setGravity(Gravity.CENTER)

        this.addView(left)
        this.addView(equalitySign)
        this.addView(right)

        if (equationEditor != null) {
            if (index >= 0) {
                equationEditor.setAddButton("save")
            } else {
                equationEditor.setAddButton("add")
            }
        }
    }

    /**
     *
     * @return the equation currently set in the edit view, null if it's not valid (containing DropZones)
     */
    def getEquation: E = {
        //if(containsDropZones()) return null
        new Equation(left.getTerm, right.getTerm)
    }

    def onSymbolDropped(): Unit = {
        equationEditor.checkAddButton()
    }

}