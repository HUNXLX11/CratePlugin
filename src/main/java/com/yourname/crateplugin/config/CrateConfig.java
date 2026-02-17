package com.yourname.crateplugin.config;

import com.yourname.crateplugin.CratePlugin;
import com.yourname.crateplugin.reward.Reward;
import com.yourname.crateplugin.reward.RewardPool;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CrateConfig {
    private final CratePlugin plugin;

    public CrateConfig(CratePlugin plugin) {
        this.plugin = plugin;
    }

    public RewardPool getPool(String crateName) {
        ConfigurationSection section = plugin.getConfig()
            .getConfigurationSection("crates." + crateName + ".rewards");
        if (section == null) return new RewardPool(List.of());

        List<Reward> rewards = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection r = section.getConfigurationSection(key);
            if (r == null) continue;

            Material mat = Material.matchMaterial(r.getString("material", "STONE"));
            if (mat == null) mat = Material.STONE;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String display = r.getString("display", "Reward");
            meta.displayName(
                LegacyComponentSerializer.legacySection()
                    .deserialize(display)
            );
            item.setItemMeta(meta);

            rewards.add(new Reward(
                r.getString("id", key),
                item,
                r.getInt("weight", 10),
                r.getStringList("commands")
            ));
        }
        return new RewardPool(rewards);
    }
}
