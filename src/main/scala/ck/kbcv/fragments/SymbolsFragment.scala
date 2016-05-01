package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.{Editable, TextWatcher}
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget._
import ck.kbcv.{Controller, OnSymbolsChangedListener, R}
import term.Term.{F, V}


class SymbolsFragment extends Fragment {
    var mCallback: OnSymbolsChangedListener = null
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


        return view
    }


    def onVariablesChanged(): Unit = {
        val variableFragment = getChildFragmentManager.findFragmentById(R.id.variable_editor_fragment).asInstanceOf[VariableEditor]
        variableFragment.setVariables()
    }


    def onFunctionsChanged(): Unit = {
        val functionFragment = getChildFragmentManager.findFragmentById(R.id.function_editor_fragment).asInstanceOf[FunctionEditor]
        functionFragment.setFunctions()
    }


}