package ck.kbcv.fragments

import android.content.ClipData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import ck.kbcv.views.Equation
import ck.kbcv.{Controller, R}
import term.util.ES

/**
 * Created by Christina on 09.12.2015.
 */
class EquationsFragment extends Fragment {
    val TAG = "EquationsFragment"
    var functionSymbolContainer: LinearLayout = null
    var variableSymbolContainer: LinearLayout = null
    var equationContainer: LinearLayout = null

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equations_fragment, container, false )

        equationContainer = view.findViewById(R.id.equationsContainer).asInstanceOf[LinearLayout]
        functionSymbolContainer = view.findViewById(R.id.functionSymbolsContainer).asInstanceOf[LinearLayout]
        variableSymbolContainer = view.findViewById(R.id.variableSymbolContainer).asInstanceOf[LinearLayout]

        val plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new View.OnClickListener {
            override def onClick(v: View): Unit = {
                val newEquation = new Equation(getActivity, null)
                equationContainer.addView(newEquation)
            }
        })
        return view
    }

    def onVariablesChanged(): Unit = {
        val variables = Controller.state.variables
        variableSymbolContainer.removeAllViews()
        for(variable <- variables) {
            val b = new Button(getContext, null, android.R.attr.buttonStyleSmall)
            b.setText(variable)
            b.setOnTouchListener(new View.OnTouchListener() {
                override def onTouch(v: View, event: MotionEvent): Boolean = {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        val data = ClipData.newPlainText("variable", variable)
                        val shadow = new View.DragShadowBuilder(b)
                        v.startDrag(data, shadow, null, 0)
                        true
                    } else {
                        false
                    }
                }
            })
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            variableSymbolContainer.addView(b)
        }
    }


    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        val i = 0
        for(function <- functions) {
            val functionSymbol = function._1
            val arity = function._2
            val b = new Button(getContext, null, android.R.attr.buttonStyleSmall)
            b.setText(functionSymbol)
            b.setOnTouchListener(new View.OnTouchListener() {
                override def onTouch(v: View, event: MotionEvent): Boolean = {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        val data = ClipData.newPlainText("function", functionSymbol)
                        data.addItem(new ClipData.Item(arity.toString))
                        val shadow = new View.DragShadowBuilder(b)
                        v.startDrag(data, shadow, null, 0)
                        true
                    } else {
                        false
                    }

                }
            })
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            functionSymbolContainer.addView(b)
        }
    }

    def onEquationsAdded(es: ES): Unit = {
        // TODO
    }
}