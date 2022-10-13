package com.github.inrhor.uiUniverse.api.manager

import com.github.inrhor.uiUniverse.common.data.DataManager
import org.bukkit.entity.Player
import taboolib.module.ui.type.Basic

object UiManager {

    fun Player.openUi(uiId: String, nameSpace: String, any: Array<Any>) {
        DataManager.spaceContainer[nameSpace]?.uiFrame(uiId)?.openMenu(this, *any)
    }

    fun Player.openUi(basic: Basic?) {
        if (basic == null) return
        openInventory(basic.build())
    }

}