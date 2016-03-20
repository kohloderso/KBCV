package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view._
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.views.EquationView
import ck.kbcv.{Controller, R}
import term.parser.ParserXmlTRS

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
        val equationsRV = view.findViewById(R.id.rvEquations).asInstanceOf[RecyclerView]

        // remove this after testing!!
        if(Controller.state.equations.isEmpty) {
            val stream = getActivity.openFileInput("new")
            val es = ParserXmlTRS.parse(stream)
            Controller.setES(es)
        }
        equationsRV.setAdapter(new EquationsAdapter(Controller.state.equations))
        equationsRV.setLayoutManager(new LinearLayoutManager(getActivity))

        return view
    }

    def orientRL(equation: EquationView): Unit = {

    }

    def orientLR(equation: EquationView): Unit = {

    }
}