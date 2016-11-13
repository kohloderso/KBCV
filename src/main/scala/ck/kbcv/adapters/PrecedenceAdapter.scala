package ck.kbcv.adapters

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import ck.kbcv.R
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.PrecedenceAdapter.ViewHolder
import term.Term.F
import term.lpo.Precedence

import scala.collection.mutable.ListBuffer


object PrecedenceAdapter {
    class ViewHolder(itemView: View, itemClickListener: ItemClickListener) extends RecyclerView.ViewHolder(itemView) with OnClickListener {
        val textView = itemView.findViewById(R.id.textView).asInstanceOf[TextView]
        itemView.setOnClickListener(this)

        def setText(text: String): Unit = {
            textView.setText(text)
        }

        override def onClick(view: View): Unit = {
            itemClickListener.onItemClicked(getAdapterPosition)
        }
    }
}

class PrecedenceAdapter(precedence: Precedence, activity: Activity)  extends SelectableAdapter[ViewHolder] {
    private var context: Context = null
    val itemClickListener = activity.asInstanceOf[ItemClickListener]
    private val mBuffer = ListBuffer.empty ++= precedence.toList
    private var markedItem: Int = -1


    override def getItemCount: Int = mBuffer.size

    def getMarkedItem(): (F, F) = {
        getItem(markedItem)
    }

    def addItem(item: (F, F)): Unit = {
        if(!mBuffer.contains(item)) {
            mBuffer.append(item)
            notifyItemInserted(mBuffer.size-1)
        }
    }

    def removeItem(item: (F, F)): Unit = {
        if (mBuffer.contains(item)) {
            val position = mBuffer.indexOf(item)
            mBuffer.remove(position)
            notifyItemRemoved(position)
        }
    }

    def updateItems(precedence: Precedence): Unit = {
        val items = precedence.toList
        // remove items that are not in the new List
        for(oldT <- mBuffer) {
            if(!items.contains(oldT)) removeItem(oldT)
        }
        // add all items that are new or have changed
        for(newT <- items) {
            if(!mBuffer.contains(newT)) addItem(newT)
        }
    }

    def getItem(position: Int): (F, F) = {
        mBuffer(position)
    }

    def markItem(position: Int): Unit = {
        val old = markedItem
        markedItem = position
        if(old >= 0) notifyItemChanged(old)
        notifyItemChanged(markedItem)
    }

    def unmarkItem(): Unit = {
        if(markedItem >= 0) {
            val old = markedItem
            markedItem = -1
            notifyItemChanged(old)
        }
    }

    override def onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder = {
        context = parent.getContext
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.precedence_item, parent, false)
        new PrecedenceAdapter.ViewHolder(view, itemClickListener)
    }

    override def onBindViewHolder(vh: ViewHolder, position: Int): Unit = {
        val (f1, f2) = mBuffer(position)
        val text = "<font color=#aeea00>" + f1 + "</font> <font> > </font> <font color=#aeea00>" + f2 + "</font>"
        vh.textView.setText(Html.fromHtml(text))
        if(isSelected(position)) {
            vh.textView.setBackgroundColor(ContextCompat.getColor(context, ck.kbcv.R.color.selected_overlay))
        } else if(markedItem == position) {
            //vh.selectedOverlay.setVisibility(View.VISIBLE)
            vh.textView.setBackgroundColor(ContextCompat.getColor(context, ck.kbcv.R.color.marked_overlay))
        }
        else {// we need to show the "normal" state
           vh.textView.setBackgroundColor(ContextCompat.getColor(context, ck.kbcv.R.color.cardview_light_background))
        }
    }

}
