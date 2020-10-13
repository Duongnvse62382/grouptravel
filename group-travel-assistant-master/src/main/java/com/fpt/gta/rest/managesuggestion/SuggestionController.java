package com.fpt.gta.rest.managesuggestion;

import com.fpt.gta.algorithm.suggest.Algorithm;
import com.fpt.gta.algorithm.suggest.Record;
import com.fpt.gta.algorithm.suggest.SuggestAlgorithm2;
import com.fpt.gta.model.constant.SuggestedActivityType;
import com.fpt.gta.model.entity.SuggestedActivity;
import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.service.PlaceService;
import com.fpt.gta.model.service.SuggestedActivityService;
import com.fpt.gta.model.service.TripService;
import com.fpt.gta.util.CustomListUtil;
import com.fpt.gta.util.DateUtil;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import weka.core.EuclideanDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/suggestion")
public class SuggestionController {
    private int k = 0;
    private static final int DAY_CAPACITY = 480;
    private static final int ITERATION = 2000;
    private static final int START_CAPACITY = 360;
    private static final int END_CAPACITY = 420;
    GeoApiContext geoApiContext;
    TripService tripService;
    PlaceService placeService;
    SuggestionService suggestionService;
    SuggestedActivityService suggestedActivityService;
    ModelMapper modelMapper;

