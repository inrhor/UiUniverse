package com.github.inrhor.uiUniverse

import taboolib.common.platform.Plugin
import taboolib.platform.BukkitIO
import taboolib.platform.BukkitPlugin

object UiUniverse : Plugin() {

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    val resource by lazy {
        BukkitIO()
    }

}