package com.tansun.batch.mapper;

import com.tansun.batch.entity.BatchJobEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
//@Mapper
public interface BatchJobDao {

    BatchJobEntity getOneBatchJob(Map<String,Object> qury);

    List<BatchJobEntity> getBatchJobList(Map<String,Object> qury);

    int updateBatchJob(BatchJobEntity batchJob);

}
