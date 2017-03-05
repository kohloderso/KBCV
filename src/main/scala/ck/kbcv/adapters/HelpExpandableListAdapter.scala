package ck.kbcv.adapters

import java.util

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.BaseExpandableListAdapter

class HelpExpandableListAdapter(context: Context, titles: util.List[String], details: util.HashMap[String, util.List[String]]) extends BaseExpandableListAdapter {

  override def getChildId(listPosition: Int, expandedListPosition: Int): Long = {
    expandedListPosition
  }

  override def getChild(listPosition: Int, expandedListPosition: Int): AnyRef = {
    details.get(titles.get(listPosition)).get(expandedListPosition)
  }

  override def getGroupView(i: Int, b: Boolean, view: View, viewGroup: ViewGroup): View = ???

  override def getGroupCount: Int = ???

  override def isChildSelectable(i: Int, i1: Int): Boolean = ???

  override def getGroupId(i: Int): Long = ???

  override def getGroup(i: Int): AnyRef = ???

  override def getChildrenCount(i: Int): Int = ???

  override def hasStableIds: Boolean = ???

  override def getChildView(i: Int, i1: Int, b: Boolean, view: View, viewGroup: ViewGroup): View = ???
}
