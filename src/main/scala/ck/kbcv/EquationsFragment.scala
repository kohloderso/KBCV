package ck.kbcv

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}

/**
 * Created by Christina on 09.12.2015.
 */
class EquationsFragment extends Fragment {
    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        return inflater.inflate( R.layout.equations_fragment, container, false )
    }

}