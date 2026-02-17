package com.yourname.crateplugin.reward;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RewardPool {
    private final List<Reward> rewards;
    private final int totalWeight;

    public RewardPool(List<Reward> rewards) {
        this.rewards = rewards;
        this.totalWeight = rewards.stream().mapToInt(Reward::weight).sum();
    }

    public Reward pickRandom() {
        if (rewards.isEmpty()) return null;
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cursor = 0;
        for (Reward r : rewards) {
            cursor += r.weight();
            if (roll < cursor) return r;
        }
        return rewards.get(rewards.size() - 1);
    }
}
