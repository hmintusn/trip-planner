package com.example.trip_planner.place.mapper;

import com.example.trip_planner.place.dto.PlaceDetailsResponse;
import com.example.trip_planner.place.model.PlaceDetails;

import java.util.stream.Collectors;

/**
 * Mapper for PlaceDetails entity and DTOs
 */
public class PlaceDetailsMapper {

    public static PlaceDetailsResponse toResponse(PlaceDetails placeDetails) {
        if (placeDetails == null) {
            return null;
        }

        return PlaceDetailsResponse.builder()
                .id(placeDetails.getId())
                .title(placeDetails.getTitle())
                .provinceId(placeDetails.getProvinceId())
                .sourceUrl(placeDetails.getSourceUrl())
                .contentBlocks(placeDetails.getContentBlocks() != null ?
                    placeDetails.getContentBlocks().stream()
                        .map(PlaceDetailsMapper::toContentBlockResponse)
                        .collect(Collectors.toList()) : null)
                .createdAt(placeDetails.getCreatedAt())
                .updatedAt(placeDetails.getUpdatedAt())
                .author(placeDetails.getAuthor())
                .build();
    }

    private static PlaceDetailsResponse.ContentBlock toContentBlockResponse(PlaceDetails.ContentBlock contentBlock) {
        if (contentBlock == null) {
            return null;
        }

        return PlaceDetailsResponse.ContentBlock.builder()
                .type(contentBlock.getType())
                .content(contentBlock.getContent())
                .url(contentBlock.getUrl())
                .caption(contentBlock.getCaption())
                .items(contentBlock.getItems())
                .build();
    }
}