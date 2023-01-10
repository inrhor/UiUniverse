package com.github.inrhor.uiUniverse.common.ui.imiPetCore

import cn.inrhor.imipetcore.api.data.DataContainer.getData
import cn.inrhor.imipetcore.api.manager.PetManager.followingPetData
import cn.inrhor.imipetcore.api.manager.SkillManager.getAllSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getLoadSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getUnloadSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.getUpdateSkills
import cn.inrhor.imipetcore.api.manager.SkillManager.icon
import cn.inrhor.imipetcore.common.database.data.PetData
import cn.inrhor.imipetcore.common.database.data.SkillData
import com.github.inrhor.uiUniverse.api.frame.ButtonRun
import com.github.inrhor.uiUniverse.api.frame.CustomButton
import com.github.inrhor.uiUniverse.api.frame.ItemElement
import com.github.inrhor.uiUniverse.api.manager.UiManager.openUi
import com.github.inrhor.uiUniverse.common.kether.eval
import com.github.inrhor.uiUniverse.common.kether.evalString
import com.github.inrhor.uiUniverse.common.ui.questEngine.UiQuest
import org.bukkit.entity.Player
import taboolib.library.xseries.XMaterial
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

    fun petVar(it: ScriptContext, petData: PetData, skillData: SkillData) {
        it.rootFrame().variables()["@PetData"] = petData
        it.rootFrame().variables()["@SkillData"] = skillData
    }

    fun openPets(player: Player) {
        player.openMenu<Linked<PetData>>(player.evalString(title)) {
            linkedButton(player) {}
            elements {
                player.getData().petDataList
            }
            onGenerate { player, element, _, _ ->
                val i = element.petOption().item
                val item = ItemElement(XMaterial.valueOf(i.material), i.name, i.lore, i.modelData)
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
                val item = ItemElement(XMaterial.valueOf(i.material), i.name, i.lore, i.modelData)
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

    fun openSkills(player: Player, petData: PetData, skills: MutableList<SkillData>) {
        player.openMenu<Linked<SkillData>>(player.evalString(title)) {
            linkedButton(player) {
                skillPetVar(it, petData)
            }
            elements {
                skills
            }
            onGenerate { player, element, _, _ ->
                val i = element.icon()
                val item = ItemElement(XMaterial.valueOf(i.material), i.name, i.lore, i.modelData)
                item.itemStack(player) {
                    petVar(it, petData, element)
                }
            }
            onClick { event, element ->
                val a = if (event.clickEvent().isRightClick) icon.rightScript else icon.script
                event.clicker.eval(a) { s ->
                    val any = arrayOf(petData, element)
                    s.rootFrame().variables().set("__UiListData__", any)
                }
                event.clickEvent().isCancelled = true
            }
        }
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

    fun openPointSkill(player: Player, petData: PetData) {
        openSkills(player, petData, petData.getAllSkills())
    }

}