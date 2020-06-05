package com.mawodu.levram;

import com.mawodu.levram.parsing.ResponseParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EndpointController {

    private HeroStore store;

    @Autowired
    public EndpointController(HeroStore store) {
        this.store = store;
    }

    @GetMapping(value = "/")
    public String allHeroesIds() {
        return store.fetchAllHeroIds().toString();
    }

    @GetMapping(value = "/{id}")
    public String foo(@PathVariable int id) {
        // fixme: optional check
        return ResponseParser.heroToJson(store.fetchHeroById(id)).get();
    }
}
