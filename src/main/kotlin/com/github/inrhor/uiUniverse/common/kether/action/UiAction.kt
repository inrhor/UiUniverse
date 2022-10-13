package com.github.inrhor.uiUniverse.common.kether.action

import com.github.inrhor.uiUniverse.api.manager.UiManager.openUi
import com.github.inrhor.uiUniverse.common.kether.backUi
import com.github.inrhor.uiUniverse.common.kether.getUiPage
import com.github.inrhor.uiUniverse.common.kether.getUiVariables
import com.github.inrhor.uiUniverse.common.kether.player
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object UiAction {

    @KetherParser(["ui"], shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("page") {
                actionNow {
                    getUiPage()
                }
            }
            case("close") {
                actionNow {
                    player().closeInventory()
                }
            }
            case("back") {
                actionNow {
                    player().openUi(backUi())
                }
            }
            case("open") {
                val ui = it.next(ArgTypes.ACTION)
                it.expect("namespace")
                val space = it.next(ArgTypes.ACTION)
                actionNow {
                    newFrame(ui).run<String>().thenApply { u ->
                        newFrame(space).run<String>().thenApply { s ->
                            player().openUi(u, s, getUiVariables())
                        }
                    }
                }
            }
        }
}

}