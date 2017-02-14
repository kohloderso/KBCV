package ck.kbcv.fragments

import android.content.ClipData
import android.graphics.drawable.LayerDrawable
import android.os.{Bundle, Handler}
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.{GestureDetectorCompat, MotionEventCompat}
import android.support.v7.preference.PreferenceManager
import android.view.View.{OnClickListener, OnLongClickListener, OnTouchListener}
import android.view._
import android.widget.{Button, ImageButton, TextView}
import ck.kbcv.dialogs.{ArityDialog, FunctionDialog}
import ck.kbcv.{Controller, HorizontalFlowLayout, OnSymbolsChangedListener, R}
import com.ogaclejapan.arclayout.ArcLayout


class FunctionEditor extends Fragment with OnTouchListener {
    var plusButton: Button = null
    var trashButton: ImageButton = null
    var flowLayout: HorizontalFlowLayout = null
    var arcLayoutSymbols: ArcLayout = null
    var arcLayoutArities: ArcLayout = null
    var gestureDetector: GestureDetectorCompat = null
    var currentState = ArcState.INITIAL
    var selectedPos = -1
    var currentlySelectedSymbol: String = null
    var currentlySelectedArity: Int = 0
    val handler = new Handler()
    val thisFragment = this
    val ARITY_DOTS = -5

    val runnable = new Runnable {
        override def run(): Unit = {
            if(currentState == ArcState.LONGPRESS_SYMBOLS) {
                // change state
                currentState = ArcState.LONGPRESS_ARITIES
                // select function symbol -> display it in the middle of the layout
                plusButton.setText(currentlySelectedSymbol)
                if(currentlySelectedSymbol == "\u2026") new FunctionDialog(thisFragment).show(getChildFragmentManager, "FunctionDialog")    //  Dialog sets currentlySelectedSymbol from outside
                // open arcLayout with arities
                arcLayoutSymbols.setVisibility(View.INVISIBLE)
                arcLayoutArities.setVisibility(View.VISIBLE)
            } else if(currentState == ArcState.LONGPRESS_ARITIES) {
                if(currentlySelectedArity == ARITY_DOTS) new ArityDialog(thisFragment).show(getChildFragmentManager, "ArityDialog")
                else addNewFunction(currentlySelectedSymbol, currentlySelectedArity) // add new function with selected name and arity
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
        trashButton = view.findViewById(R.id.imageButton).asInstanceOf[ImageButton]

        val SP = PreferenceManager.getDefaultSharedPreferences(getActivity.getBaseContext)
        val varString = SP.getString("function_symbols", "f, g, h, z")
        val funs = varString.split(",").map(s => s.trim)
        for(i <- funs.indices) {
            inflater.inflate(R.layout.arc_button, arcLayoutSymbols, true)
        }
        for(i <- 1 to funs.size) {
            val button = arcLayoutSymbols.getChildAt(i).asInstanceOf[Button]
            button.setText(funs(i-1))
        }
        for (i <- 0 until arcLayoutSymbols.getChildCount) {
            arcLayoutSymbols.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    // change state
                    currentState = ArcState.ARC_ARITIES_OPEN

                    val funName = v.asInstanceOf[Button].getText.toString
                    if(funName == "\u2026") new FunctionDialog(thisFragment).show(getChildFragmentManager, "FunctionDialog")    //  Dialog sets currentlySelectedSymbol from outside
                    // select function symbol -> display it in the middle of the layout
                    currentlySelectedSymbol = funName
                    plusButton.setText(currentlySelectedSymbol)
                    // open arcLayout with arities
                    arcLayoutSymbols.setVisibility(View.INVISIBLE)
                    arcLayoutArities.setVisibility(View.VISIBLE)
                }
            })
        }

