package ck.kbcv.views

import android.content.{ClipData, Context}
import android.util.AttributeSet
import android.view.View.{OnClickListener, OnTouchListener}
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import ck.kbcv.activities.PrecedenceActivity
import ck.kbcv.fragments.CreateEquationsFragment
import ck.kbcv.{Controller, HorizontalFlowLayout, R}
import term.Term._
import term.reco._
import term.util.{E, Equation}
import term.{Fun, Term}

trait DropSymbolsEditor extends OnTouchListener with OnClickListener {
    var linearLayout: LinearLayout = null
    var left: TermView = null
    var right: TermView = null
    var middle: TextView = null
    var addButton: Button = null
    var clearButton: Button = null

    def onSymbolDropped(): Unit = {
        checkAddButton()
    }

    def clear(): Unit = {
        left.clear()
        right.clear()
        checkAddButton()
    }

    def containsDropZones(): Boolean = {
        right.containsDropZones() || left.containsDropZones()
    }

    /**
     * set the label of the button either to "add" or "save" depending on whether its a new equation
     */
    def setAddButton(name: String): Unit = {
        addButton.setText(name)
        checkAddButton()
    }

    /**
     * check if there's still a DropZone, if not enable the 'add'-Button
     */
    def checkAddButton(): Unit = {
        if (containsDropZones()) {
            addButton.setEnabled(false)
            middle.setOnTouchListener(null)
        } else {
            addButton.setEnabled(true)
            middle.setOnTouchListener(this)
        }
    }

}

class PrecedenceEditView(context: Context, attrs: AttributeSet) extends RelativeLayout(context, attrs) with DropSymbolsEditor {
    var precedenceActivity: PrecedenceActivity = null
    var functionSymbolContainer: HorizontalFlowLayout = null
    var variableSymbolContainer: HorizontalFlowLayout = null
    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    override def onFinishInflate(): Unit = {
        functionSymbolContainer = this.findViewById(R.id.functionSymbolsContainer_prec).asInstanceOf[HorizontalFlowLayout]
        onFunctionsChanged()

        linearLayout = this.findViewById(R.id.linearLayout).asInstanceOf[LinearLayout]
        addButton = this.findViewById(R.id.addButton).asInstanceOf[Button]
        clearButton = this.findViewById(R.id.clearButton).asInstanceOf[Button]

        addButton.setOnClickListener(this)
        clearButton.setOnClickListener(this)

        middle = new TextView(context)
        middle.setText(">")
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        middle.setLayoutParams(lp)
        middle.setGravity(Gravity.CENTER)

        left = new TermView(context, attrs, null, this)
        right = new TermView(context, attrs, null, this)
        linearLayout.addView(left)
        linearLayout.addView(middle)
        linearLayout.addView(right)

        checkAddButton()
    }

    def setPrecedenceActivity(activity: PrecedenceActivity): Unit = {
        precedenceActivity = activity
    }

    def getPrecedence: (F, F) = {
        val f1 = if (left.getTerm == null || left.getTerm.funs.isEmpty) null else left.getTerm.funs.head
        val f2 = if (right.getTerm == null || right.getTerm.funs.isEmpty) null else right.getTerm.funs.head

        (f1, f2)
    }

    def setPrecedence(f1: F, f2: F): Unit = {
        left.setTerm(new Fun(f1, List.empty))
        right.setTerm(new Fun(f2, List.empty))
    }

    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        for ((funName, funArity) <- functions) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(funName)
            setOnTouchFunction(button, funName)
            functionSymbolContainer.addView(button)
        }
    }

    def setOnTouchFunction(button: Button, f: F): Unit = {
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("precedence", f)
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }


    override def onClick(v: View): Unit = {
        if (addButton.equals(v)) {
            if (containsDropZones()) {} // TODO: throw error or something
            else {
                precedenceActivity.addToPrecedence()    // TODO if addButton is set to "save", change precedence => remove the old one and add the new one
                clear()
                checkAddButton()
            }
        } else if (clearButton.equals(v)) {
            clear()
            checkAddButton()
        }
    }

    override def onTouch(v: View, event: MotionEvent): Boolean = {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            val data = ClipData.newPlainText("newPrec", "")
            val shadow = new View.DragShadowBuilder(linearLayout)
            v.startDrag(data, shadow, null, 0)
            true
        } else {
            false
        }
    }
}


