package ck.kbcv

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.{TextView, LinearLayout, ImageView}

/**
 * Created by Christina on 03.01.2016.
 */
class Equation (context: Context, attrs: AttributeSet) extends LinearLayout(context, attrs) {
    this.setOrientation(LinearLayout.HORIZONTAL)

    this.addView(new EquationDropzone(context, null))

    val equalsSign = new TextView(context)
    equalsSign.setText("=")
    equalsSign.setBackgroundColor(getResources.getColor(R.color.colorAccent))
    val lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
    equalsSign.setLayoutParams(lp)
    equalsSign.setGravity(Gravity.CENTER)

    this.addView(equalsSign)

    this.addView(new EquationDropzone(context, null))

}
