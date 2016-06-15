package ck.kbcv.adapters

import android.content.ClipData
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams
import android.view.{LayoutInflater, MotionEvent, View, ViewGroup}
import android.widget.Button
import ck.kbcv.R
import term.Term.{F, V}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object SymbolAdapter {

    class ViewHolder(itemView: View) extends RecyclerView.ViewHolder(itemView) {
        val button = itemView.asInstanceOf[Button]
        button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        def setText(text: String): Unit = {
            button.setText(text)
        }
    }

}


abstract class SymbolAdapter[S](symbols: Set[S]) extends RecyclerView.Adapter[SymbolAdapter.ViewHolder]  {

    private val TAG = "SymbolAdapter"
    val mBuffer: mutable.Buffer[S] = ListBuffer.empty ++= symbols


    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolAdapter.ViewHolder = {
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.drag_button, parent, false)
        new SymbolAdapter.ViewHolder(view)
    }

    override def getItemCount: Int = {
        mBuffer.size
    }

    def addItem(item: S): Unit = {
        if(!mBuffer.contains(item)) {
            mBuffer.append(item)
            notifyItemInserted(mBuffer.size-1)
        }
    }

    def removeItem(item: S): Unit = {
        if (mBuffer.contains(item)) {
            val position = mBuffer.indexOf(item)
            mBuffer.remove(position)
            notifyItemRemoved(position)
        }
    }

    def updateItems(items: Set[S]): Unit = {
        // remove items that are not in the new List
        for(oldT <- mBuffer) {
            if(!items.contains(oldT)) removeItem(oldT)
        }
        // add all items that are new or have changed
        for(newT <- items) {
            if(!mBuffer.contains(newT)) addItem(newT)
        }
    }
}

class VariableAdapter(variables: Set[V]) extends SymbolAdapter[V](variables) {
    override def onBindViewHolder(vh: SymbolAdapter.ViewHolder, i: Int): Unit = {
        vh.setText(mBuffer(i))
        setOnTouchVariable(vh.button, mBuffer(i))
    }

    def setOnTouchVariable(button: Button, variable: V): Unit = {
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("variable", variable)
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }
}

class FunctionAdapter(functions: Set[(F, Int)]) extends SymbolAdapter[(F, Int)](functions) {
    override def onBindViewHolder(vh: SymbolAdapter.ViewHolder, i: Int): Unit = {
        vh.setText(mBuffer(i)._1)
        setOnTouchFunction(vh.button, mBuffer(i))
    }

    def setOnTouchFunction(button: Button, f: (F, Int)): Unit = {
        val (function, arity) = f
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("function", function)
                    data.addItem(new ClipData.Item(arity.toString))
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }
}
