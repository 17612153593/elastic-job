package com.tansun.batch.entity;



/**
 * Created by IntelliJ IDEA.
 *
 * @author luoliang
 * @date 2018/4/10
 **/
public class JobTask {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String jobName;
    /**
     * 0-未执行
     * 1-已执行
     */
    private Integer status;

    private Long sendTime;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

}
