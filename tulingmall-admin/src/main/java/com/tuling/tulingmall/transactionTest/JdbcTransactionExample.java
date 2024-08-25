package com.tuling.tulingmall.transactionTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author WangJinbiao
 * @date 2024/08/18 17：52
 */
public class JdbcTransactionExample {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/tl_mall_user?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456";

        Connection conn = null;
        Statement stmt = null;

        try {
            // 1.加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2.建立数据库连接, ()里面的就是数据源对象,通过数据源获取数据库连接
            conn = DriverManager.getConnection(url, user, password);

            // 3.数据库连接设置手动提交事务
            conn.setAutoCommit(false);

            // 4.数据库连接创建Statement对象执行SQL语句
            stmt = conn.createStatement();

            // 5.Statement对象执行SQL语句
            stmt.executeUpdate("INSERT INTO `tl_mall_user`.`employees`(`id`, `name`, `age`, `position`, `hire_time`, `gmt_create`, `gmt_modified`) VALUES (3, '2', 10, '33', '2024-08-18 17:56:24', '2024-08-18 17:56:24.187810', NULL);");

            // 6.数据库连接提交事务
            conn.commit();

            System.out.println("事务已提交");

        } catch (ClassNotFoundException | SQLException e) {
            try {
                // 发生异常时回滚事务
                if (conn != null) {
                    conn.rollback();
                    System.out.println("事务已回滚");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
