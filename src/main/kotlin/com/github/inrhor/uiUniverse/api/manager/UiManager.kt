package com.github.inrhor.uiUniverse.api.manager

import com.github.inrhor.uiUniverse.common.data.DataManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

object UiManager {

    fun Player.openUi(uiId: String, nameSpace: String, any: Array<Any>) {
        info("name $nameSpace  uiId $uiId  any $any")
        DataManager.spaceContainer[nameSpace]?.uiFrame(uiId)?.openMenu(this, *any)
    }

}