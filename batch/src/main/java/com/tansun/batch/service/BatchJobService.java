package com.tansun.batch.service;

import com.tansun.batch.entity.BatchJobEntity;

import java.util.List;
import java.util.Map;

public interface BatchJobService {

    public BatchJobEntity getOneBatchJob(Map<String,Object> qury);
    public List<BatchJobEntity> getBatchJobList(Map<String,Object> qury);

    public int updateBatchJob(BatchJobEntity batchJob);

}
