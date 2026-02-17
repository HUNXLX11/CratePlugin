package com.yourname.crateplugin.gui;

import com.yourname.crateplugin.CratePlugin;
import com.yourname.crateplugin.reward.RewardPool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateGUI {

    public static final int GUI_SIZE = 27;

    public static void open(Player player, RewardPool pool, CratePlugin plugin) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE,
            Component.text("✦ Mystery Crate ✦", NamedTextColor.GOLD, TextDecoration.BOLD));
        fillBorder(gui);
        player.openInventory(gui);
        new SpinAnimator(player, gui, pool, plugin).start();
    }

    private static void fillBorder(Inventory inv) {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.displayName(Component.empty());
        glass.setItemMeta(meta);
        for (int i = 0; i < GUI_SIZE; i++) {
            boolean isBorder = (i < 9) || (i >= 18) || (i % 9 == 0) || (i % 9 == 8);
            if (isBorder) inv.setItem(i, glass);
        }
    }
}
