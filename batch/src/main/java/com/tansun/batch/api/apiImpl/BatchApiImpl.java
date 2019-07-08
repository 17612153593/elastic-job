package com.tansun.batch.api.apiImpl;

import com.tansun.batch.api.BatchJobApi;
import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.service.BatchJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BatchApiImpl implements BatchJobApi {

    @Autowired
    private BatchJobService batchJobService;

    @Override
    public BatchJobEntity getOneBatchJob(Map<String, Object> qury) {
        return this.batchJobService.getOneBatchJob(qury);
    }

    @Override
    public List<BatchJobEntity> getBatchJobList(Map<String, Object> qury) {
        return this.batchJobService.getBatchJobList(qury);
    }

    @Override
    public int updateBatchJob(BatchJobEntity batchJob) {
        return this.batchJobService.updateBatchJob(batchJob);
    }
}
