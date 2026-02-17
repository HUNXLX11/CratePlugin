package com.yourname.crateplugin;

import com.yourname.crateplugin.config.CrateConfig;
import com.yourname.crateplugin.listener.CrateListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CratePlugin extends JavaPlugin {

    private static CratePlugin instance;
    private CrateConfig crateConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        crateConfig = new CrateConfig(this);
        CrateListener listener = new CrateListener(this, crateConfig);
        getServer().getPluginManager().registerEvents(listener, this);
        var crateKey = getCommand("cratekey");
        var setCrate = getCommand("setcrate");
        if (crateKey != null) crateKey.setExecutor(listener);
        if (setCrate != null) setCrate.setExecutor(listener);
        getLogger().info("CratePlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CratePlugin disabled!");
    }

    public static CratePlugin getInstance() { return instance; }
    public CrateConfig getCrateConfig() { return crateConfig; }
}
