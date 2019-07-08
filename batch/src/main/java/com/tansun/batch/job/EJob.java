package com.tansun.batch.job;


import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.tansun.batch.api.BatchJobApi;
import com.tansun.batch.base.annotaion.TaskJob;
import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.base.annotaion.listener.MyElasticJobListener;
import com.tansun.batch.util.BatchConstant;
import com.tansun.batch.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TaskJob(jobName="eJob",cron = "0 0 0/1 * * ?",desc = "测试" ,elasticJobListener = MyElasticJobListener.class)
class EJob implements SimpleJob {

    @Autowired
    private BatchJobApi batchJobApi;

    private final static Logger logger = LoggerFactory.getLogger(EJob.class);

    @Override
    public void execute(ShardingContext shardingContext) {


       //查询当前批量完成状态，完成日期
       Map<String, Object> queryMap = new HashMap<>();
       queryMap.put("jobName",shardingContext.getJobName());
       BatchJobEntity oneBatchJob = this.batchJobApi.getOneBatchJob(queryMap);
       if (oneBatchJob==null)
           return;
       //判断当前批量是否已经执行过 状态不为（完成和执行执行中）或日期不为当前日期
       if((!BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(oneBatchJob.getState())
               &&!BatchConstant.BATCH_JOB_STATUS_RUNNING.equals(oneBatchJob.getState()))
               || !DateUtils.isSameDay(oneBatchJob.getSuccessDate(),new Date())){
           String dateString =DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss");
           System.out.println("当前时间:"+dateString );
           logger.info("-------------"+shardingContext.getJobName()+"开始执行，时间："+dateString+"--------------------------");
           BatchJobEntity batchJob=new BatchJobEntity();
           batchJob.setId(oneBatchJob.getId());
           batchJob.setState(BatchConstant.BATCH_JOB_STATUS_SUCCESS);
           batchJob.setSuccessDate(DateUtil.convert( DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
           this.batchJobApi.updateBatchJob(batchJob);
       }

    }
}