/**
 * View which allows editing (deleting, adding, changing) parts or a whole Equation.
 * Constructor either takes an existing equation (+ it's index) or null, which means new equation from scratch.
 */
class EquationEditView(context: Context, attrs: AttributeSet, equation: IE, var createEquationsFragment: CreateEquationsFragment = null) extends RelativeLayout(context, attrs) with DropSymbolsEditor {
    def this(context: Context, attrs: AttributeSet) = this(context, attrs, null)

    var index: Int = -1
    var functionSymbolContainer: HorizontalFlowLayout = null
    var variableSymbolContainer: HorizontalFlowLayout = null
    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

    override def onFinishInflate(): Unit = {
        functionSymbolContainer = this.findViewById(R.id.functionSymbolsContainer).asInstanceOf[HorizontalFlowLayout]
        variableSymbolContainer = this.findViewById(R.id.variableSymbolContainer).asInstanceOf[HorizontalFlowLayout]
        onFunctionsChanged()
        onVariablesChanged()

        linearLayout = this.findViewById(R.id.linearLayout).asInstanceOf[LinearLayout]
        addButton = this.findViewById(R.id.addButton).asInstanceOf[Button]
        clearButton = this.findViewById(R.id.clearButton).asInstanceOf[Button]

        addButton.setOnClickListener(this)
        clearButton.setOnClickListener(this)

        middle = new TextView(context)
        middle.setText("\u2248")
        val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        middle.setLayoutParams(lp)
        middle.setGravity(Gravity.CENTER)

        left = new TermView(context, attrs, null, this)
        right = new TermView(context, attrs, null, this)
        linearLayout.addView(left)
        linearLayout.addView(middle)
        linearLayout.addView(right)

        setEquation(equation)
    }

    def setFragment(createEquationsFragment: CreateEquationsFragment): Unit = {
        this.createEquationsFragment = createEquationsFragment
    }

    def setEquation(ie: IE): Unit = {
        //linearLayout.removeAllViews()

        var lhs: Term = null
        var rhs: Term = null
        if (ie != null) {
            val equation = ie._2
            index = ie._1
            lhs = equation.lhs
            rhs = equation.rhs
        }

        left.setTerm(lhs)
        right.setTerm(rhs)
//        left = new TermView(context, attrs, lhs, this)
//        right = new TermView(context, attrs, rhs, this)
//
//        linearLayout.addView(left)
//        linearLayout.addView(middle)
//        linearLayout.addView(right)


        if (createEquationsFragment != null) {
            if (index >= 0) {
                setAddButton(context.getString(R.string.save))
            } else {
                setAddButton(context.getString(R.string.add))
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

    def onVariablesChanged(): Unit = {
        val variables = Controller.state.variables
        variableSymbolContainer.removeAllViews()
        for (variable <- variables) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(variable)
            setOnTouchVariable(button, variable)
            variableSymbolContainer.addView(button)
        }
    }

    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        for ((funName, funArity) <- functions) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(funName)
            setOnTouchFunction(button, (funName, funArity))
            functionSymbolContainer.addView(button)
        }
    }

    def setOnTouchFunction(button: Button, f: (F, Int)): Unit = {
        val (function, arity) = f
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("function", function)
                    data.addItem(new ClipData.Item(arity.toString))
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }

    def setOnTouchVariable(button: Button, variable: V): Unit = {
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("variable", variable)
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }


    override def onClick(v: View): Unit = {
        if (addButton.equals(v)) {
            createEquationsFragment.addEquationFromEditor()
        } else if (clearButton.equals(v)) {
            clear()
            index = -1
            setAddButton(context.getString(R.string.add))
        }
    }

    override def onTouch(v: View, event: MotionEvent): Boolean = {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            val data = ClipData.newPlainText("newEquation", "")
            val shadow = new View.DragShadowBuilder(linearLayout)
            v.startDrag(data, shadow, null, 0)
            true
        } else {
            false
        }
    }

}