package com.tansun.batch.job;


import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.tansun.batch.api.BatchJobApi;
import com.tansun.batch.base.annotaion.TaskJob;
import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.util.BatchConstant;
import com.tansun.batch.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TaskJob(jobName = "jobA",cron="0 0 0/1 * * ?",desc="测试2")
public class JobA implements SimpleJob {

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
        //判断当前批量是否已经执行过
        if((!BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(oneBatchJob.getState())
                &&!BatchConstant.BATCH_JOB_STATUS_RUNNING.equals(oneBatchJob.getState()))
                || !DateUtils.isSameDay(oneBatchJob.getSuccessDate(),new Date())
                ){
            //查询父批量是否执行完成
            Map<String, Object> queryMap1 = new HashMap<>();
            queryMap1.put("id",oneBatchJob.getSuperJob());
            BatchJobEntity superJob = this.batchJobApi.getOneBatchJob(queryMap1);

            if (BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(superJob.getState())
                    &&DateUtils.isSameDay(superJob.getSuccessDate(),new Date())) {
                String dateString = DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss");
                System.out.println("当前时间:"+dateString );
                logger.info("-------------"+shardingContext.getJobName()+"开始执行，时间："+dateString+"--------------------------");
                oneBatchJob.setState(BatchConstant.BATCH_JOB_STATUS_SUCCESS);
                oneBatchJob.setSuccessDate(DateUtil.convert( DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
                this.batchJobApi.updateBatchJob(oneBatchJob);
            }else{
                String dateString = DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss");
                System.out.println("当前时间:"+dateString +"父批量未执行");
                logger.info("-------------"+shardingContext.getJobName()+"开始执行，时间："+dateString+"--------------------------");
                oneBatchJob.setState(BatchConstant.BATCH_JOB_STATUS_WAITING);
                //oneBatchJob.setSuccessDate(DateUtil.convert( DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
                this.batchJobApi.updateBatchJob(oneBatchJob);
            }

        }
    }
}
