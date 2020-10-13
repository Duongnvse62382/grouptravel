package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.InternalServerErrorException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.exception.UnprocessableEntityException;
import com.fpt.gta.model.entity.PlaceType;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.BlobService;
import com.fpt.gta.model.service.PlaceService;
import com.fpt.gta.rest.mangeplace.PlaceDTO;
import com.fpt.gta.util.DateUtil;
import com.fpt.gta.util.GeometryUtil;
import com.fpt.gta.util.LanguageUtil;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private static final int MAXIMUM_PHOTO = 3;
    private static final int MAXIMUM_SPEND_QUARTER_TIME = 24;
    private static final int TIME_RATE = 15;
    private static final int HOUR = 60 * 60;
    private static final int TRAVEL_TIME_LIMIT = 4;
    PlaceRepository placeRepository;
    GeoApiContext geoApiContext;
    PlaceTypeRepository placeTypeRepository;
    PlaceWithTypeRepository placeWithTypeRepository;
    OpeningHourRepository openingHourRepository;
    PlaceImageRepository placeImageRepository;
    TripRepository tripRepository;
    BlobService blobService;

    @Autowired
    public PlaceServiceImpl(PlaceRepository placeRepository, GeoApiContext geoApiContext, PlaceTypeRepository placeTypeRepository, PlaceWithTypeRepository placeWithTypeRepository, OpeningHourRepository openingHourRepository, PlaceImageRepository placeImageRepository, TripRepository tripRepository, BlobService blobService) {
        this.placeRepository = placeRepository;
        this.geoApiContext = geoApiContext;
        this.placeTypeRepository = placeTypeRepository;
        this.placeWithTypeRepository = placeWithTypeRepository;
        this.openingHourRepository = openingHourRepository;
        this.placeImageRepository = placeImageRepository;
        this.tripRepository = tripRepository;
        this.blobService = blobService;
    }

    public Place getPlaceById(Integer idPlace) {
        Optional<Place> optionalPlace = placeRepository.findById(idPlace);
        if (optionalPlace.isPresent()) {
            return optionalPlace.get();
        } else {
            throw new NotFoundException("Not Found Place by IdPlace");
        }
    }


    @Override
    public List<PlaceDTO> searchCitiesByNameApi(String searchValue) {
        AutocompletePrediction[] prediction = null;
        List<PlaceDTO> listPrediction = new ArrayList<>();
        try {
            PlaceAutocompleteRequest.SessionToken session = new PlaceAutocompleteRequest.SessionToken();
            prediction = PlacesApi.placeAutocomplete(geoApiContext, searchValue, session).
                    types(PlaceAutocompleteType.REGIONS)
//                    .origin(new LatLng(10.823098, 106.629663))
//                 .types(PlaceAutocompleteType.CITIES)
                    .language("en").await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        for (AutocompletePrediction p : prediction) {
            PlaceDTO placeDTO = new PlaceDTO();
            placeDTO.setAddress(p.structuredFormatting.secondaryText);
            placeDTO.setName(p.structuredFormatting.mainText);
            placeDTO.setGooglePlaceId(p.placeId);
            System.out.println(p.distanceMeters);
            boolean isCitiesType = false;
            for (String type : p.types) {
                switch (type) {
                    case "locality":
                    case "administrative_area_level_1":
                    case "administrative_area_level_2":
                    case "administrative_area_level_3":
                        isCitiesType = true;
                        break;
                }
            }
            if (isCitiesType) {
                listPrediction.add(placeDTO);
            }
        }
        return listPrediction;
    }

    @Override
    public List<PlaceDTO> searchAirportsByNameApi(String searchValue) {
        AutocompletePrediction[] prediction = null;
        List<PlaceDTO> listPrediction = new ArrayList<>();
        try {
            PlaceAutocompleteRequest.SessionToken session = new PlaceAutocompleteRequest.SessionToken();
            prediction = PlacesApi.placeAutocomplete(geoApiContext, searchValue, session)
                    .types(PlaceAutocompleteType.ESTABLISHMENT)
                    .language("en").await();

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        for (AutocompletePrediction p : prediction) {
            String[] types = p.types;
            boolean check = false;
            for (String type : types) {
                if (type.contains("airport")) {
                    check = true;
                }
            }
            if (check) {
                PlaceDTO placeDTO = new PlaceDTO();
                placeDTO.setAddress(p.structuredFormatting.secondaryText);
                placeDTO.setName(p.structuredFormatting.mainText);
                placeDTO.setGooglePlaceId(p.placeId);
                listPrediction.add(placeDTO);
            }
        }
        return listPrediction;
    }

    @Override
    public List<PlaceDTO> searchPlaceByNameApi(String searchValue) {
        AutocompletePrediction[] prediction = null;
        List<PlaceDTO> listPrediction = new ArrayList<>();
        try {
            PlaceAutocompleteRequest.SessionToken session = new PlaceAutocompleteRequest.SessionToken();
            prediction = PlacesApi.placeAutocomplete(geoApiContext, searchValue, session)
                    .language("en").await();

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        for (AutocompletePrediction p : prediction) {
            PlaceDTO placeDTO = new PlaceDTO();
            placeDTO.setAddress(p.structuredFormatting.secondaryText);
            placeDTO.setName(p.structuredFormatting.mainText);
            placeDTO.setGooglePlaceId(p.placeId);
            listPrediction.add(placeDTO);
        }
        return listPrediction;
    }


    @Override
    public List<PlaceDTO> autocompletePlaces(String searchValue) {
        List<PlaceDTO> listApi = new ArrayList<>();
        List<PlaceDTO> listDatabase = new ArrayList<>();

        IntStream.of(1, 2).parallel().forEach(flag -> {
            if (flag == 1) {
                listApi.addAll(searchPlaceByNameApi(searchValue));
            } else if (flag == 2) {
                listDatabase.addAll(searchPlaceByNameDatabase(searchValue));
            }
        });

        List<PlaceDTO> listTemp = new ArrayList<>();

        if (!listDatabase.isEmpty()) {
            for (int i = 0; i < listDatabase.size(); i++) {
                int finalI = i;
                listApi.stream().filter(o -> o.getGooglePlaceId().equals(listDatabase.get(finalI).getGooglePlaceId())).forEach(
                        o -> listTemp.add(listDatabase.get(finalI))
                );
            }
        }
        for (PlaceDTO i : listTemp) {
            PlaceDTO predictionDTO;
            predictionDTO = listApi.get(listApi.indexOf(i));
            predictionDTO.setId(i.getId());
            listApi.set(listApi.indexOf(i), predictionDTO);
        }
        return listApi;
    }

    @Override
    public List<PlaceDTO> searchPlaceByNameDatabase(String searchValue) {
        List<Place> list = placeRepository.searchByNameStartsWith(searchValue, PageRequest.of(0, 10));
        List<PlaceDTO> listPrediction = new ArrayList<>();
        for (Place p : list) {
            PlaceDTO placeDTO = new PlaceDTO();
            placeDTO.setAddress(p.getAddress());
            placeDTO.setGooglePlaceId(p.getGooglePlaceId());
            placeDTO.setId(p.getId());
            placeDTO.setName(p.getName());
            listPrediction.add(placeDTO);
        }
        return listPrediction;
    }

    @Override
    public List<PlaceDTO> autocompleteCities(String name) {
        List<PlaceDTO> listApi = new ArrayList<>();
        List<PlaceDTO> listDatabase = new ArrayList<>();

        IntStream.of(1, 2).parallel().forEach(flag -> {
            if (flag == 1) {
                listApi.addAll(searchCitiesByNameApi(name));
            } else if (flag == 2) {
                listDatabase.addAll(searchPlaceByNameDatabase(name));
            }
        });

        List<PlaceDTO> listTemp = new ArrayList<>();

        if (!listDatabase.isEmpty()) {
            for (int i = 0; i < listDatabase.size(); i++) {
                int finalI = i;
                listApi.stream().filter(o -> o.getGooglePlaceId().equals(listDatabase.get(finalI).getGooglePlaceId())).forEach(
                        o -> listTemp.add(listDatabase.get(finalI))
                );
            }
        }
        for (PlaceDTO i : listTemp) {
            PlaceDTO predictionDTO;
            predictionDTO = listApi.get(listApi.indexOf(i));
            predictionDTO.setId(i.getId());
            listApi.set(listApi.indexOf(i), predictionDTO);
        }
        return listApi;
    }

    @Override
    public List<PlaceDTO> autocompleteAirports(String name) {
        List<PlaceDTO> listApi = new ArrayList<>();
        List<PlaceDTO> listDatabase = new ArrayList<>();

        IntStream.of(1, 2).parallel().forEach(flag -> {
            if (flag == 1) {
                listApi.addAll(searchAirportsByNameApi(name));
            } else if (flag == 2) {
                listDatabase.addAll(searchPlaceByNameDatabase(name));
            }
        });

        List<PlaceDTO> listTemp = new ArrayList<>();

        if (!listDatabase.isEmpty()) {
            for (int i = 0; i < listDatabase.size(); i++) {
                int finalI = i;
                listApi.stream().filter(o -> o.getGooglePlaceId().equals(listDatabase.get(finalI).getGooglePlaceId())).forEach(
                        o -> listTemp.add(listDatabase.get(finalI))
                );
            }
        }
        for (PlaceDTO i : listTemp) {
            PlaceDTO predictionDTO;
            predictionDTO = listApi.get(listApi.indexOf(i));
            predictionDTO.setId(i.getId());
            listApi.set(listApi.indexOf(i), predictionDTO);
        }
        return listApi;
    }

    @Override
    public Place findOrCreatePlaceByGooglePlaceId(Place place) {
        String googlePlaceId = "";
        if (place == null) {
            throw new UnprocessableEntityException("place can not null");
        }

        if (place.getId() != null) {
            return getPlaceById(place.getId());
        } else if (!StringUtils.isEmpty(place.getGooglePlaceId())) {
            googlePlaceId = place.getGooglePlaceId();
        } else {
            throw new UnprocessableEntityException("place can not empty");
        }

        try {
            Optional<Place> optionalPlace = placeRepository.findByGooglePlaceId(googlePlaceId);
            if (optionalPlace.isPresent()) {
                place = optionalPlace.get();
            } else {
                place = new Place();
                //fetch Place from api
                PlaceDetails details =
                        PlacesApi.placeDetails(geoApiContext,
                                googlePlaceId).language("en").await();
                //prepare latitude. longitude, TimeZone
                double lat = details.geometry.location.lat;
                double lng = details.geometry.location.lng;
                String timeZone = GeometryUtil.getTimeZoneFromLatLng(lat, lng);
                BigDecimal decimalLat = BigDecimal.valueOf(lat).setScale(6, RoundingMode.DOWN);
                BigDecimal decimalLng = BigDecimal.valueOf(lng).setScale(6, RoundingMode.DOWN);
                //prepare Place entity
                place.setTimeZone(timeZone);
                place.setLatitude(decimalLat);
                place.setLongitude(decimalLng);
                place.setGooglePlaceId(googlePlaceId);
                place.setName(details.name);
                place.setAddress(details.formattedAddress);
                place.setPhoneNumber(getPhone(details));
//                place.setEstimateSpendingHour(4 * TIME_RATE);
                //prepare Local Name
                String countryCode = "";
                for (int i = 0; i < details.addressComponents.length; i++) {
                    if (Arrays.stream(details.addressComponents[i].types)
                            .anyMatch(addressComponentType -> addressComponentType.toString().equalsIgnoreCase("country"))
                    ) {
                        countryCode = details.addressComponents[i].shortName;
                        break;
                    }
                }

                //save place without relationship database
                place = placeRepository.save(place);


                Set<String> localNameSet = new HashSet<>();
                List<PlaceImage> placeImageList = new ArrayList<>();

                //effective final
                String finalCountryCode = countryCode;
                String finalGooglePlaceId = googlePlaceId;
                Place finalPlace = place;
                IntStream.of(1, 2).parallel().forEach(flag -> {
                    if (flag == 1) {
                        localNameSet.addAll(getLocalNames(finalCountryCode, finalGooglePlaceId, details.name));
                    } else if (flag == 2) {
                        placeImageList.addAll(getPlaceImageList(details, finalPlace));
                    }
                });

                //Set<String> localNameSet = getLocalNames(countryCode, googlePlaceId, details.name);
                if (localNameSet.size() >= 1) {
                    place.setLocalName(localNameSet.iterator().next());
                }

                //save place image
                for (PlaceImage placeImage : placeImageList) {
                    placeImage.setPlace(place);
                    placeImageRepository.save(placeImage);
                }
                place.setPlaceImageList(placeImageList);

                //Save opening hour
                List<OpeningHour> openingHourList = getOpeningHourList(details);
                for (OpeningHour openingHour : openingHourList) {
                    openingHour.setPlace(place);
                    openingHourRepository.save(openingHour);
                }

                //save place type
                int sumOfType = 0;
                AddressType[] types = details.types;
                for (AddressType type : types) {
                    PlaceType placeType = placeTypeRepository.findByName(type.name());
                    PlaceWithType placeWithType = new PlaceWithType();
                    placeWithType.setPlaceType(placeType);
                    placeWithType.setPlace(place);
                    placeWithTypeRepository.save(placeWithType);
                    if (type.equals(AddressType.FOOD)) {
                        sumOfType += 4;
                    } else if (type.equals(AddressType.TOURIST_ATTRACTION)) {
                        sumOfType += 4;
                    } else if (type.equals(AddressType.AIRPORT)) {
                        sumOfType += 4;
                    } else if (type.equals(AddressType.POINT_OF_INTEREST)
                            || type.equals(AddressType.LOCALITY)) {
                        sumOfType += 4;
                    } else {
                        sumOfType += 1;
                    }
                }
                if (sumOfType > MAXIMUM_SPEND_QUARTER_TIME) {
                    place.setEstimateSpendingHour(MAXIMUM_SPEND_QUARTER_TIME * TIME_RATE);
                } else {
                    place.setEstimateSpendingHour(sumOfType * TIME_RATE);
                }
            }
            placeRepository.save(place);
            // tranh loi lazy
            place.getPlaceImageList().size();
            return place;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Place API error", e);
        }
    }

    @Override
    public Place findOrCreateSuggestPlaceByGooglePlaceId(Place place, int idTrip) {
        Place suggestPlace = findOrCreatePlaceByGooglePlaceId(place);
        try {
            Place endPlace = tripRepository.findById(idTrip).get().getEndPlace();
            long travelTime = getTravelTimeBetweenPlaces(endPlace, suggestPlace);
            if (travelTime > TRAVEL_TIME_LIMIT * HOUR) {
                suggestPlace.setIsTooFar(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestPlace;
    }

    @Override
    public Place findOrCreatePlaceByGooglePlaceId(String googlePlaceId) {
        Place place = new Place();
        place.setGooglePlaceId(googlePlaceId);
        return findOrCreatePlaceByGooglePlaceId(place);
    }

    @Override
    public Place findOrCreateSuggestPlaceByGooglePlaceId(String googlePlaceId, int idTrip) {
        Place suggestPlace = new Place();
        suggestPlace.setGooglePlaceId(googlePlaceId);
        return findOrCreateSuggestPlaceByGooglePlaceId(suggestPlace, idTrip);
    }

    @Override
    public LatLng findLatLngByPlaceId(String googlePlaceId) {
        LatLng latLng = new LatLng();
        PlaceDetails details =
                PlacesApi.placeDetails(geoApiContext,
                        googlePlaceId).language("en").awaitIgnoreError();
        //prepare latitude. longitude, TimeZone
        latLng = details.geometry.location;
        return latLng;
    }

    @Override
    public long getTravelTimeBetweenPlaces(Place origin, Place destination) {
        long sum = 0;
        DirectionsResult result =
                null;
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(new LatLng(origin.getLatitude().doubleValue(),
                            origin.getLongitude().doubleValue()))
                    .destination(new LatLng(destination.getLatitude().doubleValue(),
                            destination.getLongitude().doubleValue()))
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DirectionsRoute[] routes = result.routes;
        for (DirectionsRoute route : routes) {
            DirectionsLeg[] legs = route.legs;
            for (DirectionsLeg leg : legs) {
                System.out.println(leg.distance.inMeters);
                System.out.println(leg.duration.humanReadable);
                sum += leg.duration.inSeconds;
            }
        }
        return sum;
    }

    @Override
    public boolean checkTravelTimeBetweenPlaces(Place origin, Place destination) {
        boolean check = false;
        if (getTravelTimeBetweenPlaces(origin, destination) > TRAVEL_TIME_LIMIT * HOUR) {
            check = true;
        }
        return check;
    }

    public List<OpeningHour> getOpeningHourList(PlaceDetails details) {
        List<OpeningHour> openingHourList = new ArrayList<>();
        try {
            if (details.openingHours.weekdayText.length != 0) {
                String[] weekdayTexts = details.openingHours.weekdayText;
                for (String test : weekdayTexts) {
                    OpeningHour openingHourDTO = new OpeningHour();
                    String date = test.substring(0, test.indexOf(":"));
//                    System.out.println(date);
                    String openClose = test.replace(date + ":", "");
                    if (openClose.contains(":") && !openClose.contains(",")) {
                        openingHourDTO.setDayInWeek(DateUtil.parseDayOfWeek(date, Locale.ENGLISH));
                        String[] testArr = openClose.trim().split("–");
                        if (testArr.length == 2 && !testArr[0].contains("M")) {
                            String[] ampm = testArr[1].trim().split(" ");
                            LocalTime open = LocalTime.parse(testArr[0].concat(ampm[1]), DateTimeFormatter.ofPattern("h:mm a"));
                            LocalTime close = LocalTime.parse(testArr[1].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                            openingHourDTO.setOpenAt(open);
                            openingHourDTO.setCloseAt(close);
                        } else {
                            LocalTime open = LocalTime.parse(testArr[0].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                            LocalTime close = LocalTime.parse(testArr[1].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                            openingHourDTO.setOpenAt(open);
                            openingHourDTO.setCloseAt(close);
                        }
                    } else if (openClose.contains(":") && openClose.contains(",")) {
                        String[] testWithComma = openClose.trim().split(",");
                        for (String test4 : testWithComma) {
                            openingHourDTO.setDayInWeek(DateUtil.parseDayOfWeek(date, Locale.ENGLISH));
                            String[] testArr = test4.trim().split("–");
                            if (testArr.length == 2 && !testArr[0].contains("M")) {
                                String[] ampm = testArr[1].trim().split(" ");
                                LocalTime open = LocalTime.parse(testArr[0].concat(ampm[1]), DateTimeFormatter.ofPattern("h:mm a"));
                                LocalTime close = LocalTime.parse(testArr[1].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                                openingHourDTO.setOpenAt(open);
                                openingHourDTO.setCloseAt(close);
                            } else {
                                LocalTime open = LocalTime.parse(testArr[0].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                                LocalTime close = LocalTime.parse(testArr[1].trim(), DateTimeFormatter.ofPattern("h:mm a"));
                                openingHourDTO.setOpenAt(open);
                                openingHourDTO.setCloseAt(close);
                            }
                            System.out.println();
                        }
                    } else if (openClose.contains("Open")) {
                        openingHourDTO.setDayInWeek(DateUtil.parseDayOfWeek(date, Locale.ENGLISH));
                        LocalTime open = LocalTime.MIDNIGHT;
                        LocalTime close = LocalTime.parse("11:59 PM", DateTimeFormatter.ofPattern("hh:mm a"));
                        openingHourDTO.setOpenAt(open);
                        openingHourDTO.setCloseAt(close);
                    } else if (openClose.contains("Closed")) {
                        openingHourDTO.setDayInWeek(DateUtil.parseDayOfWeek(date, Locale.ENGLISH));
                        LocalTime openAndClose = LocalTime.MIDNIGHT;
                        openingHourDTO.setOpenAt(openAndClose);
                        openingHourDTO.setCloseAt(openAndClose);
                    } else {
                        System.out.println("Not supported");
                    }
                    openingHourList.add(openingHourDTO);
                }
            }
        } catch (Exception e) {
            System.out.println("error at getting place opening hour");
        }
        return openingHourList;
    }

    public List<PlaceImage> getPlaceImageList(PlaceDetails details, Place place) {
        long startFun = System.currentTimeMillis();
        List<PlaceImage> list = Collections.synchronizedList(new ArrayList<>());
        try {
            if (details.photos != null) {
                if (details.photos.length != 0) {
//                    Photo[] photoArray = new Photo[5];
                    Photo[] photoArray = new Photo[MAXIMUM_PHOTO];
                    for (int i = 0; i < details.photos.length; i++) {
                        if (i < photoArray.length) {
                            photoArray[i] = details.photos[i];
                        } else {
                            break;
                        }
                    }
                    Arrays.stream(photoArray).parallel().forEach(photo -> {
//                Arrays.stream(photoArray).forEach(photo -> {
                        try {
                            long start = System.currentTimeMillis();
                            PlaceImage imageDTO = new PlaceImage();
                            ImageResult r = PlacesApi.photo(geoApiContext, photo.photoReference)
                                    .maxHeight(photo.height).maxWidth(photo.width).await();
//                    System.out.println(r.contentType);
                            String result = blobService.putObject("place/"
                                            + place.getId()
                                            + "_"
                                            + Instant.now().toString()
                                            + "-"
                                            + Thread.currentThread().getId()
                                            + "-"
                                            + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE),
                                    r.contentType, r.imageData);
                            imageDTO.setUri(result);
                            imageDTO.setHeight(photo.height);
                            imageDTO.setWidth(photo.width);
                            list.add(imageDTO);
                            long end = System.currentTimeMillis();
                            System.out.println((end - start) + " ms");
                        } catch (Exception e) {
                            System.out.println("ONE image crawl error");
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("image crawl error");
        }
        System.out.println("Final " + (System.currentTimeMillis() - startFun));
        return list;
    }

    public String getPhone(PlaceDetails details) {
        String phone = "";
        try {
            if (details.formattedPhoneNumber != null) {
                phone = details.formattedPhoneNumber;
            }
        } catch (Exception e) {
            System.out.println("place has no phone number");
        }
        return phone;
    }

    public Set getLocalNames(String countryCode, String googlePlaceId, String name) {
        Set<String> localNames = Collections.synchronizedSet(new HashSet<>());
        LanguageUtil language = new LanguageUtil();
        Set<String> set = language.getAvailableLanguageFromCountryCode(countryCode);
        set.parallelStream().forEach(i -> {
            try {
                PlaceDetails details =
                        PlacesApi.placeDetails(geoApiContext,
                                googlePlaceId).language(i).await();
                String localName = details.name;
                if (!localName.equals(name)) {
                    localNames.add(localName);
                }
            } catch (Exception e) {
            }
        });
        return localNames;
    }

}
