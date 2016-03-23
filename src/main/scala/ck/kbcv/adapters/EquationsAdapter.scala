package ck.kbcv.adapters

import android.graphics.Color
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Button
import ck.kbcv.R
import ck.kbcv.adapters.EquationsAdapter.{ItemClickListener, ViewHolder}
import ck.kbcv.views.EquationView
import term.util.E

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by Christina on 20.03.2016.
 */
object EquationsAdapter {
    private val TIMEOUT = 3000 // 3 sec
    class ViewHolder(itemView: View, onItemClickListener: ItemClickListener) extends RecyclerView.ViewHolder(itemView) with View.OnClickListener with View.OnLongClickListener {
        val equationView = itemView.findViewById(R.id.equationView).asInstanceOf[EquationView]
        val undoButton = itemView.findViewById(R.id.undo_button).asInstanceOf[Button]
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
    val selectedIndices = new ListBuffer[Int]
    val itemsPendingRemoval = new mutable.ListBuffer[E]
    val handler = new Handler() // handler for running delayed runnables
    val pendingRunnables = new mutable.HashMap[E, Runnable]() // map of items to pending runnables

    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = {
        val itemView = LayoutInflater.from(parent.getContext).inflate(R.layout.e_item_view, parent, false)
        new ViewHolder(itemView, itemClickListener)
    }

    // populate data into the item through holder
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        val equation = mEquations(position)

        if(itemsPendingRemoval.contains(equation)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(Color.RED)
            viewHolder.equationView.setVisibility(View.GONE)
            viewHolder.undoButton.setVisibility(View.VISIBLE)
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                override def onClick(view: View): Unit = {
                    // user wants to undo the removal, let's cancel the pending task
                    val pendingRemovalRunnable = pendingRunnables.get(equation)
                    pendingRunnables.remove(equation)
                    pendingRemovalRunnable match {
                        case Some(runnable) => handler.removeCallbacks(runnable)
                        case None => // nothing
                    }
                    itemsPendingRemoval -= equation
                    // this will rebind the row in "normal" state
                    notifyItemChanged(mEquations.indexOf(equation))
                }
            })
        } else {
            // we need to show the "normal" state
            viewHolder.equationView.setEquation(equation)
            viewHolder.itemView.setBackgroundColor(Color.WHITE)
            viewHolder.equationView.setVisibility(View.VISIBLE)
            viewHolder.undoButton.setVisibility(View.GONE)
            viewHolder.undoButton.setOnClickListener(null)
        }
    }


    def pendingRemoval(position: Int): Unit = {
        val item = mEquations(position)
        if(!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval += item
            // this will redraw row in undo state
            notifyItemChanged(position)
            // let's create, store and post a runnable to remove the item
            val pendingRemovalRunnable = new Runnable {
                override def run(): Unit = remove(position)
            }
            handler.postDelayed(pendingRemovalRunnable, EquationsAdapter.TIMEOUT)
            pendingRunnables.put(item, pendingRemovalRunnable)
        }
    }

    def remove(position: Int): Unit = {
        Log.d("Swipe", "removed Item")
        val item = mEquations(position)
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval -= item
        }
        if (mEquations.contains(item)) {
            mEquations.remove(position)
            notifyItemRemoved(position)
        }
    }

    def isPendingRemoval(position: Int): Boolean = {
        val item = mEquations(position)
        itemsPendingRemoval.contains(item)
    }


    override def getItemCount(): Int = {
        mEquations.size
    }


}

