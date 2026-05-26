package com.example.trip_planner.exploration.service.impl;

import com.example.trip_planner.common.util.RedisUtils;
import com.example.trip_planner.exploration.dto.ExplorationCreateRequest;
import com.example.trip_planner.exploration.dto.ExplorationDetailsResponse;
import com.example.trip_planner.exploration.dto.ExplorationFeedResponse;
import com.example.trip_planner.exploration.dto.ExplorationUpdateRequest;
import com.example.trip_planner.exploration.mapper.ExplorationMapper;
import com.example.trip_planner.exploration.model.Exploration;
import com.example.trip_planner.exploration.repository.ExplorationRepository;
import com.example.trip_planner.exploration.service.ExplorationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ExplorationService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorationServiceImpl implements ExplorationService {
    
    private final ExplorationRepository explorationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String EXPLORATIONS_CACHE_PREFIX = "explorations:list:";
    private static final long CACHE_TTL_SECONDS = 300; // 5 minutes
    
    @Override
    public Page<ExplorationFeedResponse> getExplorations(Integer provinceId, String category, int page, int size) {
        log.debug("Getting explorations - provinceId: {}, category: {}, page: {}", provinceId, category, page);
        
        // Build cache key
        String cacheKey = EXPLORATIONS_CACHE_PREFIX + 
                "province:" + (provinceId != null ? provinceId : "all") + 
                ":category:" + (category != null ? category : "all") + 
                ":page:" + page + ":size:" + size;
        
        // Try to get from cache
        try {
            @SuppressWarnings("unchecked")
            Page<ExplorationFeedResponse> cachedResult = (Page<ExplorationFeedResponse>) RedisUtils.get(redisTemplate, cacheKey);
            if (cachedResult != null) {
                log.debug("Cache hit for key: {}", cacheKey);
                return cachedResult;
            }
            log.debug("Cache miss for key: {}", cacheKey);
        } catch (Exception e) {
            log.warn("Failed to retrieve from cache, falling back to database: {}", e.getMessage());
        }
        
        // Sort by score descending, then by id for deterministic pagination (same as Places)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score").and(Sort.by("id")));
        
        Page<Exploration> explorationPage;
        
        // Apply filters dynamically
        if (provinceId != null && category != null) {
            explorationPage = explorationRepository.findByProvinceIdAndCategory(provinceId, category, pageable);
        } else if (provinceId != null) {
            explorationPage = explorationRepository.findByProvinceId(provinceId, pageable);
        } else if (category != null) {
            explorationPage = explorationRepository.findByCategory(category, pageable);
        } else {
            explorationPage = explorationRepository.findAll(pageable);
        }
        
        Page<ExplorationFeedResponse> result = explorationPage.map(ExplorationMapper::toFeedResponse);
        
        // Store in cache
        try {
            RedisUtils.setWithTTL(redisTemplate, cacheKey, result, CACHE_TTL_SECONDS);
        } catch (Exception e) {
            log.warn("Failed to cache result: {}", e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public ExplorationDetailsResponse getExplorationById(String id) {
        log.debug("Getting exploration detail for id: {}", id);
        
        Exploration exploration = explorationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exploration not found: " + id));
        
        return ExplorationMapper.toDetailsResponse(exploration);
    }
    
    @Override
    public ExplorationDetailsResponse createExploration(ExplorationCreateRequest request) {
        log.info("Creating new exploration: {}", request.getTitle());
        
        Exploration exploration = ExplorationMapper.toEntity(request);
        Exploration savedExploration = explorationRepository.save(exploration);
        
        log.info("Exploration created successfully with id: {}", savedExploration.getId());
        return ExplorationMapper.toDetailsResponse(savedExploration);
    }
    
    @Override
    public ExplorationDetailsResponse updateExploration(String id, ExplorationUpdateRequest request) {
        log.info("Updating exploration: {}", id);
        
        Exploration exploration = explorationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exploration not found: " + id));
        
        ExplorationMapper.updateEntity(exploration, request);
        Exploration updatedExploration = explorationRepository.save(exploration);
        
        log.info("Exploration updated successfully: {}", id);
        return ExplorationMapper.toDetailsResponse(updatedExploration);
    }
    
    @Override
    public void deleteExploration(String id) {
        log.info("Deleting exploration: {}", id);
        
        if (!explorationRepository.existsById(id)) {
            throw new RuntimeException("Exploration not found: " + id);
        }
        
        explorationRepository.deleteById(id);
        log.info("Exploration deleted successfully: {}", id);
    }
    
    @Override
    public Map<String, Integer> importExplorations(List<ExplorationCreateRequest> requests) {
        log.info("Importing {} explorations", requests.size());
        
        int imported = 0;
        int failed = 0;
        
        for (ExplorationCreateRequest request : requests) {
            try {
                // Always create new exploration with auto-generated UUID
                Exploration exploration = ExplorationMapper.toEntity(request);
                explorationRepository.save(exploration);
                imported++;
            } catch (Exception e) {
                log.error("Failed to import exploration: {}", request.getTitle(), e);
                failed++;
            }
        }
        
        log.info("Import completed - imported: {}, failed: {}", imported, failed);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("imported", imported);
        result.put("failed", failed);
        return result;
    }
}
