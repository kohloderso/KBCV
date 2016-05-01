package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.ViewGroup.{LayoutParams, MarginLayoutParams}
import android.view.{View, ViewGroup, LayoutInflater}
import android.widget._
import ck.kbcv.{HorizontalFlowLayout, Controller, R, OnSymbolsChangedListener}

// TODO listen to clicks on the variables which will open a dialog to edit them
class VariableEditor extends Fragment {
    val TAG = "VariableEditor"
    var plusButton: Button = null
    var flowLayout: HorizontalFlowLayout = null


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.variable_editor, container, false )
        flowLayout = view.findViewById(R.id.variableFlowLayout).asInstanceOf[HorizontalFlowLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        // TODO plusButton add OnClickListener that opens a new dialog to create a new variable
        setVariables()
        return view
    }

    def setVariables(): Unit = {
        val variables = Controller.state.variables
        val dpValue = 16
        val dpi = getActivity.getResources().getDisplayMetrics().density
        val margin = (dpValue * dpi).toInt // margin in pixels

        val lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(margin, margin, margin, margin)
        flowLayout.removeAllViews()

        for(variable <- variables) {
            val tv = new TextView(getActivity)
            tv.setText(variable)
            tv.setLayoutParams(lp)
            val test = tv.getLayoutParams
            Log.d(TAG, test.toString)
            flowLayout.addView(tv)
        }
        flowLayout.addView(plusButton)
    }

}
