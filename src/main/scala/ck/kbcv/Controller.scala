package ck.kbcv

import android.util.Log
import term.Term._
import term.{reco, lpo}
import term.lpo.Precedence
import term.reco._
import term.util._

import scala.collection.immutable.{HashMap, HashSet, TreeMap}
import scala.collection.mutable


object Controller {
    val TAG = "Controller"

    val startState = new State(
        new IES,
        (new IES,new ITRS,new ITRS,new H),
        new Precedence(Nil),
        Set(),
        Set(),
        100,
        "initialize"
    )

    /** The stack of all executed commands to this point. */
    val undoStack = new mutable.Stack[State]
    undoStack.push(startState)
    /** The stack of all undone commands to this point. */
    val redoStack = new mutable.Stack[State]

    def state = undoStack.top

    def builder = new StateBuilder()

    class StateBuilder {
        val ms = new MutableState(state)
        def withE0(e0: IES) = {(ms.e0=e0); this}
        def withErch(erch:ERCH)={(ms.erc=erch);this}
        def withPrecedence(precedence:Precedence)={(ms.precedence=precedence);this}
        def withFunctions(functions: Set[(F, Int)]) = {(ms.functions=functions); this}
        def withVariables(vars: Set[V]) = {(ms.variables=vars); this}
        def withDepth(depth:Int)={(ms.depth=depth);this}
        def withIdentifier(identifier:String)={(ms.identifier=identifier);this}

        def updateState() = {
            undoStack.push(new State(ms.e0, ms.erc, ms.precedence, ms.functions, ms.variables, ms.depth, ms.identifier))
            redoStack.clear()
        }

    }

    def undo() = redoStack.push(undoStack.pop)
    def redo() = undoStack.push(redoStack.pop)
    def reset() = redoStack.clear()
    /** Indicates if i steps are undoable or not.
      * @param i number of steps to undo */
    def undoable(i: Int) = undoStack.length > i
    /** Indicates if i steps are redoable or not.
      * @param i number of steps to redo */
    def redoable(i: Int) = redoStack.length >= i

    def addES(newES: ES): Unit = {

        val nis = state.erc._1.size +1 until state.erc._1.size + newES.size+1   // indices
        val ies: IES = state.erc._1 ++ (nis zip newES) // equations together with indices

        builder.
            withE0(ies).
            withFunctions(state.functions ++ funAris(newES)).
            withVariables(state.variables ++ vars(newES)).
            withErch(new ERCH(ies, state.erc._2, state.erc._3, state.erc._4)).
            updateState()
        // TODO val h = state.erc._4 ++
    }


    def setES(newES: ES): Unit = {
        val nis = 1 until newES.size+1   // indices
        val ies: IES = new IES ++ (nis zip newES) // equations together with indices
        val h: H = initHistory(ies)

        builder.
            withE0(ies).
            withFunctions(funAris(newES)).
            withVariables(vars(newES)).
            withErch(new ERCH(ies, new ITRS, new ITRS, h)).
            withPrecedence(new Precedence(Nil)).
            updateState()
    }

    def getNextI: Int = m(state.erc._4)

    def getTMIncremental(prec: Precedence)(trs: ITRS): (Boolean, Option[Precedence]) = {
        lpo.lpoX(trs.values.toList, prec)
    }

    def ercIsComplete(): Boolean = {
        return reco.isComplete(new OLS, emptyTI) (state.erc)
    }

    val emptyI:I = new HashSet[Int]
    val emptyTI:TI = term.indexing.DT.empty
    val emptyS:reco.S = new HashMap[Int,HashSet[Int]]

}


trait OnSymbolsChangedListener {
    def onVariablesChanged()

    def onFunctionsChanged()

}

trait OnEquationsChangedListener {
    def onNewEquations()

    def onEquationsAdded()
}

trait CompletionActionListener {
    def orientRL(is: IS): Boolean

    def orientLR(is: IS): Boolean

    def simplify(is: IS)

    def delete(is: IS)

    def compose(iS: IS)

    def collapse(is: IS)

    def deduce(is: IS)
}
