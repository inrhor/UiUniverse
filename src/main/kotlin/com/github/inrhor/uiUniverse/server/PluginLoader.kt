package com.github.inrhor.uiUniverse.server

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object PluginLoader {

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        Loader.logo("a")
        Loader.loadTask()
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        Loader.logo("c")
        Loader.unloadTask()
    }

}