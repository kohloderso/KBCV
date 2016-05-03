package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View.OnClickListener
import android.view.{View, ViewGroup, LayoutInflater}
import android.widget.{TextView, Button}
import ck.kbcv.dialogs.{FunctionDialog, VariableDialog}
import ck.kbcv.{HorizontalFlowLayout, Controller, R}


class FunctionEditor extends Fragment {
    var plusButton: Button = null
    var flowLayout: HorizontalFlowLayout = null


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.function_editor, container, false )
        flowLayout = view.findViewById(R.id.functionFlowLayout).asInstanceOf[HorizontalFlowLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new OnClickListener {
            override def onClick(v: View): Unit = {
                new FunctionDialog().show(getChildFragmentManager, "FunctionDialog")
            }
        })

        setFunctions()
        return view
    }

    def setFunctions(): Unit = {
        val functions = Controller.state.functions
        flowLayout.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for((funName, funArity) <- functions) {
            val functionView = inflater.inflate(R.layout.fun_arity_view, flowLayout, false)
            val funNameView = functionView.findViewById(R.id.funName).asInstanceOf[TextView]
            funNameView.setText(funName)
            val funArityView = functionView.findViewById(R.id.funArity).asInstanceOf[TextView]
            funArityView.setText(funArity.toString)
            flowLayout.addView(functionView)
        }
        flowLayout.addView(plusButton)
    }

}
