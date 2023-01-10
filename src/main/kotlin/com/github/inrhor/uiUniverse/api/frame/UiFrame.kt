package com.github.inrhor.uiUniverse.api.frame

import com.github.inrhor.uiUniverse.common.kether.evalString
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.ScriptContext
import taboolib.platform.util.buildItem

abstract class UiFrame(
    var title: String, var rows: Int, val button: MutableList<CustomButton>,
    val template: String, var slots: List<Int>, var icon: CustomButton, val data: String) {

    @Transient val runButton = mutableListOf<CustomButton>()
    @Transient val scriptButton = mutableListOf<CustomButton>()

    abstract fun openMenu(player: Player, vararg any: Any)

    fun load() {
        button.forEach {
            if (it.run != ButtonRun.CUSTOM) {
                runButton.add(it)
            }else scriptButton.add(it)
        }
    }

}

class CustomButton(
    val slot: Int = 0, val item: ItemElement = ItemElement(),
    val script: String = "", val rightScript: String = "", val run: ButtonRun = ButtonRun.CUSTOM)

enum class ButtonRun {
    CUSTOM, NEXT, PREVIOUS
}

data class ItemElement(
    var material: XMaterial = XMaterial.APPLE,
    var name: String = "", var lore: List<String> = listOf(), var modelData: Int = 0) {

    fun itemStack(player: Player, variable: (ScriptContext) -> Unit): ItemStack = buildItem(material) {
        val a = this@ItemElement
        name = player.evalString(a.name) {
            variable(it)
        }
        a.lore.forEach {
            lore.add(player.evalString(it){ s ->
                variable(s)
            })
        }
        customModelData = modelData
    }

    fun itemStack(player: Player): ItemStack = itemStack(player) {}
}