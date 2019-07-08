package com.tansun.batch.entity;

import java.util.Date;

public class BatchJobEntity {
    private int id;
    /**
     * 批量名称
     */
    private String jobName;
    /**
     *批量执行类
     */
    private String javaClass;
    /**
     *corn表达式
     */
    private String sedulerDate;
    /**
     *上级批量
     */
    private int superJob;
    /**
     *手动批量时间
     */
    private Date newBatchDate;
    /**
     *批量开始时间
     */
    private Date startDate;
    /**
     *批量状态
     * 0 成功 1执行中 2 等待 3异常 4 失败
     */
    private String state;
    /**
     *完成时间
     */
    private Date successDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getSedulerDate() {
        return sedulerDate;
    }

    public void setSedulerDate(String sedulerDate) {
        this.sedulerDate = sedulerDate;
    }

    public int getSuperJob() {
        return superJob;
    }

    public void setSuperJob(int superJob) {
        this.superJob = superJob;
    }

    public Date getNewBatchDate() {
        return newBatchDate;
    }

    public void setNewBatchDate(Date newBatchDate) {
        this.newBatchDate = newBatchDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getSuccessDate() {
        return successDate;
    }

    public void setSuccessDate(Date successDate) {
        this.successDate = successDate;
    }
}
