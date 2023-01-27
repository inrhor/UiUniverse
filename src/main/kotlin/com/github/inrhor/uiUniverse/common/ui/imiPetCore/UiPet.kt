package com.github.inrhor.uiUniverse.common.ui.imiPetCore

import cn.inrhor.imipetcore.api.data.DataContainer.getData
import cn.inrhor.imipetcore.api.manager.IconManager.iconItem
import cn.inrhor.imipetcore.api.manager.PetManager.followingPetData
import cn.inrhor.imipetcore.api.manager.SkillManager.getAllSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getLoadSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getUnloadSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getUpdateSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.icon
import cn.inrhor.imipetcore.api.manager.SkillManager.loadSkill
import cn.inrhor.imipetcore.api.manager.SkillManager.skillOption
import cn.inrhor.imipetcore.api.manager.SkillManager.treeSkillOption
import cn.inrhor.imipetcore.common.database.data.PetData
import cn.inrhor.imipetcore.common.database.data.SkillData
import cn.inrhor.imipetcore.common.option.SkillOption
import com.github.inrhor.uiUniverse.api.frame.ButtonRun
import com.github.inrhor.uiUniverse.api.frame.CustomButton
import com.github.inrhor.uiUniverse.api.frame.ItemElement
import com.github.inrhor.uiUniverse.api.manager.UiManager.openUi
import com.github.inrhor.uiUniverse.common.kether.eval
import com.github.inrhor.uiUniverse.common.kether.evalString
import com.github.inrhor.uiUniverse.common.ui.questEngine.UiQuest
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptContext
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked

