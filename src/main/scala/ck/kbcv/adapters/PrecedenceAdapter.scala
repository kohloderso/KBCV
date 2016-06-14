package ck.kbcv.adapters

import android.support.v7.widget.{CardView, RecyclerView}
import android.text.Html
import android.view.{LayoutInflater, ViewGroup, View}
import android.view.ViewGroup.LayoutParams
import android.widget.{TextView, Button}
import ck.kbcv.R
import ck.kbcv.adapters.PrecedenceAdapter.ViewHolder
import term.Term.F
import term.lpo.Precedence

import scala.collection.mutable.ListBuffer


object PrecedenceAdapter {
    class ViewHolder(itemView: View) extends RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById(R.id.textView).asInstanceOf[TextView]

        def setText(text: String): Unit = {
            textView.setText(text)
        }
    }
}

class PrecedenceAdapter(precedence: Precedence)  extends RecyclerView.Adapter[PrecedenceAdapter.ViewHolder] {

    private val mBuffer = ListBuffer.empty ++= precedence.toList


    override def getItemCount: Int = mBuffer.size

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

    override def onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder = {
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.precedence_item, parent, false)
        new PrecedenceAdapter.ViewHolder(view)
    }

    override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {
        val (f1, f2) = mBuffer(i)
        val text = "<font color=#ffab00>" + f1 + "</font> <font> < </font> <font color=#ffab00>" + f2 + "</font>"
        vh.textView.setText(Html.fromHtml(text))
    }

}
