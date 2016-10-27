package ck.kbcv.adapters

import android.content.{ClipData, Context}
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View.OnLongClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import ck.kbcv.adapters.EquationRuleAdapter.{ItemClickListener, ViewHolder}
import ck.kbcv.views.TermPairView
import ck.kbcv.views.TermPairView.OnDropListener
import term.reco._
import term.util._

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object EquationRuleAdapter {
    class ViewHolder(view: View, onItemClickListener: ItemClickListener) extends RecyclerView.ViewHolder(view) with View.OnClickListener with View.OnLongClickListener {
        val indexView = itemView.findViewById(ck.kbcv.R.id.indexView).asInstanceOf[TextView]
        val equationView = itemView.findViewById(ck.kbcv.R.id.equationView).asInstanceOf[TermPairView]
        val selectedOverlay = itemView.findViewById(ck.kbcv.R.id.selected_overlay)
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)

        override def onClick(view: View): Unit = {
            Log.d("Click", "Item clicked. Position: " + getAdapterPosition)
            onItemClickListener.onItemClicked(getAdapterPosition)
        }

        override def onLongClick(view: View): Boolean = {
            Log.d("Click", "Item long clicked. Position: " + getAdapterPosition)
            //onItemClickListener.onItemLongClicked(getAdapterPosition)
            // TODO make dragshadow larger/better
            val data = ClipData.newPlainText("test", "")
            val shadow = new View.DragShadowBuilder(itemView)
            //view.startDrag(data, shadow, null, 0)
            true
        }
    }

    trait ItemClickListener {
        def onItemClicked(position: Int)

        def onItemLongClicked(position: Int) // TODO not needed anymore
    }
}

/**
 *
 *
 * @param fragment: Fragment that handles selection
 * @param layoutId: layout for one equation or rule
 */
class EquationRuleAdapter[TP <: TermPair](is: TreeMap[Int,TP], fragment: Fragment, layoutId: Int) extends SelectableAdapter[ViewHolder] {
    type ITP = (Int, TP)
    type ITM = TreeMap[Int, TP]
    val itemClickListener = fragment.asInstanceOf[ItemClickListener]
    private val TAG = "EquationRuleAdapter"
    private val mBuffer: mutable.Buffer[(Int, TP)] = ListBuffer.empty ++= is.toList // is.toBuffer doesn't provide remove and indexOf functions that I need
    private var markedItem: Int = -1
    private var context: Context = null

    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = {
        context = parent.getContext
        val itemView = LayoutInflater.from(context).inflate(layoutId, parent, false)
        new ViewHolder(itemView, itemClickListener)
    }

    // populate data into the e_item through holder
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        val item = mBuffer(position)
        viewHolder.indexView.setText(item._1.toString)
        viewHolder.equationView.index = item._1
        viewHolder.equationView.setTermPair(item._2)
        if(isSelected(position)) {
            viewHolder.selectedOverlay.setVisibility(View.VISIBLE)
            viewHolder.selectedOverlay.setBackgroundColor(ContextCompat.getColor(context, ck.kbcv.R.color.selected_overlay))
        } else if(markedItem == position) {
            viewHolder.selectedOverlay.setVisibility(View.VISIBLE)
            viewHolder.selectedOverlay.setBackgroundColor(ContextCompat.getColor(context, ck.kbcv.R.color.marked_overlay))
        }
        else {// we need to show the "normal" state
            viewHolder.selectedOverlay.setVisibility(View.INVISIBLE)
        }
    }

    def getItem(position: Int): ITP = {
        mBuffer(position)
    }

    def getItems(positions: ListBuffer[Int]): ITM = {
        var items = new ITM
        for(position <- positions) {
            items += mBuffer(position)
        }
        items
    }

    def removeItem(iTerm: ITP): Unit = {
        if (mBuffer.contains(iTerm)) {
            val position = mBuffer.indexOf(iTerm)
            mBuffer.remove(position)
            notifyItemRemoved(position)
        }
    }

    def removeItem(position: Int): Unit = {
        mBuffer.remove(position)
        notifyItemRemoved(position)
    }


    def addItem(iTerm: ITP): Unit = {
        mBuffer.append(iTerm)
        notifyItemInserted(mBuffer.size-1)
    }

    def updateInsertItem(iTerm: ITP): Unit = {
        var i = 0
        while(i < mBuffer.size && mBuffer(i)._1 < iTerm._1) i+=1
        // does an equation with the same index exist? => overwrite it
        if(i < mBuffer.size && mBuffer(i)._1 == iTerm._1) {
            mBuffer(i) = iTerm
            notifyItemChanged(i)
        } else {
            mBuffer.insert(i, iTerm)
            notifyItemInserted(i)
        }
    }

    def updateItems(newTermPairs: ITM): Unit = {
        // remove items that are not in the new IES
        for(oldT <- mBuffer) {
            if(!newTermPairs.contains(oldT._1)) removeItem(oldT)
        }
        // add all items that are new or have changed
        for(newT <- newTermPairs) {
            if(!mBuffer.contains(newT)) updateInsertItem(newT)    // TODO: does this find changed equations?
        }

    }

    override def getItemCount(): Int = {
        mBuffer.size
    }

    def setNewItems(newTermPairs: ITM): Unit = {
        mBuffer.clear()
        mBuffer ++= newTermPairs
        notifyDataSetChanged()
    }

    def markItem(position: Int): Unit = {
        val old = markedItem
        markedItem = position
        if(old >= 0) notifyItemChanged(old)
        notifyItemChanged(markedItem)
    }

    def unmarkItem(): Unit = {
        val old = markedItem
        markedItem = -1
        notifyItemChanged(old)
    }

}

class EquationsAdapter(ies: IES, fragment: Fragment) extends EquationRuleAdapter[E](ies, fragment, ck.kbcv.R.layout.e_item) {
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        super.onBindViewHolder(viewHolder, position)
        if(fragment.isInstanceOf[OnDropListener]) {
            viewHolder.equationView.ondDropListener = fragment.asInstanceOf[OnDropListener]
        }
    }

}


class RulesAdapter(itrs: ITRS, fragment: Fragment) extends EquationRuleAdapter[R](itrs, fragment, ck.kbcv.R.layout.r_item) {

    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.itemView.setOnLongClickListener(new OnLongClickListener {
            override def onLongClick(v: View): Boolean = {
                Log.d("Click", "Item long clicked. Position: " + position)
                //onItemClickListener.onItemLongClicked(getAdapterPosition)
                // TODO make dragshadow larger/better
                val data = ClipData.newPlainText("Rule", viewHolder.indexView.getText)
                val shadow = new View.DragShadowBuilder(v)
                v.startDrag(data, shadow, null, 0)
                true
            }
        })
    }


}


