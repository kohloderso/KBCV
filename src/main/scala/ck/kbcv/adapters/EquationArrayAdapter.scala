package ck.kbcv.adapters

import android.content.Context
import android.view.{View, ViewGroup}
import android.widget.ArrayAdapter
import ck.kbcv.views.EquationView
import term.util.E

import scala.collection.JavaConverters._

class EquationArrayAdapter(context: Context, equations: List[E]) extends ArrayAdapter[E](context, -1, equations.toArray) {
    val equationsArray = equations.toArray

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
        return new EquationView(context, null, equationsArray(position))
    }



}
