package com.tuling.tulingmall.ordercurr.sharding;


import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.Properties;

/**
 * @author ：楼兰
 * @description: 分库分表后的兜底路由策略，全库表路由。
 **/

public class OrderAllRangeHintAlgorithm implements HintShardingAlgorithm {
    private Properties props;
    @Override
    public Collection<String> doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
        return availableTargetNames;
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }
}
