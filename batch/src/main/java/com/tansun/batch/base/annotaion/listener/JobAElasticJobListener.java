package com.tansun.batch.base.annotaion.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobAElasticJobListener implements ElasticJobListener {



    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {



    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {

    }
}
