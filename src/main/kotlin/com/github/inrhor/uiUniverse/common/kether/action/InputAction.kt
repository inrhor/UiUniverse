package com.github.inrhor.uiUniverse.common.kether.action

import com.github.inrhor.uiUniverse.common.kether.player
import com.github.inrhor.uiUniverse.common.kether.selectPetData
import com.github.inrhor.uiUniverse.common.manager.InputManager.inputChat
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object InputAction {

    @KetherParser(["input"])
    fun parser() = scriptParser {
        it.switch {
            case("select") {
                val select = it.nextToken()
                it.expect("lang")
                val lang = it.nextToken()
                it.expect("ui")
                val ui = it.nextToken()
                actionNow {
                    player().inputChat(select, lang, selectPetData(), ui)
                }
            }
        }
    }

}