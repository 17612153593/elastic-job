package com.tansun.batch.base.annotaion.config;



import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Created by yanshao on 2019/3/14.
 */
@SpringBootConfiguration
public class ZookeeperConfig {

    @Value("${server_lists}")
    private String serverLists;

    @Value("${namespace}")
    private String namespace;

    @Value("${base_sleep_time_milliseconds}")
    private int baseSleepTimeMilliseconds;

    @Value("${max_sleep_time_milliseconds}")
    private int maxSleepTimeMilliseconds;

    @Value("${max_retries}")
    private int maxRetries;

    /**
     * 初始化注册中心
     * */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverLists, namespace);
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        zookeeperConfiguration.setMaxRetries(maxRetries);
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }
}

