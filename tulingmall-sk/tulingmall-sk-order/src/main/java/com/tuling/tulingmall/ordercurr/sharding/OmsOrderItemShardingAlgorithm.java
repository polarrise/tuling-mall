package com.tuling.tulingmall.ordercurr.sharding;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 自定义分片算法，
 * 以纯粹的订单id为分片键会无法支持按用户id快速查询订单，
 * 所以截取客户id后2位拼凑到订单id上，分表时以订单id的后两位取模分片。
 * 这其实是个客户id和订单id都需要作为一个分片键，比较适用于复合分片算法
 * 复合分片算法配合复合策略使用，支持精确查询与部分范围查询
 **/
@Slf4j
public class OmsOrderItemShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

    private Properties props;
    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }
    /* 订单编号列名 */
    private static final String COLUMN_SHARDING_KEY = "order_id";

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
                                         ComplexKeysShardingValue<String> complexKeysShardingValue) {

        /*处理 = 以及 in */
        if (!complexKeysShardingValue.getColumnNameAndShardingValuesMap().isEmpty()) {
            Map<String, Collection<String>> columnNameAndShardingValuesMap
                    = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
            if(columnNameAndShardingValuesMap.containsKey(COLUMN_SHARDING_KEY)){
                 /*获取订单编号*/
                Collection<String> orderSns = complexKeysShardingValue.getColumnNameAndShardingValuesMap()
                        .getOrDefault(COLUMN_SHARDING_KEY, new ArrayList<>(1));

                List<String> ids = new ArrayList<>(16);
                if (Objects.nonNull(orderSns)) ids.addAll(ids2String(orderSns));

                return ids.stream()
                         /*截取 订单号或客户id的后2位*/
                        .map(id -> id.substring(id.length() - 2))
                        /* 去重*/
                        .distinct()
                        /* 转换成int*/
                        .map(Integer::new)
                        /* 对可用的表名求余数，获取到真实的表的后缀*/
                        .map(idSuffix -> idSuffix % availableTargetNames.size())
                         /*转换成string*/
                        .map(String::valueOf)
                        /* 获取到真实的表？*/
                        .map(tableSuffix -> availableTargetNames.stream().
                                filter(targetName -> targetName.endsWith(tableSuffix)).findFirst().orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        /*处理类似between and 范围查询，仅限 order_id 字段*/
        else if(!complexKeysShardingValue.getColumnNameAndRangeValuesMap().isEmpty()){
            log.info("[MyTableComplexKeysShardingAlgorithm] complexKeysShardingValue: [{}]", complexKeysShardingValue);
            Set<String> tableNameResultList = new LinkedHashSet<>();
            int tableSize = availableTargetNames.size();
            /* 提取范围查询的范围*/
            Range<String> rangeUserId = complexKeysShardingValue.getColumnNameAndRangeValuesMap().get(COLUMN_SHARDING_KEY);
            Long lower = Long.valueOf(rangeUserId.lowerEndpoint());
            Long upper = Long.valueOf(rangeUserId.lowerEndpoint());
            /*根据order_sn选择表*/
            for (String tableNameItem : availableTargetNames) {
                if (tableNameItem.endsWith(String.valueOf(lower % (tableSize -1 )))
                        || tableNameItem.endsWith(String.valueOf(upper % (tableSize -1 )))) {
                    tableNameResultList.add(tableNameItem);
                }
                if (tableNameResultList.size() >= tableSize) {
                    return tableNameResultList;
                }
            }
            return tableNameResultList;
        }
        log.warn("无法处理分区，将进行全路由！！");
        return availableTargetNames;
    }

    /*转换成String*/
    private List<String> ids2String(Collection<?> ids) {
        List<String> result = new ArrayList<>(ids.size());
        ids.forEach(id -> result.add(Objects.toString(id)));
        return result;
    }
}
