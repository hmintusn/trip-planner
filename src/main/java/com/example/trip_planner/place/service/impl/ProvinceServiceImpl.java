package com.example.trip_planner.place.service.impl;

import com.example.trip_planner.place.dto.ProvinceResponse;
import com.example.trip_planner.place.mapper.ProvinceMapper;
import com.example.trip_planner.place.model.Province;
import com.example.trip_planner.place.repository.ProvinceRepository;
import com.example.trip_planner.place.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProvinceService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public List<ProvinceResponse> getAllProvinces() {
        log.debug("Getting all provinces");

        List<Province> provinces = provinceRepository.findAll();
        return provinces.stream()
                .map(ProvinceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProvinceResponse getProvinceById(Integer id) {
        log.debug("Getting province by ID: {}", id);

        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Province not found for ID: " + id));

        return ProvinceMapper.toResponse(province);
    }

    @Override
    public List<ProvinceResponse> getProvincesByRegion(String region) {
        log.debug("Getting provinces by region: {}", region);

        List<Province> provinces = provinceRepository.findByRegion(region);
        return provinces.stream()
                .map(ProvinceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProvinceResponse createProvince(Province province) {
        log.info("Creating province: {}", province.getName());

        // Check if province with same name already exists
        if (provinceRepository.existsByName(province.getName())) {
            throw new RuntimeException("Province with name '" + province.getName() + "' already exists");
        }

        Province saved = provinceRepository.save(province);
        return ProvinceMapper.toResponse(saved);
    }

    @Override
    public ProvinceResponse updateProvince(Province province) {
        log.info("Updating province: {}", province.getId());

        // Check if province exists
        if (!provinceRepository.existsById(province.getId())) {
            throw new RuntimeException("Province not found for ID: " + province.getId());
        }

        Province saved = provinceRepository.save(province);
        return ProvinceMapper.toResponse(saved);
    }

    @Override
    public void deleteProvince(Integer id) {
        log.info("Deleting province: {}", id);

        if (!provinceRepository.existsById(id)) {
            throw new RuntimeException("Province not found for ID: " + id);
        }

        provinceRepository.deleteById(id);
    }
}