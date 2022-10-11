package com.github.inrhor.uiUniverse.common.ui.questEngine

import cn.inrhor.questengine.common.database.data.quest.QuestData
import taboolib.module.kether.ScriptFrame

fun ScriptFrame.playerQuestData() = variables().get<QuestData>("@PetData")