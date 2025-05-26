package ir.itemreforge;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static ir.itemreforge.ItemReforge.*;

public class ReforgeItem {


    Random random = new Random();



    public ItemStack reforgeItem(ItemStack item, String Type, Player player) {

        List<Integer> parsed_integer_list = parseEnchants(Type, true, null, item);
        Integer amount = generateAmount(Type);
        List<Integer> ready_list = checkList(parsed_integer_list, amount, Type, item);
        ItemStack AEitem = null;

        HashMap<Player, ItemStack> reforgedItem = new HashMap<>();
        reforgedItem.put(player, item);

        for(int i : ready_list){
            AEitem = applyEnchant(reforgedItem.get(player), i, Type);
        }
        if(AEitem != null){
            reforgedItem.put(player, AEitem);
        }
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

        public List<Integer> parseEnchants(String type, Boolean isNew, List<Integer> current_list, ItemStack item){
            List<Integer> ench_int_list = new ArrayList<>();
            Boolean repeat = plugin.getConfig().getBoolean("Reforge.allow_repeat_enchantments");
            Set<String> enchant_list = plugin.getConfig().getConfigurationSection("Reforge.enchantments." +type+ ".chances").getKeys(false);
            if(isNew){
                for(String i : enchant_list){
                    String enchantment_string = plugin.getConfig().getString("Reforge.enchantments." +type+ ".chances."+i+".ench");
                    String[] split = enchantment_string.split(":");
                    String enchantment_name = split[0];
                    String path = "Reforge.enchantments." +type+ ".chances." +i;
                    Integer chance = plugin.getConfig().getInt(path + ".chance");

                    int r = random.nextInt(100) + 1;

                    if(r <= chance){

                        if(ench_int_list.contains(i) && !repeat) {
                            continue;
                        }
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantment_name));
                        if(enchantment.canEnchantItem(item)) {
                            ench_int_list.add(Integer.valueOf(i));
                        }
                    }
                }
            }else{
                enchant_list.removeAll(current_list);
                ench_int_list = current_list;
                for(String i : enchant_list){
                    String enchantment_string = plugin.getConfig().getString("Reforge.enchantments." +type+ ".chances."+i+".ench");
                    String[] split = enchantment_string.split(":");
                    String enchantment_name = split[0];
                    String path = "Reforge.enchantments." +type+ ".chances." +i;
                    Integer chance = plugin.getConfig().getInt(path + ".chance");

                    int r = random.nextInt(100) + 1;

                    if(r <= chance){

                        if(ench_int_list.contains(i) && !repeat) {
                            continue;
                        }
                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantment_name));
                        if(enchantment.canEnchantItem(item)) {
                            ench_int_list.add(Integer.valueOf(i));
                        }
                    }
                }
            }
            return ench_int_list;

        }

        public Integer generateAmount(String type){
            Integer max_int = plugin.getConfig().getInt("Reforge.enchantments." + type + ".max_new_enchantments");
            Integer min_int = plugin.getConfig().getInt("Reforge.enchantments." + type + ".min_new_enchantments");
            return random.nextInt((max_int - min_int) + 1) + min_int;
        }

        public List<Integer> checkList(List<Integer> list, Integer amount, String type, ItemStack item) {

            List<Integer> copy = list;

            do{
                copy = parseEnchants(type, false, copy, item);
            } while (copy.size() < amount);

            while (copy.size() > amount) {
                copy = reduceList(copy);
            }

            return list;

        }

        public List<Integer> reduceList(List<Integer> list){
            Integer min = 1;
            Integer max = list.size();
            Integer i = random.nextInt((max - min)) + min;
            list.remove(list.get(i));
            return list;
        }

        public ItemStack applyEnchant(ItemStack item, Integer i, String type) {
            String path = "Reforge.enchantments." + type + ".chances." + i + ".";
            String ench_string = plugin.getConfig().getString(path + "ench");
            String[] split = ench_string.split(":");
            Integer level = Integer.valueOf(split[1]);
            String enchantment_name = split[0];

            ItemStack AEItem = null;

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantment_name));
            if (enchantment == null) {
                if(AEAPI.isAnEnchantment(enchantment_name)){ //Bukkit enchantment is null, but AE enchantment found;
                    AEItem = tryAEenchant(item, level, enchantment_name);
                }
                sendPluginMessage(enchantment_name + " is null", null, true, false, true);
            } else {
                Boolean enchantmentConflicts = false;
                if (enchantment.canEnchantItem(item)) {
                    for (Enchantment ItemEnchantment : item.getEnchantments().keySet()) {
                        if (!enchantment.conflictsWith(ItemEnchantment)) {
                            if (enchantment.getName().equals(ItemEnchantment.getName())) {
                                level = level + item.getEnchantments().get(ItemEnchantment);
                            }
                        } else {
                            enchantmentConflicts = true;
                            if (enchantment.getName().equals(ItemEnchantment.getName())) {
                                level = level + item.getEnchantments().get(ItemEnchantment);
                                item.removeEnchantment(ItemEnchantment);
                                enchantmentConflicts = false;
                            }
                        }
                    }
                    if (!enchantmentConflicts) {
                        if (level > enchantment.getMaxLevel()) {
                            do {
                                level = level - 1;
                            } while (level > enchantment.getMaxLevel());
                        }
                        int previousEnchLevel = item.getEnchantmentLevel(enchantment);
                        if (!(previousEnchLevel == level)) {
                            item.addEnchantment(enchantment, level);
                        }
                    }
                }
            }
            return AEItem;
        }

        private ItemStack tryAEenchant(ItemStack item, int level, String enchantment){
            return AEAPI.applyEnchant(enchantment, level, item);
        }

}
