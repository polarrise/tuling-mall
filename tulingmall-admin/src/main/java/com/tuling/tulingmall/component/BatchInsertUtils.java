package com.tuling.tulingmall.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Component
public class BatchInsertUtils {

    /**
     * 每一批数据条数
     */
    private static final int BATCH_SIZE = 10000;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**
     * mybatis批量插入数据，批处理
     * @param data
     * @param mapperClass
     * @param function
     * @return
     * @param <T>
     * @param <U>
     * @param <R>
     */
    public<T,U,R>int batchInsertOrUpdateData(List<T> data, Class<U> mapperClass, BiFunction<T,U,R> function){
        int i = 1;
        SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try{
            U mapper = batchSqlSession.getMapper(mapperClass);
            if(CollectionUtils.isEmpty(data)){
                log.info("数据为空");
                return 0;
            }
            int size = data.size();
            for (T element : data){
                function.apply(element,mapper);
                if(i % BATCH_SIZE == 0 || i == size){
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.commit(!TransactionSynchronizationManager.isSynchronizationActive());
        }catch (Exception e){
            batchSqlSession.rollback();
            log.info("批量写入失败："+e);
        }finally {
            batchSqlSession.close();
        }
        return i-1;
    }

}
