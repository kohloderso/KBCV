package ck.kbcv.fragments

import android.content.ClipData
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.{GestureDetectorCompat, MotionEventCompat}
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.View.{OnClickListener, OnLongClickListener, OnTouchListener}
import android.view._
import android.widget._
import ck.kbcv.dialogs.VariableDialog
import ck.kbcv.{Controller, HorizontalFlowLayout, OnSymbolsChangedListener, R}
import com.ogaclejapan.arclayout.ArcLayout

// TODO listen to clicks on the variables which will open a dialog to edit them
class VariableEditor extends Fragment with OnTouchListener {
    val TAG = "VariableEditor"
    var flowLayout: HorizontalFlowLayout = null
    var arcLayout: ArcLayout = null
    var gestureDetector: GestureDetectorCompat = null
    var plusButton: Button = null
    var trashButton: ImageButton = null
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
        trashButton = view.findViewById(R.id.imageButton).asInstanceOf[ImageButton]

        val SP = PreferenceManager.getDefaultSharedPreferences(getActivity.getBaseContext)
        val varString = SP.getString("variable_symbols", "x, y, z, a, b")
        val vars = varString.split(",").map(s => s.trim)
        for(i <- vars.indices) {
            inflater.inflate(R.layout.arc_button, arcLayout, true)
        }
        for(i <- 1 to vars.size) {
            val button = arcLayout.getChildAt(i).asInstanceOf[Button]
            button.setText(vars(i-1))
        }

        for (i <- 0 until arcLayout.getChildCount) {
            arcLayout.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    val varName = v.asInstanceOf[Button].getText.toString
                    if(varName == "\u2026") new VariableDialog().show(getChildFragmentManager, "VariableDialog")
                    else {
                        Controller.addVar(varName, getString(R.string.added_var))
                        val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                        symbolsListener.onVariablesChanged()
                    }
                    resetArcSelection()
                }
            })
        }
        gestureDetector = new GestureDetectorCompat(getContext, new MyGestureListener)
        setVariables()
        resetArcSelection()

        trashButton.setOnDragListener(new View.OnDragListener() {
            override def onDrag(v: View, event: DragEvent): Boolean = {
                val action = event.getAction
                action match {
                    case DragEvent.ACTION_DRAG_STARTED =>  //  Do nothing
                    case DragEvent.ACTION_DRAG_ENTERED =>
                    case DragEvent.ACTION_DRAG_EXITED =>
                    case DragEvent.ACTION_DRAG_ENDED => plusButton.setVisibility(View.VISIBLE)
                        trashButton.setVisibility(View.INVISIBLE)
                    case DragEvent.ACTION_DROP => Controller.removeVar(event.getClipData.getItemAt(0).getText.toString, "deleted Variable")
                        plusButton.setVisibility(View.VISIBLE)
                        trashButton.setVisibility(View.INVISIBLE)
                        val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                        symbolsListener.onVariablesChanged()
                    case _ =>
                }
                true
            }
        })

        return view
    }

    def setVariables(): Unit = {
        val variables = Controller.state.variables

        flowLayout.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for(variable <- variables) {
            val varView = inflater.inflate(R.layout.var_view, flowLayout, false).asInstanceOf[TextView]
            varView.setText(variable)
            varView.setOnLongClickListener(new OnLongClickListener {
                override def onLongClick(v: View): Boolean = {
                    val data = ClipData.newPlainText("deleteVar", varView.getText)
                    val shadow = new View.DragShadowBuilder(v)
                    v.startDrag(data, shadow, null, 0)
                    //display delete icon instead of +
                    plusButton.setVisibility(View.INVISIBLE)
                    trashButton.setVisibility(View.VISIBLE)
                    true
                }
            })
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
                                if(currentlySelected == "\u2026") new VariableDialog().show(getChildFragmentManager, "VariableDialog")
                                else {
                                    Controller.addVar(currentlySelected, getString(R.string.added_var))
                                    val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                                    symbolsListener.onVariablesChanged()
                                }
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
