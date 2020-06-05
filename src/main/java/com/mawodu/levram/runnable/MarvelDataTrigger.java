package com.mawodu.levram.runnable;

import com.mawodu.levram.ClientHandler;
import com.mawodu.levram.HeroRepositoryMetaData;
import com.mawodu.levram.HeroStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Service
public class MarvelDataTrigger {

    private TaskExecutor taskExecutor;
    private HeroStore heroStore;
    @Qualifier("marvel")
    private ClientHandler clientHandler;


    @Autowired
    public MarvelDataTrigger(TaskExecutor taskExecutor, HeroStore heroStore, ClientHandler clientHandler) {
        this.taskExecutor = taskExecutor;
        this.heroStore = heroStore;
        this.clientHandler = clientHandler;
    }

    @PostConstruct
    public void fetchDataAndPopulateDatabase() throws InterruptedException, ExecutionException {
        HeroRepositoryMetaData metaData = prefetchMetadata();

        for(int offset=0; offset<metaData.getTotal(); offset+=metaData.getCount()) {
            taskExecutor.execute(new FetchAndUpdateDb(heroStore, clientHandler, offset));
        }
    }

    // todo: implement.
    private HeroRepositoryMetaData prefetchMetadata() {
        return HeroRepositoryMetaData.create()
//                .total(1493)
                .total(200)
                .limit(20)
                .count(20)
                .responseCode(200)
                .build();
    }

}
