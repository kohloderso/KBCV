package ck.kbcv

import android.content.ClipData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget.{EditText, LinearLayout, ImageView, Button}

/**
 * Created by Christina on 09.12.2015.
 */
class EquationsFragment extends Fragment {
    val TAG = "EquationsFragment"
    var functionSymbolContainer: LinearLayout = null
    var variableSymbolContainer: LinearLayout = null

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equations_fragment, container, false )

        functionSymbolContainer = view.findViewById(R.id.functionSymbolsContainer).asInstanceOf[LinearLayout]
        variableSymbolContainer = view.findViewById(R.id.variableSymbolContainer).asInstanceOf[LinearLayout]

        return view
    }

    def onVariablesChanged(variables: Array[String]): Unit = {
        variableSymbolContainer.removeAllViews()
        for(variable <- variables) {
            val b = new Button(getContext, null, android.R.attr.buttonStyleSmall)
            b.setText(variable)
            b.setOnTouchListener(new View.OnTouchListener() {
                override def onTouch(v: View, event: MotionEvent): Boolean = {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        val data = ClipData.newPlainText("variable", variable)
                        val shadow = new View.DragShadowBuilder(b)
                        v.startDrag(data, shadow, null, 0)
                        true
                    } else {
                        false
                    }

                }
            })
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            variableSymbolContainer.addView(b)
        }

    }

}