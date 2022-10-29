package com.github.inrhor.uiUniverse.server

import com.github.inrhor.uiUniverse.api.frame.TemplateUi
import com.github.inrhor.uiUniverse.common.data.DataManager
import com.github.inrhor.uiUniverse.common.data.NameSpace
import com.github.inrhor.uiUniverse.common.ui.imiPetCore.UiPet
import com.github.inrhor.uiUniverse.common.ui.questEngine.UiQuest
import com.github.inrhor.uiUniverse.util.getFile
import com.github.inrhor.uiUniverse.util.getFileList
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject

object FileLoad {

    fun loadTemplate() {
        val folder = getFile("template", "TEMPLATE_EMPTY_FILE", true,
            "list", "home")
        getFileList(folder).forEach {
            val yaml = Configuration.loadFromFile(it)
            yaml.getConfigurationSection("")?.getKeys(false)?.forEach { e ->
                val template = yaml.getObject<TemplateUi>(e, false)
                DataManager.templateContainer[e] = template
            }
        }
    }

    fun loadQuestEngine() {
        val folder = getFile("ui/QuestEngine", "QEN_EMPTY_FILE", true,
            "home", "group", "quest", "target")
        getFileList(folder).forEach {
            val yaml = Configuration.loadFromFile(it)
            yaml.getConfigurationSection("")?.getKeys(false)?.forEach { e ->
                val ui = yaml.getObject<UiQuest>(e, false)
                val t = ui.template
                val tc = DataManager.templateContainer
                if (t.isNotEmpty() && tc.containsKey(t)) {
                    val temple = DataManager.templateContainer[ui.template]!!
                    ui.slots = temple.slots
                    ui.rows = temple.rows
                    temple.button.forEach { b ->
                        ui.button.add(b)
                    }
                }
                ui.load()
                val sp = DataManager.spaceContainer
                if (sp.containsKey("qen")) {
                    sp["qen"]!!.ui[e] = ui
                }else sp["qen"] = NameSpace(mutableMapOf(e to ui))
            }
        }
    }

    fun loadImiPet() {
        val folder = getFile("ui/imiPetCore", "PET_EMPTY_FILE", true,
            "home", "manager", "medical")
        getFileList(folder).forEach {
            val yaml = Configuration.loadFromFile(it)
            yaml.getConfigurationSection("")?.getKeys(false)?.forEach { e ->
                val ui = yaml.getObject<UiPet>(e, false)
                val t = ui.template
                val tc = DataManager.templateContainer
                if (t.isNotEmpty() && tc.containsKey(t)) {
                    val temple = DataManager.templateContainer[ui.template]!!
                    ui.slots = temple.slots
                    ui.rows = temple.rows
                    temple.button.forEach { b ->
                        ui.button.add(b)
                    }
                }
                ui.load()
                val sp = DataManager.spaceContainer
                if (sp.containsKey("imipet")) {
                    sp["imipet"]!!.ui[e] = ui
                }else sp["imipet"] = NameSpace(mutableMapOf(e to ui))
            }
        }
    }
}