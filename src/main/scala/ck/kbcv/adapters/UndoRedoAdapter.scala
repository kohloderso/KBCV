package ck.kbcv.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, TextView}
import ck.kbcv.{R, State}
import ck.kbcv.UndoRedoType._
import ck.kbcv.adapters.UndoRedoAdapter.{HistoryClickListener, ViewHolder}

import scala.collection.mutable


object UndoRedoAdapter {

    class ViewHolder(itemView: View, historyClickListener: HistoryClickListener) extends RecyclerView.ViewHolder(itemView) with View.OnClickListener {
        val iconView = itemView.findViewById(ck.kbcv.R.id.iconView).asInstanceOf[ImageView]
        //val indexView = itemView.findViewById(ck.kbcv.R.id.indexView).asInstanceOf[TextView]
        val textView = itemView.findViewById(ck.kbcv.R.id.textView).asInstanceOf[TextView]
        val selectedOverlay = itemView.findViewById(ck.kbcv.R.id.selected_overlay)
        itemView.setOnClickListener(this)

        override def onClick(view: View): Unit = {
            Log.d("Click", "Item clicked. Position: " + getAdapterPosition)
            historyClickListener.onItemClicked(getAdapterPosition)
        }

    }

    trait HistoryClickListener {
        def onItemClicked(position: Int)
    }

}

class UndoRedoAdapter(undoStack: mutable.Stack[State], redoStack: mutable.Stack[State], historyClickListener: HistoryClickListener) extends RecyclerView.Adapter[ViewHolder] {
    private val TAG = "UndoRedoAdapter"
    private val undos = undoStack.length
    private val redos = redoStack.length

    /**
     *
     * @param position
     * @return the state at this position, the type of action to get to this state (UNDO, REDO, CURR - current State) and
     *         the number of steps required to get to this state
     */
    def getItem(position: Int): (State, UndoRedoType, Int) = {
        if(position < redos) {
            val item = redoStack.apply(redos - position - 1)
            (item, REDO, redos - position)
        } else if(position == redos && undos > 0) {
            val item = undoStack.top
            (item, CURR, 0)
        } else if(position > redos && position < undos +  redos) {
            val item = undoStack(position - redos)
            (item, UNDO, position - redos)
        } else {
            (null, null, 0)
        }
    }
    override def getItemCount: Int = redos + undos

    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = {
        val itemView = LayoutInflater.from(parent.getContext).inflate(ck.kbcv.R.layout.history_item, parent, false)
        new ViewHolder(itemView, historyClickListener)
    }

    // populate data into the item through holder
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        val (state, t, _) = getItem(position)

        viewHolder.textView.setText(state.message)

        val imageR = t match {
            case UNDO => {
                viewHolder.selectedOverlay.setVisibility(View.INVISIBLE)
                R.drawable.ic_undo_black_18dp
            }
            case REDO => {
                viewHolder.selectedOverlay.setVisibility(View.INVISIBLE)
                R.drawable.ic_redo_black_18dp
            }
            case CURR => {
                viewHolder.selectedOverlay.setVisibility(View.VISIBLE)
                R.drawable.ic_done_black_18dp
            }
        }
        viewHolder.iconView.setImageResource(imageR)
    }


}
