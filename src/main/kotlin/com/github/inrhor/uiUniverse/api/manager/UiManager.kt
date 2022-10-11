package com.github.inrhor.uiUniverse.api.manager

import com.github.inrhor.uiUniverse.common.data.DataManager
import org.bukkit.entity.Player

object UiManager {

    fun Player.openUi(nameSpace: String, uiId: String, any: Array<Any>) {
        DataManager.spaceContainer[nameSpace]?.uiFrame(uiId)?.openMenu(this, *any)
    }

}