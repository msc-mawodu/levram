package com.mawodu.levram.runnable;

import com.mawodu.levram.DataProvider;
import com.mawodu.levram.HeroStore;
import com.mawodu.levram.entities.Hero;
import com.mawodu.levram.parsing.ResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FetchAndUpdateDb implements Runnable {

    Logger logger = LoggerFactory.getLogger(FetchAndUpdateDb.class);

    private HeroStore heroStore;
    private DataProvider dataProvider;
    private int offset;

    public FetchAndUpdateDb(HeroStore heroStore, DataProvider dataProvider, int offset) {
        this.heroStore = heroStore;
        this.dataProvider = dataProvider;
        this.offset = offset;
    }

    public FetchAndUpdateDb(int offset) {
        this.offset = offset;
    }

    public void run() {
        logger.info(String.format("Attempting to fetch and store heroes starting with offset: %s .", offset));
        try {
            prefetch().ifPresent( heroes -> {
                heroes.stream().forEach( hero -> {
                    logger.info(String.format("Attempting to store hero."));
                    heroStore.store(hero);
                });
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // fixme: there must be a better way than sleep.
    private Optional<List<Hero>> prefetch() throws InterruptedException, ExecutionException {
        Future<String> future = dataProvider.call(offset);
        for (int i=0; i<15; i++) {
            if (future.isDone()) {
                return Optional.of(new ResponseParser(future.get()).heroesFromJSON());
            }
            logger.info(String.format("Retrying call @Thread: %s", Thread.currentThread().getId()));
            Thread.sleep(1000);
        }
        logger.error(String.format("FAILED to get data @Thread: %s", Thread.currentThread().getId()));
        return Optional.empty();
    }
}