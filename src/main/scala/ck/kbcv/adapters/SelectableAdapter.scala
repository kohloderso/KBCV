package ck.kbcv.adapters


import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder

import scala.collection.mutable.ListBuffer

trait SelectableAdapter[VH <: ViewHolder] extends RecyclerView.Adapter[VH] {
    var singleSelection = false
    var selectedItems = new ListBuffer[Int]

    def isSelected(position: Int): Boolean = {
        selectedItems.contains(position)
    }

    def toggleSelection(position: Int): Unit = {
        isSelected(position) match {
            case true => selectedItems -= position
            case false => {
                if(singleSelection) {
                    clearSelection()
                }
                selectedItems += position
            }
        }
        notifyItemChanged(position)
    }

    def clearSelection(): Unit = {
        for(position <- selectedItems) {
            selectedItems -= position
            notifyItemChanged(position)
        }
    }

    def selectAll(): Unit = {
        if (!singleSelection) {
            val count = getItemCount
            selectedItems = ListBuffer.empty ++ List.range(0, count)
            notifyDataSetChanged()
        }
    }

    def moveSelection(removedItem: Int): Unit = {
        for (item <- selectedItems.clone()) {
            if (item > removedItem) {
                selectedItems -= item
                selectedItems += item - 1
            }
        }
    }

}
