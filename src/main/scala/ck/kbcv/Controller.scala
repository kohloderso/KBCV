package ck.kbcv

import term.Term._
import term.lpo.Precedence
import term.reco._
import term.util._
import term.{lpo, reco}

import scala.collection.immutable.{HashMap, HashSet}
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
        new OLS,
        "init" //TODO: how can I use string resources for this?
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
        def withOLS(ols: OLS)={(ms.ols=ols);this}
        def withMessage(identifier:String)={(ms.message=identifier);this}

        def updateState() = {
            undoStack.push(new State(ms.e0, ms.erc, ms.precedence, ms.functions, ms.variables, ms.depth, ms.ols, ms.message))
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

    def addFunction(function: F, arity: Int, message: String): Unit = {
        builder.
            withFunctions(state.functions + ((function, arity))).
            withMessage(message).
            updateState()
    }

    def addVar(variable: V, message: String): Unit = {
        builder.
            withVariables(state.variables + variable).
            withMessage(message).
            updateState()
    }

    def changePrecedence(prec: Precedence, message: String): Unit = {
        builder.
            withPrecedence(prec).
            withMessage(message).
            updateState()
    }

    def addEquation(equation: E, message: String): Unit = {
      val es = List.empty[E].::(equation)
      addES(es, message)
    }

    def addES(newES: ES, message: String): Unit = {

        val nis = getNextI until getNextI + newES.size   // indices
        val ies: IES = state.erc._1 ++ (nis zip newES) // equations together with indices

        builder.
            withE0(ies).
            withFunctions(state.functions ++ funAris(newES)).
            withVariables(state.variables ++ vars(newES)).
            withErch(new ERCH(ies, state.erc._2, state.erc._3, state.erc._4)).
            withMessage(message).
            updateState()
        // TODO val h = state.erc._4 ++
    }


    def setES(newES: ES, message: String): Unit = {
        val nis = 1 until newES.size+1   // indices
        val ies: IES = new IES ++ (nis zip newES) // equations together with indices
        val h: H = initHistory(ies)

        builder.
            withE0(ies).
            withFunctions(funAris(newES)).
            withVariables(vars(newES)).
            withErch(new ERCH(ies, new ITRS, new ITRS, h)).
            withPrecedence(new Precedence(Nil)).
            withMessage(message).
            updateState()
    }

    def updateEq(ie: IE, message: String): Unit = {
        val ies = state.erc._1 + ie
        builder.
            withE0(ies).
            withErch(new ERCH(ies, state.erc._2, state.erc._3, state.erc._4)).
            withMessage(message).
            updateState()
    }

    def removeEq(id: Int, message: String): Unit = {
        val ies = state.erc._1 - id
        builder.
            withE0(ies).
            withErch(new ERCH(ies, state.erc._2, state.erc._3, state.erc._4)).
            withMessage(message).
            updateState()
    }

    def clearAll(message: String): Unit = {
        builder.
            withE0(new IES).
            withErch((new IES,new ITRS,new ITRS,new H)).
            withPrecedence(new Precedence(Nil)).
            withVariables(Set()).
            withFunctions(Set()).
            withOLS(new OLS).
            withMessage(message).
            updateState()
    }

    def getNextI: Int = m(state.erc._4)

    def getTMIncremental(prec: Precedence)(trs: ITRS): (Boolean, Option[Precedence]) = {
        lpo.lpoX(trs.values.toList, prec)
    }

    def ercIsComplete(): Boolean = {
        return reco.isComplete(state.ols, emptyTI) (state.erc)
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

    def onEquationUpdated(iE: IE)
}

trait CompletionActionListener {
    def orientRL(is: IS): Boolean

    def orientLR(is: IS): Boolean

    def simplify(is: IS)

    def delete(is: IS)

    def compose(iS: IS)

    def collapse(is: IS)

    def deduce(indices: Iterable[Int])
}
