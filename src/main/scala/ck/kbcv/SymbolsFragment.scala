package ck.kbcv

import java.lang.ClassCastException

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.{Editable, TextWatcher}
import android.view.{ View, ViewGroup, LayoutInflater }
import android.widget.{EditText, NumberPicker, Button}

/**
 * Created by Christina on 05.12.2015.
 */
class SymbolsFragment extends Fragment {
    var mCallback: OnVariablesChangedListener = null

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
        val numberPicker = view.findViewById(R.id.numberPicker).asInstanceOf[NumberPicker]
        numberPicker.setMaxValue(15)
        numberPicker.setMinValue(0)
        val plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new View.OnClickListener {
            override def onClick(v: View): Unit = {
            // TODO

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