package com.semnazri.ocrcalculator

import com.semnazri.ocrcalculator.base.BaseViewModel
import com.semnazri.ocrcalculator.util.replaceCharactertoDigit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel<MainViewModel.Event,MainViewModel.State>() {

    sealed class Event {
        data class CalculateObject(val str : String) : Event()
    }

    sealed class State {
        data class ShowResultCalculate(val input: String,val result : String) : State()
    }

    override fun callEvent(event: Event) {
        when(event){
            is Event.CalculateObject -> doCalculate(event.str)
        }
    }

    private fun doCalculate(str: String) = launch{
        withContext(Dispatchers.Main){
            val input = replaceCharactertoDigit(str)
            val regexPattern = Regex("(\\d+)([-+*/])(\\d+)")
            val matchResult = regexPattern.find(input)

           val result =  if (matchResult != null) {
                val operand1 = matchResult.groupValues[1].toDouble()
                val operator = matchResult.groupValues[2]
                val operand2 = matchResult.groupValues[3].toDouble()

                 when (operator) {
                    "+" -> operand1 + operand2
                    "-" -> operand1 - operand2
                    "x" -> operand1 * operand2
                    ":" -> operand1 / operand2
                    else -> null
                }
            } else {
                0.0
           }

            setState(State.ShowResultCalculate(input,result.toString()))
        }
    }
}