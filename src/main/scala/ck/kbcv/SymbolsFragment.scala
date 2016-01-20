package ck.kbcv

import java.lang.ClassCastException

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.{Editable, TextWatcher}
import android.view.ViewGroup.LayoutParams
import android.view.{ View, ViewGroup, LayoutInflater }
import android.widget._

/**
 * Created by Christina on 05.12.2015.
 */
class SymbolsFragment extends Fragment {
    var mCallback: OnVariablesChangedListener = null
    var functionList: List[View] = Nil

    override def onAttach(context: Context): Unit = {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = getActivity.asInstanceOf[OnVariablesChangedListener]
        } catch {
            case ex: ClassCastException => {
                throw new ClassCastException(getActivity.toString()
                    + " must implement OnVariableChangedListener")
            }
        }
    }

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.symbols_fragment, container, false )
        val function_1 = view.findViewById(R.id.function_1)
        functionList = List(function_1)
        val arityPicker = function_1.findViewById(R.id.arity_picker).asInstanceOf[NumberPicker]
        arityPicker.setMaxValue(15)
        arityPicker.setMinValue(0)
        val plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new View.OnClickListener {
            override def onClick(v: View): Unit = {
                val newFunction = inflater.inflate(R.layout.function_helper, null)
                newFunction.setId(View.generateViewId())
                val lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                lp.addRule(RelativeLayout.BELOW, functionList.head.getId)
                newFunction.setLayoutParams(lp)
                view.asInstanceOf[RelativeLayout].addView(newFunction)
                functionList = newFunction :: functionList
            }
        })

        val variablesTextView = view.findViewById(R.id.variableEditText).asInstanceOf[EditText]
        variablesTextView.addTextChangedListener(new MyTextWatcher)
        return view
    }


    class MyTextWatcher extends TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {

        }

        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
            val list = s.toString.split(" ")
            mCallback.onVariablesChanged(list.sortWith(_.toLowerCase < _.toLowerCase))
        }

        override def afterTextChanged(s: Editable): Unit = {

        }

    }

}

// Container Activity must implement this interface
trait OnVariablesChangedListener {
    def onVariablesChanged(variables: Array[String])
}