/**
 * Site:http://www.tjitcast.com
 */
package com.push.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器
 */
public class TransactionManager {

	private Connection conn;

	protected TransactionManager(Connection conn) {
		this.conn = conn;
	}

	/** 开启事务 */
	public void beginTransaction() {
		try {
			conn.setAutoCommit(false); // 把事务提交方式改为手工提交
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 提交事务并关闭连接 */
	public void commitAndClose() {
		try {
			conn.commit(); // 提交事务
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(conn);
		}
	}

	/** 回滚并关闭连接 */
	public void rollbackAndClose() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(conn);
		}
	}
}
