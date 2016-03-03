package ck.kbcv.views

import android.content.{ClipData, Context}
import android.util.{AttributeSet, Log}
import android.view.{DragEvent, Gravity, View}
import android.widget.{ImageView, LinearLayout, TextView}
import ck.kbcv.R

/**
 * Created by Christina on 01.01.2016.
 */
class DropView(context: Context, attrs: AttributeSet) extends ImageView(context, attrs) {
    val TAG = "EquationDropzone"
    this.setBackgroundColor(getResources.getColor(R.color.colorPrimary))
    this.setOnDragListener(new View.OnDragListener() {
        override def onDrag(v: View, event: DragEvent): Boolean = {
            val action = event.getAction
            Log.d(TAG, "Action: " + action)
            action match {
                case DragEvent.ACTION_DRAG_STARTED => val test = event.getClipDescription//  Do nothing
                case DragEvent.ACTION_DRAG_ENTERED => v.setBackgroundColor(getResources.getColor(R.color.colorAccent))
                case DragEvent.ACTION_DRAG_EXITED => v.setBackgroundColor(getResources.getColor(R.color.colorPrimary))
                case DragEvent.ACTION_DRAG_ENDED => v.setBackgroundColor(getResources.getColor(R.color.colorPrimary))
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
        if(clipData.getDescription.getLabel.equals("variable")) {
            val symbol = clipData.getItemAt(0).getText
            val symbolView = new TextView(context)
            symbolView.setText(symbol)
            equation.addView(symbolView, index)
        } else if(clipData.getDescription.getLabel.equals("function")) {
            val functionSymbol = clipData.getItemAt(0).getText.toString
            val arity = clipData.getItemAt(1).getText.toString.toInt

            val symbolView = new TextView(context)
            symbolView.setText(functionSymbol)
            symbolView.setGravity(Gravity.CENTER)

            if(arity != 0) {
                val brace1 = new TextView(context)
                brace1.setText("(")
                brace1.setGravity((Gravity.CENTER))
                val brace2 = new TextView(context)
                brace2.setText(")")
                brace2.setGravity(Gravity.CENTER)
                equation.addView(brace2, index)

                val dropZone = new DropView(context, null)
                equation.addView(dropZone, index)

                for(i <- 2 to arity) {
                    val comma = new TextView(context)
                    comma.setText(",")
                    comma.setGravity(Gravity.CENTER)
                    equation.addView(comma, index)

                    val dropZone = new DropView(context, null)
                    equation.addView(dropZone, index)
                }
                equation.addView(brace1, index)
            }
            equation.addView(symbolView, index)
        }
    }



    override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Unit = {
        val desiredWidth = 40
        val desiredHeight = 40

        val metrics = getContext().getResources().getDisplayMetrics()
        val pixelsWidth = (metrics.density * desiredWidth + 0.5f).toInt
        val pixelsHeight = (metrics.density * desiredHeight + 0.5f).toInt

        // TODO maybe add some more sophisticated stuff here?

        setMeasuredDimension(getResources.getDimension(R.dimen.drop_view).toInt, getResources.getDimension(R.dimen.drop_view).toInt)
    }


}