class UiPet(
    title: String = "", rows: Int = 6,
    button: MutableList<CustomButton> = mutableListOf(),
    template: String = "", slots: List<Int> = listOf(), icon: CustomButton = CustomButton(), data: String = ""):
    UiQuest(title, rows, button, template, slots, icon, data) {

    override fun openMenu(player: Player, vararg any: Any) {
        when (data) {
            "pets" -> openPets(player)
            "pet-data" -> openPetData(player, any[0] as PetData)
            "pet-skill" -> openPetSelect(player)
            "pet-medical" -> openPetMedical(player, any[0] as PetData, any[1] as Double)
            "skill-manager" -> openSkillManager(player, any[0] as PetData)
            "load-skill" -> openLoadSkill(player, any[0] as PetData)
            "unload-skill" -> openUnloadSkill(player, any[0] as PetData)
            "update-skill" -> openUpdateSkill(player, any[0] as PetData)
            "point-skill" -> openPointSkill(player, any[0] as PetData)
            "load-slot" -> openLoadSlot(player, any[0] as PetData, any[1] as SkillData)
            "select-update-skill" -> openSelectUpdateSkill(player, any[0] as PetData, any[1] as SkillData)
            else -> openBasic(player)
        }
    }

    fun skillPetVar(it: ScriptContext, petData: PetData) {
        it.rootFrame().variables().set("@PetData", petData)
        val any = arrayOf<Any>(petData)
        it.rootFrame().variables().set("__UiListData__", any)
    }

    fun petVar(it: ScriptContext, petData: PetData) {
        it.rootFrame().variables()["@PetData"] = petData
    }

    private fun petVar(it: ScriptContext, petData: PetData, skillData: SkillData) {
        it.rootFrame().variables()["@IdSkill"] = skillData.id
        it.rootFrame().variables()["@PetData"] = petData
        it.rootFrame().variables()["@PetSkillData"] = skillData
    }

    fun openPets(player: Player) {
        player.openMenu<Linked<PetData>>(player.evalString(title)) {
            linkedButton(player) {}
            elements {
                player.getData().petDataList
            }
            onGenerate { player, element, _, _ ->
                val i = element.petOption().item
                val item = ItemElement(i.material, i.name, i.lore, i.modelData)
                item.itemStack(player) {
                    petVar(it, element)
                }
            }
            onClick { event, element ->
                event.clicker.eval(icon.script) { s ->
                    val any = arrayOf<Any>(element)
                    s.rootFrame().variables().set("__UiListData__", any)
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    fun openSkillManager(player: Player, petData: PetData) {
        openPetData(player, petData)
    }

    fun openPetSelect(player: Player) {
        player.openMenu<Linked<PetData>>(player.evalString(title)) {
            linkedButton(player) {}
            elements {
                player.followingPetData()
            }
            onGenerate { player, element, _, _ ->
                val i = element.petOption().item
                val item = ItemElement(i.material, i.name, i.lore, i.modelData)
                item.itemStack(player) {
                    petVar(it, element)
                }
            }
            onClick { _, element ->
                player.openUi("loadSkill", "imipet", arrayOf(element))
            }
        }
    }

    fun openPetData(player: Player, petData: PetData) {
        player.openMenu<Basic>(player.evalString(title) {
            petVar(it, petData)
        }) {
            rows(rows)
            button.forEach {
                set(it.slot, it.item.itemStack(player) { s ->
                    petVar(s, petData)
                }) {
                    player.eval(it.script) { s ->
                        petVar(s, petData)
                        val any = arrayOf(petData, 1.0)
                        s.rootFrame().variables().set("__UiListData__", any)
                    }
                }
            }
        }
    }

    fun openPetMedical(player: Player, petData: PetData, value: Double = 1.0) {
        player.openMenu<Basic>(player.evalString(title) {
            petVar(it, petData)
        }) {
            rows(rows)
            button.forEach {
                set(it.slot, it.item.itemStack(player) { s ->
                    petVar(s, petData)
                    s.rootFrame().variables()["value"] = value
                }) {
                    player.eval(it.script) { s ->
                        skillPetVar(s, petData)
                        s.rootFrame().variables()["value"] = value
                    }
                }
            }
        }
    }

    fun openLoadSkill(player: Player, petData: PetData) {
        openSkills(player, petData, petData.getLoadSkills())
    }

    fun iconItem(imIcon: cn.inrhor.imipetcore.common.option.ItemElement): ItemElement {
        // ui 的 icon
        val i = icon.item
        // imIcon -> imipet 的 icon
        // 根据ui icon匹配imipet id icon，找不到就显示imipet skill icon 否则显示 ui icon
        val item = ItemElement(i.material, i.name, i.lore, i.modelData)
        i.data.forEach {
            val ia = it.uppercase()
            if (ia.startsWith("MATERIAL:")) {
                item.material = it.getIdIcon()?.material?: imIcon.material
            }else if (ia.startsWith("NAME:")) {
                item.name = it.getIdIcon()?.name?: imIcon.name
            }else if (ia.startsWith("LORE:")) {
                item.lore = it.getIdIcon()?.lore ?: imIcon.lore
            }else if (ia.startsWith("MODEL_DATA:")) {
                item.modelData = it.getIdIcon()?.modelData?: imIcon.modelData
            }
        }
        return item
    }

    fun openLoadSlot(player: Player, petData: PetData, skillData: SkillData) {
        if (petData.getLoadSkills().size < petData.skillSystemData.number) {
            petData.loadSkill(player, skillData)
            player.openUi("unloadSkill", "imipet", arrayOf(petData))
        }else {
            slotSkill(player, petData, skillData, petData.getLoadSkills())
        }
    }

    /**
     * @param skillData 要安装的技能
     */
    fun slotSkill(player: Player, petData: PetData, skillData: SkillData, skills: MutableList<SkillData>) {
        player.openMenu<Linked<SkillData>>(player.evalString(title)) {
            linkedButton(player) {
                skillPetVar(it, petData)
            }
            elements {
                skills
            }
            onGenerate { player, element, _, _ ->
                val item = iconItem(element.icon())
                item.itemStack(player) {
                    petVar(it, petData, element)
                }
            }
            onClick { event, _ ->
                val a = if (event.clickEvent().isRightClick) icon.rightScript else icon.script
                event.clicker.eval(a) { s ->
                    val any = arrayOf(petData)
                    petVar(s, petData, skillData)
                    s.rootFrame().variables().set("__UiListData__", any)
                    s.rootFrame().variables().set("@Index", event.slot)
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    fun openSkills(player: Player, petData: PetData, skills: MutableList<SkillData>) {
        player.openMenu<Linked<SkillData>>(player.evalString(title)) {
            linkedButton(player) {
                skillPetVar(it, petData)
            }
            elements {
                skills
            }
            onGenerate { player, element, _, _ ->
                val item = iconItem(element.icon())
                item.itemStack(player) {
                    petVar(it, petData, element)
                }
            }
            onClick { event, element ->
                val a = if (event.clickEvent().isRightClick) icon.rightScript else icon.script
                event.clicker.eval(a) { s ->
                    petVar(s, petData, element)
                    val any = arrayOf(petData, element)
                    s.rootFrame().variables().set("__UiListData__", any)
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    private fun String.getIdIcon(): cn.inrhor.imipetcore.common.option.ItemElement? {
        return split(":")[1].iconItem()
    }

    override fun Linked<*>.linkedButton(player: Player, variable: (ScriptContext) -> Unit) {
        rows(rows)
        slots(this@UiPet.slots)
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
                player.eval(it.script, variable)
            }
        }
    }


    fun openUnloadSkill(player: Player, petData: PetData) {
        openSkills(player, petData, petData.getUnloadSkills())
    }

    fun openUpdateSkill(player: Player, petData: PetData) {
        openSkills(player, petData, petData.getUpdateSkills())
    }

    private fun skillOptionDataVar(it: ScriptContext, petData: PetData, skillData: SkillData, skillOption: SkillOption) {
        it.rootFrame().variables()["@IdSkill"] = skillOption.id
        it.rootFrame().variables()["@PetData"] = petData
        it.rootFrame().variables()["@PetSkillData"] = skillData
    }

    fun openSelectUpdateSkill(player: Player, petData: PetData, skillData: SkillData) {
        val tree = skillData.treeSkillOption()
        player.openMenu<Linked<SkillOption>>(player.evalString(title)) {
            linkedButton(player) {
                skillPetVar(it, petData)
            }
            elements {
                tree
            }
            onGenerate { player, element, _, _ ->
                val item = iconItem(element.icon)
                item.itemStack(player) {
                    skillOptionDataVar(it, petData, skillData, element)
                }
            }
            onClick { event, element ->
                val a = if (event.clickEvent().isRightClick) icon.rightScript else icon.script
                event.clicker.eval(a) { s ->
                    skillOptionDataVar(s, petData, skillData, element)
                    val any = arrayOf(petData, skillData, element)
                    s.rootFrame().variables().set("__UiListData__", any)
                }
                event.clickEvent().isCancelled = true
            }
        }
    }

    fun openPointSkill(player: Player, petData: PetData) {
        openSkills(player, petData, petData.getAllSkills())
    }

}