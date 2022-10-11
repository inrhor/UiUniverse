package com.github.inrhor.uiUniverse.common.data

import com.github.inrhor.uiUniverse.api.frame.UiFrame

class NameSpace(val ui: MutableMap<String, UiFrame>) {

    fun uiFrame(id: String): UiFrame? = ui[id]

}