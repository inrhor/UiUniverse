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
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

class UiQuest(
    title: String = "", rows: Int = 6,
    button: MutableList<CustomButton> = mutableListOf(),
    template: String = "", slots: List<Int> = listOf()):
    UiFrame(title, rows, button, template, slots) {

    override fun openMenu(player: Player, vararg any: Any) {
        when (any[0] as String) {
            "__QenDoing__" -> openGroupList(player, StateType.DOING)
            "__QenComplete__" -> openGroupList(player, StateType.FINISH)
            "__QenGroup__" -> openQuests(player, any[1] as String, any[2] as StateType)
            "__QenQuest__" -> openTargets(player, any[1] as QuestData)
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

    fun Linked<*>.linkedButton(player: Player) {
        runButton.forEach {
            if (it.run == ButtonRun.PREVIOUS) {
                setPreviousPage(it.slot) { _, _ ->
                    it.item.itemStack(player)
                }
            }
            if (it.run == ButtonRun.NEXT) {
                setNextPage(it.slot) { _, _ ->
                    it.item.itemStack(player)
                }
            }
        }
        scriptButton.forEach {
            set(it.slot, it.item.itemStack(player)) {
                player.eval(it.script)
            }
        }
    }
    
    fun iconButton(player: Player, data: List<String>?): ItemStack {
        val i = icon.item.copy()
        data?.forEach {
            val s = it.uppercase()
            if (s.contains("ICON:")) {
                i.material = XMaterial.valueOf(s.split(":")[1])
            }else if (s.contains("MODEL-DATA:")) {
                i.modelData = s.split(":")[1].toInt()
            }
        }
        return i.itemStack(player)
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
        player.openMenu<Linked<GroupFrame>> {
            linkedButton(player)
            elements {
                group.values.toMutableList()
            }
            onGenerate(true) { player, element, _, _ ->
                iconButton(player, element.data)
            }
            onClick { event, element ->
                event.clicker.eval(icon.script, { s ->
                    val any = arrayListOf<Any>(element.id, state)
                    s.rootFrame().variables().set("__UiListData__", any)
                }, { a ->
                    Coerce.toBoolean(a)
                }, true)
            }
        }
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
        player.openMenu<Linked<QuestData>> {
            linkedButton(player)
            elements {
                qData
            }
            onGenerate(true) { player, element, index, slot ->
                val frame = element.id.getQuestFrame()
                iconButton(player, frame?.data)
            }
            onClick { event, element ->
                event.clicker.eval(icon.script, { s ->
                    val any = arrayListOf<Any>(element)
                    s.rootFrame().variables().set("__UiListData__", any)
                }, { a ->
                    Coerce.toBoolean(a)
                }, true)
            }
        }
    }

    /**
     * 浏览目标集合
     */
    fun openTargets(player: Player, questData: QuestData) {
        player.openMenu<Linked<TargetData>> {
            linkedButton(player)
            elements {
                questData.target
            }
            onGenerate(true) { player, element, _, _ ->
                val t = element.id.getTargetFrame(element.questID)
                iconButton(player, t?.data)
            }
            onClick { event, _ ->
                event.clicker.eval(icon.script)
            }
        }
    }
}