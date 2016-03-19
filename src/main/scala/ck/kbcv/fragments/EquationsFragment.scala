package ck.kbcv.fragments

import android.content.ClipData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import ck.kbcv.adapters.EquationArrayAdapter
import ck.kbcv.views.EquationView
import ck.kbcv.{Controller, R}
import term.parser.ParserXmlTRS
import term.util.ES

/**
 * Created by Christina on 09.12.2015.
 */
class EquationsFragment extends Fragment {
    val TAG = "EquationsFragment"
    var historicX = Float.NaN
    var historicY = Float.NaN
    val DELTA = 50

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equations_fragment, container, false )
        val equationListView = view.findViewById(R.id.equation_listview).asInstanceOf[ListView]

        // remove this after testing!!
        if(Controller.state.equations.isEmpty) {
            val stream = getActivity.openFileInput("new")
            val es = ParserXmlTRS.parse(stream)
            Controller.setES(es)
        }
        equationListView.setAdapter(new EquationArrayAdapter(getActivity, Controller.state.equations))

        equationListView.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(view: View, event: MotionEvent) : Boolean = {

                event.getAction() match {

                    case _ => false
                }
            }
        })



        return view
    }

    def orientRL(equation: EquationView): Unit = {

    }

    def orientLR(equation: EquationView): Unit = {

    }
}