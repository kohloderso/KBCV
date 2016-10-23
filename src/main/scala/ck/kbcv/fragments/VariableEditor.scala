package ck.kbcv.fragments

import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.{GestureDetectorCompat, MotionEventCompat}
import android.util.Log
import android.view.View.OnTouchListener
import android.view._
import android.widget._
import ck.kbcv.{Controller, HorizontalFlowLayout, R}
import com.ogaclejapan.arclayout.ArcLayout

// TODO listen to clicks on the variables which will open a dialog to edit them
class VariableEditor extends Fragment with OnTouchListener {
    val TAG = "VariableEditor"
    var flowLayout: HorizontalFlowLayout = null
    var arcLayout: ArcLayout = null
    var gestureDetector: GestureDetectorCompat = null
    var plusButton: Button = null


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.variable_editor, container, false )
        flowLayout = view.findViewById(R.id.variableFlowLayout).asInstanceOf[HorizontalFlowLayout]
        arcLayout = view.findViewById(R.id.arcLayout).asInstanceOf[ArcLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnTouchListener(this)
        //arcLayout.setOnTouchListener(this)
        arcLayout.setVisibility(View.VISIBLE)
        gestureDetector = new GestureDetectorCompat(getContext, new MyGestureListener)
        setVariables()
        return view
    }

    def setVariables(): Unit = {
        val variables = Controller.state.variables

        flowLayout.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for(variable <- variables) {
            val varView = inflater.inflate(R.layout.var_view, flowLayout, false).asInstanceOf[TextView]
            varView.setText(variable)
            flowLayout.addView(varView)
        }
    }

    /**
     * compute center of the arcLayout, then compute the angle between the current point of touch and the center of the arcLayout
     * 0 means point of touch is in a vertical line above the center.  A positive angle indicates that the major axis of contact is oriented to the right.
     * A negative angle indicates that the major axis of contact is oriented to the left.
     */
    def computeSelectedArcButton(touchX: Float, touchY: Float): Button = {
        // compute the raw x and y coordinates of the origin of the arcLayout
        var location: Array[Int] = new Array[Int](2)
        arcLayout.getLocationOnScreen(location)
        val arcOrigin: Point = arcLayout.getOrigin
        arcOrigin.offset(location(0), location(1))

        val delta_x = touchX - arcOrigin.x
        val delta_y = arcOrigin.y - touchY
        val angle = (Math.toDegrees(Math.atan2(delta_x, delta_y)) + 270) % 360

        // find out which Button is closest to the current angle
        val degreesPerButton = 360f / arcLayout.getChildCount
        val pos = Math.round(angle / degreesPerButton) % arcLayout.getChildCount

        for (i <- 0 until arcLayout.getChildCount) {
            if (i != pos) arcLayout.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext, R.color.material_grey_600))
            else arcLayout.getChildAt(i).setBackgroundColor(ContextCompat.getColor(getContext, R.color.accent_material_light))
        }
        return null

    }


    override def onTouch(v: View, event: MotionEvent): Boolean = {

        if (v == plusButton) {
            val action = MotionEventCompat.getActionMasked(event)
            action match {
                case MotionEvent.ACTION_DOWN => arcLayout.setVisibility(View.VISIBLE); true
                case MotionEvent.ACTION_UP => Log.d("TouchTest", "touch" + event.getRawX + " " + event.getRawY); arcLayout.setVisibility(View.INVISIBLE); true
                case MotionEvent.ACTION_CANCEL => arcLayout.setVisibility(View.INVISIBLE); true
                case MotionEvent.ACTION_MOVE => computeSelectedArcButton(event.getRawX, event.getRawY); true
                case MotionEvent.ACTION_OUTSIDE => computeSelectedArcButton(event.getRawX, event.getRawY); true
                case _ => false
            }
        } else {
            false
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        override def onDown(event: MotionEvent): Boolean = {
            arcLayout.setVisibility(View.VISIBLE)
            true
        }

        override def onSingleTapUp(event: MotionEvent): Boolean = {
            arcLayout.setVisibility(View.INVISIBLE)
            true
        }
    }
}
