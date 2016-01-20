package ck.kbcv

import android.content.{ClipData, Context}
import android.util.{DisplayMetrics, Log, AttributeSet}
import android.view.{Gravity, DragEvent, View}
import android.widget.{TextView, LinearLayout, ImageView, EditText}

/**
 * Created by Christina on 01.01.2016.
 */
class EquationDropzone(context: Context, attrs: AttributeSet) extends ImageView(context, attrs) {
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
            val symbol = clipData.getItemAt(0).getText
            val symbolView = new TextView(context)
            symbolView.setText(symbol)
            symbolView.setGravity(Gravity.CENTER)
            val brace1 = new TextView(context)
            brace1.setText("(")
            brace1.setGravity((Gravity.CENTER))
            val brace2 = new TextView(context)
            brace2.setText(")")
            brace2.setGravity(Gravity.CENTER)
            val dropZone = new EquationDropzone(context, null)
            equation.addView(brace2, index)
            equation.addView(dropZone, index)
            equation.addView(brace1, index)
            equation.addView(symbolView, index)
        }
    }



    override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Unit = {
        val desiredWidth = 40
        val desiredHeight = 40

        val metrics = getContext().getResources().getDisplayMetrics()
        val pixelsWidth = (metrics.density * desiredWidth + 0.5f).toInt
        val pixelsHeight = (metrics.density * desiredHeight + 0.5f).toInt

        // maybe add some more sophisticated stuff here?

        setMeasuredDimension(pixelsWidth, pixelsHeight)
    }


}
