package com.tansun.batch.service.ServiceImpl;

import com.tansun.batch.mapper.BatchJobDao;
import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.service.BatchJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class BatchJobServiceImpl implements BatchJobService {

    @Autowired
    private BatchJobDao batchJobDao;

    @Override
    public BatchJobEntity getOneBatchJob(Map<String, Object> qury) {
        return this.batchJobDao.getOneBatchJob(qury);
    }

    @Override
    public List<BatchJobEntity> getBatchJobList(Map<String, Object> qury) {
        return this.batchJobDao.getBatchJobList(qury);
    }

    @Override
    public int updateBatchJob(BatchJobEntity batchJob) {
        return this.batchJobDao.updateBatchJob(batchJob);
    }
}
