package com.github.inrhor.uiUniverse.server

import com.github.inrhor.uiUniverse.UiUniverse
import com.github.inrhor.uiUniverse.common.data.DataManager
import org.bukkit.Bukkit

object Loader {

    fun loadTask() {
        FileLoad.loadQuestEngine()
    }

    fun unloadTask() {
        Bukkit.getScheduler().cancelTasks(UiUniverse.plugin)
        DataManager.spaceContainer.clear()
        DataManager.templateContainer.clear()
    }

}