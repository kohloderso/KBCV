package ck.kbcv

import term.util._


object Controller {
    val state = new MutableState(new State(Nil, Set() , Set()))


    def addES(newES: ES): Unit = {
        state.equations = state.equations ++ newES // TODO make sure, that the same equation doesn't get added twice
        state.functions = state.functions ++ funAris(newES)
        state.variables = state.variables ++ vars(newES)
    }


    def setES(newES: ES): Unit = {
        state.equations = newES
        state.functions = funAris(newES)
        state.variables = vars(newES)
    }

}


trait OnSymbolsChangedListener {
    def onVariablesChanged()

    def onFunctionsChanged()

}

trait OnEquationsChangedListener {
    def onNewEquations(es: ES)

    def onEquationsAdded(es: ES)
}
