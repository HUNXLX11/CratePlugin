package com.yourname.crateplugin.listener;

import com.yourname.crateplugin.CratePlugin;
import com.yourname.crateplugin.config.CrateConfig;
import com.yourname.crateplugin.gui.CrateGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CrateListener implements Listener, CommandExecutor {

    private final CratePlugin plugin;
    private final CrateConfig crateConfig;
    private final NamespacedKey CRATE_KEY_TAG;
    private final NamespacedKey CRATE_BLOCK_TAG;

    public CrateListener(CratePlugin plugin, CrateConfig crateConfig) {
        this.plugin = plugin;
        this.crateConfig = crateConfig;
        this.CRATE_KEY_TAG = new NamespacedKey(plugin, "crate_key");
        this.CRATE_BLOCK_TAG = new NamespacedKey(plugin, "crate_type");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;
        if (!isCrateBlock(block)) return;
        e.setCancelled(true);

        Player player = e.getPlayer();
        ItemStack hand = e.getItem();

        if (hand == null || !isCrateKey(hand)) {
            player.sendMessage(Component.text("❌ คุณต้องการ ", NamedTextColor.RED)
                .append(Component.text("Crate Key", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text(" เพื่อเปิดกล่องนี้!", NamedTextColor.RED)));
            return;
        }

        hand.setAmount(hand.getAmount() - 1);
        CrateGUI.open(player, crateConfig.getPool("default"), plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Component title = Component.text("✦ Mystery Crate ✦", NamedTextColor.GOLD, TextDecoration.BOLD);
        if (e.getView().title().equals(title)) {
            e.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return switch (cmd.getName().toLowerCase()) {
            case "cratekey" -> handleGiveKey(sender, args);
            case "setcrate" -> handleSetCrate(sender);
            default -> false;
        };
    }

    private boolean handleGiveKey(CommandSender sender, String[] args) {
        if (!sender.hasPermission("crate.admin")) {
            sender.sendMessage(Component.text("❌ ไม่มีสิทธิ์!", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /cratekey <player>", NamedTextColor.YELLOW));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("❌ ไม่พบผู้เล่น: " + args[0], NamedTextColor.RED));
            return true;
        }
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = key.getItemMeta();
        meta.displayName(Component.text("✦ Mystery Key", NamedTextColor.GOLD, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(CRATE_KEY_TAG, PersistentDataType.STRING, "default");
        key.setItemMeta(meta);
        target.getInventory().addItem(key);
        sender.sendMessage(Component.text("✅ ส่งกุญแจให้ ", NamedTextColor.GREEN)
            .append(Component.text(target.getName(), NamedTextColor.YELLOW))
            .append(Component.text(" แล้วครับ!", NamedTextColor.GREEN)));
        return true;
    }

    private boolean handleSetCrate(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("ต้องใช้ใน game เท่านั้น");
            return true;
        }
        if (!player.hasPermission("crate.admin")) {
            player.sendMessage(Component.text("❌ ไม่มีสิทธิ์!", NamedTextColor.RED));
            return true;
        }
        Block target = player.getTargetBlockExact(5);
        if (target == null || target.getType() != Material.CHEST) {
            player.sendMessage(Component.text("❌ มองไปที่ Chest ก่อนนะครับ!", NamedTextColor.RED));
            return true;
        }
        if (target.getState() instanceof TileState ts) {
            ts.getPersistentDataContainer().set(CRATE_BLOCK_TAG, PersistentDataType.STRING, "default");
            ts.update();
            player.sendMessage(Component.text("✅ ตั้งค่า Crate สำเร็จ!", NamedTextColor.GREEN));
        }
        return true;
    }

    private boolean isCrateBlock(Block block) {
        if (block.getState() instanceof TileState ts) {
            return ts.getPersistentDataContainer().has(CRATE_BLOCK_TAG);
        }
        return false;
    }

    private boolean isCrateKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(CRATE_KEY_TAG);
    }
}
