package ck.kbcv.fragments

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.{GestureDetectorCompat, MotionEventCompat}
import android.util.Log
import android.view.View.{OnClickListener, OnTouchListener}
import android.view._
import android.widget._
import ck.kbcv.{Controller, HorizontalFlowLayout, OnSymbolsChangedListener, R}
import com.ogaclejapan.arclayout.ArcLayout

// TODO listen to clicks on the variables which will open a dialog to edit them
class VariableEditor extends Fragment with OnTouchListener {
    val TAG = "VariableEditor"
    var flowLayout: HorizontalFlowLayout = null
    var arcLayout: ArcLayout = null
    var gestureDetector: GestureDetectorCompat = null
    var plusButton: Button = null
    var currentState = ArcState.INITIAL
    var currentlySelected: String = null

    object ArcState extends Enumeration {
        val INITIAL, ARC_OPEN, LONGPRESS = Value
    }


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.variable_editor, container, false )
        flowLayout = view.findViewById(R.id.variableFlowLayout).asInstanceOf[HorizontalFlowLayout]
        arcLayout = view.findViewById(R.id.arcLayout).asInstanceOf[ArcLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnTouchListener(this)

        for (i <- 0 until arcLayout.getChildCount) {
            arcLayout.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    Controller.addVar(v.asInstanceOf[Button].getText.toString, getString(R.string.added_var))
                    val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                    symbolsListener.onVariablesChanged()
                    resetArcSelection()
                }
            })
        }
        gestureDetector = new GestureDetectorCompat(getContext, new MyGestureListener)
        setVariables()
        resetArcSelection()

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

    def computeSelectedArcButton(touchX: Float, touchY: Float): Button = {
        val pos = arcLayout.computeSelectedArcButton(touchX, touchY)

        for (i <- 0 until arcLayout.getChildCount) {
            val layerDrawable = arcLayout.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            if (i != pos) {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
            } else {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(255)
                currentlySelected = arcLayout.getChildAt(i).asInstanceOf[Button].getText.toString
            }
        }
        return null
    }


    override def onTouch(v: View, event: MotionEvent): Boolean = {

        if (v == plusButton) {
            if (!gestureDetector.onTouchEvent(event)) {
                val action = MotionEventCompat.getActionMasked(event)
                action match {
                    case MotionEvent.ACTION_DOWN =>
                        Log.d("TouchTest", "DOWN")
                        true
                    case MotionEvent.ACTION_UP =>
                        Log.d("TouchTest", "UP")
                        // if the long press is active we need to lock in the currently selected symbol
                        if (currentState == ArcState.LONGPRESS) {
                            if (currentlySelected != null) {
                                Controller.addVar(currentlySelected, getString(R.string.added_var))
                                val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                                symbolsListener.onVariablesChanged()
                            }
                            resetArcSelection()
                            true
                        } else {
                            false // is false right here?
                        }
                    case MotionEvent.ACTION_CANCEL =>
                        Log.d("TouchTest", "CANCEL")
                        resetArcSelection()
                        true
                    case MotionEvent.ACTION_MOVE =>
                        Log.d("TouchTest", "MOVE")
                        if (currentState == ArcState.LONGPRESS) {
                            computeSelectedArcButton(event.getRawX, event.getRawY)
                        }
                        true
                    case MotionEvent.ACTION_OUTSIDE =>
                        Log.d("TouchTest", "OUTSIDE")
                        true
                    case _ => true
                }
            } else {
                false
            }
        } else {
            true
        }
    }

    def resetArcSelection(): Unit = {
        arcLayout.setVisibility(View.INVISIBLE)
        currentState = ArcState.INITIAL
        currentlySelected = null

        for (i <- 0 until arcLayout.getChildCount) {
            arcLayout.getChildAt(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext, R.drawable.arc_button))
            val layerDrawable = arcLayout.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * If the user taps the plus button once, the arcLayout opens, at the next tap it closes
         * @param event
         * @return
         */
        override def onSingleTapUp(event: MotionEvent): Boolean = {
            if (currentState == ArcState.INITIAL) {
                arcLayout.setVisibility(View.VISIBLE)
                currentState = ArcState.ARC_OPEN
                true
            } else if (currentState == ArcState.ARC_OPEN) {
                resetArcSelection()
                true
            } else {
                false
            }
        }

        /**
         * If the user long presses the plus button the arcLayout opens and stays open as long as the user is
         * still holding his finger down.
         * @param event
         */
        override def onLongPress(event: MotionEvent): Unit = {
            if (currentState == ArcState.INITIAL) {
                plusButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                arcLayout.setVisibility(View.VISIBLE)
                currentState = ArcState.LONGPRESS
            }

        }
    }
}
