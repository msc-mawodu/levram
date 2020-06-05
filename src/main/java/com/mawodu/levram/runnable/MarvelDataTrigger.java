package com.mawodu.levram.runnable;

import com.mawodu.levram.DataProvider;
import com.mawodu.levram.HeroRepositoryMetaData;
import com.mawodu.levram.HeroStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Service
public class MarvelDataTrigger {

    private TaskExecutor taskExecutor;
    private HeroStore heroStore;
    private DataProvider dataProvider;

    @Autowired
    public MarvelDataTrigger(TaskExecutor taskExecutor, HeroStore heroStore, DataProvider dataProvider) {
        this.taskExecutor = taskExecutor;
        this.heroStore = heroStore;
        this.dataProvider = dataProvider;
    }

    @PostConstruct
    public void fetchDataAndPopulateDatabase() throws InterruptedException, ExecutionException {
        HeroRepositoryMetaData metaData = prefetchMetadata();

        for(int offset=0; offset<metaData.getTotal(); offset+=metaData.getCount()) {
            taskExecutor.execute(new FetchAndUpdateDb(heroStore, dataProvider, offset));
        }
    }

    // todo: implement.
    private HeroRepositoryMetaData prefetchMetadata() {
        return HeroRepositoryMetaData.create()
//                .total(1493)
                .total(80)
                .limit(20)
                .count(20)
                .responseCode(200)
                .build();
    }

}
