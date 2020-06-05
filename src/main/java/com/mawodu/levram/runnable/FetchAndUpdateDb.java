package com.mawodu.levram.runnable;

import com.mawodu.levram.ClientHandler;
import com.mawodu.levram.HeroStore;
import com.mawodu.levram.entities.Hero;
import com.mawodu.levram.parsing.ResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FetchAndUpdateDb implements Runnable {

    Logger logger = LoggerFactory.getLogger(FetchAndUpdateDb.class);

    private HeroStore heroStore;
    private ClientHandler clientHandler;
    private int offset;


    public FetchAndUpdateDb(HeroStore heroStore, ClientHandler clientHandler, int offset) {
        this.heroStore = heroStore;
        this.clientHandler = clientHandler;
        this.offset = offset;
    }

    // FETCH -> PARSE -> STORE
    public void run() {
        fetch();
    }

    private void fetch() {
        clientHandler.processRequest(String.valueOf(offset))
                .ifPresent(this::parse);
    }

    private void parse(String response) {
        new ResponseParser(response).heroesFromJSON()
                .ifPresent(this::store);
    }

    private void store(List<Hero> heroes) {
        heroStore.batchStore(heroes);
    }
}