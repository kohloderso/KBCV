package ck.kbcv.fragments

import android.content.ClipData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import ck.kbcv.views.EquationView
import ck.kbcv.{Controller, R}
import term.util.ES

/**
 * Created by Christina on 09.12.2015.
 */
class RulesFragment extends Fragment {
    val TAG = "RulesFragment"

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.rules_fragment, container, false )

        return view
    }

}