package com.github.inrhor.uiUniverse.common.ui.questEngine

import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import com.github.inrhor.uiUniverse.api.frame.ButtonRun
import com.github.inrhor.uiUniverse.api.frame.CustomButton
import com.github.inrhor.uiUniverse.api.frame.UiFrame
import com.github.inrhor.uiUniverse.common.kether.eval
import com.github.inrhor.uiUniverse.common.kether.evalString
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.common.util.setSafely
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.ScriptContext
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

class UiQuest(
    title: String = "", rows: Int = 6,
    button: MutableList<CustomButton> = mutableListOf(),
    template: String = "", slots: List<Int> = listOf(), icon: CustomButton = CustomButton(), data: String = ""):
    UiFrame(title, rows, button, template, slots, icon, data) {

    override fun openMenu(player: Player, vararg any: Any) {
        when (data) {
            "qen_group_doing" -> openGroupList(player, StateType.DOING)
            "qen_group_complete" -> openGroupList(player, StateType.FINISH)
            "qen_quest_doing" -> openQuests(player, any[0] as String, StateType.DOING)
            "qen_quest_complete" -> openQuests(player, any[0] as String, StateType.FINISH)
            "qen_target" -> openTargets(player, any[0] as QuestData)
            else -> openBasic(player)
        }
    }

    fun openBasic(player: Player) {
        player.openMenu<Basic>(player.evalString(title)) {
            rows(rows)
            button.forEach {
                set(it.slot, it.item.itemStack(player)) {
                    player.eval(it.script)
                }
            }
        }
    }

    fun Linked<*>.linkedButton(player: Player, variable: (ScriptContext) -> Unit) {
        rows(rows)
        slots(this@UiQuest.slots)
        runButton.forEach {
            if (it.run == ButtonRun.PREVIOUS) {
                setPreviousPage(it.slot) { _, _ ->
                    it.item.itemStack(player, variable)
                }
            }
            if (it.run == ButtonRun.NEXT) {
                setNextPage(it.slot) { _, _ ->
                    it.item.itemStack(player, variable)
                }
            }
        }
        scriptButton.forEach {
            set(it.slot, it.item.itemStack(player, variable)) {
                player.eval(it.script) { s ->
                    s.rootFrame().variables()["@UiPage"] = page
                }
            }
        }
    }
    
    fun iconButton(
        player: Player,
        data: List<String>?,
        note: List<String> = listOf(),
        key: String = "",
        variable: (ScriptContext) -> Unit): ItemStack {
        val i = icon.item.copy()
        data?.forEach {
            val s = it.uppercase()
            if (s.contains("ICON:")) {
                i.material = XMaterial.valueOf(s.split(":")[1])
            }else if (s.contains("MODEL-DATA:")) {
                i.modelData = s.split(":")[1].toInt()
            }
        }
        if (note.isNotEmpty() && key.isNotEmpty()) {
            val lore = i.lore.toMutableList()
            if (lore.isNotEmpty()) {
                for (index in 0 until lore.size) {
                    val s = lore[index]
                    if (s == key) {
                        for (ni in note.indices) {
                            lore.setSafely(index+ni, note[ni], "")
                        }
                        break
                    }
                }
                i.lore = lore
            }
        }
        return i.itemStack(player) {variable(it)}
    }

    fun groupListVar(it: ScriptContext, groupFrame: GroupFrame) {
        it.rootFrame().variables()["@QenGroupID"] = groupFrame.id
    }

    /**
     * 浏览任务组集合
     */
    fun openGroupList(player: Player, state: StateType) {
        val group = mutableMapOf<String, GroupFrame>()
        QuestManager.groupMap.forEach { (t, u) ->
            if (group.containsKey(t)) return@forEach
            u.quests.forEach { i ->
                val q = player.questData(i.id)
                if (q?.state == state) group[t] = u
            }
        }
        player.openMenu<Linked<GroupFrame>>(player.evalString(title)) {
            linkedButton(player) {}
            group.values.forEach {
                info(it.name)
            }
            elements {
                group.values.toList()
            }
            onGenerate(true) { player, element, _, _ ->
                iconButton(player, element.data, element.note, "__QenGroupNote__") {
                    groupListVar(it, element)
                }
            }
            onClick { event, element ->
                event.clicker.eval(icon.script) { s ->
                    groupListVar(s, element)
                    val any = arrayOf<Any>(element.id)
                    s.rootFrame().variables().set("__UiListData__", any)
                    s.rootFrame().variables()["@UiPage"] = page
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    fun questsVar(it: ScriptContext, group: String) {
        it.rootFrame().variables()["@QenGroupID"] = group
    }

    fun questVar(it: ScriptContext, quest: String) {
        it.rootFrame().variables()["@QenQuestID"] = quest
    }

    /**
     * 浏览任务集合
     */
    fun openQuests(player: Player, group: String, state: StateType) {
        val qData = mutableListOf<QuestData>()
        QuestManager.groupMap[group]?.quest?.forEach { 
            val q = player.questData(it)?: return@forEach
            if (q.state == state) qData.add(q)
        }
        player.openMenu<Linked<QuestData>>(player.evalString(title) {
            questsVar(it, group)
        }) {
            linkedButton(player) {
                questsVar(it, group)
            }
            elements {
                qData
            }
            onGenerate(true) { player, element, _, _ ->
                val frame = element.id.getQuestFrame()
                iconButton(player, frame?.data, frame?.note?: listOf(),
                    "__QenQuestNote__") {
                    questVar(it, element.id)
                }
            }
            onClick { event, element ->
                event.clicker.eval(icon.script) { s ->
                    questVar(s, element.id)
                    val any = arrayOf<Any>(element)
                    s.rootFrame().variables().set("__UiListData__", any)
                    s.rootFrame().variables()["@UiPage"] = page
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    fun targetVar(it: ScriptContext, target: String, quest: String) {
        it.rootFrame().variables()["@QenQuestID"] = quest
        it.rootFrame().variables()["@QenTargetID"] = target
    }

    /**
     * 浏览目标集合
     */
    fun openTargets(player: Player, questData: QuestData) {
        val quest = questData.id
        player.openMenu<Linked<TargetData>>(player.evalString(title) {
            questVar(it, quest)
        }) {
            linkedButton(player) {
                questVar(it, quest)
            }
            elements {
                questData.target
            }
            onGenerate(true) { player, element, _, _ ->
                val t = element.id.getTargetFrame(element.questID)
                iconButton(player, t?.data, t?.description?: listOf(),
                    "__QenTargetNote__") {
                    targetVar(it, element.id, quest)
                }
            }
            onClick { event, element ->
                event.clicker.eval(icon.script) { s ->
                    targetVar(s, element.id, quest)
                    s.rootFrame().variables()["@UiPage"] = page
                }
                event.clickEvent().isCancelled = true
            }
        }
    }
}