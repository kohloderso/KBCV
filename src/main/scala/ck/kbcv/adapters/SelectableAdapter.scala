package ck.kbcv.adapters


import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder

import scala.collection.mutable.ListBuffer

trait SelectableAdapter[VH <: ViewHolder] extends RecyclerView.Adapter[VH] {
    var singleSelection = false
    val selectedItems = new ListBuffer[Int]

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

}
