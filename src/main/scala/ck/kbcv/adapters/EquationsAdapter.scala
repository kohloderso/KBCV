package ck.kbcv.adapters

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import ck.kbcv.adapters.EquationsAdapter.ViewHolder
import ck.kbcv.views.EquationView
import term.util.E

/**
 * Created by Christina on 20.03.2016.
 */
object EquationsAdapter {
    class ViewHolder(itemView: View) extends RecyclerView.ViewHolder(itemView) {
        val equationView = itemView.asInstanceOf[EquationView]
        // TODO ???

    }
}

class EquationsAdapter(mEquations: List[E]) extends RecyclerView.Adapter[ViewHolder] {

    override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = {
        val equationView = new EquationView(parent.getContext, null)
        return new ViewHolder(equationView)
    }

    // populate data into the item through holder
    override  def onBindViewHolder(viewHolder: ViewHolder, position: Int): Unit = {
        val equation = mEquations(position)
        viewHolder.equationView.setEquation(equation)

    }


    override def getItemCount(): Int = {
        mEquations.size
    }



}
