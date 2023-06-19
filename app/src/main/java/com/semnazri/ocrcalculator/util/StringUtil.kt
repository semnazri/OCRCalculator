package com.semnazri.ocrcalculator.util

fun replaceCharactertoDigit(char : String):String{

    return char
        .replace("B","8")
        .replace("S","5")
        .replace("I","1")
        .replace("I","1")
        .replace("D","0")
        .replace("O","0")
        .replace("T","+")

}