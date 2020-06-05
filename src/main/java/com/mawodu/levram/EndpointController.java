package com.mawodu.levram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class EndpointController {

    private HeroStore store;

    @Autowired
    public EndpointController(HeroStore store) {
        this.store = store;
    }

    @GetMapping(value = "/")
    public String foo() throws InterruptedException, ExecutionException {
        return store.fetchAllHeroIds().toString();
    }
}
