package com.example.service;

import com.example.dto.StatsDTO;
import com.example.entity.ProfileEntity;
import com.example.repository.ExpenseRepository;
import com.example.repository.ProfileRepository;
import com.example.util.SpringSecurityUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StatsService {

    private final ProfileRepository profileRepository;
    private final ExpenseRepository expenseRepository;

    public StatsService(ProfileRepository profileRepository,
                        ExpenseRepository expenseRepository) {
        this.profileRepository = profileRepository;
        this.expenseRepository = expenseRepository;
    }


    public StatsDTO getStats() {
        long users = profileRepository.countByEnabledTrue();

        long categories = 0;
        BigDecimal total = BigDecimal.ZERO;

        Integer profileId = profileRepository.findProfileIdByEmail(SpringSecurityUtil.currentUsername());

        if (profileId != null) {
            categories = expenseRepository.countDistinctCategoryByProfileId(profileId);
            total = expenseRepository.sumAmountByProfileId(profileId);
            if (total == null) total = BigDecimal.ZERO;
        }

        return new StatsDTO(users, categories, total);
    }

    /**
     * Global stats for the homepage — no login required.
     * users       = total registered profiles
     * services    = total distinct expense categories across ALL users
     * totalAmount = sum of ALL expenses across ALL users
     */
    @Cacheable(value = "stats", key = "'global'")
    public StatsDTO getGlobalStats() {
        long users      = profileRepository.count();

        long categories = expenseRepository.countDistinctCategoryGlobal();
        BigDecimal total = expenseRepository.sumAllAmounts();
        if (total == null) total = BigDecimal.ZERO;

        return new StatsDTO(users, categories, total);
    }

    public long getUserCount(){
        return profileRepository.countByEnabledTrue();
    }

    @CacheEvict(value = "stats", allEntries = true)
    public void clearStatsCache() {
        // cache tozalanadi
    }
}