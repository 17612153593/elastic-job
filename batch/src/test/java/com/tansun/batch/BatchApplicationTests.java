package com.tansun.batch;

import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.mapper.BatchJobDao;
import com.tansun.batch.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchApplicationTests {

    @Autowired
    private BatchJobDao batchJobDao;


    @Test
    public void contextLoads() {
        batchJobDao.getBatchJobList(new HashMap<>());
        BatchJobEntity batchJobEntity = new BatchJobEntity();
        batchJobEntity.setId(1);
        batchJobEntity.setSuccessDate(DateUtil.convert( DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
        batchJobDao.updateBatchJob(batchJobEntity);
    }

}
