package ir.itemreforge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.List;

import static ir.itemreforge.ItemReforge.*;
import static ir.itemreforge.ReforgeItem.reforgeItem;

public class Menu implements Listener {

    public static HashMap<Player, Inventory> inv = new HashMap<>();
    public static HashMap<Player, String> menuStatus = new HashMap<>();
    public static HashMap<Player, ItemStack> clickedItem = new HashMap<>();
    private static HashMap<Player, ItemStack> reforgedItem = new HashMap<>();
    private static Boolean AnimationCancelled = false;

    public static void createPlayerInv(Player player){
        String title = getLanguage().getString("messages.menu-title");
        inv.put(player, Bukkit.createInventory(player,45,ChatColor.translateAlternateColorCodes('&', title)));
        initializeItems(player);
        player.openInventory(inv.get(player));
        menuStatus.put(player, "None");
    }

    public static void initializeItems(Player player){

        String anvil_name = getLanguage().getString("messages.anvil_item.display-name");
        List<String> anvil_lore = getLanguage().getStringList("messages.anvil_item.lore");

        String barrier_name = getLanguage().getString("messages.close_item.display-name");
        List<String> barrier_lore = getLanguage().getStringList("messages.close_item.lore");

        //BACKGROUND
        inv.get(player).setItem(0,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(9,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(18,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(27,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(36,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(8,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(17,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(26,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(35,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        inv.get(player).setItem(44,createGuiItem(Material.RED_STAINED_GLASS_PANE, "&7", null));
        for(int i = 1; i < 8; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 10; i < 13; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 14; i < 17; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 19; i < 22; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 23; i < 26; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 28; i < 35; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 37; i < 40; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        for(int i = 41; i < 44; i++){
            inv.get(player).setItem(i,createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "&7", null));
        }
        //BACKGROUND
        //ITEMS
        inv.get(player).setItem(22,createGuiItem(Material.ANVIL, anvil_name, anvil_lore));
        inv.get(player).setItem(40,createGuiItem(Material.BARRIER, barrier_name, barrier_lore));
        //ITEMS


    }

    protected static ItemStack createGuiItem(final Material material, final String name, final List<String> lore){
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if(lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i,ChatColor.translateAlternateColorCodes('&',lore.get(i)));
            }
            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        return item;
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if(e.getClickedInventory() == null){
            return;
        }
        if(e.getClick().isShiftClick() && e.getClickedInventory().equals(inv.get(player))){
            e.setCancelled(true);
            return;
        }
        if(e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getClickedInventory().equals(inv.get(player))){
            int cost = plugin.getConfig().getInt("Reforge.cost");
            if(e.getRawSlot() == 22 && menuStatus.get(player).equalsIgnoreCase("inProgress")){
                if(getEconomy().getBalance(player) >= cost){
                    ItemStack item = inv.get(player).getItem(13);
                    reforgedItem.put(player, reforgeItem(item, getItemType(item), player));
                    getEconomy().withdrawPlayer(player, cost);
                    ItemStack air = new ItemStack(Material.AIR);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, (float) 0.6);
                    BukkitScheduler scheduler = plugin.getServer().getScheduler();
                    inv.get(player).setItem(13, air);
                    menuStatus.put(player, "InProgress_toComplete");
                    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if(!AnimationCancelled) {
                                updateMenu(false, true, player);
                                inv.get(player).setItem(13, reforgedItem.get(player));
                            }
                        }
                    },34L);
                }else{
                    String message = getLanguage().getString("messages.no-money");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                e.setCancelled(true);
                return;
            }
            if(e.getRawSlot() == 40){
                player.closeInventory();
            }
        }
        if(e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getClickedInventory().equals(inv.get(player))){
            if(e.getRawSlot() == 13 && menuStatus.get(player).equalsIgnoreCase("Complete")){
                    player.closeInventory();
                    String message = getLanguage().getString("messages.reforged");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            e.setCancelled(true);
            return;
        }
        if(e.getClickedInventory().equals(e.getView().getBottomInventory()) && e.getInventory().equals(inv.get(player))) {
            if(e.getCurrentItem() == null){
                return;
            }
            if(e.getCurrentItem().getType().equals(Material.AIR)){
                return;
            }
            if(getItemType(e.getCurrentItem()).equalsIgnoreCase("no_classified")){
                e.setCancelled(true);
                return;
            }
            if(getItemType(e.getCurrentItem()).equalsIgnoreCase("too_soft")){
                String message = getLanguage().getString("messages.too-soft");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                e.setCancelled(true);
                return;
            }
            if(getItemType((e.getCurrentItem())).equalsIgnoreCase("Already_Reforged")){
                String message = getLanguage().getString("messages.already_reforged");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                e.setCancelled(true);
            }else{
                clickedItem.put(player, e.getCurrentItem());
                e.setCancelled(true);
                if(menuStatus.get(player) != null && menuStatus.get(player).equalsIgnoreCase("inProgress")){
                    inv.get(player).setItem(13, clickedItem.get(player));
                }
                setAndRemove(clickedItem.get(player), player);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory().equals(inv.get(player))) {
            e.setCancelled(true);
        }
    }

    public void setAndRemove(ItemStack clickedItem, Player player){
        clickedItem.setAmount(1);
        player.getInventory().removeItem(clickedItem);
        inv.get(player).setItem(13, clickedItem);
        updateMenu(true,false, player);
    }

    public void updateMenu(Boolean toInProgress, Boolean toDone, Player player){
        if(toInProgress){
            String anvil_name = getLanguage().getString("messages.anvil_item.display-name");
            List<String> anvil_lore = getLanguage().getStringList("messages.anvil_item.lore2");
            int cost = plugin.getConfig().getInt("Reforge.cost");
            inv.get(player).setItem(0,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(9,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(18,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(27,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(36,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(8,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(17,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(26,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(35,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(44,createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "&7", null));
            for (int i = 0; i < anvil_lore.size(); i++) {
                anvil_lore.set(i,anvil_lore.get(i).replaceAll("%cost%", String.valueOf(cost)));
            }
            inv.get(player).setItem(22,createGuiItem(Material.ANVIL, anvil_name, anvil_lore));
            menuStatus.put(player, "inProgress");
        }else if(toDone){
            inv.get(player).setItem(0,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(9,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(18,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(27,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(36,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(8,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(17,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(26,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(35,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            inv.get(player).setItem(44,createGuiItem(Material.LIME_STAINED_GLASS_PANE, "&7", null));
            menuStatus.put(player, "Complete");
        }
    }
    @EventHandler
    public void invCloseEvent(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        if(e.getInventory().equals(inv.get(player)) && menuStatus.get(player).equalsIgnoreCase("InProgress_toComplete")){
            if(player.equals(e.getPlayer())){
                player.getInventory().addItem(reforgedItem.get(player));
                player.stopSound(Sound.BLOCK_ANVIL_USE);
                AnimationCancelled = true;
            }
        }
        if(e.getInventory().equals(inv.get(player)) && menuStatus.get(player).equalsIgnoreCase("inProgress")){
            if(player.equals(e.getPlayer())) {
                player.getInventory().addItem(clickedItem.get(player));
                menuStatus.put(player,"None");
            }
        }else if(e.getInventory().equals(inv.get(player)) && menuStatus.get(player).equalsIgnoreCase("Complete")){
            if(player.equals(e.getPlayer())){
                player.getInventory().addItem(reforgedItem.get(player));
                menuStatus.put(player, "None");
            }
        }
    }
    public String getItemType(ItemStack item){
        String ItemName = item.getType().name();
        String ItemType = "no_classified";
        String reforge_lore = getLanguage().getString("messages.reforge-lore");
        if(ItemName.contains("SWORD")){
            ItemType = "sword";
        }else if(ItemName.contains("HELMET")){
            ItemType = "armor";
        }else if(ItemName.contains("CHESTPLATE")){
            ItemType = "armor";
        }else if(ItemName.contains("LEGGINGS")){
            ItemType = "armor";
        }else if(ItemName.contains("BOOTS")){
            ItemType = "armor";
        }else if(ItemName.contains("PICKAXE")){
            ItemType = "tool";
        }else if(ItemName.contains("SHOVEL")){
            ItemType = "tool";
        }else if(ItemName.contains("AXE")){
            ItemType = "tool";
        }else if(ItemName.contains("HOE")){
            ItemType = "tool";
        }

        if(ItemName.contains("WOODEN")  || ItemName.contains("LEATHER")  || ItemName.contains("STONE")){
            ItemType = "too_soft";
        }
        if(item.getItemMeta().getLore() != null && item.getItemMeta().getLore().contains(ChatColor.translateAlternateColorCodes('&', reforge_lore))){
            ItemType = "Already_Reforged";
        }
        return ItemType;
    }

}
