package com.example.trip_planner.clustering;

import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.repository.PlaceRepository;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ClusteringController {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private PlaceRepository placeRepository;

    @PostMapping("/cluster")
    public List<ClusterDTO> clusterPlaces(@RequestBody ClusterRequest request) {
        List<Place> places = placeRepository.findAllById(request.getPlaceIds());

        List<PlacePoint> placePoints = places.stream()
                .map(p -> new PlacePoint(p.getId(), p.getLocation().getY(), p.getLocation().getX()))
                .collect(Collectors.toList());

        List<CentroidCluster<PlacePoint>> clusters = clusterService.cluster(
                placePoints, request.getDays(), 100, 10);

        List<ClusterDTO> result = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<PlacePoint> cluster = clusters.get(i);
            List<PlaceDTO> clusterPlaces = cluster.getPoints().stream()
                    .map(p -> new PlaceDTO(p.getId(), p.getLat(), p.getLon()))
                    .collect(Collectors.toList());
            result.add(new ClusterDTO(i, clusterPlaces));
        }
        return result;
    }
}