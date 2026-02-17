package com.yourname.crateplugin.gui;

import com.yourname.crateplugin.CratePlugin;
import com.yourname.crateplugin.reward.Reward;
import com.yourname.crateplugin.reward.RewardPool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class SpinAnimator extends BukkitRunnable {

    private static final int[] SPIN_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    private static final int CENTER_SLOT = 13;
    private static final int TOTAL_FRAMES = 35;

    private final Player player;
    private final Inventory gui;
    private final RewardPool pool;
    private final CratePlugin plugin;
    private final Reward finalReward;
    private int frameCount = 0;
    private long currentDelay = 2L;
    private boolean rescheduled = false;

    public SpinAnimator(Player player, Inventory gui, RewardPool pool, CratePlugin plugin) {
        this.player = player;
        this.gui = gui;
        this.pool = pool;
        this.plugin = plugin;
        this.finalReward = pool.pickRandom();
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, currentDelay);
    }

    @Override
    public void run() {
        if (!player.isOnline() || player.getOpenInventory().getTopInventory() != gui) {
            this.cancel();
            return;
        }

        frameCount++;

        // Scroll items left
        for (int i = 0; i < SPIN_SLOTS.length - 1; i++) {
            gui.setItem(SPIN_SLOTS[i], gui.getItem(SPIN_SLOTS[i + 1]));
        }
        Reward rand = pool.pickRandom();
        if (rand != null) {
            gui.setItem(SPIN_SLOTS[SPIN_SLOTS.length - 1], rand.displayItem());
        }

        if (frameCount >= TOTAL_FRAMES) {
            if (finalReward != null) {
                gui.setItem(CENTER_SLOT, finalReward.displayItem());
            }
            this.cancel();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                giveReward();
                player.closeInventory();
            }, 40L);
        }
    }

    private void giveReward() {
        if (finalReward == null) return;
        finalReward.commands().forEach(cmd -> {
            String parsed = cmd.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        });
        player.sendMessage(Component.text()
            .append(Component.text("✦ ยินดีด้วย! คุณได้รับ ", NamedTextColor.GOLD))
            .append(finalReward.displayItem().displayName()
                .decoration(TextDecoration.ITALIC, false))
            .append(Component.text(" จาก Mystery Crate!", NamedTextColor.GOLD))
            .build());
    }
}
