package com.github.inrhor.uiUniverse.api.frame

import org.bukkit.entity.Player

class TemplateUi(
    title: String = "", rows: Int = 6,
    button: MutableList<CustomButton> = mutableListOf(),
    template: String = "", slots: List<Int> = listOf(), icon: CustomButton = CustomButton(), data: String = ""):
    UiFrame(title, rows, button, template, slots, icon, data) {

    override fun openMenu(player: Player, vararg any: Any) {
    }
}