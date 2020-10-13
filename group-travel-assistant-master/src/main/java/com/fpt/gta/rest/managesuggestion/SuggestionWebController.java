package com.fpt.gta.rest.managesuggestion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class SuggestionWebController {
    @GetMapping("/map")
    public String getMap() {
        return "map";
    }

    @GetMapping("/geocoding")
    public String getGeocoding() {
        return "geocoding";
    }
}
