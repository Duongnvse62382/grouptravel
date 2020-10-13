package com.fpt.gta.rest.managesuggestion;

import com.fpt.gta.algorithm.suggest.Record;
import com.fpt.gta.algorithm.suggest.SuggestAlgorithm2;
import com.fpt.gta.model.constant.SuggestedActivityStatus;
import com.fpt.gta.model.constant.SuggestedActivityType;
import com.fpt.gta.model.entity.Place;
import com.fpt.gta.model.entity.PlaceImage;
import com.fpt.gta.model.entity.SuggestedActivity;
import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.repository.TripRepository;
import com.fpt.gta.model.service.PlaceService;
import com.fpt.gta.model.service.TripService;
import com.fpt.gta.util.CustomListUtil;
import com.fpt.gta.util.CustomMapUtil;
import com.fpt.gta.util.CustomTimestampUtil;
import com.fpt.gta.util.DateUtil;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class SuggestionService {

    private static final int SECOND = 1;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int RECOMMEND_RADIUS = 15000;
    private static final int DAY_CAPACITY = 480;
    private static final int START_CAPACITY = 240;
    private static final int END_CAPACITY = 300;
    private static final int TRAVEL_TIME = 15;
    private static final int TRAVEL_LIMIT = 4;
    private static final int ITERATION = 2000;
    private static final int NORMAL_DAY_BEGIN = 480;
    private static final int START_DAY_BEGIN = 600;
    private static final int START_DAY_UPPER_LIMIT = 19;
    private static final int END_DAY_UPPER_LIMIT = 14;
    private static final DistanceFunction EUCLIDEAN = new EuclideanDistance();
    GeoApiContext geoApiContext;
    PlaceService placeService;
    TripService tripService;
    ModelMapper modelMapper;
    TripRepository tripRepository;

    @Autowired
    public SuggestionService(GeoApiContext geoApiContext, PlaceService placeService, TripService tripService, ModelMapper modelMapper, TripRepository tripRepository) {
        this.geoApiContext = geoApiContext;
        this.placeService = placeService;
        this.tripService = tripService;
        this.modelMapper = modelMapper;
        this.tripRepository = tripRepository;
    }

    public Record createRecord(SuggestedActivity dto) {
        Record record = new Record();
        PlaceDTO placeDTO = new PlaceDTO();
        Place place = dto.getStartPlace();
        //set PlaceDTO
        placeDTO.setGooglePlaceId(place.getGooglePlaceId());
        placeDTO.setAddress(place.getAddress());
        placeDTO.setEstimateSpendingHour(place.getEstimateSpendingHour());
        placeDTO.setLatitude(place.getLatitude());
        placeDTO.setLocalName(place.getLocalName());
        placeDTO.setLongitude(place.getLongitude());
        placeDTO.setPhoneNumber(place.getPhoneNumber());
        placeDTO.setName(place.getName());
        placeDTO.setTimeZone(place.getTimeZone());
        placeDTO.setId(place.getId());
        placeDTO.setPlaceImageList(Arrays.asList(modelMapper.map(
                place.getPlaceImageList(), PlaceDTO.PLaceImageDTO[].class)));
        // set record
        record.setName(dto.getName());
        record.setLat(dto.getStartPlace().getLatitude().doubleValue());
        record.setLng(dto.getStartPlace().getLongitude().doubleValue());
        record.setPlaceName(dto.getStartPlace().getName());
        record.setPlacePhoneNumber(dto.getStartPlace().getPhoneNumber());
        record.setPlaceAddress(dto.getStartPlace().getAddress());
        record.setPlaceLocalName(dto.getStartPlace().getLocalName());
        record.setIdType(dto.getIdType());
        record.setIsAdded(false);
        record.setPlaceLong(dto.getStartPlace().getLongitude());
        record.setPlaceLat(dto.getStartPlace().getLatitude());
        record.setPlaceDTO(placeDTO);
        record.setPlaceTimeZone(dto.getStartPlace().getTimeZone());
        record.setPlaceImageList(placeDTO.getPlaceImageList());
        record.setTimeSpent(place.getEstimateSpendingHour());
        record.setIsTooFar(dto.getIsTooFar());
        return record;
    }

    public PlaceDTO makePlaceDTO(Place place, List<PlaceImage> list) {
        PlaceDTO placeDTO = new PlaceDTO();
        //set PlaceDTO
        placeDTO.setGooglePlaceId(place.getGooglePlaceId());
        placeDTO.setAddress(place.getAddress());
        placeDTO.setEstimateSpendingHour(place.getEstimateSpendingHour());
        placeDTO.setLatitude(place.getLatitude());
        placeDTO.setLocalName(place.getLocalName());
        placeDTO.setLongitude(place.getLongitude());
        placeDTO.setPhoneNumber(place.getPhoneNumber());
        placeDTO.setName(place.getName());
        placeDTO.setTimeZone(place.getTimeZone());
        placeDTO.setId(place.getId());
        placeDTO.setPlaceImageList(Arrays.asList(modelMapper.map(
                list, PlaceDTO.PLaceImageDTO[].class)));
        return placeDTO;
    }

    public Record createRecordFromNearByPlace(PlaceDTO dto) {
        Record record = new Record();
        // set record
        record.setName(dto.getName());
        record.setLat(dto.getLatitude().doubleValue());
        record.setLng(dto.getLongitude().doubleValue());
        record.setPlaceName(dto.getName());
        record.setPlacePhoneNumber(dto.getPhoneNumber());
        record.setPlaceAddress(dto.getAddress());
        record.setPlaceLocalName(dto.getLocalName());
        record.setIdStatus(SuggestedActivityStatus.PUBLIC);
        record.setIdType(SuggestedActivityType.ACTIVITY);
        record.setIsAdded(true);
        record.setPlaceLong(dto.getLongitude());
        record.setPlaceLat(dto.getLatitude());
        record.setPlaceDTO(dto);
        record.setPlaceTimeZone(dto.getTimeZone());
        record.setPlaceImageList(dto.getPlaceImageList());
        record.setTimeSpent(dto.getEstimateSpendingHour());
        return record;
    }

    public static ActivityDTO createActivity(Record record, Timestamp time) {
        ActivityDTO activityDTO = new ActivityDTO();

        // set feature cho record
        // set record
        activityDTO.setName(record.getName());
        activityDTO.setStartPlaceName(record.getPlaceName());
        activityDTO.setEndPlaceName(record.getPlaceName());
        activityDTO.setStartAt(time);
        // hard code end time fix later
        activityDTO.setEndAt(CustomTimestampUtil.updateByMinutes(time, record.getTimeSpent()));
        // fix
        activityDTO.setStartAddress(record.getPlaceAddress());
        activityDTO.setEndAddress(record.getPlaceAddress());
        activityDTO.setStartPhoneNumber(record.getPlacePhoneNumber());
        activityDTO.setEndPhoneNumber(record.getPlacePhoneNumber());
        activityDTO.setStartLatitude(record.getPlaceLat());
        activityDTO.setEndLatitude(record.getPlaceLat());
        activityDTO.setStartLongitude(record.getPlaceLong());
        activityDTO.setEndLongitude(record.getPlaceLong());
        activityDTO.setStartPlaceLocalName(record.getPlaceLocalName());
        activityDTO.setEndPlaceLocalName(record.getPlaceLocalName());
        activityDTO.setStartTimeZone(record.getPlaceTimeZone());
        activityDTO.setEndTimeZone(record.getPlaceTimeZone());
        activityDTO.setStartPlace(record.getPlaceDTO());
        activityDTO.setEndPlace(record.getPlaceDTO());
        activityDTO.setIdStatus(record.getIdStatus());
        activityDTO.setIdType(record.getIdType());
        activityDTO.setIsInPlan(record.getIsInPlan());
        activityDTO.setIsAdded(record.getIsAdded());
        activityDTO.setPlaceImageList(record.getPlaceImageList());
        activityDTO.setIsTooFar(record.getIsTooFar());
        return activityDTO;
    }

    public static ActivityDTO createLeftOut(Record record) {
        ActivityDTO activityDTO = new ActivityDTO();
        // set record
        activityDTO.setName(record.getName());
        activityDTO.setStartPlaceName(record.getPlaceName());
        activityDTO.setEndPlaceName(record.getPlaceName());
        activityDTO.setStartAddress(record.getPlaceAddress());
        activityDTO.setEndAddress(record.getPlaceAddress());
        activityDTO.setStartPhoneNumber(record.getPlacePhoneNumber());
        activityDTO.setEndPhoneNumber(record.getPlacePhoneNumber());
        activityDTO.setStartLatitude(record.getPlaceLat());
        activityDTO.setEndLatitude(record.getPlaceLat());
        activityDTO.setStartLongitude(record.getPlaceLong());
        activityDTO.setEndLongitude(record.getPlaceLong());
        activityDTO.setStartPlaceLocalName(record.getPlaceLocalName());
        activityDTO.setEndPlaceLocalName(record.getPlaceLocalName());
        activityDTO.setStartTimeZone(record.getPlaceTimeZone());
        activityDTO.setEndTimeZone(record.getPlaceTimeZone());
        activityDTO.setStartPlace(record.getPlaceDTO());
        activityDTO.setEndPlace(record.getPlaceDTO());
        activityDTO.setIdStatus(record.getIdStatus());
        activityDTO.setIdType(record.getIdType());
        activityDTO.setIsInPlan(record.getIsInPlan());
        activityDTO.setIsAdded(record.getIsAdded());
        activityDTO.setPlaceImageList(record.getPlaceImageList());
        return activityDTO;
    }

    public long getTravelTime(Record record1, Record record2) {
        long sum = 0;
        DirectionsResult result =
                null;
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(new LatLng(record1.getLat(), record1.getLng()))
                    .destination(new LatLng(record2.getLat(), record2.getLng()))
                    .await();
//                    .avoid(DirectionsApi.RouteRestriction.TOLLS)
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DirectionsRoute[] routes = result.routes;
        for (DirectionsRoute route : routes) {
//            System.out.println(route.toString());
            DirectionsLeg[] legs = route.legs;
            for (DirectionsLeg leg : legs) {
//                DirectionsStep[] steps = leg.steps;
//                for (DirectionsStep step : steps) {
//                    System.out.println(step.travelMode.toString());
//                }
                System.out.println(leg.distance.inMeters);
                System.out.println(leg.duration.humanReadable);
                sum += leg.duration.inSeconds;
            }
        }
        return sum;

    }

    public DayDTO getDayCountInCity(int tripId) {
        DayDTO dayDTO = new DayDTO();
        Trip trip = tripService.getTripById2(tripId);
        System.out.println(trip.getStartAt());
        System.out.println(trip.getEndAt());
        int daysLongType = (int) DateUtil.getActualDateDiff(trip.getStartAt(), trip.getEndAt()) + 1;
        LocalDateTime startTime = trip.getStartAt().toLocalDateTime();
        LocalDateTime endTime = trip.getEndAt().toLocalDateTime();
        boolean startDayStatus = true;
        boolean endDayStatus = true;
        boolean sameDayStatus = false;
        if (startTime.toLocalTime().compareTo(LocalTime.of(START_DAY_UPPER_LIMIT, 0)) > 0) {
            startDayStatus = false;
            daysLongType -= 1;
        }
        if (startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            System.out.println("One Day Trip:" + ChronoUnit.MINUTES.between(startTime, endTime));
            if (startTime.toLocalTime().getHour() < 10) {
                if (!endTime.isAfter(endTime.toLocalDate().atStartOfDay().plus(13, ChronoUnit.HOURS))) {
                    sameDayStatus = true;
                }
            } else {
                if (ChronoUnit.MINUTES.between(startTime, endTime) < START_CAPACITY) {
                    sameDayStatus = true;
                }
            }
            endDayStatus = false;
        } else if (endTime.toLocalTime().compareTo(LocalTime.of(END_DAY_UPPER_LIMIT, 0)) < 0) {
            endDayStatus = false;
            daysLongType -= 1;
        }
        dayDTO.setQuantity(daysLongType);
        dayDTO.setStartStatus(startDayStatus);
        dayDTO.setEndStatus(endDayStatus);
        dayDTO.setSameDay(sameDayStatus);
        if (!startDayStatus) {
            dayDTO.setStartDate(startTime.plus(1, ChronoUnit.DAYS));
        } else {
            dayDTO.setStartDate(startTime);
        }
        return dayDTO;
    }

    public void testNearBy(String text) {
        try {
            PlacesSearchResponse response =
                    PlacesApi.nearbySearchQuery(geoApiContext, new LatLng(10.823098, 106.629663))
//                            .radius(10000)
                            .rankby(RankBy.DISTANCE)
                            .name(text)
                            .await();
            PlacesSearchResult[] results = response.results;
            for (PlacesSearchResult result : results) {
                System.out.println(result.name);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String testGeocoding() throws InterruptedException, ApiException, IOException {
        DirectionsApiRequest.Waypoint[] objWaypoints = new DirectionsApiRequest.Waypoint[4];
        objWaypoints[0] = new DirectionsApiRequest.Waypoint(new LatLng(11.940419, 108.458313));
        objWaypoints[1] = new DirectionsApiRequest.Waypoint(new LatLng(11.940419, 108.458313));
        objWaypoints[2] = new DirectionsApiRequest.Waypoint(new LatLng(11.937663, 108.438364));
        objWaypoints[3] = new DirectionsApiRequest.Waypoint(new LatLng(11.94867, 108.431565));
        DirectionsResult result =
                DirectionsApi.newRequest(geoApiContext)
                        .origin(new LatLng(11.935728, 108.447872))
                        .destination(new LatLng(11.948468, 108.436386))
                        .waypoints(objWaypoints)
                        .optimizeWaypoints(true)
                        .await();
        DirectionsRoute[] routes = result.routes;
        GeocodedWaypoint[] waypoints = result.geocodedWaypoints;
        for (DirectionsRoute route : routes) {
            System.out.println(route.toString());
            DirectionsLeg[] legs = route.legs;
            for (DirectionsLeg leg : legs) {
                System.out.println(leg.distance.inMeters);
                System.out.println(leg.duration.humanReadable);
            }
        }
        for (GeocodedWaypoint waypoint : waypoints) {
            System.out.println(waypoint.toString());
        }
        return result.toString();
    }

    public List<ActivityDTO> createPlan(List<SuggestedActivity> activityDTOList, int k,
                                        Timestamp dayStart, int tripId) throws Exception {
        Record tripDestination = getTripDestination(tripId);
        Set<Record> records2 = new HashSet<>();
        List<Record> nearByRecord = new ArrayList<>();
        List<ActivityDTO> dayByDayActivities = new ArrayList<>();
        for (SuggestedActivity dto : activityDTOList) {
            records2.add(createRecord(dto));
        }
        List<PlaceDTO> listPlaces = getTripNearByPlaces(tripId);
        for (PlaceDTO listPlace : listPlaces) {
            nearByRecord.add(createRecordFromNearByPlace(listPlace));
        }
        if (activityDTOList.size() < k) {
            int missingPlace = k - activityDTOList.size();
            CustomListUtil.addPlacesToSet(records2, nearByRecord, missingPlace);
        }
        // add places to plan
        List<Record> records = CustomListUtil.convertSetToList(records2);
        nearByRecord.removeAll(records);
//        System.out.println(records.size());
        SuggestAlgorithm2 algorithm = new SuggestAlgorithm2();
        Map<Integer, List<Record>> map = algorithm.preparePlan(k);
        Map<Integer, List<Record>> clusters = algorithm.run(map, records, k,
                0, ITERATION, EUCLIDEAN, DAY_CAPACITY);
        System.out.println("END RUN 1st ALGORITHM");
        Map<Integer, List<Record>> mapEnd;
        if (k > 1) {
            Map<Integer, List<Record>> mapStart = SuggestAlgorithm2.
                    makePlanForStartDay(clusters, k, START_CAPACITY);
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapStart, k, END_CAPACITY);
        } else {
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(clusters, k, START_CAPACITY);
        }
        AtomicInteger index = new AtomicInteger(0);
        AtomicInteger index2 = new AtomicInteger(0);
        // Printing the cluster configuration
        // chuan bi hashmap de them place
        mapEnd.forEach((key, value) -> {
            int capacity = CustomListUtil.getTotalTimeSpendRecord(value);
            if (key == 0) {
                int beginCapacity = START_CAPACITY - capacity;
                if (beginCapacity > 0) {
                    List<Record> recordList = CustomListUtil.getSortedList(nearByRecord, value.get(value.size() - 1));
                    map.put(key, CustomListUtil.getSystemAddPlaces(value, recordList, beginCapacity));
                }
            } else if (key < k - 1) {
                int normalDayCapacity = DAY_CAPACITY - capacity;
                if (normalDayCapacity > 0) {
                    List<Record> recordList = CustomListUtil.getSortedList(nearByRecord, value.get(value.size() - 1));
                    map.put(key, CustomListUtil.getSystemAddPlaces(value, recordList, normalDayCapacity));
                }
            } else if (key == k - 1) {
                int endCapacity = END_CAPACITY - capacity;
                if (endCapacity > 0) {
                    List<Record> recordList = CustomListUtil.getSortedList(nearByRecord, value.get(value.size() - 1));
                    map.put(key, CustomListUtil.getSystemAddPlaces(value, recordList, endCapacity));
                }
            }
        });
        // end chuan bi
        mapEnd.forEach((key, value) -> {
            if (!value.isEmpty()) {
                if (value.get(0).getIsInPlan()) {
                    value = CustomListUtil.getSortedList(value, tripDestination);
                    // sorted Ascending by distance from trip place
//                    for (int i = value.size(); i-- > 0; ) {
//                        Record c = value.get(i);
//                        if (getTravelTime(c, tripDestination) > TRAVEL_LIMIT * HOUR) {
//                            c.setIsTooFar(true);
//                        } else {
//                            break;
//                        }
//                    }
                    //
                    Timestamp activityTime;
                    LocalDateTime normalDay = dayStart.toLocalDateTime().toLocalDate().atStartOfDay();
                    if (index.get() == 0) {
                        int startDayHour = dayStart.toLocalDateTime().getHour();
                        if (startDayHour < 10) {
                            Timestamp timestamp = Timestamp.valueOf(normalDay);
                            activityTime = Timestamp.from(timestamp.toInstant().
                                    plus(index.intValue(), ChronoUnit.DAYS).
                                    plus(START_DAY_BEGIN, ChronoUnit.MINUTES));
                        } else {
                            activityTime = Timestamp.from(dayStart.toInstant().
                                    plus(index.intValue(), ChronoUnit.DAYS));
                        }
                    } else {
                        Timestamp timestamp = Timestamp.valueOf(normalDay);
                        activityTime = Timestamp.from(timestamp.toInstant()
                                .plus(index.intValue(), ChronoUnit.DAYS).
                                        plus(NORMAL_DAY_BEGIN, ChronoUnit.MINUTES));
                    }
                    for (int i = 0; i < value.size(); i++) {
                        Record r = value.get(i);
                        if (i - 1 < 0) {
                            dayByDayActivities.add(createActivity(r, activityTime));
                        } else {
                            Timestamp time = dayByDayActivities.get((index2.get() - 1)).getEndAt();
                            dayByDayActivities.add(createActivity(r,
                                    CustomTimestampUtil.updateByMinutes(time, TRAVEL_TIME)));
                        }
                        index2.getAndIncrement();
                    }
                } else {
                    for (Record r : value) {
                        dayByDayActivities.add(createLeftOut(r));
                    }
                }
                // sau khi loop tang day len 1 don vi
                index.getAndIncrement();
                System.out.println("-------------------------- CLUSTER " + key + "----------------------------");
                String members = String.join(", ", value.stream().map(Record::toString).collect(toSet()));
                System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
                System.out.println();
            }
        });
        return dayByDayActivities;
    }

    public List<PlaceDTO> getTripNearByPlaces(int tripId) {
        List<String> places = new ArrayList<>();
        List<PlaceDTO> placeDTOS = Collections.synchronizedList(new ArrayList<>());
        PlaceType placeType = PlaceType.TOURIST_ATTRACTION;
        try {
            Trip trip = tripService.getTripById(tripId);
            LatLng placeLatLng = placeService.findLatLngByPlaceId(trip.getEndPlace().
                    getGooglePlaceId());
            PlacesSearchResponse placesSearchResponse = PlacesApi.nearbySearchQuery(geoApiContext,
                    placeLatLng).radius(RECOMMEND_RADIUS)
                    .type(placeType).language("en").await();
            PlacesSearchResult[] results = placesSearchResponse.results;
            for (PlacesSearchResult r : results) {
                places.add(r.placeId);
            }
            places.parallelStream().forEach(e -> {
                Place place = placeService.findOrCreatePlaceByGooglePlaceId(e);
                List<PlaceImage> placeImageList = place.getPlaceImageList();
                PlaceDTO dto = makePlaceDTO(place, placeImageList);
                placeDTOS.add(dto);
            });
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return placeDTOS;
    }


    public List<ActivityDTO> getPlanFromNearByPlaces(List<PlaceDTO> places, int k, Timestamp dayStart)
            throws Exception {
        Set<Record> records2 = new HashSet<>();
        List<ActivityDTO> dayByDayActivities = new ArrayList<>();
        for (PlaceDTO dto : places) {
            records2.add(createRecordFromNearByPlace(dto));
        }
        List<Record> records = CustomListUtil.convertSetToList(records2);
        AtomicInteger index = new AtomicInteger(0);
        AtomicInteger index2 = new AtomicInteger(0);

        SuggestAlgorithm2 algorithm = new SuggestAlgorithm2();
        Map<Integer, List<Record>> map = algorithm.preparePlan(k);
        Map<Integer, List<Record>> clusters = algorithm.run(map, records, k,
                0, ITERATION, EUCLIDEAN, DAY_CAPACITY);
        System.out.println("END RUN 1st ALGORITHM");
        Map<Integer, List<Record>> mapEnd;
        if (k > 1) {
            Map<Integer, List<Record>> mapStart = SuggestAlgorithm2.makePlanForStartDay(clusters, k,
                    START_CAPACITY);
            mapEnd =
                    SuggestAlgorithm2.makePlanForEndDay(mapStart, k, END_CAPACITY);
        } else {
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(clusters, k, START_CAPACITY);
        }
        // Printing the cluster configuration
//        clusters.forEach((key, value) -> {
        mapEnd.forEach((key, value) -> {
            if (!value.isEmpty()) {
                if (value.get(0).getIsInPlan()) {
                    Timestamp activityTime;
                    LocalDateTime normalDay = dayStart.toLocalDateTime().toLocalDate().atStartOfDay();
                    if (index.get() == 0) {
                        int startDayHour = dayStart.toLocalDateTime().getHour();
                        if (startDayHour < 10) {
                            Timestamp timestamp = Timestamp.valueOf(normalDay);
                            activityTime = Timestamp.from(timestamp.toInstant().
                                    plus(index.intValue(), ChronoUnit.DAYS).
                                    plus(START_DAY_BEGIN, ChronoUnit.MINUTES));
                        } else {
                            activityTime = Timestamp.from(dayStart.toInstant().
                                    plus(index.intValue(), ChronoUnit.DAYS));
                        }
                    } else {
                        Timestamp timestamp = Timestamp.valueOf(normalDay);
                        activityTime = Timestamp.from(timestamp.toInstant()
                                .plus(index.intValue(), ChronoUnit.DAYS).
                                        plus(NORMAL_DAY_BEGIN, ChronoUnit.MINUTES));
                    }
                    for (int i = 0; i < value.size(); i++) {
                        Record r = value.get(i);
                        if (i - 1 < 0) {
                            dayByDayActivities.add(createActivity(r, activityTime));
                        } else {
                            Timestamp time = dayByDayActivities.get((index2.get() - 1)).getEndAt();

                            dayByDayActivities.add(createActivity(r,
                                    CustomTimestampUtil.updateByMinutes(time, 15)));
                        }
                        index2.getAndIncrement();
                    }
                } else {
//                    for (Record r : value) {
//                        dayByDayActivities.add(createLeftOut(r));
//                    }
                }
                // sau khi loop tang ngay len 1 don vi
                index.getAndIncrement();
                System.out.println("-------------------------- CLUSTER NEARBY " + key + "----------------------------");
                // Sorting the coordinates to see the most significant tags first.
                String members = String.join(", ", value.stream().map(Record::toString).collect(toSet()));
                System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
                System.out.println();
            }
        });
        return dayByDayActivities;
    }

    public Record getTripDestination(int tripId) {
        Record record = new Record();
        Place tripDestination = tripRepository.findById(tripId).get().getEndPlace();
        if (tripDestination != null) {
            record.setLat(tripDestination.getLatitude().doubleValue());
            record.setLng(tripDestination.getLongitude().doubleValue());
        }
        return record;
    }

    public Map<Integer, List<Record>> newCreatePlan(List<SuggestedActivity> activityDTOList,
                                                    DayDTO dayOfTrip, int tripId) throws Exception {
        Set<Record> records2 = new HashSet<>();
//        Record tripDestination = getTripDestination(tripId);
        int k = dayOfTrip.getQuantity();
        List<Record> nearByRecord = new ArrayList<>();
        for (SuggestedActivity dto : activityDTOList) {
            records2.add(createRecord(dto));
        }
        List<PlaceDTO> listPlaces = getTripNearByPlaces(tripId);
        for (PlaceDTO listPlace : listPlaces) {
            nearByRecord.add(createRecordFromNearByPlace(listPlace));
        }
//        System.out.println(records.size());
        SuggestAlgorithm2 algorithm = new SuggestAlgorithm2();
        Map<Integer, List<Record>> map = algorithm.preparePlan(k);
        // add isTooFarPlaces to leftOut and remove from list suggest activity;
        map = CustomMapUtil.addTooFarPlacesToLeftOut(map, k, records2);
        // add places to plan
        if (records2.size() < k) {
            long numMatches = activityDTOList.stream()
                    .filter(c -> !c.getIsTooFar())
                    .count();
            int missingPlace = k - (int) numMatches;
            CustomListUtil.addPlacesToSet(records2, nearByRecord, missingPlace);
        }
        List<Record> records = CustomListUtil.convertSetToList(records2);
        nearByRecord.removeAll(records);
        Map<Integer, List<Record>> clusters = algorithm.run(map, records, k,
                0, ITERATION, EUCLIDEAN, DAY_CAPACITY);
        System.out.println("END RUN 1st ALGORITHM");
        Map<Integer, List<Record>> mapEnd = clusters;
        if (dayOfTrip.isStartStatus() && dayOfTrip.isEndStatus()) {
            System.out.println("normal day");
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(mapEnd, k, START_CAPACITY);
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapEnd, k, END_CAPACITY);
        } else if (dayOfTrip.isEndStatus()) {
            System.out.println("end day");
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapEnd, k, END_CAPACITY);
        } else if (dayOfTrip.isStartStatus()) {
            System.out.println("start day");
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(mapEnd, k, START_CAPACITY);
        }
        // chuan bi hashmap de them place
        List<Record> leftOutRecord = mapEnd.get(k);
        Map<Integer, List<Record>> finalMapEnd = new HashMap<>(mapEnd);
        if (!leftOutRecord.isEmpty()) {
            mapEnd.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    if (value.get(0).getIsInPlan()) {
                        int capacity = CustomListUtil.getTotalTimeSpendRecord(value);
                        Record centerRecord = CustomListUtil.getCenterRecord(value);
                        if (dayOfTrip.isStartStatus() && dayOfTrip.isEndStatus()) {
                            if (key == 0) {
                                int beginCapacity = START_CAPACITY - capacity;
                                System.out.println("Begin Capacity:" + beginCapacity);
                                if (beginCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, centerRecord);
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, beginCapacity));
                                }
                            } else if (key != k - 1) {
                                int normalDayCapacity = DAY_CAPACITY - capacity;
                                if (normalDayCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, normalDayCapacity));
                                }
                            } else {
                                int endCapacity = END_CAPACITY - capacity;
                                if (endCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, endCapacity));
                                }
                            }
                        } else if (dayOfTrip.isStartStatus()) {
                            if (key == 0) {
                                int beginCapacity = START_CAPACITY - capacity;
                                if (beginCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, beginCapacity));
                                }
                            } else {
                                int normalDayCapacity = DAY_CAPACITY - capacity;
                                if (normalDayCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, normalDayCapacity));
                                }
                            }
                        } else {
                            if (key != k - 1) {
                                int normalDayCapacity = DAY_CAPACITY - capacity;
                                if (normalDayCapacity > 0) {
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, normalDayCapacity));
                                }
                            } else {
                                int endCapacity = END_CAPACITY - capacity;
                                if (endCapacity > 0) {
                                    finalMapEnd.put(key, CustomListUtil.addPlaceFromLeftOut(value, leftOutRecord, endCapacity));
                                    CustomListUtil.getSortedList(leftOutRecord, value.get(value.size() - 1));
                                }
                            }
                        }
                    }
                }
            });
        }
        if (leftOutRecord.isEmpty()) {
            finalMapEnd.put(k, leftOutRecord);
        }
        Map<Integer, List<Record>> finalMapEnd1 = new HashMap<>(finalMapEnd);
        finalMapEnd.forEach((key, value) ->
        {
            if (!value.isEmpty()) {
                if (value.get(0).getIsInPlan()) {
                    int capacity = CustomListUtil.getTotalTimeSpendRecord(value);
                    Record centerRecord = CustomListUtil.getCenterRecord(value);
                    if (dayOfTrip.isStartStatus() && dayOfTrip.isEndStatus()) {
                        System.out.println("normal trip in " + key);
                        if (key == 0) {
                            int beginCapacity = START_CAPACITY - capacity;
                            System.out.println("Begin Capacity:" + beginCapacity);
                            if (beginCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, beginCapacity));
                            }
                        } else if (key != k - 1) {
                            int normalDayCapacity = DAY_CAPACITY - capacity;
                            if (normalDayCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, normalDayCapacity));
                            }
                        } else {
                            int endCapacity = END_CAPACITY - capacity;
                            if (endCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, endCapacity));
                            }
                        }
                    } else if (dayOfTrip.isStartStatus()) {
                        if (key == 0) {
                            int beginCapacity = START_CAPACITY - capacity;
                            if (beginCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, beginCapacity));
                            }
                        } else {
                            int normalDayCapacity = DAY_CAPACITY - capacity;
                            if (normalDayCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, normalDayCapacity));
                            }
                        }
                    } else if (dayOfTrip.isEndStatus()) {
                        if (key != k - 1) {
                            int normalDayCapacity = DAY_CAPACITY - capacity;
                            if (normalDayCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, normalDayCapacity));
                            }
                        } else {
                            int endCapacity = END_CAPACITY - capacity;
                            if (endCapacity > 0) {
                                CustomListUtil.getSortedList(nearByRecord, centerRecord);
                                finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, endCapacity));
                            }
                        }
                    } else {
                        int normalDayCapacity = DAY_CAPACITY - capacity;
                        if (normalDayCapacity > 0) {
                            CustomListUtil.getSortedList(nearByRecord, centerRecord);
                            finalMapEnd1.put(key, CustomListUtil.getSystemAddPlaces(value, nearByRecord, normalDayCapacity));
                        }
                    }
                }
            }
        });
        return finalMapEnd1;
    }

    public Map<Integer, List<Record>> newCreatePlanFromNear(List<PlaceDTO> places,
                                                            DayDTO dayOfTrip, Integer tripId) throws Exception {
        Set<Record> records2 = new HashSet<>();
        int k = dayOfTrip.getQuantity();
        for (PlaceDTO dto : places) {
            records2.add(createRecordFromNearByPlace(dto));
        }
        List<Record> records = CustomListUtil.convertSetToList(records2);
        Record tripDestination = getTripDestination(tripId);
        records = CustomListUtil.getSortedList(records, tripDestination);
//        System.out.println(records.size());
        SuggestAlgorithm2 algorithm = new SuggestAlgorithm2();
        Map<Integer, List<Record>> map = algorithm.preparePlan(k);
        Map<Integer, List<Record>> clusters = algorithm.run(map, records, k,
                0, ITERATION, EUCLIDEAN, DAY_CAPACITY);
        System.out.println("END RUN 1st ALGORITHM");
        Map<Integer, List<Record>> mapEnd;
        //
        //
        if (dayOfTrip.isStartStatus() && dayOfTrip.isEndStatus()) {
            mapEnd = SuggestAlgorithm2.
                    makePlanForStartDay(clusters, k, START_CAPACITY);
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapEnd, k, END_CAPACITY);
        } else if (dayOfTrip.isEndStatus()) {
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(clusters, k, END_CAPACITY);
        } else if (dayOfTrip.isStartStatus()) {
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(clusters, k, START_CAPACITY);
        } else {
            mapEnd = clusters;
        }
        mapEnd.remove(k);
        // chuan bi hashmap de them place
        // end chuan bi
        return mapEnd;
    }

    public List<ActivityDTO> makePlan(Map<Integer, List<Record>> mapEnd, DayDTO dayOfTrip) {
        List<ActivityDTO> dayByDayActivities = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);
        AtomicInteger index2 = new AtomicInteger(0);
        LocalDateTime normalDay = dayOfTrip.getStartDate().toLocalDate().atStartOfDay();
        mapEnd.forEach((key, value) -> {
            if (!value.isEmpty()) {
                if (value.get(0).getIsInPlan()) {
                    Timestamp activityTime;
                    if (dayOfTrip.isStartStatus() && dayOfTrip.isEndStatus()) {
                        if (index.get() == 0) {
                            int startDayHour = dayOfTrip.getStartDate().getHour();
                            if (startDayHour < 10) {
                                Timestamp timestamp = Timestamp.valueOf(normalDay);
                                activityTime = Timestamp.from(timestamp.toInstant().
                                        plus(START_DAY_BEGIN, ChronoUnit.MINUTES));
                            } else {
                                Timestamp timestamp = Timestamp.valueOf(dayOfTrip.getStartDate());
                                activityTime = Timestamp.from(timestamp.toInstant());
                            }
                        } else {
                            Timestamp timestamp = Timestamp.valueOf(normalDay);
                            activityTime = Timestamp.from(timestamp.toInstant()
                                    .plus(index.intValue(), ChronoUnit.DAYS).
                                            plus(NORMAL_DAY_BEGIN, ChronoUnit.MINUTES));
                        }
                        for (int i = 0; i < value.size(); i++) {
                            Record r = value.get(i);
                            if (i - 1 < 0) {
                                dayByDayActivities.add(createActivity(r, activityTime));
                            } else {
                                Timestamp time = dayByDayActivities.get((index2.get() - 1)).getEndAt();
                                dayByDayActivities.add(createActivity(r,
                                        CustomTimestampUtil.updateByMinutes(time, TRAVEL_TIME)));
                            }
                            index2.getAndIncrement();
                        }
                    } else if (dayOfTrip.isStartStatus()) {
                        if (index.get() == 0) {
                            int startDayHour = dayOfTrip.getStartDate().getHour();
                            if (startDayHour < 10) {
                                Timestamp timestamp = Timestamp.valueOf(normalDay);
                                activityTime = Timestamp.from(timestamp.toInstant().
                                        plus(START_DAY_BEGIN, ChronoUnit.MINUTES));
                            } else {
                                Timestamp timestamp = Timestamp.valueOf(dayOfTrip.getStartDate());
                                activityTime = Timestamp.from(timestamp.toInstant());
                            }
                        } else {
                            Timestamp timestamp = Timestamp.valueOf(normalDay);
                            activityTime = Timestamp.from(timestamp.toInstant()
                                    .plus(index.intValue(), ChronoUnit.DAYS).
                                            plus(NORMAL_DAY_BEGIN, ChronoUnit.MINUTES));
                        }
                        for (int i = 0; i < value.size(); i++) {
                            Record r = value.get(i);
                            if (i - 1 < 0) {
                                dayByDayActivities.add(createActivity(r, activityTime));
                            } else {
                                Timestamp time = dayByDayActivities.get((index2.get() - 1)).getEndAt();
                                dayByDayActivities.add(createActivity(r,
                                        CustomTimestampUtil.updateByMinutes(time, TRAVEL_TIME)));
                            }
                            index2.getAndIncrement();
                        }
//                    } else if (dayOfTrip.isEndStatus()) {
                    } else {
                        Timestamp timestamp = Timestamp.valueOf(normalDay);
                        activityTime = Timestamp.from(timestamp.toInstant()
                                .plus(index.intValue(), ChronoUnit.DAYS).
                                        plus(NORMAL_DAY_BEGIN, ChronoUnit.MINUTES));
                        for (int i = 0; i < value.size(); i++) {
                            Record r = value.get(i);
                            if (i - 1 < 0) {
                                dayByDayActivities.add(createActivity(r, activityTime));
                            } else {
                                Timestamp time = dayByDayActivities.get((index2.get() - 1)).getEndAt();
                                dayByDayActivities.add(createActivity(r,
                                        CustomTimestampUtil.updateByMinutes(time, TRAVEL_TIME)));
                            }
                            index2.getAndIncrement();
                        }

                    }
                } else {
                    for (Record r : value) {
                        dayByDayActivities.add(createLeftOut(r));
                    }
                }
                // sau khi loop tang day len 1 don vi
                index.getAndIncrement();
                System.out.println("-------------------------- CLUSTER " + key + "----------------------------");
                String members = String.join(", ", value.stream().map(Record::toString).collect(toSet()));
                System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
                System.out.println();
            }
        });
        return dayByDayActivities;
    }
}