        // add some buttons to arclayout for testing
        val arities = List(0, 1, 2, 3, 4)
        for(i <- 1 to arities.size) {
            inflater.inflate(R.layout.arc_button, arcLayoutArities, true)
        }
        for(i <- 1 to arities.size) {
            val button = arcLayoutArities.getChildAt(i).asInstanceOf[Button]
            button.setText(arities(i-1).toString)
        }
        for (i <- 0 until arcLayoutArities.getChildCount) {
            arcLayoutArities.getChildAt(i).setOnClickListener(new OnClickListener {
                override def onClick(v: View): Unit = {
                    if(v.asInstanceOf[Button].getText.toString == "\u2026") new ArityDialog(thisFragment).show(getChildFragmentManager, "ArityDialog")
                    // add new function with selected name and arity
                    else{addNewFunction(currentlySelectedSymbol, v.asInstanceOf[Button].getText.toString.toInt)}
                }
            })
        }

        resetArcSelection()
        gestureDetector = new GestureDetectorCompat(getContext, new MyGestureListener)
        setFunctions()

        trashButton.setOnDragListener(new View.OnDragListener() {
            override def onDrag(v: View, event: DragEvent): Boolean = {
                val action = event.getAction
                action match {
                    case DragEvent.ACTION_DRAG_STARTED =>  //  Do nothing
                    case DragEvent.ACTION_DRAG_ENTERED =>
                    case DragEvent.ACTION_DRAG_EXITED =>
                    case DragEvent.ACTION_DRAG_ENDED => plusButton.setVisibility(View.VISIBLE)
                        trashButton.setVisibility(View.INVISIBLE)
                    case DragEvent.ACTION_DROP =>
                        Controller.removeFunction(event.getClipData.getItemAt(0).getText.toString, event.getClipData.getItemAt(1).getText.toString.toInt, "deleted function symbol")
                        plusButton.setVisibility(View.VISIBLE)
                        trashButton.setVisibility(View.INVISIBLE)
                        val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
                        symbolsListener.onFunctionsChanged()
                    case _ =>
                }
                true
            }
        })
        return view
    }

    def addNewFunction(funName: String, arity: Int): Unit = {
        Controller.addFunction(funName, arity, getString(R.string.added_fun))
        val symbolsListener = getActivity.asInstanceOf[OnSymbolsChangedListener]
        symbolsListener.onFunctionsChanged()
        resetArcSelection()
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
            functionView.setOnLongClickListener(new OnLongClickListener {
                override def onLongClick(v: View): Boolean = {
                    val data = ClipData.newPlainText("deleteFun", funName)
                    data.addItem(new ClipData.Item(funArity.toString))
                    val shadow = new View.DragShadowBuilder(v)
                    v.startDrag(data, shadow, null, 0)
                    //display delete icon instead of +
                    plusButton.setVisibility(View.INVISIBLE)
                    trashButton.setVisibility(View.VISIBLE)
                    true
                }
            })
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
            handler.postDelayed(runnable, 1500)
        }

        for (i <- 0 until arcLayout.getChildCount) {
            val layerDrawable = arcLayout.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            if (i != pos) {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
            } else {
                layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(255)
                if(currentState == ArcState.LONGPRESS_SYMBOLS) currentlySelectedSymbol = arcLayout.getChildAt(i).asInstanceOf[Button].getText.toString
                else {
                    val arity = arcLayout.getChildAt(i).asInstanceOf[Button].getText.toString
                    if(arity =="\u2026") {
                        currentlySelectedArity = ARITY_DOTS
                    } else {
                        currentlySelectedArity = arity.toInt
                    }
                }
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
                                if(currentlySelectedSymbol == "\u2026") {
                                    new FunctionDialog(thisFragment).show(getChildFragmentManager, "FunctionDialog")
                                }
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
                            } else if(currentlySelectedArity == ARITY_DOTS) {
                                new ArityDialog(thisFragment).show(getChildFragmentManager, "ArityDialog")
                            }
                            resetArcSelection()
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
            arcLayoutSymbols.getChildAt(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext, R.drawable.arc_button))
            val layerDrawable = arcLayoutSymbols.getChildAt(i).getBackground.asInstanceOf[LayerDrawable]
            layerDrawable.findDrawableByLayerId(R.id.highlight_button).setAlpha(0)
        }
        for (i <- 0 until arcLayoutArities.getChildCount) {
            arcLayoutArities.getChildAt(i).setBackgroundDrawable(ContextCompat.getDrawable(getContext, R.drawable.arc_button))
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
