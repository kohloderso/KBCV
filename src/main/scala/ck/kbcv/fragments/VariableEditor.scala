package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget._
import ck.kbcv.dialogs.VariableDialog
import ck.kbcv.{Controller, HorizontalFlowLayout, R}

// TODO listen to clicks on the variables which will open a dialog to edit them
class VariableEditor extends Fragment {
    val TAG = "VariableEditor"
    var plusButton: Button = null
    var flowLayout: HorizontalFlowLayout = null


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.variable_editor, container, false )
        flowLayout = view.findViewById(R.id.variableFlowLayout).asInstanceOf[HorizontalFlowLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnClickListener(new OnClickListener {
            override def onClick(v: View): Unit = {
                new VariableDialog().show(getChildFragmentManager, "VariableDialog")
            }
        })

        setVariables()
        return view
    }

    def setVariables(): Unit = {
        val variables = Controller.state.variables
        val dpValue = 16

        flowLayout.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for(variable <- variables) {
            val varView = inflater.inflate(R.layout.var_view, flowLayout, false).asInstanceOf[TextView]
            varView.setText(variable)
            flowLayout.addView(varView)
        }
        flowLayout.addView(plusButton)
    }

}
