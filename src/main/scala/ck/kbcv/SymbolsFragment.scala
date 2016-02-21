package ck.kbcv

import java.lang.ClassCastException

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.{Editable, TextWatcher}
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.view.{ View, ViewGroup, LayoutInflater }
import android.widget._
import ck.kbcv.state.OnSymbolsChangedListener
import term.Term.{V, F}

/**
 * Created by Christina on 05.12.2015.
 */
class SymbolsFragment extends Fragment {
    var mCallback: OnSymbolsChangedListener = null
    var variablesTextView: EditText = null
    var functionList: List[View] = Nil
    var functionsContainer: LinearLayout = null
    var inflater: LayoutInflater = null

    override def onAttach(context: Context): Unit = {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = getActivity.asInstanceOf[OnSymbolsChangedListener]
        } catch {
            case ex: ClassCastException => {
                throw new ClassCastException(getActivity.toString()
                    + " must implement OnSymbolsChangedListener ")
            }
        }
    }

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        this.inflater = inflater
        val view = inflater.inflate( R.layout.symbols_fragment, container, false )
        functionsContainer = view.findViewById(R.id.container).asInstanceOf[LinearLayout]
        val function_1 = view.findViewById(R.id.function_1)
        functionList = List(function_1)
        val functionSymbolView = function_1.findViewById(R.id.function_symbol).asInstanceOf[EditText]
        val arityView = function_1.findViewById(R.id.arity).asInstanceOf[EditText]
        functionSymbolView.addTextChangedListener(new FunctionWatcher)  // TODO is ja furchtbar haesslich
        arityView.addTextChangedListener(new FunctionWatcher)
        //val arityPicker = function_1.findViewById(R.id.arity_picker).asInstanceOf[NumberPicker]
        //arityPicker.setMaxValue(15)
        //arityPicker.setMinValue(0)
        val plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new View.OnClickListener {
            override def onClick(v: View): Unit = {
                val newFunction = inflater.inflate(R.layout.function_helper, null)
                newFunction.setId(View.generateViewId())

                //val lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                //lp.addRule(RelativeLayout.BELOW, functionList.head.getId)
                //newFunction.setLayoutParams(lp)

                val functionSymbolView = newFunction.findViewById(R.id.function_symbol).asInstanceOf[EditText]
                val arityView = newFunction.findViewById(R.id.arity).asInstanceOf[EditText]
                functionSymbolView.addTextChangedListener(new FunctionWatcher)  // TODO is ja furchtbar haesslich
                arityView.addTextChangedListener(new FunctionWatcher)
                functionsContainer.addView(newFunction, functionsContainer.getChildCount -1)
                functionList = newFunction :: functionList
            }
        })

        variablesTextView = view.findViewById(R.id.variableEditText).asInstanceOf[EditText]
        variablesTextView.addTextChangedListener(new VariablesWatcher)
        return view
    }

    def getFunctions(): Set[(F, Int)]  = {
        var functions: Set[(F, Int)] = Set()
        for (function <- functionList) {
            val functionSymbolView = function.findViewById(R.id.function_symbol).asInstanceOf[EditText]
            val arityView = function.findViewById(R.id.arity).asInstanceOf[EditText]
            val arity = try {
                arityView.getText.toString.toInt
            } catch {
                case ex: NumberFormatException => {
                    0
                }
            }
            val test = (functionSymbolView.getText.toString, arity) // TODO: display error, when one function symbol is used more than once
            functions += test
        }
        return functions
    }

    def getVariables(): Set[V] = {
        var variables: Set[V] = Set()
        val list = variablesTextView.getText.toString.split(" ")
        for(variable <- list) {
            variables += variable
        }
        variables
    }


    def onVariablesChanged(): Unit = {
        val myVariables = getVariables()
        val stateVariables = Controller.state.variables
        if(!(myVariables == stateVariables)) {
            var string = ""
            for(variable <- stateVariables) {
                string += variable + " "
            }
            variablesTextView.setText(string)
        }
    }


    def onFunctionsChanged(): Unit = {
        val myFunctions = getFunctions()
        val stateFunctions = Controller.state.functions
        if(!(myFunctions == stateFunctions)) {
            functionList = Nil
            functionsContainer.removeViews(3, functionsContainer.getChildCount-4)
            for(function <- stateFunctions) {
                val newFunction = inflater.inflate(R.layout.function_helper, null)
                newFunction.setId(View.generateViewId())

                val functionSymbolView = newFunction.findViewById(R.id.function_symbol).asInstanceOf[EditText]
                val arityView = newFunction.findViewById(R.id.arity).asInstanceOf[EditText]

                functionSymbolView.setText(function._1)
                arityView.setText(function._2.toString)

                functionSymbolView.addTextChangedListener(new FunctionWatcher)
                arityView.addTextChangedListener(new FunctionWatcher)

                functionsContainer.addView(newFunction, functionsContainer.getChildCount -1)
                functionList = newFunction :: functionList
            }

        }
    }

    
    class FunctionWatcher extends TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {

        }

        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
            val functions = getFunctions()
            Controller.state.functions = functions
            mCallback.onFunctionsChanged()
        }

        override def afterTextChanged(s: Editable): Unit = {

        }
    }


    class VariablesWatcher extends TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {

        }

        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
            val variables = getVariables()
            Controller.state.variables = variables
            mCallback.onVariablesChanged()
        }

        override def afterTextChanged(s: Editable): Unit = {

        }

    }

}