package ck.kbcv.views

import android.content.{ClipData, Context}
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View.OnDragListener
import android.view.{DragEvent, View}
import android.widget.LinearLayout
import ck.kbcv.R
import term.{Fun, Term, Var}


class TermView(context: Context, attrs: AttributeSet, term: Term = null, equationEditView: EquationEditView = null) extends LinearLayout(context: Context, attrs: AttributeSet) with OnDragListener{

    if(term == null) {
        this.addView(new DropView(context, attrs, equationEditView))
    } else {
        val v = term match {
            case Var(x) =>
                new VarView(context, attrs, x)
            case Fun(f, ts) =>
                new FunView(context, attrs, f, ts, equationEditView)
        }
        this.addView(v)
        if(equationEditView != null) v.setOnDragListener(this)
    }

    override def onDrag(v: View, event: DragEvent): Boolean = {
        val action = event.getAction
        action match {
            case DragEvent.ACTION_DRAG_STARTED => val test = event.getClipDescription //  Do nothing
            case DragEvent.ACTION_DRAG_ENTERED => v.setBackgroundColor(getResources.getColor(R.color.colorPrimaryBright))
            case DragEvent.ACTION_DRAG_EXITED => v.setBackground(ContextCompat.getDrawable(context, R.drawable.dotted_line))
            case DragEvent.ACTION_DRAG_ENDED => v.setBackground(ContextCompat.getDrawable(context, R.drawable.dotted_line))
            case DragEvent.ACTION_DROP => replaceWithDropped(event.getClipData)
            case _ =>
        }
        true
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
        equationEditView.onSymbolDropped()
    }

    def containsDropZones(): Boolean = {
       this.getChildAt(0) match {
           case _: DropView => true
           case _: VarView => false
           case f: FunView => f.containsDropZones()
           case t: TermView => t.containsDropZones()
        }
    }

    def getTerm: Term = {
        this.getChildAt(0) match {
            case v: VarView => v.getTerm
            case f: FunView => f.getTerm
            case t: TermView => t.getTerm
        }
    }
}
