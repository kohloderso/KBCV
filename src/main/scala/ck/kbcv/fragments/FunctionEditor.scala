package ck.kbcv.fragments

import android.graphics.drawable.LayerDrawable
import android.os.{Bundle, Handler}
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.{GestureDetectorCompat, MotionEventCompat}
import android.view.View.{OnClickListener, OnTouchListener}
import android.view._
import android.widget.{Button, TextView}
import ck.kbcv.{Controller, HorizontalFlowLayout, OnSymbolsChangedListener, R}
import com.ogaclejapan.arclayout.ArcLayout


class FunctionEditor extends Fragment with OnTouchListener {
    var plusButton: Button = null
    var flowLayout: HorizontalFlowLayout = null
    var arcLayoutSymbols: ArcLayout = null
    var arcLayoutArities: ArcLayout = null
    var gestureDetector: GestureDetectorCompat = null
    var currentState = ArcState.INITIAL
    var selectedPos = -1
    var currentlySelectedSymbol: String = null
    var currentlySelectedArity: Int = 0
    val handler = new Handler()

    val runnable = new Runnable {
        override def run(): Unit = {
            if(currentState == ArcState.LONGPRESS_SYMBOLS) {
                // change state
                currentState = ArcState.LONGPRESS_ARITIES
                // select function symbol -> display it in the middle of the layout
                plusButton.setText(currentlySelectedSymbol)
                // open arcLayout with arities
                arcLayoutSymbols.setVisibility(View.INVISIBLE)
                arcLayoutArities.setVisibility(View.VISIBLE)
            } else if(currentState == ArcState.LONGPRESS_ARITIES) {
                // add new function with selected name and arity
                Controller.addFunction(currentlySelectedSymbol, currentlySelectedArity, getString(R.string.added_fun))
                val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                symbolsListener.onFunctionsChanged()
                resetArcSelection()
            }

        }
    }


