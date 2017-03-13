package ck.kbcv.fragments.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.ExpandableListView
import ck.kbcv.R
import ck.kbcv.activities.NavigationDrawerActivity
import ck.kbcv.adapters.HelpExpandableListAdapter


class HelpSettings extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_settings, container, false )
    val expandableList = view.findViewById(R.id.expandable_list).asInstanceOf[ExpandableListView]
    val titles = getResources.getStringArray(R.array.settings_groups).toList
    val details_autocomp = List(getResources.getString(R.string.settings_autocomp))
    val details_cache = List(getResources.getString(R.string.settings_cache))
    val details_rounds = List(getResources.getString(R.string.settings_rounds))
    val details_variables = List(getResources.getString(R.string.settings_variables))
    val details_functions = List(getResources.getString(R.string.settings_functions))
    val details = List(details_autocomp, details_cache, details_rounds, details_variables, details_functions)
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
