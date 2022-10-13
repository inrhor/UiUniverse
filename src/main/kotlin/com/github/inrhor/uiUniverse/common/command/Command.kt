package com.github.inrhor.uiUniverse.common.command

import com.github.inrhor.uiUniverse.common.kether.eval
import com.github.inrhor.uiUniverse.server.Loader
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand

@CommandHeader(name = "uiuniverse", aliases = ["ui"], permission = "uiuniverse.command")
object Command {

    @CommandBody(permission = "uiuniverse.admin.eval")
    val eval = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                sender.eval(argument)
            }
        }
    }

    @CommandBody(permission = "uiuniverse.admin.reload")
    val reload = subCommand {
        execute<ProxyCommandSender> { _, _, _ ->
            Loader.logo("6")
            Loader.unloadTask()
            Loader.loadTask()
        }
    }

}