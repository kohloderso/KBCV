package ck.kbcv.views

import android.content.{ClipData, Context}
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.{Log, AttributeSet}
import android.view.{DragEvent, View}
import android.view.View.OnClickListener
import android.widget.LinearLayout
import ck.kbcv.R
import term.{Fun, Term, Var}


class TermView(context: Context, attrs: AttributeSet, term: Term = null, equationEditView: EquationEditView = null) extends LinearLayout(context: Context, attrs: AttributeSet){
    if(equationEditView != null) {
        this.setOnDragListener(new View.OnDragListener() {
            override def onDrag(v: View, event: DragEvent): Boolean = {
                val action = event.getAction
                action match {
                    case DragEvent.ACTION_DRAG_STARTED => val test = event.getClipDescription//  Do nothing
                    case DragEvent.ACTION_DRAG_ENTERED => v.setBackgroundColor(getResources.getColor(R.color.colorPrimaryBright))
                    case DragEvent.ACTION_DRAG_EXITED => v.setBackgroundColor(Color.WHITE)
                    case DragEvent.ACTION_DRAG_ENDED => v.setBackgroundColor(Color.WHITE)
                    case DragEvent.ACTION_DROP => replaceWithDropped(event.getClipData)
                    case _ =>
                }
                true
            }
        })
    }

    if(term == null) {
        this.addView(new DropView(context, attrs, equationEditView))
    } else {
        term match {
            case Var(x) => this.addView(new VarView(context, attrs, x))
            case Fun(f, ts) => this.addView(new FunView(context, attrs, f, ts, equationEditView))
        }
    }

    def replaceWithDropped(clipData: ClipData): Unit = {
        val term = clipData.getDescription.getLabel match {
            case "variable" => new Var(clipData.getItemAt(0).getText.toString)
            case "function" =>
                val functionSymbol = clipData.getItemAt(0).getText.toString
                val arity = clipData.getItemAt(1).getText.toString.toInt
                new Fun(functionSymbol, List.fill(arity)(null))
        }
        this.removeAllViews()
        this.addView(new TermView(context, attrs, term, equationEditView))
    }


}
