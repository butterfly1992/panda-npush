package com.push.dao.impl;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DbUtils {

	private DbUtils() {

	}

	/* 数据源 */
	private static ComboPooledDataSource cpds = new ComboPooledDataSource();

	/* c3p0 数据源配置 */
	static {
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			// cpds.setJdbcUrl("jdbc:mysql://192.168.0.18:3306/pandaDB");
			// cpds.setUser("zy"); cpds.setPassword("zy");
			cpds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/pandaDB?autoReconnect=true");
			cpds.setUser("pandaAdminMySql");
			cpds.setPassword("pandaAdmin*#123MySql");
			/*
			 * cpds.setJdbcUrl("jdbc:mysql://60.216.6.135:3306/pandaDB");
			 * cpds.setUser("pandaConnectUser");
			 * cpds.setPassword("panda*#Connect@&User2014");
			 */
			cpds.setInitialPoolSize(5);// 初始化5个连接
			cpds.setMinPoolSize(5);// 最小连接
			cpds.setMaxPoolSize(100);// 最大连接
			cpds.setMaxStatements(0);
			cpds.setIdleConnectionTestPeriod(18000);
			cpds.setMaxIdleTime(25000);// 最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃。
			cpds.setTestConnectionOnCheckout(true);

		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public static ComboPooledDataSource getDataSource() {
		return cpds;
	}

	public static void freeDataSources() throws SQLException {
		cpds.close();
	}

	public static Connection getConnectWithOutTransaction() {
		Connection conn = null;
		try {
			conn = cpds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeConnect(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();

	/**
	 * 根据数据库的默认连接参数获取数据库的Connection对象，并绑定到当前线程上
	 * 
	 * @return 成功，返回Connection对象，否则返回null
	 */
	public static synchronized Connection getConnection() {
		Connection conn = tl.get(); // 先从当前线程上取出连接实例

		if (null == conn) { // 如果当前线程上没有Connection的实例
			try {
				conn = cpds.getConnection(); // 从连接池中取出一个连接实例
				tl.set(conn); // 把它绑定到当前线程上
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	/**
	 * 获取事务管理器
	 * 
	 * @return 事务管理实例
	 */
	public static synchronized TransactionManager getTranManager() {
		return new TransactionManager(getConnection());
	}

	/**
	 * 关闭数据库连接，并卸装线程绑定 要关闭数据库连接实例
	 */
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				tl.remove(); // 卸装线程绑定
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
