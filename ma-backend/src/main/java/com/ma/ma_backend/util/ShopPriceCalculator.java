package com.ma.ma_backend.util;

import com.ma.ma_backend.domain.Boss;
import com.ma.ma_backend.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopPriceCalculator {

    private final BossRepository bossRepository;

    /**
     * Calculate item price based on previous boss reward and price multiplier
     * According to spec: prices are based on "reward from boss at end of PREVIOUS level"
     *
     * @param userLevel current user level
     * @param priceMultiplier multiplier from template (e.g., 0.5 for 50%, 0.6 for 60%)
     * @return calculated price in coins
     */
    public Integer calculatePrice(Integer userLevel, Double priceMultiplier) {
        // For level 1 users, use level 1 boss reward
        Integer bossLevel = Math.max(1, userLevel - 1);

        Boss boss = bossRepository.findByLevel(bossLevel).orElse(null);

        if (boss == null) {
            // Fallback if boss not found
            return 100;
        }

        return (int) (boss.getCoinReward() * priceMultiplier);
    }
}
