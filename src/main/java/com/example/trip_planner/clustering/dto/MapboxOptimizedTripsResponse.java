package com.example.trip_planner.clustering.dto;

import java.util.List;

public class MapboxOptimizedTripsResponse {
    private String code;
    private List<Waypoint> waypoints;
    private List<Trip> trips;

    public static class Waypoint {
        private double distance;
        private String name;
        private double[] location; // [longitude, latitude]
        private int waypoint_index;
        private int trips_index;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double[] getLocation() {
            return location;
        }

        public void setLocation(double[] location) {
            this.location = location;
        }

        public int getWaypoint_index() {
            return waypoint_index;
        }

        public void setWaypoint_index(int waypoint_index) {
            this.waypoint_index = waypoint_index;
        }

        public int getTrips_index() {
            return trips_index;
        }

        public void setTrips_index(int trips_index) {
            this.trips_index = trips_index;
        }
    }

    public static class Trip {
        private String geometry;
        private List<Leg> legs;
        private String weight_name;
        private double weight;
        private double duration;
        private double distance;

        public static class Leg {
            private String summary;
            private double weight;
            private double duration;
            private double distance;

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public double getWeight() {
                return weight;
            }

            public void setWeight(double weight) {
                this.weight = weight;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }
        }

        public String getGeometry() {
            return geometry;
        }

        public void setGeometry(String geometry) {
            this.geometry = geometry;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public String getWeight_name() {
            return weight_name;
        }

        public void setWeight_name(String weight_name) {
            this.weight_name = weight_name;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
