package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Button
import ck.kbcv.{Controller, OnSymbolsChangedListener, OnEquationsChangedListener, R}
import ck.kbcv.views.EquationEditView
import term.util.E

class EquationEditor extends Fragment with OnClickListener {
    var equationEditView: EquationEditView = null
    var saveButton: Button = null
    var clearButton: Button = null
    var equationsListener: OnEquationsChangedListener = null

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equation_editor, container, false )
        equationEditView = view.findViewById(R.id.edit_view).asInstanceOf[EquationEditView]
        equationEditView.setEquationEditor(this)
        saveButton = view.findViewById(R.id.saveButton).asInstanceOf[Button]
        saveButton.setEnabled(false)
        saveButton.setOnClickListener(this)
        clearButton = view.findViewById(R.id.clearButton).asInstanceOf[Button]
        clearButton.setOnClickListener(this)

        // Verify that the host activity implements the callback interface
        try {
            equationsListener = getActivity.asInstanceOf[OnEquationsChangedListener]
        } catch {
            case e: ClassCastException =>
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(getActivity.toString()
                    + " must implement Listener");
        }

        return view
    }

    /**
     * check if there's still a DropZone, if not activate the 'save'-Button
     */
    def onSymbolDropped(): Unit = {
        if(equationEditView.containsDropZones()) {
            saveButton.setEnabled(false)
        } else {
            saveButton.setEnabled(true)
        }
    }

    override def onClick(v: View): Unit = {
        if(saveButton.equals(v)) {
            if(equationEditView.containsDropZones()) {} // TODO: throw error or something
            else {
                val equation = equationEditView.getEquation()
                Controller.addEquation(equation, getString(R.string.created_eq))
                equationsListener.onNewEquations()
                equationEditView.clear()
            }
        } else if(clearButton.equals(v)) {
            equationEditView.clear()
        }
    }
}
