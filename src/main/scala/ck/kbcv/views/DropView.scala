package ck.kbcv.views

import android.content.{ClipData, Context}
import android.support.v7.widget.CardView
import android.util.{AttributeSet, Log}
import android.view.{DragEvent, View}
import android.widget.LinearLayout
import ck.kbcv.R
import term.{Fun, Var}

class DropView(context: Context, attrs: AttributeSet, symbolListener: DropSymbolsEditor = null) extends CardView(context, attrs) {
    val TAG = "EquationDropzone"
    this.setCardBackgroundColor(getResources.getColor(R.color.colorSecondaryBright))
    this.setOnDragListener(new View.OnDragListener() {
        override def onDrag(v: View, event: DragEvent): Boolean = {
            val action = event.getAction
            Log.d(TAG, "Action: " + action)
            action match {
                case DragEvent.ACTION_DRAG_STARTED => val test = event.getClipDescription//  Do nothing
                case DragEvent.ACTION_DRAG_ENTERED => v.setBackgroundColor(getResources.getColor(R.color.colorPrimaryBright))
                case DragEvent.ACTION_DRAG_EXITED => v.setBackgroundColor(getResources.getColor(R.color.colorAccent))
                case DragEvent.ACTION_DRAG_ENDED => v.setBackgroundColor(getResources.getColor(R.color.colorAccent))
                case DragEvent.ACTION_DROP =>
                    replaceDropzone(event.getClipData)
                case _ =>
            }
            true
        }
    })



    def replaceDropzone(clipData: ClipData): Unit = {
        val equation = getParent.asInstanceOf[LinearLayout]
        val index = equation.indexOfChild(this)
        equation.removeViewAt(index)
        val term = clipData.getDescription.getLabel match {
            case "variable" => new Var(clipData.getItemAt(0).getText.toString)
            case "function" =>
                val functionSymbol = clipData.getItemAt(0).getText.toString
                val arity = clipData.getItemAt(1).getText.toString.toInt
                new Fun(functionSymbol, List.fill(arity)(null))
            case "precedence" => new Fun(clipData.getItemAt(0).getText.toString, List.empty)
        }
        equation.addView(new TermView(context, attrs, term, symbolListener), index)
        symbolListener.onSymbolDropped()
    }



    override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Unit = {

        setMeasuredDimension(getResources.getDimension(R.dimen.drop_view).toInt, getResources.getDimension(R.dimen.drop_view).toInt)
    }


}
