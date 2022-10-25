package ir.itemreforge;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static ir.itemreforge.ItemReforge.getLanguage;

public class AnvilMerge implements Listener {

    @EventHandler
    public void AnvilListener(InventoryClickEvent e) {
        if(e.getClickedInventory() == null){
            return;
        }
        if (e.getClickedInventory().equals(e.getView().getBottomInventory()) && e.getInventory() instanceof AnvilInventory) {
            if (e.getCurrentItem().getType() != Material.AIR) {
                ItemStack item = e.getCurrentItem();
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasLore()) {
                    List<String> lore = itemMeta.getLore();
                    if (lore.contains(ChatColor.translateAlternateColorCodes('&', getLanguage().getString("messages.reforge-lore")))) {
                        e.setCancelled(true);
                        Player player = (Player) e.getWhoClicked();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getLanguage().getString("messages.already_reforged")));
                    }
                }
            }
        }
    }
}