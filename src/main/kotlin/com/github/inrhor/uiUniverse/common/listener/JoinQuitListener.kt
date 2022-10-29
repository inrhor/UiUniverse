package com.github.inrhor.uiUniverse.common.listener

import com.github.inrhor.uiUniverse.common.data.DataManager.initData
import com.github.inrhor.uiUniverse.common.data.DataManager.removeData
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

object JoinQuitListener {

    @SubscribeEvent
    fun join(ev: PlayerJoinEvent) {
        ev.player.initData()
    }

    @SubscribeEvent
    fun quit(ev: PlayerQuitEvent) {
        ev.player.removeData()
    }

}