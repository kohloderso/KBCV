package ck.kbcv.fragments.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.ExpandableListView
import ck.kbcv.R
import ck.kbcv.activities.NavigationDrawerActivity
import ck.kbcv.adapters.HelpExpandableListAdapter


class HelpHistory extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_history, container, false )
    val expandableList = view.findViewById(R.id.expandable_list).asInstanceOf[ExpandableListView]
    val titles = getResources.getStringArray(R.array.history_groups).toList
    val details_undo = getResources.getStringArray(R.array.history_undo).toList
    val details_dialog = List(getResources.getString(R.string.history_dialog))
    val details = List(details_undo, details_dialog)
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
