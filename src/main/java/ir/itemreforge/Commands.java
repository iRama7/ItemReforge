package ir.itemreforge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ir.itemreforge.ItemReforge.*;
import static ir.itemreforge.Menu.createPlayerInv;

public class Commands implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            sendPluginMessage("&eCommands:", (Player) sender, false,false,false);
            sendPluginMessage("&7/reforge reload", (Player) sender, false,false,false);
            sendPluginMessage("&7/reforge menu", (Player) sender, false,false,false);
        }else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("reforge.admin")){
            plugin.reloadConfig();
            reloadLanguage();
            reloadMenuConfig();
            sendPluginMessage("&eYou have successfully reloaded the plugin files.", (Player) sender, false,false,false);
        }else if(args[0].equalsIgnoreCase("menu") && sender.hasPermission("reforge.use")){
            createPlayerInv((Player) sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if(args.length == 1){
            if(sender.hasPermission("reforge.admin")){
                commands.add("reload");
                commands.add("menu");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }else if(sender.hasPermission("reforge.menu")){
                commands.add("menu");
            }
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}
