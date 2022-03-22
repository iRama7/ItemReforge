package ir.itemreforge;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ItemReforge extends JavaPlugin {

    public static Plugin plugin;

    private static File languageFile;
    private static FileConfiguration language;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        sendPluginMessage("&eEnabling ItemReforge &8- By ImRama", null, true, false, false);
        plugin = this;
        this.saveDefaultConfig();
        createLanguageConfig();
        registerCommands();
        registerEvents();
        if (!setupEconomy() ) {
            sendPluginMessage("Disabling due to Vault dependency not found!", null, true, false, false);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

    }

    public static void sendPluginMessage(String message, Player player, Boolean asConsole, Boolean isError, Boolean debug){
        String prefix = ChatColor.translateAlternateColorCodes('&', "&6[&aItemReforge&6] ");
        if(isError){
            prefix = ChatColor.translateAlternateColorCodes('&', "&6[&aItemReforge&6] &c[ERROR] ");
        }
        if(debug){
            prefix = ChatColor.translateAlternateColorCodes('&', "&6[&aItemReforge&6] &6[Debug] ");
        }
        if(!asConsole){
            player.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',message));
        }else{
            Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&',message));
        }
    }

    public static FileConfiguration getLanguage(){
        return language;
    }
    public void createLanguageConfig(){
        languageFile = new File(getDataFolder(), "language.yml");
        if(!languageFile.exists()){
            languageFile.getParentFile().mkdirs();
            saveResource("language.yml", false);
        }
        language = new YamlConfiguration();
        try{
            language.load(languageFile);
        }catch(IOException| InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public static void reloadLanguage(){
        language = YamlConfiguration.loadConfiguration(languageFile);
    }

    public void registerCommands(){
        sendPluginMessage("&eRegistering commands...", null, true, false, false);
        TabExecutor tabExecutor = new Commands();
        this.getCommand("reforge").setExecutor(tabExecutor);
        this.getCommand("reforge").setTabCompleter(tabExecutor);
    }

    public void registerEvents(){
        sendPluginMessage("&eRegistering events...", null, true, false, false);
        getServer().getPluginManager().registerEvents(new Menu(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }



}
