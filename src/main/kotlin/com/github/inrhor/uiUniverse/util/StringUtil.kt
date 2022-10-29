package com.github.inrhor.uiUniverse.util

import taboolib.common.util.VariableReader
import java.lang.Double.parseDouble

fun String.variableReader(): MutableList<String> {
    val list = mutableListOf<String>()
    VariableReader().readToFlatten(this).forEach {
        if (it.isVariable) list.add(it.text)
    }
    return list
}

fun String.isDouble(): Boolean {
    var i = true
    try {
        parseDouble(this)
    }catch (ex: Exception) {
        i = false
    }
    return i
}