    @Autowired
    public SuggestionController(GeoApiContext geoApiContext, TripService tripService,
                                PlaceService placeService, SuggestionService suggestionService,
                                SuggestedActivityService suggestedActivityService, ModelMapper modelMapper) {
        this.geoApiContext = geoApiContext;
        this.tripService = tripService;
        this.placeService = placeService;
        this.suggestionService = suggestionService;
        this.suggestedActivityService = suggestedActivityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/makePlan2/{id}")
    public ResponseEntity makePlan2(@PathVariable("id") String id) throws Exception {
        Trip trip = tripService.getTripById(Integer.parseInt(id));
        if (trip != null) {
//            int daysLongType = (int) DateUtil.getDateDiff(trip.getStartAt(), trip.getEndAt(), TimeUnit.DAYS) + 1;
            int daysLongType = (int) DateUtil.getActualDateDiff(trip.getStartAt(), trip.getEndAt()) + 1;
            System.out.println("Trip :" + daysLongType + " days");
            k = daysLongType;
            SuggestAlgorithm2 suggestAlgorithm2 = new SuggestAlgorithm2();
            Map<Integer, List<Record>> map = suggestAlgorithm2.preparePlan2(k);
            Map<Integer, List<Record>> clusters = suggestAlgorithm2.run(map, SuggestAlgorithm2.data, k
                    , 0, ITERATION, new EuclideanDistance(), DAY_CAPACITY);
            System.out.println("END RUN 1st ALGORITHM");
            Map<Integer, List<Record>> mapEnd;
            if (k > 1) {
                Map<Integer, List<Record>> mapStart = SuggestAlgorithm2.makePlanForStartDay(clusters, k,
                        START_CAPACITY);
                mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapStart, k, END_CAPACITY);
            } else {
                mapEnd = SuggestAlgorithm2.makePlanForStartDay(clusters, k, START_CAPACITY);
            }
            mapEnd.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    System.out.println("-------------------------- CLUSTER " + key + " ----------------------------");
//                 Sorting the coordinates to see the most significant tags first.
                    String members = String.join(", ", value.stream().map(Record::getName).collect(toSet()));
                    System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
                    System.out.println();
                }
            });
            return ResponseEntity.ok().body("ok");
        }
        return ResponseEntity.badRequest().
                body("Trip not found");
    }

    @GetMapping("/makePlan4/{id}")
    public ResponseEntity makePlan(@PathVariable("id") String id) throws Exception {
        Trip trip = tripService.getTripById2(Integer.parseInt(id));
        List<SuggestedActivity> suggestedActivityList =
                suggestedActivityService.findAllSuggestedActivityInTripByType
                        (Integer.parseInt(id), SuggestedActivityType.ACTIVITY);
        if (trip != null) {
//            int daysLongType = (int) DateUtil.getDateDiff(trip.getStartAt(), trip.getEndAt(),
//                    TimeUnit.DAYS) + 1;
            int daysLongType = (int) DateUtil.getActualDateDiff(trip.getStartAt(), trip.getEndAt()) + 1;
//            System.out.println(trip.getEndAt() + " " + trip.getStartAt());
            k = daysLongType;
            System.out.println("Trip :" + daysLongType + " days");
            if (!suggestedActivityList.isEmpty()) {
                System.out.println(suggestedActivityList.size());
                List<ActivityDTO> result = suggestionService.createPlan(suggestedActivityList,
                        daysLongType, trip.getStartAt(), Integer.parseInt(id));
                System.out.println();
                return ResponseEntity.ok().body(result);
            } else {
                System.out.println("no suggest place");
                List<PlaceDTO> placeDTOS = suggestionService.getTripNearByPlaces(Integer.parseInt(id));
                System.out.println(placeDTOS.size());
                List<ActivityDTO> result = suggestionService.getPlanFromNearByPlaces(placeDTOS,
                        daysLongType, trip.getStartAt());
                return ResponseEntity.ok().body(result);
            }
        } else {
            return ResponseEntity.badRequest().body("Trip not found");
        }
    }

    @GetMapping("/makePlan/{id}")
    public ResponseEntity makePlan4(@PathVariable("id") String id) throws Exception {
        Trip trip = tripService.getTripById2(Integer.parseInt(id));
        List<SuggestedActivity> suggestedActivityList =
                suggestedActivityService.findAllSuggestedActivityInTripByType
                        (Integer.parseInt(id), SuggestedActivityType.ACTIVITY);
        if (trip != null) {
            DayDTO dayOfTrip = suggestionService.getDayCountInCity(Integer.parseInt(id));
            int daysLongType = dayOfTrip.getQuantity();
            System.out.println("Trip :" + daysLongType + " days");
            if (daysLongType > 0 && !dayOfTrip.isSameDay()) {

//                if (!suggestedActivityList.isEmpty() && !CustomListUtil.checkListOnlyHasIsTooFar(suggestedActivityList)) {
                if (!suggestedActivityList.isEmpty()){
                    System.out.println(suggestedActivityList.size() + " suggested activity");
                    Map<Integer, List<Record>> mapEnd = suggestionService.newCreatePlan(suggestedActivityList
                            , dayOfTrip, Integer.parseInt(id));
                    List<ActivityDTO> result = suggestionService.makePlan(mapEnd, dayOfTrip);
                    return ResponseEntity.ok().body(result);
                } else {
                    System.out.println("no suggest place");
                    List<PlaceDTO> placeDTOS = suggestionService.getTripNearByPlaces(Integer.parseInt(id));
//                    System.out.println(placeDTOS.size());
                    if (placeDTOS.isEmpty()) {
                        return ResponseEntity.notFound().build();
//                                body("Cannot calculatePlan. There is no near by places");
                    } else {
                        Map<Integer, List<Record>> mapEnd = suggestionService.newCreatePlanFromNear(placeDTOS
                                , dayOfTrip, Integer.parseInt(id));
                        List<ActivityDTO> result = suggestionService.makePlan(mapEnd, dayOfTrip);
                        return ResponseEntity.ok().body(result);
                    }
                }
            } else {
                return new ResponseEntity("Cannot calculatePlan. Please check day and time.", HttpStatus.CONFLICT);
//                return ResponseEntity.badRequest().
//                        body("Cannot calculatePlan. Please check day and time.");
            }
        } else {
            return ResponseEntity.badRequest().
                    body("Trip not found");
        }
    }

    @GetMapping("/makePlan3/{id}")
    public List<ActivityDTO> makePlanFromSearchNearBy(@PathVariable("id") String id) throws Exception {
        Trip trip = tripService.getTripById2(Integer.parseInt(id));
        List<PlaceDTO> placeDTOS = suggestionService.getTripNearByPlaces(Integer.parseInt(id));
        List<ActivityDTO> result = new ArrayList<>();
        if (trip != null) {
            if (!placeDTOS.isEmpty()) {
//                System.out.println(trip.getEndAt() + " " + trip.getStartAt());
                int daysLongType = (int) DateUtil.getDateDiff(trip.getStartAt(), trip.getEndAt(),
                        TimeUnit.DAYS) + 1;
                k = daysLongType;
                System.out.println("Trip :" + daysLongType + " days");
                result = suggestionService.getPlanFromNearByPlaces(placeDTOS,
                        daysLongType, trip.getStartAt());
            }
        }
        return result;
    }

    @GetMapping("/getPlaces")
    @ResponseBody
    public List<Record> getALlPlaces() throws Exception {
        k = 4;
        List<Record> places = new ArrayList<>();
        Record tripDestination = suggestionService.getTripDestination(18);
        SuggestAlgorithm2 suggestAlgorithm2 = new SuggestAlgorithm2();
        Map<Integer, List<Record>> map = suggestAlgorithm2.preparePlan2(k);
        Map<Integer, List<Record>> clusters = suggestAlgorithm2.run(map, SuggestAlgorithm2.data, k,
                0, ITERATION, new EuclideanDistance(), DAY_CAPACITY);
        System.out.println("END RUN 1st ALGORITHM");
        Map<Integer, List<Record>> mapEnd;
        if (k > 1) {
            Map<Integer, List<Record>> mapStart = SuggestAlgorithm2.makePlanForStartDay(clusters, k,
                    START_CAPACITY);
            mapEnd = SuggestAlgorithm2.makePlanForEndDay(mapStart, k, END_CAPACITY);
        } else {
            mapEnd = SuggestAlgorithm2.makePlanForStartDay(clusters, k, START_CAPACITY);
        }

        mapEnd.forEach((key, value) -> {
//        clusters.forEach((key, value) -> {
            System.out.println("-------------------------- CLUSTER " + key + " ----------------------------");
            // Sorting the coordinates to see the most significant tags first.
            String members = String.join(", ", value.stream().
                    map(Record::getName).collect(toSet()));
            System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
            System.out.println();
            System.out.println("-------------------------- MODIFY " + key + " ----------------------------");
            // Sorting the coordinates to see the most significant tags first.
            value = CustomListUtil.getSortedList(value, tripDestination);
            String members2 = String.join(", ", value.stream().
                    map(Record::getName).collect(toSet()));
            System.out.println(members2 + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
            System.out.println();
            places.addAll(value);
        });
        return places;
    }

    @GetMapping("/getPlacesAfterKMeans")
    @ResponseBody
    public List<Record> getPlacesAfterKMeans() throws Exception {
        List<Record> places = new ArrayList<>();
        Algorithm algorithm = new Algorithm();
        Map<Integer, List<Record>> map = algorithm.preparePlan(4);
        Map<Integer, List<Record>> clusters = algorithm.runKMeans(map,
                Algorithm.data, 4, 2000, 0, new EuclideanDistance());
        clusters = algorithm.runBinPacking(clusters, 480, 0, 4);
        clusters = algorithm.runKMeans(clusters, Algorithm.data, 3, 2000, 1,
                new EuclideanDistance());
        clusters = algorithm.runBinPacking(clusters, 480, 1, 4);
        clusters = algorithm.runKMeans(clusters, Algorithm.data, 2, 2000, 2,
                new EuclideanDistance());
        clusters = algorithm.runBinPacking(clusters, 480, 2, 4);
//        clusters= algorithm.addRemainingToMap(clusters,2);
        clusters = algorithm.lastBinPacking(clusters, 480, 3);
        clusters.forEach((key, value) -> {
            System.out.println("-------------------------- CLUSTER " + key + " ----------------------------");
            // Sorting the coordinates to see the most significant tags first.
            String members = String.join(", ", value.stream().
                    map(Record::getName).collect(toSet()));
            System.out.println(members + " - " + CustomListUtil.getTotalTimeSpendRecord(value));
            System.out.println();
            places.addAll(value);
        });
        return places;
    }

    @GetMapping("testDirection")
    public String direction() throws InterruptedException, ApiException, IOException {
        return suggestionService.testGeocoding();
    }

    @GetMapping("testDayOfTrip/{id}")
    public DayDTO testDayOfTrip(@PathVariable("id") String id) {
        DayDTO dayOfTrip = suggestionService.getDayCountInCity(Integer.parseInt(id));
        return dayOfTrip;
    }

    @GetMapping("testNearBy/{text}")
    public String testNearBy(@PathVariable("text") String text) {
        suggestionService.testNearBy(text);
        return "";
    }
}
