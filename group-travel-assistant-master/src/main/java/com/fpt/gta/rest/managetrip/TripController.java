package com.fpt.gta.rest.managetrip;

import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.service.TripService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/trip")
public class TripController {
    TripService tripService;
    ModelMapper modelMapper;

    @Autowired
    public TripController(TripService tripService, ModelMapper modelMapper) {
        this.tripService = tripService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{idTrip}")
    public TripDTO findTripByIdTrip(@PathVariable String idTrip) {
        return modelMapper.map(tripService.getTripById(Integer.parseInt(idTrip)), TripDTO.class);
    }

    @GetMapping
    public List<TripDTO> findAllJoinedGroup(@RequestParam String idGroup) {
        return Arrays.asList(
                modelMapper.map(tripService.findAllTripInGroup(Integer.parseInt(idGroup)), TripDTO[].class));
    }

    @PostMapping
    public void createTrip(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idGroup,
            @RequestBody TripDTO tripDTO) {
        tripService.createTrip(firebaseToken.getUid(), Integer.parseInt(idGroup), modelMapper.map(tripDTO, Trip.class));
    }

    @PutMapping
    public void updateTrip(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                           @RequestBody TripDTO tripDTO) {
        tripService.updateTrip(firebaseToken.getUid(), modelMapper.map(tripDTO, Trip.class));
    }

    @DeleteMapping("/{idTrip}")
    public void removeGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @PathVariable String idTrip) {
        tripService.deleteTrip(firebaseToken.getUid(), Integer.parseInt(idTrip));
    }


}
