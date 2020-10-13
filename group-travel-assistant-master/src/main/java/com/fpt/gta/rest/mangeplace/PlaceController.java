package com.fpt.gta.rest.mangeplace;

import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.service.PlaceService;
import com.fpt.gta.model.service.TripService;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private static final int MAX_RADIUS = 50000;
    private static final int RECOMMEND_RADIUS = 15000;
    GeoApiContext geoApiContext;
    PlaceService placeService;
    TripService tripService;
    ModelMapper modelMapper;

    @Autowired
    public PlaceController(GeoApiContext geoApiContext, PlaceService placeService, TripService tripService, ModelMapper modelMapper) {
        this.geoApiContext = geoApiContext;
        this.placeService = placeService;
        this.tripService = tripService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/searchByCity/{name}")
    public List<PlaceDTO> autocompleteCities(@PathVariable("name") String name) {
        return Arrays.asList(
                modelMapper.map(placeService.autocompleteCities(name), PlaceDTO[].class));
    }

    @GetMapping("/searchByAirport/{name}")
    public List<PlaceDTO> autocompleteAirports(@PathVariable("name") String name) {
        return Arrays.asList(
                modelMapper.map(placeService.autocompleteAirports(name), PlaceDTO[].class));
    }

    @GetMapping("/searchByName/{name}")
    public List<PlaceDTO> autocompletePlaces(@PathVariable("name") String name) {
        return Arrays.asList(
                modelMapper.map(placeService.autocompletePlaces(name), PlaceDTO[].class));
    }

    @GetMapping("/crawl/{id}")
    public PlaceDTO crawlPlace(@PathVariable("id") String id,
                               @RequestParam(value = "idTrip", required = false) String idTrip) {
        if (idTrip != null) {
            int tripId = Integer.parseInt(idTrip);
            if (tripId > 1) {
                return modelMapper.map(placeService.findOrCreateSuggestPlaceByGooglePlaceId(id, tripId), PlaceDTO.class);
            }
        }
        return modelMapper.map(placeService.findOrCreatePlaceByGooglePlaceId(id), PlaceDTO.class);
    }

    @GetMapping("/searchNearby/{id}")
    public List<PlaceDTO> searchNearBy(@PathVariable(value = "id") String tripId,
                                       @RequestParam(value = "type", required = false) String type) {
        List<String> places = new ArrayList<>();
        List<PlaceDTO> placeDTOS = Collections.synchronizedList(new ArrayList<>());
        PlaceType placeType = PlaceType.TOURIST_ATTRACTION;
        try {
            if (type != null) {
                switch (type) {
                    case "airport":
                        placeType = PlaceType.AIRPORT;
                        break;
                    case "train_station":
                        placeType = PlaceType.TRAIN_STATION;
                        break;
                    case "transit_station":
                        placeType = PlaceType.TRANSIT_STATION;
                        break;
                }
            }
            Trip trip = tripService.getTripById(Integer.parseInt(tripId));
            LatLng placeLatLng = placeService.findLatLngByPlaceId(trip.getEndPlace().getGooglePlaceId());
            PlacesSearchResponse placesSearchResponse = PlacesApi.nearbySearchQuery(geoApiContext,
                    placeLatLng).radius(RECOMMEND_RADIUS)
                    .type(placeType).language("en").await();
            PlacesSearchResult[] results = placesSearchResponse.results;
            for (PlacesSearchResult r : results) {
                places.add(r.placeId);
            }
            places.parallelStream().forEach(e -> placeDTOS.add(modelMapper.
                    map(placeService.findOrCreatePlaceByGooglePlaceId(e),
                            PlaceDTO.class)));
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(
                modelMapper.map(placeDTOS, PlaceDTO[].class));
    }
}
