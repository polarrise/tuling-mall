package com.tuling.tulingmall.transactionTest;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author WangJinbiao
 * @date 2024/08/25 21：35
 * 编程式事务过程，我们简化了一下，如下：
 * 1.定义数据源DataSource 这里用的DruidDataSource，创建事务管理器
 * 2.定义事务属性信息：TransactionDefinition transactionDefinition = new DefaultTrsansactionDefinition
 * 3.获取事务：通过平台事务管理器得到事务状态对象：TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
 * 4.执行sql操作：比如以下通过JdbcTemplate的各种方法执行各种sql操作
 * 5.通过平台事务管理器提交事务(platformTransactionManager.commit)或者回滚事务(platformTransactionManager.rollback)
 */
public class ByPlatformTransactionManager {

    public static void main(String[] args) {
        //定义一个数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/tl_mall_user?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setInitialSize(5);

        //定义一个JdbcTemplate，用来方便执行数据库增删改查
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        //1.定义事务管理器，给其指定一个数据源（可以把事务管理器想象为一个人，这个人来负责事务的控制操作）
        /**
         * Spring事务管理器的接口是org.springframework.transaction.PlatformTransactionManager：负责：获取事务、提交事务、回滚事务
         * DataSourceTransactionManager：如果你用是指定数据源的方式，比如操作数据库用的是：JdbcTemplate、mybatis、ibatis，那么需要用这个管理器来帮你控制事务。
         * 实际上，DataSourceTransactionManager是通过调用java.sql.Connection来管理事务，而后者是通过DataSource获取到的。
         * 通过调用连接的commit()方法来提交事务，同样，事务失败则通过调用rollback()方法进行回滚。
         * 也就是：PlatformTransactionManager接口(DataSourceTransactionManager实现类) ->依赖于数据库连接Connection来管理事务 ->依赖于DataSource来创建数据库连接。
         */
        PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);

        //2.定义事务属性：TransactionDefinition，TransactionDefinition可以用来配置事务的属性信息，比如事务隔离级别、事务超时时间、事务传播方式、是否是只读事务等等。
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        //3.获取事务：调用platformTransactionManager.getTransaction开启事务操作，得到事务状态(TransactionStatus)对象
        /**
         * 平台事务管理器获取事务方法getTransaction(transactionDefinition)内部：
         * 会调用doBegin方法开启事务
         * 1。获取db连接:从平台事务管理器内部的dataSource调用getConnection()方法获取一个数据库连接,将连接置为手动提交
         * 2.将<dataSource,事务对象的数据库连接放入threadLocal>存入 threadLocal中,也就是绑定到了当前线程。
         * 3.准备事务同步：将事务的一些信息放到ThreadLocal中
         */
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        //4.执行业务操作，下面就执行2个插入操作
        try {
            System.out.println("before:" + jdbcTemplate.queryForList("SELECT * from friend"));
            jdbcTemplate.update("insert into friend (name) values (?)", "test1-1");
            jdbcTemplate.update("insert into friend (name) values (?)", "test1-2");
            //5.提交事务：platformTransactionManager.commit
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            //6.回滚事务：platformTransactionManager.rollback
            platformTransactionManager.rollback(transactionStatus);
        }
        System.out.println("after:" + jdbcTemplate.queryForList("SELECT * from friend"));
    }
}
