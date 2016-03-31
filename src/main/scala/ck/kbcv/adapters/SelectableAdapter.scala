package ck.kbcv.adapters


import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder

import scala.collection.mutable.ListBuffer

/**
 * Created by Christina on 23.03.2016.
 */
trait SelectableAdapter[VH <: ViewHolder] extends RecyclerView.Adapter[VH] {

    val selectedItems = new ListBuffer[Int]

    def isSelected(position: Int): Boolean = {
        selectedItems.contains(position)
    }

    def toggleSelection(position: Int): Unit = {
        isSelected(position) match {
            case true => selectedItems -= position
            case false => selectedItems += position
        }
        notifyItemChanged(position)
    }

    def clearSelection(): Unit = {
        for(position <- selectedItems) {
            selectedItems -= position
            notifyItemChanged(position)
        }
    }

}
