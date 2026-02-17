package com.yourname.crateplugin.reward;

import org.bukkit.inventory.ItemStack;
import java.util.List;

public record Reward(
    String id,
    ItemStack displayItem,
    int weight,
    List<String> commands
) {}
