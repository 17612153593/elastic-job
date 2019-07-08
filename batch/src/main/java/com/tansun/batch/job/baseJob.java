package com.tansun.batch.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobAPIFactory;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.google.common.base.Optional;
import com.tansun.batch.api.BatchJobApi;
import com.tansun.batch.base.annotaion.TaskJob;
import com.tansun.batch.entity.BatchJobEntity;
import com.tansun.batch.util.BatchConstant;
import com.tansun.batch.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TaskJob(jobName = "baseJob",cron = "0/3 * * * * ?",desc = "总控批量")
public class baseJob implements SimpleJob {

    @Autowired
    private BatchJobApi batchJobApi;

    @Autowired
    private Environment env;
    private final static Logger logger = LoggerFactory.getLogger(EJob.class);

    @Override
    public void execute(ShardingContext shardingContext) {
        //查询所有批量
        Map<String, Object> queryMap = new HashMap<>();
        BatchJobEntity baseJob=new BatchJobEntity();

        List<BatchJobEntity> batchJobs = this.batchJobApi.getBatchJobList(queryMap);
        for (BatchJobEntity batchJob: batchJobs) {
                //如果为本任务不做处理 继续下次循环
                if(this.getClass().getName().equals(batchJob.getJavaClass())){
                    baseJob.setId(batchJob.getId());
                    continue;
                }
                //判断是否已经执行完成
                if (!BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(batchJob.getState())
                        && !BatchConstant.BATCH_JOB_STATUS_RUNNING.equals(batchJob.getState())) {

                    if (BatchConstant.BATCH_JOB_STATUS_EXCEPTION.equals(batchJob.getState())) {
                        //异常
                        //触发任务
                        JobOperateAPI jobAPIService = JobAPIFactory.createJobOperateAPI(env.getProperty("server_lists"), env.getProperty("namespace"), Optional.fromNullable(null));
                        jobAPIService.trigger(Optional.of(batchJob.getJobName()), Optional.<String>absent());
                    } else if(BatchConstant.BATCH_JOB_STATUS_WAITING.equals(batchJob.getState())){
                        //等待
                        queryMap.put("id", batchJob.getSuperJob());
                        BatchJobEntity oneBatchJob = this.batchJobApi.getOneBatchJob(queryMap);
                        //判断父批是否已经执行完成
                        if (DateUtils.isSameDay(oneBatchJob.getSuccessDate(), new Date())
                                && BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(oneBatchJob.getState())) {
                            JobOperateAPI jobAPIService = JobAPIFactory.createJobOperateAPI(env.getProperty("server_lists"), env.getProperty("namespace"), Optional.fromNullable(null));
                            jobAPIService.trigger(Optional.of(batchJob.getJobName()), Optional.<String>absent());
                        }
                    }else{
                        //失败不做处理

                    }

                }else if(!DateUtils.isSameDay(batchJob.getSuccessDate(), new Date())
                        &&BatchConstant.BATCH_JOB_STATUS_SUCCESS.equals(batchJob.getState())){
                    //完成时间非今天说明今天尚未执行
                    JobOperateAPI jobAPIService = JobAPIFactory.createJobOperateAPI(
                            env.getProperty("server_lists"),//zk注册地址
                            env.getProperty("namespace"), /*zk命名空间*/ Optional.fromNullable(null));
                    jobAPIService.trigger(Optional.of(batchJob.getJobName()/*定时任务类的全类名*/), Optional.<String>absent());
                }
            baseJob.setState(BatchConstant.BATCH_JOB_STATUS_SUCCESS);
            baseJob.setSuccessDate(DateUtil.convert( DateUtil.convert(new Date(),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
            this.batchJobApi.updateBatchJob(baseJob);
        }
    }
}
