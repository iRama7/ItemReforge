package ir.itemreforge;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static ir.itemreforge.ItemReforge.*;

public class ReforgeItem {


    public static ItemStack reforgeItem(ItemStack item, String Type, Player player) {

        int newEnchAmount = 0;
        int newEnchLimit = plugin.getConfig().getInt("Reforge.enchantments." + Type + ".max_new_enchantments");
        int minEnchLimit = plugin.getConfig().getInt("Reforge.enchantments." + Type + ".min_new_enchantments");
        HashMap<Player, ItemStack> reforgedItem = new HashMap<>();
        reforgedItem.put(player, item);
            do {
                for (String string : plugin.getConfig().getConfigurationSection("Reforge.enchantments." + Type + ".chances").getKeys(false)) {
                    if (newEnchAmount < newEnchLimit) {
                        String Ench_level = plugin.getConfig().getString("Reforge.enchantments." + Type + ".chances." + string + ".ench");
                        String[] parts = Ench_level.split(":");
                        String Ench = parts[0];
                        int level = Integer.parseInt(parts[1]);
                        int chance = plugin.getConfig().getInt("Reforge.enchantments." + Type + ".chances." + string + ".chance");

                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(Ench));
                        if (enchantment == null) {
                            sendPluginMessage(Ench + " is null", null, true, false, true);
                            continue;
                        }
                        Random random = new Random();
                        int r = random.nextInt(100) + 1;
                        if (r <= chance) {
                            Boolean enchantmentConflicts = false;
                            if (enchantment.canEnchantItem(reforgedItem.get(player))) {
                                for (Enchantment ItemEnchantment : reforgedItem.get(player).getEnchantments().keySet()) {
                                    if (!enchantment.conflictsWith(ItemEnchantment)) {
                                        if (enchantment.getName().equals(ItemEnchantment.getName())) {
                                            level = level + reforgedItem.get(player).getEnchantments().get(ItemEnchantment);
                                        }
                                    }else{
                                        enchantmentConflicts = true;
                                    }
                                }
                                if (!enchantmentConflicts) {
                                    if (level > enchantment.getMaxLevel()) {
                                        do {
                                            level = level - 1;
                                        } while (level > enchantment.getMaxLevel());
                                    }
                                    int previousEnchLevel = reforgedItem.get(player).getEnchantmentLevel(enchantment);
                                    if (!(previousEnchLevel == level)) {
                                        reforgedItem.get(player).addEnchantment(enchantment, level);
                                        newEnchAmount = newEnchAmount + 1;
                                    }
                                }
                            }
                        }
                    }
                }
            } while (newEnchAmount < minEnchLimit);
            List<String> lore = new ArrayList<>();
            if (item.getItemMeta().hasLore()) {
                List<String> itemLore = item.getItemMeta().getLore();
                lore = itemLore;
                lore.add(" ");
                lore.add(ChatColor.translateAlternateColorCodes('&', getLanguage().getString("messages.reforge-lore")));
            } else {
                lore.add(" ");
                lore.add(ChatColor.translateAlternateColorCodes('&', getLanguage().getString("messages.reforge-lore")));
            }
            ItemMeta itemMeta = reforgedItem.get(player).getItemMeta();
            itemMeta.setLore(lore);
            reforgedItem.get(player).setItemMeta(itemMeta);
            return reforgedItem.get(player);
        }
}
