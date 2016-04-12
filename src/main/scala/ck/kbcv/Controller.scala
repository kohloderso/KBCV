package ck.kbcv

import term.{reco, lpo}
import term.lpo.Precedence
import term.reco._
import term.util._

import scala.collection.immutable.{HashMap, HashSet, TreeMap}


object Controller {
    val state = new MutableState(new State(new ERCH(new IES, new ITRS, new ITRS, new H), new Precedence(Nil), Nil, Set() , Set(), 100))


    def addES(newES: ES): Unit = {

        state.equations = state.equations ++ newES // TODO make sure, that the same equation doesn't get added twice
        state.functions = state.functions ++ funAris(newES)
        state.variables = state.variables ++ vars(newES)
    }


    def setES(newES: ES): Unit = {
        val nis = 1 until newES.size+1   // indices
        val ies: IES = new IES ++ (nis zip newES) // equations together with indices
        val h: H = initHistory(ies)
        state.erc = new ERCH(ies, new ITRS, new ITRS, h)
        state.precedence = new Precedence(Nil)  // does this belong here?
        state.equations = newES
        state.functions = funAris(newES)
        state.variables = vars(newES)
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
    def onNewEquations(es: ES)

    def onEquationsAdded(es: ES)
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
