package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view._
import ck.kbcv.R

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