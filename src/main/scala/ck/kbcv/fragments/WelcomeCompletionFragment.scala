package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import ck.kbcv.views.RuleView
import ck.kbcv.{R, TR, TypedFindView}

class WelcomeCompletionFragment extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate(R.layout.welcome_completion_fragment, container, false)


    //val test = view.findViewById(R.id.equationView)
    //val rule2 = view.findViewById(R.id.r_item2)
    view
  }

}
