package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Place;
import com.fpt.gta.rest.mangeplace.PlaceDTO;
import com.google.maps.model.LatLng;

import java.util.List;

public interface PlaceService {

    List<PlaceDTO> searchCitiesByNameApi(String searchValue);

    List<PlaceDTO> searchAirportsByNameApi(String searchValue);

    List<PlaceDTO> searchPlaceByNameApi(String searchValue);

    List<PlaceDTO> autocompletePlaces(String searchValue);

    List<PlaceDTO> searchPlaceByNameDatabase(String searchValue);

    List<PlaceDTO> autocompleteCities(String searchValue);

    List<PlaceDTO> autocompleteAirports(String searchValue);

    Place findOrCreatePlaceByGooglePlaceId(Place place);

    Place findOrCreateSuggestPlaceByGooglePlaceId(Place place, int idTrip);

    Place findOrCreatePlaceByGooglePlaceId(String googlePlaceId);

    Place findOrCreateSuggestPlaceByGooglePlaceId(String googlePlaceId, int idTrip);

    LatLng findLatLngByPlaceId(String googlePlaceId);

    long getTravelTimeBetweenPlaces(Place origin, Place destination);

    boolean checkTravelTimeBetweenPlaces(Place origin, Place destination);

    Place getPlaceById(Integer idPlace);
}
