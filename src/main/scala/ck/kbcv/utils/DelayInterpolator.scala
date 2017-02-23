package ck.kbcv.utils

import android.view.animation.Interpolator

/**
  * Linear interpolation with a pause at the beginning and end
  * @param startOffset in percent
  * @param endOffset in percent
  */
class DelayInterpolator(startOffset: Float, endOffset: Float) extends Interpolator {
    val endPoint = 1 -endOffset
    val h = 1/(endPoint - startOffset) // slope of linear interpolation condensed into shorter interval
    override def getInterpolation(input: Float): Float = {
        if(input < startOffset) 0
        else if(input > endPoint) 1
        else
            (input - startOffset)*h

    }
}
