package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import ck.kbcv.R


class WelcomePrecedencesFragment extends Fragment {

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate(R.layout.welcome_precedences_fragment, container, false)

        view
    }


}
