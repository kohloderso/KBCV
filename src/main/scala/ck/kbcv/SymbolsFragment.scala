package ck.kbcv

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{ View, ViewGroup, LayoutInflater }

/**
 * Created by Christina on 05.12.2015.
 */
class SymbolsFragment extends Fragment {
    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        return inflater.inflate( R.layout.symbols_fragment, container, false )
    }

}
