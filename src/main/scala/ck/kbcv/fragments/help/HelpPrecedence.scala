package ck.kbcv.fragments.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.ExpandableListView
import ck.kbcv.R
import ck.kbcv.adapters.HelpExpandableListAdapter


class HelpPrecedence extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_precedence, container, false )
    val expandableList = view.findViewById(R.id.expandable_list).asInstanceOf[ExpandableListView]
    val titles = getResources.getStringArray(R.array.precedence_groups).toList
    val details_general = List(getResources.getString(R.string.prec_general))
    val details_add = List(getResources.getString(R.string.prec_add))
    val details_edit = List(getResources.getString(R.string.prec_edit))
    val details = List(details_general, details_add, details_edit)
    val adapter = new HelpExpandableListAdapter(getActivity, titles, details)

    expandableList.setAdapter(adapter)
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
