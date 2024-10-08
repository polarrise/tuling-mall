package com.tuling.tulingmall.ordercurr.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.Properties;

/**
 *                  ,;,,;
 *                ,;;'(    社
 *      __      ,;;' ' \   会
 *   /'  '\'~~'~' \ /'\.)  主
 * ,;(      )    /  |.     义
 *,;' \    /-.,,(   ) \    码
 *     ) /       ) / )|    农
 *     ||        ||  \)
 *     (_\       (_\
 * @author ：图灵学院
 * @version: V1.0
 * @slogan: 天下风云出我辈，一入代码岁月催
 * @description: hint查询，无需再经过sql解析与路由阶段，直接查询目标库表
 **/
@Slf4j
public class OrderHintShardingAlgorithm implements HintShardingAlgorithm {

    private Properties props;
    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }
    /**
     * 返回的是查询的真实节点
     * @param collection
     * @param hintShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection collection, HintShardingValue hintShardingValue) {

        // 真实节点
        collection.stream().forEach((item)->{
            log.info("actual node table:{}",item);
        });

        log.info("logic table name:{},rout column:{}",hintShardingValue.getLogicTableName(),hintShardingValue.getColumnName());


        hintShardingValue.getValues().parallelStream().forEach((item)->{
            log.info("column value:{}",item);
        });

        return null;
    }
}
