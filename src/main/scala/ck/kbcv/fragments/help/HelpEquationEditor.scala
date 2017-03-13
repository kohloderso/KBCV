package ck.kbcv.fragments.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.ExpandableListView
import ck.kbcv.R
import ck.kbcv.activities.NavigationDrawerActivity
import ck.kbcv.adapters.HelpExpandableListAdapter


class HelpEquationEditor extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_equation_editor, container, false )
    val expandableList = view.findViewById(R.id.expandable_list).asInstanceOf[ExpandableListView]
    val titles = getResources.getStringArray(R.array.eq_groups).toList
    val details_general = List(getResources.getString(R.string.eq_general))
    val details_saving = getResources.getStringArray(R.array.eq_save).toList
    val details_symbols = getResources.getStringArray(R.array.eq_symbols).toList
    val details_equations = getResources.getStringArray(R.array.eq_equations).toList
    val details = List(details_general, details_saving, details_symbols, details_equations)
    val adapter = new HelpExpandableListAdapter(getActivity, titles, details)
    expandableList.setAdapter(adapter)


    setHasOptionsMenu(true)
    getActivity.asInstanceOf[NavigationDrawerActivity].drawerToggle.setDrawerIndicatorEnabled(false)
    return view
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if(item.getItemId == android.R.id.home) {
      getActivity().onBackPressed()
      true
    } else super.onOptionsItemSelected(item)
  }

}
