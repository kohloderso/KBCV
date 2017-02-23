package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import ck.kbcv.R


class WelcomeEquationsFragment extends Fragment {

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate(R.layout.welcome_equations_fragment, container, false)
        val eq_tv1 = view.findViewById(R.id.eq_tv1).asInstanceOf[TextView]
        eq_tv1.setText(Html.fromHtml(getString(R.string.eq1)))
        val eq_tv2 = view.findViewById(R.id.eq_tv2).asInstanceOf[TextView]
        eq_tv2.setText(Html.fromHtml(getString(R.string.eq2)))
        val eq_tv3 = view.findViewById(R.id.eq_tv3).asInstanceOf[TextView]
        eq_tv3.setText(Html.fromHtml(getString(R.string.eq3)))

        view
    }


}
