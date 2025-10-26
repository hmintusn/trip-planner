package com.example.trip_planner.place.mapper;

import com.example.trip_planner.place.dto.ProvinceResponse;
import com.example.trip_planner.place.model.Province;

/**
 * Mapper for Province entity and DTOs
 */
public class ProvinceMapper {

    public static ProvinceResponse toResponse(Province province) {
        if (province == null) {
            return null;
        }

        return ProvinceResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .region(province.getRegion())
                .description(province.getDescription())
                .thumbnail(province.getThumbnail())
                .build();
    }
}