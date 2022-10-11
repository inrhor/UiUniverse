package com.github.inrhor.uiUniverse.common.kether

import org.bukkit.entity.Player
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script

fun ScriptFrame.player() = script().sender?.castSafely<Player>()?: error("unknown player")

fun ScriptFrame.getUiPage() = variables().get<Int?>("@UiPage")
    .orElse(null)?: 0

fun ScriptFrame.getUiVariables() = variables().get<Array<Any>>("__UiListData__").orElse(null)?: arrayOf()