package ck.kbcv.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import ck.kbcv.R
import ck.kbcv.adapters.EquationsAdapter.{ItemClickListener, ViewHolder}
import ck.kbcv.views.EquationView
import term.util.E

import scala.collection.mutable

/**
 * Created by Christina on 20.03.2016.
 */
object EquationsAdapter {
    class ViewHolder(itemView: View, onItemClickListener: ItemClickListener) extends RecyclerView.ViewHolder(itemView) with View.OnClickListener with View.OnLongClickListener {
        val equationView = itemView.findViewById(R.id.equationView).asInstanceOf[EquationView]
        val selectedOverlay = itemView.findViewById(R.id.selected_overlay)
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)

        override def onClick(view: View): Unit = {
            Log.d("Click", "Item clicked. Position: " + getAdapterPosition)
            onItemClickListener.onItemClicked(getAdapterPosition)
        }

        override def onLongClick(view: View): Boolean = {
            Log.d("Click", "Item long clicked. Position: " + getAdapterPosition)
            onItemClickListener.onItemLongClicked(getAdapterPosition)
            true
        }
    }

    trait ItemClickListener {
        def onItemClicked(position: Int)

        def onItemLongClicked(position: Int)
    }
}

class EquationsAdapter(mEquations: mutable.Buffer[E], itemClickListener: ItemClickListener) extends SelectableAdapter[ViewHolder] {
    private val TIMEOUT = 3000 // 3 sec
    private val TAG = "Equations_Adapter"

    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = {
        //val itemView = LayoutInflater.from(parent.getContext).inflate(R.layout.e_item_view, parent, false)
        val itemView = LayoutInflater.from(parent.getContext).inflate(R.layout.item, parent, false)
        new ViewHolder(itemView, itemClickListener)
    }

    // populate data into the item through holder
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        val equation = mEquations(position)
        viewHolder.equationView.setEquation(equation)
       if(isSelected(position)) {
           viewHolder.selectedOverlay.setVisibility(View.VISIBLE)
        } else {// we need to show the "normal" state
           viewHolder.selectedOverlay.setVisibility(View.INVISIBLE)
        }
    }

    def getItem(position: Int): E = {
        mEquations(position)
    }

    def removeItem(equation: E): Unit = {
        if (mEquations.contains(equation)) {
            val position = mEquations.indexOf(equation)
            mEquations.remove(position)
            notifyItemRemoved(position)
        }
    }

    def removeItem(position: Int): Unit = {
        Log.d(TAG, "removed Item")
        val item = mEquations(position)
        if (mEquations.contains(item)) {
            mEquations.remove(position)
            notifyItemRemoved(position)
        }
    }

    def addItem(equation: E): Unit = {
        mEquations.append(equation)
        notifyItemInserted(mEquations.size-1)
    }

    override def getItemCount(): Int = {
        mEquations.size
    }


}

