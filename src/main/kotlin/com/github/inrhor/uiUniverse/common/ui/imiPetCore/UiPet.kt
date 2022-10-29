package com.github.inrhor.uiUniverse.common.ui.imiPetCore

import cn.inrhor.imipetcore.api.data.DataContainer.getData
import cn.inrhor.imipetcore.common.database.data.PetData
import com.github.inrhor.uiUniverse.api.frame.CustomButton
import com.github.inrhor.uiUniverse.api.frame.ItemElement
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
            "pet-medical" -> openPetMedical(player, any[0] as PetData, any[1] as Double)
            else -> openBasic(player)
        }
    }

    fun petVar(it: ScriptContext, petData: PetData) {
        it.rootFrame().variables()["@PetData"] = petData
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
                        val any = arrayOf<Any>(petData)
                        s.rootFrame().variables().set("__UiListData__", any)
                        petVar(s, petData)
                        s.rootFrame().variables()["value"] = value
                    }
                }
            }
        }
    }

}