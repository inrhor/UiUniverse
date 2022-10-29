package com.github.inrhor.uiUniverse.common.kether

import cn.inrhor.imipetcore.common.database.data.PetData
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.script
import taboolib.module.ui.type.Basic

fun ScriptFrame.player() = script().sender?.castSafely<Player>()?: error("unknown player")

fun ScriptFrame.getUiPage() = variables().get<Int?>("@UiPage")
    .orElse(null)?: 0

fun ScriptFrame.getUiVariables() = variables().get<Array<Any>>("__UiListData__").orElse(null)?: arrayOf()

fun ScriptFrame.backUi() = variables().get<Basic>("__UiBack__").orElse(null)?: null

fun ScriptFrame.selectPetData() = variables().get<PetData>("@PetData")
    .orElse(null)?: error("unknown @PetData")