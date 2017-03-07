package ck.kbcv.adapters

import java.util

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseExpandableListAdapter, TextView}
import ck.kbcv.R

class HelpExpandableListAdapter(context: Context, titles: List[String], details: List[List[String]]) extends BaseExpandableListAdapter {

  override def getChildId(listPosition: Int, expandedListPosition: Int): Long = {
    expandedListPosition
  }

  override def getChild(listPosition: Int, expandedListPosition: Int): AnyRef = {
    details(listPosition)(expandedListPosition)
  }

  override def getGroupView(listPosition: Int, isExpanded: Boolean, view: View, parent: ViewGroup): View = {
    val title = getGroup(listPosition).asInstanceOf[String]
    var convertView = view
    if(convertView == null) {
      val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
      convertView = layoutInflater.inflate(R.layout.expandable_list_group, null)
    }
    val titleTextView = convertView.findViewById(R.id.titleTextView).asInstanceOf[TextView]
    titleTextView.setText(title)
    convertView
  }

  override def getGroupCount: Int = {
    titles.length
  }

  override def isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean = false

  override def getGroupId(listPosition: Int): Long = {
    listPosition
  }

  override def getGroup(listPosition: Int): AnyRef = {
    titles(listPosition)
  }

  override def getChildrenCount(listPosition: Int): Int = {
    details(listPosition).length
  }

  override def hasStableIds: Boolean = false

  override def getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, view: View, parent: ViewGroup): View = {
    val expandedListText = getChild(listPosition, expandedListPosition).asInstanceOf[String]
    var convertView = view
    if(view == null) {
      val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
      convertView = layoutInflater.inflate(R.layout.expandable_list_item, null)
    }
    val expandedListTextView = convertView.findViewById(R.id.itemTextView).asInstanceOf[TextView]
    expandedListTextView.setText(expandedListText)
    convertView
  }
}
