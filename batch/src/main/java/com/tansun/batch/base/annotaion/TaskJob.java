package com.tansun.batch.base.annotaion;


import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

/**
 * Created by yanshao on 2019/3/14.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface TaskJob {

    @NotNull
    String jobName();

    @NotNull
    String cron();

    @NotNull
    String desc();

    /**
     * 监听器
     * 会在任务执行前和执行后执行
     * @return
     */
    Class elasticJobListener()default String.class;

    boolean failover() default false;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    boolean overwrite() default true;

    int shardingTotalCount() default 1;

}
