package com.github.inrhor.uiUniverse.common.kether

import com.github.inrhor.uiUniverse.util.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.platform.compat.replacePlaceholder

object ActionSpace {
    val name = listOf("QuestEngine", "adyeshach", "")
}

fun Player.eval(script: String, variable: (ScriptContext) -> Unit, get: (Any?) -> Any, def: Any): Any {
    return KetherShell.eval(script, sender = adaptPlayer(this), namespace = ActionSpace.name) {
        variable(this)
    }.thenApply {
        get(it)
    }.getNow(def)
}

fun Player.eval(script: String, variable: (ScriptContext) -> Unit): Any {
    return KetherShell.eval(script, sender = adaptPlayer(this), namespace = ActionSpace.name) {
        variable(this)
    }.thenApply {
        Coerce.toBoolean(it)
    }.getNow(true)
}

fun Player.eval(script: String) {
    eval(script) {}
}

fun Player.evalString(script: String, variable: (ScriptContext) -> Unit): String {
    var text = script
    script.variableReader().forEach { e ->
        text = text.replace("{{$e}}", eval(e, {variable(it)}, {
            Coerce.toString(it)
        }, script).toString())
    }
    return text.replacePlaceholder(this).colored()
}

fun Player.evalString(script: String) = evalString(script) {}