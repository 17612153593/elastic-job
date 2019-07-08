package com.tansun.batch.base.annotaion.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import org.springframework.stereotype.Service;

@Service
public class MyElasticJobListener implements ElasticJobListener {


    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        System.out.println("eJob开始了");
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {

    }

}
