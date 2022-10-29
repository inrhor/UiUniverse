package com.github.inrhor.uiUniverse.common.data

import com.github.inrhor.uiUniverse.api.frame.UiFrame
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DataManager {

    /**
     * 玩家数据
     */
    val playerData = ConcurrentHashMap<UUID, PlayerData>()

    fun Player.initData() {
        playerData[uniqueId] = PlayerData()
    }

    fun Player.removeData() {
        playerData.remove(uniqueId)
    }

    fun Player.data() = playerData[uniqueId]

    /**
     * 空间容器
     */
    val spaceContainer = ConcurrentHashMap<String, NameSpace>()

    /**
     * 模板容器
     */
    val templateContainer = ConcurrentHashMap<String, UiFrame>()

}