    object ArcState extends Enumeration {
        val INITIAL, ARC_SYMBOLS_OPEN, LONGPRESS_SYMBOLS, ARC_ARITIES_OPEN, LONGPRESS_ARITIES = Value
    }


    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.function_editor, container, false )
        flowLayout = view.findViewById(R.id.functionFlowLayout).asInstanceOf[HorizontalFlowLayout]
        arcLayoutSymbols = view.findViewById(R.id.arcLayoutSymbols).asInstanceOf[ArcLayout]
        arcLayoutArities = view.findViewById(R.id.arcLayoutArities).asInstanceOf[ArcLayout]
        plusButton = view.findViewById(R.id.plusButton).asInstanceOf[Button]
        plusButton.setOnTouchListener(this)

        // add some buttons to arclayout for testing
        val symbols = List("f", "g", "h", "c")
        for(i <- symbols.indices) {
            inflater.inflate(R.layout.arc_button, arcLayoutSymbols, true)
        }
        for(i <- symbols.indices) {
            val button = arcLayoutSymbols.getChildAt(i).asInstanceOf[Button]
            button.setText(symbols(i))
        }
        for (i <- 0 until arcLayoutSymbols.getChildCount) {
            arcLayoutSymbols.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    // change state
                    currentState = ArcState.ARC_ARITIES_OPEN
                    // select function symbol -> display it in the middle of the layout
                    currentlySelectedSymbol = v.asInstanceOf[Button].getText.toString
                    plusButton.setText(currentlySelectedSymbol)
                    // open arcLayout with arities
                    arcLayoutSymbols.setVisibility(View.INVISIBLE)
                    arcLayoutArities.setVisibility(View.VISIBLE)
                }
            })
        }

        // add some buttons to arclayout for testing
        val arities = List(0, 1, 2, 3, 4)
        for(i <- arities.indices) {
            inflater.inflate(R.layout.arc_button, arcLayoutArities, true)
        }
        for(i <- arities.indices) {
            val button = arcLayoutArities.getChildAt(i).asInstanceOf[Button]
            button.setText(arities(i).toString)
        }
        for (i <- 0 until arcLayoutArities.getChildCount) {
            arcLayoutArities.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    // add new function with selected name and arity
                    Controller.addFunction(currentlySelectedSymbol, v.asInstanceOf[Button].getText.toString.toInt, getString(R.string.added_fun))
                    val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                    symbolsListener.onFunctionsChanged()
                    resetArcSelection()
                }
            })
        }

        resetArcSelection()

        gestureDetector = new GestureDetectorCompat(getContext, new MyGestureListener)

        setFunctions()
        return view
    }

    def setFunctions(): Unit = {
        val functions = Controller.state.functions
        flowLayout.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for((funName, funArity) <- functions) {
            val functionView = inflater.inflate(R.layout.fun_arity_view, flowLayout, false)
            val funNameView = functionView.findViewById(R.id.funName).asInstanceOf[TextView]
            funNameView.setText(funName)
            val funArityView = functionView.findViewById(R.id.funArity).asInstanceOf[TextView]
            funArityView.setText(funArity.toString)
            flowLayout.addView(functionView)
        }
    }


    def selectArcbutton(touchX: Float, touchY: Float): Unit = {
        val arcLayout = currentState match {
            case ArcState.LONGPRESS_SYMBOLS => arcLayoutSymbols
            case ArcState.LONGPRESS_ARITIES => arcLayoutArities
        }

        val pos = arcLayout.computeSelectedArcButton(touchX, touchY)

        if(pos == selectedPos) return
        else {
            selectedPos = pos
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 1000)
        }

        for (i <- 0 until arcLayout.getChildCount) {
            val layerDrawable = arcLayout.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            if (i != pos) {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
            } else {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(255)
                if(currentState == ArcState.LONGPRESS_SYMBOLS) currentlySelectedSymbol = arcLayout.getChildAt(i).asInstanceOf[Button].getText.toString
                else currentlySelectedArity = arcLayout.getChildAt(i).asInstanceOf[Button].getText.toString.toInt
            }
        }
    }

    override def onTouch(v: View, event: MotionEvent): Boolean = {

        if (v == plusButton) {
            if (!gestureDetector.onTouchEvent(event)) {
                val action = MotionEventCompat.getActionMasked(event)
                action match {
                    case MotionEvent.ACTION_UP =>
                        // if the long press is active we need to lock in the currently selected symbol
                        if (currentState == ArcState.LONGPRESS_SYMBOLS) {
                            if (currentlySelectedSymbol != null) {
                                handler.removeCallbacks(runnable)
                                plusButton.setText(currentlySelectedSymbol)
                                arcLayoutSymbols.setVisibility(View.INVISIBLE)
                                arcLayoutArities.setVisibility(View.VISIBLE)
                                currentState = ArcState.ARC_ARITIES_OPEN
                            }
                        } else if (currentState == ArcState.LONGPRESS_ARITIES) {
                            if (currentlySelectedArity >= 0) {
                                // add new function with selected name and arity
                                Controller.addFunction(currentlySelectedSymbol, currentlySelectedArity, getString(R.string.added_fun))
                                val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                                symbolsListener.onFunctionsChanged()
                                resetArcSelection()
                            }
                        }
                    case MotionEvent.ACTION_CANCEL =>
                        resetArcSelection()
                    case MotionEvent.ACTION_MOVE =>
                        if (currentState == ArcState.LONGPRESS_SYMBOLS || currentState == ArcState.LONGPRESS_ARITIES) {
                            selectArcbutton(event.getRawX, event.getRawY)
                        }
                    case _ =>
                }
            }
            true
        } else {
            false
        }
    }

    def resetArcSelection(): Unit = {
        plusButton.setText("+")
        arcLayoutSymbols.setVisibility(View.INVISIBLE)
        arcLayoutArities.setVisibility(View.INVISIBLE)
        currentState = ArcState.INITIAL
        currentlySelectedSymbol = null
        currentlySelectedArity = -1

        for (i <- 0 until arcLayoutSymbols.getChildCount) {
            arcLayoutSymbols.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext, R.drawable.arc_button))
            val layerDrawable = arcLayoutSymbols.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
        }
        for (i <- 0 until arcLayoutArities.getChildCount) {
            arcLayoutArities.getChildAt(i).setBackground(ContextCompat.getDrawable(getContext, R.drawable.arc_button))
            val layerDrawable = arcLayoutArities.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
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
                arcLayoutSymbols.setVisibility(View.VISIBLE)
                currentState = ArcState.ARC_SYMBOLS_OPEN
                true
            } else if (currentState == ArcState.ARC_ARITIES_OPEN || currentState == ArcState.ARC_SYMBOLS_OPEN) {
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
                arcLayoutSymbols.setVisibility(View.VISIBLE)
                currentState = ArcState.LONGPRESS_SYMBOLS
            }

        }
    }

}
