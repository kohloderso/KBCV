package ck.kbcv.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.{LinearLayout, TextView}
import ck.kbcv.R
import term.Term._
import term.{Fun, Term}

import scala.collection.mutable


class FunView(context: Context, attrs: AttributeSet, funName: F, funArgs: List[Term], symbolListener: DropSymbolsEditor = null) extends LinearLayout(context: Context, attrs: AttributeSet) {
    this.setOrientation(LinearLayout.HORIZONTAL)


    val nameView = new TextView(context, attrs)
    nameView.setText(funName)
    nameView.setTextColor(getResources.getColor(R.color.textColorFunction))

    this.addView(nameView)

    if(!funArgs.isEmpty) {
        val bracketL = new TextView(context, attrs)
        bracketL.setText("(")
        val bracketR = new TextView(context, attrs)
        bracketR.setText(")")

        this.addView(bracketL)
        for(arg <- funArgs) {
            val termView = new TermView(context, attrs, arg, symbolListener)
            this.addView(termView)
            val comma = new TextView(context, attrs)
            comma.setText(", ")
            this.addView(comma)
        }
        this.removeViewAt(this.getChildCount-1) // remove last comma
        this.addView(bracketR)
    }

    override def setOnDragListener(listener: View.OnDragListener): Unit = {
        nameView.setOnDragListener(listener)
        nameView.setBackground(ContextCompat.getDrawable(context, R.drawable.dotted_line))
    }

    def containsDropZones(): Boolean = {
        if(funArgs.isEmpty) return false
        for(i <- 2 to 2+funArgs.size + funArgs.size -1 by 2){
            this.getChildAt(i) match {
                case _: DropView => return true
                case _: VarView =>
                case v: FunView => if(v.containsDropZones) return true
                case t: TermView => if(t.containsDropZones) return true
            }
        }
        false
    }

    def getTerm: Term = {
        val args = new mutable.MutableList[Term]
        for(i <- 2 to 2+funArgs.size + funArgs.size -1 by 2){
            this.getChildAt(i) match {
                case v: VarView => args += v.getTerm
                case f: FunView => args += f.getTerm
                case t: TermView => args += t.getTerm
                case _: DropView => args += null
            }
        }
        new Fun(funName, args.toList)
    }



}
