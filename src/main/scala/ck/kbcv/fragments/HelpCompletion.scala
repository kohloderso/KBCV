package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import ck.kbcv.R


class HelpCompletion extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_equation_editor, container, false )

    setHasOptionsMenu(true)
    getActivity.asInstanceOf[AppCompatActivity].getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    return view
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if(item.getItemId == android.R.id.home) {
      getActivity().onBackPressed()
      true
    } else super.onOptionsItemSelected(item)
  }



}
