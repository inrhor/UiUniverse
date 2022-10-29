package com.github.inrhor.uiUniverse.common.manager

import cn.inrhor.imipetcore.common.database.data.PetData
import com.github.inrhor.uiUniverse.common.data.DataManager.data
import com.github.inrhor.uiUniverse.common.data.InputData
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

object InputManager {

    fun Player.inputChat(select: String, lang: String, petData: PetData, uiId: String) {
        data()?.let {
            it.input = InputData(select, petData, uiId)
            sendLang(lang)
        }
    }

}