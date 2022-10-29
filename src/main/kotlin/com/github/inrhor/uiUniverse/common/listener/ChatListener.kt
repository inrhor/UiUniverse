package com.github.inrhor.uiUniverse.common.listener

import cn.inrhor.imipetcore.api.manager.PetManager.renamePet
import cn.inrhor.imipetcore.common.database.data.PetData
import com.github.inrhor.uiUniverse.api.manager.UiManager.openUi
import com.github.inrhor.uiUniverse.common.data.DataManager.data
import com.github.inrhor.uiUniverse.util.isDouble
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object ChatListener {

    @SubscribeEvent
    fun chat(ev: AsyncPlayerChatEvent) {
        val p = ev.player
        val data = p.data()?: return
        data.input?.let {
            val m = ev.message
            submit {
                when (it.select) {
                    "pet-medical" -> {
                        val value = if (m.isDouble()) m.toDouble() else 1.0
                        p.openUi(it.uiId, "imipet", arrayOf(it.any as PetData, value))
                    }
                    "pet-rename" -> {
                        val pet = (it.any as PetData)
                        p.renamePet(pet, m)
                        pet.name = m
                        p.openUi(it.uiId, "imipet", arrayOf(it.any))
                    }
                }
            }
            ev.isCancelled = true
        }
        data.input = null
    }

}