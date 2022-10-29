package com.github.inrhor.uiUniverse.server

import com.github.inrhor.uiUniverse.UiUniverse
import com.github.inrhor.uiUniverse.common.data.DataManager
import org.bukkit.Bukkit
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

object Loader {

    fun logo(color: String) {
        console().sendMessage(
            "§$color\n" +
                "▄• ▄▌▪  ▄• ▄▌ ▐ ▄ ▪   ▌ ▐·▄▄▄ .▄▄▄  .▄▄ · ▄▄▄ .\n" +
                "█▪██▌██ █▪██▌•█▌▐███ ▪█·█▌▀▄.▀·▀▄ █·▐█ ▀. ▀▄.▀·\n" +
                "█▌▐█▌▐█·█▌▐█▌▐█▐▐▌▐█·▐█▐█•▐▀▀▪▄▐▀▀▄ ▄▀▀▀█▄▐▀▀▪▄\n" +
                "▐█▄█▌▐█▌▐█▄█▌██▐█▌▐█▌ ███ ▐█▄▄▌▐█•█▌▐█▄▪▐█▐█▄▄▌\n" +
                " ▀▀▀ ▀▀▀ ▀▀▀ ▀▀ █▪▀▀▀. ▀   ▀▀▀ .▀  ▀ ▀▀▀▀  ▀▀▀ \n")
    }

    fun loadTask() {
        val pluginCon = UiUniverse.plugin.description
        console().sendLang("LOADER_INFO", pluginCon.name, pluginCon.version)
        FileLoad.loadTemplate()
        FileLoad.loadQuestEngine()
        FileLoad.loadImiPet()
    }

    fun unloadTask() {
        Bukkit.getScheduler().cancelTasks(UiUniverse.plugin)
        DataManager.spaceContainer.clear()
        DataManager.templateContainer.clear()
    }

}