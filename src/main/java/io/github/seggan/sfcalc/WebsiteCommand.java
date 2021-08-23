package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.commands.AbstractCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import javax.annotation.Nonnull;

public class WebsiteCommand extends AbstractCommand {

    public WebsiteCommand() {
        super("website", "Gives the SFCalc website", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender commandSender, @Nonnull String[] strings) {
        ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sfcalc-online.pages.dev");
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            p.spigot().sendMessage(new ComponentBuilder()
                .color(ChatColor.YELLOW)
                .event(event)
                .append("Click to go to the SFCalc website")
                .create()
            );
        } else {
            commandSender.sendMessage("https://sfcalc-online.pages.dev");
        }
    }

    @Override
    public void onTab(@Nonnull CommandSender commandSender, @Nonnull String[] strings, @Nonnull List<String> list) {

    }
}
