package ck.kbcv.fragments.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.ExpandableListView
import ck.kbcv.R
import ck.kbcv.activities.NavigationDrawerActivity
import ck.kbcv.adapters.HelpExpandableListAdapter


class HelpCompletion extends Fragment {

  override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
    val view = inflater.inflate( R.layout.help_completion, container, false )
    val expandableList = view.findViewById(R.id.expandable_list).asInstanceOf[ExpandableListView]
    val titles = getResources.getStringArray(R.array.completion_groups).toList
    val details_export = (getResources.getStringArray(R.array.export_content)).toList
    val details_gestures = (getResources.getStringArray(R.array.gesture_content)).toList
    val details_autocompletion = getResources.getStringArray(R.array.automatic_comp_content).toList
    val details_compcheck = getResources.getStringArray(R.array.check_content).toList
    val details = List(details_export, details_gestures, details_autocompletion, details_compcheck)
    val adapter = new HelpExpandableListAdapter(getActivity, titles, details)
    expandableList.setAdapter(adapter)

    setHasOptionsMenu(true)
    val activity = getActivity.asInstanceOf[NavigationDrawerActivity]
    activity.drawerToggle.setDrawerIndicatorEnabled(false)

    return view
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if(item.getItemId == android.R.id.home) {
      getActivity().onBackPressed()
      true
    } else super.onOptionsItemSelected(item)
  }



}
